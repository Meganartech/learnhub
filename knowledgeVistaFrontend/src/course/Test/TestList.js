import React, { useEffect, useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import "../../css/test.css"
import { useParams } from 'react-router-dom';

const TestList = () => {
  const { courseId } = useParams();
  const MySwal = withReactContent(Swal);
  const [test, setTest] = useState(null); // Change to single test instead of array
  const [notFound, setNotFound] = useState(false); // State to track if test is not found

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`http://localhost:8080/test/getall/${courseId}`);
        if (response.status === 404) {
          setNotFound(true); // Set notFound state to true if test is not found
        } else if (!response.ok) {
          throw new Error('Failed to fetch test');
        } else {
          const data = await response.json();
          setTest(data);
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
      title: "Delete Test?",
      text: "Are you sure you want to delete this test?",
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



  return (
    <div className='contentbackground'>
      <div className='contentinner'>
        {notFound ? (
          <div className='enroll'>
            <h2>No test found for this course.</h2>
            <a href={`/course/AddTest/${courseId}`} className='btn btn-primary'>Add Test</a>
          </div>
        ) : (
          test && (
            <div>
     
                <div className='singletest'>
                  <span><b>Course Name:</b> {test.coursename}</span>
                  <span><b>Test Name:</b> {test.testName}</span>
                  <span><b>Number of Questions:</b> {test.noOfQuestions}</span>
                  <span><b>Pass Mark:</b> {test.passPercentage}</span>
                  <span><b>Number Of Attempt:</b> {test.noofattempt}</span>
             
                  <span onClick={() => handleDelete(test.testId)}><i className="fa-solid fa-trash text-danger"></i></span>
                </div>
              
              {/* Render questions if test is available */}
              {test.questions && (
                <div className="table-container">
                <table className='table table-hover mt-5 table-bordered table-sm'>
                  <thead className='thead-dark'>
                    <tr>
                    <th scope="col" >S.no</th>
                      <th scope="col" >Question</th>
                      <th scope="col" >Option 1</th>
                      <th scope="col" >Option 2</th>
                      <th scope="col" >Option 3</th>
                      <th scope="col" >Option 4</th>
                      <th scope="col" >Answer</th>
                    </tr>
                  </thead>
                  <tbody>
                    {test.questions.map((question, index) => (
                      <tr key={index}>
                        <td>{index+1}</td>
                        <td >{question.questionText}</td>
                        <td >{question.option1}</td>
                        <td >{question.option2}</td>
                        <td >{question.option3}</td>
                        <td >{question.option4}</td>
                        <td >{question.answer}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                </div>
              )}
            </div>
          )
        )}
      </div>
    </div>
  );
}

export default TestList;
