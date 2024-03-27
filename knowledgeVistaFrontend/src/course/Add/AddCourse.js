import React from "react";
import { useState } from "react";
import "../../OrganizationSettings/CreateApplication.css"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import "../Lessons/Style.css";
const AddCourse = () => {
  const MySwal = withReactContent(Swal);
  const [formData, setFormData] = useState({
    courseName: "",
    courseDescription: "",
    courseCategory: "",
    courseAmount:"",
    courseImage: null,
    base64Image: null,
  });
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };
  const convertImageToBase64 = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });
  };
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    // Update formData with the new file
    setFormData((prevFormData) => ({ ...prevFormData, courseImage: file }));
    // Check if base64Image is not already set
    if (!formData.base64Image) {
      // Convert the file to base64
      convertImageToBase64(file)
        .then((base64Data) => {
          // Set the base64 encoded image in the state
          setFormData((prevFormData) => ({
            ...prevFormData,
            base64Image: base64Data,
          }));
        })
        .catch((error) => {
          console.error("Error converting image to base64:", error);
        });
    }
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(formData);
    const formDataToSend = new FormData();
    formDataToSend.append("courseName", formData.courseName);
    formDataToSend.append("courseDescription", formData.courseDescription);
    formDataToSend.append("courseCategory", formData.courseCategory);
    formDataToSend.append("courseAmount",formData.courseAmount)
    formDataToSend.append("courseImage", formData.courseImage);

    try {
      const response = await fetch("http://localhost:8080/course/add", {
        method: "POST",
        body: formDataToSend,
      });
      console.log(formDataToSend);
      await response.json();

      if (response.ok) {
        MySwal.fire({
          title: "Course Created",
          text: "Your course have created successfully!",
          icon: "success",
        }).then((result) => {
          if (result.isConfirmed) {
           
            window.location.href = "/course/admin/edit";
          }
        });
      }
    } catch (error) {
      // Handle network errors or other exceptions
      MySwal.fire({
        title: "Error!",
        text: "Some Unexpected Error occured . Please try again later.",
        icon: "error",
        confirmButtonText: "OK",
      });
    }
  };
  const userRole = sessionStorage.getItem('role'); 

  return (
    <>
    {userRole === "ADMIN"&&(
    <div className="contentbackground ">
      <form onSubmit={handleSubmit}>
        <div className="contentinner">
        <div className="outer1 " >
          <div className="First1">
            <h2 className="heading">Add Course</h2>
            <div className="form-group">
              <label htmlFor="courseName" >
                Name<span className="text-danger">*</span>
              </label>
              <input
                type="text"
                name="courseName"
                id="courseName"
                className="form-control "
                placeholder="Enter the Course Name"
                value={formData.courseName}
                onChange={handleChange}
                required
                autoFocus
              />
            </div>
            <div className="form-group">
              <label htmlFor="courseDescription">
                Description<span className="text-danger">*</span>
              </label>
              <textarea
                name="courseDescription"
                id="courseDescription"
                rows={6}
                className="form-control "
                placeholder="Description about the Course"
                value={formData.courseDescription}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="courseCategory" >
                Category<span className="text-danger">*</span>
              </label>
              <input
                type="text"
                name="courseCategory"
                id="courseCategory"
                className="form-control "
                placeholder="Category"
                value={formData.courseCategory}
                onChange={handleChange}
                required
              />
            </div>
          </div>
          <div className="vertical-line"></div>
          <div className="second1">
            <div className="form-group mt-5">
              <label htmlFor="courseImage" >
                Course Image <span className="text-danger">*</span>
              </label>
              <input
                type="file"
                name="courseImage"
                id="courseImage"
                accept="image/jpeg, image/png, image/gif"
                className="form-control-file"
                onChange={handleFileChange}
                required
              />
            </div>
            {formData.base64Image && (
              <img
                src={formData.base64Image}
                alt="Selected "
                style={{ maxWidth: "100%", maxHeight: "200px" }}
              />
            )}
            <div>
            <div className="form-group">
              <label htmlFor="courseAmount" >
                Amount <span className="text-danger">*</span>
              </label>
              <input
                type="number"
                name="courseAmount"
                id="courseAmount"
                className="form-control "
                placeholder="Amount"
                value={formData.courseAmount}
                onChange={handleChange}
                required
              />
            </div>
              <button type="submit" className="btn btn-primary w-100">
                Create Course
              </button>
            </div>
          </div>
        </div>
        </div>
      </form>
    </div>)}</>
  );
};

export default AddCourse;
