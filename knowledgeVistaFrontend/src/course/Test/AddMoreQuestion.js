import React, { useEffect, useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import baseUrl from '../../api/utils';
import axios from 'axios';

const AddMoreQuestion = () => {
    const MySwal = withReactContent(Swal);
    const { courseName,testId } = useParams();
    const token = sessionStorage.getItem("token");
    const location = useLocation();
     const navigate=useNavigate();
    // State for question and options
    const [questionData, setQuestionData] = useState({
        questionText: '',
        options: {
            option1: '',
            option2: '',
            option3: '',
            option4: ''
        },
        selectedOption: ''
    });

    // State for errors
    const [errors, setErrors] = useState({
        questionText: '',
        options: {
            option1: '',
            option2: '',
            option3: '',
            option4: ''
        },
        selectedOption: ''
    });

    // Function to handle input change
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setQuestionData(prevData => ({
            ...prevData,
            options: {
                ...prevData.options,
                [name]: value
            }
        }));

        // Validate input fields
        if (value.trim() === '') {
            setErrors(prevErrors => ({
                ...prevErrors,
                options: {
                    ...prevErrors.options,
                    [name]: 'This field is required'
                }
            }));
        } else {
            setErrors(prevErrors => ({
                ...prevErrors,
                options: {
                    ...prevErrors.options,
                    [name]: ''
                }
            }));
        }
    };

    // Function to handle question text change
    const handleQuestionTextChange = (e) => {
        const { value } = e.target;
        setQuestionData(prevData => ({
            ...prevData,
            questionText: value
        }));

        // Validate question text
        if (value.trim() === '') {
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

    // Function to handle option selection
    const handleOptionChange = (option) => {
        setQuestionData(prevData => ({
            ...prevData,
            selectedOption: option
        }));

        // Validate option selection
        if (option === '') {
            setErrors(prevErrors => ({
                ...prevErrors,
                selectedOption: 'Please select an option'
            }));
        } else {
            setErrors(prevErrors => ({
                ...prevErrors,
                selectedOption: ''
            }));
        }
    };

    // Function to handle save
    const handleSave = async () => {
        // Check for errors
        if (questionData.questionText.trim() === '') {
            setErrors(prevErrors => ({
                ...prevErrors,
                questionText: 'This field is required'
            }));
        }
        if (questionData.selectedOption === '') {
            setErrors(prevErrors => ({
                ...prevErrors,
                selectedOption: 'Please select an option'
            }));
        }
        for (const option in questionData.options) {
            if (questionData.options[option].trim() === '') {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    options: {
                        ...prevErrors.options,
                        [option]: 'This field is required'
                    }
                }));
            }
        }

        // If there are no errors, proceed with saving
        if (
            questionData.questionText.trim() !== '' &&
            questionData.selectedOption !== '' &&
            Object.values(questionData.options).every(option => option.trim() !== '')
        ) {
            try {
                const formData = new FormData();
                formData.append("questionText", questionData.questionText);
                formData.append("answer", questionData.options[questionData.selectedOption]);

                // Append all options to FormData for additional context if needed
                Object.keys(questionData.options).forEach((optionKey, index) => {
                    formData.append(`option${index + 1}`, questionData.options[optionKey]);
                });
                const response = await axios.post(`${baseUrl}/test/add/${testId}`,formData, {
                    headers: {
                        "Authorization": token
                    }
                });
                if (response.status===200) {
                    const data =  response.data;
                    MySwal.fire({
                        title: "Success",
                        text: "Question updated successfully",
                        icon: "success",
                        confirmButtonText: "OK"
                    });
                    window.history.back();

                } 
            } catch (error) {
                if(error.response && error.response.status===401){
                    MySwal.fire({
                        title: "Error",
                        text: "you are unauthorized to access this page",
                        icon: "error",
                        confirmButtonText: "OK"
                    });
                    window.location.href = "/unauthorized"; // Redirect to previous page
               
                }else{
                MySwal.fire({
                    title: "Error",
                    text: error,
                    icon: "error",
                    confirmButtonText: "OK"
                });
            }
        }
        }
    };

    return (
        <div className='contentbackground'>
            <div className='contentinner'>
            <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
                <div className='atdiv'>
                    <div className='atgrid' >
                        <div>
                            <h6>Add Question to {courseName}</h6>
                            <input
                            className={`form-control .form-control-sm  ${errors.questionText && 'is-invalid'}`}
                            autoFocus
                            placeholder='Enter Question'
                            value={questionData.questionText}
                            onChange={handleQuestionTextChange}
                        />
                        {errors.questionText && <div className="invalid-feedback">{errors.questionText}</div>}
                        </div>
                        <ul className='listgroup' >
                            {Object.keys(questionData.options).map((option, index) => (
                    
                    <li className='choice' key={index}>
                                    <input
                                        className='mt-2'
                                        type="radio"
                                        value={questionData.options[option]}
                                        checked={questionData.selectedOption === option}
                                        onChange={() => handleOptionChange(option)}
                                    />
                                    <div>
                                    <input
                                        className={`form-control .form-control-sm  ${errors.options[option] && 'is-invalid'}`}
                                        type='text'
                                        placeholder={`Option ${index + 1}`}
                                        value={questionData.options[option]}
                                        name={option}
                                        onChange={handleInputChange}
                                    />
                                    {errors.options[option] && <div className="invalid-feedback">{errors.options[option]}</div>}
                                    </div>
                                </li>
                            ))}
                        </ul>
                        {errors.selectedOption && <div className="invalid-feedback">{errors.selectedOption}</div>}
                    </div>
                    <div className='atbtndiv'>
                        <button className='btn btn-primary' onClick={() => window.history.back()}>Cancel</button>
                        <div></div>
                        <button className='btn btn-primary' onClick={handleSave} disabled={
                            questionData.questionText.trim() === '' ||
                            questionData.selectedOption === '' ||
                            Object.values(questionData.options).some(option => option.trim() === '')
                        }>Save</button>
                    </div>

                </div>
            </div>
        </div>
    );
}

export default AddMoreQuestion;
