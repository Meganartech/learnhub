
import React, { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom';
import baseUrl from '../api/utils';
import axios from 'axios';

const TrainerProfile = () => {
  const navigate=useNavigate();
  const token=sessionStorage.getItem("token");
  const role=sessionStorage.getItem("role");
  const [notfound,setnotfound]=useState();
    const [img, setimg] = useState();
    const {traineremail}=useParams();
    const [userData, setUserData] = useState({

       username:"",
       email:"",
       phone:"",
       skills:"",
       dob:"",
       
    countryCode:"",
       role:{
        roleName:"",
        roleId:""
       }
      });
          
      useEffect(() => {
        const fetchData = async () => {
          if(role==="ADMIN"){
          try {
            console.log(traineremail)
            const response = await axios.get(`${baseUrl}/student/admin/getTrainer/${traineremail}`,{
               headers: {
                'Authorization': token,
              },
            });
            if (response.status === 200) {
              const userData = response.data;
              setimg(`data:image/jpeg;base64,${userData.profile}`);
              setUserData(userData);
          } 
          } catch (error) {
            if(error.response && error.response.status===404){
              setnotfound(true);
            }else if(error.response && error.response.status===401){
              window.location.href = '/unauthorized';
            }
          }}
          
          else if(role==="TRAINER"){
           window.location.href="/unauthorized";
          }
        };
    
        fetchData();
      }, []);
    
  return (
    <div className='contentbackground'>
    <div className='contentinner'>
      <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate("/view/Trainer")}}><i className="fa-solid fa-xmark"></i></div>
      </div>
      {notfound ? (<h1 style={{textAlign:"center",marginTop:"250px"}}>No Trainer found with the email</h1>) : (

  <div className='innerFrame '>
    <h2 style={{textDecoration:"underline"}}> Trainer Profile</h2>
    <div className='mainform'>
      <div className='profile-picture'>
        <div className='image-group' >
          <img id="preview"  src={img} alt='profile' />
        </div>
        
      </div>

      <div className='formgroup' style={{backgroundColor:"#F2E1F5",padding:"10px",paddingLeft:"20px",borderRadius:"20px" }} >
        <div className='inputgrp2' >
          <label htmlFor='Name'> Name</label>
          <span>:</span>
          <label>
           {userData.username}</label>
        </div>
        <div className='inputgrp2'>
          <label htmlFor='email'> Email</label>
          <span>:</span>
          <label>
         {userData.email}</label>
        </div>

        <div className='inputgrp2'>
          <label htmlFor='dob'>Date of Birth</label>
          <span>:</span>
          <label>{userData.dob}</label>
        </div>
        <div className='inputgrp2'>
          <label htmlFor='skills'>Skills</label>
          <span>:</span>
          <label>{userData.skills}</label>
        </div>
        <div className='inputgrp2'>
          <label htmlFor='countrycode'> country code</label>
          <span>:</span>
          <label>{userData.countryCode}</label>
        </div>
        <div className='inputgrp2'>
          <label htmlFor='Phone'> Phone</label>
          <span>:</span>
          <label>{userData.phone}</label>
        </div>
        
        <div className='inputgrp2'>
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

export default TrainerProfile
