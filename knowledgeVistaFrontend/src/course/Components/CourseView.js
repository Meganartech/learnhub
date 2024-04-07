import React, { useState } from "react";
import styles from "../../css/CourseView.module.css";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const CourseView = ({ filteredCourses }) => {
  const MySwal = withReactContent(Swal);
  const role = sessionStorage.getItem("role");
  const userId = sessionStorage.getItem("userid");


  const handlesubmit = async (courseId, userId) => {
    try {
        const response = await fetch('http://localhost:8080/buyCourse/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                courseId: courseId,
                userId: userId
            })
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            MySwal.fire({
                icon: 'error',
                title: 'Oops...',
                text: errorMessage
            });
        } else {
            const order = await response.text();

            const options = {
                order_id: order, 
                name: "KnowledgeVista", 
                description: "This is for my final year project",
                handler: function (response) {
                    if (response.error) {
                        console.log('Payment canceled or failed:', response.error);
                    } else {
                        console.log('Payment successful:', response.razorpay_payment_id);
                        sendPaymentIdToServer(response.razorpay_payment_id, order);
                    }
                },
                theme: {
                    color: "#3399cc"
                }
            };

            var pay = new window.Razorpay(options);
            pay.open();
        }
    } catch (error) {
        console.error('Error creating order:', error);
    }
};

  const sendPaymentIdToServer = async (paymentId, order) => {
    try {
        const response = await fetch('http://localhost:8080/buyCourse/payment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                paymentId: paymentId,
                orderId: order,
            })
        });

        if (response.ok) {
            // Success response
            const message = await response.text();
             window.location.href=message;
        
        } else {
            // Error response
            const errorMessage = await response.text();
            console.error('Error updating payment ID:', errorMessage);
        }
    } catch (error) {
        console.error('Error sending payment ID to server:', error);
    }
};
const handleClick = async (event, id,amount,url) => {
  event.preventDefault();
if(amount===0){
  window.location.href=url;
}else{
  try {
      const response = await fetch('http://localhost:8080/CheckAccess/match', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json'
          },
          body: JSON.stringify({
              courseId: id,
              userId: userId,
          })
      });
    
      if (response.ok) {
          const message = await response.text();
          // Redirect to the URL provided in the response
          window.location.href = message;
      } else {
          // Handle different HTTP status codes appropriately
          if (response.status === 400) {
            MySwal.fire({
              icon: 'error',
              title: 'Oops...',
              text: "cannot Access Course "
          });
          } else if (response.status === 404) {
              // Handle Not Found
              console.error('Not Found:', await response.text());
          } else {
              // Handle other errors
              console.error('Error:', response.status, await response.text());
          }
      }
  } catch (error) {
      // Handle fetch operation errors
      console.error('Error sending request to server:', error);
  }
}
};


  return (
    <div className={styles.supercontainer} >
      {filteredCourses.length > 0 ? (
        <ul className={styles.maincontainer}>
          {filteredCourses
            .slice()
            .reverse()
            .map((item) => (
              <li key={item.courseId}>
                <div className={styles.containers}>
                  <div className={styles.imagediv}>
                    <img
                      src={`data:image/jpeg;base64,${item.courseImage}`}
                      alt="Course"
                    />
                  </div>
                 {/* href={item.courseUrl} */}
                  <div className={styles.content}>
                    <h4>
                   
                      <button className="anchorlike" onClick={(e)=>{handleClick(e,item.courseId,item.amount,item.courseUrl)}}>

                        {item.courseName.length > 15
                          ? item.courseName.slice(0, 15) + "..."
                          : item.courseName}
                      </button>
                    </h4>
                    <p>
                      {item.courseDescription.length > 20
                        ? item.courseDescription.slice(0, 20) + "..."
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
                            gridTemplateColumns: "1fr 1fr"
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
                            onClick={() => handlesubmit(item.courseId, userId)}
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
        <div className={styles.maincontainer} style={{borderRadius:"10px",display:"flex",justifyContent:"center",alignItems:"center"}}>
             <h1>No Course Found </h1>
        </div>
      )}
    </div>
  );
};

export default CourseView;
