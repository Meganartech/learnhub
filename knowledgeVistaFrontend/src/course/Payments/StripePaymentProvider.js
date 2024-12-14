import { loadStripe } from "@stripe/stripe-js";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils";
import axios from "axios";
import { useNavigate } from "react-router-dom";


const StripePaymentProvider = (orderData) => {

    const navigate=useNavigate()
  const MySwal = withReactContent(Swal);
  const token = sessionStorage.getItem("token");

  const handlePayment = async () => {
    try {
      // Fetch the Stripe publishable key
      const keyResponse = await axios.get(`${baseUrl}/get/stripe/publishkey`, {
        headers: {
          Authorization: token,
        },
      });
  
      if (keyResponse.status === 200) {
        const publishableKey = keyResponse.data;
  
        // // Initialize Stripe
         const stripe = await loadStripe(publishableKey);
  
        // const { loadStripe } = await import("@stripe/stripe-js");
        // const stripe = await loadStripe(publishableKey);
        // Prepare order data
        const data = {
          courseId: orderData?.courseId,
          userId: orderData?.userId,
        };
  
        // Create the order
        const orderResponse = await axios.post(`${baseUrl}/full/buyCourse/create?gateway=STRIPE`, data, {
          headers: {
            Authorization: token,
            "Content-Type": "application/json",
          },
        });
  
        // Handle the response from order creation
        const { sessionId } = orderResponse.data;
        sessionStorage.setItem("sessionId", sessionId);
  
        // Redirect to Stripe checkout
        await stripe.redirectToCheckout({ sessionId });

      } else if (keyResponse.status === 204) {
        MySwal.fire({
          icon: "warning",
          title: "Not Found",
          text: "Payment details not found.",
        });
      }
    } catch (error) {
      const status = error.response?.status;
  
      if (status === 401 || status === 403) {
        MySwal.fire({
          icon: "error",
          title: "Unauthorized",
          text: error.response?.data || "You are not authorized to access this page.",
        }).then(() => {
          navigate("/unauthorized");
        });
      } else if (status === 400) {
        MySwal.fire({
          icon: "error",
          title: "Error Creating Order",
          text: error.response?.data || "An error occurred.",
        });
      } else {
        MySwal.fire({
          icon: "error",
          title: "Unexpected Error",
          text: "An unexpected error occurred. Please try again.",
        });
        console.error("Unhandled error:", error);
      }
    }
  };
  

    return { handlePayment };
};

export default StripePaymentProvider;
