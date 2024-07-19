import React, { useState } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import SlideBar from './SlideBar'
import NavBar from './NavBar'
import Footer from './Footer'

const Layout = ({searchQuery,handleSearchChange,setSearchQuery}) => {
  const [showSidebar,setShowSidebar]=useState(false); 
  const userRole = sessionStorage.getItem("role");
  const handleSidebarToggle = () => {
    setShowSidebar(!showSidebar);
  };
 const location=useLocation();
  const [activeLink, setActiveLink] =  useState(location.pathname);
  return (
    
            <div id="">          
            <NavBar  
                      showSidebar={showSidebar}
                    handleSidebarToggle={handleSidebarToggle}
                activeLink={activeLink}searchQuery={searchQuery}
                 handleSearchChange={handleSearchChange} 
                 setSearchQuery={setSearchQuery}/>

              <div id="wrappercenter">
               
                <div  id="sidebar" className={showSidebar?" displayit":" hide"}>
                <SlideBar
                handleSidebarToggle={handleSidebarToggle}
                activeLink={activeLink}
                setActiveLink={setActiveLink}
                />
                </div>  
                <div id="outlet" className='w-100'>
                 <Outlet/> 
                 </div>   
              </div>
              <Footer/>
            </div>
           
        

  )
}

export default Layout

