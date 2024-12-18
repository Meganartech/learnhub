import React, { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../../api/utils';
import axios from 'axios';

const LessonList = () => {
  const navigate=useNavigate();
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
          
            const lessonList = response.data;
           setlessons(lessonList);
          } catch (error) {
            if(error.response && error.response.status===401)
            {
              navigate("/unauthorized")
            }else if(error.response && error.response.status===404){
              navigate("/missing")
            }
            else{
              // MySwal.fire({
              //   title: "Error!",
              //   text: error.response.data ? error.response.data : "error occured",
              //   icon: "error",
              //   confirmButtonText: "OK",
              // });
              throw error
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
                    // MySwal.fire({
                    //     title: "Error",
                    //     text: "An error occurred while deleting the lesson.",
                    //     icon: "error",
                    //     confirmButtonText: "OK",
                    // });
                    throw error
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
    <div>
    <div className="page-header"></div>
    <div className="card">
      <div className=" card-body">
      <div className="row">
      <div className="col-12">
    <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate("/dashboard/course")}}><i className="fa-solid fa-xmark"></i></div>
      </div>
        {lessons.length>0 ?(
        <div className='twodiv'>
         <div style={{display:"grid",gridTemplateColumns:"9fr 1fr",marginBottom:"10px"}}>
        <h4>Lessons of {courseName}</h4>
        <Link to={`/course/Addlesson/${courseName}/${courseId}`} className='btn btn-primary mybtn'> 
                 <i className="fas fa-plus"></i> Add </Link>
        </div>
        
        <div className='listback'>       
            <div >
            {lessons.map((lesson, index) => (
              <div key={lesson.lessonId} className='listbackinner'>
                <span>{index+1} .</span>
                <Link className="lesson-title" to= {`/courses/${courseName}/${courseId}/${lesson.lessonId}`}>{lesson.Lessontitle}</Link>
               
                <Link to={`/edit/${courseName}/${courseId}/${lesson.Lessontitle}/${lesson.lessonId}`}> <i className="fas fa-edit text-primary"></i></Link>
                <span>
                    <i className="fas fa-trash text-danger" onClick={()=>deletelesson(lesson.Lessontitle,lesson.lessonId)}></i></span>
              </div>
             
            ))}
          </div>
          </div>
 
        </div>):(
          
<div className='centerflex'>
        <div className='enroll' >
          <h3 className='mt-4'>No Lessons Found for {courseName}</h3>
          <Link to={`/course/Addlesson/${courseName}/${courseId}`} className='btn btn-primary'>Add Now</Link>
        </div>
        </div>)}
        </div>
        </div>
        </div>
        </div>
        </div>
  )
}

export default LessonList
