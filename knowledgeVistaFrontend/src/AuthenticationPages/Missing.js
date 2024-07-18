import React from 'react'
import { useNavigate } from 'react-router-dom'

const Missing = () => {
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
              <h1 className="display-1 text-danger">404</h1>
              <h2 className="display-4">Oops! Page not found</h2>
              <p className="lead">The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.</p>
              <p>Go back To <a href="/dashboard/course">Home</a></p>
          </div>
      </div>
      </div>
      
  )
}

export default Missing
