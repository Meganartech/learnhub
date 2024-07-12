import React, { useEffect, useState } from 'react';
import axios from 'axios'; // Assuming axios is installed
import baseUrl from '../api/utils';

const LicenceDetails = () => {
  const [licenceDetails, setLicenceDetails] = useState({}); // State for licence details
const token=sessionStorage.getItem("token");
  useEffect(() => {
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

    fetchData();
  }, []);
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
              </div>
            </div>
          </div>
          <div className='modal-footer'>
          <input
            onClick={() => window.open('https://www.youtube.com/', '_blank')}
            value='Upgrade'
            className='btn btn-primary'
          />
        </div>
        </div>
      </div>
    </div>
  );
  
}

export default LicenceDetails