import React, {  useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../../api/utils';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const CourseCreation = () => {
    const token = sessionStorage.getItem("token");
    const MySwal = withReactContent(Swal);
    const navigate =useNavigate();
    // Initial state for form errors
    const [errors, setErrors] = useState({
        courseName: "",
        courseDescription: "",
        courseCategory: "",
        courseAmount: "",
        Duration: "",
        Noofseats: "",
        courseImage: "",
    });

    // Initial state for form data
    const [formData, setFormData] = useState({
        courseName: "",
        courseDescription: "",
        courseCategory: "",
        courseAmount: "",
        Duration: "",
        Noofseats: "",
        courseImage: null,
        base64Image: null,
    });

    // Handle changes to form inputs
    const handleChange = (e) => {
        const { name, value } = e.target;
        let error = '';

        // Parse value to a number where necessary
        const numericValue = name === 'courseAmount' || name === 'Duration' || name === 'Noofseats' ? parseFloat(value) : value;

        switch (name) {
            case 'courseName':
                error = value.length < 1 ? 'Please enter a Course Title' : '';
                break;
            case 'courseCategory':
                error = value.length < 1 ? 'Please enter a Course Category' : '';
                break;
            case 'courseDescription':
                error = value.length < 1 ? 'Please enter a Course Description' : '';
                break;
            case 'courseAmount':
                error = numericValue < 0 ? 'Course amount must be greater than or equal to 0' : '';
                break;
            case 'Duration':
                error = numericValue < 1 ? 'Duration must be greater than 0' : '';
                break;
            case 'Noofseats':
                error = numericValue < 1 ? 'Number of seats must be greater than 0' : '';
                break;
            default:
                break;
        }

        // Update errors state
        setErrors((prevErrors) => ({
            ...prevErrors,
            [name]: error,
        }));

        // Update form data state
        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
    };

    // Convert image file to base64
    const convertImageToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result);
            reader.onerror = (error) => reject(error);
        });
    };

    // Handle changes to the file input
    const handleFileChange = (e) => {
      const file = e.target.files[0];
  
      // Define allowed MIME types for images
      const allowedImageTypes = ['image/jpeg', 'image/png'];
  
      // Check file type (MIME type)
      if (!allowedImageTypes.includes(file.type)) {
          setErrors((prevErrors) => ({
              ...prevErrors,
              courseImage: 'Please select an image of type JPG or PNG',
          }));
          return;
      }
  
      // Check file size (should be 1 MB or less)
      if (file.size > 1 * 1024 * 1024) {
          setErrors((prevErrors) => ({
              ...prevErrors,
              courseImage: 'Image size must be 1 MB or smaller',
          }));
          return;
      }
  
      // Update formData with the new file
      setFormData((prevFormData) => ({
          ...prevFormData,
          courseImage: file,
      }));
  
      // Convert the file to base64
      convertImageToBase64(file)
          .then((base64Data) => {
              // Set the base64 encoded image in the state
              setFormData((prevFormData) => ({
                  ...prevFormData,
                  base64Image: base64Data,
              }));
              setErrors((prevErrors) => ({
                  ...prevErrors,
                  courseImage: "",
              }));
          })
          .catch((error) => {
              console.error("Error converting image to base64:", error);
              setErrors((prevErrors) => ({
                  ...prevErrors,
                  courseImage: 'Error converting image to base64',
              }));
          });
  };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Create a FormData object to send the form data
        const formDataToSend = new FormData();
        formDataToSend.append("courseName", formData.courseName);
        formDataToSend.append("courseDescription", formData.courseDescription);
        formDataToSend.append("courseCategory", formData.courseCategory);
        formDataToSend.append("courseAmount", formData.courseAmount);
        formDataToSend.append("courseImage", formData.courseImage);
        formDataToSend.append("Trainer", formData.Trainer);
        formDataToSend.append("Duration", formData.Duration);
        formDataToSend.append("Noofseats", formData.Noofseats);

        // Send the form data
        try {
            const response = await axios.post(`${baseUrl}/course/add`,formDataToSend,{
            headers:{
                Authorization:token
            }
            });

            if (response.status===200) {
                const reply = response.data;
                const { message, courseId, coursename } = reply;
                window.location.href = `/course/Addlesson/${coursename}/${courseId}`;
            } 
        } catch (error) {
            if(error.response && error.response.status===401){
                MySwal.fire({
                    title: "Error",
                    text: "you are unauthorized to access this page",
                    icon: "error",
                    confirmButtonText: "OK"
                }).then((result)=>{
                    if(result.isConfirmed){
                        navigate(-1)
                    }
                })
            }else{
            MySwal.fire({
                title: "Error!",
                text: "Some unexpected error occurred. Please try again later.",
                icon: "error",
                confirmButtonText: "OK",
            });
        }
        }
    };

    // Calculate if the form can be submitted
    const canSubmit = () => {
        // Check if all form data fields are filled and there are no errors
        const hasNoErrors = Object.values(errors).every((error) => error === '');
        const hasNoEmptyFields = Object.values(formData).every((value) => value !== '' && value !== null);
        
        return hasNoErrors && hasNoEmptyFields;
    };

    return (
        <div className='contentbackground'>
            <div className='contentinner'>
                <div className='divider ml-2'>
                    <h1 style={{ textDecoration: "underline" }}>Setting up a Course</h1>
                    <form onSubmit={handleSubmit}>
                        {/* Form fields */}
                        <div className='formgroup mt-2' style={{ fontSize: "larger" }}>
                            {/* Course Title */}
                            <div className='inputgrp'>
                                <label htmlFor='courseName'>Course Title <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="text"
                                        style={{ width: "400px" }}
                                        id='courseName'
                                        name="courseName"
                                        value={formData.courseName}
                                        className={`form-control form-control-lg mt-2 ${errors.courseName && 'is-invalid'}`}
                                        placeholder="Course Title"
                                        onChange={handleChange}
                                        autoFocus
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.courseName}
                                    </div>
                                </div>
                            </div>
                            
                            {/* Course Description */}
                            <div className='inputgrp'>
                                <label htmlFor='courseDescription'>Course Description <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <textarea
                                        type="text"
                                        rows={3}
                                        style={{ width: "400px" }}
                                        id='courseDescription'
                                        name="courseDescription"
                                        value={formData.courseDescription}
                                        onChange={handleChange}
                                        className={`form-control form-control-lg ${errors.courseDescription && 'is-invalid'}`}
                                        placeholder="Course Description"
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.courseDescription}
                                    </div>
                                </div>
                            </div>
                            
                            {/* Course Category */}
                            <div className='inputgrp'>
                                <label htmlFor='courseCategory'>Course Category <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="text"
                                        style={{ width: "400px" }}
                                        id='courseCategory'
                                        name="courseCategory"
                                        onChange={handleChange}
                                        className={`form-control form-control-lg mt-1 ${errors.courseCategory && 'is-invalid'}`}
                                        placeholder="Course Category"
                                        value={formData.courseCategory}
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.courseCategory}
                                    </div>
                                </div>
                            </div>
                            
                            {/* Course Image */}
                            <div className='inputgrp'>
                                <label htmlFor='courseImage'>Course Image <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                <label
                                        htmlFor='courseImage'
                                        style={{ width: "400px" }}
                                        className={`file-upload-btn ${errors.courseImage && `is-invalid`}`}
                                    >
                                        Upload Image
                                    </label>
                                    <div className="invalid-feedback">
                                        {errors.courseImage}
                                    </div>
                                    <input
                                        type="file"
                                        onChange={handleFileChange}
                                        style={{ width: "400px" }}
                                        id='courseImage'
                                        name="courseImage"
                                        accept='image/*'
                                        className='file-upload'
                                    />
                                  
                                </div>
                            </div>
                            {/* Displaying Selected Image */}
                            {formData.base64Image && (
                                <div className='inputgrp'>
                                    <div></div>
                                    <div></div>
                                    <div>
                                        <img
                                            src={formData.base64Image}
                                            alt="Selected"
                                            style={{ width: "200px", height: "200px" }}
                                        />
                                    </div>
                                </div>
                            )}
                            <div className='inputgrp'>
                                <label htmlFor='Duration'>Duration (Hours) <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="number"
                                        style={{ width: "400px" }}
                                        id='Duration'
                                        name="Duration"
                                        value={formData.Duration}
                                        onChange={handleChange}
                                        className={`form-control form-control-lg mt-1 ${errors.Duration && 'is-invalid'}`}
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.Duration}
                                    </div>
                                </div>
                            </div>

                            {/* Number of Seats */}
                            <div className='inputgrp'>
                                <label htmlFor='Noofseats'>Number of Seats <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="number"
                                        style={{ width: "400px" }}
                                        id='Noofseats'
                                        name="Noofseats"
                                        className={`form-control form-control-lg mt-1 ${errors.Noofseats && 'is-invalid'}`}
                                        value={formData.Noofseats}
                                        onChange={handleChange}
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.Noofseats}
                                    </div>
                                </div>
                            </div>

                            {/* Course Amount */}
                            <div className='inputgrp'>
                                <label htmlFor='courseAmount'>Course Amount <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="number"
                                        style={{ width: "400px", marginBottom: "4px" }}
                                        id='courseAmount'
                                        name="courseAmount"
                                        value={formData.courseAmount}
                                        className={`form-control form-control-lg mt-1 ${errors.courseAmount && 'is-invalid'}`}
                                        onChange={handleChange}
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.courseAmount}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Submit and Cancel Buttons */}
                        <div className='cornerbtn'>
                            <button
                                className='btn btn-primary'
                                type="submit"
                                disabled={!canSubmit()}
                            >
                                Save
                            </button>
                            <button
                                className='btn btn-primary'
                                type="button"
                                onClick={() => {
                                    // Add cancel logic here if necessary
                                }}
                            >
                                Cancel
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default CourseCreation;
