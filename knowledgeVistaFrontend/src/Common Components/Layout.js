// import React, { useEffect, useState } from 'react'
// import { Outlet, useLocation } from 'react-router-dom'
// import SlideBar from './SlideBar'
// import NavBar from './NavBar'
// import Footer from './Footer'

// const Layout = ({searchQuery,handleSearchChange,setSearchQuery}) => {
//   const [showSidebar,setShowSidebar]=useState(false); 
//   const userRole = sessionStorage.getItem("role");
//   const handleSidebarToggle = () => {
//     setShowSidebar(!showSidebar);
//   };
 

//  const location=useLocation();
//   const [activeLink, setActiveLink] =  useState(location.pathname);
//   return (
    
//             <div id="">          
//             <NavBar 
//                       showSidebar={showSidebar}
//                     handleSidebarToggle={handleSidebarToggle}
//                 activeLink={activeLink}searchQuery={searchQuery}
//                  handleSearchChange={handleSearchChange} 
//                  setSearchQuery={setSearchQuery}/>

//               <div id="wrappercenter">
               
//                 <div  id="sidebar" className={showSidebar?" displayit":" hide"}>
//                 <SlideBar
//                 handleSidebarToggle={handleSidebarToggle}
//                 activeLink={activeLink}
//                 setActiveLink={setActiveLink}
//                 />
//                 </div>  
//                 <div id="outlet" className='w-100'>
//                  <Outlet/> 
//                  </div>   
//               </div>
//               <Footer/>
//             </div>
           
        

//   )
// }

// export default Layout

import React, { useEffect, useState, useRef } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import SlideBar from './SlideBar';
import NavBar from './NavBar';
import Footer from './Footer';

const Layout = ({ searchQuery, handleSearchChange, setSearchQuery }) => {
  const [showSidebar, setShowSidebar] = useState(false);
  const sidebarRef = useRef(null);
  const navbarref=useRef(null);
  const userRole = sessionStorage.getItem("role");

  const handleSidebarToggle = () => {
    setShowSidebar(!showSidebar);
  };

  const handleClickOutside = (event) => {
    if (sidebarRef.current && !sidebarRef.current.contains(event.target) && navbarref.current && !navbarref.current.contains(event.target)) {
      setShowSidebar(false);
    }
  };

  useEffect(() => {
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const location = useLocation();
  const [activeLink, setActiveLink] = useState(location.pathname);

  return (
    <div>
      <NavBar
       navbarref={navbarref}
        showSidebar={showSidebar}
        handleSidebarToggle={handleSidebarToggle}
        activeLink={activeLink}
        searchQuery={searchQuery}
        handleSearchChange={handleSearchChange}
        setSearchQuery={setSearchQuery}
      />
      <div id="wrappercenter">
        <div id="sidebar" ref={sidebarRef} className={showSidebar ? "displayit" : "hide"}>
          <SlideBar
            handleSidebarToggle={handleSidebarToggle}
            activeLink={activeLink}
            setActiveLink={setActiveLink}
          />
        </div>
        <div id="outlet" className='w-100'>
          <Outlet />
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default Layout;
