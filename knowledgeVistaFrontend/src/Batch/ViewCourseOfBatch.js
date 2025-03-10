import axios from "axios";
import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import baseUrl from "../api/utils";
import errorimg from "../images/errorimg.png";
const ViewCourseOfBatch = () => {
  const { batchTitle,batchid } = useParams();
  const [submitting, setsubmitting] = useState(false);
  const token = sessionStorage.getItem("token");
  const [courses, setCourses] = useState([]);
  const navigate = useNavigate();
  const numericBatchId = batchid.split("_")[1];
  const[counts,setcounts]=useState({
    revenue:"",
    students:""
  })
  const fetchcountsOfBatch = async () => {
    try {
      setsubmitting(true);
      const response = await axios.get(
        `${baseUrl}/Batch/getcounts`,
        {
          params:{
            id:numericBatchId
          },
          headers: {
            Authorization: token,
          },
        }
      );
      setsubmitting(false);
      if (response.status === 200) {
        setcounts(response.data);
      }
    } catch (error) {
      setsubmitting(false);
      console.log(error);
      if (error.response && error.response.status === 401) {
        navigate("/unauthorized");
      }
    }
  };
  const fetchCourseOFBatch = async () => {
    try {
      setsubmitting(true);
      const response = await axios.get(
        `${baseUrl}/Batch/getCourses/${batchid}`,
        {
          headers: {
            Authorization: token,
          },
        }
      );
      setsubmitting(false);
      if (response.status === 200) {
        setCourses(response.data);
      }
    } catch (error) {
      setsubmitting(false);
      console.log(error);
      if (error.response && error.response.status === 401) {
        navigate("/unauthorized");
      } else {
        throw error;
      }
    }
  };
  useEffect(() => {
    
    fetchCourseOFBatch();
    fetchcountsOfBatch()
  }, []);
  return (
    <div className="parent">
      <div className="page-header">  <div className="page-block">
                <div className="row align-items-center">
                    <div className="col-md-12">
                        <div className="page-header-title">
                            <h5 className="m-b-10">Courses Of {batchTitle}</h5>
                        </div>
                        <ul className="breadcrumb">
                            <li className="breadcrumb-item"><a href="#"onClick={()=>{navigate("/batch/viewall")}} ><i className="fa-solid fa-object-group"></i></a></li>
                            <li className="breadcrumb-item"><a href="#"onClick={()=>{navigate("/batch/viewall")}} >{batchTitle}</a></li>
                            <li className="breadcrumb-item"><a href="#">Courses</a></li>
                        </ul>
                       
                    </div>
                </div>
            </div></div>
      {submitting ? (
        <div className="outerspinner active">
          <div className="spinner"></div>
        </div>
      ) : (
      <div>   
        <div style={{display:"flex",gap:"10px" ,flexWrap:"wrap"}}>
   <div className="pointer" style={{width:"300px"}}>
  <div className="card" onClick={()=>{navigate("/payment/transactionHitory")}}>
    <div className="card-body">
      <div className="row align-items-center">
        <div className="col">
          <h3>{counts.revenue}<i className="fa-solid fa-indian-rupee-sign text-c-green m-l-10"></i></h3>
          <h6 className="text-muted m-b-0">
            Revenue
          </h6>
        </div>
      </div>
    </div>
  </div>
</div>
<div className=" pointer" style={{width:"300px"}} onClick={()=>navigate(`/batch/ViewStudents/${batchTitle}/${batchid}`)}>
  <div className="card">
    <div className="card-body">
      <div className="row align-items-center">
        <div className="col">
          <h3>{counts.students} <i className="fa fa-users text-c-purple m-l-10"></i></h3>
          <h6 className="text-muted m-b-0">
            Students
          </h6>
        </div>
      
      </div>
    </div>
  </div>
</div>
<div className=" pointer" style={{width:"300px"}}>
  <div className="card">
    <div className="card-body">
      <div className="row align-items-center">
        <div className="col">
          <h3>55% <i className="fa-solid fa-thumbs-up text-c-yellow"></i></h3>
          <h6 className="text-muted m-b-0">
            Pass
          </h6>
        </div>
      
      </div>
    </div>
  </div>
</div>
<div className=" pointer" style={{width:"300px"}}>
  <div className="card">
    <div className="card-body">
      <div className="row align-items-center">
        <div className="col">
          <h3>45%<i className="fa-solid fa-thumbs-down text-c-red"></i></h3>
          <h6 className="text-muted m-b-0">
            Fail
          </h6>
        </div>
      
      </div>
    </div>
  </div>
</div>
</div>
        {courses.length > 0 ? ( // Corrected condition placement
        <div className="row">
          {courses
            .slice()
            .reverse()
            .map((item) => (
              <div className="col-md-6 col-xl-3 course" key={item.courseId}>
                <div className="card mb-3">
                  <img
                    style={{ cursor: "pointer" }}
                    title={`${item.courseName} image`}
                    className="img-fluid card-img-top"
                    src={`data:image/jpeg;base64,${item.courseImage}`}
                    onError={(e) => {
                      e.target.src = errorimg; // Use the imported error image
                    }}
                    alt="Course"
                  />
                  <div className="card-body">
                    <h5
                      className="courseName"
                      title={item.courseName}
                      style={{ cursor: "pointer" }}
                    >
                      {item.courseName}
                    </h5>
                    <a
                      title="Schedule Quizz"
                      className="btn btn-sm btn-outline-success w-100"
                    onClick={()=>{navigate(`/sheduleQuizz/${batchTitle}/${batchid}/${item.courseName}/${item.courseId}`)}}
                    >
                      Schedule Quizz
                    </a>
                  </div>
                </div>
              </div>
            ))}
        </div>
      ) : (
        <div>
          <h1 className="text-light">No Course Found</h1>
        </div>
      )}
      </div>)}
    
    </div>
  );
};

export default ViewCourseOfBatch;
