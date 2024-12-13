import React, { useEffect, useState } from 'react'
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../api/utils"
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
const CreateCourseTrainer = () => {
  const token = sessionStorage.getItem("token");
  const MySwal = withReactContent(Swal);
  const navigate=useNavigate();
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
        case "courseName":
        error = value.length < 1 
            ? "Please enter a Course Title" 
            : value.length > 50 
            ? "Course Title should not exceed 50 characters" 
             :(value.includes("/") || value.includes("\\"))
            ?  "Course Title should not contain the '/' or '\' character"
            : "";
        break;
        
        case "courseCategory":
            error = value.length < 1 
            ? "Please enter a Course Category" 
            : value.length > 50 
            ? "Course Category should not exceed 50 characters" 
             :(value.includes("/") || value.includes("\\"))
            ?  "Course Title should not contain the '/' or '\' character"
            : "";
            break;
          case "courseDescription":
            error = value.length < 1 
            ? "Please enter a Course Description " : value.length > 1000
            ? "Course Description should not exceed 100 characters" 
            : "";
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
    if (file &&  !allowedImageTypes.includes(file.type)) {
        setErrors((prevErrors) => ({
            ...prevErrors,
            courseImage: 'Please select an image of type JPG or PNG',
        }));
        console.log("ERROR",errors.courseImage)
        return;
    }

    // Check file size (should be 1 MB or less)
    if (file && file.size > 50 * 1024) {
        setErrors((prevErrors) => ({
            ...prevErrors,
            courseImage: 'Image size must be 50kb or smaller',
        }));
        console.log("ERROR333",errors.courseImage)
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
      formDataToSend.append("courseName", formData.courseName.trim());
      formDataToSend.append("courseDescription", formData.courseDescription);
      formDataToSend.append("courseCategory", formData.courseCategory);
      formDataToSend.append("courseAmount", formData.courseAmount);
      formDataToSend.append("courseImage", formData.courseImage);
      formDataToSend.append("Trainer", formData.Trainer);
      formDataToSend.append("Duration", formData.Duration);
      formDataToSend.append("Noofseats", formData.Noofseats);

      // Send the form data
      try {
          const response = await axios.post(`${baseUrl}/course/create/trainer`,formDataToSend, {
              headers:{
                "Authorization": token
              }
          });

          if (response.status===200) {
              const reply = response.data;
              const { message, courseId, coursename } = reply;
              navigate(`/course/Addlesson/${coursename}/${courseId}`)
          }
      } catch (error) {
        
            if(error.response && error.response.status===401){
                MySwal.fire({
                    title: "Un Authorized!",
                    text: "You are Un Authorided to create the course.",
                    icon: "error",
                    confirmButtonText: "OK",
                }); 
            }else if(error.response && error.response.status===400){
                MySwal.fire({
                    title: "Error!",
                    text: error.response.data,
                    icon: "error",
                    confirmButtonText: "OK",
                });
            }else{
                // MySwal.fire({
                //     title: "Error!",
                //     text: "Some unexpected error occurred. Please try again later.",
                //     icon: "error",
                //     confirmButtonText: "OK",
                // });
                throw error
            }
       
        }
  };

  const canSubmit = () => {
      // Check if all form data fields are filled and there are no errors
      const hasNoErrors = Object.values(errors).every((error) => error === '');
      const hasNoEmptyFields = Object.values(formData).every((value) => value !== '' && value !== null);
      
      return hasNoErrors && hasNoEmptyFields;
  };

  return (
    <div>
    <div className="page-header"></div>
    <div className="card">
      <div className="card-body">
      <div className="row">
          <div className="col-12">
            <div className="navigateheaders">
              <div onClick={() => { navigate(-1); }}>
                <i className="fa-solid fa-arrow-left"></i>
              </div>
              <div></div>
              <div onClick={() => { navigate("/dashboard/course"); }}>
                <i className="fa-solid fa-xmark"></i>
              </div>
            </div>
                    <h4>Setting up a Course</h4>
                    <form className='col-10'>
                        {/* Form fields */}
                        <div >
                            {/* Course Title */}
                            <div className='form-group row'>
                                <label htmlFor='courseName'className="col-sm-3 col-form-label"
                                >Course Title <span className="text-danger">*</span></label>
                                <div className='col-sm-9'>
                                    <input
                                        type="text"
                                       
                                        id='courseName'
                                        name="courseName"
                                        value={formData.courseName}
                                        className={`form-control   mt-2 ${errors.courseName && 'is-invalid'}`}
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
                            <div className='form-group row'>
                                <label htmlFor='courseDescription' className="col-sm-3 col-form-label">Course Description <span className="text-danger">*</span></label>
                                <div className='col-sm-9'>
                                    <textarea
                                        type="text"
                                        rows={3}
                                        
                                        id='courseDescription'
                                        name="courseDescription"
                                        value={formData.courseDescription}
                                        onChange={handleChange}
                                        className={`form-control   ${errors.courseDescription && 'is-invalid'}`}
                                        placeholder="Course Description"
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.courseDescription}
                                    </div>
                                </div>
                            </div>
                            
                            {/* Course Category */}
                            <div className='form-group row'>
                                <label htmlFor='courseCategory' className="col-sm-3 col-form-label">Course Category <span className="text-danger">*</span></label>
                                <div className='col-sm-9'>
                                    <input
                                        type="text"
                                       
                                        id='courseCategory'
                                        name="courseCategory"
                                        onChange={handleChange}
                                        className={`form-control   mt-1 ${errors.courseCategory && 'is-invalid'}`}
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
                            <div className='form-group row'>
                                <label htmlFor='courseImage'className="col-sm-3 col-form-label">Course Image <span className="text-danger">*</span></label>
                               <div className='col-sm-9'> <div className=" custom-file">
                  <input
                    type="file"
                    className={`custom-file-input ${errors.courseImage && "is-invalid"}`}
                    onChange={handleFileChange}
                    id="courseImage"
                    name="courseImage"
                    accept="image/*"
                  />
                  <label className="custom-file-label" htmlFor="courseImage">
                    Choose file...
                  </label>
                  <div className="invalid-feedback">{errors.courseImage}</div>
                </div></div>
                            </div>
                            {/* Displaying Selected Image */}
                            {formData.base64Image && (
                                <div className='form-group row'>
                                    <div className="col-sm-3 col-form-label"></div>
                                    <div className='col-sm-9'>
                                        <img
                                            src={formData.base64Image}
                                            alt="Selected"
                                            style={{ width: "100px", height: "100px" }}
                                        />
                                    </div>
                                </div>
                            )}
 <div className="form-group row p-3">
    <div className="col-sm-6">
                            <div className='row'>
                                <label htmlFor='Duration'className=" col-form-label">Duration (Hours) <span className="text-danger">*</span></label>
                                <div className='col'>
                                    <input
                                        type="number"
                                        placeholder='Duration'
                                        id='Duration'
                                        name="Duration"
                                        value={formData.Duration}
                                        onChange={handleChange}
                                        className={`form-control   mt-1 ${errors.Duration && 'is-invalid'}`}
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.Duration}
                                    </div>
                                </div>
                            </div>
</div>
<div className="col-sm-6">
                            {/* Number of Seats */}
                            <div className='row'>
                                <label htmlFor='Noofseats' className="col-form-label">Number of Seats <span className="text-danger">*</span></label>
                                <div className='col'>
                                    <input
                                        type="number"
                                       placeholder='No of Seats'
                                        id='Noofseats'
                                        name="Noofseats"
                                        className={`form-control   mt-1 ${errors.Noofseats && 'is-invalid'}`}
                                        value={formData.Noofseats}
                                        onChange={handleChange}
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.Noofseats}
                                    </div>
                                </div>
                            </div>
                            </div>
</div>
                            {/* Course Amount */}
                            <div className='form-group row'>
                                <label htmlFor='courseAmount' className="col-sm-3 col-form-label">Course Amount <span className="text-danger">*</span></label>
                                <div className='col-sm-9'>
                                    <input
                                        type="number"
                                        placeholder='Amount'
                                        style={{  marginBottom: "4px" }}
                                        id='courseAmount'
                                        name="courseAmount"
                                        value={formData.courseAmount}
                                        className={`form-control   mt-1 ${errors.courseAmount && 'is-invalid'}`}
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
                        
                    </form>
                    <div className='cornerbtn'>
                          
                            <button
                                className='btn btn-secondary'
                                type="button"
                                onClick={() => {
                                    // Add cancel logic here if necessary
                                }}
                            >
                                Cancel
                            </button>
                            <button
                                className='btn btn-primary'
                                type="submit"
                                onClick={handleSubmit}
                                disabled={!canSubmit()}
                            >
                                Save
                            </button>
                        </div>
                </div>
                </div>
                </div>
            </div>
        </div>
  )
}

export default CreateCourseTrainer
