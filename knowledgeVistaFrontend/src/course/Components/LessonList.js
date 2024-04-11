import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import {  toast } from 'react-toastify';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const LessonList = () => {
  
  const MySwal = withReactContent(Swal);
    const{courseName,courseId}=useParams();
    const [lessons,setlessons]=useState([]);
    
  const token=sessionStorage.getItem("token");
    useEffect(() => {
        const fetchData = async () => {
          try {
            const response = await fetch(`http://localhost:8080/course/getLessonlist/${courseId}`);
            if (!response.ok) {
              throw new Error('Failed to fetch data');
            }
            const lessonList = await response.json();
           setlessons(lessonList);
          } catch (error) {
            console.error('Error fetching data:', error);
          }
        };
    
        fetchData();
      }, [courseId]);

      const deletelesson = async (Lesstitle, lessId) => {
        const formData = new FormData();
        formData.append("lessonId", lessId);
        formData.append("Lessontitle", Lesstitle);
        MySwal.fire({
          title: "Delete Lesson?",
          text: `Are you sure you want to delete Lesson ${Lesstitle}`,
          icon: "warning",
          showCancelButton: true,
          confirmButtonColor: "#d33",
          confirmButtonText: "Delete",
          cancelButtonText: "Cancel",
        }).then(async (result) => {
          if (result.isConfirmed) {
            if (lessId != null) {
              try {
                const response = await fetch("http://localhost:8080/lessons/delete", {
                  method: "DELETE",
                  headers: {
                    'Authorization': token,
                  },
                  body: formData,
                });
      
                const message = await response.json();
                if (response.ok) {
                  toast.success(`${message.message}`, {
                    autoClose: 3000, // Close the toast after 3 seconds
                    onClose: () => {
                      // After the toast is closed, reload the page
                      window.location.reload();
                    },
                  });
                } else {
                  toast.error(`${message.message}`);
                }
              } catch (error) {
                // Handle network errors or other exceptions
                toast.error("An error occurred while deleting the lesson.");
              }
            }
          }
        }).catch(error => {
          toast.error("An unexpected error occurred while deleting the lesson.");
        });
      };
      
      
  return (
    <div className='contentbackground'>
    <div className='contentinner'>
        {lessons.length>0 ?(
        <div className='twodiv'>
         <div style={{display:"grid",gridTemplateColumns:"9fr 1fr"}}>
        <h2 style={{textDecoration:"underline"}}>Lessons of {courseName}</h2>
        <Link to={`/course/Addlesson/${courseName}/${courseId}`} className='btn btn-primary' style={{height:"40px"}}> 
                 <i className="fas fa-plus"></i> Add Lesson</Link>
        </div>
        <div className='scrolldiv'>
        <div className='listback'>       
            <div >
            {lessons.map((lesson, index) => (
              <div key={lesson.lessonId} className='listbackinner'>
                <span>{index+1} .</span>
                <Link to={`/edit/${courseName}/${lesson.lessonId}`}>{lesson.Lessontitle}</Link>
               
                <Link to={`/edit/${courseName}/${courseId}/${lesson.Lessontitle}/${lesson.lessonId}`}> <i className="fas fa-edit text-primary"></i></Link>
                <span>
                    <i className="fas fa-trash text-danger" onClick={()=>deletelesson(lesson.Lessontitle,lesson.lessonId)}></i></span>
              </div>
             
            ))}
          </div>
          </div></div>
 
        </div>):(

        <div className='enroll'>
          <h3 className='mt-4'>No Lessons Found for {courseName}</h3>
          <Link to={`/course/Addlesson/${courseName}/${courseId}`} className='btn btn-primary'>Add Now</Link>
        </div>)}
        </div>
        </div>
  )
}

export default LessonList
