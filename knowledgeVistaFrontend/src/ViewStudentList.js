import React from 'react'
import { useEffect } from 'react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
//style.css
const ViewStudentList = () => {
    const [users, setUsers] = useState([]);
    useEffect(() => {
        // Simulating fetching data from the server
        const fetchData = async () => {
          try {
            // Fetch data from server
            const response = await fetch("http://localhost:8080/view/users");
            const data = await response.json();
            setUsers(data);
          } catch (error) {
            console.error('Error fetching data:', error);
          }
        };
    
        fetchData();
      }, []);
  return (
    <div className='bg1'>
    <div className='tableview'>
      <div style={{ display: 'grid', gridTemplateColumns: '40fr 5fr' }} className='mb-4'>
        <h1>Student Details</h1>
        <a href="" className='btn btn-primary'><i class="fa-solid fa-plus"></i> Add Student</a>
      </div>
      <div className="table-container">
        <table className="table table-sm">
          <thead>
            <tr>
              <th scope="col">#</th>
              <th scope="col">Username</th>
              <th scope="col">Email</th>
              <th scope="col">Date of Birth</th>
              <th scope="col">Phone</th>
              <th scope="col">Role</th>
              <th colspan="3" scope="col">Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => (
              <tr key={user.userId}>
                <th scope="row">{index + 1}</th>
                <td className='py-2'>{user.username}</td>
                <td className='py-2'>{user.email}</td>
                <td className='py-2'>{user.dob}</td>
                <td className='py-2'>{user.phone}</td>
                <td className='py-2'>{user.role.roleName}</td>
                <td>
                <Link to={`/edit/${user.userId}`} className='hidebtn' >
                    <i className="fas fa-edit"></i>
                    </Link>
                </td>
                <td>
                <Link to={`/assignCourse/${user.userId}`} className='hidebtn' >
                    <i className="fas fa-plus"></i>
                    </Link>
                </td>
                <td>
                  <button className='hidebtn'>
                    <i className="fas fa-trash text-danger"></i>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  </div>
  
  )
}

export default ViewStudentList
