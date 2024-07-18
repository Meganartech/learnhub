import { useState ,useEffect} from "react";
import React from "react";
import "../../css/Style.css";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils"
import EditCourseForm from "./EditCourseForm";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
const CourseDetails = () => {
  const [img, setimg] = useState();
  const MySwal = withReactContent(Swal);
  const [editMode, setEditMode] = useState(false);
  const [courseEdit, setCourseEdit] = useState([]);
  const {courseId}=useParams();
  const navigate=useNavigate();
  const toggleEditMode = () => {
    setEditMode(!editMode);
  };
  const token =sessionStorage.getItem("token")

  useEffect(() => {
    const fetchcourse = async () => {
      try {
        const response = await axios.get(
          `${baseUrl}/course/get/${courseId}`,
          {
            headers: {
              "Authorization":token,
              "Content-Type": "application/json", // Set appropriate headers if needed
            },
          }
        );
        if (!response.status===200) {
          // If response is not successful (HTTP status code not in the range 200-299)
          MySwal.fire({
            icon: "error",
            title: "HTTP Error!",
            text: `HTTP error! Status: ${response.status}`,
          });
        }
        const data =  response.data; // Convert response to JSON format
        setimg(`data:image/jpeg;base64,${data.courseImage}`);
       
        setCourseEdit(data);
      } catch (error) {
        MySwal.fire({
          title: "Error!",
          text: "An error occurred while Fetching course. Please try again later.",
          icon: "error",
          confirmButtonText: "OK",
        });
      }
    };
    fetchcourse();
  }, [courseId]);
   const  courseName= courseEdit.courseName;
   const  courseCategory=courseEdit.courseCategory;
   const  courseDescription=courseEdit.courseDescription;
   const amount=courseEdit.amount;
   const  courseImage=img;
   const Duration=courseEdit.duration;
   const Noofseats=courseEdit.noofseats;

  return (
    <div className="contentbackground">
          <div className="contentinner">
          <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate("/dashboard/course")}}><i className="fa-solid fa-xmark"></i></div>
      </div>
      {editMode ? (
        <EditCourseForm id={courseId} toggleEditMode={toggleEditMode} />
      ) : (
       
          <div className="outer">
            <div className="first">
              <div className="head">
                <h2>Course Details</h2>{" "}
                <h5>
                  <a onClick={toggleEditMode} className="btn btn-primary">
                    <i className="fas fa-edit"></i>
                  </a>
                </h5>
              </div>
              <div className="form-group">
                <label htmlFor="courseName">Course Name</label>
                <input
                  readOnly
                  type="text"
                  id="courseName"
                  value={courseName}
                  className='disabledbox '
        
                  
                />
              </div>
              <div className="form-group">
                <label htmlFor="courseDescription">Course Description</label>
                <textarea
                  readOnly
                  id="courseDescription"
                  rows={4}
                  value={courseDescription}
                  className='disabledbox'
                />
              </div>
              <div className="form-group">
                <label htmlFor="courseCategory">Course Category</label>
                <input
                  readOnly
                  type="text"
                  id="courseCategory"
                  value={courseCategory}
                  className='disabledbox'
                />
              </div>
              <div className="form-group mt-1">
                <label htmlFor="Noofseats">No of Seats</label>
                <input
                  readOnly
                  type="number"
                  id="Noofseats"
                  value={Noofseats}
                  className='disabledbox'
                />
              </div>
            </div>
           
            <div className="second">
              <h2>Course Image</h2>
              {courseImage && (
                <img src={courseImage} alt="Course"  
                style={{
                  width:'100px',
                  height:'100px'
                }}
                className="img-fluid" />
              )}
              {!courseImage && <p>No image available</p>}

              <div className="form-group mt-1">
                <label htmlFor="courseAmount">Course Amount</label>
                <input
                  readOnly
                  type="text"
                  id="courseAmount"
                  value={amount}
                  className='disabledbox'
                />
              </div>
              
              <div className="form-group mt-1">
                <label htmlFor="Duration">Course Duration</label>
                <input
                  readOnly
                  type="number"
                  id="Duration"
                  value={Duration}
                  className='disabledbox'
                />
              </div>
               
              <div className="form-group mt-1">
                <a href={`/course/update/paymentSettings/${courseName}/${courseId}`}>  Payment Settings</a>
              </div>
            </div>

          </div>
    
      )}
    </div>
    </div>
  );
};

export default CourseDetails;
