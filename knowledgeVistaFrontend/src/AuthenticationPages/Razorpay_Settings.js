import React, { useEffect, useState } from 'react';
import '../css/certificate.css';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Razorpay_Settings = () => {
  const MySwal = withReactContent(Swal);
  const [valid, setValid] = useState(true);
  const data=sessionStorage.getItem("type");
  const token=sessionStorage.getItem("token")
const [isnotFound,setisnotFound]=useState();
const[initialsave,setinitialsave]=useState(false);
  const[defaultsettings,setdefaultsettings]=useState({
    id:"",
    razorpay_key:"",
    razorpay_secret_key:""
  })
  const navigate=useNavigate();
  useEffect(() => {
    // data === "false" ? setValid(true) : setValid(false);
  
    const fetchpaymentsettings = async () => {
      try {
        const response = await axios.get(`${baseUrl}/api/getPaymentDetails`, {
          headers: {
            "Authorization": token
          }
        });
  
        if (response.status === 200) {
          const data = response.data;
          setdefaultsettings(data);
          setRazorpay_Key(data.razorpay_key || ''); // Set default empty string if not found
          setRazorpay_Secret_Key(data.razorpay_secret_key || '');
        } 
      } catch (error) {
        if (error.response) {
          if (error.response.status === 404) {
            setisnotFound(true);
            setinitialsave(true);
          } else if (error.response.status === 401) {
            window.location.href = "/unauthorized";
          }
        }
      }
    };
  
    fetchpaymentsettings();
  }, []); 
  
  const [errors, setErrors] = useState({
    Razorpay_Key: "",
    Razorpay_Secret_Key: ""
  });

  const [Razorpay_Key, setRazorpay_Key] = useState('');
  
  const changeRazorpay_KeyHandler = (event) => {
    const newValue = event.target.value;
    setRazorpay_Key(newValue); 
    if (newValue?.trim() !== '') {
      setErrors(prevErrors => ({
        ...prevErrors,
        Razorpay_Key: ""
      }));
    } else {
      setErrors(prevErrors => ({
        ...prevErrors,
        Razorpay_Key: "Please enter a valid Razorpay Key."
      }));
    }
  };

  const [Razorpay_Secret_Key, setRazorpay_Secret_Key] = useState('');
  const changeRazorpay_Secret_KeyHandler = (event) => {
    const newValue = event.target.value;
    setRazorpay_Secret_Key(newValue); 
    if (newValue.trim() !== '') {
      setErrors(prevErrors => ({
        ...prevErrors,
        Razorpay_Secret_Key: ""
      }));
    } else {
      setErrors(prevErrors => ({
        ...prevErrors,
        Razorpay_Secret_Key: "Please enter a valid Razorpay Secret Key."
      }));
    }
  };
  const Edit=(e)=>{
    e.preventDefault();
    setisnotFound(true);
    
    console.log(isnotFound)
  }

  const save = (e) => {
    e.preventDefault();
  
    const formData = new FormData();
    formData.append("razorpay_key",Razorpay_Key);
    formData.append("razorpay_secret_key",Razorpay_Secret_Key);
    const data = {
      razorpay_key: Razorpay_Key,
      razorpay_secret_key: Razorpay_Secret_Key
    };
    if(initialsave){

      axios.post(`${baseUrl}/api/Paymentsettings`, data, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token
        }
      })
    .then(response => {
      if (response.status === 200) {
        MySwal.fire({
          title: "Saved !",
          text: "Payment Details Saved Sucessfully" ,
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
      }else{
        MySwal.fire({
          icon: 'error',
          title: 'Some Error Occurred',
          text: error.data
        });
      }
    });
  }else{ 
    if(defaultsettings.id){

    axios.patch(`${baseUrl}/api/update/${defaultsettings.id}`,formData ,{
    headers:{
      "Authorization":token
      }
  })
  .then(response => {
    if (response.status=== 200) {
      MySwal.fire({
        title: "Updated",
        text: "Payment Details Saved Sucessfully" ,
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
    
  }}
    
  };


  const getsettings=( 
  <div className="contentinner">
    <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
  <div className='innerFrameforset '>
  <h2 className='mb-5'>Razorpay Settings</h2>
    <div >
      <div className='formgroup pt-4' >
        <div >
          <div className='inputgrp'>
            <label htmlFor='Razorpay_Key'>Razorpay Key <span className="text-danger">*</span></label>
            <span>:</span>
            <div>
            <input
              id='Razorpay_Key'
              placeholder='Razorpay_Key'
              value={Razorpay_Key}
              className={`form-control .form-control-sm  ${errors.Razorpay_Key && 'is-invalid'}`}
              onChange={changeRazorpay_KeyHandler}
              required
            />
            <div className="invalid-feedback">
              {errors.Razorpay_Key}
            </div>
            </div>
          </div>
          <br></br>
          <div className='inputgrp'>
            <label htmlFor='Razorpay_Secret_Key'>Razorpay Secret Key <span className="text-danger">*</span></label>
            <span>:</span>
            <div>
             <input
              id='Razorpay_Secret_Key'
              placeholder='Razorpay Secret Key'
              value={Razorpay_Secret_Key}
              className={`form-control .form-control-sm  ${errors.Razorpay_Secret_Key && 'is-invalid'}`}
              onChange={changeRazorpay_Secret_KeyHandler}
              required
            />
            <div className="invalid-feedback">
              {errors.Razorpay_Secret_Key}
            </div>
            </div>

          </div>
          
        </div>
      </div>
    </div>
    {valid?
      <div className='btngrp'>
        <button className='btn btn-primary' onClick={save}
          disabled={
            Object.values(errors).some(error => error !== "") || // Check for errors
            !Razorpay_Key.trim() || // Check if Razorpay_Key is empty
            !Razorpay_Secret_Key.trim() // Check if Razorpay_Secret_Key is empty
          }>
          Save</button>
      </div>:<div></div>
    }
  </div>
</div>)


const oldSettings =(<div className="contentinner">
  <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
<div className='innerFrameforset '>
<h2 className='mb-5'>Razorpay Settings</h2>
  <div >
    <div className='formgroup'>
   
      <div >
        <div className='inputgrp'>
          <label htmlFor='Razorpay_Key'>Razorpay Key </label>
          <span>:</span>
         <input
          className='disabledbox'
          readOnly
         value={defaultsettings ? defaultsettings.razorpay_key :""}/>
         
        </div>
        <br></br>
        <div className='inputgrp'>
          <label htmlFor='Razorpay_Secret_Key'>Razorpay Secret Key</label>
          <span>:</span>
         
          <input   
          className='disabledbox'
          readOnly
          value={defaultsettings ? defaultsettings.razorpay_secret_key : ""}
         />
              
          

         

        </div>
       
      </div>
    </div>
  </div>
  {valid?
      <div className='btngrp' >
        <button className='btn btn-primary' onClick={Edit}>Edit</button>
      </div>:<div></div>
      }
</div>
</div>)
  return (
    <div className="contentbackground">
      {isnotFound ? getsettings:oldSettings}
    </div>
  );
};

export default Razorpay_Settings;
