import React from "react";
import errorimg from "../../images/errorimg.png";
import { Link, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils";
import axios from "axios";
const EditCourse = ({ filteredCourses }) => {
  const navigate=useNavigate();
  const MySwal = withReactContent(Swal);
  const token = sessionStorage.getItem("token");
  const createCourse = async () => {
    try {
      const response = await axios.get(`${baseUrl}/api/v2/count`, {
        headers: {
          Authorization: token,
        },
      });

      if (response.status === 200) {
         navigate("/course/addcourse");
      } else if (response.status === 429) {
        Swal.fire({
          title: "Course Limit is Reached",
          text: "Need to upgrade your lisense",
          icon: "warning",
          showCancelButton: true,
          confirmButtonText: "ok",
        });

      }
    } catch (error) {
      throw error

    }
  };
  const handleDelete = (e, courseId) => {
    e.preventDefault();
    MySwal.fire({
      title: "Delete Course?",
      text: "Are you sure you want to delete this course ?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Delete",
      cancelButtonText: "Cancel",
    }).then((result) => {
      if (result.isConfirmed) {
        // If the user clicked "Delete"
        axios
          .delete(`${baseUrl}/course/${courseId}`, {
            headers: {
              Authorization: token,
            },
          })
          .then((response) => {
            if (response.status === 200) {
              MySwal.fire({
                title: "Deleted!",
                text: "Your course has been deleted.",
                icon: "success",
              }).then((result) => {
                if (result.isConfirmed) {
                  window.location.reload();
                }
              });
            }
          })
          .catch((error) => {
            if (error.response && error.response.status === 401) {
              navigate("/unauthorized")
            } else if(error.response && error.response.status === 403){
              MySwal.fire({
                title: "Error!",
                text: error.response.data
                  ? error.response.data
                  : "error occured",
                icon: "error",
                confirmButtonText: "OK",
              });
            }else{
            throw error
            }
          });
      }
    });
  };
  return (
    <>
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
                      navigate(item.courseUrl);
                    }}
                    className="img-fluid card-img-top"
                    src={`data:image/jpeg;base64,${item.courseImage}`}
                    onError={(e) => {
                      e.target.src = errorimg; // Use the imported error image
                    }}
                    alt="Course"
                  />
                  <div className="card-body">
                    <h5 className="card-title flexWithPadding">
                      <div
                        style={{ cursor: "pointer" }}
                        onClick={(e) => {
                          navigate(item.courseUrl);
                        }}
                      >
                        {item.courseName.length > 15
                          ? item.courseName.slice(0, 15) + "..."
                          : item.courseName}
                      </div>
                      <div className="gap-10">
                        <div className="dropdown">
                          <a
                            className="dropdown-toggle"
                            href="#"
                            data-toggle="dropdown"
                          >
                            <i className="fa-solid fa-plus"></i>
                          </a>
                          <div className="dropdown-menu dropdown-menu-right  dropdown-menu-top  dropdown-menu-top shadow animated--grow-in">
                            <Link
                              to={`/lessonList/${item.courseName}/${item.courseId}`}
                              className="dropdown-item"
                            >
                              Lessons
                            </Link>
                            <div className="dropdown-divider"></div>
                            <Link
                              to={`/course/testlist/${item.courseName}/${item.courseId}`}
                              className="dropdown-item"
                            >
                              Test
                            </Link>
                          </div>
                        </div>
                        <Link to={`/course/edit/${item.courseId}`}>
                          <i className="fa-solid fa-edit"></i>
                        </Link>

                        <a
                          href="#"
                          onClick={(e) => handleDelete(e, item.courseId)}
                        >
                          <i className="fas fa-trash"></i>
                        </a>
                      </div>
                    </h5>
                    <div className="card-text">
                      {item.amount === 0 ? (
                        <a
                        href="#"
                        onClick={(e)=>{e.preventDefault();  navigate(item.courseUrl)}}
                          className=" btn btn-outline-success w-100"
                        >
                          {" "}
                          Free
                        </a>
                      ) : (
                        <a className="btn btn-outline-primary w-100">
                          <i className="fa-solid fa-indian-rupee-sign mr-2"></i>
                          <label>{item.amount}</label>
                        </a>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
        </div>
      ) : (
        <div
          className="maincontainernew"
          style={{
            borderBottomLeftRadius: "10px",
            borderBottomRightRadius: "10px",
            height: "70vh",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <h1>No Course Found</h1>
        </div>
      )}
    </>
  );
};

export default EditCourse;