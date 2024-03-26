import React from "react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Component.css";

const SlideBar = ({ isToggled, setIsToggled }) => {
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
        <div className="sidebar-brand-text mx-3 text-dark">Knowledge Vista</div>
      </a>

      <hr className="sidebar-divider" />

      <li className="nav-item mt-4">
        <a
          className={activeLink === "/dashboard/course" ? "ActiveLink nav-link" : "nav-link text-muted"}
          href="#"
          onClick={() => handleClick("/dashboard/course")}
        >
          <i className={activeLink === "/dashboard/course" ? "fas fa-fw fa-tachometer-alt text-light" : "fas fa-fw fa-tachometer-alt text-muted"}></i>
          <span>Dashboard</span>
        </a>
      </li>

      {userRole === "USER" && (
        <li className="nav-item mt-4">
          <a
            className={activeLink === "/mycourses" ? "ActiveLink nav-link" : "nav-link text-muted"}
            href="#"
            onClick={() => handleClick("/mycourses")}
          >
            <i className={activeLink === "/mycourses" ? "fa-solid fa-book-open text-light" : "fa-solid fa-book-open text-muted"}></i>
            <span>MyCourses</span>
          </a>
        </li>
      )}

      {userRole === "ADMIN" && (
        <li className="nav-item mt-4">
          <a
            className={activeLink === "/course/admin/edit" ? "ActiveLink nav-link" : "nav-link text-muted"}
            href="#"
           
            onClick={() => handleClick("/course/admin/edit")}
          >
            <i className={activeLink === "/course/admin/edit" ? "fa-solid fa-book-open text-light" : "fa-solid fa-book-open text-muted"}></i>
            <span>Courses</span>
          </a>
     
        </li>
      )}
      {/* {userRole === "ADMIN" && (
      <li className="nav-item mt-4">
        <a
          className={activeLink === "/trainers" ? "ActiveLink nav-link" : "nav-link text-muted"}
          href="#"
          onClick={() => handleClick("/trainers")}
        >
          <i className={activeLink === "/trainers" ? "fa-solid fa-chalkboard-user text-light" : "fa-solid fa-chalkboard-user text-muted"}></i>
          <span>Trainers</span>
        </a>
      </li>
      )} */}
        {userRole === "ADMIN" && (
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
          {userRole === "USER" && (
        <li className="nav-item mt-4">
          <a
            className={activeLink === "/mycertificate" ? "ActiveLink nav-link" : "nav-link text-muted"}
            href="#"
            onClick={() => handleClick("/mycertificate")}
          >
            <i className={activeLink === "/mycertificate" ? "fa-solid fa-award text-light" : "fa-solid fa-award text-muted"}></i>
            <span>MyCertificates</span>
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
