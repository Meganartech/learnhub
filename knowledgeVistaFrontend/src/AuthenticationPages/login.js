
import React, { useState, useEffect } from 'react';
import { Link } from "react-router-dom";
import login from "../images/login.png"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import {  toast } from 'react-toastify';

const Login = () => {
  const MySwal = withReactContent(Swal);
  const [formData, setFormData] = useState({ username: "", password: "" });
  const [valid, setValid] = useState();
  const [Tag, setTag] = useState([]);
  const [isEmpty, setIsEmpty] = useState();
  
  const [errors, setErrors] = useState({username: "", password: "" });

  const handleChange = (e) => {
    const { name, value } = e.target;
    let error=""
    switch(name){
      case 'username':
        error = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? '' : 'Please enter a valid email address';
        break;
        case "password":
          error = value.length < 6 ? 'Password must be at least 6 characters long' : '';
          break;
          default:
            break;
    }
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: error,
    }));
  };


 


  const handleSubmit = async (e) => {
    e.preventDefault();
    if (Object.values(errors).some(error => error) || !formData.username || !formData.password) {
      return;
  }
    try {
        const response = await fetch("http://localhost:8080/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formData),
        });

        if (response.ok) {
            // Await the JSON response
            // fetchUsers();
            console.log("in the login")
            const data = await response.json(); // Retrieve JSON data
            const jwtToken = data.token;
            const role = data.role;
            const userId=data.userid;
            const email=data.email;
            sessionStorage.setItem('token', jwtToken);
            sessionStorage.setItem('role', role);
            sessionStorage.setItem('userid',userId);
            sessionStorage.setItem('email',email);
                // Redirect to dashboard or home page
                window.location.href = "/dashboard/course";
         
        } 
        else if (response.status === 401) {
          setErrors(prevErrors => ({
            ...prevErrors,
            password: "Incorrect password"
          }));
            // Incorrect password
           
        } else if (response.status === 404) {
          setErrors(prevErrors => ({
            ...prevErrors,
            username: "User not found"
          }));
           
        }
    } catch (error) {
        MySwal.fire({
          title: "Error Occured!",
          text: "An error occurred while logging in. Please try again later." ,
          icon: "error",
         
        })
    }
};



  return (
    <form className="user" onSubmit={handleSubmit}>
    <div className="d-flex flex-row justify-content-center align-items-center ">
    <div className="card-body  text-center pt-5">
    <img 
          style={{width:"700px",height:"700px"}}
          src={login} 
          alt='boy-pic'
        />
        </div>
      <div className="card-body p-5 m-5 text-center">

        <h3 className="h4 text-gray-900 mb-4">Sign in</h3>
  
        <div className="form-outline mb-4">
          <input
            type="text"
            name="username"
            id="username"
            value={formData.username}
            onChange={handleChange}
            className={`form-control form-control-lg ${errors.username && 'is-invalid'}`}
            placeholder="Username"
            autoComplete="username"
            autoFocus
            required
          />
          <div className="invalid-feedback">
            {errors.username}
          </div>
        </div>
  
        <div className="form-outline mb-4">
          <input
            type="password"
            name="password"
            id="password"
            value={formData.password}
            onChange={handleChange}
            className={`form-control form-control-lg ${errors.password && "is-invalid"}`}
            placeholder="Password"
            autoComplete="current-password"
            required
          />
          <div className="invalid-feedback">
            {errors.password}
          </div>
        </div>
  
        <div className="mb-4 text-center">
          <Link
            className="user text-decoration-none mr-3"
            to="/forgot-password"
          >
            Forgot Password?
          </Link> 
          <Link className="user text-decoration-none ml-3" to="/">
            New User?
          </Link>
        </div>
  
        <button
          className="btn btn-primary btn-lg btn-block"
          type="submit"
        >
          Login
        </button>
        <Link className="btn btn-warning btn-lg btn-block" to="/">
          Cancel
        </Link>
  
        <hr className="my-4" />
      </div>
    </div>
  </form>
  
  );
};

export default Login;
