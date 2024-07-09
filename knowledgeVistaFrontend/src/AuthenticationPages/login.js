
import React, { useState, useEffect } from 'react';
import { Link } from "react-router-dom";
import login from "../images/login.png"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';

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
      const response = await axios.post(`${baseUrl}/login`, formData, {
        headers: {
          "Content-Type": "application/json",
        }
      });
      
      if (response.status === 200) {
        const data = response.data; 
        const jwtToken = data.token;
        const role = data.role;
        const userId = data.userid;
        const email = data.email;
        sessionStorage.setItem('token', jwtToken);
        sessionStorage.setItem('role', role);
        sessionStorage.setItem('userid', userId);
        sessionStorage.setItem('email', email);
    if(role==="SYSADMIN"){
      window.location.href = "/viewAll/Admins";
    }else{
       window.location.href = "/dashboard/course";
    }
      } 
    } catch (error) {
      if (error.response && error.response.status === 404 ){
        setErrors(prevErrors => ({
          ...prevErrors,
          username: "User not found"
        }));
      } else if (error.response && error.response.status === 401){
        const data = error.response.data ? error.response.data : "error occured";
       const message=data.message;
        if (message === 'Incorrect password') {
          setErrors((prevErrors) => ({
            ...prevErrors,
            password: 'Incorrect password',
          }));
        } else if (message === 'In Active') {
          MySwal.fire({
            title: 'In Active User!',
            text: `reason : ${data.Description}`,
            icon: 'error',
          });
        }
      } else {
        MySwal.fire({
          title: "Error Occured!",
          text: "An error occurred while logging in. Please try again later.",
          icon: "error",
        })
      }
    }
  };
  



  return (
    <form  onSubmit={handleSubmit}>
    <div className="login-container d-flex flex-wrap justify-content-center align-items-center"> {/* New class */}
      <div className="image-section card-body text-center ">
    <img 
          style={{ width: "90%", height: "95%" }}
          src={login}
          alt='boy-pic'
        />
        </div>
        <div className="form-section card-body  text-center"> {/* New class */}

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
          <Link className="user text-decoration-none ml-3" to="/adminRegistration">
           Register as Admin
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
