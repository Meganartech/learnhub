import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../api/utils"
import axios from 'axios';

const MailSettings = () => {
const[initialsave,setinitialsave]=useState(false);
  const MySwal = withReactContent(Swal); 
  const token=sessionStorage.getItem("token")
  const [isnotFound,setisnotFound]=useState(false);
  const navigate=useNavigate();
  const[settings,setsettings]=useState({
    hostname:"",
    port:"587",
    emailid:"",
    password:""
  })
  const[defaultsettings,setdefaultsettings]=useState({
    hostname:"",
    port:"",
    emailid:"",
    password:""
  })
  const[errors,seterrors]=useState({
    hostname:"",
    emailid:"",
    password:""
  })
  useEffect(() => {
    if(token){
      const fetchMailAccountSettings = async () => {
        try {
          const response = await axios.get(`${baseUrl}/get/mailkeys`, {
            headers: {
              "Authorization": token
            }
          });
          if (response.status === 200) {
            const data = response.data;
            
            setdefaultsettings(data);
            setsettings(data);
            
        } 
        } catch (error) {
          if (error.response) {
            if (error.response.status === 404) {
              setisnotFound(true);
              console.log("notfound",isnotFound)
              setinitialsave(true);
            } else if (error.response.status === 401) {
              window.location.href = "/unauthorized";
            }
          }
        }
      };
    
      fetchMailAccountSettings();
    }
    }, []); 
    const save =async (e) => {
      e.preventDefault();
      console.log("hi in save",initialsave);

      if(initialsave){
        console.log("hi in initial save");
  try{
      const response=await axios.post(`${baseUrl}/save/mailkeys`, settings, {
          headers: {
            'Authorization': token
          }
        })
      
        if (response.status === 200) {
          MySwal.fire({
            title: "Saved !",
            text: "Email Details Saved Sucessfully" ,
            icon: "success",
            confirmButtonText: "OK",
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.reload();
            }   });
          setisnotFound(false)
        } 
  }catch(error){
    console.log(error)
    MySwal.fire({
      icon: 'error',
      title: 'Some Error Occurred',
      text: "error occured"
    });
  }
     
    }else{ 
      if(defaultsettings.id){
        console.log("hi in default");
      axios.patch(`${baseUrl}/Edit/mailkeys`,settings ,{
      headers:{
        "Authorization":token
        }
    })
    .then(response => {
      if (response.status=== 200) {
        MySwal.fire({
          title: "Updated",
          text: "Email Details Saved Sucessfully" ,
          icon: "success",
          confirmButtonText: "OK",
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.reload();
          }   });
        setisnotFound(false)
      }
      
    })
    .catch(error => {
     if(error.response.status===401){
      window.location.href="/unauthorized"
     } else{
      MySwal.fire({
        icon: 'error',
        title: 'Some Error Occurred',
        text: error.data
      });
     } 
    });
      
    }
  
  }
      
    };


  const handleInputsChange=(e)=>{
const {name,value}=e.target;
setsettings((prev)=>({
    ...prev,
    [name]:value
}))
  }
  const Edit=(e)=>{
    e.preventDefault();
    setisnotFound(true);
  }

  const getinputs=(<div>
    <div className='navigateheaders'>
     <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
     <div></div>
     <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
     </div>
     <div className='innerFrameforset '>
 <h2 className='mb-5' style={{ textDecoration: "underline" }}>Mail Settings</h2>
   
     <div className='formgroup pt-4' >
      
         <div className='inputgrp'>
           <label htmlFor='hostname'>Mail Host Name<span className="text-danger">*</span></label>
           <span>:</span>
           <div>
           <input
             id='hostname'
             name='hostname'
             placeholder='Email Host Name'
             value={settings.hostname}
             className={`form-control .form-control-sm  ${errors.hostname && 'is-invalid'}`}
             onChange={handleInputsChange}
           />
           <div className="invalid-feedback">
             {errors.hostname}
           </div>
           </div>
         </div>

         <div className='inputgrp'>
           <label htmlFor='port'>Mail port Name<span className="text-danger">*</span></label>
           <span>:</span>
           <div>
           <input
             id='port'
             name='port'
             placeholder='Email port Name'
             value={settings.port}
             className={`form-control .form-control-sm  ${errors.port && 'is-invalid'}`}
             onChange={handleInputsChange}
           />
           <div className="invalid-feedback">
             {errors.port}
           </div>
           </div>
         </div>
         <div className='inputgrp'>
           <label htmlFor='emailid'>Email Id <span className="text-danger">*</span></label>
           <span>:</span>
           <div>
            <input
            name='emailid'
             id='emailid'
             placeholder='Email Id'
           value={settings.emailid}
           className={`form-control .form-control-sm  ${errors.emailid && 'is-invalid'}`}
           onChange={handleInputsChange}
           />
           <div className="invalid-feedback">
            {errors.emailid}
           </div>
           </div>

         </div>
         <div className='inputgrp'>
           <label htmlFor='password'>Password<span className="text-danger">*</span></label>
           <span>:</span>
           <div>
            <input
             id='password'
             name='password'
             placeholder='password '
             className={`form-control .form-control-sm  ${errors.password && 'is-invalid'}`}
             value={settings.password}
            onChange={handleInputsChange}
           />
           <div className="invalid-feedback">
             {errors.password}
           </div>
           </div>

         </div>
      
     </div>
  
 
     <div className='btngrp'>
       <button className='btn btn-primary' 
       onClick={save}
        >
         Save</button>
     </div>
   
 </div>
 </div>) 
   const defaultinputs=(
   <div>
    <div className='navigateheaders'>
     <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
     <div></div>
     <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
     </div>
     <div className='innerFrameforset '>
 <h2 className='mb-5' style={{ textDecoration: "underline" }} >Mail Settings</h2>
   
     <div className='formgroup pt-4' >
      
         <div className='inputgrp'>
           <label htmlFor='hostname'>Mail Host Name <span className="text-danger">*</span></label>
           <span>:</span>
           
           <input
             id='hostname'
             placeholder='Host Name'
             value={defaultsettings.hostname}
             readOnly
            className='disabledbox'
           />
         </div>

         <div className='inputgrp'>
           <label htmlFor='port'>Mail port Name <span className="text-danger">*</span></label>
           <span>:</span>
           
           <input
             id='port'
             placeholder='port Name'
             value={defaultsettings.port}
             readOnly
            className='disabledbox'
           />
         </div>
         <div className='inputgrp'>
           <label htmlFor='emailid'>Email Id <span className="text-danger">*</span></label>
           <span>:</span>
           
            <input
             id='emailid'
             placeholder='Email Id'
             className='disabledbox'
             readOnly
             value={defaultsettings.emailid}
           />
          

         </div>
         <div className='inputgrp'>
           <label htmlFor='password'>Password<span className="text-danger">*</span></label>
           <span>:</span>
            <input
             id='password'
              placeholder='password '
             value={defaultsettings.password}
             className='disabledbox'
             readOnly
           />
          

         </div>
      
     </div>
  
  
     <div className='btngrp' >
       <button className='btn btn-primary' onClick={Edit}>Edit</button>
     </div>
     
 </div>
 </div>
 )
  return (
    <div className="contentbackground">
    <div className='contentinner'>
    {isnotFound ? getinputs :defaultinputs }
    </div>
  
</div>
  )
}

export default MailSettings