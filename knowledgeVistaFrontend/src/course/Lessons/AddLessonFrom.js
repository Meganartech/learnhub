import { useParams } from "react-router-dom";
import React, { useState, useEffect } from "react";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import InnerNavigationBar from "./InnerNavigationBar";
import CourseDetails from "./CourseDetails";
import AddLesson from "./AddLesson";
import Lesson from "./Lesson";

import CreateTest from "../Test/CreateTest";
import TestList from "../Test/TestList";

const AddLessonFrom = () => {
  const [courseEdit, setCourseEdit] = useState([]);
  const [img, setimg] = useState();
  const { courseId } = useParams();
  const MySwal = withReactContent(Swal);
  const [component, setComponent] = useState("Lesson");
  const handleLinkClick = (link) => {
    setComponent(link);
  };
  useEffect(() => {
    const fetchcourse = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/course/get/${courseId}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json", // Set appropriate headers if needed
            },
          }
        );
        if (!response.ok) {
          // If response is not successful (HTTP status code not in the range 200-299)
          MySwal.fire({
            icon: "error",
            title: "HTTP Error!",
            text: `HTTP error! Status: ${response.status}`,
          });
        }
        const data = await response.json(); // Convert response to JSON format
        setimg(`data:image/jpeg;base64,${data.courseImage}`);
        setCourseEdit(data);
      } catch (error) {
        MySwal.fire({
          title: "Error!",
          text: "An error occurred while Fetching course. Please try again later.",
          icon: "error",
          confirmButtonText: "OK",
        });
      }
    };
    fetchcourse();
  }, [courseId]);
  return (
    <div>
      <InnerNavigationBar
        courseName={courseEdit.courseName}
      
        courseId={courseEdit.courseId}
        handleLinkClick={handleLinkClick}
      />
      {/* {component === "courseDetails" && (
        <CourseDetails
       
          courseName={courseEdit.courseName}
          courseCategory={courseEdit.courseCategory}
          courseDescription={courseEdit.courseDescription}
          courseImage={img}
          courseId={courseEdit.courseId}
        
        />
      )} */}

      {component === "AddLesson" && <AddLesson courseId={courseId} />}
      {component === "Lesson" && <Lesson courseId={courseId} courseName={courseEdit.courseName}/>}
      {component === "createTest" && <CreateTest courseId={courseId} />}
      {component==="test"&& <TestList courseId={courseId} />}
      {/* {component === "settings" && <CreateTest courseId={courseId} />} */}
    </div>
  );
};

export default AddLessonFrom;
