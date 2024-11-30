import React, {  useContext, useEffect, useState } from "react";
import undraw_profile from"../images/profile.png"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useNavigate } from "react-router-dom";
import baseUrl from "../api/utils";
import axios from "axios";
import course from "../icons/course.svg"
import trainers from "../icons/trainers.svg"
import seats from "../icons/seats.svg"
import students from "../icons/students.svg"
import { GlobalStateContext } from "../Context/GlobalStateProvider";
import RadialProgressBar from "./RadialProgressBar";
import SupportChart from "./SupportChart";
const Dashboard = () => {

  const navigate = useNavigate();
  const MySwal = withReactContent(Swal);
  const token = sessionStorage.getItem("token");
  const [Courses, setCourses] = useState([]);
  const { displayname } = useContext(GlobalStateContext);
  const[storagedetail,setStoragedetail]=useState({
    total:"",
    StorageUsed:"",
    balanceStorage:""
  })
  const [countdetails, setcountdetails] = useState({
    coursecount: "",
    trainercount: "",
    usercount: "",
    availableseats: "",
    paidcourse:"",
    amountRecived:"",
  });
  const[trainerFest,settrainerFest]=useState([
    { name: '', profile: null, freeCourses: "", paidCourses: "", totalStudents: "" },
])
const[StudentFest,setStudentFest]=useState([
    { name: '', profile: null, freeCourses: "", paidCourses: "", totalStudents: "" ,pending:""},
])
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
         
          navigate("/unauthorized")
          return;
        }
        MySwal.fire({
          icon: "error",
          title: "Some Error Occurred",
          text: error.message,
        });
      }
    };
const fetchstorage=async()=>{
    try{
   const resu=await axios.get(`${baseUrl}/dashboard/storage`,{
    headers:{
        Authorization:token
    }
   })
   setStoragedetail(resu.data);
    }catch(error){
        if (error.response && error.response.status === 401) {
            navigate("/unauthorized")
            return;
          }
          MySwal.fire({
            icon: "error",
            title: "Some Error Occurred",
            text: error.message,
          });
        
    }
}
    const fetchStudentFects=async()=>{
        try{
            const res=await axios.get(`${baseUrl}/dashboard/StudentSats`,{
                headers:{
                    Authorization: token,
                  },
            });
           const data=res.data;
           setStudentFest(data)
        } catch (error) {
            if (error.response && error.response.status === 401) {
                navigate("/unauthorized")
              return;
            }
            MySwal.fire({
              icon: "error",
              title: "Some Error Occurred",
              text: error.message,
            });
          }

    }
    const fetchtrainerFetcs=async()=>{
        try{
            const res=await axios.get(`${baseUrl}/dashboard/trainerSats`,{
                headers:{
                    Authorization: token,
                  },
            });
           const data=res.data;
           settrainerFest(data)
        } catch (error) {
            if (error.response && error.response.status === 401) {
                navigate("/unauthorized")
              return;
            }
            MySwal.fire({
              icon: "error",
              title: "Some Error Occurred",
              text: error.message,
            });
          }

    }
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
            navigate("/unauthorized")
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
    fetchtrainerFetcs();
    fetchStudentFects();
    fetchstorage();
  }, []);
  const convertToGB = (value) => {
    if (value.toLowerCase().includes("mb")) {
      // Convert MB to GB
      return parseFloat(value) / 1024;
    } else if (value.toLowerCase().includes("gb")) {
      // Already in GB
      return parseFloat(value);
    }
    return 0; // Default fallback in case of an invalid value
  };

  // Convert all values to GB
  const totalGB = convertToGB(storagedetail.total);
  const usedGB = convertToGB(storagedetail.StorageUsed);

  // Calculate percentage of storage used
  const usedPercentage = (usedGB / totalGB) * 100;
  

  return (
      <>
        <div className="page-header">
            <div className="page-block">
                <div className="row align-items-center">
                    <div className="col-md-12">
                        <div className="page-header-title">
                            <h5 className="m-b-10">Dashboard </h5>
                        </div>
                        <ul className="breadcrumb">
                            <li className="breadcrumb-item"><a href="#" onClick={()=>{ navigate("/admin/dashboard")}}><i className="feather icon-home"></i></a></li>
                            <li className="breadcrumb-item"><a href="#">Dashboard </a></li>
                        </ul>
                        {isvalid && (
        <div className="marquee-container">
          <div className="marquee-content">
            <a onClick={()=>{ navigate("/licenceDetails")}} href="#" style={{ color: "darkred" }}>
              License has been expired Need to uploard new License or contact
              "111111111111"
            </a>
          </div>
        </div>
      )}
                    </div>
                </div>
            </div>
        </div>
        <div className="row">
            <div className="col-lg-7 col-md-12">
                <div className="row">
                    <div className="col-sm-6">
                        <div className="card support-bar overflow-hidden">
                            <div className="card-body pb-0">
                                <h2 className="m-0">Rs. {countdetails.amountRecived}</h2>
                                <span className="text-c-blue">Total Course Revenue</span>
                                <p className="mb-3 mt-3">Total number of courses calculated amount.</p>
                            </div>
                            <div id="support-chart">
                              
                                <SupportChart data={[0, countdetails.paidcourse, countdetails.trainercount, countdetails.coursecount, 0]} />
                            </div>
                            <div className="card-footer bg-primary text-white">
                                <div className="row text-center">
                                    <div className="col">
                                        <h5 className="m-0 text-white">{countdetails.paidcourse}</h5>
                                        <span>Paid</span>
                                    </div>
                                    <div className="col">
                                        <h5 className="m-0 text-white">{countdetails.trainercount}</h5>
                                        <span>Trainers</span>
                                    </div>
                                    <div className="col">
                                        <h5 className="m-0 text-white">{countdetails.coursecount}</h5>
                                        <span>Courses</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="col-sm-6">
                        <div className="card support-bar overflow-hidden">
                            <div className="card-body pb-0">
                                <h2 className="m-0">Storage</h2>
                                <span className="text-c-green">Total Storage</span>
                                <p className="mb-3 mt-3">Number of Video Space Graph</p>
                            </div>
                            
                            <div className="radial-bar" > 
                            <RadialProgressBar percentage={usedPercentage} total={storagedetail.total}/></div>
                            <div className="card-footer bg-success text-white">
                                <div className="row text-center">
                                    <div className="col">
                                        <h5 className="m-0 text-white">{storagedetail.total}</h5>
                                        <span>Total Space</span>
                                    </div>
                                    <div className="col">
                                        <h5 className="m-0 text-white">{storagedetail.StorageUsed}</h5>
                                        <span>used</span>
                                    </div>
                                    <div className="col">
                                        <h5 className="m-0 text-white">{storagedetail.balanceStorage}</h5>
                                        <span>Available </span>
                                    </div>
                             
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-lg-5 col-md-12">
                <div className="row">
                    <div className="col-sm-6">
                        <div className="card">
                            <div className="card-body">
                                <div className="row align-items-center">
                                    <div className="col-8">
                                        <h4 className="text-c-yellow">{countdetails.availableseats}</h4>
                                        <h6 className="text-muted m-b-0">Seats</h6>
                                    </div>
                                    <div className="col-4 text-right">
                                        <img src={seats} alt="seats"/>
                                    </div>
                                </div>
                            </div>
                            <div className="card-footer bg-c-yellow">
                                <div className="row align-items-center">
                                    <div className="col-9">
                                        <p className="text-white m-b-0">Available Seats</p>
                                    </div>
                                   
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="col-sm-6" style={{cursor:"pointer"}} onClick={()=>{navigate("/dashboard/course")}}>
                        <div className="card">
                            <div className="card-body">
                                <div className="row align-items-center">
                                    <div className="col-8">
                                        <h4 className="text-c-green">{countdetails.coursecount}</h4>
                                        <h6 className="text-muted m-b-0">Courses</h6>
                                    </div>
                                    <div className="col-4 text-right">
                                        <img src={course} alt="course Icon" />
                                    </div>
                                </div>
                            </div>
                            <div className="card-footer bg-c-green">
                                <div className="row align-items-center">
                                    <div className="col-9">
                                        <p className="text-white m-b-0">Total Courses</p>
                                    </div>
                                   
                                </div>
                            </div>
                        </div>
                    </div>
                    <div style={{cursor:"pointer"}} onClick={()=>{navigate("/view/Students")}}  className="col-sm-6">
                        <div className="card">
                            <div className="card-body">
                                <div className="row align-items-center">
                                    <div className="col-8">
                                        <h4 className="text-c-red">{countdetails.usercount}</h4>
                                        <h6 className="text-muted m-b-0">{displayname && displayname.student_name
                     ? displayname.student_name
                    : "Student"}</h6>
                                    </div>
                                    <div className="col-4 text-right">
                                        <img  src={students} alt="students"/>
                                    </div>
                                </div>
                            </div>
                            <div className="card-footer bg-c-red">
                                <div className="row align-items-center">
                                    <div className="col-9">
                                        <p className="text-white m-b-0">Total {displayname && displayname.student_name
                     ? displayname.student_name
                    : "Student"}</p>
                                    </div>
                                   
                                </div>
                            </div>
                        </div>
                    </div>
                    <div style={{cursor:"pointer"}} onClick={()=>{navigate("/view/Trainer")}} className="col-sm-6">
                        <div className="card">
                            <div className="card-body">
                                <div className="row align-items-center">
                                    <div className="col-8">
                                        <h4 className="text-c-blue">{countdetails.trainercount}</h4>
                                        <h6 className="text-muted m-b-0">{displayname && displayname.trainer_name
                    ? displayname.trainer_name
                    : "Trainer"}</h6>
                                    </div>
                                    <div className="col-4 text-right">
                                    <img src={trainers} alt="trainers"/>
                                    </div>
                                </div>
                            </div>
                            <div className="card-footer bg-c-blue">
                                <div className="row align-items-center">
                                    <div className="col-9">
                                        <p className="text-white m-b-0">Total {displayname && displayname.trainer_name
                    ? displayname.trainer_name
                    : "Trainer"}</p>
                                    </div>
                                    
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                </div>
               <div className="col-xl-6 col-md-12">
                <div className="card table-card">
                    <div className="card-header">
                        <h5>Trainers</h5>
                      
                    </div>
                    <div className="card-body p-0">
                        <div className="table-responsive">
                            <table className="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>
                                           
                                            Name
                                        </th>
                                        <th>Paid Courses</th>
                                        <th>Free Courses</th>
                                        <th className="text-right">Students</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {trainerFest.map((trainer,index) => (
          <tr key={index}>
                                        <td>
                                           
                                            <div className="d-inline-block align-middle">
                                                <img src={trainer.profile!==null ? `data:image/jpeg;base64,${trainer.profile}`:undraw_profile} 
                                                alt="user"
                                                 className="img-radius wid-40 align-top m-r-15"/>
                                                <div className="d-inline-block">
                                                    <h6>{trainer.name || ""}</h6>
                                                </div>
                                            </div>
                                        </td>
                                        <td>{trainer.paidCourses || 0}</td>
                                        <td>{trainer.freeCourses || 0}</td>
                                        <td className="text-right">{trainer.students||0}</td>
                                    </tr>))}
                                    
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-xl-6 col-md-12">
                <div className="card table-card">
                    <div className="card-header">
                        <h5>Students</h5>
                     
                    </div>
                    <div className="card-body p-0">
                        <div className="table-responsive">
                            <table className="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>
                                           
                                            Name
                                        </th>
                                        <th>paid Coures</th>
                                        <th>Free Courses</th>
                                        <th className="text-right">Pending Fee</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {StudentFest.map((Student,index) => (
          <tr key={index}>
                                        <td>
                                           
                                            <div className="d-inline-block align-middle">
                                                <img src={Student.profile!==null ? `data:image/jpeg;base64,${Student.profile}`:undraw_profile} 
                                                alt="user"
                                                 className="img-radius wid-40 align-top m-r-15"/>
                                                <div className="d-inline-block">
                                                    <h6>{Student.name || ""}</h6>
                                                </div>
                                            </div>
                                        </td>
                                        <td>{Student.paidCourses || 0}</td>
                                        <td>{Student.freeCourses || 0}</td>
                                        <td className="text-right">{Student.pending||0}</td>
                                    </tr>))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
                </div>
                </>      
  );
};

export default Dashboard;
