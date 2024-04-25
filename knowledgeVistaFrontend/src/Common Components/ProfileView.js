
import React, { useEffect, useState } from 'react'
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useNavigate, useParams } from 'react-router-dom';

const ProfileView = () => {
  
  const token=sessionStorage.getItem("token")
  const MySwal = withReactContent(Swal);
  const [img, setImg] = useState();
  const email = sessionStorage.getItem("email");
  const [userData, setUserData] = useState({
    username: "",
    email: "",
    phone: "",
    skills: "",
    dob: "",
    base64Image:null,
  });
  const [isEditing, setIsEditing] = useState(false); // State variable to control which view to show
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
}, [errors, userData]);
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`http://localhost:8080/student/users/${email}`);
        const userData = await response.json();
        if (!response.ok) {
          throw new Error('Failed to fetch user data');
        }

        console.log(userData);
        setImg(`data:image/jpeg;base64,${userData.profile}`);
        setUserData(userData);
      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    };

    fetchData();
  }, [email,isEditing]);

  // Function to handle the edit button click
  const handleEditClick = () => {
    setIsEditing(true);
  };

  // Function to handle the save button click (you can add save logic here)
  const handleSaveClick = () => {
    setIsEditing(false);
  };

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

    setUserData(prevState => ({
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
        setUserData((prevFormData) => ({
          ...prevFormData,
          profile: file,
          base64Image: base64Data,
        }));
      })
      .catch((error) => { MySwal.fire({
        title: "Error!",
        text: error ,
        icon: "error",
        confirmButtonText: "OK",
      });
      });
  };
  

  const handleSubmit = async (e) => {
      e.preventDefault();
     
      const formDataToSend = new FormData();
      formDataToSend.append("username", userData.username);
      formDataToSend.append("email", userData.email);
      formDataToSend.append("dob", userData.dob);
      formDataToSend.append("phone", userData.phone);
      formDataToSend.append("profile", userData.profile);
      formDataToSend.append("skills",userData.skills);
      
      formDataToSend.append("isActive", userData.isActive);
    
      try {
        const response = await fetch('http://localhost:8080/Edit/self', {
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
            text: "Profile Updated successfully!",
            icon: "success",
            confirmButtonText: "OK",
            
          }).then((result) => {
              if (result.isConfirmed) {
                setIsEditing(false)
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
            text: "you are unable to Update a student",
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
          text: "An error occurred while Updating STUDENT. Please try again later.",
          icon: "error",
          confirmButtonText: "OK",
        });
      }
  };
 


  // Profile view
  const profileView = (
    <div className='innerFrame'>
      <h2 style={{ textDecoration: "underline" }}>Profile</h2>
      <div className='mainform'>
        <div className='profile-picture'>
          <div className='image-group'>
            <img id="preview" src={img} alt='profile' />
          </div>
        </div>
        <div className='formgroup'>
          <div className='inputgrp'>
            <label>Name</label>
            <span>:</span>
            <input   
            className='disabledbox'
                readOnly
                value={userData.username}/>
          </div>
          <div className='inputgrp'>
            <label>Email</label>
            <span>:</span>
            <input 
             className='disabledbox'
                readOnly
                value={userData.email}/>
          </div>
          <div className='inputgrp'>
            <label>Date of Birth</label>
            <span>:</span>
            <input
              className='disabledbox'
              readOnly
              value={userData.dob}/>
          </div>
          <div className='inputgrp'>
            <label>Skills</label>
            <span>:</span>
            <input
              className='disabledbox'
              readOnly
              value={userData.skills}/>
          </div>
          <div className='inputgrp'>
            <label>Phone</label>
            <span>:</span>
            <input
              className='disabledbox'
              readOnly
              value={userData.phone}/>
          </div>
        </div>
      </div>
      <div className='btngrp'>
        <button className="btn btn-primary" onClick={handleEditClick}>Edit</button>
      </div>
    </div>
  );

  // Edit profile view
  const editProfileView = (
    <div className='innerFrame'>
      <h2 style={{ textDecoration: "underline" }}>Edit Profile</h2>
      <div className='mainform'>
        <div className='profile-picture'>
          <div className='image-group'>  
          {userData.base64Image ? (
                    <img
                      src={userData.base64Image}
                      alt="Selected Image"
                      className="profile-picture"
                    />
                  ) : (
                    <img
                      src={img}
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
            <label htmlFor='Name'>Name</label>
            <span>:</span>
            <div> 
            <input
             type="text"
              id='Name'
              value={userData.username}
              onChange={handleChange}
              name="username"
              
              className={`form-control form-control-lg mt-1 ${errors.username && 'is-invalid'}`}
              placeholder="Full Name"
              autoFocus
              required
            />
            <div className="invalid-feedback">
              {errors.username}
            </div>
            </div> 
          </div>
          <div className='inputgrp'>
            <label htmlFor='email'>Email</label>
            <span>:</span>
            <div>              <input
                    type="email"
                    autoComplete="off"
                    className={`form-control form-control-lg ${errors.email && 'is-invalid'}`}
                    name="email"
                    value={userData.email}
                    onChange={handleChange}
                    placeholder="Email Address"
                    required
                  />
                  <div className="invalid-feedback">
                    {errors.email}
                  </div></div>
          </div>
          <div className='inputgrp'>
            <label htmlFor='dob'>Date of Birth</label>
            <span>:</span>
            <div>
            <input
              type="date"
                                  name="dob"
                                  className={`form-control form-control-lg ${errors.dob && 'is-invalid'}`}
                                  placeholder="Starting year"
                                  value={userData.dob}
                                  onChange={handleChange}
                                  required
                                 
            />
            <div className="invalid-feedback">
              {errors.dob}
            </div></div>
          </div>
          <div className='inputgrp'>
            <label htmlFor='skills'>Skills</label>
            <span>:</span>
            <div> <input
             type="text"
              id='skills'
              value={userData.skills}
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
            <label htmlFor='Phone'>Phone</label>
            <span>:</span>
            <div>
            <input
             type="text"
              id='phone'
              value={userData.phone}
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
        <button className="btn btn-primary" id='submitbtn' onClick={handleSubmit}>Save</button>
      </div>
    </div>
  );

  return (
    <div className='contentbackground'>
      <div className='contentinner'>
        {/* Render either profile view or edit profile view based on the state */}
        {isEditing ? editProfileView : profileView}
      </div>
    </div>
  );
}

export default ProfileView;