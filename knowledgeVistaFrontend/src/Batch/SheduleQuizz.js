import { useNavigate, useParams } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import axios from "axios";
import baseUrl from "../api/utils";
import { useEffect, useState } from "react";

const SheduleQuizz = () => {
  const { batchTitle, batchId, courseName, courseId } = useParams();
  const navigate = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const MySwal = withReactContent(Swal);
  const token = sessionStorage.getItem("token");
  const [schedule, setSchedule] = useState([]);

  useEffect(() => {
    const fetchSheduleQuizzDetails = async () => {
      try {
        setSubmitting(true);
        const response = await axios.get(
          `${baseUrl}/Quizz/getSheduledQuizz/${courseId}/${batchId}`,
          {
            headers: {
              Authorization: token,
            },
          }
        );
        if (response.status === 200) {
          setSubmitting(false);
          setSchedule(response.data);
        }
      } catch (error) {
        setSubmitting(false);
        if (error.response && error.response.status === 401) {
          navigate("/unauthorized");
        } else {
          throw error;
        }
      }
    };
    fetchSheduleQuizzDetails();
  }, []);

  // const handleDateChange = (index, field, value) => {
  //   setSchedule((prev) => {
  //     const newSchedule = [...prev];
  //     newSchedule[index][field] = value;
  //     newSchedule[index].enabled = true; // Enable save button on change
  //     return newSchedule;
  //   });
  // };
  const handleDateChange = (index, field, value) => {
    setSchedule((prev) => {
      const newSchedule = [...prev];
      newSchedule[index][field] = value;

      // Validate the start date to be greater than the current date
      if (field === "startDate") {
        const currentDate = new Date().toISOString().slice(0, 16); // Current date in YYYY-MM-DDTHH:MM format
        if (newSchedule[index].startDate < currentDate) {
          MySwal.fire({
            title: "Invalid Date",
            text: "Start date and time must be in the future.",
            icon: "warning",
          });
          newSchedule[index][field] = ""; // Reset the value
        }
      }

      // Validate end date not to be less than start date
      if (field === "endDate" && newSchedule[index].startDate && newSchedule[index].endDate) {
        if (newSchedule[index].endDate < newSchedule[index].startDate) {
         
          newSchedule[index][field] = ""; // Reset the value
        }
      }

      newSchedule[index].enabled = true; // Enable save button on change
      return newSchedule;
    });
  };


  // API call function
   const saveOrUpdateSchedule = async (quiz, index) => {
    try {
      const response = await axios.post(
        `${baseUrl}/Quizz/Shedule/${courseId}/${batchId}`,
        null,
        {
          params: {
            quizzId: quiz.quizzId,
            batchId,
            startDate: quiz.startDate,
            endDate: quiz.endDate,
          },
          headers: { Authorization: token },
        }
      );
      if (response.status === 200) {
        MySwal.fire({ title:`${response?.data}`, text: `Shedule ${response?.data} SuccessFully`, icon: "success" });
        setSchedule((prev) => {
          const newSchedule = [...prev];
          newSchedule[index].enabled = null; // Disable button after saving
          return newSchedule;
        });
      } else if (response.status === 204) {
        MySwal.fire({ title: "Not Found!", text: "Error occurred", icon: "warning" });
      }
    } catch (error) {
      if (error.response?.status === 401) navigate("/unauthorized");
    }
  };

 


  return (
    <div>
      <div className="page-header">
        <div className="page-block">
          <div className="row align-items-center">
            <div className="col-md-12">
              <div className="page-header-title">
                <h5 className="m-b-10">Quizz</h5>
              </div>
              <ul className="breadcrumb">
                <li className="breadcrumb-item">
                  <a href="#" onClick={() => navigate("/batch/viewall")}>
                    <i className="fa-solid fa-object-group"></i>
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a href="#" onClick={() => navigate("/batch/viewall")}>
                    {batchTitle}
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a href="#" onClick={() => navigate(`/batch/viewcourse/${batchTitle}/${batchId}`)}>
                    {courseName}
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a href="#">Quizz</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="row">
          <div className="col-12">
            <div className="card-header">
              <div className="navigateheaders">
                <div onClick={() => navigate(-1)}>
                  <i className="fa-solid fa-arrow-left"></i>
                </div>
                <div></div>
                <div onClick={() => navigate("/dashboard/course")}>
                  <i className="fa-solid fa-xmark"></i>
                </div>
              </div>
              <div className="samerow">
                <h4>
                  Batch: <span className="text-primary">{batchTitle}</span>
                </h4>
                <h4>
                  Course Name: <span className="text-primary">{courseName}</span>
                </h4>
              </div>
            </div>

                <div className="card-body">
         <div className="table-container">
           <table className="table table-hover table-bordered table-sm">
             <thead className="thead-dark">
               <tr>
                 <th>Quizz</th>
                 <th>Lesson</th>
                 <th colSpan={3}>Schedule Duration</th>
               </tr>
             </thead>
             {submitting ? (
              <div className="outerspinner active"><div className="spinner"></div></div>
            ) : (
              <tbody>
                {schedule.map((item, index) => (
                  <tr key={index}>
                    <td>{item.quizzName}</td>
                    <td>{item.lessontitle}</td>
                    {/* <td>
                      <div className="row">
                      <span className="col-sm-2">From: </span>
                      <input
                        type="datetime-local"
                        className="form-control col-sm-9"
                        value={item.startDate || ""}
                        onChange={(e) => handleDateChange(index, "startDate", e.target.value)}
                      />
                      </div>
                    </td>
                    <td>
                      <div className="row">
                      <span  className="col-sm-2">To: </span>
                      <input
                        type="datetime-local"
                         className="form-control col-sm-9"
                        value={item.endDate || ""}
                        onChange={(e) => handleDateChange(index, "endDate", e.target.value)}
                      />
                      </div>
                    </td> */}
                  <td>
                            <div className="row">
                              <span className="col-sm-2">From: </span>
                              <input
                                type="datetime-local"
                                className="form-control col-sm-9"
                                value={item.startDate || ""}
                                min={new Date().toISOString().slice(0, 16)} // Disable past dates
                                onChange={(e) => handleDateChange(index, "startDate", e.target.value)}
                              />
                            </div>
                          </td>
                          <td>
                            <div className="row">
                              <span className="col-sm-2">To: </span>
                              <input
                                type="datetime-local"
                                className="form-control col-sm-9"
                                value={item.endDate || ""}
                                min={item.startDate } // Disable end date before start date
                                onChange={(e) => handleDateChange(index, "endDate", e.target.value)}
                              />
                            </div>
                          </td>

                      {item.enabled && (
                           <td>
                        <button className="btn btn-primary" onClick={() => saveOrUpdateSchedule(item, index)}>
                          Save
                        </button>
                        </td>
                      )}
                   
                  </tr>
                ))}
              </tbody>
            )}
          </table>
        </div>
      </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SheduleQuizz;