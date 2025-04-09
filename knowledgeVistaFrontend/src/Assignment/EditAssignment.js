import React, { useEffect, useState } from "react";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../api/utils.js";
import axios from "axios";
import AddQuestionToEditAssignment from "./AddQuestionToEditAssignment";
import { useNavigate, useParams } from "react-router-dom";

const EditAssignment = () => {
  const { courseName, courseId, assignmentId } = useParams();
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
    totalMarks: 10,
    passingMarks: 10,
  });

  const [errors, setErrors] = useState({
    title: "",
    description: "",
    totalMarks: "",
    passingMarks: "",
  });
  const getAssignment = async () => {
    try {
      const response = await axios.get(
        `${baseUrl}/Assignment/get?assignmentId=${assignmentId}`,
        {
          headers: {
            Authorization: token,
          },
        }
      );
      if (response.status === 200) {
        setAssignment(response?.data);
        setAssignmentQuestion(response?.data?.questions);
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
  const validate = (name, value, formData = {}) => {
    let newErrors = { ...errors };

    // Convert to number only when validating numeric fields
    const totalMarks = Number(formData.totalMarks);
    const passingMarks = Number(formData.passingMarks);
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
      } else if (passingMarks > numericValue) {
        newErrors.totalMarks = "";
        newErrors.passingMarks =
          "Passing marks cannot be greater than total marks";
      } else {
        newErrors.totalMarks = "";
        newErrors.passingMarks = "";
      }
    }

    if (name === "passingMarks") {
      if (numericValue <= 0) {
        newErrors.passingMarks = "Passing marks must be greater than 0";
      } else if (numericValue > totalMarks) {
        newErrors.passingMarks =
          "Passing marks cannot be greater than total marks";
      } else {
        newErrors.passingMarks = "";
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
    errors.passingMarks ||
    Assignment.title.trim() === "" ||
    Assignment.description.trim() === "" ||
    Assignment.totalMarks <= 0 ||
    Assignment.passingMarks <= 0;
  const handleSubmit = async () => {
    if (isFormInvalid) {
      MySwal.fire({
        icon: "error",
        title: "Validation Error",
        text: "Please fill all required fields correctly.",
      });
      return;
    }


    try {
      const response = await axios.patch(
        `${baseUrl}/Assignment/Edit?AssignmentId=${assignmentId}`,
        Assignment,
        {
          headers: {
            Authorization: token,
          },
        }
      );

      if (response.status === 200) {
        MySwal.fire({
          icon: "success",
          title: "Assignment Updated",
          text: "The assignment has been successfully Updated!",
        }).then(() => {
          getAssignment();
        });
      } else if (response?.status === 204) {
        MySwal.fire({
          icon: "warning",
          title: " Not Found",
          text: "Course Not Found",
        }).then(() => getAssignment());
      }
    } catch (error) {
      if (error?.response.status === 401) {
        navigate("/unauthorized");
      } else if (error?.response.status === 403) {
        MySwal.fire({
          icon: "error",
          title: "Forbidden",
          text: error?.response?.data || "You Can't Access This Course",
        }).then((result) => {
          if (result.isConfirmed) {
            navigate(-1);
          }
        });
      } else {
        MySwal.fire({
          icon: "error",
          title: "Error",
          text: error.message || "Failed to create assignment",
        });
      }
    }
  };
  const handleSaveQuestions = async () => {
    try {
      const cleanedQuestions = AssignmentQuestion.filter(
        (q) => q.questionText.trim() !== ""
      );
      if (cleanedQuestions.length === 0) {
        MySwal.fire({
          icon: "error",
          title: "Validation Error",
          text: "Please add at least one valid question before submitting.",
        });
        return; // Stop further execution
      }
      const response = await axios.patch(
        `${baseUrl}/Assignment/EditQuestion?AssignmentId=${assignmentId}`,
        cleanedQuestions,
        {
          headers: {
            Authorization: token,
          },
        }
      );

      if (response.status === 200) {
        MySwal.fire({
          icon: "success",
          title: "Questions Updated",
          text: "The Questions has been successfully Updated!",
        }).then(() => {
          getAssignment();
        });
      } else if (response?.status === 204) {
        MySwal.fire({
          icon: "warning",
          title: " Not Found",
          text: "Course Not Found",
        }).then(() => getAssignment());
      }
    } catch (error) {
      if (error?.response.status === 401) {
        navigate("/unauthorized");
      } else if (error?.response.status === 403) {
        MySwal.fire({
          icon: "error",
          title: "Forbidden",
          text: error?.response?.data || "You Can't Access This Course",
        }).then((result) => {
          if (result.isConfirmed) {
            navigate(-1);
          }
        });
      } else {
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
                  <a href="#">Update Assignment</a>
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
                <div className="headingandbutton">
                  <h4>Update Assignment</h4>
                  <button
                    className="btn btn-primary"
                    style={{ width: "150px" }}
                    onClick={() => setShowAddQuestion(true)}
                  >
                    Edit Questions
                  </button>
                </div>

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
                        className={`form-control ${
                          errors.title && "is-invalid"
                        }`}
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
                        rows={3}
                        name="description"
                        value={Assignment.description}
                        maxLength="1000"
                        className={`form-control ${
                          errors.description && "is-invalid"
                        }`}
                        placeholder="Assignment description"
                        onChange={handleAssignmentChange}
                      />
                      <div className="invalid-feedback">
                        {errors.description}
                      </div>
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
                      <div className="invalid-feedback">
                        {errors.totalMarks}
                      </div>
                    </div>
                  </div>

                  <div className="form-group row">
                    <label
                      htmlFor="passingMarks"
                      className="col-sm-3 col-form-label"
                    >
                      Passing Marks <span className="text-danger">*</span>
                    </label>
                    <div className="col-sm-6">
                      <input
                        type="number"
                        id="passingMarks"
                        name="passingMarks"
                        value={Assignment.passingMarks}
                        className={`form-control ${
                          errors.passingMarks && "is-invalid"
                        }`}
                        placeholder="Passing Marks"
                        onChange={handleAssignmentChange}
                      />
                      <div className="invalid-feedback">
                        {errors.passingMarks}
                      </div>
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
                    onClick={handleSubmit}
                    disabled={isFormInvalid}
                  >
                    Update
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <AddQuestionToEditAssignment
          AssignmentQuestion={AssignmentQuestion}
          setAssignmentQuestion={setAssignmentQuestion}
          setShowAddQuestion={setShowAddQuestion}
          handleSaveQuestions={handleSaveQuestions}
          getAssignment={getAssignment}
        />
      )}
    </div>
  );
};

export default EditAssignment;
