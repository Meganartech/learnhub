import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const AttenTest = () => {
    const MySwal = withReactContent(Swal);
    // const {userId}=sessionStorage.getItem("userid");
    const {courseId,courseName}=useParams();
    
  const [isSubmitting, setIsSubmitting] = useState(false);
    const [testdetails,settestdetails]=useState();
    const [questions, setQuestions] = useState([]);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [selectedAnswers, setSelectedAnswers] = useState({});
    const [proceedClicked, setProceedClicked] = useState(false);
    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch(`http://localhost:8080/test/getTestByCourseid/${courseId}`);
                if (!response.ok) {
                    throw new Error('Failed to fetch questions');
                }
                const data = await response.json();
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
                setQuestions(transformedQuestions);
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
      // Create an array to store question IDs with selected answers
      const selectedAnswersArray = [];
      
      // Iterate over selectedAnswers object to populate the array
      for (const questionIndex in selectedAnswers) {
          const questionId = questions[questionIndex].questionId;
          const selectedAnswer = selectedAnswers[questionIndex];
          selectedAnswersArray.push({ questionId, selectedAnswer });
      }
  
      try {
        const token = sessionStorage.getItem("token");
     
          // Send the selectedAnswersArray to the server
          const response = await fetch(`http://localhost:8080/test/calculateMarks/${courseId}`, {
              method: 'POST',
              headers: {
                "Authorization": token,
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify(selectedAnswersArray)
          });
  
          if (!response.ok) {
              throw new Error('Failed to submit the test');
              
    setIsSubmitting(false);
          }
  
          setIsSubmitting(false);
          // Handle the response from the server
          const data = await response.json();
          if(data.result === "pass"){

          
            MySwal.fire({
                icon: 'success',
                title: 'Congratulations!',
                text: `${data.message}`,
            }).then(() => {
                window.location.href="/MyCertificateList"; // Redirect after the user clicks "OK"
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
          // Handle any errors that occur during the fetch
          console.error('Error:', error);
      }
  };
  
  const handleProceedClick = () => {
    setProceedClicked(true);
};
    return (
        <div className='contentbackground'>
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
                        <li>The Test Consist of <b>{ `${testdetails ? testdetails.noOfQuestions : ""}`}</b> Multiple-choice Questions </li>
                        <li>Each Question carries one Mark </li>
                        <li>Read the Question Carefully and select your Answer</li>
                        <li>To pass this test you have to score atleast<b> {`${testdetails?(testdetails.passPercentage):("")}`}% </b>to get the certificate</li>
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
            ) : null}
        </div>
    );
};

export default AttenTest;
