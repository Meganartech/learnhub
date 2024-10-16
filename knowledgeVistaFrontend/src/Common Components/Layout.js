import React, { useEffect, useState, useRef } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import SlideBar from './SlideBar';
import NavBar from './NavBar';
import ErrorBoundary from '../ErrorBoundary';

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
    try {
      document.addEventListener('mousedown', handleClickOutside);
      return () => {
        document.removeEventListener('mousedown', handleClickOutside);
      };
    } catch (error) {
      console.error("Error in useEffect:", error);
    }
  }, []);
  

  const location = useLocation();
  const [activeLink, setActiveLink] = useState(location.pathname);

  return (
    <div>
         <ErrorBoundary>      
          <NavBar
       navbarref={navbarref}
        showSidebar={showSidebar}
        handleSidebarToggle={handleSidebarToggle}
        searchQuery={searchQuery}
        handleSearchChange={handleSearchChange}
        setSearchQuery={setSearchQuery}
      />
      </ErrorBoundary>
      <div id="wrappercenter">
        <div id="sidebar" ref={sidebarRef} className={showSidebar ? "displayit" : "hide"}>
        <ErrorBoundary>
          <SlideBar
            handleSidebarToggle={handleSidebarToggle}
            activeLink={activeLink}
            setActiveLink={setActiveLink}
          />
          </ErrorBoundary>
        </div>
        <div id="outlet" className='w-100'>
      
          <Outlet key={location.pathname}/>
        </div>
      </div>

    </div>
  );
};

export default Layout;
