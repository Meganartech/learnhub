import React, { useEffect, useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import "../../css/test.css"
import { Link, useParams } from 'react-router-dom';

const TestList = () => {
  const { courseId } = useParams();
  const MySwal = withReactContent(Swal);
  const [test, setTest] = useState(null); // Change to single test instead of array
  const [notFound, setNotFound] = useState(false); // State to track if test is not found
  const [editedTest, setEditedTest] = useState({}); 
  const [errors, setErrors] = useState({
    noofattempt: "",
    passPercentage: "",
    testName :""
  });
  const token = sessionStorage.getItem("token");
  const [editingField, setEditingField] = useState(null); // State to track which field is being edited


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
          setEditedTest(data)
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

  // Function to toggle between edit and check icons
  const toggleEditingField = (field) => {
    if (editingField === field) {
      setEditingField(null); // Reset if the same field is clicked again
    } else {
      setEditingField(field);
    }
  };

  const handleSaveTest = async (name, value) => {
    try {
      // Check if there are errors
      if (!errors[name] && value !== null) {
        const formData = new FormData();
        formData.append(name, value);
        const response = await fetch(`http://localhost:8080/test/update/${test.testId}`, {
          method: "PATCH",
          headers: {
            "Authorization": token
          },
          body: formData
        });
        if (response.ok) {
          window.location.reload();
        }
      }
    } catch (error) {
      console.error('Error updating test:', error);
    }
  };

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
              method: "DELETE"
            });

            // Check if the response is successful (status code 200-299)
            if (response.ok) {
              console.log(`Test  deleted successfully`);
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

  const DeleteQuestion = async (questionId) => {
    MySwal.fire({
      title: "Delete Test?",
      text: "Are you sure you want to delete this Question?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Delete",
      cancelButtonText: "Cancel",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          if (questionId != null) {
            const response = await fetch(`http://localhost:8080/test/questions/${questionId}`, {
              method: "DELETE",
              headers:{
                "Authorization":token
              }
            });

            // Check if the response is successful (status code 200-299)
            if (response.ok) {
              console.log(`Test  deleted successfully`);
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

  const handleCriteriaChange = (e) => {
    const { name, value } = e.target;
    let error = "";
    
    // Convert value to a number if it is an attempt count or percentage
    const numericValue = name === "noofattempt" || name === "passPercentage" ? parseFloat(value) : value;
    
    switch (name) {
      case "testName":
        
      error = value.length < 1 ? 'Test name Cannot be Empty' : '';
      setEditedTest({ ...editedTest, testName: value });
      break;
      case "noofattempt":
        error = !numericValue || numericValue < 1 ? "Number of attempts must be at least 1." : "";
        setEditedTest({ ...editedTest, noofattempt: numericValue }); // Use numericValue here
        break;
      case "passPercentage":
        error = !numericValue || numericValue < 1 || numericValue > 100 ? "Pass percentage must be between 1 and 100." : "";
        setEditedTest({ ...editedTest, passPercentage: numericValue }); // Use numericValue here
        break;
      default:
        break;
    }
    
    // Update error state
    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: error
    }));
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
              <h3 className='text-center '><b>{test.coursename}</b></h3>
              <div className='singletest'>
                <span className='edititems'>
                  <b>Test Name:</b>
                  {editingField === 'testName' ? (
                    <>
                     <div> <input
                        type="text"
                        name='testName'
                        className={`form-control form-control-lg smalltextbox ${errors.testName && 'is-invalid'}`}
                        value={editedTest && editedTest.testName !== undefined ? editedTest.testName : test.testName}
                        onChange={handleCriteriaChange}
                        />
                        {errors.testName && (
                          <div className="invalid-feedback">{errors.testName}</div>
                        )}</div>
                      <i className="fa-solid fa-check text-success " onClick={() => handleSaveTest("testName", editedTest.testName)}></i>
                    </>
                  ) : (
                    <>
                      <h5 onDoubleClick={() => toggleEditingField('testName')}>{test.testName}</h5>
                    </>
                  )}
                </span>
                <span className='edititems'>
                  <b>Pass Mark:</b>
                  {editingField === 'passPercentage' ? (
                    <>
                      <div>
                        <input
                          type="number"
                          name='passPercentage'
                          className={`form-control form-control-lg smalltextbox ${errors.passPercentage && 'is-invalid'}`}
                          value={editedTest && editedTest.passPercentage !== undefined ? editedTest.passPercentage : test.passPercentage}
                          onChange={handleCriteriaChange}
                        />
                        {errors.passPercentage && (
                          <div className="invalid-feedback">{errors.passPercentage}</div>
                        )}
                      </div>
                      <i className={`fa-solid fa-check text-success   ${errors.passPercentage || editedTest.passPercentage === null ? 'disabled' : ''}`} id='passPercentage' onClick={() => handleSaveTest("passPercentage", editedTest.passPercentage)}></i>
                    </>
                  ) : (
                    <>
                     <h5 onDoubleClick={() => toggleEditingField('passPercentage')}> {test.passPercentage}</h5>
                       </>
                  )}
                </span>
                <span className='edititems'>
                  <b>Number Of Attempt:</b>
                  {editingField === 'noofattempt' ? (
                    <>
                      <div>
                        <input
                          type="number"
                          name='noofattempt'
                          className={`form-control form-control-lg  smalltextbox ${errors.noofattempt && 'is-invalid'}`}
                          value={editedTest && editedTest.noofattempt !== undefined ? editedTest.noofattempt : test.noofattempt}
                          onChange={handleCriteriaChange}
                        />
                        {errors.noofattempt && (
                          <div className="invalid-feedback">{errors.noofattempt}</div>
                        )}
                      </div>
                      <i className={`fa-solid fa-check text-success ${errors.noofattempt || editedTest.noofattempt === null ? 'disabled' : ''}`} onClick={() => handleSaveTest("noofattempt", editedTest.noofattempt)}></i>
                    </>
                  ) : (
                    <>
                    <h5 onDoubleClick={() => toggleEditingField('noofattempt')}>  {test.noofattempt}</h5>
                       </>
                  )}
                </span>
                <span>
                  <b>Number of Questions:</b> {test.noOfQuestions}
                </span>
                <span onClick={() => handleDelete(test.testId)}>
                  <i className="fa-solid fa-trash text-danger"></i>
                </span>
              </div>
              {/* Render questions if test is available */}
              {test.questions && (
                <div className="table-container">
                  <table className='table table-hover mt-5 table-bordered table-sm'>
                    <thead className='thead-dark'>
                      <tr>
                        <th scope="col">S.no</th>
                        <th scope="col">Question</th>
                        <th scope="col">Option 1</th>
                        <th scope="col">Option 2</th>
                        <th scope="col">Option 3</th>
                        <th scope="col">Option 4</th>
                        <th scope="col">Answer</th>
                        <th scope="col" colSpan={2}>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {test.questions.map((question, index) => (
                        <tr key={index}>
                          <td>{index + 1}</td>
                          <td>{question.questionText}</td>
                          <td>{question.option1}</td>
                          <td>{question.option2}</td>
                          <td>{question.option3}</td>
                          <td>{question.option4}</td>
                          <td>{question.answer}</td>
                          <td className='text-center'>
                            <Link to={`/test/Edit/${question.questionId}`}>
                              <i className='fa text-primary fa-edit'></i>
                            </Link>
                          </td>
                          <td className='text-center'>
                            {/* Conditionally render delete button */}
                            {test.questions.length > 1 ? (
                              <i className='fa fa-trash text-danger' onClick={() => DeleteQuestion(question.questionId)}></i>
                            ) : (
                              // Render disabled delete button
                              <i className='fa fa-trash text-danger' style={{ cursor: 'not-allowed', opacity: 0.5 }}></i>
                            )}
                          </td>
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
