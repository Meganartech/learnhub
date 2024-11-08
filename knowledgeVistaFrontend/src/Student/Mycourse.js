
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import baseUrl from '../api/utils';
import axios from 'axios';
import errorimg from "../images/errorimg.png"
const Mycourse = () => {
    const token = sessionStorage.getItem("token");
    const [courses, setCourses] = useState([]);
    const navigate=useNavigate();
    useEffect(() => {
        const fetchItems = async () => {
            try {
                // Replace {userId} with the actual user ID
                const response = await axios.get(`${baseUrl}/AssignCourse/student/courselist`,{
                    headers:{
                        'Authorization':token
                    }
                });
               
                const data =  response.data;
                setCourses(data);
            } catch (error) {
                console.error('Error fetching courses:', error);
                // Handle error here, for example, show an error message
                throw error
            }
        };

        fetchItems();
    }, []);

    return (
        <div className='contentbackground'>
        <div className='contentinner'>
        <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate("/dashboard/course")}}><i className="fa-solid fa-xmark"></i></div>
      </div>
        {courses.length === 0 ? (
         
                <div className='centerflex'>
                 <div className='enroll'  >
                <h3 className='mt-4'>No courses Enrolled </h3>
                <Link to="/dashboard/course" className='btn btn-primary'>Enroll Now</Link></div>
                </div>
           
        ) : (
            <>
<div className="">

    <ul className="maincontainernew" >
        
                {courses.map((item) => (
                    <li key={item.courseId}>
                        <div className="containersnew">
                            <div className="imagedivnew">
                                <img
                                    src={`data:image/jpeg;base64,${item.courseImage}`}
                                    onError={(e) => {
                                        e.target.src = errorimg; // Use the imported error image
                                      }}
                                    alt="Course"
                                />
                            </div>
                            <div className="contentnew">
                                <h4>
                                    <a href={item.courseUrl}>
                                        {item.courseName.length > 15
                                            ? item.courseName.slice(0, 15) + "..."
                                            : item.courseName}
                                    </a>
                                </h4>
                                <p>
                                    {item.courseDescription.length > 50
                                        ? item.courseDescription.slice(0, 50) + "..."
                                        : item.courseDescription}
                                </p>
                                <p>{item.courseCategory} </p>
                            </div>
                        </div>
                    </li>
                ))}
            
    </ul>
</div>
</>
        )}
</div>

</div>
    
    );
};

export default Mycourse;
