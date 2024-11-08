import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../../api/utils';
import axios from 'axios';

const AttenTest = () => {
    const navigate =useNavigate();
    const MySwal = withReactContent(Swal);
    const {courseId,courseName}=useParams();
    const role=sessionStorage.getItem("role");
    const token =sessionStorage.getItem("token")
  const [isSubmitting, setIsSubmitting] = useState(false);
    const [testdetails,settestdetails]=useState({
        passPercentage:"",
        noOfQuestions:"",
    });
    const [attemplimit,setattemptlimit]=useState(false);
    const [questions, setQuestions] = useState([]);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [selectedAnswers, setSelectedAnswers] = useState({});
    const [proceedClicked, setProceedClicked] = useState(false);
    const [notFound, setNotFound] = useState(false); // State to track if test is not found
    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get(`${baseUrl}/test/getTestByCourseId/${courseId}`,{
                     
                headers:{
                    "Authorization":token,
                }
                });
               
               
                  if(response.status===200){
                const data =  response.data;
                
                settestdetails(data);
                const transformedQuestions = data.questions.map(question => ({
                    ...question,
                    options: [
                        { optionId: 1, optionText: question.option1 },
                        { optionId: 2, optionText: question.option2 },
                        { optionId: 3, optionText: question.option3 },
                        { optionId: 4, optionText: question.option4 }
                    ]
                }));
                setQuestions(transformedQuestions);}
            } catch (error) {
                if(error.response && error.response.status===404)
                {
                    setNotFound(true); 
                  }
                  else if(error.response && error.response.status===400){
                    setattemptlimit(true);
                  }else{
                 
                // MySwal.fire({
                //     title: "Error",
                //     text: error.response.data ? error.response.data : "error occured",
                //     icon: "error",
                //     confirmButtonText: "OK"
                // });
                throw error
            }
            }
        };

        fetchData();
    }, []);

    const handleSelectAnswer = (answerText) => {
        setSelectedAnswers({
            ...selectedAnswers,
            [currentQuestionIndex]: answerText
        });
    };

    const handleNextQuestion = () => {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
    };

    const handlePrevQuestion = () => {
        setCurrentQuestionIndex(currentQuestionIndex - 1);
    };
    const handleSubmit = async () => {
    setIsSubmitting(true);
      const selectedAnswersArray = [];
      for (const questionIndex in selectedAnswers) {
          const questionId = questions[questionIndex].questionId;
          const selectedAnswer = selectedAnswers[questionIndex];
          selectedAnswersArray.push({ questionId, selectedAnswer });
      }
  
      try {
        const token = sessionStorage.getItem("token");
      const datatosend= JSON.stringify(selectedAnswersArray)
          const response = await axios.post(`${baseUrl}/test/calculateMarks/${courseId}`,datatosend, {
            
              headers: {
                "Authorization": token,
                  'Content-Type': 'application/json'
              }
          });
          setIsSubmitting(false);
          const data = response.data;
          if(data.result === "pass"){          
            MySwal.fire({
                icon: 'success',
                title: 'Congratulations!',
                text: `${data.message}`,
            }).then(() => {
                window.location.href="/MyCertificateList"; 
            });
        }
          else{
            MySwal.fire({
                
                title :'Do well next Time',
                text:`${data.message}`
            }).then(()=>{
                window.location.href = "/dashboard/course";
            })
          }
          
      } catch (error) {
        
        setIsSubmitting(false);
        const message=error.response
        // MySwal.fire({
        //     title: "Error",
        //     text: message,
        //     icon: "error",
        //     confirmButtonText: "OK"
        // });
        throw error
      }
  };
  
  const handleProceedClick = () => {
    setProceedClicked(true);
};
    return (
        <div className='contentbackground'>
          {attemplimit?(
            <div className='contentinner'>
                <div className='navigateheaders'>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
      </div>
            <div className='centerflex'>
            <div className='enroll ' > 
        <h2 className='mt-2'>Your Attempt Limit Exceeded for this test </h2>
        <p>Contact your Trainer</p>
        <button className='btn btn-primary' onClick={()=>{navigate(-1)}}>Go Back</button>
        </div>
            </div>
            </div>):(  
                <>
              {notFound ? (  

       
        <div className='contentinner'>
            <div className='centerflex'>
        <div className='enroll'> 
        <h2>No test found for this course.</h2>
        
      <button onClick={()=>{navigate(-1)}} className='btn btn-primary'>Go Back</button>
        </div></div>
        </div>
      
          
       ) : (<>
            {!proceedClicked && (
            <div className='contentinner'>
            
                <div className='div3'>
                    <h2 style={{textDecoration:"underline"}}>Test Instructions</h2>
                   <div className='instruction'>
                    <h2 style={{textAlign:"center"}}><i className="fa-solid fa-triangle-exclamation"></i></h2>
                    <h3 style={{textAlign:"center"}}>Notice</h3>
                    <p>
                        Welcome for the online test of <b>{courseName}</b>.Please Read the Following Information Carefully before Proceeding 
                    </p>
                    <h5 className='font-weight-bold' > Instruction :</h5>
                    <ul style={{ listStyleType: 'disc' }}>
                        <li>Ensure you have a stable internet connection throughout the duration of the test.</li>
                        <li>Use a Desktop or Laptop with a compatiable browser (Google Chrome ,Mozila Firefox, Safari,or Microsoft Edge).</li>
                        <li>Disable any pop-up blockers and ensure JavaScript is enabled in your Browser Settings</li>
                    </ul>
                    <h5 className='font-weight-bold' >Test Environment :</h5>
                    <ul style={{ listStyleType: 'disc' }}>
                        <li>Find a Quiet and Comfortable envronment to take the test.</li>
                        <li>Avoid distractions and interruptions during the test duration.</li>
                        <li>Make sure your Surroundings are well-lighted and your Screen is easily readable.</li>
                    </ul >
                    <h5 className='font-weight-bold'>Test Format :</h5>
                    <ul style={{ listStyleType: 'disc' }}>
                        <li>The Test Consist of <b>{testdetails.noOfQuestions }</b> Multiple-choice Questions </li>
                        <li>Each Question carries one Mark </li>
                        <li>Read the Question Carefully and select your Answer</li>
                        <li>To pass this test you have to score atleast<b> {testdetails.passPercentage} % </b>to get the certificate</li>
                    </ul >
                    <h5 className='font-weight-bold'>Submission :</h5>
                    <ul style={{ listStyleType: 'disc' }}>
                        <li>Once you have answered all the questions, click on the "Submit" button to submit your test</li>
                        <li>you will not be able to make any changes after the test is submitted.</li>
                    </ul>
                     <p>By Proceeding ,you acknowledged that you have read and understand the instruction provided above</p>
                     <h5 className='font-weight-bold' style={{textAlign:"center"}}>Good Luck !</h5>
                   </div>
                   <div className='atbtndiv' >
                    <Link to={`/courses/${courseName}/${courseId}`} className='btn btn-primary'>cancel</Link><div>

                    </div>
                    <button onClick={handleProceedClick} className='btn btn-primary'>Proceed</button>
                    </div>
            </div>
            </div>)}
            
            {proceedClicked && questions.length > 0 && currentQuestionIndex < questions.length ? (
                <div className='contentinner'>
                <div className='atdiv'>
                  <div className='atgrid'>
                    <h3>{questions[currentQuestionIndex].questionText}</h3>
                    <ul className='listgroup'>
                        {questions[currentQuestionIndex].options.map((option) => (
                            <li key={option.optionId} className='lielement'>
                                <input
                                    type="radio"
                                    id={option.optionId}
                                    name="answer"
                                    value={option.optionText}
                                    checked={selectedAnswers[currentQuestionIndex] === option.optionText}
                                    onChange={() => handleSelectAnswer(option.optionText)}
                             
                                />
                                <label htmlFor={option.optionId}>{option.optionText}</label>
                            </li>
                        ))}
                    </ul>
                   
                   
                </div>
                <div className='atbtndiv'>
                 
                    {currentQuestionIndex > 0 ? (
                        <button className='btn btn-primary' onClick={handlePrevQuestion}>Previous</button>
                            ) : (
                        <div></div>
                    )}

                    <div></div>
                 <button
                 onClick={handleNextQuestion}
                 disabled={!selectedAnswers[currentQuestionIndex]}
                 className='btn btn-primary'
             >
                 Next
             </button></div>
             
                </div>
           </div>

            ) : proceedClicked && Object.keys(selectedAnswers).length === testdetails.noOfQuestions ? (
                // Render submission section if questions are answered
                <div className='contentinner'>
                    <div className='atdiv'>
                        <div className="text-center mt-5">
                            <h3>All questions answered. You can preview your responses if needed, otherwise submit the test.</h3>
                            <h1 className="display-1"><i className="fas fa-check-circle text-success me-2"></i> All the best!!</h1>
                        </div>
                        <div className='atbtndiv'>
                            <button onClick={handlePrevQuestion} className='btn btn-primary' 
                 disabled={isSubmitting}>Previous</button>
                            <div></div>
                            <button onClick={handleSubmit} className='btn btn-primary' 
                 disabled={isSubmitting}>Submit</button>
                        </div>
                    </div>
                </div>
            ) : null} </>)}
            </>)}
        </div>
    );
};

export default AttenTest;
