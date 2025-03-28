
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';
import errorimg from "../images/errorimg.png"
import undraw_profile from "../images/profile.png"
const AssignCourseTRAINER = () => {
    
  const { userId } = useParams();
  const [img, setImg] = useState();
  const [userData, setUserData] = useState({});
  const [courses, setCourses] = useState([]);
  const MySwal = withReactContent(Swal);
  const token=sessionStorage.getItem("token");
const navigate=useNavigate();
  useEffect(() => {
    const fetchData = async () => {
        try {
            const response = await axios.get(`${baseUrl}/view/users/${userId}`,{
              headers:{
                Authorization:token
              }
            });
            const userData = response.data;
          
            if(userData.profile!==null){
            setImg(`data:image/jpeg;base64,${userData.profile}`);
            }
            setUserData(userData);
            const response1 = await axios.get(`${baseUrl}/course/allotList?email=${userData.email}`, {
                headers: {
                    Authorization: token
                }
            });
            const data =  response1.data;
            setCourses(data);
        } catch (error) {
          if(error.response && error.response.status===401){
             navigate("/unauthorized")
          }else{
            console.error('Error fetching user data:', error);
            throw error
          }
        }
    };

    fetchData();
}, []);

  const handleToggleCourse = courseId => {
    setCourses(prevCourses =>
      prevCourses.map(course =>
        course.courseId === courseId ? { ...course, selected: !course.selected } : course
      )
    );
  };

  
const handleAssignCourse = async () => {
  const selected = courses.filter(course => course.selected);
  const unselected = courses.filter(course => !course.selected);
  const courseData = {
    selectedCourses: selected.map(course => course.courseId), // List of Longs
    unselectedCourses: unselected.map(course => course.courseId) // List of Longs
  };
  try {
    const response = await axios.post(`${baseUrl}/AssignCourse/trainer/${userId}/courses`,courseData, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      }
      
    });

    if (response.status===200) {
      await MySwal.fire({
        icon: 'success',
        title: 'Courses Assigned!',
        text: 'Selected courses have been assigned successfully.'
      });
      navigate("/view/Trainer")
    } 
  } catch (error) {
    if(error.response){
      if(error.response.status===401){
        MySwal.fire({
          title: "Un Authorized!",
          text: "you are unable to Assign course to  a Trainer",
          icon: "error",
          confirmButtonText: "OK",
        });
      } else if(error.response.status===404){
        MySwal.fire({
          title: " Not Found!",
          text: error.response.data,
          icon: "error",
          confirmButtonText: "OK",
        });
      }
      else{
        throw error
      }
    }
  }
};
  return (
    <div>
    <div className="page-header"></div>
    <div className="card">
      <div className="card-body">
      <div className="row">
      <div className="col-12">
        <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
          <h3>Assign Course</h3>
          <div className='mainform'>
            <div className='profile-picture'>
              <div className='image-group'>
                <img id="preview" src={img ? img :undraw_profile} 
                 onError={(e) => {
                  e.target.src = errorimg; // Use the imported error image
                }}
                 alt='profile' />
              </div>
              <div className=" p-2 rounded" style={{width:"100%", backgroundColor: "#F2E1F5" }}>
                <div className='form-group row '>
                  <label htmlFor='Name' className="col-sm-4 col-form-label"> Name</label>
                  <div className="col-sm-8">
                  <label className="col-form-label">
                    {userData.username}</label>
                    </div>
                </div>
                <div className='form-group row'>
                  <label htmlFor='Name' className="col-sm-4 col-form-label"> Email</label>
                  <div className="col-sm-8">
                  <label className="col-form-label">
                    {userData.email}</label>
                    </div>
                </div>
              </div>
            </div>
            {courses.length <= 0 ? (
  <div className='formgroup rounded p-5 ' style={{ backgroundColor: '#F2E1F5' }}>
    <h1 className='text-center' style={{margin:"auto"}}>No courses found to assign</h1>
  </div>
) : (
  <div className='formgroup rounded p-5 assignlist'>
    {courses.map(course => (
      <div key={course.courseId} className='checkboxes'>
        <input
          type="checkbox" 
          checked={course.selected} // The checkbox state is controlled by course.selected
          onChange={() => handleToggleCourse(course.courseId)} // Toggle course selection on change
        />
        <label>{course.courseName}</label>
      </div>
    ))}
  </div>
)}



          </div>
          <div className='btngrp'>
            <button className='btn btn-primary' onClick={handleAssignCourse} >Save</button>
          </div>
        </div>
      </div>
      </div>
      </div>
    </div>
  )
}

export default AssignCourseTRAINER
