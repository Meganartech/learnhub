import React, { useEffect, useState } from "react";
import baseUrl from "../api/utils";
import errorimg from "../images/errorimg.png";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import altBatchImage from "../images/altBatchImage.jpg";

const Mybatches = () => {
     const token = sessionStorage.getItem("token");
      const role=sessionStorage.getItem("role");
      const MySwal = withReactContent(Swal);
      const [batch, setbatch] = useState([]);
      const Currency = sessionStorage.getItem("Currency");
      const navigate = useNavigate();
      useEffect(() => {
        const fetchBatch = async () => {
          try {
            const response = await axios.get(`${baseUrl}/Batch/getEnrolledBatch`, {
              headers: {
                Authorization: token,
              },
            });
            const data = response.data;
            console.log(data);
            setbatch(data);
          } catch (err) {
            console.log(err);
          }
        };
        fetchBatch();
      }, []);
  return (
    <div>
      <div className="page-header"></div>
      {batch.length > 0 ? (
        <div className="row">
          {batch
            .slice()
            .reverse()
            .map((item) => (
              <div className="col-md-6 col-xl-3 course" key={item.id}>
                <div className="card mb-3">
                  {item.batchImage ? (
                    <img
                      style={{ cursor: "pointer" }}
                      title={`${item.batchTitle} image`}
                      className="img-fluid card-img-top"
                      src={`data:image/jpeg;base64,${item.batchImage}`}
                      onError={(e) => {
                        e.target.src = errorimg; // Use the imported error image
                      }}
                      alt="Batch"
                    />
                  ) : (
                    <div
                      className="img-fluid card-img-top "
                      title={item.batchTitle}
                      style={{
                        backgroundImage: `url(${altBatchImage})`, // Set the background image as altBatchImage
                        backgroundSize: "cover", // Ensure the image covers the div
                        backgroundPosition: "center", 
                        height: "150px", // Set a fixed height or adjust accordingly
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                      }}
                    >
                      {/* var(--primary) */}
                     <h4 style={{color:'white',
                       wordWrap: "break-word", // Ensure the text wraps if it overflows
                       margin: "0", // Remove any default margin
                       padding: "0 10px", // Add some padding for better readability if needed
                       textAlign:"center"
                    
                     }}> {item.batchTitle}</h4>
                    </div>
                  )}
                  <div className="card-body">
                    <h5
                      className="courseName"
                      title={item.batchTitle}
                      style={{ cursor: "pointer" }}
                      // onClick={(e) => {
                      //   handleClick(
                      //     e,
                      //     item.courseId,
                      //     item.amount,
                      //     item.courseUrl
                      //   );
                      // }}
                    >
                      {item.batchTitle}
                    </h5>
              
                    <p title={item.courseNames} className="batchlist">
                      <b> Courses :</b> {item.courseNames}
                    </p>
                    <p title={item.trainerNames} className="batchlist">
                      <b>Trainers :</b> {item.trainerNames}
                    </p>
                    <p title={item.duration} className="batchlist">
                      <b>Duration :</b> {item.duration}
                    </p>
                
                  </div>
                </div>
              </div>
            ))}
        </div>
      ) : (
        <div>
          <h1 className="text-light ">No Batch Found </h1>
        </div>
      )}
    </div>
  )
}

export default Mybatches