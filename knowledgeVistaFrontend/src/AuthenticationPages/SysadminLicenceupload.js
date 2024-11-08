import React, { useState } from 'react'
import baseUrl from '../api/utils';
import axios from 'axios';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
const SysadminLicenceupload = () => {
    
  const MySwal = withReactContent(Swal);
  const [audioFile, setAudioFile] = useState(null);
  
  const [selectedFile, setSelectedFile] = useState(null);
  const token=sessionStorage.getItem("token")
  const handleFileChange = (event) => {
    const file = event.target.files[0];
    setSelectedFile(file);
  };

    const handleSubmit = async (e) => {
        e.preventDefault();
      
        try {
          const formData = new FormData();
          const audioData = {
            audioFile: audioFile,
          };
          for (const key in audioData) {
            formData.append(key, audioData[key]);
          }
      
            const token=sessionStorage.getItem("token");
        
          const response=await axios.post(`${baseUrl}/api/Sysadmin/uploadLicence`,formData, {
           headers: {
               Authorization: token
              }
       });
      
          if (response.status === 200) {
            MySwal.fire({
              title: "Licence Updated!",
              text: "Licence Have been updated successfully!",
              icon: "success",
              confirmButtonText: "Reload",
          }).then((result) => {
              if (result.isConfirmed) {
                  window.location.reload();
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
            // MySwal.fire({
            //   title: "Error!",
            //  text: "Some unexpected error occured try again later",
            //   icon: "error",
            //   confirmButtonText: "OK",
            // });
            throw error
          }
        }
    }
  return (
    <div className='contentbackground'>
        <div className='contentinner'>
             <form className='form-container' onSubmit={handleSubmit}>
                 
                  
                     
                      <div className='inputgrp' >
                        <label style={{ display:"block"}} >Add New License File </label>
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
                 
                 
                  <div className='modal-footer'>
                    <input
                      type='submit'
                      value='Upload'
                      className='btn btn-primary'
                    />
                  </div>
                </form></div>
    </div>
  )
}

export default SysadminLicenceupload