import React, { useEffect, useState } from 'react'
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import profile from "../images/profile.png"
import { useNavigate, useParams } from 'react-router-dom';

const EditTrainer = () => {
    const{email}=useParams()
    const navigate = useNavigate();
    const token=sessionStorage.getItem("token")
    const MySwal = withReactContent(Swal);
    const [notFound, setNotFound] = useState(false);
    const [formData, setFormData] = useState({
      username: "",
      base64Image:null,
      email: "",
      dob: "",
      skills:"",
      phone:"",
      profile: null,
      isActive: true,
    });
    const [errors, setErrors] = useState({
      username: '',
      email: '',
      dob: '',
      skills:'',
     
      phone: '',
      fileInput:''
      
    });
    useEffect(() => {
      const hasErrors = Object.values(errors).some(error => !!error) ;
      const submitBtn = document.querySelector('#submitbtn');
      if (submitBtn) {
          submitBtn.disabled = hasErrors;
      }
  }, [errors, formData]);
    useEffect(() => {
      const fetchData = async () => {
        try {
          const response = await fetch(`http://localhost:8080/student/users/${email}`);
          const userData = await response.json();
        if(response.ok){
  
          setFormData(userData);
          setFormData((prevFormData) => ({
            ...prevFormData, 
            base64Image: `data:image/jpeg;base64,${userData.profile}` 
        }));
    }else if(response.status===404){
        setNotFound(true)
    }
        } catch (error) {
          console.error('Error fetching user data:', error);
        }
      };
  
      fetchData();
    }, []);
  
    const handleChange = (e) => {
      const { name, value } = e.target;
      let error = '';
  
      switch (name) {
        case 'username':
          error = value.length < 1 ? 'Please enter a username' : '';
          break;
          case 'skills':
            error = value.length < 1 ? 'Please enter a skill' : '';
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
        formDataToSend.append("skills",formData.skills);
      
        try {
          const response = await fetch(`http://localhost:8080/Edit/Trainer/${email}`, {
            method: "PATCH",
            headers: {
              Authorization: token,
            },
            body: formDataToSend,
          });
         
          const data = await response.json();
      
          if (response.ok) {
           
            // Display success message and reset form fields
            MySwal.fire({
              title: "Updated !",
              text: "Trainer Information successfully!",
              icon: "success",
              confirmButtonText: "OK",
              
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = "/view/Trainer";
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
              text: "you are unable to Update a Trainer",
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
      
          MySwal.fire({
            title: "Error!",
            text: "An error occurred while adding Trainer. Please try again later.",
            icon: "error",
            confirmButtonText: "OK",
          });
        }
    };
   
  return (
    <div className='contentbackground'>
    <div className='contentinner'>
    {notFound ? (
          <div className='enroll'>
            <h2>No trainer found in this email</h2>
            <button className='btn btn-primary' onClick={()=>{navigate(-1);}}>Go Back</button>
          </div>
        ) : (
  <div className='innerFrame'>
    <h2  style={{textDecoration:"underline"}}>Edit  Trainer</h2>
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
            <label htmlFor='skills'> Skills <span className="text-danger">*</span></label>
            <span>:</span>
          <div> <input
             type="text"
              id='skills'
              value={formData.skills}
              onChange={handleChange}
              name="skills"
              
              className={`form-control form-control-lg  ${errors.skills && 'is-invalid'}`}
              placeholder="skills"
           
              required
            />
            <div className="invalid-feedback">
              {errors.skills}
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

       
          <div className='inputgrp '>
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
      <button className= "btn btn-primary" id='submitbtn' onClick={handleSubmit}>Save</button>

      </div>
  </div>     )}
  </div>
</div>
  )
}

export default EditTrainer
