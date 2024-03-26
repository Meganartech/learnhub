import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import "./test.css";

const AttenTestList = () => {
    const { courseId } = useParams();
    const MySwal = withReactContent(Swal);
    const [tests, setTests] = useState([]);
    const [selectedTestId, setSelectedTestId] = useState(null);
    const [notFound, setNotFound] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch(`http://localhost:8080/test/getall/user/${courseId}`);
                if (response.status === 404) {
                    setNotFound(true); // Set notFound state to true if tests are not found
                } else if (!response.ok) {
                    throw new Error('Failed to fetch questions');
                } else {
                    const data = await response.json();
                    setTests(data);
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
    }, [courseId]);

    const toggleTestQuestions = (testId) => {
        setSelectedTestId(selectedTestId === testId ? null : testId);
    };

    const specificRedirect = (testId) => {
        // Redirect to the specific test start page
        window.location.href = `/test/start/${courseId}/${testId}`;
    };

    return (
        <div className='testcontainermain'>
            {notFound ? (
                <div className='notfound'>
                    <h2>No tests found for this course.</h2>
                </div>
            ) : (
                <div className='testscontainer'>
                    {tests.map((test, index) => (
                        <div key={index}>
                            <div className='testcontainer' onClick={() => toggleTestQuestions(test.testId)}>
                                <div className='singletest'>
                                    <span>{index + 1}</span>
                                    <span>{test.testName}</span>
                                    <span>Number of Questions: {test.noOfQuestions}</span>
                  <span>Pass Mark: {test.passPercentage}</span>
                  <span>Number Of Attempt :{test.noofattempt}</span>
                                    <span>
                                        <button className='btn btn-primary' onClick={() => specificRedirect(test.testId,courseId)} style={{ margin: 0, marginTop: 1 }}>
                                            Start
                                        </button>
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default AttenTestList;
