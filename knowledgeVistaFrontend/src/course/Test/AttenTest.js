import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

const AttenTest = () => {
    const MySwal = withReactContent(Swal);
    // const {userId}=sessionStorage.getItem("userid");
    const {courseId}=useParams();
    const { testId } = useParams();
    const [questions, setQuestions] = useState([]);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [selectedAnswers, setSelectedAnswers] = useState({});

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch(`http://localhost:8080/test/getbytestid/${testId}`);
                if (!response.ok) {
                    throw new Error('Failed to fetch questions');
                }
                const data = await response.json();
                

                // Transform the data structure to match the expected format
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
          const response = await fetch(`http://localhost:8080/test/${testId}/calculateMarks/${courseId}`, {
              method: 'POST',
              headers: {
                "Authorization": token,
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify(selectedAnswersArray)
          });
  
          if (!response.ok) {
              throw new Error('Failed to submit the test');
          }
  
          // Handle the response from the server
          const data = await response.json();
          if(data.result === "pass"){

          
            MySwal.fire({
                icon: 'success',
                title: 'Congratulations!',
                text: `${data.message}`,
            }).then(() => {
                window.location.href = "/dashboard/course"; // Redirect after the user clicks "OK"
            });
        }
          else{
            MySwal.fire({
                
                title :'Do well next Time',
                text:`${data.message}`
            }).then(()=>{
                window.location.href="/dashboard/course";
            })
          }
          
      } catch (error) {
          // Handle any errors that occur during the fetch
          console.error('Error:', error);
      }
  };
  
  
    return (
        <div className='atbg p-4'>
            {questions.length > 0 && currentQuestionIndex < questions.length ? (
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
        
            ) : (
                <div className='atdiv'>
                    <div className="text-center mt-5">
                        <h3>No More questions available...</h3>
                        <h4>All questions answered. You can preview your responses if needed, otherwise submit the test.</h4>
                        <h1 className="display-1"><i className="fas fa-check-circle text-success me-2"></i> All the best!!</h1>
                    </div>

                    <div className='atbtndiv ' >
                      
                      
                    <button onClick={handlePrevQuestion}className='btn btn-primary'>Previous</button><div></div>
                    <button onClick={handleSubmit} className='btn btn-primary'>Submit</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AttenTest;
