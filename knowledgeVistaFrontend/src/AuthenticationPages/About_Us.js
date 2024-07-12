import React, { useState, useEffect } from 'react';
import baseUrl from '../api/utils';
import axios from 'axios';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const About_Us = () => {
  
  const MySwal = withReactContent(Swal);
  const [audioFile, setAudioFile] = useState(null);
  const [Activeprofile,setActiverofile]=useState();
  const [errors, setErrors] = useState({});
  const [isDataList, setIsDataList] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [lastModifiedDate, setLastModifiedDate] = useState(null);

  const token=sessionStorage.getItem("token")
  useEffect(() => {
    const fetchactive=async()=>{
      const active=await axios.get(`${baseUrl}/Active/Environment`)
      
      setActiverofile(active.data);
    }
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
    fetchactive();
  
  }, []); 

 
  return (
    <div className="contentbackground" style={{height:"90vh"}}>
      <div className="contentinner"  >

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
             
                <br></br>
                
                <form className='form-container'>
                <div className='row'>
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
                      value='Send'
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