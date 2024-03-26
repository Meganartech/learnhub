import React, { useState } from "react";
import "@fortawesome/fontawesome-free/css/all.min.css"; // Import Font Awesome CSS
import login from "../images/login.jpg";
import "./StudentRegister.css";
import "../certificate/certificate.css"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const StudentRegister = () => {
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

  const handleChange = (e) => {
    const { name, value } = e.target;
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
      const response = await fetch("http://localhost:8080/student/register", {
        method: "POST",
        body: formDataToSend,
      });

      const data = await response.json();

      if (response.ok) {
        // Display success message and redirect to login
        MySwal.fire({
          title: "welcome to our Family !",
          text: "You have been registered successfully!",
          icon: "success",
          confirmButtonText: "Go to Login",
          showCancelButton: true,
          cancelButtonText: "Cancel",
        }).then((result) => {
          if (result.isConfirmed) {
            // Redirect to login page
            window.location.href = "/login";
          }
        });
      } else if (response.status === 400) {
        MySwal.fire({
          title: "Error!",
          text: `${data.message}`,
          icon: "error",
          confirmButtonText: "OK",
        });
      }
      
    } catch (error) {
      // Display error message
      MySwal.fire({
        title: "Error!",
        text: "An error occurred while registering. Please try again later.",
        icon: "error",
        confirmButtonText: "OK",
      });
    }
  };

  return (
   
    <div className='background'>
      <div className='innerFrame'>
        <h3>Join with us..</h3>
        <div className='mainform'>
          <div className='profile-picture'>
            <div className='image-group'>
            {formData.base64Image && (
              <img
                src={formData.base64Image}
                alt="Selected "
             
              />
            )}
            </div>
            <label htmlFor='fileInput' className='file-upload-btn'>
              Upload
            </label>
            <input
              type='file'
              id='fileInput'
              className='file-upload'
              accept='image/*'
              onChange={handleFileChange}
            />
          </div>

          <div className='formgroup'>
            <div className='inputgrp'>
              <label htmlFor='Name'> Name</label>
              <span>:</span>
              <input
               type="text"
                id='Name'
                value={formData.username}
                onChange={handleChange}
                name="username"
                placeholder="Full Name"
                autoFocus
                required
              />
            </div>
            <div className='inputgrp'>
              <label htmlFor='nName'> Email</label>
              <span>:</span>
              <input
                      type="email"
                      autoComplete="off"
                      className="form-control form-control-lg"
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      placeholder="Email Address"
                      required
                    />
            </div>

            <div className='inputgrp'>
              <label htmlFor='ownerName'>Date of Birth</label>
              <span>:</span>
              <input
                type="date"
                                    name="dob"
                                    className="form-control form-control-lg"
                                    placeholder="Starting year"
                                    value={formData.dob}
                                    onChange={handleChange}
                                    required
              />
            </div>

            <div className='inputgrp'>
              <label htmlFor='Password'>Password</label>
              <span>:</span>
              <input
                        type="password"
                        name="psw"
                        className="form-control form-control-lg"
                        value={formData.psw}
                        onChange={handleChange}
                        placeholder="Password"
                        autoComplete="new-password"
                        required
                      />
            </div>

            <div className='inputgrp'>
              <label htmlFor='confirm_password'>Re-type password</label>
              <span>:</span>
              <input
                        type="password"
                        name="confirm_password"
                        className="form-control form-control-lg"
                        id="exampleRepeatPassword"
                        autoComplete="new-password"
                        placeholder="Repeat Password"
                        required
                      />
            </div>
            <div className='inputgrp'>
              <label htmlFor='Phone'> Phone</label>
              <span>:</span>
              <input
               type="text"
                id='phone'
                value={formData.phone}
                onChange={handleChange}
                name="phone"
                placeholder="Phone"
               
                required
              />
            </div>
           
          </div>
        </div>
        <div className='btngrp'>
          <button className='btn btn-primary' onClick={handleSubmit}>Register</button>
         
        </div><div style={{textAlign:"right"}} className="w-100">
        <a className="small" href="/login">
                     Already have an account? Login!
                 </a></div>
      </div>
    </div>
  );
};

export default StudentRegister;
