import React from "react";
import undraw_profile from "../images/undraw_profile.jpg";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import "../css/Component.css"

const NavBar = ({ setSearchQuery,searchQuery,handleSearchChange}) => {
  const MySwal = withReactContent(Swal);
  const username=sessionStorage.getItem("name");
  const image=sessionStorage.getItem("image");
  const imageSource = image ? `data:image/jpeg;base64,${image}` : undraw_profile;
  const handleLogout = async () => {
    Swal.fire({
      title: "Logout",
      text: "Are you sure you want to logout?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Logout",
      cancelButtonText: "Cancel",
    }).then(async (result) => {
      if (result.isConfirmed) {
        const token = sessionStorage.getItem("token"); // Retrieve token from session storage
        if (!token) {
          console.error("Token not found");
          return;
        }
  
        try {
          // Send logout request to the server
          const response = await fetch("http://localhost:8080/logout", {
            method: "POST",
            headers: {
              "Authorization": token, // Pass the token in the Authorization header
              "Content-Type": "application/json", // Add any other necessary headers
            },
            // Add any necessary body data here
          });
  
          if (response.ok) {
            // Clear token from session storage after successful logout
            sessionStorage.removeItem("token");
            localStorage.clear();
            // Redirect to login page
            window.location.href = "/login";
            return;
          }
         
        } catch (error) {
          console.error("Logout failed:", error);
          // Show error message
          MySwal.fire({
            title: "Error!",
            text: "An error occurred while logging out. Please try again later.",
            icon: "error",
            confirmButtonText: "OK",
          });
        }
      }
    });
  };
  
  

  return (
    <nav className="navbar navbar-expand  navcolor topbar static-top navgrid ">
      {/* <!-- Sidebar Toggle (Topbar) --> */}
    <div className="searchgrid">
      <div className="searchbar">
    <i className="fa fa-search pt-1 pl-1" aria-hidden="true"></i>
    <input className="searchinput" 
            type="text"
            name="search"
            id="search"  
            value={searchQuery}
            onChange={handleSearchChange}
            autoFocus placeholder="search...."/>
              {searchQuery && (  
            <i class="fa-solid fa-xmark pt-1"
            onClick={() => setSearchQuery('')}></i>)}
    </div>
    
    </div> 

      <ul className="navbar-nav ml-auto">
        <li className="nav-item dropdown no-arrow ">
          <a
            className="nav-link dropdown-toggle profile"
            href="#"
            id="userDropdown"
            role="button"
            data-toggle="dropdown"
            aria-haspopup="true"
            aria-expanded="false"
          >
            <h5 className="username">{username}</h5>
            <img
              className="img-profile rounded-circle  borderimg "
              src={imageSource}
              alt="User Profile"
              width="100px"
              height="100px"
            />
          </a>
          {/* <!-- Dropdown - User Information --> */}
          <div
            className="dropdown-menu dropdown-menu-right shadow animated--grow-in"
            aria-labelledby="userDropdown"
          >
            <a className="dropdown-item" href="/course/dashboard/profile">
              <i className="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
              Profile
            </a>
            
            
            <div className="dropdown-divider"></div>
            <button
              className="dropdown-item"
              onClick={handleLogout}
              data-toggle="modal"
              data-target="#logoutModal"
            >
              <i className="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
              Logout
            </button>
          </div>
        </li>
      </ul>
    </nav>
  );
};

export default NavBar;
