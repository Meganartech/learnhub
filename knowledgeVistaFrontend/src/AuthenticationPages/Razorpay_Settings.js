import React, { useEffect, useState } from 'react';
import '../css/certificate.css';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

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
  useEffect(() => {
    data==="false"?setValid(true):setValid(false)
    
    const fetchpaymentsettings = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/getPaymentDetails",{
          headers:{
          "Authorization":token
          }
        });
if(response.ok){
        const data = await response.json();
        setdefaultsettings(data);
        setRazorpay_Key(data.razorpay_key);
        setRazorpay_Secret_Key(data.razorpay_secret_key);
        
      }else if (response.status === 404) {
        setisnotFound(true);
        setinitialsave(true)
      }else if(response.status === 401){
        window.location.href="/unauthorized"
      }
      } catch (error) {
       console.error(error);
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
    setRazorpay_Key(newValue); // Update the state with the new value

    // Clear the error for Razorpay Key if the value is not empty
    if (newValue.trim() !== '') {
      setErrors(prevErrors => ({
        ...prevErrors,
        Razorpay_Key: ""
      }));
    } else {
      // Show error message if Razorpay Key is empty
      setErrors(prevErrors => ({
        ...prevErrors,
        Razorpay_Key: "Please enter a valid Razorpay Key."
      }));
    }
  };

  const [Razorpay_Secret_Key, setRazorpay_Secret_Key] = useState('');
  const changeRazorpay_Secret_KeyHandler = (event) => {
    const newValue = event.target.value;
    setRazorpay_Secret_Key(newValue); // Update the state with the new value

    // Clear the error for Razorpay Secret Key if the value is not empty
    if (newValue.trim() !== '') {
      setErrors(prevErrors => ({
        ...prevErrors,
        Razorpay_Secret_Key: ""
      }));
    } else {
      // Show error message if Razorpay Secret Key is empty
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
 

    // Send the data to the server
    const data = {
      razorpay_key: Razorpay_Key,
      razorpay_secret_key: Razorpay_Secret_Key
    };
    if(initialsave){

    fetch('http://localhost:8080/api/Paymentsettings', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        "Authorization":token
      },
    
      body: JSON.stringify(data),
    })
    .then(response => {
      if (response.ok) {
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
     

      } else if (response.status === 400) {
        response.text().then(errorMessage => {
          console.log(errorMessage);  });
      }else if(response.status === 401){
        window.location.href="/unauthorized"
      }  else {
        // Other error
        response.text().then(errorMessage => {
          console.log(errorMessage);  });
      }
    })
    .catch(error => {
      console.error('Error:', error);
      alert('An error occurred. Please try again later.');
    });
  }else{ 
    if(defaultsettings.id){

    fetch(`http://localhost:8080/api/update/${defaultsettings.id}`, {
    method: 'PATCH',
    headers:{
      "Authorization":token
      },
    body:formData,
  })
  .then(response => {
    if (response.ok) {
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
      
    } else if (response.status === 400) {
      response.text().then(errorMessage => {
        console.log(errorMessage);  });
    }else if(response.status === 401){
      window.location.href="/unauthorized"
    }
    else {
      // Other error
      response.text().then(errorMessage => {
        console.log(errorMessage);  });
    }
  })
  .catch(error => {
    console.error('Error:', error);
    alert('An error occurred. Please try again later.');
  });
    
  }}
    
  };


  const getsettings=( <div className="contentinner">
  <div className='innerFrame p-5'>
    <div className='mainform' style={{ gridTemplateColumns:"none"}}>
      <div className='formgroup'>
        <h2>Razorpay Settings</h2>
        <div style={{ paddingLeft:"5rem", paddingRight:"10rem"}}>
          <div className='inputgrp'>
            <label htmlFor='Razorpay_Key'>Razorpay Key <span className="text-danger">*</span></label>
            <span>:</span>
            <div>
            <input
              id='Razorpay_Key'
              placeholder='Razorpay_Key'
              value={Razorpay_Key}
              className={`form-control form-control-lg ${errors.Razorpay_Key && 'is-invalid'}`}
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
              className={`form-control form-control-lg ${errors.Razorpay_Secret_Key && 'is-invalid'}`}
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
<div className='innerFrame p-5'>
  <div className='mainform' style={{ gridTemplateColumns:"none"}}>
    <div className='formgroup'>
      <h2>Razorpay Settings</h2>
      <div style={{ paddingLeft:"5rem", paddingRight:"10rem"}}>
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
