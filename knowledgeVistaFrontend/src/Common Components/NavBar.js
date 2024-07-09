import React, { useEffect, useState } from "react";
import undraw_profile from "../images/profile.png";
import Swal from "sweetalert2";
import logo from "../images/logo.png"
import withReactContent from "sweetalert2-react-content";
import "./SlideBar"
import "../css/Component.css"
import baseUrl from "../api/utils";
import axios from "axios";
import bell from "../images/bell.png"
import Notification from "./Notification";

const NavBar = ({ setSearchQuery,searchQuery,handleSearchChange ,activeLink,handleSidebarToggle,showSidebar}) => {
  const [data,setdata]=useState({
    name:"",
    profileImage:null
  })
  const [isopen,setisopen]=useState(false);
  const[count,setcount]=useState(0);
  const token =sessionStorage.getItem("token");
 const role=sessionStorage.getItem("role");

  useEffect(() => {
    
    const fetchItems = async () => {
        try {
            const response = await axios.get(`${baseUrl}/Edit/profiledetails`, {
                headers: {
                    Authorization : token,
                },
            });

            if (response.status===200) {
                const data =  response.data;
                setdata(data);
            } 
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };

    fetchItems();
}, [token]);  
const fetchUnreadCount = async () => {
  try {
    const response = await axios.get(`${baseUrl}/unreadCount`, {
      headers: {
        Authorization: token,
      },
    });

    if (response.status === 200) {
      const data = response.data;
      setcount(data);
    }
  } catch (error) {
    console.error("Error fetching unread count:", error);
  }
};
useEffect(() => {



  // Fetch initially
  fetchUnreadCount();

  // Cleanup for interval
}, [count]); // Only re-run on token change

const handlemarkallasRead =async (notificationIds)=>{
  const markread=  await axios.post(`${baseUrl}/MarkAllASRead`, notificationIds, {
      headers: {
        Authorization: token,
      },
      
    });
    if(markread.status===200){

      fetchUnreadCount();
    }
 }

 

  const MySwal = withReactContent(Swal);
  const imageSource = data.profileImage ? `data:image/jpeg;base64,${data.profileImage}` : undraw_profile;
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
         if (!token) {
          console.error("Token not found");
          return;
        }
  
        try {
          const response = await axios.post(`${baseUrl}/logout`,{}, {
            headers: {
              Authorization: token 
            }
          });
  
          if (response.status===200) {
            sessionStorage.removeItem("token");
            localStorage.clear();
            window.location.href = "/login";
            return;
          }
         
        } catch (error) {
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
    <nav className="navbar .navbar-expand  navcolor topbar static-top navgrid ">
  
    
      <div className="gridnav">
  
      
      <div className="barhide mt-4" onClick={handleSidebarToggle}><i className={showSidebar?'fa-solid fa-bars-staggered':'fa-solid fa-bars'}></i></div> 

    {["/dashboard/course","/AssignedCourses", '/mycourses',"/course/admin/edit"].includes(activeLink) && (
      <div className="searchbar mt-2" style={{gridColumn:"2"}}>
    <i className="fa fa-search pt-1 pl-1 " aria-hidden="true"></i>
    
        <input
          className="searchinput"
          type="text"
          name="search"
          id="search"
          value={searchQuery}
          onChange={handleSearchChange}
          autoFocus
          placeholder="Search Course...."
        />
      
              {searchQuery && (  
            <i className="fa-solid fa-xmark pt-1"
            onClick={() => setSearchQuery('')}></i>)}
    </div>
    )}
   
<div className="navbar-nav ml-auto mt-3  " style={{gridColumn:"3"}}>
  <div className="nav-item dropdown no-arrow">
    <a
    onClick={()=>{setisopen(! isopen)}}
      href="#"
      
    >
      <span className="w-100"></span>
      <span className="notification-container">
        <div> <img
            className="img-profile rounded-circle borderimg"
            src={bell}
            alt="User Profile"
            width="40px"
            height="40px"
          />
        {count > 0 ?<span className="notification-count">{count}</span>:<></>}
        </div>
      </span>
    </a>
    
    </div>
</div>
 
      <ul className="navbar-nav ml-auto " style={{gridColumn:"4"}} >
        <li className="nav-item dropdown no-arrow ">
          
          <a
            className="nav-link dropdown-toggle profile"
            href="#"
            id="userDropdown "
            role="button"
            data-toggle="dropdown"
            aria-haspopup="true"
            aria-expanded="false"
          >
            <span id="spanid" ></span>
            <h5 id="profilename" className="username">{data.name.length > 20 ? data.name.substring(0, 20) : data.name}</h5>

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
            className="dropdown-menu dropdown-menu-left shadow animated--grow-in"
            aria-labelledby="userDropdown"
          >
          {role!=="SYSADMIN" &&<> <a className="dropdown-item" href="/course/dashboard/profile">
              <i className="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
              Profile
            </a>
            
            
            <div className="dropdown-divider"></div></> }
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
          </div>
          <div className="gridnav">
          <div></div>
          <div></div>
          <div></div>
          {isopen && <div>
        <Notification setisopen={setisopen} isopen={isopen} setcount={setcount} handlemarkallasRead={handlemarkallasRead}/>
      
    </div>}
    </div>
    </nav>
  );
};

export default NavBar;
