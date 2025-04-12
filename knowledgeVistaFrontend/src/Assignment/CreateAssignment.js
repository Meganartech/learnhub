import React, { useState } from "react";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useNavigate, useParams } from "react-router-dom";
import AddQuestionToAssignment from "./AddQuestionToAssignment";
import baseUrl from "../api/utils.js"
import axios from "axios";

const CreateAssignment = () => {
  const { courseName, courseId } = useParams();
  const token = sessionStorage.getItem("token");
  const MySwal = withReactContent(Swal);
  const navigate = useNavigate();
  const [showAddQuestion, setShowAddQuestion] = useState(false);
  const [AssignmentQuestion, setAssignmentQuestion] = useState(
    Array(5).fill({ questionText: "" })
  );
  const [Assignment, setAssignment] = useState({
    title: "",
    description: "",
    totalMarks: 10
  });

  const [errors, setErrors] = useState({
    title: "",
    description: "",
    totalMarks: ""
  });

  const validate = (name, value, formData = {}) => {
    let newErrors = { ...errors };
  
    const numericValue = Number(value);
  
    if (name === "title") {
      if (value.trim() === "") {
        newErrors.title = "Title is required";
      } else if (value.length > 255) {
        newErrors.title = "Title cannot exceed 255 characters";
      } else {
        newErrors.title = "";
      }
    }
  
    if (name === "description") {
      if (value.trim() === "") {
        newErrors.description = "Description is required";
      } else if (value.length > 1000) {
        newErrors.description = "Description cannot exceed 1000 characters";
      } else {
        newErrors.description = "";
      }
    }
  
    if (name === "totalMarks") {
      if (numericValue <= 0) {
        newErrors.totalMarks = "Total marks must be greater than 0";
      }  else {
        newErrors.totalMarks = "";
      }
    }
    setErrors(newErrors);
  };
  
  
  
    const handleAssignmentChange = (e) => {
      const { name, value } = e.target;
    
      setAssignment((prev) => {
        const updatedAssignment = {
          ...prev,
          [name]: value,
        };
    
        // Pass the full form data to validate (with the latest updated value)
        validate(name, value, updatedAssignment);
    
        return updatedAssignment;
      });
    };

  const isFormInvalid =
    errors.title ||
    errors.description ||
    errors.totalMarks ||
    Assignment.title.trim() === "" ||
    Assignment.description.trim() === "" ||
    Assignment.totalMarks <= 0 ||
    AssignmentQuestion.length<=0;
    const handleSubmit = async () => {
      if (isFormInvalid) {
          MySwal.fire({
              icon: "error",
              title: "Validation Error",
              text: "Please fill all required fields correctly.",
          });
          return;
      }
  
      const assignmenttosend = {
          ...Assignment,
          questions: AssignmentQuestion.filter(q => q.questionText.trim() !== ""),
      };
      if (assignmenttosend.questions.length === 0) {
        MySwal.fire({
          icon: "error",
          title: "Validation Error",
          text: "Please add at least one valid question before submitting.",
      });
        return;
      }
      try {
          const response = await axios.post(`${baseUrl}/Assignment/save?courseId=${courseId}`, assignmenttosend,{
              headers: {
                  Authorization: token,
              }
          });
  
          if (response.status===200) {
              MySwal.fire({
                  icon: "success",
                  title: "Assignment Created",
                  text: "The assignment has been successfully created!",
              }).then(() => {
                setAssignment({title: "",
                  description: "",
                  totalMarks: 10})
                  setAssignmentQuestion(Array(5).fill({ questionText: "" }))
                navigate(`/Assignment/getAll/${courseName}/${courseId}`)});
          }else if(response?.status===204){
            MySwal.fire({
              icon: "warning",
              title: " Not Found",
              text: "Course Not Found",
          }).then(() => navigate("/dashboard/course"));
          }
      } catch (error) {
        if(error?.response.status===401){
          navigate("/unauthorized")
        }else if(error?.response.status===403){
          MySwal.fire({
            icon: "error",
            title: "Forbidden",
            text: error?.response?.data || "You Can't Access This Course",
        }).then((result) => {
            if (result.isConfirmed) {
                navigate(-1);
            }
        });
        }else{
          MySwal.fire({
              icon: "error",
              title: "Error",
              text: error.message || "Failed to create assignment",
          });
        }
      }
  };
  

  return (
    <div>
      <div className="page-header">
        <div className="page-block">
          <div className="row align-items-center">
            <div className="col-md-12">
              <div className="page-header-title">
                <h5 className="m-b-10">Assignment</h5>
              </div>
              <ul className="breadcrumb">
                <li className="breadcrumb-item">
                  <a href="#" onClick={() => navigate("/dashboard/course")}>
                    <i className="feather icon-layout"></i>
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a href="#" onClick={() => navigate("/dashboard/course")}>
                    {courseName}
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a href="#">Create Assignment</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      {!showAddQuestion ? (
        <div className="row">
          <div className="col-sm-12">
            <div className="card">
              <div className="card-body">
                <div className="navigateheaders">
                  <div onClick={() => navigate(-1)}>
                    <i className="fa-solid fa-arrow-left"></i>
                  </div>
                  <div></div>
                  <div onClick={() => navigate(-1)}>
                    <i className="fa-solid fa-xmark"></i>
                  </div>
                </div>
                <h4>Create Assignment</h4>

                <div className="vh-50-overflow mt-4">
                  <div className="form-group row">
                    <label htmlFor="title" className="col-sm-3 col-form-label">
                      Assignment Title <span className="text-danger">*</span>
                    </label>
                    <div className="col-sm-6">
                      <input
                        type="text"
                        id="title"
                        name="title"
                        value={Assignment.title}
                        className={`form-control ${errors.title && "is-invalid"}`}
                        placeholder="Assignment Title"
                        onChange={handleAssignmentChange}
                        required
                      />
                      <div className="invalid-feedback">{errors.title}</div>
                    </div>
                  </div>

                  <div className="form-group row">
                    <label
                      htmlFor="description"
                      className="col-sm-3 col-form-label"
                    >
                      Assignment Description{" "}
                      <span className="text-danger">*</span>
                    </label>
                    <div className="col-sm-6">
                      <textarea
                        id="description"
                        rows={8}
                        name="description"
                        value={Assignment.description}
                        maxLength="1000"
                        className={`form-control ${
                          errors.description && "is-invalid"
                        }`}
                        placeholder="Assignment description"
                        onChange={handleAssignmentChange}
                      />
                      <div className="invalid-feedback">{errors.description}</div>
                    </div>
                  </div>

                  <div className="form-group row">
                    <label
                      htmlFor="totalMarks"
                      className="col-sm-3 col-form-label"
                    >
                      Total Marks <span className="text-danger">*</span>
                    </label>
                    <div className="col-sm-6">
                      <input
                      
                        type="number"
                        id="totalMarks"
                        name="totalMarks"
                        maxLength={100} 
                        value={Assignment.totalMarks}
                        className={`form-control ${
                          errors.totalMarks && "is-invalid"
                        }`}
                        placeholder="Total Marks"
                        onChange={handleAssignmentChange}
                      />
                      <div className="invalid-feedback">{errors.totalMarks}</div>
                    </div>
                  </div>

                </div>

                <div className="cornerbtn">
                  <button
                    className="btn btn-secondary"
                    type="button"
                    onClick={() => navigate(-1)}
                  >
                    Cancel
                  </button>
                  <button
                    className="btn btn-primary"
                    onClick={() => setShowAddQuestion(true)}
                    disabled={isFormInvalid}
                  >
                    Next
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <AddQuestionToAssignment
          AssignmentQuestion={AssignmentQuestion}
          setAssignmentQuestion={setAssignmentQuestion}
          setShowAddQuestion={setShowAddQuestion}
          handleSubmit={handleSubmit}
        />
      )}
    </div>
  );
};

export default CreateAssignment;
