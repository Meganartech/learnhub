import React from 'react'
import { useState,useEffect } from 'react';

const ProfileView = () => {
  const [img, setimg] = useState();
    const email=sessionStorage.getItem("email")
    const [userData, setUserData] = useState({

       username:"",
       email:"",
       phone:"",
       skills:"",
       dob:"",
       role:{
        roleName:"",
        roleId:""
       }
      });
    
      useEffect(() => {
        const fetchData = async () => {
          try {
            const response = await fetch(`http://localhost:8080/student/users/${email}`);
            const userData = await response.json();
            if (!response.ok) {
              throw new Error('Failed to fetch user data');
            }

            console.log(userData);
            setimg(`data:image/jpeg;base64,${userData.profile}`);
            setUserData(userData);
          } catch (error) {
            console.error('Error fetching user data:', error);
          }
        };
    
        fetchData();
      }, []);
  return (
    <div className='contentbackground'>
      <div className='contentinner'>
    <div className='innerFrame '>
      <h2 style={{textDecoration:"underline"}}>Profile</h2>
      <div className='mainform'>
        <div className='profile-picture'>
          <div className='image-group' >
            <img id="preview"  src={img} alt='profile' />
          </div>
          
        </div>

        <div className='formgroup' >
          <div className='inputgrp'>
            <label htmlFor='Name'> Name</label>
            <span>:</span>
            <label>
             {userData.username}</label>
          </div>
          <div className='inputgrp'>
            <label htmlFor='email'> Email</label>
            <span>:</span>
            <label>
           {userData.email}</label>
          </div>

          <div className='inputgrp'>
            <label htmlFor='dob'>Date of Birth</label>
            <span>:</span>
            <label>{userData.dob}</label>
          </div>
          <div className='inputgrp'>
            <label htmlFor='skills'>Skills</label>
            <span>:</span>
            <label>{userData.skills}</label>
          </div>

          <div className='inputgrp'>
            <label htmlFor='Phone'> Phone</label>
            <span>:</span>
            <label>{userData.phone}</label>
          </div>
          <div className='inputgrp'>
            <label htmlFor='role'>RoleName</label>
            <span>:</span>
            <label>{userData.role.roleName}</label>
          </div>
        </div>
      </div>
      
    </div>
    </div>
  </div>
  )
}

export default ProfileView
