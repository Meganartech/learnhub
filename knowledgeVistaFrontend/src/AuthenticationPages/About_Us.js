import React, { useState, useEffect } from 'react';
import baseUrl from '../api/utils';
import axios from 'axios';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const About_Us = () => {
  
  const MySwal = withReactContent(Swal);
  const [audioFile, setAudioFile] = useState(null);
  const [errors, setErrors] = useState({});
  const [isDataList, setIsDataList] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [lastModifiedDate, setLastModifiedDate] = useState(null);

  const token=sessionStorage.getItem("token")
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(`${baseUrl}/api/v2/GetAllUser`,{
          headers:{
            "Authorization":token,
            }
          });
  
        if (response.status !== 200) {
          console.error('Error fetching data:');
        }
  
        const data = response.data;
        setIsDataList(data.dataList);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
  
    fetchData();
  
  }, []); 

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    setSelectedFile(file);
   // setLastModifiedDate(file ? new Date(file.lastModified).toLocaleString() : null);
   setLastModifiedDate(file ? new Date(file.lastModified).toISOString().replace('Z', '') : null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const formData = new FormData();
      const audioData = {
        audioFile: audioFile,
        lastModifiedDate: lastModifiedDate,
      };
      for (const key in audioData) {
        formData.append(key, audioData[key]);
      }
  
        const token=sessionStorage.getItem("token");
    
      const response=await axios.post(`${baseUrl}/api/v2/uploadfile`,formData, {
       headers: {
           Authorization: token
          }
   });
  
      if (response.status === 200) {
        MySwal.fire({
          title: "Licence Updated!",
          text: "Licence Have been updated successfully!",
          icon: "success",
          confirmButtonText: "Go to Login",
      }).then((result) => {
          if (result.isConfirmed) {
              window.location.href = "/login";
          }
        });
      }
   
    } catch (error) {
      if(error.response && error.response.status===401){
        MySwal.fire({
          title: "Error!",
          text: "you are unAuthorized to access this page",
          icon: "error",
          confirmButtonText: "OK",
        });
      }else{
        MySwal.fire({
          title: "Error!",
         text: "Some unexpected error occured try again later",
          icon: "error",
          confirmButtonText: "OK",
        });
      }
     
    
    }
  
   // setAudioFile(null);
  };


  const validateForm = () => {
    let isValid = true;
    const errors = {};
    setErrors(errors);
    return isValid;
  };


  return (
    <div className="contentbackground">
      <div className="contentinner" >

        <div className='innerFrame ' style={{ gridTemplateColumns: "none", gridTemplateRows: "none" }}>
          <div className='mainform' style={{ gridTemplateColumns: "none", gridTemplateRows: "none" }}>
            <div className='formgroup' style={{ overflow: "unset" }}>
              <h2 style={{ textDecoration: "underline", textAlign: "center" }}>Product Info</h2>

              <div style={{ paddingLeft: "3rem", paddingRight: "3rem" }}>
                <div className='row'>
                  <div className='col-6'>
                    <div className='inputgrp'>
                      <label className='labl' >Product name </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length > 0?isDataList[0].ProductName:""}</label>
                    </div>
                  </div>
                  <div className='col-6'>
                    <div className='inputgrp'>
                      <label className='lab'  >HotFix Installed (if any) </label>
                      <span>:</span>
                      <label  >NO </label>
                    </div>
                  </div>
                </div>
                <br></br>
                <div className='row'>
                  <div className='col-6'>
                    <div className='inputgrp'>
                      <label className='labl' >Company Name </label>
                      <span>:</span>
                      <label  >{isDataList && isDataList.length > 0?isDataList[0].CompanyName:""}</label>
                    </div>
                  </div>
                  <div className='col-6'>
                    <div className='inputgrp'>
                      <label className='lab' >Contact Support No </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length> 0?isDataList[0].Contact:""} </label>
                    </div>
                  </div>
                </div>
                <br></br>
                <div className='row'>
                  <div className='col-6'>
                    <div className='inputgrp'>
                      <label className='labl'  >Product Version </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length> 0?isDataList[0].version:""} </label>
                    </div>
                  </div>
                  <div className='col-6'>
                    <div className='inputgrp'>
                      <label className='lab' >Contact E-Mail </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length > 0?isDataList[0].Email:""}</label>
                    </div>
                  </div>
                </div>
                <br></br>
                <div className='row'>
                  <div className='col-6'>
                    <div className='inputgrp'>
                      <label className='labl' >Start Date </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length > 0?(isDataList[0].StartDate===""?"NA":isDataList[0].StartDate):""}</label>
                    </div>
                  </div>
                  <div className='col-6'>
                    <div className='inputgrp'>
                      <label className='lab'>End Date </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length > 0?(isDataList[0].EndDate===""?"NA":isDataList[0].EndDate):""}</label>
                    </div>
                  </div>
                </div>
                <br></br>
                <div className='row'>
                  <div className='col-6'>
                    <div className='inputgrp '>
                      <label className='labl' >Version</label>
                      <span>:</span>
                      <label  >{isDataList && isDataList.length > 0?isDataList[0].Type:""}</label>
                    </div>
                  </div>

                </div>
                <br></br>
                <form className='form-container' onSubmit={handleSubmit}>
                  <div className='row'>
                    <div className='col-6'>
                      <div className='inputgrp'>
                        <label className='labl' >Add New License File </label>
                        <span>:</span>
                 
                        <input
                          type='file'
                          className=''
                          placeholder='Choose  File'
                          accept=".xml"
                          name='audioFile'
                          onChange=
                          {(e) => {
                            setAudioFile(e.target.files[0]);
                            handleFileChange(e);
                          }}
                          style={{ padding: "0px", width: "20rem" }}
                        />
                      </div>
                    </div>
                    <div className='col-6'>
                      <div className='inputgrp'>
                        <label className='lab' > Feedback</label>
                        <span>:</span>
                       
                        <input
                          className='disabledbox'
                          readOnly
                          value=" " />
                      </div>
                    </div>
                  </div>
                 
                  <div className='modal-footer'>
                    <input
                      type='submit'
                      value='Upload'
                      className='btn btn-primary'
                    />
                  </div>
                </form>


              </div>


            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default About_Us