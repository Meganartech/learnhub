import React, { useState,useEffect } from 'react';
import "../css/certificate.css"


const Feedback = () => {

  
  const [Feedback, setFeedback] = useState('');
  const changeFeedback = (event) => {
    const newValue = event.target.value;
    setFeedback(newValue); // Updating the state using the setter function from useState
  };
  
  const save = (e) => {
    e.preventDefault();
    let SiteSetting = { feedback: Feedback};
    console.log('employee =>'+JSON.stringify(SiteSetting));
      
    const data = {
      feedback: Feedback
    };
    console.log("data")
    console.log(data)
    setFeedback('');
    // Send the category name to the server using a POST request
    fetch('http://localhost:8080/api/feedback', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify(data),
})

    
      
  };
  


  return (
    <div className='innerFrame'>
   
   <div className='mainform' style={{ gridTemplateColumns:"none"}}>
    
    
    
     {/* <div className='formgroup'> */}
     <div className="row justify-content-center">
     <h1 >Product Info</h1>
     </div>
     <br></br>
     <div style={{ paddingLeft:"5rem",paddingRight:"10rem"}}>
     <div className='row'>
        <div className='inputgrp col-6'>
          <label htmlFor='institutionName'>Product Name</label>
          <span style={{ marginTop:"18px"}}>:</span>
         <h4 style={{ marginTop:"24px"}}> Product name</h4>
        </div>
        <br></br>
        <div className='inputgrp col-6'>
          <label htmlFor='ownerName'>Hotfix</label>
          <span style={{ marginTop:"18px"}}>:</span>
         <h4 style={{ marginTop:"24px"}}>No </h4>
        </div>
        <br></br>
        </div>
        <br></br>
        <div className='row'>
        <div className='inputgrp col-6'>
          <label htmlFor='institutionName'>Company Name</label>
          <span style={{ marginTop:"18px"}}>:</span>
         <h4 style={{ marginTop:"24px"}}>Company Name</h4>
        </div>
        <br></br>
        <div className='inputgrp col-6'>
          <label htmlFor='ownerName'>Support Number</label>
          <span style={{ marginTop:"18px"}}>:</span>
         <h4 style={{ marginTop:"24px"}}>0000000000</h4>
        </div>
        <br></br>
        </div>
        <br></br>
        <div className='row'>
        <div className='inputgrp col-6'>
          <label htmlFor='institutionName'>Product Version</label>
          <span style={{ marginTop:"18px"}}>:</span>
         <h4 style={{ marginTop:"24px"}}>0000000000</h4>
        </div>
        <br></br>
        <div className='inputgrp col-6'>
          <label htmlFor='ownerName'>Contact E-Mail</label>
          <span style={{ marginTop:"18px"}}>:</span>
         <h4 style={{ marginTop:"24px"}}>0000000000</h4>
        </div>
        <br></br>
        </div>
        <br></br>
        <div className='row'>
        <div className='inputgrp col-6'>
          <label htmlFor='institutionName'>Feedback</label>
          <span style={{ marginTop:"18px"}}>:</span>
        
          <input
            id='Feedback'
            placeholder='Feedback'
            value={Feedback}
            onChange={changeFeedback}
            required
          />
        </div>
        <br></br>
        </div>
        
        
        </div>
       
      </div>
    {/* </div> */}
    <div className='btngrp' style={{height:"7rem"}}>
      <button className='btn btn-primary' style={{width:"8rem"}} onClick={save}>Save</button>
    </div>
    </div>
  );
};

export default Feedback;
