import React, { useEffect, useState } from "react";
//import styles from "../../css/CourseView.module.css";
import "../../css/CourseView.css"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils";
import errorimg from"../../images/errorimg.png"
import axios from "axios";
const CourseView = ({ filteredCourses }) => {
  const MySwal = withReactContent(Swal);
  const userId = sessionStorage.getItem("userid");
  const [submitting,setsubmitting]=useState(false);
  const token = sessionStorage.getItem("token");
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
  const handlepaytype =(courseId, userId,paytype)=>{

    let url=""
    if(paytype==="FULL"){
      url="/full/buyCourse/create";
      handlesubmit(courseId,userId,url);
    }else{
    
    MySwal.fire({
      icon:"question",
      title: 'Payment Type?',
      text:"Want To Pay the Amount Partially or Fully? ",
      showDenyButton: true,
      showCancelButton: true,
      confirmButtonColor:"#4e73df",
      denyButtonColor:"#4e73df",
      confirmButtonText: `Pay Fully `,
      denyButtonText: `Pay in  Part`,
    }).then((result) => {
      if (result.isConfirmed) {
        url="/full/buyCourse/create";
        handlesubmit(courseId,userId,url);
      } else if (result.isDenied) {
        url="/part/buyCourse/create";
        
        handlesubmit(courseId,userId,url);
      }
    })
    }
  }
  const handlesubmit = async (courseId, userId,url) => {
    try {
      setsubmitting(true)
  const data=JSON.stringify({
        courseId: courseId,
        userId: userId
    })
     
        const response = await axios.post(`${baseUrl}${url}`,data, {
      
          headers: {
            "Authorization": token,
            'Content-Type': 'application/json'
          }
        });
      
        setsubmitting(false)
        const scriptLoaded = await loadRazorpayScript();

        if (!scriptLoaded) {
          alert("Failed to load Razorpay SDK. Please try again.");
          return;
        }
            const order =  response.data;
            const options = {
                order_id: order.orderId,  
                description :order.description,
               name:order.name,
                handler: function (response) {
                  if (response.error ){
                    MySwal.fire({
                      icon: 'error',
                      title: 'Payment Failed:',
                      text: response.error,
                    });
                  } else {
                    
                        sendPaymentIdToServer(response.razorpay_payment_id, order.orderId,response.razorpay_signature);

                    }
                },
                
                theme: {
                    color: "#3399cc"
                },
            };

            var pay = new window.Razorpay(options);
            
            pay.open();
           
        
    } catch (error) {
      setsubmitting(false)
    //   MySwal.fire({
    //     icon: 'error',
    //     title: 'Error creating order:', 
    //     text:  error.response.data ? error.response.data :"error occured"
    // });
    throw error
    }
};

  const sendPaymentIdToServer = async (paymentId, order ,signature) => {
    try {
      setsubmitting(true)
      const paydata=JSON.stringify({
        paymentId: paymentId,
        orderId: order,
        signature:signature,
    })
        const response = await axios.post(`${baseUrl}/buyCourse/payment`,paydata, {
          headers: {
            "Authorization": token,
            'Content-Type': 'application/json'
          }
        });

        if (response.status===200) {
          setsubmitting(false)
            // Success response
            const message =  response.data;
             window.location.href=message;
        
        } else {
          setsubmitting(false)
            const errorMessage =  response.data;
            MySwal.fire({
              icon: 'error',
              title: 'Error updating payment ID:', 
              text: errorMessage
          });
        }
    } catch (error) {
      setsubmitting(false)
    //   MySwal.fire({
    //     icon: 'error',
    //     title: 'Error sending payment ID to server:', 
    //     text: error.response.data ? error.response.data : "error occured"
    // });
    throw error
    }
};
const handleClick = async (event, id,amount,url) => {
  event.preventDefault();
if(amount===0){
  window.location.href=url;
}else{
  try {
    const formdata=JSON.stringify({ courseId: id})
      const response = await axios.post(`${baseUrl}/CheckAccess/match`, formdata,{

          headers: {
              'Content-Type': 'application/json',
              "Authorization":token
          } 
      });
    
      if (response.status===200) {
          const message = response.data;
          window.location.href = message;
      } 
  } catch (error) {
    if(error.response.status===401){
      MySwal.fire({
        icon: 'error',
        title: 'Oops...',
        text: "cannot Access Course "
    });
    
    }else{
    
  //   MySwal.fire({
  //     icon: 'error',
  //     title: 'Not Found',
  //     text: error
  // });
  throw error
}
  }
}
};


  return (
    <div className="contentbackground">
      <div className="contentinner">
    <div className="supercontainernew" >
    <div></div>
   {submitting &&  <div className="outerspinner active">
        <div className="spinner"></div>
      </div>}
      {filteredCourses.length > 0 ? (
        <ul className="maincontainernew"  style={{height:"65vh"}}>
          {filteredCourses
            .slice()
            .reverse()
            .map((item) => (
              <li key={item.courseId}>
                <div className="containersnew">
                  <div className="imagedivnew">
                    <img
                      src={`data:image/jpeg;base64,${item.courseImage}`}
                      onError={(e) => {
                        e.target.src = errorimg; // Use the imported error image
                      }}
                      alt="Course"
                    />
                  </div>
                 
                  <div className="contentnew">
                    <h4>
                   
                      <button className="anchorlike" onClick={(e)=>{handleClick(e,item.courseId,item.amount,item.courseUrl)}}>

                        {item.courseName.length > 15
                          ? item.courseName.slice(0, 15) + "..."
                          : item.courseName}
                      </button>
                    </h4>
                    <p>
                      {item.courseDescription.length > 40
                        ? item.courseDescription.slice(0, 40) + "..."
                        : item.courseDescription}
                    </p>
                    <h6>
                      {item.amount === 0 ? (
                        <a
                          href={item.courseUrl}
                          className=" btn btn-outline-success w-100"
                        >
                          Enroll for Free
                        </a>
                      ) : (
                        <div
                          style={{
                          
                            display: "grid",
                            gridTemplateColumns: "1fr 2fr"
                          }}
                        >
                          <div>
                            <i className="fa-solid fa-indian-rupee-sign"></i>
                            <label className="mt-3 blockquote">
                              {item.amount}
                            </label>
                          </div>
                          <a
                            className="btn btn-outline-primary"
                            style={{maxHeight:"50px" ,padding:"5px"}}
                            onClick={() => handlepaytype(item.courseId, userId,item.paytype)}
                          >
                            Enroll Now
                          </a>
                        </div>
                      )}
                    </h6>
                  </div>
                </div>
              </li>
            ))}
        </ul>
    
      ) : (
        <div className="maincontainernew" style={{borderRadius:"10px",display:"flex",justifyContent:"center",alignItems:"center"}}>
             <h1>No Course Found </h1>
        </div>
      )}
    </div>
    </div>
    </div>
  );
};

export default CourseView;
