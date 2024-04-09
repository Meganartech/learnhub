import React, { useState } from 'react'
import { useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { toast } from 'react-toastify';

const CourseCreation = () => {
  const MySwal = withReactContent(Swal);
  const [formData, setFormData] = useState({
    courseName: "",
    courseDescription: "",
    courseCategory: "",
    courseAmount:"",
    Trainer:"",
    Duration:"",
    Noofseats:"",
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
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    const formDataToSend = new FormData();
    formDataToSend.append("courseName", formData.courseName);
    formDataToSend.append("courseDescription", formData.courseDescription);
    formDataToSend.append("courseCategory", formData.courseCategory);
    formDataToSend.append("courseAmount",formData.courseAmount)
    formDataToSend.append("courseImage", formData.courseImage);
    formDataToSend.append("Trainer",formData.Trainer);
    formDataToSend.append("Duration",formData.Duration);
    formDataToSend.append("Noofseats",formData.Noofseats);
   
    try {
      const response = await fetch("http://localhost:8080/course/add", {
        method: "POST",
        body: formDataToSend,
      });
      console.log(formDataToSend);
      const reply=await response.json();
      const Message=reply.message;
      const Id=reply.courseId;
      const Name=reply.coursename;

      if (response.ok) {
           
            window.location.href = `/course/Addlesson/${Name}/${Id}`;
          
        
      }
    } catch (error) {
      // Handle network errors or other exceptions
      toast.error(`Some Unexpected Error occured . Please try again later`);
      
      // MySwal.fire({
      //   title: "Error!",
      //   text: "Some Unexpected Error occured . Please try again later.",
      //   icon: "error",
      //   confirmButtonText: "OK",
      // });
    }
  };
  return (
    <div className='contentbackground'>
        <div className='contentinner'> 
        <div className='divider ml-2'>
        <h1 style={{textDecoration:"underline"}}>Setting up a Course</h1>
        <form onSubmit={handleSubmit}>
        <div className='formgroup mt-2' style={{fontSize:"larger"}}>
        <div className='inputgrp '>
              <label htmlFor='courseName'> Course Title <span className="text-danger">*</span></label>
              <span>:</span>
             <div> 
                <input
               type="text"
               style={{width:"400px"}}
               id='courseName'
                name="courseName"
                value={formData.courseName}
                className='form-control form-control-lg mt-2'
               //className={`form-control form-control-lg mt-1 ${errors.title && 'is-invalid'}`}
                placeholder="Course Title"
                onChange={handleChange}
                autoFocus
                required
              />
              <div className="invalid-feedback">
              {/* {errors.username} */}
              </div>
            </div> 
            </div>

            <div className='inputgrp'>
              <label htmlFor='courseDescription'> Course description <span className="text-danger">*</span></label>
              <span>:</span>
             <div> 
                <input
               type="text"
               style={{width:"400px"}}
               id='courseDescription'
                name="courseDescription"
                value={formData.courseDescription}
                onChange={handleChange}
                className='form-control form-control-lg '
               // className={`form-control form-control-lg mt-1 ${errors.title && 'is-invalid'}`}
                placeholder="Course description"
                required
              />
              <div className="invalid-feedback">
              {/* {errors.description} */}
              </div>
            </div> 
            </div> 

            <div className='inputgrp'>
              <label htmlFor='courseCategory'> Course category <span className="text-danger">*</span></label>
              <span>:</span>
             <div> 
                <input
               type="text"
               style={{width:"400px"}}
               id='courseCategory'
                name="courseCategory"
                onChange={handleChange}
                className='form-control form-control-lg '
               // className={`form-control form-control-lg mt-1 ${errors.title && 'is-invalid'}`}
                placeholder="Course category"
                value={formData.courseCategory}
                required
              />
              <div className="invalid-feedback">
              {/* {errors.category} */}
              </div>
            </div> 
            </div>

            <div className='inputgrp'>
              <label htmlFor='Trainer'>  Trainer <span className="text-danger">*</span></label>
              <span>:</span>
             <div> 
                <input
               type="text"
               style={{width:"400px"}}
               id='Trainer'
                name="Trainer"
                value={formData.Trainer}
                onChange={handleChange}
                className='form-control form-control-lg '
               // className={`form-control form-control-lg mt-1 ${errors.trainer && 'is-invalid'}`}
                placeholder="Course trainer"
            
                required
              />
              <div className="invalid-feedback">
              {/* {errors.trainer} */}
              </div>
            </div> 
            </div>
            <div className='inputgrp'>
              <label htmlFor='courseimage'>  Course Image <span className="text-danger">*</span></label>
              <span>:</span>
             <div> 
                <label htmlFor='courseimage' 
               style={{width:"400px"}}   className='file-upload-btn '>upload Image</label>
                < input
               type="file"
               onChange={handleFileChange}
               style={{width:"400px"}}
               id='courseimage'
               className='file-upload'
                name="courseimage"
                accept='image/*'
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
                alt="Selected "
                style={{ width:"200px",height:"200px"}}
              /></div></div>
            )}
            <div className='inputgrp '>
              <label htmlFor='Duration'>  Duration <span className="text-danger">*</span></label>
              <span>:</span>
             <div> 
                <input
                type="number"
                style={{width:"400px"}}
                id='Duration'
                name="Duration"
                value={formData.Duration}
                onChange={handleChange}
                className='form-control form-control-lg '
               // className={`form-control form-control-lg mt-1 ${errors.duration && 'is-invalid'}`
                required
              />
              <div className="invalid-feedback">
              {/* {errors.duration} */}
              </div>
            </div> 
            </div>
            <div className='inputgrp'>
              <label htmlFor='Noofseats'>  Set Number of Seats <span className="text-danger">*</span></label>
              <span>:</span>
             <div> 
                <input
               type="number"
               style={{width:"400px"}}
               id='Noofseats'
                name="Noofseats"
               // className={`form-control form-control-lg mt-1 ${errors.Noofseats && 'is-invalid'}`}
               value={formData.Noofseats}
               onChange={handleChange}
               className='form-control form-control-lg '
                
                required
              />
              <div className="invalid-feedback">
              {/* {errors.duration} */}
              </div>
            </div> 
          

        </div>
        <div className='inputgrp'>
              <label htmlFor='courseAmount'>  Course Amount <span className="text-danger">*</span></label>
              <span>:</span>
             <div> 
                <input
               type="number"
               style={{width:"400px",marginBottom:"4px"}}
               id='courseAmount'
                name="courseAmount"
                
            value={formData.courseAmount}
               // className={`form-control form-control-lg mt-1 ${errors.courseAmount && 'is-invalid'}`}
               onChange={handleChange}
               className='form-control form-control-lg '
                
                required
              />
              <div className="invalid-feedback">
              {/* {errors.duration} */}
              </div>
            </div>
            </div>
            </div>
           
        <div className='cornerbtn'>           
                 <button className='btn btn-primary' type="submit">Save</button> 
                 <button className='btn btn-primary'>Cancel</button>
            </div>
              </form>
            </div>
          
    </div>
      
    </div>
  )
}

export default CourseCreation
