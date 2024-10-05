import profile from "../images/profile.png";
import React, { useContext, useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import baseUrl from "../api/utils";
import axios from "axios";

const AdminProfileView = () => {
  const navigate = useNavigate();
  const token = sessionStorage.getItem("token");
  const role = sessionStorage.getItem("role");
  const [notfound, setNotFound] = useState(false);
  const [img, setImg] = useState(null);
  const { adminemail } = useParams();
  const location = useLocation();
  const [licenceDetails, setLicenceDetails] = useState({});
  const [initialUserData, setInitialUserData] = useState(
    location.state?.user || null
  );
  const[LicenceNotfound,setLicenceNotfound]=useState(true)
  const [userData, setUserData] = useState({
    username: "",
    email: "",
    phone: "",
    skills: "",
    dob: "",
    countryCode: "",
    profile: null,
    roleName: "",
  });

  useEffect(() => {
    const fetchData = async () => {
      if (role === "SYSADMIN") {
        try {
          let fetchedInitialUserData = initialUserData;

          // Fetch initialUserData if it's not available from location.state
          if (!fetchedInitialUserData) {
            const detailsRes = await axios.get(
              `${baseUrl}/details/${adminemail}`,
              {
                headers: { Authorization: token },
              }
            );
            fetchedInitialUserData = detailsRes.data;
            setInitialUserData(fetchedInitialUserData);
          }

          // Fetch additional user data
          if (fetchedInitialUserData) {
            const email = fetchedInitialUserData.email;
            const response = await axios.get(
              `${baseUrl}/student/getadmin/${email}`,
              {
                headers: { Authorization: token },
              }
            );

            if (response.status === 200) {
              const serverData = response.data;
              if (serverData.profile) {
                setImg(`data:image/jpeg;base64,${serverData.profile}`);
              }
              setUserData((prevData) => ({
                ...prevData,
                ...fetchedInitialUserData,
                ...serverData,
              }));
            }
          }
        } catch (error) {
          if (error.response) {
            if (error.response.status === 404) {
              setNotFound(true);
            } else if (error.response.status === 401) {
              window.location.href = "/unauthorized";
            }
          }
        }
      } else if (role === "TRAINER") {
        window.location.href = "/unauthorized";
      }
    };

    fetchData();
  }, [initialUserData, adminemail, role, token]);
  useEffect(() => {
    const fetchlicencedetailsforadmin = async () => {
        try{
      const response = await axios.get(
        `${baseUrl}/licence/getinfo/${adminemail}`,
        {
          headers: {
            Authorization: token,
          },
        }
      );
      const { data } = response; // Destructure data from response

      // Filter out unwanted properties
      const filteredData = {
        ProductName: data.product_name,
        CompanyName: data.company_name,
        trainer: data.trainer, // Assuming Contact refers to trainer
        student: data.students,
        course: data.course,
        Email: data.email, // Assuming email property exists
        version: data.version, // Assuming version property exists
        Type: data.type,
        StartDate: data.start_date
          ? new Date(data.start_date).toLocaleDateString()
          : "NA",
        EndDate: data.end_date
          ? new Date(data.end_date).toLocaleDateString()
          : "NA",
        StorageSize: `${data.storagesize || 0} GB`, // Handle null storage size
      };

      setLicenceDetails(filteredData);
    }
catch(error){
        if (error.response) {
            if (error.response.status === 404) {
                setLicenceNotfound(true)
            }else{
                console.log(error)
            }
        }
    }
};
    fetchlicencedetailsforadmin();
  }, []);
  return (
    <div className="contentbackground">
      <div className="contentinner">
        <div className="navigateheaders">
          <div
            onClick={() => {
              navigate(-1);
            }}
          >
            <i className="fa-solid fa-arrow-left"></i>
          </div>
          <div></div>
          <div
            onClick={() => {
              navigate("/view/Trainer");
            }}
          >
            <i className="fa-solid fa-xmark"></i>
          </div>
        </div>
        {notfound ? (
          <h1 style={{ textAlign: "center", marginTop: "250px" }}>
            No Admin found with the email
          </h1>
        ) : (
          <div className="innerFrame">
            <h2 style={{ textDecoration: "underline" }}>Admin Profile</h2>
            <div className="mainform">
              <div className="profile-picture">
                <div className="image-group">
                  <img
                    id="preview"
                    src={img || profile}
                    alt="profile"
                    onError={(e) => (e.target.src = profile)} // Handle image load error
                  />
                </div>
              </div>
              <div
                className="formgroup"
                style={{
                  backgroundColor: "#F2E1F5",
                  padding: "10px",
                  paddingLeft: "20px",
                  borderRadius: "20px",
                }}
              >
                <div className="inputgrp2">
                  <label htmlFor="Name">Name</label>
                  <span>:</span>
                  <label>{userData.username}</label>
                </div>
                <div className="inputgrp2">
                  <label htmlFor="email">Email</label>
                  <span>:</span>
                  <label>{userData.email}</label>
                </div>
                <div className="inputgrp2">
                  <label htmlFor="dob">Date of Birth</label>
                  <span>:</span>
                  <label>{userData.dob}</label>
                </div>
                <div className="inputgrp2">
                  <label htmlFor="skills">Skills</label>
                  <span>:</span>
                  <label>{userData.skills}</label>
                </div>

                <div className="inputgrp2">
                  <label htmlFor="Phone">Phone</label>
                  <span>:</span>
                  <label>
                    {userData.countryCode}
                    {userData.phone}
                  </label>
                </div>
                <div className="inputgrp2">
                  <label htmlFor="role">RoleName</label>
                  <span>:</span>
                  <label>Admin</label>
                </div>
              </div>
            </div>
            {LicenceNotfound ? (
          <h1 style={{ textAlign: "center", marginTop: "20px" }}>
            No Licence found with the email
          </h1>
        ) : (
            <div className="mt-5">
              <h2 style={{ textDecoration: "underline", textAlign: "center"  }}>
                Licence Info
              </h2>

              <div
                className="twosplit"
                style={{ marginBottom: "10px", gap: "20px" }}
              >
                <div className="inputgrp2">
                  <label>Product name </label>
                  <span>:</span>
                  <label>{licenceDetails.ProductName || "NA"}</label>
                </div>

              

                <div className="inputgrp2">
                  <label>Trainers </label>
                  <span>:</span>
                  <label>{licenceDetails.trainer || "NA"}</label>
                </div>

                <div className="inputgrp2">
                  <label>Students </label>
                  <span>:</span>
                  <label>{licenceDetails.student || "NA"}</label>
                </div>

                <div className="inputgrp2">
                  <label>Course </label>
                  <span>:</span>
                  <label>{licenceDetails.course || "NA"}</label>
                </div>

                <div className="inputgrp2">
                  <label>Type </label>
                  <span>:</span>
                  <label>{licenceDetails.Type || "NA"}</label>
                </div>

                <div className="inputgrp2">
                  <label>Start Date </label>
                  <span>:</span>
                  <label>{licenceDetails.StartDate}</label>
                </div>

                <div className="inputgrp2">
                  <label>End Date </label>
                  <span>:</span>
                  <label>{licenceDetails.EndDate}</label>
                </div>

                <div className="inputgrp2">
                  <label>Storage Size </label>
                  <span>:</span>
                  <label>{licenceDetails.StorageSize}</label>
                </div>

              
              </div>
            </div>)}
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminProfileView;
