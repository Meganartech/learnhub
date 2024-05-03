import React, { useEffect, useState } from 'react'
import arrowpic from "../../images/arrowpic.jpeg"
import pencilpic from "../../images/pencilpic.jpeg"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useNavigate } from 'react-router-dom';
const Dashboard = () => {
  
  const navigate = useNavigate();
  const MySwal = withReactContent(Swal);
  const token=sessionStorage.getItem("token")
  const[Courses,setCourses]=useState([]);
  const [countdetails,setcountdetails]=useState({
    coursecount:"",
    trainercount:"",
    usercount:"",
    availableseats:""
  });
  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const response = await fetch('http://localhost:8080/course/countcourse', {
          method: 'GET',
          headers: {
            "Authorization": token
          }
        });
        
        if (response.ok) {
          const data = await response.json();
          setcountdetails(data);
        } else if (response.status === 401) { 
          window.location.href = '/unauthorized'; // Redirect to unauthorized page
          return; // Stop further execution
        } else {
          await MySwal.fire({
            icon: 'error',
            title: 'Some Error Occurred',
            text: 'Please Try Again Later',
            confirmButtonText: "OK",
          }).then( (result) => {
            if (result.isConfirmed) {
               navigate(-1); // Navigate back after user confirmation
            }
          });
        
        }
        
      } catch (error) {
        MySwal.fire({
          icon: 'error',
          title: 'Some Error Occurred',
          text: error.message
        });
      }
    }
    const fetchpopularcourse=async ()=>{
      try {
        const response = await fetch('http://localhost:8080/courseControl/popularCourse', {
          method: 'GET',
          headers: {
            "Authorization": token
          }
        });
        
        if (response.ok) {
          const data = await response.json();
          setCourses(data)
        } else if (response.status === 401) { 
          window.location.href = '/unauthorized'; // Redirect to unauthorized page
          return; // Stop further execution
        } else {
          await MySwal.fire({
            icon: 'error',
            title: 'Some Error Occurred',
            text: 'Please Try Again Later',
            confirmButtonText: "OK",
          }).then( (result) => {
            if (result.isConfirmed) {
               navigate(-1); // Navigate back after user confirmation
            }
          });
        
        }
        
      } catch (error) {
        MySwal.fire({
          icon: 'error',
          title: 'Some Error Occurred',
          text: error.message
        });
      }
    }
    fetchCounts();
    fetchpopularcourse();
  }, []);
  


  
  return (
    <div className='contentbackground'>
        <div className='contentinner'>
          <div style={{display:"flex"}}>
            <div style={{flex:"3"}}>
                    <div className='counts '>
                      <div className='countchild '>
                        <p>Available seats</p>
                        <div className=' rounded-circle circleimg' ><h1> <i className="fa-solid fa-wheelchair mt-2"></i></h1></div>
                        <p>{countdetails.availableseats} seats</p>
                      </div>
                     
                      <div className='countchild'>
                        <p>Total Courses</p>
                        <div className=' rounded-circle circleimg' ><h1> <i className="fa-solid fa-chart-line mt-2"></i></h1></div>
                        <p>{countdetails.coursecount} courses</p>
                      </div>
                      <div className='countchild'>
                        <p>Total Students</p>
                        <div className=' rounded-circle circleimg' ><h1> <i className="fa-solid fa-person-walking mt-2"></i></h1></div>
                        <p>{countdetails.usercount} students</p>
                      </div>
                      <div className='countchild'>
                        <p>Total Trainers</p>
                        <div className=' rounded-circle circleimg' ><h1> <i className="fa-solid fa-person-chalkboard mt-2"></i></h1></div>
                        <p>{countdetails.trainercount} Trainers</p>
                        </div>
                  </div>
                  
              
                  <div className='counts '>
                    
                   
                    {Courses.map((course, index) => (
                          <div key={index} className='countchild' style={{padding:"5px"}}>
                            <img
                            style={{width:"140px",height:"100px", marginBottom:"10px", borderRadius:"5px"}}
                            src={`data:image/jpeg;base64,${course.courseImage}`}
                            alt="Course"
                          />
                            <a href={course.courseUrl} >
                            {course.courseName.length > 10 ? course.courseName.slice(0, 10) + "..." : course.courseName}
                            </a>
                            </div>
                        ))}
                
                    


                    </div>
                  
              </div>
                    
            <div style={{flex:"1"}}>
                    <div>
                      <img src={arrowpic}
                      alt='arrowpic'></img>
                    </div>
                    <div>
                      <img src={pencilpic}
                      alt='pencilpic'></img>
                    </div>
            </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
