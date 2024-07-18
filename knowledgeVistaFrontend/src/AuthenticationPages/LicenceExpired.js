import React from 'react'
import { useNavigate } from 'react-router-dom'

const LicenceExpired = () => {
  const navigate=useNavigate();
  return (
    <div className='contentbackground' style={{height:"100vh"}}>
  <div className='contentinner' style={{height:"88vh"}}>
  <div className='navigateheaders'>
      <div onClick={()=>{navigate(-2)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-2)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
        <div className="text-center mt-5 ">
              <h1 className="display-1 text-danger">License Expired</h1>
              <h2 className="display-4">It Seems That your Product Licence was Expired So you Cannot Access THis Page</h2>
              <p className="lead">Contact Administrator to Upgrade Licence </p>
              
          </div>
      </div>
      </div>
      
  )
}

export default LicenceExpired