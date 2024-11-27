import React, { useEffect, useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import baseUrl from '../../api/utils';
import axios from 'axios';

const EditQuestion = () => {
  const MySwal = withReactContent(Swal);
  const { questionId } = useParams();
  const token = sessionStorage.getItem("token");
 
  const navigate =useNavigate();
  const [questionText, setQuestionText] = useState('');
  const [options, setOptions] = useState({
    option1: '',
    option2: '',
    option3: '',
    option4: ''
  });
  const [selectedOption, setSelectedOption] = useState('');
  const [saveEnabled, setSaveEnabled] = useState(false); // Initially disable save button
  const [errors, setErrors] = useState({
    option1: '',
    option2: '',
    option3: '',
    option4: '',
    questionText: '',
    selectedOption: ''
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(`${baseUrl}/test/getQuestion/${questionId}`, {
       
          headers: {
            "Authorization": token
          }
        });
       
          const data =  response.data;
          setQuestionText(data.questionText);
          setOptions({
            option1: data.option1,
            option2: data.option2,
            option3: data.option3,
            option4: data.option4
          });
          const selectedOption = Object.keys(data).find(optionKey => data[optionKey] === data.answer);
          setSelectedOption(selectedOption); 
        
      } catch (error) {
        MySwal.fire({
          title: "Error",
          text: "Some error occurred. Please try again later.",
          icon: "error",
          confirmButtonText: "OK"
        }).then((result) => {
          if (result.isConfirmed) {
            navigate(-1);
          }
      })
      }
    };
  
    fetchData();
  }, [questionId, token]);
  

  useEffect(() => {
    validateForm(); // Validate the form whenever any state changes
  }, [questionText, options, selectedOption]);

  const handleOptionChange = (option) => {
    setSelectedOption(option);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setOptions(prevOptions => ({
      ...prevOptions,
      [name]: value
    }));
    validateInput(name, value);
  };

  const handleQuestionTextChange = (e) => {
    const { value } = e.target;
    setQuestionText(value);
    validateInput('questionText', value);
  };

  const validateInput = (name, value) => {
    if (value.trim() === '') {
      setErrors(prevErrors => ({
        ...prevErrors,
        [name]: 'This field is required.'
      }));
    } else {
      setErrors(prevErrors => ({
        ...prevErrors,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    let isValid = true;
    const newErrors = { ...errors };

    // Check if question text is empty
    if (questionText.trim() === '') {
      newErrors.questionText = 'This field is required.';
      isValid = false;
    } else {
      newErrors.questionText = '';
    }

    // Check if any option is empty
    Object.keys(options).forEach(option => {
      if (options[option].trim() === '') {
        newErrors[option] = 'This field is required.';
        isValid = false;
      } else {
        newErrors[option] = '';
      }
    });

    // Check if an answer is selected
    if (selectedOption === '') {
      newErrors.selectedOption = 'Please select an answer.';
      isValid = false;
    } else {
      newErrors.selectedOption = '';
    }

    setErrors(newErrors);
    setSaveEnabled(isValid);
  };

  const handleSave = async () => {
    try {
      const formData = new FormData();
      formData.append("questionText", questionText);
      formData.append("option1", options.option1);
      formData.append("option2", options.option2);
      formData.append("option3", options.option3);
      formData.append("option4", options.option4);
      formData.append("answer", options[selectedOption]);

      const response = await axios.patch(`${baseUrl}/test/edit/${questionId}`, formData,{
        headers: {
          "Authorization": token
        }
      });
      if (response.status===200) {
     
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
          text: "You are unauthorized to access this page",
          icon: "error",
          confirmButtonText: "OK"
        });
        window.location.href="/unauthorized";
      } else {
        MySwal.fire({
          title: "Error",
          text: error,
          icon: "error",
          confirmButtonText: "OK"
        });
      }}
  };
  
  return (
    <div>
    <div className="page-header"></div>
    <div className="card">
    <div className="card-body">
        <div className="row">
        <div className="col-12">
      <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
     
        <div className='atdiv'  >
          <div className='atgrid' >
            <div>            
              <input 
              className={`form-control   ${errors.questionText && 'is-invalid'}`}
              autoFocus
              value={questionText}
              onChange={handleQuestionTextChange}
            />
            {errors.questionText && <div className="invalid-feedback">{errors.questionText}</div>}
            </div>

            <ul className='listgroup'>
              {Object.keys(options).map((optionKey, index) => (
                <li className='choice' key={index}>
                  <input
                    className={`mt-2 ${errors[optionKey] && 'is-invalid'}`}
                    type="radio"
                    value={optionKey} 
                    checked={selectedOption === optionKey} 
                    onChange={() => handleOptionChange(optionKey)} 
                  />
                  <div>
                  <input
                    type='text'
                    value={options[optionKey]}
                    name={optionKey}
                    className={`form-control   ${errors[optionKey] && 'is-invalid'}`}
                    onChange={handleInputChange}
                  />
                  {errors[optionKey] && <div className="invalid-feedback">{errors[optionKey]}</div>}
                  </div>
                </li>
              ))}
            </ul>
          </div>
          <div className='atbtndiv'>
            <div>
            <button className='btn btn-secondary' onClick={() => window.history.back()}>Cancel</button>
            </div>
            <div></div>
            <div>
            <button
              className='btn btn-primary'
              onClick={handleSave}
              disabled={!saveEnabled}
            >
              Save
            </button>
            </div>
          </div>
        </div>
        </div>
        </div>
        </div>
      </div>
    </div>
  );
}

export default EditQuestion;
