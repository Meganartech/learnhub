import React, { useEffect, useState, useContext } from "react";
import baseUrl from "../api/utils.js";
import axios from "axios";
import { GlobalStateContext } from "../Context/GlobalStateProvider.js";
const Sidebar = ({filter,handleFilterChange}) => {
  const [isvalid, setIsvalid] = useState();
  const [isEmpty, setIsEmpty] = useState();
  const userRole = sessionStorage.getItem("role");
  const token = sessionStorage.getItem("token");
  const { displayname } = useContext(GlobalStateContext);
  useEffect(() => {
    const fetchData = async () => {
      try {
        if (userRole !== "SYSADMIN" && token) {
          const response = await axios.get(`${baseUrl}/api/v2/GetAllUser`, {
            headers: {
              Authorization: token,
            },
          });

          const data = response.data;
          setIsEmpty(data.empty);
          setIsvalid(data.valid);
          sessionStorage.setItem("LicenceVersion", data.productversion);
          const type = data.type;
          sessionStorage.setItem("type", type);
        }
      } catch (error) {
        if (error.response && error.response.status !== 200) {
          setIsEmpty(error.response.data.empty);
          console.error("Error fetching data:", error);
        }
        console.error("Error fetching data:", error);
      }
    };
    fetchData();
  }, []);

  // const handleClick = (link) => {
  //   if (userRole === "ADMIN" || userRole === "TRAINER") {
  //     if (
  //       (link === "/about" ||
  //         link === "/admin/dashboard" ||
  //         link === "/licenceDetails") &&
  //       isEmpty
  //     ) {
  //       setActiveLink(link);
  //       navigate(link);
  //     } else if (
  //       (link === "/about" ||
  //         link === "/admin/dashboard" ||
  //         link === "/licenceDetails") &&
  //       !isEmpty &&
  //       !isvalid
  //     ) {
  //       setActiveLink(link);
  //       navigate(link);
  //     } else if (!isEmpty && isvalid) {
  //       setActiveLink(link);
  //       navigate(link);
  //     }
  //   } else if (userRole === "USER" || userRole === "SYSADMIN") {
  //     setActiveLink(link);
  //     navigate(link);
  //   }
  // };

  return (
    <nav className="pcoded-navbar menu-light  ">
      <div className="navbar-wrapper">
        <div className="navbar-content scroll-div ">
          {/* Admin Sidebar */}
         
          {userRole === "ADMIN" && (<>
          <ul className="nav pcoded-inner-navbar ">
          
            <li className="nav-item pt-2">
              <a
                href="/admin/dashboard"
                className="nav-link has-ripple"
              >
                <span className="pcoded-micon">
                  <i className="feather icon-home"></i>
                </span>
                <span className="pcoded-mtext">Dashboard</span>
              </a>
            </li>

            <li className="nav-item pcoded-hasmenu">
              <a href="#!" className="nav-link">
                <span className="pcoded-micon">
                  <i className="feather icon-layout"></i>
                </span>
                <span className="pcoded-mtext">Courses</span>
              </a>
              <ul className="pcoded-submenu">
          
                <li className="view-course">
                  
                  <a
                    href="/course/admin/edit"
                   
                  ><i className="fa-solid fa-edit pr-2"></i>
                    Edit Courses
                  </a>
                  <ul className="toggle-list ">
  <li>
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={filter.paid}
            name="paid"
            onChange={() => {
              console.log("Paid checkbox toggled"); // Debug log
              handleFilterChange('paid'); // Trigger filter change for 'paid'
            }}
            className="mr-1"
          />
          <span className="checkbox-custom"></span>
          Paid
        </label>
      </li>
      <li>
        <label className="checkbox-label ">
          <input
            type="checkbox"
            checked={filter.unpaid}
            name="unpaid"
            onChange={() => {
              console.log("Unpaid checkbox toggled"); // Debug log
              handleFilterChange('unpaid'); // Trigger filter change for 'unpaid'
            }}
            className="mr-1"
          />
          <span className="checkbox-custom"></span>
          Free
        </label>
      </li>
  </ul>
                </li>

                <li  >
                  <a
                    href="/course/addcourse"
                   
                  >
                    <i className="fa-solid fa-file-circle-plus pr-2"></i>
                    Create course
                  </a>
                </li>


                <li className="view-course">
  <a href="/dashboard/course">
    <i className="fa-regular fa-eye pr-2"></i>
    View Course
  </a>
  <ul className="toggle-list" >
  <li>
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={filter.paid}
            name="paid"
            onChange={() => {
              console.log("Paid checkbox toggled"); // Debug log
              handleFilterChange('paid'); // Trigger filter change for 'paid'
            }}
            className="mr-1"
          />
          <span className="checkbox-custom"></span>
          Paid
        </label>
      </li>
      <li >
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={filter.unpaid}
            name="unpaid"
            onChange={() => {
              console.log("Unpaid checkbox toggled"); // Debug log
              handleFilterChange('unpaid'); // Trigger filter change for 'unpaid'
            }}
            className="mr-1"
          />
          <span className="checkbox-custom"></span>
          Free
        </label>
      </li>
  </ul>
</li>

              </ul>
            </li>
          
            <li className="nav-item pcoded-hasmenu">
              <a href="#!" className="nav-link ">
                <span className="pcoded-micon">
                  <i className="fa fa-gear"></i>
                </span>
                <span className="pcoded-mtext">Settings</span>
              </a>
              <ul className="pcoded-submenu">
                <li>
                  <a
                    href="/settings/viewsettings"
                   
                  ><i className="fa-solid fa-gears pr-2"></i>
                    General
                  </a>
                </li>
                <li>
                  <a
                    href="/settings/socialLogins"
                   
                  ><i className="fa-brands fa-google pr-2"></i>
                    Social Login 
                  </a>
                </li>
                <li>
                  <a href="/certificate" >
                  <i className="fa-solid fa-award pr-2"></i> Certificate
                  </a>
                </li>

                <li>
                  <a
                    href="/settings/mailSettings"
                  >
                   <i className="fa-solid fa-envelope pr-2"></i> Mail
                  </a>
                </li>
                <li>
                  <a
                    href="/zoom/settings"
                  >
                   <i className="fa-solid fa-video pr-2"></i> Zoom
                  </a>
                </li>
                <li>
                  <a
                    href="/settings/displayname"
                  >
                   <i className="fa-solid fa-users-gear"></i> Roles
                  </a>
                </li>
              </ul>
            </li>
            <li className="nav-item pcoded-hasmenu">
              <a href="#!" className="nav-link ">
                <span className="pcoded-micon">
                  <i className="fa fa-users"></i>
                </span>
                <span className="pcoded-mtext">People</span>
              </a>
              <ul className="pcoded-submenu">
            <li>
              <a
                href="/view/Trainer"
                className="nav-link "
              >
                <span className="pcoded-micon">
                <i className="fa-solid fa-person-chalkboard"></i>
                </span>
                <span className="pcoded-mtext">
                  {displayname && displayname.trainer_name
                    ? displayname.trainer_name
                    : "Trainers"}
                </span>
              </a>
            </li>
            <li >
              <a
                href="/view/Students"
                className="nav-link "
              >
                <span className="pcoded-micon">
                <i className="fa-solid fa-chalkboard-user"></i>
                </span>
                <span className="pcoded-mtext">
                  {displayname && displayname.student_name
                    ? displayname.student_name
                    : "Student"}
                </span>
              </a>
            </li>
            <li>
              <a
                href="/view/Approvals"
                className="nav-link "
              >
                <span className="pcoded-micon">
                <i className="fa-solid fa-person-circle-check"></i>
                </span>
                <span className="pcoded-mtext">
                 Approvals
                </span>
              </a>
            </li>
            </ul>
            </li>
          
            <li className="nav-item pcoded-hasmenu">
              <a href="#!" className="nav-link ">
                <span className="pcoded-micon">
                <i className="fa-solid fa-users-rectangle"></i>
                </span>
                <span className="pcoded-mtext">meeting</span>
              </a>
              <ul className="pcoded-submenu">
            <li >
              <a
                href="/meeting/calender"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-video"></i>
                </span>
                <span className="pcoded-mtext">My Meetings</span>
              </a>
            </li>
            <li >
              <a
                href="/meeting/Shedule"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-plus"></i>
                </span>
                <span className="pcoded-mtext">Shedule New</span>
              </a>
            </li>
         </ul>
         </li>
         <li className="nav-item pcoded-hasmenu">
              <a href="#!" className="nav-link ">
                <span className="pcoded-micon">
                <i className="fa-regular fa-credit-card"></i>
                </span>
                <span className="pcoded-mtext">payments</span>
              </a>
              <ul className="pcoded-submenu">
            <li >
              <a
                href="/payment/keys"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-gear"></i>
                </span>
                <span className="pcoded-mtext">payment Keys</span>
              </a>
            </li>
            <li >
              <a
                href="/payment/transactionHitory"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-clock-rotate-left"></i>
                </span>
                <span className="pcoded-mtext">Transactions</span>
              </a>
            </li>
          </ul>
          </li>

            <li className="nav-item">
              <a
                href="/licenceDetails"
                className="nav-link "
              >
                <span className="pcoded-micon">
                <i className="fa-solid fa-clipboard-check"></i>
                </span>
                <span className="pcoded-mtext">Licence</span>
              </a>
            </li>
            <li className="nav-item">
              <a
                href="/about"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-circle-info"></i>
                </span>
                <span className="pcoded-mtext">About us</span>
              </a>
            </li>
          </ul>

         </>
          )}
          {/* Admin Sidebar */}
          {/* Sysadmin Sidebar */}
          {userRole === "SYSADMIN" && (
            <ul className="nav pcoded-inner-navbar ">
          
              <li className="nav-item pt-2">
                <a
                  href="/viewAll/Admins"
                  className="nav-link "
                >
                  <span className="pcoded-micon">
                    <i className="fa-solid fa-user-tie"></i>
                  </span>
                  <span className="pcoded-mtext">
                  Admins
                  </span>
                </a>
              </li>
              <li className="nav-item">
                <a
                  href="/viewAll/Trainers"
                  className="nav-link "
                >
                  <span className="pcoded-micon">
                    <i className="fa-solid fa-chalkboard-user"></i>
                  </span>
                  <span className="pcoded-mtext">
                  Trainers
                  </span>
                </a>
              </li>
              <li className="nav-item">
                <a
                  href="/viewAll/Students"
                  className="nav-link "
                >
                  <span className="pcoded-micon">
                    <i className="fa-solid fa-user"></i>
                  </span>
                  <span className="pcoded-mtext">
                  Students
                  </span>
                </a>
              </li>
              <li className="nav-item">
                <a
                  href="/Affiliates"
                  className="nav-link "
                >
                  <span className="pcoded-micon">
                    <i className="fa-regular fa-handshake"></i>
                  </span>
                  <span className="pcoded-mtext">
                  Affiliates
                  </span>
                </a>
              </li>
            
              <li className="nav-item">
                <a
                  href="/view/SocialLogin"
                  className="nav-link "
                >
                  <span className="pcoded-micon">
                    <i className="fa-solid fa-users"></i>
                  </span>
                  <span className="pcoded-mtext">
                  Social Login
                  </span>
                </a>
              </li>

              <li className="nav-item">
                <a
                  href="/Zoomkeyupload"
                  className="nav-link "
                >
                  <span className="pcoded-micon">
                    <i className="fa-solid  fa-video"></i>
                  </span>
                  <span className="pcoded-mtext">
                 Zoom Keys
                  </span>
                </a>
              </li>


            <li className="nav-item">
              <a
                href="/licenceupload"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="feather icon-sidebar"></i>
                </span>
                <span className="pcoded-mtext">Licence</span>
              </a>
            </li>
            </ul>
          )}
          {/* Sysadmin Sidebar */}
            {/* Trainer Sidebar */}
           {( userRole === "TRAINER" )&&( 
            <ul className="nav pcoded-inner-navbar ">
          
            <li className="nav-item pt-2 view-course">
              <a
                href="/dashboard/course"
                className="nav-link has-ripple"
              >
                <span className="pcoded-micon">
                <i className="feather icon-layout"></i>
                </span>
                <span className="pcoded-mtext ">Courses</span>
              </a>
              <ul className="toggle-list pl-4">
  <li>
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={filter.paid}
            name="paid"
            onChange={() => {
              console.log("Paid checkbox toggled"); // Debug log
              handleFilterChange('paid'); // Trigger filter change for 'paid'
            }}
            className="mr-1"
          />
          <span className="checkbox-custom"></span>
          Paid
        </label>
      </li>
      <li>
        <label className="checkbox-label ">
          <input
            type="checkbox"
            checked={filter.unpaid}
            name="unpaid"
            onChange={() => {
              console.log("Unpaid checkbox toggled"); // Debug log
              handleFilterChange('unpaid'); // Trigger filter change for 'unpaid'
            }}
            className="mr-1"
          />
          <span className="checkbox-custom"></span>
          Free
        </label>
      </li>
  </ul>
            </li>
            <li className="nav-item ">
              <a
                href="/AssignedCourses"
                className="nav-link has-ripple"
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-book"></i>
                </span>
                <span className="pcoded-mtext">My Courses</span>
              </a>
     
            </li>
            <li className="nav-item pcoded-hasmenu">
              <a href="#!" className="nav-link ">
                <span className="pcoded-micon">
                  <i className="fa-solid fa-video"></i>
                </span>
                <span className="pcoded-mtext">Meeting</span>
              </a>
              <ul className="pcoded-submenu">
            <li >
              <a
                href="/meeting/calender"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-user-clock"></i>
                </span>
                <span className="pcoded-mtext">My Meetings</span>
              </a>
            </li>
            <li >
              <a
                href="/meeting/Shedule"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-plus"></i>
                </span>
                <span className="pcoded-mtext">Shedule New</span>
              </a>
            </li>
</ul>
</li>
           
            <li className="nav-item">
              <a
                href="/view/Students"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-users"></i>
                </span>
                <span className="pcoded-mtext">
                  {displayname && displayname.student_name
                    ? displayname.student_name
                    : "All Students"}
                </span>
              </a>
            </li>
            <li className="nav-item">
              <a
                href="/myStudents"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-chalkboard-user"></i>
                </span>
                <span className="pcoded-mtext">
                My  {displayname && displayname.student_name
                    ? displayname.student_name
                    : "My Students"}
                </span>
              </a>
            </li>
       
            
          
</ul>)}
 {/* Trainer Sidebar */}
  {/* User Sidebar */}
  {( userRole === "USER" )&&( 
            <ul className="nav pcoded-inner-navbar ">
          
            <li className="nav-item pt-2 view-course">
              <a
                href="/dashboard/course"
                className="nav-link has-ripple"
              >
                <span className="pcoded-micon">
                <i className="feather icon-layout"></i>
                </span>
                <span className="pcoded-mtext ">Courses</span>
              </a>
              <ul className="toggle-list pl-4">
  <li>
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={filter.paid}
            name="paid"
            onChange={() => {
              console.log("Paid checkbox toggled"); // Debug log
              handleFilterChange('paid'); // Trigger filter change for 'paid'
            }}
            className="mr-1"
          />
          <span className="checkbox-custom"></span>
          Paid
        </label>
      </li>
      <li>
        <label className="checkbox-label ">
          <input
            type="checkbox"
            checked={filter.unpaid}
            name="unpaid"
            onChange={() => {
              console.log("Unpaid checkbox toggled"); // Debug log
              handleFilterChange('unpaid'); // Trigger filter change for 'unpaid'
            }}
            className="mr-1"
          />
          <span className="checkbox-custom"></span>
          Free
        </label>
      </li>
  </ul>
            </li>
            <li className="nav-item ">
              <a
                href="/mycourses"
                className="nav-link has-ripple"
              >
                <span className="pcoded-micon">
                <i className="fa-solid fa-book"></i>
                </span>
                <span className="pcoded-mtext">My Courses</span>
              </a>
     
            </li>
          
            
            <li className="nav-item">
              <a
                href="/MyCertificateList"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-award"></i>
                </span>
                <span className="pcoded-mtext">Certificates</span>
              </a>
            </li>
          
            <li className="nav-item">
              <a
                href="/user/meeting/calender"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-video"></i>
                </span>
                <span className="pcoded-mtext">My Meetings</span>
              </a>
            </li>

            <li className="nav-item">
              <a
                href="/myPayments"
                className="nav-link "
              >
                <span className="pcoded-micon">
                  <i className="fa-solid fa-credit-card"></i>
                </span>
                <span className="pcoded-mtext">My Payments</span>
              </a>
            </li>

</ul>)}
{/* User Sidebar */}
        </div>
      </div>
    </nav>
  );
};

export default Sidebar;
