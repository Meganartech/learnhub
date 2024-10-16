import React, { useContext, useEffect, useState } from "react";
import arrowpic from "../images/arrowpic.jpeg";
import pencilpic from "../images/pencilpic.jpeg";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useNavigate } from "react-router-dom";
import baseUrl from "../api/utils";
import axios from "axios";
import { GlobalStateContext } from "../Context/GlobalStateProvider";
const Dashboard = () => {

  const navigate = useNavigate();
  const MySwal = withReactContent(Swal);
  const token = sessionStorage.getItem("token");
  const [Courses, setCourses] = useState([]);
  const { displayname } = useContext(GlobalStateContext);
  const [countdetails, setcountdetails] = useState({
    coursecount: "",
    trainercount: "",
    usercount: "",
    availableseats: "",
  });
  const [isvalid, setIsvalid] = useState();
  const [isEmpty, setIsEmpty] = useState();

  //need to change
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(`${baseUrl}/api/v2/GetAllUser`, {
          headers: {
            Authorization: token,
          },
        });

        if (response.status !== 200) {
          setIsEmpty(response.data.empty);
          console.error("Error fetching data:");
        }

        const data = response.data;

        setIsEmpty(data.empty);
        setIsvalid(data.valid);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    const fetchCounts = async () => {
      try {
        const response = await axios.get(`${baseUrl}/course/countcourse`, {
          headers: {
            Authorization: token,
          },
        });

        if (response.status === 200) {
          const data = response.data;
          setcountdetails(data);
        }
      } catch (error) {
        if (error.response && error.response.status === 401) {
          // Check if the error has a response and status 401
          window.location.href = "/unauthorized";
          return;
        }
        MySwal.fire({
          icon: "error",
          title: "Some Error Occurred",
          text: error.message,
        });
      }
    };
    const fetchpopularcourse = async () => {
      try {
        const response = await axios.get(
          `${baseUrl}/courseControl/popularCourse`,
          {
            headers: {
              Authorization: token,
            },
          }
        );

        if (response.status === 200) {
          const data = response.data;
          setCourses(data);
        }
      } catch (error) {
        if (error.response && error.response.status === 401) {
          window.location.href = "/unauthorized";
          return;
        }
        MySwal.fire({
          icon: "error",
          title: "Some Error Occurred",
          text: error.message,
        });
      }
    };

    fetchpopularcourse();
    fetchCounts();
  }, []);

  return (
    <div className="contentbackground">
      {!isvalid && (
        <div className="marquee-container">
          <div className="marquee-content">
            <a href="/licenceDetails" style={{ color: "darkred" }}>
              License has been expired Need to uploard new License or contact
              "111111111111"
            </a>
          </div>
        </div>
      )}
      <div className="contentinner">
        <div className="dash">
          <div style={{ flex: "3" }}>
            <div className="counts ">
              <div className="countchild ">
                <p>Available seats</p>
                <div className=" rounded-circle circleimg">
                  <h1>
                    {" "}
                    <i className="fa-solid fa-wheelchair mt-2"></i>
                  </h1>
                </div>
                <p>{countdetails.availableseats} seats</p>
              </div>

              <div className="countchild">
                <p>Total Courses</p>
                <div className=" rounded-circle circleimg">
                  <h1>
                    {" "}
                    <i className="fa-solid fa-chart-line mt-2"></i>
                  </h1>
                </div>
                <p>{countdetails.coursecount} courses</p>
              </div>
              <div className="countchild">
                <p>
                  Total{" "}
                  {displayname && displayname.student_name
                    ? displayname.student_name
                    : "Student"}
                </p>
                <div className=" rounded-circle circleimg">
                  <h1>
                    {" "}
                    <i className="fa-solid fa-person-walking mt-2"></i>
                  </h1>
                </div>
                <p>{countdetails.usercount} students</p>
              </div>
              <div className="countchild">
                <p>
                  Total{" "}
                  {displayname && displayname.trainer_name
                    ? displayname.trainer_name
                    : "Trainer"}
                </p>
                <div className=" rounded-circle circleimg">
                  <h1>
                    {" "}
                    <i className="fa-solid fa-person-chalkboard mt-2"></i>
                  </h1>
                </div>
                <p>{countdetails.trainercount} Trainers</p>
              </div>
            </div>

            <h5 className="font-weight-bold mt-5 ml-5">Popular Courses</h5>
            <div className="counts mb-4 " style={{ marginTop: "0px" }}>
              {Courses.map((course, index) => (
                <div
                  key={index}
                  className="countchild"
                  style={{ padding: "5px" }}
                >
                  <img
                    style={{
                      width: "100%",
                      height: "100px",
                      marginBottom: "10px",
                      borderRadius: "5px",
                      objectFit: "cover",
                    }}
                    src={`data:image/jpeg;base64,${course.courseImage}`}
                    alt="Course"
                  />
                  <a href={course.courseUrl}>
                    {course.courseName.length > 10
                      ? course.courseName.slice(0, 10) + "..."
                      : course.courseName}
                  </a>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-5" style={{ flex: "1" }}>
            <div>
              <img src={arrowpic} alt="arrowpic"></img>
            </div>
            <div>
              <img src={pencilpic} alt="pencilpic"></img>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
