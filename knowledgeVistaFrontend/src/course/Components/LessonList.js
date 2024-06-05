import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../../api/utils';
import axios from 'axios';

const LessonList = () => {
  
  const MySwal = withReactContent(Swal);
    const{courseName,courseId}=useParams();
    const [lessons,setlessons]=useState([]);
    
  const token=sessionStorage.getItem("token");
    useEffect(() => {
        const fetchData = async () => {
          try {
            const response = await axios.get(`${baseUrl}/course/getLessonlist/${courseId}`, {
              headers: {
                Authorization: token,
               }
            });
            if (!response.status===200) {
              throw new Error('Failed to fetch data');
            }
            const lessonList = response.data;
           setlessons(lessonList);
          } catch (error) {
            if(error.response && error.response.status===401)
            {
              window.location.href="/unauthorized";
            }else{
              MySwal.fire({
                title: "Error!",
                text: error.response.data,
                icon: "error",
                confirmButtonText: "OK",
              });
            }          }
        };
    
        fetchData();
      }, [courseId]);

      const deletelesson = async (Lesstitle, lessId) => {
        const formData = new FormData();
        formData.append("lessonId", lessId);
        formData.append("Lessontitle", Lesstitle);
    
        MySwal.fire({
            title: "Delete Lesson?",
            text: `Are you sure you want to delete Lesson ${Lesstitle}?`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            confirmButtonText: "Delete",
            cancelButtonText: "Cancel",
        }).then(async (result) => {
            if (result.isConfirmed && lessId != null) {
                try {
                    const response = await axios.delete(`${baseUrl}/lessons/delete`, {
                        headers: {
                            Authorization: token
                           },
                        data: formData,
                    });
    
                    const message =  response.data;
    
                    if (response.status===200) {
                        MySwal.fire({
                            title: "Deleted Successfully",
                            text: `Lesson ${Lesstitle} was deleted successfully.`,
                            icon: "success",
                            confirmButtonText: "OK",
                        }).then(() => {
                            // After the modal is closed, reload the page
                            window.location.reload();
                        });
                    }
                } catch (error) {
                  if(error.response && error.response.status===401){
                    MySwal.fire({
                      title: "Deletion Failed",
                      text: "you are UnAuthorized to delete this lesson",
                      icon: "error",
                      confirmButtonText: "OK",
                  });
                  }else{
                    MySwal.fire({
                        title: "Error",
                        text: "An error occurred while deleting the lesson.",
                        icon: "error",
                        confirmButtonText: "OK",
                    });
                }
              }
            }
        }).catch(error => {
            MySwal.fire({
                title: "Unexpected Error",
                text: "An unexpected error occurred while deleting the lesson.",
                icon: "error",
                confirmButtonText: "OK",
            });
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
                 <i className="fas fa-plus"></i> Add </Link>
        </div>
        <div className='scrolldiv'>
        <div className='listback'>       
            <div >
            {lessons.map((lesson, index) => (
              <div key={lesson.lessonId} className='listbackinner'>
                <span>{index+1} .</span>
                <Link to= {`/courses/${courseName}/${courseId}/${lesson.lessonId}`}>{lesson.Lessontitle}</Link>
               
                <Link to={`/edit/${courseName}/${courseId}/${lesson.Lessontitle}/${lesson.lessonId}`}> <i className="fas fa-edit text-primary"></i></Link>
                <span>
                    <i className="fas fa-trash text-danger" onClick={()=>deletelesson(lesson.Lessontitle,lesson.lessonId)}></i></span>
              </div>
             
            ))}
          </div>
          </div></div>
 
        </div>):(

        <div className='enroll' >
          <h3 className='mt-4'>No Lessons Found for {courseName}</h3>
          <Link to={`/course/Addlesson/${courseName}/${courseId}`} className='btn btn-primary'>Add Now</Link>
        </div>)}
        </div>
        </div>
  )
}

export default LessonList
