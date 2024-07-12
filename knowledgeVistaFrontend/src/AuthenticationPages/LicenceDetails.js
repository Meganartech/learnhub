import React, { useState, useEffect } from 'react';
import baseUrl from '../api/utils';
import axios from 'axios';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const LicenceDetails = () => {
  const MySwal = withReactContent(Swal);
  const [audioFile, setAudioFile] = useState(null);
  const [errors, setErrors] = useState({});
  const [isDataList, setIsDataList] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [lastModifiedDate, setLastModifiedDate] = useState(null);
  const [licenceDetails, setLicenceDetails] = useState({}); // State for licence details
const token=sessionStorage.getItem("token");
const [Activeprofile,setActiverofile]=useState();
  useEffect(() => {
    const fetchactive=async()=>{
      const active=await axios.get(`${baseUrl}/Active/Environment`)
      
      setActiverofile(active.data);
    }
    const fetchData = async () => {
      try {
        const response = await axios.get(`${baseUrl}/licence/getinfo`, {
          headers: {
            Authorization: token,
          },
        });
        const { data } = response; // Destructure data from response

        // Filter out unwanted properties
        const filteredData = {
          ProductName: data.product_name,
          CompanyName: data.company_name,
          trainer: data.trainer, // Assuming Contact refers to trainer
          student:data.students,
          course:data.course,
          Email: data.email, // Assuming email property exists
          version: data.version, // Assuming version property exists
          Type: data.type,
          StartDate: data.start_date ? new Date(data.start_date).toLocaleDateString() : 'NA',
          EndDate: data.end_date ? new Date(data.end_date).toLocaleDateString() : 'NA',
          StorageSize: `${data.storagesize || 0} GB`, // Handle null storage size
        };

        setLicenceDetails(filteredData);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

  
    fetchactive();
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
    <div className="contentbackground" style={{ height: "90vh" }}>
      <div className="contentinner">
        <div className="innerFrame" style={{ gridTemplateColumns: "none", gridTemplateRows: "none" }}>
          <div className="mainform" style={{ gridTemplateColumns: "none", gridTemplateRows: "none" }}>
            <div className="formgroup" style={{ overflow: "unset" }}>
              <h2 style={{ textDecoration: "underline", textAlign: "center" }}>Licence Info</h2>
  
              <div style={{ paddingLeft: "3rem", paddingRight: "3rem" }}>
                <div className="row">
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="labl">Product name </label>
                      <span>:</span>
                      <label>{licenceDetails.ProductName || 'NA'}</label>
                    </div>
                  </div>
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="lab">Company Name </label>
                      <span>:</span>
                      <label>{licenceDetails.CompanyName || 'NA'}</label>
                    </div>
                  </div>
                </div>
                <br />
                <div className="row">
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="labl">Trainers </label>
                      <span>:</span>
                      <label>{licenceDetails.trainer || 'NA'}</label>
                    </div>
                  </div>
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="lab">Students </label>
                      <span>:</span>
                      <label>{licenceDetails.student || 'NA'}</label>
                    </div>
                  </div>
                </div>
                <br />
                <div className="row">
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="labl">Course </label>
                      <span>:</span>
                      <label>{licenceDetails.course || 'NA'}</label>
                    </div>
                  </div>
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="lab">Type </label>
                      <span>:</span>
                      <label>{licenceDetails.Type || 'NA'}</label>
                    </div>
                  </div>
                </div>
                <br />
                <div className="row">
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="labl">Start Date </label>
                      <span>:</span>
                      <label>{licenceDetails.StartDate}</label>
                    </div>
                  </div>
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="lab">End Date </label>
                      <span>:</span>
                      <label>{licenceDetails.EndDate}</label>
                    </div>
                  </div>
                </div>
                <br />
                <div className="row">
                  <div className="col-6">
                    <div className="inputgrp">
                      <label className="labl">Storage Size </label>
                      <span>:</span>
                      <label>{licenceDetails.StorageSize}</label>
                    </div>
                  </div>
                </div>
                <div className='row'>
                  
                <div className='col-6'>
                {Activeprofile!=="SAS" &&(
                <form className='form-container' onSubmit={handleSubmit}>
                 
                     
                      <div className='inputgrp mt-3'>
                        <label className='labl ' >Add New License </label>
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
               
                    <input
                      type='submit'
                      value='Upload'
                      className='btn btn-primary'
                    />
                </form>
)}
</div>
                </div>
              </div>
            </div>
          </div>
         

          <div className='modal-footer'>
          <input
            onClick={() => window.open('https://www.youtube.com/', '_blank')}
            value='Upgrade Licence here'
            className='btn btn-warning'
          />
        </div>
  
        </div>
      </div>
    </div>
  );
  
}

export default LicenceDetails