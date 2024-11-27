import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import baseUrl from "../api/utils";
import axios from "axios";
import errorimg from "../images/errorimg.png";
const Mycourse = () => {
  const token = sessionStorage.getItem("token");
  const [courses, setCourses] = useState([]);
  const navigate = useNavigate();
  useEffect(() => {
    const fetchItems = async () => {
      try {
        // Replace {userId} with the actual user ID
        const response = await axios.get(
          `${baseUrl}/AssignCourse/student/courselist`,
          {
            headers: {
              Authorization: token,
            },
          }
        );

        const data = response.data;
        setCourses(data);
      } catch (error) {
        console.error("Error fetching courses:", error);
        // Handle error here, for example, show an error message
      }
    };

    fetchItems();
  }, []);

  return (
    <>
      <div className="page-header"></div>
      <div className="row">
        <div className="col-sm-12">
          {courses.length === 0 ? (
            <div className="card">
              <div className="card-body">
                <div className="centerflex">
                  <div className="enroll">
                    <h3 className="mt-4">No courses Enrolled </h3>
                    <Link to="/dashboard/course" className="btn btn-primary">
                      Enroll Now
                    </Link>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div className="row">
              {courses.map((item) => (
                <div className="col-md-6 col-xl-3 course" key={item.courseId}>
                  <div className="card mb-3">
                    <img
                      style={{ cursor: "pointer" }}
                      onClick={(e) => {
                        window.location.href = item.courseUrl;
                      }}
                      className="img-fluid card-img-top"
                      src={`data:image/jpeg;base64,${item.courseImage}`}
                      onError={(e) => {
                        e.target.src = errorimg; // Use the imported error image
                      }}
                      alt="Course"
                    />
                    <div className="card-body">
                      <h5>
                        <a href={item.courseUrl} className="card-title">
                          {item.courseName.length > 15
                            ? item.courseName.slice(0, 15) + "..."
                            : item.courseName}
                        </a>
                      </h5>
                      <div className="card-text">{item.courseDescription}</div>
                      <div className="card-text">{item.courseCategory} </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Mycourse;
