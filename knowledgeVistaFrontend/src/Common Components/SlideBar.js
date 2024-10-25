import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Component.css";
import baseUrl from "../api/utils";
import axios from "axios";
import { GlobalStateContext } from "../Context/GlobalStateProvider";

const SlideBar = ({ handleSidebarToggle, activeLink, setActiveLink }) => {
  const [isvalid, setIsvalid] = useState();
  const [isEmpty, setIsEmpty] = useState();
  const userRole = sessionStorage.getItem("role");
  const navigate = useNavigate();
  const token = sessionStorage.getItem("token");
  const { displayname } = useContext(GlobalStateContext);
  useEffect(() => {
    const fetchData = async () => {
      
      try {
        
        if (userRole!=="SYSADMIN" && token) {
          const response = await axios.get(`${baseUrl}/api/v2/GetAllUser`, {
            headers: {
              Authorization: token,
            },
          });

          const data = response.data;
          setIsEmpty(data.empty);
          setIsvalid(data.valid);
          sessionStorage.setItem("LicenceVersion",data.productversion)
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

  const handleClick = (link) => {
    if (userRole === "ADMIN" || userRole === "TRAINER") {
      if (
        (link === "/about" ||
          link === "/admin/dashboard" ||
          link === "/licenceDetails") &&
        isEmpty
      ) {
        handleSidebarToggle();
        setActiveLink(link);
        navigate(link);
      } else if (
        (link === "/about" ||
          link === "/admin/dashboard" ||
          link === "/licenceDetails") &&
        !isEmpty &&
        !isvalid
      ) {
        handleSidebarToggle();
        setActiveLink(link);
        navigate(link);
      } else if (!isEmpty && isvalid) {
        handleSidebarToggle();
        setActiveLink(link);
        navigate(link);
      }
    } else if (userRole === "USER" || userRole === "SYSADMIN") {
      handleSidebarToggle();
      setActiveLink(link);
      navigate(link);
    }
  };

  return (
    <ul
      className="navbar-nav   sidebar sidebar-dark accordion "
      id="accordionSidebar"
    >
      {/* <div className="simplebar-content">
       <div className="pc-sidebar pc-trigger">
       <div className="navbar-wrapper">
       <div className="navbar-content">
      <ul className="pc-navbar" style={{display:"block"}}>
        <li className="pc-item pc-hasmenu">
          <a href="#!" className="pc-link"
            ><span className="pc-micon">
              <i className="ph ph-tree-structure"></i> </span
            ><span className="pc-mtext">Menu levels</span><span className="pc-arrow"><i data-feather="chevron-right"></i></span
          ></a>
          <ul className="pc-submenu">
            <li className="pc-item"><a className="pc-link" href="#!">Level 2.1</a></li>
            <li className="pc-item pc-hasmenu">
              <a href="#!" className="pc-link"
                >Level 2.2<span className="pc-arrow"><i data-feather="chevron-right"></i></span
              ></a>
              <ul className="pc-submenu">
                <li className="pc-item"><a className="pc-link" href="#!">Level 3.1</a></li>
                <li className="pc-item"><a className="pc-link" href="#!">Level 3.2</a></li>
                <li className="pc-item pc-hasmenu">
                  <a href="#!" className="pc-link"
                    >Level 3.3<span className="pc-arrow"><i data-feather="chevron-right"></i></span
                  ></a>
                  <ul className="pc-submenu">
                    <li className="pc-item"><a className="pc-link" href="#!">Level 4.1</a></li>
                    <li className="pc-item"><a className="pc-link" href="#!">Level 4.2</a></li>
                  </ul>
                </li>
              </ul>
            </li>
            <li className="pc-item pc-hasmenu">
              <a href="#!" className="pc-link"
                >Level 2.3<span className="pc-arrow"><i data-feather="chevron-right"></i></span
              ></a>
              <ul className="pc-submenu">
                <li className="pc-item"><a className="pc-link" href="#!">Level 3.1</a></li>
                <li className="pc-item"><a className="pc-link" href="#!">Level 3.2</a></li>
                <li className="pc-item pc-hasmenu">
                  <a href="#!" className="pc-link"
                    >Level 3.3<span className="pc-arrow"><i data-feather="chevron-right"></i></span
                  ></a>
                  <ul className="pc-submenu">
                    <li className="pc-item"><a className="pc-link" href="#!">Level 4.1</a></li>
                    <li className="pc-item"><a className="pc-link" href="#!">Level 4.2</a></li>
                  </ul>
                </li>
              </ul>
            </li>
          </ul>
        </li>
        </ul>
</div>
        </div>
        </div>
        </div> */}
      <hr className="sidebar-divider " />

      {userRole === "ADMIN" && (
        <li className="nav-item mt-1">
          <a
            className={
              activeLink === "/admin/dashboard"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/admin/dashboard")}
          >
            <i
              className={
                activeLink === "/admin/dashboard"
                  ? "fa-solid fa-gauge text-light"
                  : "fa-solid fa-gauge text-muted"
              }
            ></i>
            <span>Dashboard</span>
          </a>
        </li>
      )}
      {userRole === "ADMIN" && (
        <div
          className={`mt-2 ${
            activeLink.includes("/course") ? "ActiveLink" : ""
          }`}
        >
          <li className="nav-item">
            <a
              className={`nav-link ${
                activeLink.includes("/course") ? "text-light" : "text-muted"
              }`}
              href="#"
              onClick={() => {
                setActiveLink("/course");
                const collapseTwo = document.getElementById("collapseTwo");
                if (collapseTwo.classList.contains("show")) {
                  collapseTwo.classList.remove("show");
                } else {
                  collapseTwo.classList.add("show");
                }
              }}
            >
              <i
                className={`fa-solid fa-book-open ${
                  activeLink.includes("/course") ? "text-light" : "text-muted"
                }`}
              ></i>
              <span>Courses</span>
            </a>
          </li>
          <div
            style={{ width: "100%" }}
            id="collapseTwo"
            className={`collapse  newnav ${
              activeLink.includes("/course") ? "show" : ""
            }`}
            aria-labelledby="headingTwo"
            data-parent="#accordionSidebar"
          >
            <div
              style={{ width: "100%" }}
              className="text-light collapse-inner"
            >
              <a
                className={`nav-link mb-2 collapse-item text-light ${
                  activeLink === "/course/admin/edit" ? " SubActiveLink " : ""
                }`}
                href="#"
                onClick={() => {
                  handleClick("/course/admin/edit");
                  setActiveLink("/course/admin/edit");
                }}
              >
                <i className=" fa-solid fa-edit text-light pl-4"></i>
                <span> Edit Courses</span>
              </a>

              <a
                className={`collapse-item mb-2 text-light nav-link ${
                  activeLink === "/course/addcourse" ? "SubActiveLink " : " "
                }`}
                href="#"
                onClick={() => {
                  handleClick("/course/addcourse");
                  setActiveLink("/course/addcourse");
                }}
              >
                <i className=" fa-solid fa-plus text-light pl-4"></i>
                <span> Create course</span>
              </a>

              <a
                className={`collapse-item text-light nav-link ${
                  activeLink === "/dashboard/course" ? "SubActiveLink " : ""
                }`}
                href="#"
                onClick={() => {
                  handleClick("/dashboard/course");
                  setActiveLink("/dashboard/course");
                }}
              >
                <i className="  fa-solid fa-eye text-light pl-4"></i>
                <span> View Course</span>
              </a>
            </div>
          </div>
        </div>
      )}

      {(userRole === "USER" || userRole === "TRAINER") && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/dashboard/course"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/dashboard/course")}
          >
            <i
              className={
                activeLink === "/dashboard/course"
                  ? "fa-solid fa-book text-light"
                  : "fa-solid fa-book text-muted"
              }
            ></i>
            <span>Courses</span>
          </a>
        </li>
      )}

      {userRole === "USER" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/mycourses"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/mycourses")}
          >
            <i
              className={
                activeLink === "/mycourses"
                  ? "fa-solid fa-book text-light"
                  : "fa-solid fa-book text-muted"
              }
            ></i>
            <span>My Courses</span>
          </a>
        </li>
      )}

      {userRole === "USER" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/MyCertificateList"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/MyCertificateList")}
          >
            <i
              className={
                activeLink === "/MyCertificateList"
                  ? "fa-solid fa-award text-light"
                  : "fa-solid fa-award text-muted"
              }
            ></i>
            <span> Certificates</span>
          </a>
        </li>
      )}
  {userRole === "USER" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/user/meeting/calender"
                ? "ActiveLink nav-link text-light"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/user/meeting/calender")}
          >
            <i
              className={
                activeLink === "/user/meeting/calender"
                  ? "fa-solid fa-video  text-light"
                  : "fa-solid fa-video  text-muted"
              }
            ></i>
            <span> Meetings</span>
          </a>
        </li>
      )}
      {userRole === "USER" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/myPayments"
                ? "ActiveLink nav-link text-light"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/myPayments")}
          >
            <i
              className={
                activeLink === "/myPayments"
                  ? "fa-regular fa-credit-card text-light"
                  : "fa-regular fa-credit-card text-muted"
              }
            ></i>
            <span> Payments</span>
          </a>
        </li>
      )}

      {userRole === "TRAINER" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/AssignedCourses"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/AssignedCourses")}
          >
            <i
              className={
                activeLink === "/AssignedCourses"
                  ? "fa-solid fa-book text-light"
                  : "fa-solid fa-book text-muted"
              }
            ></i>
            <span>My Courses</span>
          </a>
        </li>
      )}
     
      {userRole === "ADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/view/Trainer"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/view/Trainer")}
          >
            <i
              className={
                activeLink === "/view/Trainer"
                  ? "fa-solid fa-chalkboard-user text-light"
                  : "fa-solid fa-chalkboard-user text-muted"
              }
            ></i>
            <span>{displayname && displayname.trainer_name 
          ? displayname.trainer_name
          : "Trainers" 
        }</span>
          </a>
        </li>
      )}
      {userRole === "ADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/view/Students"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/view/Students")}
          >
            <i
              className={
                activeLink === "/view/Students"
                  ? "fa-solid fa-users text-light"
                  : "fa-solid fa-users text-muted"
              }
            ></i>
            <span>{displayname && displayname.student_name 
          ? displayname.student_name 
          : "Student" 
        }</span>
          </a>
        </li>
      )}

      {userRole === "ADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/certificate"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/certificate")}
          >
            <i
              className={
                activeLink === "/certificate"
                  ? "fa-solid fa-award text-light"
                  : "fa-solid fa-award text-muted"
              }
            ></i>
            <span>Certificate</span>
          </a>
        </li>
      )}

    
       {(userRole === "ADMIN" ||userRole=="TRAINER") && (
        <div
          className={`mt-2 ${activeLink.includes("/meet") ? "ActiveLink" : ""}`}
        >
          <li className="nav-item">
            <a
              className={`nav-link ${
                activeLink.includes("/meet") ? "text-light" : "text-muted"
              }`}
              href="#"
              onClick={() => {
                setActiveLink("/meet");
                const collapseThree = document.getElementById("collapsemeet");
                if (collapseThree.classList.contains("show")) {
                  collapseThree.classList.remove("show");
                } else {
                  collapseThree.classList.add("show");
                }
              }}
            >
              <i
                className={`fa-solid fa-video ${
                  activeLink.includes("/meet") ? "text-light" : "text-muted"
                }`}
              ></i>
              <span>Meeting</span>
            </a>
          </li>
          <div
            style={{ width: "100%" }}
            id="collapsemeet"
            className={`collapse  newnav ${
              activeLink.includes("/meet") ? "show" : ""
            }`}
            aria-labelledby="headingTwo"
            data-parent="#accordionSidebar"
          >
            <div
              style={{ width: "100%" }}
              className="text-light collapse-inner"
            >
 <a
                className={`nav-link mb-2 collapse-item text-light ${
                  activeLink === "/meeting/calender" ? " SubActiveLink " : ""
                }`}
                href="#"
                onClick={() => {
                  handleClick("/meeting/calender");
                  setActiveLink("/meeting/calender");
                }}
              >
                <i className=" pr-1 fa-solid fa-user-clock text-light pl-4"></i>
                <span>My Meetings</span>
              </a>
{userRole==="ADMIN" &&
              <a
                className={`nav-link mb-2 collapse-item text-light ${
                  activeLink === "/meeting/settings" ? " SubActiveLink " : ""
                }`}
                href="#"
                onClick={() => {
                  handleClick("/meeting/settings");
                  setActiveLink("/meeting/settings");
                }}
              >
                <i className=" pr-1 fa-solid fa-gear text-light pl-4"></i>
                <span>Settings</span>
              </a>
}
              <a
                className={`collapse-item mb-2 text-light nav-link ${
                  activeLink === "/meeting/Shedule"
                    ? "SubActiveLink "
                    : " "
                }`}
                href="#"
                onClick={() => {
                  handleClick("/meeting/Shedule");
                  setActiveLink("/meeting/Shedule");
                }}
              >
                <i className=" fa-solid fa-plus text-light pl-4 pr-2"></i>
                <span>Shedule Meeting</span>
              </a>
            </div>
          </div>
        </div>
      )}
 {userRole === "ADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/viewsettings"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/viewsettings")}
          >
            <i
              className={
                activeLink === "/viewsettings"
                  ? "fa-solid fa-gear text-light"
                  : "fa-solid fa-gear text-muted"
              }
            ></i>
            <span>Settings</span>
          </a>
        </li>
      )}

{userRole === "ADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/displayname"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/displayname")}
          >
            <i
              className={
                activeLink === "/displayname"
                  ? "fa-solid fa-user text-light"
                  : "fa-solid fa-user text-muted"
              }
            ></i>
            <span>Role Settings</span>
          </a>
        </li>
      )}


      {userRole === "ADMIN" && (
        <div
          className={`mt-2 ${activeLink.includes("/pay") ? "ActiveLink" : ""}`}
        >
          <li className="nav-item">
            <a
              className={`nav-link ${
                activeLink.includes("/pay") ? "text-light" : "text-muted"
              }`}
              href="#"
              onClick={() => {
                setActiveLink("/pay");
                const collapseTwo = document.getElementById("collapsepay");
                if (collapseTwo.classList.contains("show")) {
                  collapseTwo.classList.remove("show");
                } else {
                  collapseTwo.classList.add("show");
                }
              }}
            >
              <i
                className={`fa-regular fa-credit-card ${
                  activeLink.includes("/pay") ? "text-light" : "text-muted"
                }`}
              ></i>
              <span>Payments</span>
            </a>
          </li>
          <div
            style={{ width: "100%" }}
            id="collapsepay"
            className={`collapse  newnav ${
              activeLink.includes("/pay") ? "show" : ""
            }`}
            aria-labelledby="headingTwo"
            data-parent="#accordionSidebar"
          >
            <div
              style={{ width: "100%" }}
              className="text-light collapse-inner"
            >
              <a
                className={`nav-link mb-2 collapse-item text-light ${
                  activeLink === "/settings/payment" ? " SubActiveLink " : ""
                }`}
                href="#"
                onClick={() => {
                  handleClick("/settings/payment");
                  setActiveLink("/settings/payment");
                }}
              >
                <i className=" pr-1 fa-solid fa-gear text-light pl-4"></i>
                <span>Payment Settings</span>
              </a>

              <a
                className={`collapse-item mb-2 text-light nav-link ${
                  activeLink === "/payment/transactionHitory"
                    ? "SubActiveLink "
                    : " "
                }`}
                href="#"
                onClick={() => {
                  handleClick("/payment/transactionHitory");
                  setActiveLink("/payment/transactionHitory");
                }}
              >
                <i className=" fa-solid fa-clock-rotate-left text-light pl-4"></i>
                <span> Transaction History</span>
              </a>
            </div>
          </div>
        </div>
      )}
      {userRole === "ADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/licenceDetails"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/licenceDetails")}
          >
            <i
              className={
                activeLink === "/licenceDetails"
                  ? "fa-solid fa-file-code text-light"
                  : "fa-solid fa-file-code text-muted"
              }
            ></i>
            <span>Licence </span>
          </a>
        </li>
      )}

      {userRole === "ADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/about"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/about")}
          >
            <i
              className={
                activeLink === "/about"
                  ? "fa-solid fa-circle-info text-light"
                  : "fa-solid fa-circle-info text-muted"
              }
            ></i>
            <span>About us </span>
          </a>
        </li>
      )}

      {userRole === "TRAINER" && (
        <div
          className={`mt-2 ${activeLink.includes("Stu") ? "ActiveLink" : ""}`}
        >
          <li className="nav-item">
            <a
              className={`nav-link ${
                activeLink.includes("Stu") ? "text-light" : "text-muted"
              }`}
              href="#"
              onClick={() => {
                setActiveLink("Stu");
                const collapseTw = document.getElementById("collapseTw");
                if (collapseTw.classList.contains("show")) {
                  collapseTw.classList.remove("show");
                } else {
                  collapseTw.classList.add("show");
                }
              }}
            >
              <i
                className={`fa-solid fa-users ${
                  activeLink.includes("Stu") ? "text-light" : "text-muted"
                }`}
              ></i>
              <span>Students</span>
            </a>
          </li>
          <div
            style={{ width: "100%" }}
            id="collapseTw"
            className={`collapse  newnav ${
              activeLink.includes("Stu") ? "show" : ""
            }`}
            aria-labelledby="headingTw"
            data-parent="#accordionSidebar"
          >
            <div
              style={{ width: "100%" }}
              className="text-light collapse-inner"
            >
              <a
                className={`collapse-item mb-2 text-light nav-link ${
                  activeLink === "/view/Students" ? "SubActiveLink " : " "
                }`}
                href="#"
                onClick={() => {
                  handleClick("/view/Students");
                  setActiveLink("/view/Students");
                }}
              >
                <i className=" fa-solid fa-users text-light pl-4"></i>
                <span> All Students</span>
              </a>
              <a
                className={`collapse-item text-light nav-link ${
                  activeLink === "/myStudents" ? "SubActiveLink " : ""
                }`}
                href="#"
                onClick={() => {
                  handleClick("/myStudents");
                  setActiveLink("/myStudents");
                }}
              >
                <i className="  fa-solid fa-users-between-lines text-light pl-4"></i>
                <span> My Students</span>
              </a>
            </div>
          </div>
        </div>
      )}
      {userRole === "TRAINER" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/payment/trainer/transactionHitory"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/payment/trainer/transactionHitory")}
          >
            <i
              className={
                activeLink === "/payment/trainer/transactionHitory"
                  ? "fa-solid fa-clock-rotate-left text-light"
                  : "fa-solid fa-clock-rotate-left  text-muted"
              }
            ></i>
            <span>Transactions </span>
          </a>
        </li>
      )}
      {/* for SysAdmin */}
      {userRole === "SYSADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/viewAll/Admins"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/viewAll/Admins")}
          >
            <i
              className={
                activeLink === "/viewAll/Admins"
                  ? "fa-solid fa-user-tie text-light"
                  : "fa-solid fa-user-tie text-muted"
              }
            ></i>
            <span>Admins</span>
          </a>
        </li>
      )}
      {userRole === "SYSADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/viewAll/Trainers"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/viewAll/Trainers")}
          >
            <i
              className={
                activeLink === "/viewAll/Trainers"
                  ? "fa-solid fa-chalkboard-user text-light"
                  : "fa-solid fa-chalkboard-user text-muted"
              }
            ></i>
            <span>Trainers</span>
          </a>
        </li>
      )}
      {userRole === "SYSADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/viewAll/Students"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/viewAll/Students")}
          >
            <i
              className={
                activeLink === "/viewAll/Students"
                  ? "fa-solid fa-users text-light"
                  : "fa-solid fa-users text-muted"
              }
            ></i>
            <span>Students</span>
          </a>
        </li>
      )}

{userRole === "SYSADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/Affiliates"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/Affiliates")}
          >
            <i
              className={
                activeLink === "/Affiliates"
                  ? "fa-regular fa-handshake text-light"
                  : "fa-regular fa-handshake text-muted"
              }
            ></i>
            <span>Affiliates</span>
          </a>
        </li>
      )}
 {userRole === "SYSADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/view/SocialLogin"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/view/SocialLogin")}
          >
            <i
              className={
                activeLink === "/view/SocialLogin"
                  ? "fa-solid fa-users text-light"
                  : "fa-solid fa-users text-muted"
              }
            ></i>
            <span>Social Login</span>
          </a>
        </li>
      )}
{userRole === "SYSADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/Zoomkeyupload"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/Zoomkeyupload")}
          >
            <i
              className={
                activeLink === "/Zoomkeyupload"
                  ? "fa-solid fa-video  text-light"
                  : "fa-solid fa-video  text-muted"
              }
            ></i>
            <span>Zoom Keys </span>
          </a>
        </li>
      )}




      {userRole === "SYSADMIN" && (
        <li className="nav-item mt-2">
          <a
            className={
              activeLink === "/licenceupload"
                ? "ActiveLink nav-link"
                : "nav-link text-muted"
            }
            href="#"
            onClick={() => handleClick("/licenceupload")}
          >
            <i
              className={
                activeLink === "/licenceupload"
                  ? "fa-solid fa-file-code text-light"
                  : "fa-solid fa-file-code text-muted"
              }
            ></i>
            <span>Licence</span>
          </a>
        </li>
      )}





      <hr className="sidebar-divider d-none d-md-block" />
    </ul>
  );
};

export default SlideBar;
