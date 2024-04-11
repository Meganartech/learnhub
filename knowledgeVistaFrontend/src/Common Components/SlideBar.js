import React from "react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Component.css"

const SlideBar = ({ isToggled, setIsToggled }) => {
  const [isActive, setIsActive] = useState(false);

  const handleDropdownToggle = () => {
    setIsActive(!isActive);
  };

  const handleLinkClick = (link) => {
    setActiveLink(link);
    setIsActive(false); // Close dropdown after selecting an option
  };



  const userRole = sessionStorage.getItem("role");
  const navigate = useNavigate();
  const [activeLink, setActiveLink] = useState(localStorage.getItem('activeLink') || "/dashboard/course");

  const handleSetActiveLink = (linkName) => {
    setActiveLink(linkName);
    localStorage.setItem("activeLink",linkName)
  };
  const handleClick = (link) => {
    handleSetActiveLink(link);
    navigate(link);
  };

  return (
    <ul
      className={
        isToggled
          ? "navbar-nav   sidebar sidebar-dark accordion toggled"
          : "navbar-nav  sidebar sidebar-dark accordion"
      }
      id="accordionSidebar"
    >
      <a href="#" className="sidebar-brand d-flex align-items-center justify-content-center">
        <div className="sidebar-brand-icon rotate-n-15">
          <i className="fa-solid fa-book-open-reader text-dark"></i>
        </div>
        <div className="sidebar-brand-text mx-3 text-dark">Learn HUB</div>
      </a>

      <hr className="sidebar-divider" />





      {/* {userRole === "ADMIN"  && (
         <li className="nav-item mt-4">
         <a
           className={activeLink === "/admin/dashboard" ? "ActiveLink nav-link" : "nav-link text-muted"}
           href="#"
          
           onClick={() => handleClick("/admin/dashboard")}
         >
           <i className={activeLink === "/admin/dashboard" ? "fa-solid fa-gauge text-light" : "fa-solid fa-gauge text-muted"}></i>
           <span>Dashboard</span>
         </a>
    
       </li>
      )} */}
      
      <li className="nav-item mt-4">
        <a
          className={activeLink === "/dashboard/course" ? "ActiveLink nav-link" : "nav-link text-muted"}
          href="#"
          onClick={() => handleClick("/dashboard/course")}
        >
          <i className={activeLink === "/dashboard/course" ? "fa-solid fa-book-open text-light" : "fa-solid fa-book-open text-muted"}></i>
          <span>courses</span>
        </a>
      </li>
    

      {userRole === "USER" && (
        <li className="nav-item mt-4">
          <a
            className={activeLink === "/mycourses" ? "ActiveLink nav-link" : "nav-link text-muted"}
            href="#"
            onClick={() => handleClick("/mycourses")}
          >
            <i className={activeLink === "/mycourses" ? "fa-solid fa-book text-light" : "fa-solid fa-book text-muted"}></i>
            <span>My Courses</span>
          </a>
        </li>
      )}

      
{userRole === "USER" && (
        <li className="nav-item mt-4">
          <a
            className={activeLink === "/MyCertificateList" ? "ActiveLink nav-link" : "nav-link text-muted"}
            href="#"
            onClick={() => handleClick("/MyCertificateList")}
          >
            <i className={activeLink === "/MyCertificateList" ? "fa-solid fa-award text-light" : "fa-solid fa-award text-muted"}></i>
            <span>My Certificates</span>
          </a>
        </li>
      )}


      

      {userRole === "ADMIN"  && (
        <li className="nav-item mt-4">
          <a
            className={activeLink === "/course/admin/edit" ? "ActiveLink nav-link" : "nav-link text-muted"}
            href="#"
           
            onClick={() => handleClick("/course/admin/edit")}
          >
            <i className={activeLink === "/course/admin/edit" ? "fa-solid fa-book-open text-light" : "fa-solid fa-book-open text-muted"}></i>
            <span>Edit Courses</span>
          </a>
     
        </li>
      )}
   

   {userRole === "TRAINER" && (
      <li className="nav-item mt-4">
        <a
          className={activeLink === "/AssignedCourses" ? "ActiveLink nav-link" : "nav-link text-muted"}
          href="#"
          onClick={() => handleClick("/AssignedCourses")}
        >
          <i className={activeLink === "/AssignedCourses" ? "fa-solid fa-book text-light" : "fa-solid fa-book text-muted"}></i>
          <span>Assigned Courses</span>
        </a>
      </li>
      )}

      {userRole === "ADMIN" && (
      <li className="nav-item mt-4">
        <a
          className={activeLink === "/view/Trainer" ? "ActiveLink nav-link" : "nav-link text-muted"}
          href="#"
          onClick={() => handleClick("/view/Trainer")}
        >
          <i className={activeLink === "/view/Trainer" ? "fa-solid fa-chalkboard-user text-light" : "fa-solid fa-chalkboard-user text-muted"}></i>
          <span>Trainers</span>
        </a>
      </li>
      )}
        {(userRole === "ADMIN" || userRole === "TRAINER") && (
      <li className="nav-item mt-4">
        <a
          className={activeLink === "/view/Students" ? "ActiveLink nav-link" : "nav-link text-muted"}
          href="#"
          onClick={() => handleClick("/view/Students")}
        >
          <i className={activeLink === "/view/Students" ? "fa-solid fa-users text-light" : "fa-solid fa-users text-muted"}></i>
          <span>Students</span>
        </a>
      </li>
        )}

     {userRole === "ADMIN" && (
      <li className="nav-item mt-4">
        <a
          className={activeLink === "/certificate" ? "ActiveLink nav-link" : "nav-link text-muted"}
          href="#"
          onClick={() => handleClick("/certificate")}
        >
          <i className={activeLink === "/certificate" ? "fa-solid fa-award text-light" : "fa-solid fa-award text-muted"}></i>
          <span>Certificate</span>
        </a>
      </li>
        )}
        
{(userRole === "ADMIN" ) && (
        <li className="nav-item mt-4">
          <a
            className={activeLink === "/settings/payment" ? "ActiveLink nav-link" : "nav-link text-muted"}
            href="#"
           
            onClick={() => handleClick("/settings/payment")}
          >
            <i className={activeLink === "/settings/payment" ? "fa-solid fa-gear text-light" : "fa-solid fa-gear text-muted"}></i>
            <span>Payment settings</span>
          </a>
     
        </li>
      )}
     
      <hr className="sidebar-divider d-none d-md-block" />
      <div className="text-center d-none d-md-inline mt-4">
        <button
          className="rounded-circle border-0  bg-secondary text-white"
          onClick={() => setIsToggled((prevState) => !prevState)}
          id="sidebarToggle"
        ></button>
      </div>
    </ul>
  );
};

export default SlideBar;
