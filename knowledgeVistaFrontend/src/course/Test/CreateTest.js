import { useState } from "react";
import React from "react";
import "../../css/Style.css";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useParams } from "react-router-dom";

const CreateTest = () => {
  const { courseId } = useParams();
  const MySwal = withReactContent(Swal);
  const [testName, setTestName] = useState("");
  const [questionText, setQuestionText] = useState("");
  const [options, setOptions] = useState(["", "", "", ""]);
  const [answer, setAnswer] = useState("");
  const [savedQuestions, setSavedQuestions] = useState([]);
  const [selectedQuestionIndex, setSelectedQuestionIndex] = useState(null);
  const [noofattempt, setNoOfAttempt] = useState(1);
  const [passPercentage, setPassPercentage] = useState(0);
  const [showCriteria, setShowCriteria] = useState(false);

  const [errors, setErrors] = useState({
    noofattempt: "",
    passPercentage: ""
  });

  // Function to handle changes in noofattempt and passPercentage fields
  const handleCriteriaChange = (e) => {
    const { name, value } = e.target;
    let error = "";

    // Convert value to a number if it is an attempt count or percentage
    const numericValue = name === "noofattempt" || name === "passPercentage" ? parseFloat(value) : value;

    switch (name) {
      case "noofattempt":
        error = numericValue < 1 ? "Number of attempt must be at least 1." : "";
        setNoOfAttempt(numericValue);
        break;
      case "passPercentage":
        error = numericValue < 1 || numericValue > 100 ? "Pass percentage must be between 1 and 100." : "";
        setPassPercentage(numericValue);
        break;
      default:
        break;
    }

    // Update error state
    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: error
    }));
  };

  // Other functions such as handleDelete, handleEditQuestion, addQuestion, and submitTest remain unchanged

  const handleDelete = (index) => {
    const newSavedQuestions = savedQuestions.filter((ques, i) => i !== index);
    setSavedQuestions(newSavedQuestions);
  };

  const handleEditQuestion = (index) => {
    setSelectedQuestionIndex(index);
    const { questionText, option1, option2, option3, option4, answer } = savedQuestions[index];
    setQuestionText(questionText);
    setOptions([option1, option2, option3, option4]);
    setAnswer(answer);
  };

  const addQuestion = () => {
    if (!questionText || options.some((option) => !option) || !answer) {
      // If any field is empty, do not add the question
      return;
    }

    const newQuestion = {
      questionText: questionText,
      option1: options[0],
      option2: options[1],
      option3: options[2],
      option4: options[3],
      answer: answer
    };

    if (selectedQuestionIndex !== null) {
      // Update the existing question
      const updatedQuestions = [...savedQuestions];
      updatedQuestions[selectedQuestionIndex] = newQuestion;
      setSavedQuestions(updatedQuestions);
      setSelectedQuestionIndex(null); // Reset selected question index
    } else {
      // Add a new question
      setSavedQuestions([...savedQuestions, newQuestion]);
    }

    // Clear input fields after adding or updating a question
    setQuestionText("");
    setOptions(["", "", "", ""]);
    setAnswer("");
  };

  const submitTest = async (e) => {
    e.preventDefault(); // Prevent default form submission behavior


    try {
      const noOfQuestions = savedQuestions.length; // Count the number of questions
      const requestBody = {
        testName,
        questions: savedQuestions,
        noOfQuestions,
        noofattempt,
        passPercentage
      };

      const response = await fetch(`http://localhost:8080/test/create/${courseId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody)
      });

      if (!response.ok) {
        throw new Error("Failed to submit test");
      }

      // Reset state after successful submission
      setSavedQuestions([]);
      setTestName("");

      Swal.fire({
        title: "Test Submitted Successfully",
        text: "Thank you for submitting the test",
        icon: "success",
        confirmButtonText: "OK"
      }).then((result) => {
        if (result.isConfirmed) {
          window.location.href = `/course/testlist/${courseId}`;
        }
      });

    } catch (error) {
      // Handle error
      Swal.fire({
        title: "Error",
        text: error.message,
        icon: "error",
        confirmButtonText: "OK"
      });
    }
  };

  return (
    <div className="contentbackground">
      {/* Show criteria div based on showCriteria state */}
      {showCriteria ? (
        <div className="contentinner p-5">
          <h1>Test Criteria</h1>
          <p className="text-danger"><span>*</span> By default, each question carries one mark</p>
          
          <div className="inputgrp mt-3">
            <label>Number of Questions</label>
            <span>:</span>
            <input value={savedQuestions.length} readOnly />
          </div>
          
          <div className="inputgrp mt-5">
            <label>Number of Attempt</label>
            <span>:</span>
            <div>
            <input
              type="number"
              value={noofattempt}
              name="noofattempt"
              className={errors.noofattempt && "is-invalid"}
              onChange={handleCriteriaChange}
            />
            {errors.noofattempt && (
              <div className="invalid-feedback">{errors.noofattempt}</div>
            )}</div>
          </div>
          
          <div className="inputgrp mt-5">
            <label>Pass Percentage</label>
            <span>:</span>
            <div>
            <input
              type="number"
              value={passPercentage}
              name="passPercentage"
              className={errors.passPercentage && "is-invalid"}
              onChange={handleCriteriaChange}
            />
            {errors.passPercentage && (
              <div className="invalid-feedback">{errors.passPercentage}</div>
            )}
            </div>
          </div>

          <div className="btngrp mt-5">
            <button
              className="btn btn-primary"
              onClick={submitTest}
              disabled={!!errors.noofattempt || !!errors.passPercentage || !noofattempt || !passPercentage}
            >
              Save
            </button>
          </div>
        </div>
      ) : (
        <div className="contentinner">
          <div className="outer mt-3">
            <div className="first">
              <h3>Create Test</h3>
              <input
                className="form-control"
                value={testName}
                placeholder="Test Name"
                onChange={(e) => setTestName(e.target.value)}
              />
              <textarea
                className="form-control w-100 mt-4"
                value={questionText}
                placeholder="Add Question here"
                onChange={(e) => setQuestionText(e.target.value)}
                rows={3}
                required
              />
              {options.map((option, index) => (
                <div key={index} className="option">
                  <input
                    className="form-control w-100"
                    type="text"
                    value={option}
                    placeholder={`Option ${index + 1}`}
                    onChange={(e) => {
                      const newOptions = [...options];
                      newOptions[index] = e.target.value;
                      setOptions(newOptions);
                    }}
                    required
                  />
                </div>
              ))}
              <div>
                <select
                  value={answer}
                  className="form-control w-100 mt-4"
                  onChange={(e) => setAnswer(e.target.value)}
                  required
                >
                  <option value="">Select answer</option>
                  {options.map((option, index) => (
                    <option key={index} value={option}>{option}</option>
                  ))}
                </select>
              </div>
              <button
                onClick={addQuestion}
                className="btn btn-primary mt-4"
              >
                {selectedQuestionIndex !== null ? "Update Question" : "Add Question"}
              </button>
            </div>

            <div></div>
            <div className="second">
              <h3>Saved Questions:</h3>
              <div className="questions">
                {savedQuestions.length > 0 &&
                  savedQuestions.map((savedQuestion, index) => (
                    <div key={index} className="question form-control">
                      {savedQuestion.questionText.length > 20
                        ? savedQuestion.questionText.substring(0, 20) + "..."
                        : savedQuestion.questionText}
                      <i
                        className="fa-solid fa-edit iconedit"
                        onClick={() => handleEditQuestion(index)}
                      ></i>
                      <i
                        className="fas fa-trash icontrash"
                        onClick={() => handleDelete(index)}
                      ></i>
                    </div>
                  ))}
              </div>

              {savedQuestions.length > 0 && (
                <div className="buttondiv">
                  {/* Show criteria button */}
                  <button
                    className="btn btn-primary"
                    onClick={() => setShowCriteria(true)}
                  >
                    Submit Test
                  </button>
                  <button className="btn btn-warning">Cancel</button>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CreateTest;
