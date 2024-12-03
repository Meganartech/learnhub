import React, { useEffect, useState } from "react";
import baseUrl from "../../api/utils";
import axios from "axios";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import errorimg from "../../images/errorimg.png";
import Header from "../../Common Components/Header";
import pcoded from "../../assets/js/pcoded.js"
import Sidebar from "../../Common Components/Sidebar.js";
import { useNavigate } from "react-router-dom";
const ViewCourseVps = () => {
  useEffect(() => {
      pcoded();  
      },[]);
  const MySwal = withReactContent(Swal);
  const [searchQuery, setSearchQuery] = useState("");
  const [course, setCourse] = useState([]);  
  const [submitting,setsubmitting]=useState(false);
  const[notfound,setnotfound]=useState(false);
  const islogedin=sessionStorage.getItem("token")!==null;
  const navigate=useNavigate();
  useEffect(() => {
    const fetchCourse = async () => {
      try {
        
          setsubmitting(true); // Show loading indicator
          const response = await axios.get(`${baseUrl}/course/viewAllVps`);
          setsubmitting(false); // Hide loading indicator
          const data = response.data;
          setCourse(data); // Set the fetched course data
        
      } catch (error) {
        setnotfound(true)
        console.error('Error fetching courses:', error);
        throw error
      }
    };

   
      fetchCourse();
    
  }, []); // This effect depends on the `showInLandingPage` state

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };
  const filteredCourses = course.filter((item) => {
    const name = item.courseName ? item.courseName.toLowerCase() : "";
    return name.includes(searchQuery.toLowerCase());
  });

  const userId = sessionStorage.getItem("userid");
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
  }, []);  // Empty dependency array ensures this only runs once when the component mounts
  
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
   
    if (!token) {
      // Save payment data to localStorage or sessionStorage
      sessionStorage.setItem("pendingPayment", JSON.stringify({ courseId,  paytype }));
     
      navigate("/login");
      return;
    }
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
            navigate(message);
        
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
const handleClick = async (event, id,amount,url,paytype) => {
  if (!token) {
    // Save payment data to localStorage or sessionStorage
    sessionStorage.setItem("pendingPayment", JSON.stringify({ id,  paytype }));
   
    navigate("/login");
    return;
  }
  event.preventDefault();
if(amount===0){
navigate(url);
}else{
  try {
    if(token){
    const formdata=JSON.stringify({ courseId: id})
      const response = await axios.post(`${baseUrl}/CheckAccess/match`, formdata,{

          headers: {
              'Content-Type': 'application/json',
              "Authorization":token
          } 
      });
    
      if (response.status===200) {
          const message = response.data;
          navigate(message);
      } 
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
  <>
    {islogedin && <Sidebar/>}
    <Header searchQuery={searchQuery}
        handleSearchChange={handleSearchChange}
        setSearchQuery={setSearchQuery} />
  
    
     <div className="pcoded-main-container"   
     style={{ marginLeft: islogedin ? undefined : '0px' }}>
     <div className="pcoded-content" >
    <div className="page-header"></div>
    
    {filteredCourses && filteredCourses.length > 0 ? (
      <div>
        <h4 style={{color:"white"}}>Courses For You</h4>
        < div className="row">
          {filteredCourses
            .slice()
            .reverse()
            .map((item) => (
              <div className="col-6 col-md-4 col-lg-2 col-xl-2 course" key={item.courseId}>
                <div className="card mb-3">
                  <img
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
                        onClick={(e) =>
                          handleClick(
                            e,
                            item.courseId,
                            item.amount,
                            item.courseUrl,
                            item.paytype
                          )
                        }
                      >
                        {item.courseName.length > 15
                          ? item.courseName.slice(0, 15) + "..."
                          : item.courseName}
                      </h5>
                   
                    <div className="card-text">
                      {item.amount === 0 ? (
                        <a href="#" 
                        onClick={(e)=>{e.preventDefault();  navigate(item.courseUrl)}}
                        style={{ width:"100%" ,maxHeight: "50px", padding: "5px" }}
                        className="btn btn-outline-success">
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
      </div>
    ) : notfound ? (
      <h1>No Course Found</h1>
    ) : null}
    </div>
    </div>
  </>
);

};

export default ViewCourseVps;
