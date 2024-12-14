import React, { useEffect, useState } from 'react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { Link, useNavigate, useParams } from 'react-router-dom';
import baseUrl from '../../api/utils';
import axios from 'axios';

const TestList = () => {
  const {courseName, courseId } = useParams();
  const navigate=useNavigate();
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
        const response = await axios.get(`${baseUrl}/test/getall/${courseId}`, {
          headers: {
            Authorization: token,
          }
        });
          if (response.status===200) {
          const data =  response.data;
          setTest(data);
          setEditedTest(data)
        }
      } catch (error) {
        if(error.response && error.response.status===401)
          {
            navigate("/unauthorized")
          }else if(error.response && error.response.status===404){
          setNotFound(true);
        }else{
        // MySwal.fire({
        //   title: "Error",
        //   text: error.response,
        //   icon: "error",
        //   confirmButtonText: "OK"
        // });
        throw error
      }
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
      // Check if9 there are errors

     
      if (!errors[name] && value !== null) {
        const formData = new FormData();
        formData.append(name, value);
        const response = await axios.patch(`${baseUrl}/test/update/${test.testId}`, formData,{
          headers: {
            "Authorization": token
          }
        });
        if (response.status===200) {
          window.location.reload();
        }
      }
    } catch (error) {
      console.error('Error updating test:', error);
      throw error
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
            const response = await axios.delete(`${baseUrl}/test/${testId}`, {
              headers: {
                Authorization: token,
               }
            });
            if (response.status===200) {
              window.location.reload();
            }
          }
        } catch (error) {
          if(error.response && error.response.status===401)
          {
            navigate("/unauthorized")
          }else{
            // MySwal.fire({
            //   title: "Error!",
            //   text: error.response.data ? error.response.data : "error occured",
            //   icon: "error",
            //   confirmButtonText: "OK",
            // });
            throw error
          }
        }
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
            const response = await axios.delete(`${baseUrl}/test/questions/${questionId}`, {
              headers:{
                "Authorization":token
              }
            });
            if (response.status===200) {
              window.location.reload();
            }
          }
        } catch (error) {
          console.error('Error deleting test:', error);
          throw error
        }
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
    <div>
    <div className="page-header"></div>
    <div className='row'>
      <div className='col-sm-12'>
        <div className='card'>
          <div className='card-header'>
      <div className='navigateheaders' style={{margin:"2px"}}>
      <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
      <div></div>
      <div onClick={()=>{navigate("/dashboard/course")}}><i className="fa-solid fa-xmark"></i></div>
      </div>
      <h4 className='text-center 'style={{margin:"0px"}}>{courseName}</h4>
             
     </div>
        {notFound ? (
          <div className='centerflex'>
          <div className='enroll'>
            <h4>No test found for the  course {courseName}</h4>
            <a href={`/course/AddTest/${courseName}/${courseId}`} className='btn btn-primary'>Add Test</a>
          </div></div>
        ) : (
          test && (
            <div className='card-body'>
              <div className='singletest'>
                <span className='edititems'>
                  <b>Test Name:</b>
                  {editingField === 'testName' ? (
                    <>
                     <div> <input
                        type="text"
                        name='testName'
                        className={`form-control .form-control-sm  smalltextbox ${errors.testName && 'is-invalid'}`}
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
                          className={`form-control .form-control-sm  smalltextbox ${errors.passPercentage && 'is-invalid'}`}
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
                  <b>No Of Attempt:</b>
                  {editingField === 'noofattempt' ? (
                    <>
                      <div>
                        <input
                          type="number"
                          name='noofattempt'
                          className={`form-control .form-control-sm   smalltextbox ${errors.noofattempt && 'is-invalid'}`}
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
              
              </div>
              <span className='atbtndiv' >
                <span onClick={() => handleDelete(test.testId)}>
                  <i className="fa-solid fa-trash text-danger" style={{ fontSize: '20px',paddingTop:"20px" }}></i>
                </span>
                <div></div>
                <div>
                <Link to={`/test/AddMore/${courseName}/${test.testId}`} className='btn btn-primary mr-2' style={{width:"150px"}}><i className='fa fa-plus'></i> Add more </Link>
                </div>
                </span>
               
              {test.questions && (
                <div className="table-container mt-2">
                  <table className='table table-hover  table-bordered table-sm'>
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
                            {test.questions.length > 1 ? (
                              <i className='fa fa-trash text-danger' onClick={() => DeleteQuestion(question.questionId)}></i>
                            ) : (
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
        </div>
        </div>
  );
}

export default TestList;
