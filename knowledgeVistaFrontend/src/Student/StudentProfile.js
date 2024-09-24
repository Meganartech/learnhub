import undraw_profile from "../images/profile.png"
import React, { useContext, useEffect, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import baseUrl from '../api/utils';
import axios from 'axios';
import errorimg from "../images/errorimg.png"
import { GlobalStateContext } from "../Context/GlobalStateProvider";
const StudentProfile = () => {
  
  const navigate=useNavigate();
    const token=sessionStorage.getItem("token");
  const role=sessionStorage.getItem("role");
  const [notfound,setnotfound]=useState(false);
  const { displayname } = useContext(GlobalStateContext);
  const location = useLocation();
  const [initialUserData, setInitialUserData] = useState(location.state?.user || null);
    const [img, setimg] = useState();
    const {studentemail}=useParams();
    const [userData, setUserData] = useState({

       username:"",
       email:"",
       phone:"",
       skills:"",
       dob:"",
       countryCode:"",
        roleName:"",
        profile:null,
      });

      useEffect(() => {
        const fetchData = async () => {
          if (role === "ADMIN" || role === "TRAINER") {
            try {
              let fetchedInitialUserData = initialUserData;
              
              // Fetch initialUserData if it's not available from location.state
              if (!fetchedInitialUserData) {
                const detailsRes = await axios.get(`${baseUrl}/details/${studentemail}`, {
                  headers: { Authorization: token },
                });
                fetchedInitialUserData = detailsRes.data;
                setInitialUserData(fetchedInitialUserData);
              }
              
              // Fetch additional user data
              if (fetchedInitialUserData) {
                const email = fetchedInitialUserData.email;
                const response = await axios.get(`${baseUrl}/student/admin/getstudent/${email}`, {
                  headers: { Authorization: token },
                });
      
                if (response.status === 200) {
                  const serverData = response.data;
      
                  // Merge initialUserData and serverData into userData
                  setUserData(prevData => {
                    const updatedData = { ...prevData, ...fetchedInitialUserData, ...serverData };
                    if (updatedData.profile) {
                      setimg(`data:image/jpeg;base64,${updatedData.profile}`);
                    }
                    return updatedData;
                  });
                }
              }
            } catch (error) {
              if (error.response) {
                if (error.response.status === 404) {
                  setnotfound(true);
                } else if (error.response.status === 401) {
                  window.location.href = '/unauthorized';
                }
              }
            }
          } else {
            window.location.href = "/unauthorized";
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
      <div onClick={()=>{navigate("/view/Students")}}><i className="fa-solid fa-xmark"></i></div>
      </div>
        {notfound ? (
        <h1 style={{textAlign:"center",marginTop:"250px"}}>No {displayname && displayname.student_name 
          ? displayname.student_name 
          : "Student" 
        } found with the email</h1>) : (
            <div className='innerFrame '>
              <h2 style={{textDecoration:"underline"}}> {displayname && displayname.student_name 
          ? displayname.student_name 
          : "Student" 
        } Profile</h2>
              <div className='mainform'>
                <div className='profile-picture'>
                  <div className='image-group' >
                    <img id="preview"  src={img ? img : undraw_profile}
                     onError={(e) => {
                e.target.src = errorimg; // Use the imported error image
              }}
               alt='profile' />
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
          <label htmlFor='Phone'> Phone</label>
          <span>:</span>
          <label>{userData.phone}</label>
        </div>
        <div className='inputgrp2'>
          <label htmlFor='countrycode'> country code</label>
          <span>:</span>
          <label>{userData.countryCode}</label>
        </div>
        <div className='inputgrp2'>
          <label htmlFor='role'>RoleName</label>
          <span>:</span>
         <label>{displayname && displayname.student_name 
          ? displayname.student_name 
          : "Student" 
        }</label> 


        </div>
      </div>
    </div>
    
  </div>)}
  </div>
</div>

  
  )
}

export default StudentProfile
