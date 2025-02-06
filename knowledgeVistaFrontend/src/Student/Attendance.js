import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import baseUrl from '../api/utils';

const Attendance = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [user, setUser] = useState( location.state?.user);
  const [datacounts, setdatacounts] = useState({
      start: "",
      end: "",
      total: "",
    });
  const token = sessionStorage.getItem('token');
  const [attendance, setAttendance] = useState([]);
  const [currentPage, setCurrentPage] = useState(0); // To handle pagination
  const [totalPages, setTotalPages] = useState(0); // To handle the total pages
   const itemsperpage=10
 

  const fetchAttendanceForUser = async () => {
    try {
      const res = await axios.get(`${baseUrl}/view/StudentAttendance/${user.userId}?page=${currentPage}&size=${itemsperpage}`, {
        headers: {
          Authorization: token
        }
      });
      
      if (res.status === 200) {
        setAttendance(res?.data?.content); // Assuming 'content' is the list of attendance
        setTotalPages(res?.data?.totalPages); // Get total pages for pagination
        setdatacounts(() => ({
          start: currentPage * itemsperpage + 1,
          end: currentPage * itemsperpage + itemsperpage,
          total: res?.data.totalElements,
        }));
      }
      if (res.status === 204) {
        navigate("/notFound");
      }
    } catch (error) {
      if (error.response && error.response.status === 401) {
        navigate("/unauthorized");
      } else {
        throw error;
      }
    }
  };

  useEffect(() => {
    fetchAttendanceForUser();
  }, [currentPage]); // Fetch attendance every time currentPage changes

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setCurrentPage(newPage);
    }
  };
  const renderPaginationButtons = () => {
    const buttons = [];
    for (let i = 0; i < totalPages; i++) {
      buttons.push(
        <li
          className={`page-item ${i === currentPage ? "active" : ""}`}
          key={i}
        >
          <a className="page-link" href="#" onClick={() => handlePageChange(i)}>
            {i + 1}
          </a>
        </li>
      );
    }
    return buttons;
  };


  return (
    <div>
      <div className="page-header"></div>
      <div className="row">
        <div className="col-sm-12">
          <div className="card">
            <div className="card-header">
              <div className="navigateheaders">
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
                    navigate(-1);
                  }}
                >
                  <i className="fa-solid fa-xmark"></i>
                </div>
              </div>
              <div className="tableheader ">
                <h4>Attendance</h4>
              </div>{" "}
              <div className="detailstab">
                <div>
                  <h6>Name :</h6> <label>{user?.username}</label>
                </div>
                <div>
                  <h6>Email :</h6>
                  <label>{user?.email}</label>
                </div>
                <div>
                  <h6>Contact No :</h6>
                  <label>{user?.phone}</label>
                </div>
              </div>
            </div>
            <div className="card-body">
              <div className="table-container">
                <table className="table table-hover table-bordered table-sm">
                  <thead className="thead-dark">
                    <tr>
                      <th scope="col">#</th>
                      <th scope="col">Session</th>
                      <th scope="col">Date</th>
                      <th scope="col"> Attendance</th>
                    </tr>
                  </thead>
                  <tbody>
                    {attendance?.map((item, index) => (
                      <tr key={index}>
                        <th> {currentPage * itemsperpage + (index + 1)}</th>
                        <td className="py-2"> {item.topic}</td>
                        <td className="py-2"> {item.date}</td>
                        <td className="py-2"> {item.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              
                 
              </div>
              <div className="cornerbtn">
                <ul className="pagination">
                  <li
                    className={`page-item ${
                      currentPage === 0 ? "disabled" : ""
                    }`}
                    key="prev"
                  >
                    <a
                      className="page-link"
                      href="#"
                      aria-label="Previous"
                      onClick={() => handlePageChange(currentPage - 1)}
                    >
                      <span aria-hidden="true">«</span>
                      <span className="sr-only">Previous</span>
                    </a>
                  </li>
                  {renderPaginationButtons()}
                  <li
                    className={`page-item ${
                      currentPage === totalPages - 1 ? "disabled" : ""
                    }`}
                    key="next"
                  >
                    <a
                      className="page-link"
                      href="#"
                      aria-label="Next"
                      onClick={() => handlePageChange(currentPage + 1)}
                    >
                      <span aria-hidden="true">»</span>
                      <span className="sr-only">Next</span>
                    </a>
                  </li>
                </ul>
                <div>
                  <label className="text-primary">
                    ( {datacounts.start}- {datacounts.start + attendance.length - 1})
                    of {datacounts.total}
                  </label>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Attendance;
