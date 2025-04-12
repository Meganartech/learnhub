import axios from 'axios'
import React, { useEffect, useState } from 'react'
import baseUrl from '../api/utils'
import { useNavigate, useParams } from 'react-router-dom'
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const ValidateAssignment = () => {
     const navigate = useNavigate();
    const{batchId,userId,assignmentId}=useParams();
    const [Assignment, setAssignment] = useState();
     const MySwal = withReactContent(Swal);
   const token=sessionStorage.getItem('token')
   const[errors,seterrors]=useState({
    totalMarksObtained:"",
    feedback:""
   })
     const [answers, setAnswers] = useState({});
     const[existingSubmission,setexistingSubmission]=useState({});
    const getSubmitteAssignmentforUser=async()=>{
        try{
        const response= await axios.get(`${baseUrl}/Assignments/getAssignment`,{
            headers:{
                Authorization:token
            },params:{
                batchId,
                userId,
                assignmentId

            }
        })
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
             }).then((Assignment) => {
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
                }).then((Assignment) => {
                    navigate(-1);
                });
          }else{
          console.log(err)
          throw err
          }
          }
    }
    useEffect(()=>{
        getSubmitteAssignmentforUser()
    },[])

    const handleResultChange = (e) => {
      const { name, value } = e.target;
      let error = "";
    
      if (name === "totalMarksObtained") {
        const marks = parseInt(value);
        const maxMarks = Assignment?.totalMarks || 0;
    
        if (isNaN(marks) || marks < 0) {
          error = "Marks must be a valid number greater than or equal to 0.";
        } else if (marks > maxMarks) {
          error = `Marks cannot exceed the maximum of ${maxMarks}.`;
        }else{
          error=""
          seterrors((prev) => ({ ...prev, [name]: error }));
        }
      }
    
      // Optionally store error if you're handling it in state
       seterrors((prev) => ({ ...prev, [name]: error }));
    
      setexistingSubmission((prev) => ({
        ...prev,
        [name]: value,
      }));
    };
    
     const handleSubmit = async () => {
        try {
          const hasError = Object.values(errors).some((err) => err);
  if (hasError) return;
          const response = await axios.post(
            `${baseUrl}/Assignments/Validate`,
            null,
            {
              headers: {
                Authorization: token,
              },
              params:{
                batchId,
                userId,
                assignmentId,
                feedback:existingSubmission?.feedback,
              marks:existingSubmission?.totalMarksObtained,              }
            }
          );
      
          if (response.status === 200) {
            MySwal.fire({
              title: "Success!",
              text: "Assignment submitted successfully.",
              icon: "success",
              confirmButtonText: "OK",
            });
          }else if(response?.status===204){
            MySwal.fire({
              title: "Not Found!",
              text: "Batch Not Found",
              icon: "warning",
              confirmButtonText: "OK",
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
           throw err;
          }
        }
      };
  return (
    <div>
    <div className="page-header"></div>
    <div className="row">
      <div className="col-sm-12">
        <div className="card min-vh-80">
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
          <div>          
             <div className="mb-4 ">
              <h4 className="text-primary">{Assignment?.title}</h4>
              <p className="text-muted text-indend-2em">{Assignment?.description}</p>
              <p>
                <strong>Total Marks:</strong> {Assignment?.totalMarks}
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
                    value={answers[q?.id] || ""}
                    maxLength={5000}
                    readOnly
                  />
                <div className="text-end">
<small className="text-muted">
  {answers[q?.id]?.length || 0}/5000 characters
</small>
</div>
                </div>
              ))}
            </div>

           <div>
           <div className="form-group row">
                    <label htmlFor="totalMarksObtained" className="col-sm-3 col-form-label">
                      Score <span className="text-danger">*</span>
                    </label>
                    <div className="col-sm-6">
                      <input
                        type="number"
                        id="totalMarksObtained"
                        name="totalMarksObtained"
                        max={Assignment?.totalMarks ?? ''}
                        min={0}
                        value={existingSubmission?.totalMarksObtained}
                        className={`form-control ${errors?.totalMarksObtained && "is-invalid"}`}
                        placeholder="totalMarksObtained"
                        onChange={handleResultChange}
                        required
                      />
                      <div className="invalid-feedback">{errors?.totalMarksObtained}</div>
                    </div>
                  </div>
                  <div className="form-group row">
                    <label htmlFor="feedback" className="col-sm-3 col-form-label">
                    Feedback <span className="text-danger">*</span>
                    </label>
                    <div className="col-sm-6">
                      <input
                        type="text"
                        id="feedback"
                        name="feedback"
                        value={existingSubmission?.feedback}
                        className={`form-control ${errors?.feedback && "is-invalid"}`}
                        placeholder="Feedback"
                        onChange={handleResultChange}
                        required
                      />
                      <div className="invalid-feedback">{errors?.feedback}</div>
                    </div>
                  </div>
           </div>
           <div className="cornerbtn">
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

          </div>
          </div>
          </div>
          </div>
          </div>
          </div>
  )
}

export default ValidateAssignment