import React, { useState, useEffect } from 'react';
import baseUrl from '../api/utils';
import axios from 'axios';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useNavigate } from 'react-router-dom';

const LicenceDetails = () => {
 
  const MySwal = withReactContent(Swal);
  const [audioFile, setAudioFile] = useState(null);
  const [errors, setErrors] = useState({audioFile:""});
  const [isDataList, setIsDataList] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [lastModifiedDate, setLastModifiedDate] = useState(null);
  const [licenceDetails, setLicenceDetails] = useState({}); // State for licence details
const token=sessionStorage.getItem("token");
const [Activeprofile,setActiverofile]=useState();
const navigate=useNavigate();
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
    const { name, value } = event.target;
    let error = '';
    const file = event.target.files[0];
    const fileExtension = file.name.split('.').pop().toLowerCase();
    if (fileExtension !== 'xml') {
      error="please select .xml file"
      setErrors(prevErrors => ({
        ...prevErrors,
        [name]: error
      }));
      setSelectedFile(null);
    }else{
    setAudioFile(file);
    setSelectedFile(file);

   // setLastModifiedDate(file ? new Date(file.lastModified).toLocaleString() : null);
   setLastModifiedDate(file ? new Date(file.lastModified).toISOString().replace('Z', '') : null);
   setErrors({ audioFile: "" });  
  }
   };

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {

    if (!audioFile) {
      setErrors({ audioFile: "Please select a file ." }); // Handle missing file
      return;
    }
    if(errors.audioFile){
      return;
    }
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
      <div className="contentinner p-4 pb-4">
      <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
              <h2 style={{ textDecoration: "underline", textAlign: "center" }}>Licence Info</h2>
  
              <div className='twosplit' style={{marginBottom:"10px" ,gap:"20px"}}>
                    <div className="inputgrp2">
                      <label >Product name </label>
                      <span>:</span>
                      <label>{licenceDetails.ProductName || 'NA'}</label>
                    </div>
                 
                    {/* <div className="inputgrp2">
                      <label >Company Name </label>
                      <span>:</span>
                      <label>{licenceDetails.CompanyName || 'NA'}</label>
                    </div>
                  */}
              
                    <div className="inputgrp2">
                      <label >Trainers </label>
                      <span>:</span>
                      <label>{licenceDetails.trainer || 'NA'}</label>
                    </div>
                 
                    <div className="inputgrp2">
                      <label >Students </label>
                      <span>:</span>
                      <label>{licenceDetails.student || 'NA'}</label>
                    </div>
                  
               
                    <div className="inputgrp2">
                      <label >Course </label>
                      <span>:</span>
                      <label>{licenceDetails.course || 'NA'}</label>
                    </div>
                
                    <div className="inputgrp2">
                      <label >Type </label>
                      <span>:</span>
                      <label>{licenceDetails.Type || 'NA'}</label>
                    </div>
                 
                
                    <div className="inputgrp2">
                      <label >Start Date </label>
                      <span>:</span>
                      <label>{licenceDetails.StartDate}</label>
                    </div>
                  
                    <div className="inputgrp2">
                      <label >End Date </label>
                      <span>:</span>
                      <label>{licenceDetails.EndDate}</label>
                    </div>
              
                    <div className="inputgrp2">
                      <label >Storage Size </label>
                      <span>:</span>
                      <label>{licenceDetails.StorageSize}</label>
                    </div>

                 
             
               
                {Activeprofile!=="SAS" &&(
                <form onSubmit={handleSubmit}>
                      <div className='inputgrp2' style={{height:"80px",alignItems:"flex-start"}} >
                        <label >Add New License </label>
                        <span>:</span>
                  <div>         
                  <div>
                   <label htmlFor='audioFile' 
                   style={{width:"200px"}} 
                   className='file-upload-btn'>  upload file</label>
                    {selectedFile && (
                <p style={{fontSize:"smaller"}}>{selectedFile.name}</p> )}
            <div className="text-danger" >
            {errors.audioFile}
          </div>
             </div>
                        <input
                          type='file'
                          className={`file-upload ${errors.audioFile && 'is-invalid'}`}
                          id="audioFile"
                          accept=".xml"
                          name='audioFile'
                          onChange=
                          {handleFileChange}
                        /> 
                      
                         
                        </div>
                  </div>
               
                    <input
                      type='submit'
                      value='Upload'
                      className='btn btn-primary'
                    />
                </form>
)}
</div>

          <div className='modal-footer'>
          <input
            onClick={() => window.open('https://learnhub.vsmartengine.com/', '_blank')}
            value='Upgrade Licence here'
            className='btn btn-warning'
            readOnly
          />
        </div>
  </div>
  </div>     
  );
  
}

export default LicenceDetails