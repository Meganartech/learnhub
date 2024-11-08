import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import axios from 'axios';
import baseUrl from '../api/utils';
import googleicon from "../images/google.png"
const SocialLoginKeysAdmin = () => {
    const MySwal = withReactContent(Swal); 
    const token=sessionStorage.getItem("token")
    const [isnotFound,setisnotFound]=useState(false);
    const navigate=useNavigate();
  const[defaultkeys,setdefaultkeys]=useState({
    clientid:"",
    clientSecret:"",
    redirectUrl:"",
    provider:"",
  })

  const[errors,seterrors]=useState({
    clientid:"",
    clientSecret:"",
    redirectUrl:""
  })
  const[loginkeys,setloginkeys]=useState({
    clientid:"",
    clientSecret:"",
    redirectUrl:"",
    provider:"GOOGLE",
  })
  useEffect(() => {
    if(token){
      const fetchSocialLoginKeys = async () => {
        try {
          const response = await axios.get(`${baseUrl}/sysadmin/get/socialLoginKeys`, {
            headers:{
                "Authorization":token
            },
            params: {
             "Provider":"GOOGLE"
            }
          });
    
          if (response.status === 200) {
            const data = response.data;
            setdefaultkeys(data);
            setloginkeys(data)
        } 
        } catch (error) {
          if (error.response) {
            if (error.response.status === 404) {
              setisnotFound(true);
            } else if (error.response.status === 401) {
              window.location.href = "/unauthorized";
            }else{
              throw error
            }
          }
        }
      };
    
      fetchSocialLoginKeys();
    }
    }, []);
    const handleInputsChange=(e)=>{
      e.preventDefault();
      
      const{name,value}=e.target;
      let error=""
      switch(name){
        case 'clientid':
          error = value.length < 1 ? 'Please enter a Valid Client Id' : '';
          break;
        case 'clientSecret':
            error = value.length < 1 ? 'Please enter a Valid  Client Secret' : '';
          break;
          case 'redirectUrl':
            error = value.length < 1 ? 'Please enter a Valid  Redirect Url' : '';
          break;
      }
      seterrors(prevErrors => ({
        ...prevErrors,
        [name]: error
      }));
      setloginkeys(prevset=>({
        ...prevset,
        [name]:value
      }))
      }
    const Edit=(e)=>{
        e.preventDefault();
        setisnotFound(true);
      }
      const save =async (e) => {
        e.preventDefault();
        const requiredFields = ['clientid', 'clientSecret', 'redirectUrl'];
        let hasErrors=false
          requiredFields.forEach(field => {
            if (!loginkeys[field] || loginkeys[field].length === 0 || errors[field]) {
             hasErrors=true
              seterrors(prevErrors => ({
                ...prevErrors,
                [field]: !loginkeys[field] ? 'This field is required' : errors[field]
              }));
            }
          });
          if(hasErrors){
            return
          }
     
    try{
        const response=await axios.post(`${baseUrl}/sysadmin/save/SocialKeys`,loginkeys, {
            headers: {
              'Authorization': token
            }
          })
        
          if (response.status === 200) {
            MySwal.fire({
              title: "Saved !",
              text: response.data,
              icon: "success",
              confirmButtonText: "OK",
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }   });
            setisnotFound(false)
          } 
    }catch(error){
      // MySwal.fire({
      //   icon: 'error',
      //   title: 'Some Error Occurred',
      //   text: "error occured"
      // });
      throw error
    }
       
      
        
      
        
      };
      const oldinputs=(
      <div>
       
          <div className='innerFrameforset '>
         <h4 className='mb-2' style={{ textDecoration: "underline" }}>  Google Login Settings</h4>
        
          <div className='formgroup pt-4' >
           
              <div className='inputgrp'>
                <label htmlFor='clientid'>Google Cloud Client Id <span className="text-danger">*</span></label>
                <span>:</span>
                
                <input
                  id='clientid'
                  placeholder='Client Id'
                  value={defaultkeys.clientid}
                  readOnly
                 className='disabledbox'
                />
              </div>
              <div className='inputgrp'>
                <label htmlFor='clientSecret'>Google Cloud client Secret <span className="text-danger">*</span></label>
                <span>:</span>
                
                 <input
                  id='clientSecret'
                  placeholder='Client Secet' 
                  className='disabledbox'
                  readOnly
                  value={defaultkeys.clientSecret}
                />
               
    
              </div>
              <div className='inputgrp'>
                <label htmlFor='redirectUrl'>Google Cloud RedirectUrl<span className="text-danger">*</span></label>
                <span>:</span>
                 <input
                  id='redirectUrl'
                  value={defaultkeys.redirectUrl}
                  placeholder=' RedirectUrl'
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

      const EditInputs=(  <div>
       
         <div className='innerFrameforset '>
        <h4 className='mb-2' style={{ textDecoration: "underline" }}> Google Login Settings</h4>
       
         <div className='formgroup pt-4' >
          
             <div className='inputgrp'>
               <label htmlFor='clientid'>Google Cloud Client Id <span className="text-danger">*</span></label>
               <span>:</span>
               <div>
               <input
                 id='clientid'
                 placeholder='Client Id'
                 value={loginkeys.clientid}
                 name='clientid'
                onChange={handleInputsChange}
                className={`form-control .form-control-sm  ${errors.clientid && "is-invalid"}`}
               />
               <div className="invalid-feedback">{errors.clientid}</div>
               </div>
             </div>
             <div className='inputgrp'>
               <label htmlFor='clientSecret'>Google Cloud client Secret <span className="text-danger">*</span></label>
               <span>:</span>
               <div>
                <input
                name='clientSecret'
                 id='clientSecret'
                 placeholder='Client Secet' 
                 onChange={handleInputsChange}
                 value={loginkeys.clientSecret}
                 className={`form-control .form-control-sm  ${errors.clientSecret && "is-invalid"}`}
                 />
                 <div className="invalid-feedback">{errors.clientSecret}</div>
                 </div>
             </div>
             <div className='inputgrp'>
               <label htmlFor='redirectUrl'>Google Cloud RedirectUrl<span className="text-danger">*</span></label>
               <span>:</span>
               <div>
                <input
                 id='redirectUrl'
                 name='redirectUrl'
                 value={loginkeys.redirectUrl}
                 placeholder=' RedirectUrl'
                 onChange={handleInputsChange}
                 className={`form-control .form-control-sm  ${errors.redirectUrl && "is-invalid"}`}
                 />
                 <div className="invalid-feedback">{errors.redirectUrl}</div>
                 </div>
   
             </div>
          
         </div>
      
         
         <div className='btngrp' >
           <button className='btn btn-primary' onClick={save}>Save</button>
         </div>
         
     </div>
     </div>)
  return (
   <>
    {isnotFound ?EditInputs: oldinputs  }
    </>
  )
}


export default SocialLoginKeysAdmin