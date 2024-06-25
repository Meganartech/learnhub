import { useEffect, useState } from "react";
import React from "react";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useParams } from "react-router-dom";
import baseUrl from "../../api/utils";
import axios from "axios";

const CreateTest = () => {
  const { courseName,courseId} = useParams();
  const MySwal = withReactContent(Swal);
  const [testName, setTestName] = useState("");
  const [questionText, setQuestionText] = useState("");
  const [options, setOptions] = useState(["", "", "", ""]);
  const [answer, setAnswer] = useState('');
  const [savedQuestions, setSavedQuestions] = useState([]);
  const [selectedQuestionIndex, setSelectedQuestionIndex] = useState(0);
  const [noofattempt, setNoOfAttempt] = useState(1);
  const [passPercentage, setPassPercentage] = useState(0);
  const [showCriteria, setShowCriteria] = useState(false);
  const token=sessionStorage.getItem("token")
  const [errors, setErrors] = useState({
    noofattempt: "",
    passPercentage: "",
    testName:'',
    questionText: '',
    options: {
        option1: '',
        option2: '',
        option3: '',
        option4: ''
    },
    selectedOption: ''
  });



 const showprevious=(e)=>{
    const index=selectedQuestionIndex-1;
    console.log(index)
    if(index >=0){
    const { questionText, option1, option2, option3, option4, answer } = savedQuestions[index];
    setQuestionText(questionText);
    setOptions([option1, option2, option3, option4]);
    setAnswer(answer);
    setSelectedQuestionIndex(index)
    }
 }
 const shownext=(e)=>{
    const nextindex=selectedQuestionIndex+1;
    
    if(nextindex<savedQuestions.length){
        const { questionText, option1, option2, option3, option4, answer } = savedQuestions[nextindex];
    setQuestionText(questionText);
    setOptions([option1, option2, option3, option4]);
    setAnswer(answer);
    setSelectedQuestionIndex(nextindex)

    }
 }
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
    if(index!==0){
    setSelectedQuestionIndex(index-1)
    const { questionText, option1, option2, option3, option4, answer } = savedQuestions[index-1];
    setQuestionText(questionText);
    setOptions([option1, option2, option3, option4]);
    setAnswer(answer);
    }else{
        setSelectedQuestionIndex(0)
        setQuestionText("");
    setOptions(["", "", "", ""]);
    setAnswer("");
    }
  };



  const addQuestion = () => {
    
    if(!testName){
      setErrors((prevErrors) => ({
        ...prevErrors,
        testName: 'This field is required ',
      }));
      return;
    }
    if (!questionText) {
      setErrors((prevErrors) => ({
        ...prevErrors,
        questionText: 'This field is required ',
      }));
      return;
    }  if (!answer) {
      setErrors((prevErrors) => ({
        ...prevErrors,
        selectedOption: 'Please select the correct answer',
      }));
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
     
      const updatedQuestions = [...savedQuestions];
      updatedQuestions[selectedQuestionIndex] = newQuestion;
      setSavedQuestions(updatedQuestions)
      
    setSelectedQuestionIndex(prevIndex => prevIndex + 1);
    
    }

    // Clear input fields after adding or updating a question
    setQuestionText("");
    setOptions(["", "", "", ""]);
    setAnswer("");
  };
 
  const submitTest = async (e) => {
    e.preventDefault(); // Prevent default form submission behavior

   console.log("questions",savedQuestions)
    try {
      const noOfQuestions = savedQuestions.length; // Count the number of questions
      const requestBody = {
        testName,
        questions: savedQuestions,
        noOfQuestions,
        noofattempt,
        passPercentage
      };
  const res=JSON.stringify(requestBody)
      const response = await axios.post(`${baseUrl}/test/create/${courseId}`,res, {
        headers: {
          "Content-Type": "application/json",
          Authorization: token,
        }
      });

      if (!response.status===200) {
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
       if(error.response && error.response.status===401)
          {
            window.location.href="/unauthorized";
          }else{
            MySwal.fire({
              title: "Error!",
              text: error.response,
              icon: "error",
              confirmButtonText: "OK",
            });
          }
    }
  };
  
const handleTestNameChange =(e)=>{
    const { value } = e.target;
    setTestName(value)
    if (value.trim() === '') {
      setErrors(prevErrors => ({
          ...prevErrors,
          testName  : 'This field is required'
      }));
  } else {
      setErrors(prevErrors => ({
          ...prevErrors,
          testName: ''
      }));
  }
  }
  const handleQuestionTextChange = (e) => {

    setQuestionText(e.target.value)

    // Validate question text
    if (e.target.value.trim() === '') {
        setErrors(prevErrors => ({
            ...prevErrors,
            questionText: 'This field is required'
        }));
    } else {
        setErrors(prevErrors => ({
            ...prevErrors,
            questionText: ''
        }));
    }
};
const handleselectanswer=(e)=>{
  const { value } = e.target;
  setAnswer(value)
  setErrors((prevErrors) => ({
    ...prevErrors,
    selectedOption: '',
  }));
}
const handleOptionChange = (e, index) => {
    const { value } = e.target;

    // Update the option value
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);

    // Clear any previous error message for the current option
    const newErrors = { ...errors };
    newErrors.options[index] = '';

    // Perform validation for the current option
    if (!value.trim()) {
        newErrors.options[index] = 'Option cannot be empty';
    }

    // Update the state with new errors
    setErrors(newErrors);
};


  return (
    <div className="contentbackground">
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

          <div className="atbtndiv mt-5">
            <button  className="btn btn-primary" onClick={()=>{setShowCriteria(false)}}>Back</button>
            <div></div>
            <button
              className="btn btn-primary"
              onClick={submitTest}
              disabled={
                !!errors.noofattempt ||
                !!errors.passPercentage ||
                !noofattempt ||
                !passPercentage
              }
            >
              Save
            </button>
          </div>
        </div>
      ) : (
        <div className="contentinner">
         <div className="atdiv" style={{ padding: "30px" }}>
              
              <div className='atgrid ' style={{height:"400px"}}>
             <div>
             <h4 style={{textDecoration:"underline"}}> Create Test For {courseName}</h4>   {errors.selectedOption && (
              <div className="text-danger">{errors.selectedOption}</div>
            )}
              <div className="mb-3" > 
              <input
                className={`form-control form-control-lg  ${errors.testName && 'is-invalid'}`}
                value={testName}
                placeholder="Test Name"
                onChange={handleTestNameChange}
              />
              {errors.testName && <div className="invalid-feedback">{errors.testName}</div>}
              
  </div>
  {selectedQuestionIndex < savedQuestions.length &&(
              <i className="fas fa-trash text-danger  "
                style={{ float: "right" ,fontSize:"20px",padding:"10px"}}
                        onClick={() => handleDelete(selectedQuestionIndex)}
                      ></i>)}
        <div>
              <input 
              className={`form-control form-control-lg ${errors.questionText && 'is-invalid'}`}       
              type="text"  
              value={questionText}
                placeholder="Add Question here"
                onChange={handleQuestionTextChange}
                rows={2}
                required
              />
               {errors.questionText && <div className="invalid-feedback">{errors.questionText}</div>}
              
<ul className='listgroup' >                

{options.map((option, index) => (
  <li className='choice' key={index} >
    <input
   className='mt-2'
      type="radio"
      name="answer"
      value={option}
      checked={option !== "" && answer === option}
      onChange={handleselectanswer}
      required
    /> 
    <div>
    <input
    className={`form-control form-control-lg ${errors.options[index] && 'is-invalid'}`}
    type="text"
    value={option}
    placeholder={`Option ${index + 1}`}
    onChange={(e) => handleOptionChange(e, index)}
    required
/>
{errors.options[index] && (
    <div className="invalid-feedback">{errors.options[index]}</div>
)}

    </div>
  </li>
))}
</ul>

                    </div>


<div className="atbtndiv">
              <button
                onClick={addQuestion}
                className="btn btn-primary mt-4"
              >
                {selectedQuestionIndex === savedQuestions.length ? "Add " : "update "}
              </button>
           

            <div className="atbtndiv">
            <div>
            {selectedQuestionIndex !==0 &&(    <button 
                className="btn btn-primary mt-4" 
                onClick={showprevious}
                disabled={selectedQuestionIndex===0}
                >
                &lt;
                </button>)}
            </div>
                       <div></div>
            {selectedQuestionIndex < savedQuestions.length-1 &&(
            <button 
                className="btn btn-primary mt-4" 
                onClick={shownext}
                disabled={selectedQuestionIndex > savedQuestions.length}
                >
                &gt;
                </button>)}
            
            </div>
          
            <div>
            {savedQuestions.length > 0 && (
                            <button
                            className="btn btn-primary mt-4"
                            onClick={() => setShowCriteria(true)}
                            >
                            Save
                            </button>
                        )} 
                        </div>
                      </div>   
                   </div>     
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
export default CreateTest;
