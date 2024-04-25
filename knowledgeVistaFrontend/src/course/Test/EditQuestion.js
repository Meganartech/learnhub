import React, { useEffect, useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useParams, useLocation } from 'react-router-dom';

const EditQuestion = () => {
  const MySwal = withReactContent(Swal);
  const { questionId } = useParams();
  const token = sessionStorage.getItem("token");
  const location = useLocation();
  
  // State variables to hold question data
  const [questionText, setQuestionText] = useState('');
  const [options, setOptions] = useState({
    option1: '',
    option2: '',
    option3: '',
    option4: ''
  });
  const [selectedOption, setSelectedOption] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`http://localhost:8080/test/getQuestion/${questionId}`, {
          method: "GET",
          headers: {
            "Authorization": token
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch test');
        } else {
          const data = await response.json();
          setQuestionText(data.questionText);
          setOptions({
            option1: data.option1,
            option2: data.option2,
            option3: data.option3,
            option4: data.option4
          });
          setSelectedOption(data.answer);
        }
      } catch (error) {
        MySwal.fire({
          title: "Error",
          text: error.message,
          icon: "error",
          confirmButtonText: "OK"
        });
      }
    };

    fetchData();
  }, [questionId, token]);

  const handleOptionChange = (option) => {
    setSelectedOption(option);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setOptions(prevOptions => ({
      ...prevOptions,
      [name]: value
    }));
  };

  const handleQuestionTextChange = (e) => {
    setQuestionText(e.target.value);
  };

  const handleSave = async () => {
    try {
      const formData = new FormData();
      formData.append("questionText", questionText);
      formData.append("option1", options.option1);
      formData.append("option2", options.option2);
      formData.append("option3", options.option3);
      formData.append("option4", options.option4);
      formData.append("answer", selectedOption);
  
      const response = await fetch(`http://localhost:8080/test/edit/${questionId}`, {
        method: "PATCH",
        headers: {
          "Authorization": token
        },
        body: formData
      });
      if (response.ok) {
        const data=await response.json();
        MySwal.fire({
          title: "Success",
          text: "Question updated successfully",
          icon: "success",
          confirmButtonText: "OK"
        });
        window.history.back();
        
      } else if(response.status===401){
        
        MySwal.fire({
          title: "Error",
          text: "you are unauthorized to access this page",
          icon: "error",
          confirmButtonText: "OK"
        });
        window.location.href="/unauthorized" // Redirect to previous page
      }else{
        
        const data=await response.json();
        MySwal.fire({
          title: "Error",
          text: data.message,
          icon: "error",
          confirmButtonText: "OK"
        });
      }
    } catch (error) {
      MySwal.fire({
        title: "Error",
        text: error.message,
        icon: "error",
        confirmButtonText: "OK"
      });
    }
  };
  

  return (
    <div className='contentbackground'>
      <div className='contentinner'>
        <div className='atdiv'>
          <div className='atgrid'>
            <input 
              className='form-control form-control-lg'
              autoFocus
              value={questionText}
              onChange={handleQuestionTextChange}
            />
            <ul className='listgroup'>
              {Object.keys(options).map((optionKey, index) => (
                <li className='choice' key={index}>
                  <input
                    className='mt-2'
                    type="radio"
                    value={options[optionKey]}
                    checked={selectedOption === options[optionKey]}
                    onChange={() => handleOptionChange(options[optionKey])}
                  />
                  <input
                    type='text'
                    value={options[optionKey]}
                    name={optionKey}
                    className="form-control form-control-lg"
                    onChange={handleInputChange}
                  />
                </li>
              ))}
            </ul>
          </div>
          <div className='atbtndiv'>
            <button className='btn btn-primary' onClick={() => window.history.back()}>Cancel</button>
            <div></div>
            <button className='btn btn-primary' onClick={handleSave}>Save</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default EditQuestion;
