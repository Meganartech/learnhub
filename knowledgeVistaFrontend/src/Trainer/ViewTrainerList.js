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
          title: "Delete Test?",
          text: `Are you sure you want to delete Student ${username}`,
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
                  toast.success(`Trainer ${username} deleted successfully`, {
                    autoClose: 3000, // Close the toast after 3 seconds
                    onClose: () => {
                      // After the toast is closed, reload the page
                      window.location.reload();
                    }
                  });
                } else if (response.status === 404) {
                  // Display error toast notification for 404 Not Found
                  toast.error('Error: Trainer not found');
                } else {
                  // Display generic error toast notification for other errors
                  toast.error('Error deleting Trainer');
                }
              } else {toast.error('Error deleting Trainer');
              
              }
            } catch (error) {
              toast.error('Error deleting Trainer');
            }
          } 
        });
      };
    
  return (
    <div className='contentbackground'>
    <div className='contentinner'>
      <div style={{ display: 'grid', gridTemplateColumns: '40fr 5fr' }} className='mb-4'>
        <h1>Trainers Details</h1>
        <a href="/addTrainer" className='btn btn-primary'><i class="fa-solid fa-plus"></i> Add Trainer</a>
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

              <th colspan="3" scope="col">Action</th>
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
                <td>
                <Link to={`/edit/${user.userId}`} className='hidebtn' >
                    <i className="fas fa-edit"></i>
                    </Link>
                </td>
                {userRole==="ADMIN"?(
                <td className='text-center'>
                <Link to={`/assignCourse/Trainer/${user.userId}`} className='hidebtn' >
                    <i className="fas fa-plus"></i>
                    </Link>
                </td>):null}
                <td>
                  <button className='hidebtn' onClick={()=>handledelete(user.userId,user.username,user.email)}>
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
