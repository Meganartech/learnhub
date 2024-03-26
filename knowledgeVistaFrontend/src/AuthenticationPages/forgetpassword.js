import React, { useState } from "react";
import { Link } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const MySwal = withReactContent(Swal);

const ForgetPassword = () => {
  const [isResetPassword, setIsResetPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [formData, setFormData] = useState({
    password: "",
    confirmPassword: "",
  });
  const [forgetPasswordFormData, setForgetPasswordFormData] = useState({
    email: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleForgetPasswordChange = (e) => {
    const { name, value } = e.target;
    setForgetPasswordFormData({
      ...forgetPasswordFormData,
      [name]: value,
    });
  };

  const handleResetPasswordSubmit = async (e) => {
    e.preventDefault();

    // Check if passwords match
    if (formData.password !== formData.confirmPassword) {
      Swal.fire({
        icon: "error",
        title: "Passwords do not match!",
      });
      return;
    }

    const formDataToSend = new FormData();
    formDataToSend.append("email", email);
    formDataToSend.append("password", formData.password);

    // If passwords match, proceed with reset password request
    try {
      const response = await fetch("http://localhost:8080/resetpassword", {
        method: "POST",
        body: formDataToSend,
      });

      if (response.ok) {
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
        Swal.fire({
          icon: "error",
          title: "User Not Found",
          text: "The provided email address is not registered.",
        });
      }
    } catch (error) {
      console.error("Error:", error);
    }
  };

  const handleForgetPasswordSubmit = async (e) => {
    e.preventDefault();
    try {
      const formDataToSend = new FormData();
      formDataToSend.append("email", forgetPasswordFormData.email);
      const response = await fetch("http://localhost:8080/forgetpassword", {
        method: "POST",
        body: formDataToSend,
      });

      if (response.ok) {
        setEmail(forgetPasswordFormData.email);
        setIsResetPassword(true);
      }
    } catch (error) {
      if (error.response && error.response.status === 404) {
        // Display error message if user not found
        Swal.fire({
          icon: "error",
          title: "User Not Found",
          text: "The provided email address is not registered.",
        });
      } else {
        // Handle other errors
        console.error("Error:", error.message);
      }
    }
  };

  const resetPasswordForm = (
    <form className="user" onSubmit={handleResetPasswordSubmit}>
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
          className="form-control form-control-lg"
          placeholder="New Password"
          autoComplete="new-password"
          value={formData.password}
          onChange={handleChange}
        />
      </div>
      <div className="form-outline mb-4">
        <input
          type="password"
          name="confirmPassword"
          id="confirmPassword"
          className="form-control form-control-lg"
          placeholder="Confirm Password"
          autoComplete="new-password"
          value={formData.confirmPassword}
          onChange={handleChange}
        />
      </div>
      <button className="btn btn-primary btn-lg btn-block" type="submit">
        Reset Password
      </button>
      <button className="btn btn-warning btn-lg btn-block">Cancel</button>
      <hr className="my-4" />
    </form>
  );
  

  const forgetPasswordForm = (
    <form className="user" onSubmit={handleForgetPasswordSubmit}>
      <h3 className="h4 text-gray-900 mb-4">Student Verification</h3>
      <div className="form-outline mb-4">
        <input
          type="text"
          name="email"
          id="username"
          className="form-control form-control-lg"
          placeholder="Enter Email Address..."
          autoComplete="username"
          autoFocus
          value={forgetPasswordFormData.email}
          onChange={handleForgetPasswordChange}
        />
      </div>
      <button className="btn btn-primary btn-lg btn-block" type="submit">
        Login
      </button>
      <Link className="btn btn-warning btn-lg btn-block" to="/">
        Cancel
      </Link>
      <hr className="my-4" />
    </form>
  );

  return (
    <section className="vh-100 vw-100" style={{ backgroundColor: "#508bfc" }}>
      <div className="container py-5 h-100">
        <div className="row d-flex justify-content-center align-items-center h-100">
          <div className="col-12 col-md-8 col-lg-6 col-xl-5">
            <div
              className="card shadow-2-strong"
              style={{ borderRadius: "1rem" }}
            >
              <div className="card-body p-5 text-center shadow-lg">
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
          </div>
        </div>
      </div>
    </section>
  );
};

export default ForgetPassword;
