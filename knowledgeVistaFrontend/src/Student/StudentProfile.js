
import { error } from 'jquery';
import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom';
const StudentProfile = () => {
    const token=sessionStorage.getItem("token");
  const role=sessionStorage.getItem("role");
  const [notfound,setnotfound]=useState(false);
    const [img, setimg] = useState();
    const {studentemail}=useParams();
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
          if(role==="ADMIN" ||role==="TRAINER"){
          try {
            const response = await fetch(`http://localhost:8080/student/admin/getstudent/${studentemail}`,{
              method:"GET",
               headers: {
                'Authorization': token,
              },
            });
            if (response.status === 200) {
                const userData = await response.json();
                setimg(`data:image/jpeg;base64,${userData.profile}`);
                setUserData(userData);
            } else if (response.status === 404) {
                setnotfound(true);
            } else if (response.status === 401) {
                // Unauthorized, redirect to login page
                window.location.href = '/unauthorized';
            } else {
                console.error('Error fetching user data. Status:', response.status);
            }} catch (error) {
            console.error('Error fetching user data:', error);
          }}
          
          else{
           window.location.href="/unauthorized";
          }
        };
    
        fetchData();
      }, []);
    

  return (
    <div className='contentbackground'>
    <div className='contentinner'>
        {notfound ? (<h1 style={{textAlign:"center",marginTop:"250px"}}>No Student found with the email</h1>) : (

  <div className='innerFrame '>
    <h2 style={{textDecoration:"underline"}}> Trainer Profile</h2>
    <div className='mainform'>
      <div className='profile-picture'>
        <div className='image-group' >
          <img id="preview"  src={img} alt='profile' />
        </div>
        
      </div>

      <div className='formgroup' style={{backgroundColor:"#F2E1F5",padding:"10px",paddingLeft:"20px",borderRadius:"20px" }} >
        <div className='inputgrp' >
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
        {userData.role ? <label>{userData.role.roleName}</label> : null}


        </div>
      </div>
    </div>
    
  </div>)}
  </div>
</div>

  
  )
}

export default StudentProfile
