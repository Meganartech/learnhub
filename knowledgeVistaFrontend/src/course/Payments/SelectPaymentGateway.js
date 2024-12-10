import React, { useEffect, useState } from "react";
import razorpay from "../../images/razorpay.png";
import Paypal from "../../images/paypal.png";
import Stripe from "../../images/stripe.png";
import RazorpayPaymentProvider from "./RazorpayPaymentProvider"; // Import the RazorpayPaymentProvider
import StripePaymentProvider from "./StripePaymentProvider";
import PaypalPaymentProvider from "./PaypalPaymentProvider";
import axios from "axios";
import baseUrl from "../../api/utils";
import { useNavigate } from "react-router-dom";

const SelectPaymentGateway = ({ orderData, setorderData }) => {
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState(null);
  const token = sessionStorage.getItem("token");
  const navigate = useNavigate();
  const [paymentMethods, setPaymentMethods] = useState({
    PAYPAL: false,
    RAZORPAY: false,
    STRIPE: false,
  });
  const [loading, setLoading] = useState(true);

  const fetchpaymentTypedetails = async () => {
    try {
      const response = await axios.get(`${baseUrl}/get/paytypedetailsforUser`, {
        headers: {
          Authorization: token,
        },
      });

      setPaymentMethods(response.data);
      setLoading(false);
    } catch (error) {
      if (error.response && error.response.status === 401) {
        navigate("/unauthorized");
      }
      console.error("Error fetching payment methods", error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchpaymentTypedetails();
  }, []);

  const { handleSubmit } = RazorpayPaymentProvider(orderData);
  const { handlePayment } = StripePaymentProvider(orderData);
  const { handlePaymentPaypal } = PaypalPaymentProvider(orderData);

  const handlepaytypeClick = (e) => {
    const name = e.target.name;
    setSelectedPaymentMethod(name);
  };

  const handlePayNowClick = () => {
    if (selectedPaymentMethod === "razorpay") {
      setSelectedPaymentMethod("");
      handleSubmit();
    } else if (selectedPaymentMethod === "Stripe") {
      setSelectedPaymentMethod("");
      handlePayment();
    } else if (selectedPaymentMethod === "Paypal") {
      setSelectedPaymentMethod("");
      handlePaymentPaypal();
    } else {
      alert("Please select a payment method before proceeding.");
    }
  };

  if (loading) {
    return <div>Loading payment methods...</div>;
  }

  const noPaymentMethodsEnabled =
    !paymentMethods.PAYPAL &&
    !paymentMethods.RAZORPAY &&
    !paymentMethods.STRIPE;
  const showOrAndRazorpay =
    paymentMethods.RAZORPAY && (paymentMethods.PAYPAL || paymentMethods.STRIPE);

  return (
    <div className="paybackground ">
      <div className="card m-5" style={{position:"absolute",top:"10px"}}>
        <div className="card-body">
          <div className="header-container">
            <h4>Select Payment Method</h4>
            <i
              onClick={() => {
                setorderData({
                  amount: "",
                  courseAmount: "",
                  coursename: "",
                  installment: "",
                  paytype: "",
                });
              }}
              className="fa-solid fa-xmark"
            ></i>
          </div>

          <div className="twosplits">
            <div className="borderCurved">
              {noPaymentMethodsEnabled ? (
                <div style={{ textAlign: "center", padding: "20px" }}>
                  <p>No payment methods are enabled at the moment.</p>
                  <p>Please try again later or contact Administrator.</p>
                </div>
              ) : (
                <div
                style={{
                  height: "100%",
                  display:
                    paymentMethods.PAYPAL || paymentMethods.STRIPE || paymentMethods.RAZORPAY
                      ? "flex"
                      : "none",
                  flexDirection: "column",
                }}
              >
                {(paymentMethods.PAYPAL || paymentMethods.STRIPE) && (
                  <div className="payrow p-3" style={{ flex: 1 }}>
                    {paymentMethods.PAYPAL && (
                      <div className="payimgdiv">
                        <img
                          src={Paypal}
                          alt="paypal"
                          name="Paypal"
                          onClick={(e) => handlepaytypeClick(e)}
                          className={`payment-image ${
                            selectedPaymentMethod === "Paypal" ? "selectedpay" : ""
                          }`}
                        />
                      </div>
                    )}
                    {paymentMethods.STRIPE && (
                      <div className="payimgdiv">
                        <img
                          src={Stripe}
                          alt="stripe"
                          name="Stripe"
                          onClick={(e) => handlepaytypeClick(e)}
                          className={`payment-image ${
                            selectedPaymentMethod === "Stripe" ? "selectedpay" : ""
                          }`}
                        />
                      </div>
                    )}
                  </div>
                )}
              
                {showOrAndRazorpay && paymentMethods.RAZORPAY && (
                  <div style={{ flex: 0 }}>
                    <p style={{ textAlign: "center" }}>or</p>
                    <div className="horizontal-line"></div>
                    <h6 className="p-3">Other Payments</h6>
                  </div>
                )}
              
                {paymentMethods.RAZORPAY && (
                  <div className="payrow p-3" style={{ flex: 1 }}>
                    <div className="payimgdiv">
                      <img
                        src={razorpay}
                        alt="razorpay"
                        name="razorpay"
                        className={`payment-image ${
                          selectedPaymentMethod === "razorpay" ? "selectedpay" : ""
                        }`}
                        onClick={(e) => handlepaytypeClick(e)}
                      />
                    </div>
                  </div>
                )}
              </div>
              
              
              )}
            </div>

            <div
              className="borderCurved p-3 d-flex flex-column"
              style={{ height: "100%" }}
            >
              <h5 style={{ textAlign: "center" }}>Order Summary</h5>
              <div className="horizontal-line"></div>

              {/* Main content */}
              <div className="flex-grow-1">
                <div className="row">
                  <label className="col-form-label col-sm-6">
                    Course Name:
                  </label>
                  <label
                    className="col-form-label col-sm-6"
                    F
                    style={{ textAlign: "right" }}
                  >
                    {orderData?.coursename}
                  </label>
                </div>
                <div className="horizontal-line"></div>
                <div className="row">
                  <label className="col-form-label col-sm-6">
                    Course Amount:
                  </label>
                  <label
                    className="col-form-label col-sm-6"
                    style={{ textAlign: "right" }}
                  >
                    {orderData?.courseAmount}
                  </label>
                  {orderData?.paytype === "PART" && (
                    <>
                      <label className="col-form-label col-sm-6">
                        Installment Number:
                      </label>
                      <label
                        className="col-form-label col-sm-6"
                        style={{ textAlign: "right" }}
                      >
                        {orderData?.installment}
                      </label>
                      <label className="col-form-label col-sm-6">
                        Partial Amount:
                      </label>
                      <label
                        className="col-form-label col-sm-6"
                        style={{ textAlign: "right" }}
                      >
                        {orderData?.amount}
                      </label>
                    </>
                  )}
                </div>
              </div>

              {/* Footer content */}
              <div>
                <div className="horizontal-line"></div>
                <div className="row">
                  <label className="col-form-label col-sm-6">
                    <b>Total:</b>
                  </label>
                  <label
                    className="col-form-label col-sm-6"
                    style={{ textAlign: "right" }}
                  >
                    <b>{orderData?.amount}</b>
                  </label>
                </div>
                <button
                  className="btn btn-primary btn-sm btn-block m-2"
                  onClick={handlePayNowClick}
                  disabled={!selectedPaymentMethod}
                >
                  Pay Now
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SelectPaymentGateway;
