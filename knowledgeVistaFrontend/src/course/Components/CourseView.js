import React from "react";
import styles from "../../css/CourseView.module.css";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils";
import axios from "axios";

const CourseView = ({ filteredCourses }) => {
  const MySwal = withReactContent(Swal);
  const role = sessionStorage.getItem("role");
  const userId = sessionStorage.getItem("userid");
  const token = sessionStorage.getItem("token");

  const handlesubmit = async (courseId, userId) => {
    try {
      const data=JSON.stringify({
        courseId: courseId,
        userId: userId
    })
        const response = await axios.post(`${baseUrl}/buyCourse/create`,data, {
      
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.status===200) {
            const errorMessage =  response.data;
           
        } else {
            const order =  response.data;

            const options = {
                order_id: order,  
                handler: function (response) {
                    if (response.error) {
                      MySwal.fire({
                        icon: 'error',
                        title: ' Payment canceled or failed:', 
                        text: response.error
                    }); 
                    } else {
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
      MySwal.fire({
        icon: 'error',
        title: 'Error creating order:', 
        text: error
    });
    }
};

  const sendPaymentIdToServer = async (paymentId, order) => {
    try {
      const paydata=JSON.stringify({
        paymentId: paymentId,
        orderId: order,
    })
        const response = await axios.post(`${baseUrl}/buyCourse/payment`,paydata, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status===200) {
            // Success response
            const message =  response.data;
             window.location.href=message;
        
        } else {
          
            const errorMessage =  response.data;
            MySwal.fire({
              icon: 'error',
              title: 'Error updating payment ID:', 
              text: errorMessage
          });
        }
    } catch (error) {
      MySwal.fire({
        icon: 'error',
        title: 'Error sending payment ID to server:', 
        text: error
    });
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
    
      if (response.status==200) {
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
    
    MySwal.fire({
      icon: 'error',
      title: 'Not Found',
      text: error
  });
}
  }
}
};


  return (
    <div className="contentbackground">
      <div className="contentinner">
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
    </div>
    </div>
  );
};

export default CourseView;
