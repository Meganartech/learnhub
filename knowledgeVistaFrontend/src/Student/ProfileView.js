import React from 'react'
import { useState,useEffect } from 'react';

const ProfileView = () => {
  const [img, setimg] = useState();
    const email=sessionStorage.getItem("email")
    const [userData, setUserData] = useState({
       
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
    <div className='background'>
    <div className='innerFrame'>
      <h3>Profile</h3>
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
            <label htmlFor='nName'> Email</label>
            <span>:</span>
            <label>
           {userData.email}</label>
          </div>

          <div className='inputgrp'>
            <label htmlFor='ownerName'>Date of Birth</label>
            <span>:</span>
            <label>{userData.dob}</label>
          </div>

          <div className='inputgrp'>
            <label htmlFor='Phone'> Phone</label>
            <span>:</span>
            <label>{userData.phone}</label>
          </div>
         
        </div>
      </div>
      
    </div>
  </div>
  )
}

export default ProfileView
