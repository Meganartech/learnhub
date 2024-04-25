import React from 'react'
import { useEffect } from 'react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

import { toast } from 'react-toastify';
const ViewTrainerList = () => {
  const MySwal = withReactContent(Swal);
    const [users, setUsers] = useState([]);
    const token=sessionStorage.getItem("token");
    const userRole = sessionStorage.getItem('role');
    useEffect(() => {
        // Simulating fetching data from the server
        const fetchData = async () => {
          try {
            // Fetch data from server
            const response = await fetch("http://localhost:8080/view/Trainer");
            const data = await response.json();
            setUsers(data);
          } catch (error) {
            console.error('Error fetching data:', error);
          }
        };
    
        fetchData();
      }, []);

      const handledelete =async (userId,username,email)=>{
        const formData = new FormData();
        formData.append('email', email);
        MySwal.fire({
          title: "Delete ?",
          text: `Are you sure you want to delete trainer ${username}`,
          icon: "warning",
          showCancelButton: true,
          confirmButtonColor: "#d33",
          confirmButtonText: "Delete",
          cancelButtonText: "Cancel",
        }).then(async (result) => {
          if (result.isConfirmed) {
            try {
              if (userId != null) {
                const response = await fetch("http://localhost:8080/admin/delete/trainer", {
                  method: "DELETE",
                  headers: {
                    'Authorization': token
                  },
                  body: formData
                });
                if (response.ok) {
                  MySwal.fire({
                    title: "Deleted",
                    text: `Trainer ${username} deleted successfully`,
                    icon: "success",
                    confirmButtonText: "OK",
                }).then(() => {
                    window.location.reload();
                });
                

               
                   
                } else if (response.status === 404) {
                  MySwal.fire({
                    icon: 'error',
                    title: '404',
                    text: 'Trainer not found'
                });
                } else {
                  MySwal.fire({
                    icon: 'error',
                    title: 'ERROR',
                    text: 'Error deleting Trainer'
                });
                }
              } 
            } catch (error) {
              MySwal.fire({
                icon: 'error',
                title: 'ERROR',
                text: 'Error deleting Trainer'
            });
            }
          } 
        });
      };
    
  return (
    <div className='contentbackground'>
    <div className='contentinner'>
      <div style={{ display: 'grid', gridTemplateColumns: '40fr 5fr' }} className='mb-4'>
        <h1>Trainers Details</h1>
        <a href="/addTrainer" className='btn btn-primary'><i className="fa-solid fa-plus"></i> Add Trainer</a>
      </div>
      <div className="table-container">
        <table className="table table-hover table-bordered table-sm">
          <thead className='thead-dark'>
            <tr>
              <th scope="col">#</th>
              <th scope="col">Username</th>
              <th scope="col">Email</th>
              <th scope="col">Date of Birth</th>
              <th scope="col">Phone</th>
              <th scope="col"> Skills</th>
              <th scope="col">Role</th>

              <th colSpan="3" scope="col">Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => (
              <tr key={user.userId}>
                <th scope="row">{index + 1}</th>
                <td className='py-2'><Link to={`/view/Trainer/profile/${user.email}`}>{user.username}</Link></td>
                <td className='py-2'>{user.email}</td>
                <td className='py-2'>{user.dob}</td>
                <td className='py-2'>{user.phone}</td>
                
                <td className='py-2'>{user.skills}</td>
                <td className='py-2'>{user.role.roleName}</td>
                <td className='text-center'>
                <Link to={`/trainer/edit/${user.email}`} className='hidebtn' >
                    <i className="fas fa-edit"></i>
                    </Link>
                </td>
                {userRole==="ADMIN"?(
                <td className='text-center'>
                <Link to={`/assignCourse/Trainer/${user.userId}`} className='hidebtn' >
                    <i className="fas fa-plus"></i>
                    </Link>
                </td>):null}
                <td  className='text-center'>
                  <button className='hidebtn'  onClick={()=>handledelete(user.userId,user.username,user.email)}>
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

export default ViewTrainerList
