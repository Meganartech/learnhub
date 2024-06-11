import React, { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import baseUrl from '../../api/utils';
import axios from 'axios';

import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
const UpdatePartialPaymentSettings = () => {
    const {courseName,courseId}=useParams();
    const MySwal = withReactContent(Swal);
  const token=sessionStorage.getItem("token")
   const [enablechecked,setenablechecked]=useState();
   const [installmentData, setInstallmentData] = useState([]);
   const[noOfInstallments,setNoOfInstallments]=useState();
const navigate=useNavigate();
   function getOrdinalSuffix(num) {
    const ones = num % 10;
    const tens = Math.floor(num / 10) % 10;
    if (tens === 1) {
      return num + 'th';
    } else {
      switch (ones) {
        case 1:
          return num + 'st';
        case 2:
          return num + 'nd';
        case 3:
          return num + 'rd';
        default:
          return num + 'th';
      }
    }
  }
  useEffect(() => {
      const fetchpartpaydata = async () => {
          try {
              const response = await axios.get(`${baseUrl}/viewPaymentList/${courseId}`, {
                  headers: {
                      "Authorization": token
                  }
              });
              const data = await response.data;
            
              if (response.status===200) {
                  setenablechecked(true);
                  setInstallmentData(data)
                  setNoOfInstallments(installmentData.length)
              }   
          } catch (error) {
            if(error.response){
              if(error.response.status===401){
                MySwal.fire({
                  title: "Un Authorized",
                  text: error.response.data,
                  icon: "error",
                }).then((result) => {
                  if (result.isConfirmed) {
                    navigate(-1);
                     }
                });
              }else if(error.response.status===404){
                setenablechecked(false)
                MySwal.fire({
                  title: "Not Found",
                  text: error.response.data,
                  icon: "error",
                }).then((result) => {
                  if (result.isConfirmed) {
                    navigate(-1);
                     }
                });
              }
            }else{
              MySwal.fire({
                title: "Error!",
                text: "An error occurred . Please try again later.",
                icon: "error",
                confirmButtonText: "OK",
              });
            }
          }
      }
  
      fetchpartpaydata(); // Call the async function
  
      // Add any dependencies if needed
  }, []);
//   const handlenoofinstallmentchange = (e) => {
//     const newNoOfInstallments = parseInt(e.target.value, 10);
//     if (newNoOfInstallments >= 2) {
//       setNoOfInstallments((prevState)=>({

//       }));

//       setFormData((prevState) => ({
//         ...prevState,
//         [name]: value,
//       }));
//     }
//   };
  return (
    <div className='contentbackground'>
    <div className='contentinner'>
       <h2>
          <span style={{ textDecoration: 'underline' }}>Partial Payment Settings for {courseName}</span>
        </h2>
        <h5>
          <input type="checkbox" className="m-4" name='check' checked={enablechecked} onChange={()=>{setenablechecked(!enablechecked)}}/>
          <p htmlFor='check' style={{ display: "inline" }}>
            Enable Partial Payment
          </p>
        </h5>
        {enablechecked && <>
        <div className='mainform2'>
          <div className='inputgrp2'>
            <label>Course Amount</label>
            <span>:</span>
            <input type='number'  />
          </div>
         
          <div className='inputgrp2'>
            <label> No of Installments </label>
            <span>:</span>
            <input type='number' value={noOfInstallments}  />
          </div>
        </div>
        <div className='mainform2 mt-3' style={{ height: '320px' }}>
            <div>
            {installmentData.map((installment) => (
            <div key={installment.id}>
              <div className='inputgrp2 pt-2'>
                <label> installment{installment.installmentNumber}</label>
                <span>:</span>
                <input
                  type='number'
                  value={installment.installmentAmount}
                />
              </div>
                </div>))}
                </div>
                <div className='pt-5'>
                {installmentData.map((installment, index) => (
          <div className='inputgrp2 pt-2' key={index}>
            {installment.durationInDays ===0?<></>:(<>
            <label>
              Duration for {getOrdinalSuffix(index + 1)} installment
            </label>
            <span>:</span>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <input
                type='number'
                value={installment.durationInDays}
                onChange={(e) => {
                  if(e.target.value>0){
                  const installmentdata=[...installmentData]
                  installmentdata[index+1].DurationInDays=parseInt(e.target.value, 10);
                  
                }
                }}
              />
              <label style={{ marginLeft: '5px' }}>Days</label>
            </div></>)}
          </div>
        ))}
                </div>

            
        </div></>
}
        <div className='atbtndiv'>
          {/* <button className='btn btn-primary' >cancel</button>
          <div></div>
          <button className="btn btn-primary" >Save</button> */}
        </div>
        </div>
    </div>
  )
}

export default UpdatePartialPaymentSettings
