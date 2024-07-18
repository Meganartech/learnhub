import React, {  useEffect, useRef, useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../../api/utils';
import { json, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Partialpaymentsetting from './Partialpaymentsetting';

const CourseCreation = () => {
    const token = sessionStorage.getItem("token");
    const MySwal = withReactContent(Swal);
    const navigate =useNavigate();
    const [nextclick,setnextclick]=useState(false)
    const [installmentData, setInstallmentData] = useState([]);
  const [durations, setDurations] = useState([]);
  
  const [enablechecked,setenablechecked]=useState(false);
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
    const courseName=useRef(null);
    const  courseCategory=useRef(null);
    const  courseDescription =useRef(null);
    const  courseAmount=useRef(null);
    const Duration=useRef(null);
    const Noofseats=useRef(null);
    const courseImage=useRef(null);
;

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
   
    const scrollToError = () => {
        if (errors.courseName) {
          courseName.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
        }else if (errors.courseCategory) {
            courseCategory.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
          }else if (errors.courseDescription) {
            courseDescription.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
          }else if (errors.courseAmount) {
            courseAmount.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
          }else if (errors.Duration) {
            Duration.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
          }else if (errors.Noofseats) {
            Noofseats.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
          }
      };
    
      useEffect(() => {
        scrollToError();
      }, [errors]);
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
const handlenextclick =(e)=>{
    e.preventDefault();
    let hasErrors = false;
    const requiredFields = ['courseName', 'courseDescription', 'courseCategory',  'courseAmount','Duration','Noofseats','courseImage'];
  
    requiredFields.forEach(field => {
      if (!formData[field] || formData[field].length === 0 || errors[field]) {
        hasErrors = true;
        setErrors(prevErrors => ({
          ...prevErrors,
          [field]: !formData[field] ? 'This field is required' : errors[field]
        }));
      }
    });
    if(!formData.courseImage){
      hasErrors = true;
      setErrors(prevErrors => ({
        ...prevErrors,
        courseImage: 'Image is Required'
      }));
    }
  
    if (!hasErrors) {
        setnextclick(true);
    }
   
}
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
        if(enablechecked){
         formDataToSend.append("paytype","PART")
        
        const installmentDataJson = JSON.stringify(installmentData);
  formDataToSend.append("InstallmentDetails", installmentDataJson);
 
        }else{
            
         formDataToSend.append("paytype","FULL")
        }
    
          
        
       
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
            } else if(error.response && error.response.status===400){
                MySwal.fire({
                    title: "Limit Reached !",
                    text: error.response.data,
                    icon: "waring",
                    confirmButtonText: "OK",
                });
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
    // const canSubmit = () => {
    //     // Check if all form data fields are filled and there are no errors
    //     const hasNoErrors = Object.values(errors).every((error) => error === '');
    //     const hasNoEmptyFields = Object.values(formData).every((value) => value !== '' && value !== null);
        
    //     return hasNoErrors && hasNoEmptyFields;
    // };

    return (
        <div className='contentbackground'>
            <div className='contentinner p-3'>
           
                {nextclick?(
                    <Partialpaymentsetting enablechecked={enablechecked} setenablechecked={setenablechecked} handleSubmit={handleSubmit}
                setnextclick={setnextclick} courseamount={formData.courseAmount} setDurations={setDurations} durations={durations}
                 installmentData={installmentData} setInstallmentData={setInstallmentData}/>
                ):(
                <div className='divider ml-2'>
                     <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate("/dashboard/course")}}><i className="fa-solid fa-xmark"></i></div>
      </div>
                    <h2 style={{ textDecoration: "underline" }}>Setting up a Course</h2>
                    <form >
                        {/* Form fields */}
                        <div className='formgroup pt-1 maxheight' >
                            {/* Course Title */}
                            <div className='inputgrp' ref={courseName}>
                                <label htmlFor='courseName' >Course Title <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="text"
                                        
                                        id='courseName'
                                        name="courseName"
                                        value={formData.courseName}
                                        className={`form-control form-control-lg  ${errors.courseName && 'is-invalid'}`}
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
                            <div className='inputgrp' ref={courseDescription}>
                                <label htmlFor='courseDescription'>Course Description <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <textarea
                                        type="text"
                                        rows={3}
                                      
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
                            <div className='inputgrp' ref={courseCategory}>
                                <label htmlFor='courseCategory'>Course Category <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="text"
                                       
                                        id='courseCategory'
                                        name="courseCategory"
                                        onChange={handleChange}
                                        className={`form-control form-control-lg  ${errors.courseCategory && 'is-invalid'}`}
                                        placeholder="Course Category"
                                        value={formData.courseCategory}
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.courseCategory}
                                    </div>
                                </div>
                            </div>
                           
                            <div className='inputgrp' ref={courseImage}>
                                <label htmlFor='courseImage' className='displaynone' >Course Image <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                <label
                                        htmlFor='courseImage'
                                         style={{width:"200px"}}
                                         id='must'
                                        className={`file-upload-btn  ${errors.courseImage && `is-invalid`}`}
                                    >
                                        Upload Image
                                    </label>
                                    <div className="invalid-feedback">
                                        {errors.courseImage}
                                    </div>
                                    <input
                                        type="file"
                                        onChange={handleFileChange}
                                       style={{display:"none"}}
                                        id='courseImage'
                                        name="courseImage"
                                        accept='image/*'
                                        className='file-upload'
                                    />
                                  
                                </div>
                            </div>
                            {formData.base64Image && (
                                <div className='inputgrp'>
                                    <div></div>
                                    <div></div>
                                    <div>
                                        <img
                                            src={formData.base64Image}
                                            alt="Selected"
                                            style={{ width: "100px", height: "100px" }}
                                        />
                                    </div>
                                </div>
                            )}
                            <div className='inputgrp' ref={Duration}>
                                <label htmlFor='Duration'>Duration (Hours) <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="number"
                                        placeholder='Duration'
                                        id='Duration'
                                        name="Duration"
                                        value={formData.Duration}
                                        onChange={handleChange}
                                        className={`form-control form-control-lg  ${errors.Duration && 'is-invalid'}`}
                                        required
                                    />
                                    <div className="invalid-feedback">
                                        {errors.Duration}
                                    </div>
                                </div>
                            </div>

                            {/* Number of Seats */}
                            <div className='inputgrp' ref={Noofseats}>
                                <label htmlFor='Noofseats'>Number of Seats <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="number"
                                        placeholder='No Of Seats'
                                        id='Noofseats'
                                        name="Noofseats"
                                        className={`form-control form-control-lg  ${errors.Noofseats && 'is-invalid'}`}
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
                            <div className='inputgrp' ref={courseAmount}>
                                <label htmlFor='courseAmount'>Course Amount <span className="text-danger">*</span></label>
                                <span>:</span>
                                <div>
                                    <input
                                        type="number"
                                        placeholder='Amount'
                                        style={{ marginBottom: "4px" }}
                                        id='courseAmount'
                                        name="courseAmount"
                                        value={formData.courseAmount}
                                        className={`form-control form-control-lg  ${errors.courseAmount && 'is-invalid'}`}
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
                               className='btn btn-primary'
                               type="button"
                               onClick={() => {
                                  navigate(-1) 
                               }}
                           >
                               Cancel
                           </button>
                           <button
                               className='btn btn-primary'
                               onClick={handlenextclick}
                           >
                               next
                           </button>
                       </div>
                </div>)}
            </div>
        </div>
    );
};

export default CourseCreation;
