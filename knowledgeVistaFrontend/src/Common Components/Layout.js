import React, { useEffect, useState, useRef } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';
import ErrorBoundary from "../ErrorBoundary.js";
import $ from "jquery";
const Layout = ({ searchQuery, handleSearchChange, setSearchQuery,filter,handleFilterChange }) => {
  const location = useLocation();
  const [activeLink, setActiveLink] = useState(location.pathname);
  return (
    <>
      <ErrorBoundary >  
    <Sidebar
    filter={filter}
    handleFilterChange={handleFilterChange}
  />
  </ErrorBoundary>
  <ErrorBoundary>  
  <Header 
        searchQuery={searchQuery}
        handleSearchChange={handleSearchChange}
        setSearchQuery={setSearchQuery}
    />
    </ErrorBoundary>
  <div className="pcoded-main-container">
    <div className="pcoded-content">
    <ErrorBoundary>  
<Outlet key={location.pathname}/>
</ErrorBoundary>
    </div>
    </div>
  </>
  );
};

export default Layout;
