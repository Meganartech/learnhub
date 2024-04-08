// import React from "react";
// import { Link } from "react-router-dom";
// // import "@fortawesome/fontawesome-free/css/all.min.css";
// import { useState } from "react";
// import Swal from "sweetalert2";
// import withReactContent from "sweetalert2-react-content";
// const Login = () => {
//   const MySwal = withReactContent(Swal);
//   const [formData, setFormData] = useState({ username: "", password: "" });

//   const handleChange = (e) => {
//     const { name, value } = e.target;
//     setFormData((prevState) => ({
//       ...prevState,
//       [name]: value,
//     }));
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     try {
//         const response = await fetch("http://localhost:8080/login", {
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json",
//             },
//             body: JSON.stringify(formData),
//         });

//         if (response.ok) {
//             // Await the JSON response
//             const data = await response.json(); // Retrieve JSON data
//             const jwtToken = data.token;
//             const role = data.role;
//             const name = data.name;
//             const userId=data.userid;
//             const email=data.email;
//             const img=data.profileImage;

// //             const tokenParts = jwtToken.split('.');
// // const decodedPayload = JSON.parse(atob(tokenParts[1]));

// // // Extract the role from the decoded payload
// // const userRole = decodedPayload.role;

// // console.log('User role:', userRole);

//             // Store token, name, and role in session storage
//             sessionStorage.setItem("name", name);
//             sessionStorage.setItem('token', jwtToken);
//             sessionStorage.setItem('role', role);
//             sessionStorage.setItem('userid',userId);
//             sessionStorage.setItem('email',email);
//             sessionStorage.setItem("image",img);

          
//                 // Redirect to dashboard or home page
//                 window.location.href = "/dashboard/course";
         
//         } else if (response.status === 401) {
//             // Incorrect password
//             Swal.fire({
//                 title: "Error!",
//                 text: "Incorrect password",
//                 icon: "error",
//                 confirmButtonText: "OK",
//             });
//         } else if (response.status === 404) {
//             // User not found
//             Swal.fire({
//                 title: "Error!",
//                 text: "User not found",
//                 icon: "error",
//                 confirmButtonText: "OK",
//             });
//         }
//     } catch (error) {
//         console.error("An error occurred:", error);
//         // Display error message
//         Swal.fire({
//             title: "Error!",
//             text: "An error occurred while logging in. Please try again later.",
//             icon: "error",
//             confirmButtonText: "OK",
//         });
//     }
// };



//   return (
//     <form className="user" onSubmit={handleSubmit}>
//       <section className="vh-100 vw-100" style={{ backgroundColor: "#508bfc" }}>
//         <div className="container py-5 h-100">
//           <div className="row d-flex justify-content-center align-items-center h-100">
//             <div className="col-12 col-md-8 col-lg-6 col-xl-5">
//               <div
//                 className="card shadow-2-strong"
//                 style={{ borderradius: "1rem" }}
//               >
//                 <div className=" card-body p-5 text-center shadow-lg">
//                   <h3 className="h4 text-gray-900 mb-4">Sign in</h3>

//                   <div className="form-outline mb-4">
//                     <input
//                       type="text"
//                       name="username"
//                       id="username"
//                       value={formData.username}
//                       onChange={handleChange}
//                       className="form-control form-control-lg"
//                       placeholder="Username"
//                       autoComplete="username"
//                       autoFocus
//                     />
//                   </div>

//                   <div className="form-outline mb-4">
//                     <input
//                       type="password"
//                       name="password"
//                       id="password"
//                       value={formData.password}
//                       onChange={handleChange}
//                       className="form-control form-control-lg"
//                       placeholder="Password"
//                       autoComplete="current-password"
//                     />
//                   </div>

//                   <div className="mb-4 text-center">
//                     <Link
//                       className="user text-decoration-none"
//                       to="/forgot-password"
//                     >
//                       Forgot Password?
//                     </Link>
//                   </div>

//                   <button
//                     className="btn btn-primary btn-lg btn-block"
//                     type="submit"
//                   >
//                     Login
//                   </button>
//                   <Link className="btn btn-warning btn-lg btn-block" to="/">
//                     Cancel
//                   </Link>

//                   <hr className="my-4" />
//                 </div>
//               </div>
//             </div>
//           </div>
//         </div>
//       </section>
//     </form>
//   );
// };

// export default Login;
import React, { useState, useEffect } from 'react';
import { Link } from "react-router-dom";
// import "@fortawesome/fontawesome-free/css/all.min.css";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
const Login = () => {
  const MySwal = withReactContent(Swal);
  const [formData, setFormData] = useState({ username: "", password: "" });
  const [valid, setValid] = useState();
  const [Tag, setTag] = useState([]);
  const [isEmpty, setIsEmpty] = useState();
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  useEffect(() => {
    // fetchUsers();

    fetch('http://localhost:8080/api/v2/GetAllUser')
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
        setValid(response.data.valid);
      setIsEmpty(response.data.empty);
      console.log("Is empty: " + isEmpty); // Corrected from empty to isEmpty
      console.log("Valid: " + valid);
      console.log("--------------------------------------------------------------------");
      
      }
      return response.json();
    })
    .then(data => {
      setTag(data);
      setValid(data.valid);
      setIsEmpty(data.empty);
    })
    .catch(error => {
      console.error('Error fetching data:', error);
    });
  }, []);

  // const fetchUsers = async () => {
  //   try {
  //     const response = await fetch('http://localhost:8080/api/v2/GetAllUser');

  //     console.log(Tag);
  //     console.log("Is empty: " + isEmpty); // Corrected from empty to isEmpty
  //     console.log("Valid: " + valid);
  //     console.log("--------------------------------------------------------------------");
  //     // After setting the users state, check if the username exists
  //     // const userFound = response.data.find(userData => userData.username === user.username);
  //   } catch (error) {
  //     console.log('Error fetching users:', error);
  //   }

  // }


  const handleSubmit = async (e) => {
    e.preventDefault();
    // fetchUsers();
    try {
        const response = await fetch("http://localhost:8080/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formData),
        });

        if (response.ok && !isEmpty && valid ) {
            // Await the JSON response
            // fetchUsers();
            console.log("in the login")
            const data = await response.json(); // Retrieve JSON data
            const jwtToken = data.token;
            const role = data.role;
            const name = data.name;
            const userId=data.userid;
            const email=data.email;
            const img=data.profileImage;

//             const tokenParts = jwtToken.split('.');
// const decodedPayload = JSON.parse(atob(tokenParts[1]));

// // Extract the role from the decoded payload
// const userRole = decodedPayload.role;

// console.log('User role:', userRole);

            // Store token, name, and role in session storage
            sessionStorage.setItem("name", name);
            sessionStorage.setItem('token', jwtToken);
            sessionStorage.setItem('role', role);
            sessionStorage.setItem('userid',userId);
            sessionStorage.setItem('email',email);
            sessionStorage.setItem("image",img);

          
                // Redirect to dashboard or home page
                window.location.href = "/dashboard/course";
         
        } 
        else if (isEmpty) {
          alert("Licence required")
          window.location.href = "/License";
        } else if (!valid) {
    
          alert("Licence Expired")
          window.location.href = "/License";
        }
        else if (response.status === 401) {
            // Incorrect password
            Swal.fire({
                title: "Error!",
                text: "Incorrect password",
                icon: "error",
                confirmButtonText: "OK",
            });
        } else if (response.status === 404) {
            // User not found
            Swal.fire({
                title: "Error!",
                text: "User not found",
                icon: "error",
                confirmButtonText: "OK",
            });
        }
    } catch (error) {
        console.error("An error occurred:", error);
        // Display error message
        Swal.fire({
            title: "Error!",
            text: "An error occurred while logging in. Please try again later.",
            icon: "error",
            confirmButtonText: "OK",
        });
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
                style={{ borderradius: "1rem" }}
              >
                <div className=" card-body p-5 text-center shadow-lg">
                  <h3 className="h4 text-gray-900 mb-4">Sign in</h3>

                  <div className="form-outline mb-4">
                    <input
                      type="text"
                      name="username"
                      id="username"
                      value={formData.username}
                      onChange={handleChange}
                      className="form-control form-control-lg"
                      placeholder="Username"
                      autoComplete="username"
                      autoFocus
                    />
                  </div>

                  <div className="form-outline mb-4">
                    <input
                      type="password"
                      name="password"
                      id="password"
                      value={formData.password}
                      onChange={handleChange}
                      className="form-control form-control-lg"
                      placeholder="Password"
                      autoComplete="current-password"
                    />
                  </div>

                  <div className="mb-4 text-center">
                    <Link
                      className="user text-decoration-none"
                      to="/forgot-password"
                    >
                      Forgot Password?
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
            </div>
          </div>
        </div>
      </section>
    </form>
  );
};

export default Login;
