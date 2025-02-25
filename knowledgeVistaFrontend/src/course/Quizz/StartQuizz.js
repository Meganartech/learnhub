import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import baseUrl from '../../api/utils';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import QuizzInstruction from './QuizzInstruction';

const StartQuizz = () => {
    const { quizzName,quizzId,batchName, batchId } = useParams();
    const [questions, setQuestions] = useState([]);
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [selectedAnswers, setSelectedAnswers] = useState([]); // Array of objects
    const token = sessionStorage.getItem("token");
    const MySwal = withReactContent(Swal);
    const navigate = useNavigate();
        const [proceedClicked, setProceedClicked] = useState(false);
    const handleProceedClick = () => {
        setProceedClicked(true);
    };
    useEffect(() => {
        const fetchQuestions = async () => {
            try {
                const response = await axios.get(`${baseUrl}/Quizz/Start`, {
                    params: { quizzId, batchId },
                    headers: { Authorization: token }
                });

                if (response.status === 200) {
                    setQuestions(response.data);
                } else if (response.status === 204) {
                    navigate("/notFound");
                 
                }
            } catch (err) {
                console.log(err);
                if (err?.response?.status === 401) {
                    navigate("/unauthorized");
                } else if (err?.response?.status === 403) {
                    MySwal.fire({
                        icon: "warning",
                        title: "Forbidden",
                        text: err?.response?.data || "Access Denied",
                    });
                    navigate(-1);
                } else {
                    console.log(err);
                }
            }
        };

        fetchQuestions();
    }, []);

    // Handle answer selection
    const handleSelectAnswer = (questionId, answerText) => {
        setSelectedAnswers((prevAnswers) => {
            const existingAnswerIndex = prevAnswers.findIndex(ans => ans.questionId === questionId);

            if (existingAnswerIndex !== -1) {
                // Update existing answer
                const updatedAnswers = [...prevAnswers];
                updatedAnswers[existingAnswerIndex] = { questionId, selected: answerText };
                return updatedAnswers;
            } else {
                // Add new answer
                return [...prevAnswers, { questionId, selected: answerText }];
            }
        });
    };

    // Move to next question
    const handleNextQuestion = () => {
        setCurrentQuestionIndex(currentQuestionIndex + 1);
    };

    // Move to previous question
    const handlePrevQuestion = () => {
        setCurrentQuestionIndex(currentQuestionIndex - 1);
    };

    // Handle quiz submission
    const handleSubmit =async () => {
        if (selectedAnswers.length === 0) {
            alert("Please answer all questions before submitting.");
            return;
        }
        try{
         const response= await axios.post(`${baseUrl}/Quizz/submit`,selectedAnswers,{
            params:{
                quizzId:quizzId,
            },
            headers:{
                Authorization:token
            }
         })
         if(response?.status===200){
            MySwal.fire({
                icon:"success",
                title:response?.data
            })
            window.location.href="/user/ProgramCalender"
         }
        }catch(err){
            if(err?.response && err?.response?.status===401){
                MySwal.fire({
                    icon:"warning",
                    title:"unAuthorized",
                    text:err?.response.data
                })
                navigate("/unauthorized")
            }
            console.log(err)
        }
    };

    return (
        <div>
            <div className="page-header"></div>
            {!proceedClicked ? (<QuizzInstruction handleProceedClick={handleProceedClick} quizzName={quizzName} noofQuestion={questions?.length}/>):(
            <div className="card">
                <div className="card-body">
                    <div className="row">
                        <div className="col-12">
                            {questions.length === 0 ? (
                                <div className="centerflex">
                                    <div className="enroll">
                                        <h2>Quiz Not Found</h2>
                                        <button onClick={() => navigate(-1)} className="btn btn-primary">Go Back</button>
                                    </div>
                                </div>
                            ) : (
                                <div className="atgrid">
                                    <h3>{questions[currentQuestionIndex].questionText}</h3>
                                    <ul className="listgroup">
                                        {[1, 2, 3, 4].map((num) => {
                                            const optionValue = questions[currentQuestionIndex][`option${num}`];
                                            return (
                                                <li className="lielement" key={num}>
                                                    <input
                                                        type="radio"
                                                        id={`option${num}-${questions[currentQuestionIndex].questionId}`}
                                                        name={`answer-${questions[currentQuestionIndex].questionId}`}
                                                        value={optionValue}
                                                        checked={selectedAnswers.some(
                                                            ans => ans.questionId === questions[currentQuestionIndex].questionId && ans.selected === optionValue
                                                        )}
                                                        onChange={() => handleSelectAnswer(questions[currentQuestionIndex].questionId, optionValue)}
                                                    />
                                                    <label htmlFor={`option${num}-${questions[currentQuestionIndex].questionId}`}>
                                                        {optionValue}
                                                    </label>
                                                </li>
                                            );
                                        })}
                                    </ul>

                                    <div className="cornerbtns">
    {/* Previous Button - Aligned Left */}
    {currentQuestionIndex > 0 && (
        <button className="btn btn-primary" onClick={handlePrevQuestion}>
            Previous
        </button>
    )}

    {/* Spacer to push Next/Submit to the right */}
    <div style={{ flex: 1 }}></div>

    {/* Next and Submit Buttons - Aligned Right */}
    {currentQuestionIndex < questions.length - 1 ? (
        <button
            onClick={handleNextQuestion}
            disabled={!selectedAnswers.some(ans => ans.questionId === questions[currentQuestionIndex].questionId)}
            className="btn btn-primary"
        >
            Next
        </button>
    ) : (
        <button onClick={handleSubmit} className="btn btn-success">
            Submit
        </button>
    )}
</div>

                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>)}
        </div>
    );
};

export default StartQuizz;
