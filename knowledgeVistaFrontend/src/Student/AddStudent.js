import React, { useEffect, useState } from 'react'
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import profile from "../images/profile.png"

const AddStudent = () => {
    const token=sessionStorage.getItem("token")
    const MySwal = withReactContent(Swal);
    const [formData, setFormData] = useState({
      username: "",
      psw: "",
      email: "",
      dob: "",
      phone:"",
      profile: null,
      isActive: true,
    });
    const [errors, setErrors] = useState({
      username: '',
      email: '',
      dob: '',
      psw: '',
      confirm_password: '',
      phone: '',
      fileInput:''
      
    });
    useEffect(() => {
      // Check if any errors exist or if any input is null
      const hasErrors = Object.values(errors).some(error => !!error) || Object.values(formData).some(value => value === null);
      // Enable or disable submit button based on error presence
      const submitBtn = document.querySelector('.btn.btn-primary');
      if (hasErrors) {
        submitBtn.disabled = true;
      } else {
        submitBtn.disabled = false;
      }
    }, [errors, formData]);
  
    const handleChange = (e) => {
      const { name, value } = e.target;
      let error = '';
  
      switch (name) {
        case 'username':
          error = value.length < 1 ? 'Please enter a username' : '';
          break;
        case 'email':
          // This is a basic email validation, you can add more advanced validation if needed
          error = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? '' : 'Please enter a valid email address';
          break;
        case 'dob':
          const dobDate = new Date(value);
          const today = new Date();
          const maxDate = new Date(today.getFullYear() - 8, today.getMonth(), today.getDate()); // Min age 8 years
          const minDate = new Date(today.getFullYear() - 100, today.getMonth(), today.getDate()); // Max age 100 years
          error = dobDate <= maxDate && dobDate >= minDate ? '' : 'Please enter a valid date of birth';
          break;
        case 'psw':
          error = value.length < 6 ? 'Password must be at least 8 characters long' : '';
          break;
        case 'confirm_password':
          error = value !== formData.psw ? 'Passwords do not match' : '';
          break;
        case 'phone':
          // This is a basic phone number validation, you can add more advanced validation if needed
          error = /^\d{10}$/.test(value) ? '' : 'Please enter a valid phone number';
          break;
          
  
        default:
          break;
      }
  
      setErrors(prevErrors => ({
        ...prevErrors,
        [name]: error
      }));
  
      setFormData(prevState => ({
          ...prevState,
          [name]: value
      }));
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
      
      // Convert the file to base64
      convertImageToBase64(file)
        .then((base64Data) => {
          // Set the base64 encoded image and the file in the state
          setFormData((prevFormData) => ({
            ...prevFormData,
            profile: file,
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
        formDataToSend.append("username", formData.username);
        formDataToSend.append("psw", formData.psw);
        formDataToSend.append("email", formData.email);
        formDataToSend.append("dob", formData.dob);
        formDataToSend.append("phone", formData.phone);
        formDataToSend.append("isActive", formData.isActive);
        formDataToSend.append("profile", formData.profile);
      
        try {
          const response = await fetch("http://localhost:8080/admin/addStudent", {
            method: "POST",
            headers: {
              Authorization: token,
            },
            body: formDataToSend,
          });
         
          const data = await response.json();
      
          if (response.ok) {
            // Display success message and reset form fields
            MySwal.fire({
              title: "Added !",
              text: "New Student Added successfully!",
              icon: "success",
              confirmButtonText: "OK",
              
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = "/view/Students";
                }
              });
          } else if (response.status === 400) {
            if (data.message === "EMAIL ALREADY EXISTS") {
              setErrors(prevErrors => ({
                ...prevErrors,
                email: "This email is already registered."
              }));
            } else {
              MySwal.fire({
                title: "Error!",
                text: `${data.message}`,
                icon: "error",
                confirmButtonText: "OK",
              });
            }
          } else if (response.status === 401) {
            MySwal.fire({
              title: "Un Authorized!",
              text: "you are unable to Add a student",
              icon: "error",
              confirmButtonText: "OK",
            });
          } else if (response.status === 500) {
            MySwal.fire({
              title: "Server Error!",
              text: "Unexpected Error Occured",
              icon: "error",
              confirmButtonText: "OK",
            });
          }
          
        } catch (error) {
          // Display error message
          MySwal.fire({
            title: "Error!",
            text: "An error occurred while adding STUDENT. Please try again later.",
            icon: "error",
            confirmButtonText: "OK",
          });
        }
    };
    
   
  return (
    <div className='background'>
    <div className='innerFrame'>
      <h3>Add Students</h3>
      <div className='mainform'>
          <div className='profile-picture'>
            <div className='image-group'>
            {formData.base64Image ? (
                      <img
                        src={formData.base64Image}
                        alt="Selected Image"
                        className="profile-picture"
                      />
                    ) : (
                      <img
                        src={profile}
                        alt="Default Profile Picture"
                        className="prof"
                      />
                    )}
            </div>
            <label htmlFor='fileInput' className='file-upload-btn'>
              Upload
            </label>
      
            <input
                  type='file'
                  name="fileInput"
                  id='fileInput'
                  className={`file-upload ${errors.fileInput && 'is-invalid'}`}
                  accept='image/*'
                  onChange={handleFileChange}
                />
    
          </div>

          <div className='formgroup'>
            <div className='inputgrp'>
              <label htmlFor='Name'> Name <span className="text-danger">*</span></label>
              <span>:</span>
            <div> <input
               type="text"
                id='Name'
                value={formData.username}
                onChange={handleChange}
                name="username"
                
                className={`form-control form-control-lg mt-1 ${errors.username && 'is-invalid'}`}
                placeholder="Full Name"
                autoFocus
                required
              />
              <div className="invalid-feedback">
                {errors.username}
              </div></div> 
            </div>
            <div className='inputgrp'>
            
              <label htmlFor='email'> Email<span className="text-danger">*</span></label>
              <span>:</span><div>              <input
                      type="email"
                      autoComplete="off"
                      className={`form-control form-control-lg ${errors.email && 'is-invalid'}`}
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      placeholder="Email Address"
                      required
                    />
                    <div className="invalid-feedback">
                      {errors.email}
                    </div></div>

            </div>

            <div className='inputgrp'>
              <label htmlFor='dob'>Date of Birth<span className="text-danger">*</span></label>
              <span>:</span>
              <div>
              <input
                type="date"
                                    name="dob"
                                    className={`form-control form-control-lg ${errors.dob && 'is-invalid'}`}
                                    placeholder="Starting year"
                                    value={formData.dob}
                                    onChange={handleChange}
                                    required
                                   
              />
              <div className="invalid-feedback">
                {errors.dob}
              </div></div>
            </div>

            <div className='inputgrp'>
              <label htmlFor='Password'>Password<span className="text-danger">*</span></label>
              <span>:</span>
              <div>
                <input
                        type="password"
                        name="psw"
                        className={`form-control form-control-lg ${errors.psw && 'is-invalid'}`}
                        value={formData.psw}
                        onChange={handleChange}
                        placeholder="Password"
                        autoComplete="new-password"
                        required
                      />
                      <div className="invalid-feedback">
                        {errors.psw}
                      </div></div>

            </div>

            <div className='inputgrp'>
              <label htmlFor='confirm_password'>Re-type password<span className="text-danger">*</span></label>
              <span>:</span>
              <div>
              <input
                        type="password"
                        name="confirm_password"
                        className={`form-control form-control-lg ${errors.confirm_password && 'is-invalid'}`}
                        id="exampleRepeatPassword"
                        onChange={handleChange}
                        autoComplete="new-password"
                        placeholder="Repeat Password"
                        required
                      />
                      <div className="invalid-feedback">
                        {errors.confirm_password}
                      </div>
                      </div>
            </div>
            <div className='inputgrp'>
              <label htmlFor='Phone'> Phone<span className="text-danger">*</span></label>
              <span>:</span>
              <div>
              <input
               type="text"
                id='phone'
                value={formData.phone}
                className={`form-control form-control-lg ${errors.phone && 'is-invalid'}`}
                onChange={handleChange}
                name="phone"
                placeholder="Phone"
                required
              />
              <div className="invalid-feedback">
                {errors.phone}
              </div>
              </div>
            </div>
           
          </div>
        </div>
        <div className='btngrp'>
        <button className={`btn btn-primary `} onClick={handleSubmit}>Register</button>

        </div>
    </div>
  </div>
  )
}

export default AddStudent
