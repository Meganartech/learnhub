import axios from "axios";
import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useNavigate, useParams } from "react-router-dom";
import baseUrl from "../api/utils";

const SubmitAssignment = () => {
  const navigate = useNavigate();

const{sheduleId,batchId,AssignmentId}=useParams();
 const MySwal = withReactContent(Swal);
  const [Assignment, setAssignment] = useState();
const token=sessionStorage.getItem('token')
  const [answers, setAnswers] = useState({});
  const[existingSubmission,setexistingSubmission]=useState({});
  const handleInputChange = (id, value) => {
    setAnswers((prev) => ({ ...prev, [id]: value }));
  };

  const handleSubmit = async () => {
    try {
      const response = await axios.post(
        `${baseUrl}/Assignment/Submit?assignmentId=${AssignmentId}&scheduleId=${sheduleId}&batchId=${batchId}`,
        answers,
        {
          headers: {
            Authorization: token,
          },
        }
      );
  
      if (response.status === 200) {
        MySwal.fire({
          title: "Success!",
          text: "Assignment submitted successfully.",
          icon: "success",
          confirmButtonText: "OK",
        }).then(() => {
          navigate("/user/assignments"); // or wherever you want to redirect
        });
      }
    } catch (err) {
      if (err?.response?.status === 403 || err?.response?.status === 400) {
        MySwal.fire({
          title: "Submission Failed",
          text: err?.response?.data,
          icon: "warning",
          confirmButtonText: "OK",
        });
      } else if (err?.response?.status === 401) {
        navigate("/unauthorized");
      } else {
        MySwal.fire({
          title: "Error",
          text: "Something went wrong while submitting the assignment.",
          icon: "error",
          confirmButtonText: "OK",
        });
        console.error(err);
      }
    }
  };
  

  const getAssignment = async () => {
    try {
      const response = await axios.get(
        `${baseUrl}/Assignment/getSubmission?assignmentId=${AssignmentId}&batchId=${batchId}`,
        {
          headers: {
            Authorization: token,
          },
        }
      );
      if (response.status === 200) {
       setAssignment(response?.data?.assignment);
       if(response?.data?.existingSubmission){
       setexistingSubmission(response?.data?.existingSubmission)
       }
       if(response?.data?.existingSubmission?.answers){
       setAnswers(response?.data?.existingSubmission?.answers);
       }
      }else if(response.status === 204){
        MySwal.fire({
          title: " Not Found!",
          text:"Assignment Not Found",
          icon: "warning",
          confirmButtonText: "OK",
        }).then((result) => {
            navigate(-1);
        });
      }
    } catch (err) {
      if(err?.response?.status===401){
        navigate("/unauthorized")
    }else if (err?.response?.status===403){
        MySwal.fire({
            title: " Forbitten!",
            text: err?.response?.data,
            icon: "warning",
            confirmButtonText: "OK",
          }).then((result) => {
              navigate(-1);
          });
    }else{
    console.log(err)
    throw err
    }
    }
  };
  useEffect(() => {
    getAssignment();
  }, []);

  return (
    <div>
      <div className="page-header"></div>
      <div className="row">
        <div className="col-sm-12">
          <div className="card">
          <div className="card-body">
            <div className="navigateheaders">
              <div
                onClick={() => {
                  navigate(-1);
                }}
              >
                <i className="fa-solid fa-arrow-left"></i>
              </div>
              <div></div>
              <div
                onClick={() => {
                  navigate(-1);
                }}
              >
                <i className="fa-solid fa-xmark"></i>
              </div>
            </div>
            
              <div className="mb-4">
                <h4 className="text-primary">{Assignment?.title}</h4>
                <p className="text-muted">{Assignment?.description}</p>
                <p>
                  <strong>Total Marks:</strong> {Assignment?.totalMarks} &nbsp;|&nbsp;
                  <strong>Passing Marks:</strong> {Assignment?.passingMarks}
                </p>
              </div>

              <div>
                {Assignment?.questions.map((q, idx) => (
                  <div className="mb-3" key={q.id}>
                    <label className="form-label">
                      {idx + 1}. {q?.questionText}
                    </label>
                    <textarea
                      rows={3}
                      className="form-control"
                      placeholder="Type your answer here..."
                      value={answers[q?.id] || ""}
                      maxLength={5000}
                      onChange={(e) => handleInputChange(q?.id, e.target.value)}
                      readOnly={Object.keys(existingSubmission).length !== 0}
                    />
                  <div className="text-end">
  <small className="text-muted">
    {answers[q?.id]?.length || 0}/5000 characters
  </small>
</div>
                  </div>
                ))}
              </div>

             
              {Object.keys(existingSubmission).length === 0 &&  <div className="cornerbtn">
                <button
                  className="btn btn-secondary"
                  type="button"
                  onClick={() => {
                    navigate(-1);
                  }}
                >
                  Cancel
                </button>
              <button className="btn btn-primary" onClick={handleSubmit}>
                  Submit
                </button>
              </div>
}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SubmitAssignment;


