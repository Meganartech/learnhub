import axios from 'axios';
import React, { useEffect, useState } from 'react'
import baseUrl from '../api/utils';
//component.css

const Footer = () => {
  const [FooterDetails, setFooterDetails] = useState({
    copyright: "",
    contact: "",
    supportmail: "",
    institutionmail: "",
  });
  const Environment=sessionStorage.getItem("Activeprofile");
  const token = sessionStorage.getItem("token");
  useEffect(()=>{
    const getFooterdetails=async()=>{
     try{
      if(Environment==="VPS"){
      if(token){
     const response=await axios.get(`${baseUrl}/Get/FooterDetails`,{
       headers:{
         Authorization:token
       }
     })
     if(response.status===200){
       setFooterDetails(response.data)
     }
    }else{
      const response2=await axios.get(`${baseUrl}/all/get/FooterDetails`)
      if(response2.status===200){
        setFooterDetails(response2.data)
      }
    }
  }
   }catch(error){
     console.log(error)
     setFooterDetails({copyright: "",
      contact: "",
      supportmail: "",
      institutionmail: "",}); 
   }
  
    }
    getFooterdetails();
 },[]

)
  return (
    <div>
      <div className='part2'>
      <div>{FooterDetails.copyright ? FooterDetails.copyright : "\u00A9 2024 All rights reserved"}</div>
        <div>{FooterDetails.supportmail? FooterDetails.supportmail:"support@vsmartengine.com"}</div>
        <div>{FooterDetails.contact? FooterDetails.contact :"Ph : 91-9566191759"}</div>
        <div>{FooterDetails.institutionmail? FooterDetails.institutionmail :"learnhub.vsmartengine.com"}</div>
      </div>
      </div>
   
  )
}

export default Footer
