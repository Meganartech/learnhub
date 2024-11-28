import React from "react";
import DisplayCourses from "./DisplayCourses";
import { useNavigate } from "react-router-dom";
import ErrorBoundary from "../ErrorBoundary";
import DisplaysocialLogin from "./DisplaysocialLogin";
import SocialLoginKeysAdmin from "./SocialLoginKeysAdmin";
import Sitesettings from "./Sitesettings";

const SettingsComponent = () => {
  const navigate = useNavigate();
  return (
    <div>
    <div className="page-header"></div>
    <div className="card">
      <div className=" card-body">
      <div className="row">
      <div className="col-12">
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
        <h4 >Settings</h4>
        <ErrorBoundary>
          <DisplayCourses />
        </ErrorBoundary>
        <ErrorBoundary>
          <DisplaysocialLogin/>
        </ErrorBoundary>
      <ErrorBoundary>
        <Sitesettings/>
      </ErrorBoundary>
       
        </div>
        </div>
        </div>
      </div>
    </div>
  );
};

export default SettingsComponent;
