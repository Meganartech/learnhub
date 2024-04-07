
//import styles from "../course/css/CourseView.module.css";
import React, { useState, useEffect } from 'react';
import styles from "../css/CourseView.module.css";
import { Link } from 'react-router-dom';

const Mycourse = () => {
    const userId = sessionStorage.getItem("userid");
    const [courses, setCourses] = useState([]);

    useEffect(() => {
        const fetchItems = async () => {
            try {
                // Replace {userId} with the actual user ID
                const response = await fetch(`http://localhost:8080/AssignCourse/${userId}/courselist`);
                if (!response.ok) {
                    throw new Error('Failed to fetch courses');
                }
                const data = await response.json();
                setCourses(data);
            } catch (error) {
                console.error('Error fetching courses:', error);
                // Handle error here, for example, show an error message
            }
        };

        fetchItems();
    }, []);

    return (
        <div className='contentbackground'>
        <div className='contentinner'>
<div className={styles.supercontainer}>
    <div className={styles.createbtn}></div>

    <ul className={styles.maincontainer}>
        {courses.length === 0 ? (
            <li>
                 <div className='enroll'>
                <h3 className='mt-4'>No courses Enrolled </h3>
                <Link to="/dashboard/course" className='btn btn-primary'>Enroll Now</Link></div>
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
            </>
        )}
    </ul>
</div>
</div>
</div>
    
    );
};

export default Mycourse;
