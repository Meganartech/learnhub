import React, { useEffect, useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import "./test.css"
import { useParams } from 'react-router-dom';


const TestList = () => {
  
  const {courseId}=useParams();
  const MySwal = withReactContent(Swal);
  const [tests, setTests] = useState([]);
  const [selectedTestId, setSelectedTestId] = useState(null);
  const [notFound, setNotFound] = useState(false); // State to track if tests are not found

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`http://localhost:8080/test/getall/${courseId}`);
        console.log(courseId);
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
  const handleDelete = async (testId) => {
    MySwal.fire({
        title: "Delete Course?",
        text: "Are you sure you want to delete this course?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        confirmButtonText: "Delete",
        cancelButtonText: "Cancel",
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                if (testId != null) {
                    const response = await fetch(`http://localhost:8080/test/${testId}`, {
                        method: "DELETE" // Method should be part of the object
                    });

                    // Check if the response is successful (status code 200-299)
                    if (response.ok) {
                        console.log(`Test with ID ${testId} deleted successfully`);
                        window.location.reload();
                        // Optionally, you can update your UI or state here
                    }
                }
            } catch (error) {
                console.error('Error deleting test:', error);
            }
        } else {
            // Handle the case when the user cancels the deletion
            console.log('Deletion cancelled');
        }
    });
};
 
  const toggleTestQuestions = (testId) => {
    setSelectedTestId(prevTestId => (prevTestId === testId ? null : testId));
  };

  return (
    <div className='testcontainermain'>
      <div className='testscontainer'>
        {notFound ? ( 
         <div className='notfound'>
         <h2>No tests found for this course.</h2>
           <a href ={`/course/AddTest/${courseId}`} className='btn btn-primary'>add test</a>
       </div>
       
        ) : (
          tests.map((test, index) => (
            <div key={index}>
              <div className='testcontainer'>
                <div className='singletest'>
                  <span>{index + 1}</span>
                  <span>{test.testName}</span>
                  <span>Number of Questions: {test.noOfQuestions}</span>
                  <span>Pass Mark: {test.passPercentage}</span>
                  <span>Number Of Attempt :{test.noofattempt}</span>
                  <span onClick={() =>handleDelete(test.testId)}><i className="fa-solid fa-trash"></i></span>
                  <span  onClick={() => toggleTestQuestions(test.testId)}className={`icon ${selectedTestId === test.testId ? 'selected' : ''}`}>
                    {selectedTestId === test.testId ? '▲' : '▼'}
                  </span>
                </div>
              </div>
              {selectedTestId === test.testId && test.questions && (
                <table className='Questionstable'>
                  <thead className='Qthead'>
                    <tr>
                      <th>Question</th>
                      <th>Option 1</th>
                      <th>Option 2</th>
                      <th>Option 3</th>
                      <th>Option 4</th>
                      <th>Answer</th>
                    </tr>
                  </thead>
                  <tbody>
                    {test.questions.map((question, index) => (
                      <tr key={index}>
                        <td className='bodyelement'>{question.questionText}</td>
                        <td className='bodyelement'>{question.option1}</td>
                        <td className='bodyelement'>{question.option2}</td>
                        <td className='bodyelement'>{question.option3}</td>
                        <td className='bodyelement'>{question.option4}</td>
                        <td className='bodyelement'>{question.answer}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default TestList;
