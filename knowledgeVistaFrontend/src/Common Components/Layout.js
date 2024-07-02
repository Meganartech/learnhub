import React, { useState } from 'react'
import { Outlet } from 'react-router-dom'
import SlideBar from './SlideBar'
import NavBar from './NavBar'
import Footer from './Footer'

const Layout = ({searchQuery,handleSearchChange,setSearchQuery}) => {
    
  const userRole = sessionStorage.getItem("role");
  const [activeLink, setActiveLink] =  useState(localStorage.getItem('activeLink') ||(userRole==="ADMIN"?"admin/dashboard":"/dashboard/course"));
  return (
    <div >
            <div id="wrapper">          
                <SlideBar
                activeLink={activeLink}
                setActiveLink={setActiveLink}
                />    
              <div id="content-wrapper">
                <div id="content">
                
                    <NavBar  
                activeLink={activeLink}searchQuery={searchQuery} handleSearchChange={handleSearchChange} setSearchQuery={setSearchQuery}/>

                
                 <Outlet/>
                
                    </div>
                   
              </div>
              
            </div>
            <Footer/>
          </div>

  )
}

export default Layout
