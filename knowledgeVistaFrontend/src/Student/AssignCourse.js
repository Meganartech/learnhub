
import React, { useContext, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';
import { GlobalStateContext } from '../Context/GlobalStateProvider';
import errorimg from "../images/errorimg.png"
import altBatchImage from "../images/altBatchImage.jpg"
const AssignCourse = () => {
 const { displayname } = useContext(GlobalStateContext);
 const Currency = sessionStorage.getItem("Currency");
  const { userId } = useParams()
  const [userData, setUserData] = useState({});
  const [courses, setCourses] = useState([]);
  const MySwal = withReactContent(Swal);
  const token=sessionStorage.getItem("token");
  const navigate=useNavigate();
  const[assignedBatches,setassignedBatches]=useState([])
  const fetchAssignedBAtches=async()=>{
    try {
      const response = await axios.get(`${baseUrl}/user/GetBatches/${userId}?page=${0}&size=${10}`,{
        headers:{
          Authorization:token
        }
      });
      if(response?.status===200){
      setassignedBatches(response?.data?.content)
      }
    }catch(err){
      if(err.response && err.response.status===401){
        navigate("/unauthorized");
      }else{
        console.log(err);
      }
      }
  }
  useEffect(() => {
    const fetchData = async () => {
        try {
            const response = await axios.get(`${baseUrl}/view/users/${userId}`,{
              headers:{
                Authorization:token
              }
            });
            const userData =  response.data;
          
            setUserData(userData);

            const response1 = await axios.get(`${baseUrl}/course/assignList?email=${userData.email}`, {
                headers: {
                    Authorization: token
                }
            });
            const data = response1.data;
            setCourses(data);
        } catch (error) {
          if(error.response && error.response.status===401){
            navigate("/unauthorized");
          }else{
            console.error('Error fetching user data:', error);
            throw error
          }
        }
    };

    fetchData();
    fetchAssignedBAtches()
}, []);

  const handleToggleCourse = courseId => {
    setCourses(prevCourses =>
      prevCourses.map(course =>
        course.courseId === courseId ? { ...course, selected: !course.selected } : course
      )
    );
  };

  const otherBatches = [
    "Batch B1", "Batch B2", "Batch B3", "Batch B4", "Batch B5", "Batch B6",
  ];

  const [view, setView] = useState(null); 
const handleAssignCourse = async () => {
    const selected = courses.filter(course => course.selected);
    const unselected = courses.filter(course => !course.selected);
    const courseData = {
      selectedCourses: selected.map(course => course.courseId), // List of Longs
      unselectedCourses: unselected.map(course => course.courseId) // List of Longs
    };
    try {
        const response = await axios.post(`${baseUrl}/AssignCourse/${userId}/courses`,courseData, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token
            }
        });

        const data =  response.data;

        if (response.status===200) {
            // Handle success response
            await MySwal.fire({
                icon: 'success',
                title: 'Courses Assigned!',
                text: response.data.message
            });
            // Redirect after success
             navigate("/view/Students");
        } 
    } catch (error) {
      
      //   MySwal.fire({
      //     icon: 'error',
      //     title: 'An unexpected error occurred!',
      //     text: "Try Again After Some Time",
      //     confirmButtonText: "OK"
      // });
      throw error
    
       // window.location.reload()
    }
};

  return (
    <div>
    <div className="page-header">
    <div className="page-block">
          <div className="row align-items-center">
            <div className="col-md-12">
              <div className="page-header-title">
                <h5 className="m-b-10">Settings </h5>
              </div>
              <ul className="breadcrumb">
                <li className="breadcrumb-item">
                  <a
                    href="#"
                    onClick={() => {
                      navigate("/admin/dashboard");
                    }}
                    title="dashboard"
                  >
                    <i className="feather icon-home"></i>
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a href="#" onClick={()=>{navigate("/view/Students")}}>
                    {" "}
                    {displayname && displayname.student_name
                      ? displayname.student_name
                      : "Student"}{" "}
                    Details{" "}
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a href="#" >
                    Assign Course
                  </a>
                </li>
              </ul>
            </div>
          </div>
        </div>
    </div>
    <div className="card">
      <div className="card-body">
      <div className="row">
      <div className="col-12">
        <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
         <div className='tableheader '><h4>Assign batch</h4> <div className='selectandadd'> <p><b className='text-primary'>Name : </b>{userData?.username}</p><p><b className='text-primary'>Email : </b>{userData?.email}</p></div></div>
         <div className='courseBatchSplit '>
          <div className='courseList card'>
            {courses.map((item)=>(
              <span key={item?.courseId}   className='checkboxes'> <input  type="checkbox"/> <label title={item?.courseName}>{item?.courseName}</label></span>
            ))}
             
          </div>

          <div className="batchView pl-2 ">
      {view !== "other" && (
        <div className="mb-4">
          <div className="head">
            <h5>Assigned Batches</h5>
            <button
              className="text-blue hidebtn"
              onClick={() => setView(view === "assigned" ? null : "assigned")}
            >
              {view === "assigned" ? "Show Less" : "See All"}
            </button>
          </div>
          <div className="batch-container-small">
          {(view === "assigned"  ? assignedBatches : assignedBatches.slice(0, 4)).map((item) => (
         
            <div className="batch-small mb-3 card" key={item.id}>
              {item.batchImage ? (
                <img
                  style={{ cursor: "pointer" }}
                  title={`${item.batchtitle} image`}
                  className="img-fluid card-img-top"
                  src={`data:image/jpeg;base64,${item.batchImage}`}
                  onError={(e) => {
                    e.target.src = errorimg;
                  }}
                  onClick={() => navigate(`/batch/viewcourse/${item.batchtitle}/${item.batchId}`)}
                  alt="Batch"
                />
              ) : (
                <div
                  className="img-fluid card-img-top"
                  title={item.batchtitle}
                  onClick={() => navigate(`/batch/viewcourse/${item.batchtitle}/${item.batchId}`)}
                  style={{
                    backgroundImage: `url(${altBatchImage})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                    height: "100px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <h5 style={{ color: "white", textAlign: "center", margin: 0 }}>
                    {item.batchtitle}
                  </h5>
                </div>
              )}
              <div className="card-body pt-1 pb-1">
                <p
                  className="courseName mb-0"
                  title={item.batchtitle}
                  style={{ cursor: "pointer" }}
                  onClick={() => navigate(`/batch/viewcourse/${item.batchtitle}/${item.batchId}`)}
                >
                 <b> {item.batchtitle}</b>
                </p>
                
                <small className="batchlist">
                  <b>Courses:</b> {item?.coursenames}
                </small>
                <small className="batchlist">
                  <b>Trainers:</b> {item?.trainernames}
                </small>
                <small className="batchlist">
                  <b>Duration:</b> {item.duration}
                  <div className="amt">
                        <i
                          className={
                            Currency === "INR"
                              ? "fa-solid fa-indian-rupee-sign pr-1"
                              : "fa-solid fa-dollar-sign pr-1"
                          }
                        ></i>
                        <span>{item.amount}</span>
                      </div>
                </small>
                   
                  
              </div>
            </div>
        ))}
        </div>
        </div>)}
        
      {view === null && <hr className="borderLine p-0 my-2" />}
      {view !== "assigned" && (
        <div>
          <div className="head">
            <h5>Other Batches</h5>
            <button
              className="text-blue hidebtn"
              onClick={() => setView(view === "other" ? null : "other")}
            >
              {view === "other" ? "Show Less" : "See All"}
            </button>
          </div>
          <ul className="list-disc ml-5">
            {(view === "other" ? otherBatches : otherBatches.slice(0, 4)).map(
              (batch, index) => (
                <li key={index}>{batch}</li>
              )
            )}
          </ul>
        </div>
      )}
    </div>
         </div>
          <div className='cornerbtn'>
          <button className='btn btn-secondary' onClick={()=>{navigate(-1)}} >Cancel</button>
            <button className='btn btn-primary' onClick={handleAssignCourse} >Assign</button>
          </div>
        </div>
      </div>
      </div>
      </div>
    </div>
  );
};

export default AssignCourse;
