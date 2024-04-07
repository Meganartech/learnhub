import { useState ,useEffect} from "react";
import React from "react";
import "../css/Style.css";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

import EditCourseForm from "./Update/EditCourseForm";
import { useParams } from "react-router-dom";
const CourseDetails = () => {
  const [img, setimg] = useState();
  const MySwal = withReactContent(Swal);
  const [editMode, setEditMode] = useState(false);
  const [courseEdit, setCourseEdit] = useState([]);
  const {courseId}=useParams();

  const toggleEditMode = () => {
    setEditMode(!editMode);
  };


  useEffect(() => {
    const fetchcourse = async () => {
      try {
        console.log(courseId);
        const response = await fetch(
          `http://localhost:8080/course/get/${courseId}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json", // Set appropriate headers if needed
            },
          }
        );
        if (!response.ok) {
          // If response is not successful (HTTP status code not in the range 200-299)
          MySwal.fire({
            icon: "error",
            title: "HTTP Error!",
            text: `HTTP error! Status: ${response.status}`,
          });
        }
        const data = await response.json(); // Convert response to JSON format
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
   

  return (
    <div>
      {editMode ? (
        <EditCourseForm id={courseId} toggleEditMode={toggleEditMode} />
      ) : (
        <div className="contentbackground">
          <div className="contentinner">
          <div className="outer mb-3">
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
                  className="form-control"
                  
                />
              </div>
              <div className="form-group">
                <label htmlFor="courseDescription">Course Description</label>
                <textarea
                  readOnly
                  id="courseDescription"
                  rows={5}
                  value={courseDescription}
                  className="form-control"
                />
              </div>
              <div className="form-group">
                <label htmlFor="courseCategory">Course Category</label>
                <input
                  readOnly
                  type="text"
                  id="courseCategory"
                  value={courseCategory}
                  className="form-control"
                />
              </div>
            </div>
            <div></div>
            <div className="second">
              <h2>Course Image</h2>
              {courseImage && (
                <img src={courseImage} alt="Course"  
                style={{
                  maxWidth: "100%",
                  maxHeight: "200px",
                  alignItems: "center",
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
                  className="form-control"
                />
              </div>
            </div>

          </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CourseDetails;
