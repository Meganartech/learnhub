import React, {  useState } from "react";
import "./Style.css";
import AddNotes from "./AddNotes";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useParams } from "react-router-dom";

const AddLesson = () => {
  const {courseId}=useParams();
  const{courseName}=useParams();
  const MySwal = withReactContent(Swal);

  const [notesField, setNotesField] = useState({
    notesTitle: "",
    notesDesc: "",
    fileUrl: "",
    videoFile: null,
  });

  const [lesson, setLesson] = useState({
    lessonTitle: "",
    lessonDesc: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setLesson({ ...lesson, [name]: value });
  };

  const handleNotesChange = (e) => {
    const { name, value } = e.target;
    setNotesField({ ...notesField, [name]: value });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setNotesField({ ...notesField, videoFile: file, fileUrl: "" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevents the default form submission behavior

    try {
      // Combine lesson and notes into one object
      const formData = new FormData();
      formData.append("lessonTitle", lesson.lessonTitle);
      formData.append("lessonDesc", lesson.lessonDesc);
      formData.append("notesTitle", notesField.notesTitle);
      formData.append("notesDesc", notesField.notesDesc);
    
      if (notesField.videoFile) {
        formData.append("videoFile", notesField.videoFile);
      }else{
        formData.append("fileUrl", notesField.fileUrl);
      }

      const response = await fetch(`http://localhost:8080/Lesson/save/${courseId}`, {
        method: "POST",
        body: formData,
      });

      if (response.ok) {
        // Operation was successful
        const data = await response.json();
        MySwal.fire({
          title: "Success!",
          text: data.message, // Assuming the message is returned from the backend
          icon: "success",
          confirmButtonText: "OK",
        }).then((result) => {
          // Clear all input fields if the user clicks OK
          if (result.isConfirmed) {
            setLesson({
              lessonTitle: "",
              lessonDesc: "",
            });
            setNotesField({
              notesTitle: "",
              notesDesc: "",
              fileUrl: "",
              videoFile: null,
            });
            window.location.href = `/course/viewlessons/${courseName}/${courseId}`;
          }
        });
      }
    } catch (error) {
      // Network error or other exception occurred
      console.error("Exception:", error); // Log the error for debugging
      // Display an error message to the user
      MySwal.fire({
        title: "Error!",
        text: "An error occurred while registering. Please try again later.",
        icon: "error",
        confirmButtonText: "OK",
      });
    }
  };

  return (
    <div className="contentbackground">
      <form onSubmit={handleSubmit}>
      <div className="contentinner">
        <div className="outer">
          <div className="first">
            <h2 className="heading">{courseName}</h2>
            <div className="mb-3">
              <label htmlFor="lessonTitle" className=" mt-3">
                <h5>Lesson Title <span className="text-danger">*</span></h5>
              </label>
              <input
                type="text"
                autoFocus
                name="lessonTitle"
                value={lesson.lessonTitle}
                className="form-control"
                id="lessonTitle"
                placeholder="Lesson Name"
                onChange={handleChange}
             
              />
            </div>
            <div className="mb-3">
              <label htmlFor="lessonDesc" >
                <h5> Lesson Description <span className="text-danger">*</span></h5>
              </label>
              <textarea
                className="form-control"
                name="lessonDesc"
                value={lesson.lessonDesc}
                id="lessonDesc"
                rows="6"
                onChange={handleChange}
                placeholder="Give a brief description about the Lesson..."
              ></textarea>
            </div>
          </div>
          <div className="vertical-line" ></div>

          <div className="second">
                      <AddNotes
              notesField={notesField}
              handleNotesChange={handleNotesChange}
              handleFileChange={handleFileChange}
            />

          </div>
          <div className="submitbtn text-center mb-4">
          <button type="submit" className="btn btn-primary btn-lg">
            Submit
          </button>
          <button type="reset" className="btn btn-warning btn-lg">
            Cancel
          </button>
        </div>
        </div>

        </div>
      </form>
    </div>
  );
};

export default AddLesson;
