import React, { useState,useEffect } from 'react';
import "../css/certificate.css";
import signature from "../images/signature.png"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';


const CertificateInputs = () => {

  const token=sessionStorage.getItem("token");
  const MySwal = withReactContent(Swal);
  const [defaultcerti,setdefaultcerti]=useState({
    institutionName: '',
    ownerName: '',
    qualification: '',
    address: '',
    authorizedSign: null,
  
  });

  
  const [certificate, setCertificate] = useState({
    institutionName: '',
    ownerName: '',
    qualification: '',
    address: '',
    authorizedSign: null, 
  });
const [isnotFound,setisnotFound]=useState();
const[sign,setsign]=useState();
const [getSign,setgetSign]=useState();
const[isinitial,setisinitial]=useState(true);

useEffect(() => {
  const fetchCertificate = async () => {
    try {
      const certificatedata = await axios.get(`${baseUrl}/certificate/viewAll`);
      if (certificatedata.status === 200) {
        const certificateJson = certificatedata.data;
        setsign(`data:image/jpeg;base64,${certificateJson.authorizedSign}`);
        setdefaultcerti(certificateJson);
        setCertificate(certificateJson);
        setisinitial(false);
      }
    } catch (error) {
      if (error.response && error.response.status === 404) {
        setisnotFound(true);
        
      }
    }
  };
  fetchCertificate();
}, []);


const handleEdit=()=>{
setgetSign(`data:image/jpeg;base64,${certificate.authorizedSign}`)
setisnotFound(true);

}

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setgetSign(e.target.result)
        setCertificate(prevState => ({
          ...prevState,
          authorizedSign: file // Store file object in certificate state
        }));
      };
      reader.readAsDataURL(file);
    } 

    // Reset input value
    event.target.value = null;
  };

  const handleChange = (event) => {
    const { id, value } = event.target;
    setCertificate(prevState => ({
      ...prevState,
      [id]: value
    }));
  };

  const handleSave = async () => {
    // Check if all fields are filled
    if (
        certificate.institutionName &&
        certificate.ownerName &&
        certificate.qualification &&
        certificate.address &&
        certificate.authorizedSign
    ) {
      try {
        const formData = new FormData();
        formData.append('institutionName', certificate.institutionName);
        formData.append('ownerName', certificate.ownerName);
        formData.append('qualification', certificate.qualification);
        formData.append('address', certificate.address);
        formData.append('authorizedSign', certificate.authorizedSign); 
        formData.append("certificateId", certificate.certificateId);
      
        const url = isinitial ? `${baseUrl}/certificate/add` : `${baseUrl}/certificate/Edit`;
        const method = isinitial ? "POST" : "PATCH";
      let response;
        if (method === 'POST') {
           response = await axios.post(url, formData, {
              headers: {
                  "Authorization": token,
              }
          });
      } else if (method === 'PATCH') {
           response = await axios.patch(url, formData, {
              headers: {
                  "Authorization": token,
              }
          });
      }
        
        const data = response.data;
        if (response.status===200) {
          MySwal.fire({
            title: "Saved",
            text: data.message,
            icon: "success",
            confirmButtonText: "OK",
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.reload();
            }
          });
        }
      } catch (error) {
      
        MySwal.fire({
          title: "Error!",
          text: error.response.data,
          icon: "error",
          confirmButtonText: "OK",
        });
      
    }
    
          } else {
        alert('Please fill in all fields before saving.');
    }
};

const certificateInputs=(

  <div className='contentinner'>
    <div className='innerFrame'>
<h1>Certificate Form</h1>
<div className='mainform'>
  <div className='profile-picture'>
  <div className='image-group'>
  <img
            src={getSign || signature}
            alt='Signature'
            className='profile-image'
          />
      
    </div>
    <label htmlFor='fileInput' className='file-upload-btn'>
      Upload
    </label>
    <input
      type='file'
      id='fileInput'
      className='file-upload'
      accept='image/*'
      onChange={handleFileChange}
    />
  </div>


  <div className='formgroup'>
    <div className='inputgrp'>
      <label htmlFor='institutionName'>Institution Name</label>
      <span>:</span>
      <input
        id='institutionName'
        placeholder='Institution Name'
        value={certificate.institutionName}
        onChange={handleChange}
        required
      />
    </div>

    <div className='inputgrp'>
      <label htmlFor='ownerName'>Owner Name</label>
      <span>:</span>
      <input
        id='ownerName'
        placeholder='Owner Name'
        value={certificate.ownerName}
        onChange={handleChange}
        required
      />
    </div>

    <div className='inputgrp'>
      <label htmlFor='qualification'>Qualification</label>
      <span>:</span>
      <input
      name='qualification'
        id='qualification'
        placeholder='Qualification'
        value={certificate.qualification}
        onChange={handleChange}
        required
      />
    </div>

    <div className='inputgrp'>
      <label htmlFor='address'>Address</label>
      <span>:</span>
      <input
        id='address'
        placeholder='Address'
        value={certificate.address}
        onChange={handleChange}
        required
      />
    </div>

   
  </div>
</div>
<div className='btngrp'>
  <button className='btn btn-primary' onClick={handleSave}>Save</button>
</div>
</div>
</div>);

const certificateView=(
  <div className='contentinner'>
  <div className='innerFrame'>
        <h3>Certificate Templates</h3>
        <div className='mainform'>
          <div className='profile-picture'>
          <div className='image-group'>
              <img src={sign} alt='signature' />
            </div>
          
          </div>
        

          <div className='formgroup'>
            <div className='inputgrp'>
              <label htmlFor='institutionName'>Institution Name</label>
              <span>:</span>
              <input
                id='institutionName'
                placeholder='Institution Name'
                value={defaultcerti.institutionName}
                className='disabledbox'
                readOnly
              />
            </div>

            <div className='inputgrp'>
              <label htmlFor='ownerName'>Owner Name</label>
              <span>:</span>
              <input
                id='ownerName'
                placeholder='Owner Name'
                value={defaultcerti.ownerName}
                className='disabledbox'
                readOnly
              />
            </div>

            <div className='inputgrp'>
              <label htmlFor='qualification'>Qualification</label>
              <span>:</span>
              <input
                id='qualification'
                placeholder='Qualification'
                value={defaultcerti.qualification}
                className='disabledbox'
                readOnly
              />
            </div>

            <div className='inputgrp'>
              <label htmlFor='address'>Address</label>
              <span>:</span>
              <input
                id='address'
                placeholder='Address'
                value={defaultcerti.address}
                className='disabledbox'
                readOnly
               
              />
            </div>

           
          </div>
        </div>
        <div className='btngrp'>
  <button className='btn btn-primary' onClick={handleEdit}>Edit</button>
</div>
      </div>
      </div>
)

  return (
    <div className='contentbackground'>
      {isnotFound ? certificateInputs : certificateView}
    </div>
  );
};

export default CertificateInputs;
