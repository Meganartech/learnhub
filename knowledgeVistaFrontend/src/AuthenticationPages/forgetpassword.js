import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import login from "../images/login.png"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../api/utils";
import axios from "axios";
import logo from "../images/logo.png"
const MySwal = withReactContent(Swal);

const ForgetPassword = () => {
  
  const navigate = useNavigate();
  const [isResetPassword, setIsResetPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [formData, setFormData] = useState({
    password: "",
    confirmPassword: "",
  });
  const [forgetPasswordFormData, setForgetPasswordFormData] = useState({
    email: "",
  });
  // State variables for errors
  const [passwordError, setPasswordError] = useState("");
  const [confirmPasswordError, setConfirmPasswordError] = useState("");
  const [emailError, setEmailError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    // Update form data
    setFormData({
      ...formData,
      [name]: value,
    });
    // Reset error message for the field being changed
    if (name === "password") {
      setPasswordError(value.length < 6 ? "Password must be at least 6 characters long" : "");
    } else if (name === "confirmPassword") {
      setConfirmPasswordError(value !== formData.password ? "Passwords do not match" : "");
    }
  };

  const handleForgetPasswordChange = (e) => {
    const { name, value } = e.target;
    // Update forget password form data
    setForgetPasswordFormData({
      ...forgetPasswordFormData,
      [name]: value,
    });
    // Reset email error when changing email field
    setEmailError(/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? "" : "Please enter a valid email address");
  };

  const handleResetPasswordSubmit = async (e) => {
    e.preventDefault();
    // Check if passwords match and have minimum length
    if (formData.password !== formData.confirmPassword) {
      setConfirmPasswordError("Passwords do not match");
      return;
    }
    if (formData.password.length < 6) {
      setPasswordError("Password must be at least 6 characters long");
      return;
    }

    const formDataToSend = new FormData();
    formDataToSend.append("email", email);
    formDataToSend.append("password", formData.password);

    // If passwords match and have minimum length, proceed with reset password request
    try {
      const response = await axios.post(`${baseUrl}/resetpassword`,formDataToSend);

      if (response.status===200) {
        MySwal.fire({
          title: "Success",
          text: "Your password has been reset successfully!",
          icon: "success",
          confirmButtonText: "OK",
        }).then((result) => {
          if (result.isConfirmed) {
            // Redirect to login page
            window.location.href = "/login";
          }
        });
      } else if (response.status === 404) {
        setEmailError("User not found");
      }
    } catch (error) {
      MySwal.fire({
        title: "error",
        text: error,
        icon: "error",
        confirmButtonText: "OK",
      }).then((result) => {
        if (result.isConfirmed) {
          // Redirect to login page
          window.location.href = "/login";
        }
      });
    }
  };

  const handleForgetPasswordSubmit = async (e) => {
    e.preventDefault();
    try {
      const formDataToSend = new FormData();
      formDataToSend.append("email", forgetPasswordFormData.email);
      const response = await axios.post(`${baseUrl}/forgetpassword`,formDataToSend);

      if (response.status===200) {
        setEmail(forgetPasswordFormData.email);
        setIsResetPassword(true);
      } 
    } catch (error) {
      if (error.response && error.response.status === 404) {
        setEmailError("User not found");
    } else{
      MySwal.fire({
        title: "error",
        text: error.message,
        icon: "error",
        confirmButtonText: "OK",
      }).then((result) => {
        if (result.isConfirmed) {
          // Redirect to login page
          window.location.href = "/login";
        }
      });
    }
      }
    };


  // Button disabling logic
  const isResetButtonDisabled = !email || emailError || !formData.password || !formData.confirmPassword || passwordError || confirmPasswordError;
  const isForgetButtonDisabled = !forgetPasswordFormData.email || emailError;

  const resetPasswordForm = (
    <form  onSubmit={handleResetPasswordSubmit}>
      <h3 className="h4 text-gray-900 mb-4">Change Password</h3>
      <div className="form-outline mb-4">
        <input
          type="text"
          name="email"
          id="username"
          className="form-control form-control-lg"
          placeholder="Username"
          value={email}
          autoComplete="username"
          readOnly
        />
      </div>
      <div className="form-outline mb-4">
        <input
          type="password"
          name="password"
          id="password"
          className={`form-control form-control-lg ${passwordError && 'is-invalid'}`}
          placeholder="New Password"
          autoComplete="new-password"
          value={formData.password}
          onChange={handleChange}
          required
        />
        {passwordError && <div className="invalid-feedback">{passwordError}</div>}
      </div>
      <div className="form-outline mb-4">
        <input
          type="password"
          name="confirmPassword"
          id="confirmPassword"
          className={`form-control form-control-lg ${confirmPasswordError && 'is-invalid'}`}
          placeholder="Confirm Password"
          autoComplete="new-password"
          value={formData.confirmPassword}
          onChange={handleChange}
          required
        />
        {confirmPasswordError && <div className="invalid-feedback">{confirmPasswordError}</div>}
      </div>
      <button className="btn btn-primary btn-lg btn-block" type="submit" disabled={isResetButtonDisabled}>
        Reset Password
      </button>
      <button className="btn btn-warning btn-lg btn-block" onClick={()=>{navigate("/login")}}>Cancel</button>
      <hr className="my-4" />
    </form>
  );

  const forgetPasswordForm = (
    <form  onSubmit={handleForgetPasswordSubmit}>
      <h3 className="h4 text-gray-900 mb-4">Student Verification</h3>
      <div className="form-outline mb-4">
        <input
          type="text"
          name="email"
          id="username"
          className={`form-control form-control-lg ${emailError ? 'is-invalid' : ''}`}
          placeholder="Enter Email Address..."
          autoComplete="username"
          autoFocus
          value={forgetPasswordFormData.email}
          onChange={handleForgetPasswordChange}
          required
        />
        {emailError && <div className="invalid-feedback">{emailError}</div>}
      </div>
      <button className="btn btn-primary btn-lg btn-block" type="submit" disabled={isForgetButtonDisabled}>
        Verify 
      </button>
      <button className="btn btn-warning btn-lg btn-block" onClick={()=>{navigate("/login")}}>
        Cancel
      </button>
      <hr className="my-4" />
    </form>
  );

  return (
    <div className="login-container d-flex flex-wrap justify-content-center align-items-center"> 
      <div className=" image-section card-body  text-center ">
        <img 
          style={{ width: "100%", height: "100%" }}
          src={login} 
          alt='boy-pic'
        />
      </div>
      <div className="form-section card-body text-center ">
      <img style={{width:"200px",height:"200px"}} src={logo}/>
        {isResetPassword ? resetPasswordForm : forgetPasswordForm}
        {!isResetPassword && (
          <div className="text-center">
            <Link className="small" to="/login">
              Go to Login page!
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default ForgetPassword;
