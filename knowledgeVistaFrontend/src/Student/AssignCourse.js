
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const AssignCourse = () => {

  const { userId } = useParams();
  const [img, setImg] = useState();
  const [userData, setUserData] = useState({});
  const [courses, setCourses] = useState([]);
  const MySwal = withReactContent(Swal);
  const token=sessionStorage.getItem("token");

  useEffect(() => {
    const fetchData = async () => {
        try {
            const response = await fetch(`http://localhost:8080/view/users/${userId}`);
            const userData = await response.json();
            if (!response.ok) {
                throw new Error('Failed to fetch user data');
            }
            setImg(`data:image/jpeg;base64,${userData.profile}`);
            setUserData(userData);

            // Fetch course data without including a body in the GET request
            const response1 = await fetch(`http://localhost:8080/course/assignList?email=${userData.email}`, {
                method: "GET",
                headers: {
                    'Authorization': token
                }
            });
            const data = await response1.json();
            // Add a 'selected' property to each course object to track selection
            setCourses(data);
        } catch (error) {
            console.error('Error fetching user data:', error);
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
    console.log('Selected Courses:', selected);

    try {
        const response = await fetch(`http://localhost:8080/AssignCourse/${userId}/courses`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token, // Don't forget to add the Authorization header
            },
            body: JSON.stringify(selected.map(course => course.courseId)) // Send only the courseIds in the body
        });

        const data = await response.json(); // Parse the response as JSON

        if (response.ok) {
            // Handle success response
            await MySwal.fire({
                icon: 'success',
                title: 'Courses Assigned!',
                text: data.message // Accessing the message from the parsed data
            });
            // Redirect after success
            window.location.href = "/view/Students";
        } else {
            // Handle error response
            await MySwal.fire({
                icon: 'error',
                title: 'An error occurred!',
                text: data.error // Accessing the error from the parsed data
            });
            window.location.reload()
        }
    } catch (error) {
        console.error('Error:', error);
        await MySwal.fire({
            icon: 'error',
            title: 'An unexpected error occurred!',
            text: error.message // Show error message
        });
        window.location.reload()
    }
};

  return (
    <div>
      <div className='contentbackground'>
        <div className='contentinner'>
          <h3>Assign Course</h3>
          <div className='mainform'>
            <div className='profile-picture'>
              <div className='image-group'>
                <img id="preview" src={img} alt='profile' />
              </div>
              <div className=" mt-2 p-3 rounded" style={{ backgroundColor: "#F2E1F5", height: "100px" }}>
                <div className='inputgrp '>
                  <label htmlFor='Name'> Name</label>
                  <span>:</span>
                  <label>
                    {userData.username}</label>
                </div>
                <div className='inputgrp'>
                  <label htmlFor='Name'> Email</label>
                  <span>:</span>
                  <label>
                    {userData.email}</label>
                </div>
              </div>
            </div>
  {courses.length <= 0 ? (
  <div className='formgroup rounded p-5 ' style={{ backgroundColor: '#F2E1F5' }}>
  <h1 className='text-center' style={{marginTop:"130px"}}>No courses found  to assign</h1>
  </div>
) : (
  <div className='formgroup rounded p-5' style={{ backgroundColor: '#F2E1F5', display: 'grid', gridTemplateColumns: "1fr 1fr 1fr 1fr" }}>
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
