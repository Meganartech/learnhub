import React, { useState, useEffect } from 'react';
import styles from "../css/CourseView.module.css";
import { Link } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';


const MyAssignedcourses = () => {
    const MySwal = withReactContent(Swal);
    const role=sessionStorage.getItem("role");
    
    const token = sessionStorage.getItem("token");
    const [courses, setCourses] = useState([]);

    useEffect(() => {
        const fetchItems = async () => {
            try {
                // Replace {userId} with the actual user ID
                const response = await axios.get(`${baseUrl}/AssignCourse/Trainer/courselist`, {
                 headers:{
                    'Authorization':token
                }
            });
                if (!response.status===200) {
                    throw new Error('Failed to fetch courses');
                }
                const data = response.data;
                setCourses(data);
            } catch (error) {
                console.error('Error fetching courses:', error);
                // Handle error here, for example, show an error message
            }
        };

        fetchItems();
    }, []);
    const handleDelete = (e, courseId) => {
     e.preventDefault();
     MySwal.fire({
       title: "Delete Course?",
       text: "Are you sure you want to delete this course?",
       icon: "warning",
       showCancelButton: true,
       confirmButtonColor: "#d33",
       confirmButtonText: "Delete",
       cancelButtonText: "Cancel",
     }).then((result) => {
       if (result.isConfirmed) {
         // If the user clicked "Delete"
         axios.delete(`${baseUrl}/course/${courseId}`, {
           headers: {
             "Content-Type": "application/json",
           },
         })
           .then((response) => {
             if (response.status===200) {
               // Handle successful deletion
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
             MySwal.fire({
               title: "Error!",
               text:
                 "An error occurred while Deleting courses. Please try again later.",
               icon: "error",
               confirmButtonText: "OK",
             });
           });
       } else {
         // If the user clicked "Cancel" or closed the dialog
         MySwal.fire("Cancelled", "Your course is safe :)", "info");
       }
     });
   };
  return (
    
    <div className='contentbackground'>
    <div className='contentinner'>
<div className={styles.supercontainer}>
<div className={styles.createbtn}>  
{(role === "ADMIN" || role==="TRAINER") && (    
     <a href="/course/Trainer/addcourse">
       <button type="button" className="btn btn-primary mt-4">
       <i className="fa-solid fa-plus"></i>  Create Course
       </button>
     </a> 
   )}</div>

<ul className={styles.maincontainer}>
    {courses.length === 0 ? (
        <li>
             <div className='enroll pb-5'>
            <h3 className='mt-5'>No courses Found </h3>
           </div>
            </li>
    ) : (
        <>
            {courses.map((item) => (
               <li key={item.courseId}>
               <div className={styles.containers}>
                 <div className={styles.imagediv}>
                   <img
                     src={`data:image/jpeg;base64,${item.courseImage}`}
                     alt="Course"
                   />
                 </div>
                 <div className={styles.content}>
                   <div className={styles.editicons}>
                     {" "}
                     <h4>
                       <a href={item.courseUrl}>{item.courseName.length > 10 ? item.courseName.slice(0, 10) + "..." : item.courseName}</a>{" "}
                     </h4>
                  <h5 className="dropdown no-arrow">
                   <a className="dropdown-toggle" 
                   href="#"
                   id="userDropdown"
                    role="button"
                    data-toggle="dropdown"
                    aria-haspopup="true"
                    aria-expanded="false">
                  <i className="fa-solid fa-plus"></i></a>
                  <div
                 className="dropdown-menu dropdown-menu-left shadow animated--grow-in"
                 aria-labelledby="userDropdown"
                       >
                   <Link to={`/lessonList/${item.courseName}/${item.courseId}`}
                     className="dropdown-item"
                     data-toggle="modal"
                     data-target="#logoutModal"   
                   >
                    Lessons
                   </Link>
                   <div className="dropdown-divider"></div>
                   <Link to={`/course/testlist/${item.courseId}`}
                     className="dropdown-item"
                     data-toggle="modal"
                     data-target="#logoutModal"   
                   >
                    Test
                   </Link>

                 </div>
                     </h5>
                     <h5>
                        <Link to={`/course/edit/${item.courseId}`}>
                         <i className="fa-solid fa-edit"></i>
                       </Link>
                     </h5>
                     <h5>
                       <a
                         href="#"
                         onClick={(e) => handleDelete(e, item.courseId)}
                       >
                         <i className="fas fa-trash "></i>
                       </a>
                     </h5>
                   </div>

                   <p> {item.courseDescription.length > 20
                       ? item.courseDescription.slice(0, 20) + "..."
                       : item.courseDescription}</p>
             
                   <h6>{item.amount === 0 ? <a href={item.courseUrl} className=" btn btn-outline-success w-100"> Free</a> : 
                   <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr' }}>
                     <div><i className="fa-solid fa-indian-rupee-sign"></i><label className="mt-3 blockquote">{item.amount}</label>
                     </div>

       
       
                   </div>}</h6>
                 </div>
               </div>
             </li>
            ))}
        </>
    )}
</ul>
</div>
</div>
</div>

  )
}

export default MyAssignedcourses
