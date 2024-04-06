import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

const LessonList = () => {
    const{courseName,courseId}=useParams();
    const [lessons,setlessons]=useState([]);
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
  return (
    <div className='contentbackground'>
    <div className='contentinner'>
        {lessons.length>0 ?(
        <div className='twodiv'>
         <div style={{display:"grid",gridTemplateColumns:"9fr 1fr"}}>
        <h2 style={{textDecoration:"underline"}}>Lessons of {courseName}</h2>
        <Link to={`/uploadvideo/${courseName}/${courseId}`} className='btn btn-primary' style={{height:"40px"}}> 
                 <i className="fas fa-plus"></i> Add Lesson</Link>
        </div>
        <div className='scrolldiv'>
        <div className='listback'>       
            <div >
            {lessons.map((lesson, index) => (
              <div key={lesson.lessonId} className='listbackinner'>
                <span>{index+1} .</span>
                <span>{lesson.Lessontitle}</span>
                <span> <i className="fas fa-edit text-primary"></i></span>
                <span>
                    <i className="fas fa-trash text-danger"></i></span>
              </div>
             
            ))}
          </div>
          </div></div>
 
        </div>):(

        <div className='enroll'>
          <h3 className='mt-4'>No Lessons Found for {courseName}</h3>
          <Link to={`/uploadvideo/${courseName}/${courseId}`} className='btn btn-primary'>Add Now</Link>
        </div>)}
        </div>
        </div>
  )
}

export default LessonList
