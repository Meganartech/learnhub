import React, { useState, useEffect } from 'react';
import baseUrl from '../api/utils';
import axios from 'axios';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useNavigate } from 'react-router-dom';

const About_Us = () => {
  
  const [isDataList, setIsDataList] = useState(null);
  const navigate=useNavigate();
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
        throw error
      }
    };
  
    fetchData();
  
  }, []); 

 
  return (
    <div className="contentbackground" >
      <div className="contentinner p-4"  >
      <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
              <h2 style={{ textDecoration: "underline", textAlign: "center" }}>Product Info</h2>

           <div className='twosplit'>
              
                    <div className='inputgrp2'>
                      <label >Product name </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length > 0?isDataList[0].ProductName:""}</label>
                    </div>
                 
                    <div className='inputgrp2'>
                      <label   >HotFix Installed (if any) </label>
                      <span>:</span>
                      <label  >NO </label>
                    </div>
                
                    <div className='inputgrp2'>
                      <label  >Company Name </label>
                      <span>:</span>
                      <label  >{isDataList && isDataList.length > 0?isDataList[0].CompanyName:""}</label>
                    </div>
               
                    <div className='inputgrp2'>
                      <label  >Contact Support No </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length> 0?isDataList[0].Contact:""} </label>
                    </div>
                 
                
                    <div className='inputgrp2'>
                      <label   >Product Version </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length> 0?isDataList[0].version:""} </label>
                    </div>
                 
                    <div className='inputgrp2'>
                      <label  >Contact E-Mail </label>
                      <span>:</span>
                      <label >{isDataList && isDataList.length > 0?isDataList[0].Email:""}</label>
                    </div>
                
                         
               
               
                      <div className='inputgrp2'>
                        <label  > Feedback</label>
                        <span>:</span>
                        <input
                        style={{width:"100%"}}
                          className='disabledbox'
                          readOnly
                          value=" " />
                      </div>
                  
                 
                 
                
                </div> 
                <div className='modal-footer mt-5'>
                    <button
                      className='btn btn-primary'
                    > Send</button>
                  </div>
              </div>

          </div>
  
     
  )
}

export default About_Us