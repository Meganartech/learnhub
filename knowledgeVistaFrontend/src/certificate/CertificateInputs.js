import React, { useState,useEffect } from 'react';

import "../css/certificate.css";
import signature from "../images/signature.png"
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";


const CertificateInputs = () => {
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
    authorizedSign: null, // Change to file input
  });
const [isnotFound,setisnotFound]=useState();
const[sign,setsign]=useState();
const [getSign,setgetSign]=useState();
useEffect(() => {
  const fetchCertificate = async () => {
    try {
      const certificatedata = await fetch("http://localhost:8080/certificate/viewAll");
      if (certificatedata.ok) {
        const certificateJson = await certificatedata.json();
       
        setsign(`data:image/jpeg;base64,${certificateJson.authorizedSign}`);
        setdefaultcerti(certificateJson);
      } else if (certificatedata.status === 404) {
        setisnotFound(true);
      }
    } catch (error) {
      console.error("Error fetching certificate:", error);
    }
  };
  fetchCertificate();
}, [isnotFound]);
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
        
           
              console.log(certificate);
            const response = await fetch("http://localhost:8080/certificate/add", {
                method: "POST",
                body: formData
            });
            if (response.ok) {
            
              setisnotFound(false);
          }

           
        } catch (error) {
            console.error(error);
        }
    } else {
        alert('Please fill in all fields before saving.');
    }
};
const certificateInputs=(

  <div className='contentinner'>
    <div className='innerFrame'>
<h3>Certificate Form</h3>
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
                readOnly
               
              />
            </div>

           
          </div>
        </div>
       
      </div></div>
)

  return (
    <div className='contentbackground'>
      {isnotFound ? certificateInputs : certificateView}
    </div>
  );
};

export default CertificateInputs;
