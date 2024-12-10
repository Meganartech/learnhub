import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils";
import errorimg from "../../images/errorimg.png";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import SelectPaymentGateway from "../Payments/SelectPaymentGateway";
const CourseView = ({ filteredCourses }) => {
  const MySwal = withReactContent(Swal);
  const userId = sessionStorage.getItem("userid");
  const [submitting, setsubmitting] = useState(false);
  const token = sessionStorage.getItem("token");
  const navigate =useNavigate();
  const[orderData,setorderData]=useState({
    userId:"",
    courseId:"",
    amount:"" ,
    courseAmount:"",
    coursename:"",
    installment:"",
    paytype:"",
    url:""
})
  useEffect(() => {
    const pendingPayment = JSON.parse(sessionStorage.getItem("pendingPayment"));

    if (pendingPayment) {
      const { courseId, paytype } = pendingPayment;

      // Clear pending payment data from localStorage
      sessionStorage.removeItem("pendingPayment");
      const userId = sessionStorage.getItem("userid");
      // Resume the payment process
      handlepaytype(courseId, userId, paytype);
    }
  }, []);
  const loadRazorpayScript = () => {
    return new Promise((resolve) => {
      const script = document.createElement("script");
      script.src = "https://checkout.razorpay.com/v1/checkout.js";
      script.onload = () => {
        resolve(true);
      };
      script.onerror = () => {
        resolve(false);
      };
      document.body.appendChild(script);
    });
  };
  const handlepaytype = (courseId, userId, paytype) => {
    let url = "";
    if (paytype === "FULL") {
      url = "/Full/getOrderSummary";
      FetchOrderSummary(courseId, userId, url);
    } else {
      MySwal.fire({
        icon: "question",
        title: "Payment Type?",
        text: "Want To Pay the Amount Partially or Fully? ",
        showDenyButton: true,
        showCancelButton: true,
        confirmButtonColor: "#4e73df",
        denyButtonColor: "#4e73df",
        confirmButtonText: `Pay Fully `,
        denyButtonText: `Pay in  Part`,
      }).then((result) => {
        if (result.isConfirmed) {
          url = "/Full/getOrderSummary";
          FetchOrderSummary(courseId, userId, url);
        } else if (result.isDenied) {
          url = "/Part/getOrderSummary";

          FetchOrderSummary(courseId, userId, url);
        }
      });
    }
  };
  const FetchOrderSummary=async(courseId, userId, url) =>{
    try {
          setsubmitting(true);
          const data = JSON.stringify({
            courseId: courseId,
            userId: userId,
          });
    
          const response = await axios.post(`${baseUrl}${url}`, data, {
            headers: {
              Authorization: token,
              "Content-Type": "application/json",
            },
          });
          setsubmitting(false);

setorderData(response.data)
        }catch(error){
          setsubmitting(false);
              if(error.response && error.response.status===400){
              MySwal.fire({
                icon: "error",
                title: "Error creating order:",
                text: error.response.data ? error.response.data : "error occured",
              });
            }else{
              throw error
            }
        }
  }
  // const handlesubmit = async (courseId, userId, url) => {
  //   try {
  //     setsubmitting(true);
  //     const data = JSON.stringify({
  //       courseId: courseId,
  //       userId: userId,
  //     });

  //     const response = await axios.post(`${baseUrl}${url}?gateway=RAZORPAY`, data, {
  //       headers: {
  //         Authorization: token,
  //         "Content-Type": "application/json",
  //       },
  //     });

  //     setsubmitting(false);
  //     const scriptLoaded = await loadRazorpayScript();

  //     if (!scriptLoaded) {
  //       alert("Failed to load Razorpay SDK. Please try again.");
  //       return;
  //     }
  //     const order = response.data;
  //     const options = {
  //       order_id: order.orderId,
  //       description: order.description,
  //       name: order.name,
  //       handler: function (response) {
  //         if (response.error) {
  //           MySwal.fire({
  //             icon: "error",
  //             title: "Payment Failed:",
  //             text: response.error,
  //           });
  //         } else {
  //           sendPaymentIdToServer(
  //             response.razorpay_payment_id,
  //             order.orderId,
  //             response.razorpay_signature
  //           );
  //         }
  //       },

  //       theme: {
  //         color: "#3399cc",
  //       },
  //     };

  //     var pay = new window.Razorpay(options);

  //     pay.open();
  //   } catch (error) {
  //     setsubmitting(false);
  //     if(error.response && error.response.status===400){
  //     MySwal.fire({
  //       icon: "error",
  //       title: "Error creating order:",
  //       text: error.response.data ? error.response.data : "error occured",
  //     });
  //   }else{
  //     throw error
  //   }
  //   }
  // };

  const sendPaymentIdToServer = async (paymentId, order, signature) => {
    try {
      setsubmitting(true);
      const paydata = JSON.stringify({
        paymentId: paymentId,
        orderId: order,
        signature: signature,
      });
      const response = await axios.post(
        `${baseUrl}/buyCourse/payment`,
        paydata,
        {
          headers: {
            Authorization: token,
            "Content-Type": "application/json",
          },
        }
      );

      if (response.status === 200) {
        setsubmitting(false);
        // Success response
        const message = response.data;
        navigate(message)
      } else {
        setsubmitting(false);
        const errorMessage = response.data;
        MySwal.fire({
          icon: "error",
          title: "Error updating payment ID:",
          text: errorMessage,
        });
      }
    } catch (error) {
      setsubmitting(false);
      if(error.response && error.response.status===404){
      MySwal.fire({
        icon: "error",
        title: "Error sending payment ID to server:",
        text: error.response.data ? error.response.data : "error occured",
      });
      }else{
      throw error
      }
    }
  };
  const handleClick = async (event, id, amount, url) => {
    event.preventDefault();
    if (amount === 0) {
      navigate(url)
    } else {
      try {
        const formdata = JSON.stringify({ courseId: id });
        const response = await axios.post(
          `${baseUrl}/CheckAccess/match`,
          formdata,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: token,
            },
          }
        );

        if (response.status === 200) {
          const message = response.data;
          navigate(message);
        }
      } catch (error) {
        if (error.response.status === 401) {
          MySwal.fire({
            icon: "error",
            title: "Oops...",
            text: "cannot Access Course ",
          });
        } else {
          // MySwal.fire({
          //   icon: "error",
          //   title: "Not Found",
          //   text: error,
          // });
          throw error
        }
      }
    }
  };

  return (
    <>
      {submitting && (
        <div className="outerspinner active">
          <div className="spinner"></div>
        </div>
      )}
      {orderData.amount && (
        <SelectPaymentGateway orderData={orderData} setorderData={setorderData}/>
      )}
      <div className="page-header"></div>
      {filteredCourses.length > 0 ? (
        <div className="row">
        
          {filteredCourses
            .slice()
            .reverse()
            .map((item) => (
              <div className="col-md-6 col-xl-3 course" key={item.courseId}>
                <div className="card mb-3">
                  <img
                   style={{ cursor: "pointer" }}
                   onClick={(e) => {
                     handleClick(
                       e,
                       item.courseId,
                       item.amount,
                       item.courseUrl
                     );
                   }}
                    className="img-fluid card-img-top"
                    src={`data:image/jpeg;base64,${item.courseImage}`}
                    onError={(e) => {
                      e.target.src = errorimg; // Use the imported error image
                    }}
                    alt="Course"
                  />
                  <div className="card-body">
                    <h5
                      className="card-title"
                      style={{ cursor: "pointer" }}
                      onClick={(e) => {
                        handleClick(
                          e,
                          item.courseId,
                          item.amount,
                          item.courseUrl
                        );
                      }}
                    >
                      {item.courseName.length > 15
                        ? item.courseName.slice(0, 15) + "..."
                        : item.courseName}
                    </h5>

                    <div className="card-text">
                      {item.amount === 0 ? (
                        <a
                          // href={item.courseUrl}
                          onClick={(e)=>{ e.preventDefault();navigate(item.courseUrl)}}
                          style={{ maxHeight: "50px", padding: "5px" }}
                          className="btn btn-outline-success w-100"
                        >
                          Enroll for Free
                        </a>
                      ) : (
                        <div
                          style={{
                            display: "grid",
                            gridTemplateColumns: "1fr 2fr",
                          }}
                        >
                          <div>
                            <i className="fa-solid fa-indian-rupee-sign"></i>
                            <span className="mt-3 blockquote">
                              {item.amount}
                            </span>
                          </div>
                          <button
                            className="btn btn-outline-primary"
                            style={{ maxHeight: "50px", padding: "5px" }}
                            onClick={() =>
                              handlepaytype(item.courseId, userId, item.paytype)
                            }
                          >
                            Enroll Now
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
                 </div>
      ) : (
        <div >
        <h1 className="text-light ">No Course Found </h1>
        </div>
      )}
    </>
  );
};

export default CourseView;