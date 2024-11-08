
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import undraw_profile from "../images/profile.png"
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';
import errorimg from "../images/errorimg.png"
const AssignCourse = () => {

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
            const userData =  response.data;
           
            if(userData.profile!==null){
            setImg(`data:image/jpeg;base64,${userData.profile}`);
          }
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
            window.location.href="/unauthorized"
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
            window.location.href = "/view/Students";
        } 
    } catch (error) {
      if(error.response && error.response.status===400){
         MySwal.fire({
            icon: 'error',
            title: 'An unexpected error occurred!',
            text: error.response.data.error,
            confirmButtonText: "OK"
        });
      }else{
      //   MySwal.fire({
      //     icon: 'error',
      //     title: 'An unexpected error occurred!',
      //     text: "Try Again After Some Time",
      //     confirmButtonText: "OK"
      // });
      throw error
      }
       // window.location.reload()
    }
};

  return (
    <div>
      <div className='contentbackground'>
        <div className='contentinner'>
        <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
          <h3>Assign Course</h3>
          <div className='mainform'>
            <div className='profile-picture'>
              <div className='image-group'>
                <img id="preview" src={img ? img :undraw_profile}  onError={(e) => {
                        e.target.src = errorimg; // Use the imported error image
                      }} 
                      alt='profile' />
              </div>
              <div className=" mt-2 p-3 rounded" style={{ backgroundColor: "#F2E1F5", height: "100px" }}>
                <div className='inputgrp2'>
                  <label htmlFor='Name'> Name</label>
                  <span>:</span>
                  <label>
                    {userData.username}</label>
                </div>
                <div className='inputgrp2'>
                  <label htmlFor='Name'> Email</label>
                  <span>:</span>
                  <label>
                    {userData.email}</label>
                </div>
              </div>
            </div>
  {courses.length <= 0 ? (
  <div className='formgroup rounded p-5 ' style={{ backgroundColor: '#F2E1F5' }}>
  <h1 className='text-center' style={{margin:"auto"}}>No courses found  to assign</h1>
  </div>
) : (
  <div className='formgroup rounded p-5 assignlist' >
    {courses.map(course => (
      <div key={course.courseId} className='checkboxes' >
        <input type="checkbox" checked={course.selected} onChange={() => handleToggleCourse(course.courseId)} />
        <label>
          {course.courseName}
        </label>
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
  );
};

export default AssignCourse;
