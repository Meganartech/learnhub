import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../api/utils";
import axios from "axios";

const MailSettings = () => {
  const navigate = useNavigate();
  const [initialsave, setinitialsave] = useState(false);
  const MySwal = withReactContent(Swal);
  const token = sessionStorage.getItem("token");
  const [isnotFound, setisnotFound] = useState(false);
  const [settings, setsettings] = useState({
    hostname: "",
    port: "587",
    emailid: "",
    password: "",
  });
  const [defaultsettings, setdefaultsettings] = useState({
    hostname: "",
    port: "",
    emailid: "",
    password: "",
  });
  const [errors, seterrors] = useState({
    hostname: "",
    emailid: "",
    password: "",
  });
  useEffect(() => {
    if (token) {
      const fetchMailAccountSettings = async () => {
        try {
          const response = await axios.get(`${baseUrl}/get/mailkeys`, {
            headers: {
              Authorization: token,
            },
          });
          if (response.status === 200) {
            const data = response.data;

            setdefaultsettings(data);
            setsettings(data);
<<<<<<< HEAD
            
        }else if(response.status === 204){
          setisnotFound(true);
          setinitialsave(true);
        }
=======
          }
>>>>>>> ae1ca461709f858794f65dea8b73fed4321b5b3c
        } catch (error) {
          if (error.response) {
            if (error.response.status === 404) {
              setisnotFound(true);
              setinitialsave(true);
            } else if (error.response.status === 401) {
              window.location.href = "/unauthorized";
            }else{
              throw error
            }
          }
        }
      };

      fetchMailAccountSettings();
    }
  }, []);
  const save = async (e) => {
    e.preventDefault();
    console.log("hi in save", initialsave);

    if (initialsave) {
      console.log("hi in initial save");
      try {
        const response = await axios.post(
          `${baseUrl}/save/mailkeys`,
          settings,
          {
            headers: {
              Authorization: token,
            },
          }
        );

        if (response.status === 200) {
          MySwal.fire({
            title: "Saved !",
            text: "Email Details Saved Sucessfully",
            icon: "success",
            confirmButtonText: "OK",
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.reload();
<<<<<<< HEAD
            }   });
          setisnotFound(false)
        } 
  }catch(error){
    console.log(error)
    // MySwal.fire({
    //   icon: 'error',
    //   title: 'Some Error Occurred',
    //   text: "error occured"
    // });
    throw error
  }
     
    }else{ 
      if(defaultsettings.id){
        console.log("hi in default");
      axios.patch(`${baseUrl}/Edit/mailkeys`,settings ,{
      headers:{
        "Authorization":token
=======
            }
          });
          setisnotFound(false);
>>>>>>> ae1ca461709f858794f65dea8b73fed4321b5b3c
        }
      } catch (error) {
        console.log(error);
        MySwal.fire({
          icon: "error",
          title: "Some Error Occurred",
          text: "error occured",
        });
      }
    } else {
      if (defaultsettings.id) {
        console.log("hi in default");
        axios
          .patch(`${baseUrl}/Edit/mailkeys`, settings, {
            headers: {
              Authorization: token,
            },
          })
          .then((response) => {
            if (response.status === 200) {
              MySwal.fire({
                title: "Updated",
                text: "Email Details Saved Sucessfully",
                icon: "success",
                confirmButtonText: "OK",
              }).then((result) => {
                if (result.isConfirmed) {
                  window.location.reload();
                }
              });
              setisnotFound(false);
            }
          })
          .catch((error) => {
            if (error.response.status === 401) {
              window.location.href = "/unauthorized";
            } else {
              MySwal.fire({
                icon: "error",
                title: "Some Error Occurred",
                text: error.data,
              });
            }
          });
      }
<<<<<<< HEAD
      
    })
    .catch(error => {
     if(error.response.status===401){
      window.location.href="/unauthorized"
     } else{
      // MySwal.fire({
      //   icon: 'error',
      //   title: 'Some Error Occurred',
      //   text: error.data
      // });
      throw error
     } 
    });
      
=======
>>>>>>> ae1ca461709f858794f65dea8b73fed4321b5b3c
    }
  };

  const handleInputsChange = (e) => {
    const { name, value } = e.target;
    setsettings((prev) => ({
      ...prev,
      [name]: value,
    }));
  };
  const Edit = (e) => {
    e.preventDefault();
    setisnotFound(true);
  };

  const getinputs = (
    <div>
      <div className="page-header"></div>
      <div className="card">
        <div className=" card-body">
          <div className="row">
            <div className="col-12">
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
                    navigate(-1);
                  }}
                >
                  <i className="fa-solid fa-xmark"></i>
                </div>
              </div>

              <h4>Mail Settings</h4>

              <div className="form-group row">
                <label htmlFor="hostname" className="col-sm-3 col-form-label">
                  Mail Host Name<span className="text-danger">*</span>
                </label>
                <div className="col-sm-9">
                  <input
                    id="hostname"
                    name="hostname"
                    placeholder="Email Host Name"
                    value={settings.hostname}
                    className={`form-control   ${
                      errors.hostname && "is-invalid"
                    }`}
                    onChange={handleInputsChange}
                  />
                  <div className="invalid-feedback">{errors.hostname}</div>
                </div>
              </div>

              <div className="form-group row">
                <label htmlFor="port" 
                className="col-sm-3 col-form-label">
                  Mail port Name<span className="text-danger">*</span>
                </label>
                <div className="col-sm-9">
                  <input
                    id="port"
                    name="port"
                    placeholder="Email port Name"
                    value={settings.port}
                    className={`form-control   ${
                      errors.port && "is-invalid"
                    }`}
                    onChange={handleInputsChange}
                  />
                  <div className="invalid-feedback">{errors.port}</div>
                </div>
              </div>
              <div className="form-group row">
                <label htmlFor="emailid" className="col-sm-3 col-form-label">
                  Email Id <span className="text-danger">*</span>
                </label>
                <div className="col-sm-9">
                  <input
                    name="emailid"
                    id="emailid"
                    placeholder="Email Id"
                    value={settings.emailid}
                    className={`form-control   ${
                      errors.emailid && "is-invalid"
                    }`}
                    onChange={handleInputsChange}
                  />
                  <div className="invalid-feedback">{errors.emailid}</div>
                </div>
              </div>
              <div className="form-group row">
                <label htmlFor="password" className="col-sm-3 col-form-label">
                  Password<span className="text-danger">*</span>
                </label>
                <div className="col-sm-9">
                  <input
                    id="password"
                    name="password"
                    placeholder="password "
                    className={`form-control   ${
                      errors.password && "is-invalid"
                    }`}
                    value={settings.password}
                    onChange={handleInputsChange}
                  />
                  <div className="invalid-feedback">{errors.password}</div>
                </div>
              </div>
         

            <div className="btngrp">
              <button className="btn btn-primary" onClick={save}>
                Save
              </button>
            </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
  const defaultinputs = (
    <div>
      <div className="page-header"></div>
      <div className="card">
        <div className=" card-body">
          <div className="row">
            <div className="col-12">
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
                    navigate(-1);
                  }}
                >
                  <i className="fa-solid fa-xmark"></i>
                </div>
              </div>
              <h4>Mail Settings</h4>

              <div className="form-group row">
                <label
                  htmlFor="hostname"
                  className="col-sm-3 col-form-label
           "
                >
                  Mail Host Name <span className="text-danger">*</span>
                </label>
                <div className="col-sm-9">
                  <input
                    id="hostname"
                    placeholder="Host Name"
                    value={defaultsettings.hostname}
                    readOnly
                    className="form-control"
                  />
                </div>
              </div>

              <div className="form-group row">
                <label htmlFor="port" className="col-sm-3 col-form-label">
                  Mail port Name <span className="text-danger">*</span>
                </label>
                <div className="col-sm-9">
                  <input
                    id="port"
                    placeholder="port Name"
                    value={defaultsettings.port}
                    readOnly
                    className="form-control"
                  />
                </div>
              </div>
              <div className="form-group row">
                <label htmlFor="emailid" className="col-sm-3 col-form-label">
                  Email Id <span className="text-danger">*</span>
                </label>
                <div className="col-sm-9">
                  <input
                    id="emailid"
                    placeholder="Email Id"
                    className="form-control"
                    readOnly
                    value={defaultsettings.emailid}
                  />
                </div>
              </div>
              <div className="form-group row">
                <label htmlFor="password" className="col-sm-3 col-form-label">
                  Password<span className="text-danger">*</span>
                </label>
                <div className="col-sm-9">
                  <input
                    id="password"
                    placeholder="password "
                    value={defaultsettings.password}
                    className="form-control"
                    readOnly
                  />
                </div>
              </div>
            </div>

            <div className="btngrp">
              <button className="btn btn-success" onClick={Edit}>
                Edit
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
  return (
   <div>
        {isnotFound ? getinputs : defaultinputs}
        </div>
  );
};

export default MailSettings;
