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
                  text: error.response.data ? error.response.data : "error occured",
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
                  text: error.response.data ? error.response.data : "error occured",
                  icon: "warning",
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
    <div>
  <div className="page-header"></div>
  <div className="card">
    <div className='card-header'>
       <h4>
          <span>Partial Payment Settings for {courseName}</span>
        </h4>
        </div>
        <div className="card-body">
    <div className="row">
      <div className="col-12">
        <h5>
          <input type="checkbox" readOnly className="m-4" name='check' checked={enablechecked} />
          <h4 htmlFor='check' style={{ display: "inline" }}>
            Enable Partial Payment
          </h4>
        </h5>
        {enablechecked && <>
          <div className="row">
          <div className="col-md-6">
          <div className='form-group row'>
            <label className="col-sm-4 col-form-label">Course Amount</label>
            <div className="col-sm-8">
            <input type='number'  className="form-control" />
            </div>
          </div>
         
          <div className='form-group row'>
            <label className="col-sm-4 col-form-label"> No of Installments </label>
            <div className="col-sm-8">
            <input type='number'  className="form-control" value={noOfInstallments}  />
            </div>
          </div>
          </div>
        </div>
        <div className='row mt-3' style={{marginBottom:"10px",minHeight:"200px", maxHeight: "250px",overflow:"auto" }}>
            <div className="col-md-6">
            {installmentData.map((installment) => (
            <div key={installment.id}>
              <div className='form-group row pt-2'>
                <label className="col-sm-4 col-form-label"> installment{installment.installmentNumber}</label>
               <div className='col-sm-8'>
                <input
                  type='number'
                  className='form-control'
                  value={installment.installmentAmount}
                />
                </div>
              </div>
                </div>))}
                </div>
                <div className='pt-5 col-md-6'>
                {installmentData.map((installment, index) => (
          <div className='form-group row pt-2' key={index}>
            {installment.durationInDays ===0?<></>:(<>
            <label>
              Duration for {getOrdinalSuffix(index + 1)} installment
            </label>
         <div className='col-sm-8'>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <input
                type='number'
                className='form-control'
                value={installment.durationInDays}
                onChange={(e) => {
                  if(e.target.value>0){
                  const installmentdata=[...installmentData]
                  installmentdata[index+1].DurationInDays=parseInt(e.target.value, 10);
                  
                }
                }}
              />
              <label style={{ marginLeft: '5px' }}>Days</label>
            </div>
            </div></>)}
          </div>
        ))}
                </div>

            
        </div></>
}
        <div className='atbtndiv'>
          {/* <button className='btn btn-secondary' >cancel</button>
          <div></div>
          <button className="btn btn-primary" >Save</button> */}
        </div>
        </div>
        </div>
        </div>
        </div>
    </div>
  )
}

export default UpdatePartialPaymentSettings
