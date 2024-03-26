import React, { useState } from "react";
import { useParams } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const ResetPassword = () => {
  const MySwal = withReactContent(Swal);
  const { email } = useParams();
  const [formData, setFormData] = useState({
    password: "",
    confirmPassword: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
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
    formDataToSend.append("email",email);
    formDataToSend.append("password",formData.password);

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
       
      } else if(response.status===404) {
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

  return (
    <form className="user" onSubmit={handleSubmit}>
      <section className="vh-100 vw-100" style={{ backgroundColor: "#508bfc" }}>
        <div className="container py-5 h-100">
          <div className="row d-flex justify-content-center align-items-center h-100">
            <div className="col-12 col-md-8 col-lg-6 col-xl-5">
              <div
                className="card shadow-2-strong"
                style={{ borderRadius: "1rem" }}
              >
                <div className="card-body p-5 text-center shadow-lg">
                  <h3 className="h4 text-gray-900 mb-4">Change Password </h3>

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
                      placeholder="Password"
                      autoComplete="current-password"
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
                      placeholder="Re-type Password"
                      autoComplete="current-password"
                      value={formData.confirmPassword}
                      onChange={handleChange}
                    />
                  </div>

                  <button
                    className="btn btn-primary btn-lg btn-block"
                    type="submit"
                  >
                    Reset password
                  </button>
                  <button className="btn btn-warning btn-lg btn-block">
                    Cancel
                  </button>

                  <hr className="my-4" />
                  <div class="text-center">
                    <a class="small" href="/login">
                      Go to Login page!
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </form>
  );
};

export default ResetPassword;
