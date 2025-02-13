import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils";
import axios from "axios";
const ViewQuizz = () => {
  const { courseName, courseID, lessonsName, lessonId, quizzName, quizzId } =
    useParams();
  const navigate = useNavigate();
  const MySwal = withReactContent(Swal);
  const [quizz, setquizz] = useState([]);
  const [submitting, setsubmitting] = useState(true);
  const [notFound, setNotFound] = useState(false);
  const token = sessionStorage.getItem("token");
  const [selectedIds,setselectedIds]=useState([]);
  useEffect(() => {
    const fetchQuizzQuestions = async () => {
      try {
        const response = await axios.get(`${baseUrl}/Quizz/${quizzId}`, {
          headers: {
            Authorization: token,
          },
        });
        setsubmitting(false);
        if (response.status === 200) {
       
          const data = response.data;
          setquizz(data);
        }
        if(response.status===204){
          navigate(`/AddQuizz/${courseName}/${courseID}/${lessonsName}/${lessonId}`)
        }
      } catch (error) {
        setsubmitting(false);
        if (error.response && error.response.status === 401) {
          navigate("/unauthorized");
        } else if (error.response && error.response.status === 404) {
          setNotFound(true);
        } else {
          // MySwal.fire({
          //   title: "Error",
          //   text: error.response,
          //   icon: "error",
          //   confirmButtonText: "OK"
          // });
          throw error;
        }
      }
    };

    fetchQuizzQuestions();
  }, []);
  const handleNavigation = () => {
    const role = sessionStorage.getItem("role");
    if (role === "ADMIN") {
      navigate("/course/admin/edit");
    } else if (role === "TRAINER") {
      navigate("/AssignedCourses");
    } else {
      navigate("/unauthorized");
    }
  };
  const handleQuestionselect=(id)=>{
    setselectedIds((prevSelectedIds) => {
      if (prevSelectedIds.includes(id)) {
        // If the ID is already present, remove it
        return prevSelectedIds.filter((selectedId) => selectedId !== id);
      } else {
        // If the ID is not present, add it
        return [...prevSelectedIds, id];
      }
    });
    
  }
  const DeleteQuestion = async () => {
    if(selectedIds.length<=0){
      return
    }
    MySwal.fire({
      title: "Delete Test?",
      text: "Are you sure you want to delete this Questions?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Delete",
      cancelButtonText: "Cancel",
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          if (quizzId != null) {
            const response = await axios.delete(`${baseUrl}/Quizz/Delete/${quizzId}`, {
              data: selectedIds, 
              headers: {
                "Authorization": token
              }
            });
            
            if (response.status===200) {
              window.location.reload();
            }
          }
        } catch (error) {
          setselectedIds([])
          console.error('Error deleting test:', error);
          throw error
        }
      } 
      setselectedIds([])
    });
  };
  const handleSelectAll = () => {
    if (selectedIds.length === quizz.length) {
      // If all are selected, deselect all
      setselectedIds([]);
    } else {
      // Select all question IDs
      const allQuestionIds = quizz.map(q => q.questionId);
      setselectedIds(allQuestionIds);
    }
  };
  return (
    <div>
      <div className="page-header">
        <div className="page-block">
          <div className="row align-items-center">
            <div className="col-md-12">
              <div className="page-header-title">
                <h5 className="m-b-10">Quizz</h5>
              </div>
              <ul className="breadcrumb">
                <li className="breadcrumb-item">
                  <a href="#" onClick={handleNavigation}>
                    <i className="feather icon-layout"></i>
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a
                    href="#"
                    onClick={() => {
                      navigate(`/lessonList/${courseName}/${courseID}`);
                    }}
                  >
                    {lessonsName}
                  </a>
                </li>
                <li className="breadcrumb-item">
                  <a href="#">{quizzName}</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col-sm-12">
          <div className="card">
            <div className="card-header">
              <div className="navigateheaders" style={{ margin: "2px" }}>
                <div
                  onClick={() => {
                    navigate(-1);
                  }}
                >
                  <i className="fa-solid fa-arrow-left"></i>
                </div>
                <div></div>
                <div
                  onClick={() => {
                    navigate("/dashboard/course");
                  }}
                >
                  <i className="fa-solid fa-xmark"></i>
                </div>
              </div>
              {submitting && (
                <div className="outerspinner active">
                  <div className="spinner"></div>
                </div>
              )}
              <div className="headingandbutton">
                <h4 className="text-center " style={{ margin: "0px" }}>
                  {quizzName}
                </h4>
                <Link
                  to={`/AddQuestionInQuizz/${courseName}/${courseID}/${lessonsName}/${lessonId}/${quizzName}/${quizzId}`}
                  className="btn btn-primary mr-2"
                  style={{ width: "130px" }}
                >
                  <i className="fa fa-plus"></i> Add more{" "}
                </Link>{" "}
                <div></div>
              </div>
              <span className='singlerow' >
                <span >
                  <i className="fa-solid fa-trash text-danger" onClick={DeleteQuestion} style={{ fontSize: '20px',paddingTop:"20px" }}></i>
                </span>
                
              
                </span>
              {quizz && (
                <div className="table-container mt-2">
                  <table className="table table-hover  table-bordered table-sm">
                    <thead className="thead-dark">
                      <tr>
                      <th scope="col" style={{width:"50px"}}><i className="fa-solid fa-list-check " title='Select All' onClick={handleSelectAll}></i></th>
                        <th scope="col" style={{width:"50px"}}>S.no</th>
                        <th scope="col">Question</th>
                        <th scope="col">Option 1</th>
                        <th scope="col">Option 2</th>
                        <th scope="col">Option 3</th>
                        <th scope="col">Option 4</th>
                        <th scope="col" >Answer</th>
                        <th scope="col"style={{width:"50px"}}>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {quizz.map((question, index) => (
                        <tr key={index}>
                          <td><input type='checkbox' title='Select'
                        checked={selectedIds.includes(question.questionId)} // Check if the ID is in selectedIds
                        onChange={() => handleQuestionselect(question.questionId)} // Prevents default event propagation
                      /></td>
                          <td>{index + 1}</td>
                          <td>{question.questionText}</td>
                          <td>{question.option1}</td>
                          <td>{question.option2}</td>
                          <td>{question.option3}</td>
                          <td>{question.option4}</td>
                          <td>{question.answer}</td>
                          <td>
                            <Link
                              to={`/editQuizzQuestion/${courseName}/${courseID}/${lessonsName}/${lessonId}/${quizzName}/${quizzId}/${question.questionId}`}
                            >
                              {" "}
                              <i className="fas fa-edit text-primary"></i>
                            </Link>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ViewQuizz;
