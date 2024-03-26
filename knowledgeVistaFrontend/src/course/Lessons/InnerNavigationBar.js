import React from "react";
import { useState } from "react";
import { Link } from "react-router-dom";
import "./Style.css";

const InnerNavigationBar = ({ courseName, handleLinkClick }) => {
  //for reference visit  -->https://getbootstrap.com/docs/4.0/components/navbar/ under Supported Content you may find
  const [activeLink, setActiveLink] = useState("courseDetails");
  const handleLinkClickWithActiveLink = (linkname) => {
    handleLinkClick(linkname);
    setActiveLink(linkname);
  };
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <a className="navbar-brand" href="#">
        <h3>{courseName}</h3>
      </a>
      <button
        className="navbar-toggler"
        type="button"
        data-toggle="collapse"
        data-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span className="navbar-toggler-icon"></span>
      </button>

      <div className="collapse navbar-collapse  " id="navbarSupportedContent">
        <ul className="navbar-nav font-weight-bold ">
          <li className="nav-item active">
            <Link
              className={`nav-link ${
                activeLink === "courseDetails" ? "border-bottom" : ""
              }`}
              to="#"
              onClick={() => handleLinkClickWithActiveLink("courseDetails")}
            >
              Course Details
            </Link>
          </li>
          <li className="nav-item active">
            <Link
              className={`nav-link ${
                activeLink === "Lesson" ? "border-bottom" : ""
              }`}
              to="#"
              onClick={() => handleLinkClickWithActiveLink("Lesson")}
            >
              Lessons
            </Link>
          </li>
          <li className="nav-item active">
            <Link
              className={`nav-link ${
                activeLink === "AddLesson" ? "border-bottom" : ""
              }`}
              to="#"
              onClick={() => handleLinkClickWithActiveLink("AddLesson")}
            >
              Add Lessons
            </Link>
          </li>
          <li className="nav-item active">
            <Link
              className="nav-link"
              //  className={`nav-link ${
              //   activeLink === "settings" ? "border-bottom" : ""}`}
              to="#"
              // onClick={() => handleLinkClick("settings")}
            >
              Settings
            </Link>
          </li>
          <li className="nav-item active">
            <Link
              to="#"
              onClick={() => handleLinkClickWithActiveLink("test")}
              className={`nav-link ${
                activeLink === "test" ? "border-bottom" : ""
              }`}
            >
               Test
            </Link>
          </li>

          <li className="nav-item active">
            <Link
              to="#"
              onClick={() => handleLinkClickWithActiveLink("createTest")}
              className={`nav-link ${
                activeLink === "createTest" ? "border-bottom" : ""
              }`}
            >
              Create Test
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default InnerNavigationBar;
