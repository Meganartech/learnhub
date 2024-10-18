import React from "react";
import DisplayCourses from "./DisplayCourses";
import MailSettings from "./MailSettings";
import { useNavigate } from "react-router-dom";
import DisplayName from "./DisplayName";
import ErrorBoundary from "../ErrorBoundary";

const SettingsComponent = () => {
  const navigate = useNavigate();
  return (
    <div className="contentbackground">
      <div className="contentinner">
        <div className="navigateheaders">
          <div
            onClick={() => {
              navigate(-1);
            }}
          >
            <i className="fa-solid fa-arrow-left"></i>
          </div>
          <div></div>
          <div
            onClick={() => {
              navigate(-1);
            }}
          >
            <i className="fa-solid fa-xmark"></i>
          </div>
        </div>
        <h2 style={{ textDecoration: "underline" }}>Settings</h2>
        <ErrorBoundary>
          <DisplayCourses />
        </ErrorBoundary>
        <ErrorBoundary>
          <MailSettings />
        </ErrorBoundary>
        
      </div>
    </div>
  );
};

export default SettingsComponent;
