
import React, { useContext, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';
import { GlobalStateContext } from '../Context/GlobalStateProvider';
const AssignCourse = () => {
 const { displayname } = useContext(GlobalStateContext);
  const { userId } = useParams()
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
          

          <div className='batchView '>

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
