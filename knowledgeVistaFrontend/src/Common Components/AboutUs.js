import React from 'react'

const AboutUs = () => {
  return (
    <div className='contentbackground'>
  <div className='contentinner'>
  <div className='innerFrame'>
   
   <div className='mainform' style={{ gridTemplateColumns:"none"}}>
    
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
      
        
        
        </div>
       
      </div>
   
    </div>
  </div>
      
    </div>
  )
}

export default AboutUs
