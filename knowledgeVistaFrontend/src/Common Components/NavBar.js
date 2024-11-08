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
import { useLocation } from "react-router-dom";
 import errorimg from "../images/errorimg.png" // Use the imported error image


const NavBar = ({ setSearchQuery,searchQuery,handleSearchChange ,handleSidebarToggle,showSidebar,navbarref}) => {
  const islogedin=sessionStorage.getItem("token")!==null;
  const location = useLocation();
  const [data,setdata]=useState({
    name:"",
    profileImage:null
  })
  const [isopen,setisopen]=useState(false);
  const[count,setcount]=useState(0);
  const token =sessionStorage.getItem("token");
 const role=sessionStorage.getItem("role");

  useEffect(() => {
    
    const cachedData = sessionStorage.getItem("profileData");

  if (cachedData) {
    setdata(JSON.parse(cachedData));
   

  } else {
    // Fetch data from API if not cached
    const fetchItems = async () => {
      try {
        if (token) {
          const response = await axios.get(`${baseUrl}/Edit/profiledetails`, {
            headers: {
              Authorization: token,
            },
          });

          if (response.status === 200) {
            const fetchedData = response.data;
            setdata(fetchedData);

            // Cache the data in sessionStorage
            sessionStorage.setItem("profileData", JSON.stringify(fetchedData));
          }
        }
      } catch (error) {
        console.error("Error fetching data:", error);
        throw error
      }
    };

    fetchItems();
  }
}, []);  
const fetchUnreadCount = async () => {
  try {
    if(role!=="SYSADMIN" &&token) {
    const response = await axios.get(`${baseUrl}/unreadCount`, {
      headers: {
        Authorization: token,
      },
    });

    if (response.status === 200) {
      const data = response.data;
      setcount(data);
    }
  }
  } catch (error) {
    console.error("Error fetching unread count:", error);
    throw error
  }
};
useEffect(() => {


  // Fetch initially
  fetchUnreadCount();

  // Cleanup for interval
}, [count]); // Only re-run on token change

const handlemarkallasRead =async (notificationIds)=>{
  try{
  const markread=  await axios.post(`${baseUrl}/MarkAllASRead`, notificationIds, {
      headers: {
        Authorization: token,
      },
      
    });
    if(markread.status===200){

      fetchUnreadCount();
    }
  }catch (error) {
    console.error("Error fetching unread count:", error);
    throw error
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
            sessionStorage.clear(); 
            localStorage.clear();
            window.location.href = "/login";
            return;
          }
         
        } catch (error) {
          // MySwal.fire({
          //   title: "Error!",
          //   text: "An error occurred while logging out. Please try again later.",
          //   icon: "error",
          //   confirmButtonText: "OK",
          // });
          throw error
        }
      }
    });
  };
  
  

  return (
    <nav className="navbar .navbar-expand  navcolor topbar static-top navgrid ">
  
    
      <div className="gridnav">
      <a href="#" className="logodesign logodisphide ">
        <div className='logoicon'>
          <img src={logo} alt='logo'/> 
        <div className="logoText">LearnHub</div>
         
        </div>
     
      </a>
      
     {islogedin && <div className="barhide " ref={navbarref} onClick={handleSidebarToggle}><i className={showSidebar?'fa-solid fa-bars-staggered':'fa-solid fa-bars'}></i></div> 
}
     {["/","/dashboard/course","/AssignedCourses", '/mycourses',"/course/admin/edit"].includes(location.pathname) && (
      <div className="searchbar " style={{gridColumn:"3"}}>
    <i className="fa fa-search pt-1 pl-1 " aria-hidden="true"></i>
    
        <input
          className="searchinput"
          type="text"
          name="search"
          id="search"
          value={searchQuery}
          onChange={handleSearchChange}
          placeholder="Search Course...."
        />
      
              {searchQuery && (  
            <i className="fa-solid fa-xmark pt-1"
            onClick={() => setSearchQuery('')}></i>)}
    </div>
    )}
 {!islogedin && <div className="singinupbtn" style={{gridColumn:"4", width:"250px"}}>
            <button
              className="btn btn-primary mr-2"
              onClick={() => {
                window.location.href = "/login";
              }}
            >
              Sign In{" "}
            </button>
            <button
              className="btn btn-secondary"
              onClick={() => {
                window.location.href = "/StudentRegistration";
              }}
            >
              Sign up
            </button>
          </div>}
   
{islogedin &&<div className="navbar-nav  " style={{gridColumn:"4"}}>
  <div className="nav-item nav-link dropdown no-arrow">
    <a
    onClick={()=>{setisopen(! isopen)}}
      href="#"
      
    >
      {/* <span className="w-100"></span> */}
      <span className=" notification-container">
        <div > 
          <img
            className="img-profile rounded-circle borderimg"
            src={bell}
            id="noti"
            alt="User Profile"
            
          />
        {count > 0 ?<span className="notification-count">{count}</span>:<></>}
        </div>
      </span>
    </a>
    
    </div>
</div>}
 
     {islogedin && <ul className="navbar-nav " style={{gridColumn:"5"}} >
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
              onError={(e) => {
                e.target.src = errorimg; // Use the imported error image
              }}
              id="profi"
              alt="User Profile"
            />
          </a>
        
          <div
          
            className="dropdown-menu  shadow animated--grow-in absoluteleft"
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
          </ul>}
          </div>
      
          
       { islogedin && isopen && 
          <div style={{width:"100%" , display:"flex",justifyContent:"flex-end"}}>
        <Notification setisopen={setisopen} isopen={isopen} setcount={setcount} handlemarkallasRead={handlemarkallasRead}/>
        </div> }
 
    </nav>
  );
};

export default NavBar;
