import React from 'react'
import { useEffect } from 'react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../api/utils';
import axios from 'axios';
const ViewAdmin = () => {
  const MySwal = withReactContent(Swal);
    const [users, setUsers] = useState([]);
    const token=sessionStorage.getItem("token");
    const userRole = sessionStorage.getItem('role');

    const [filterOption, setFilterOption] = useState("All");
    const [searchQuery, setSearchQuery] = useState('');
   
  const filterData = () => {
    if (filterOption === "All") {
      return users;
    } else if (filterOption === "Active") {
      return users.filter(user => user.isActive === true);
    } else if (filterOption === "Inactive") {
      return users.filter(user => user.isActive === false);
    }else if(filterOption==="search"){
      return users.filter(user => user.email && user.email.toLowerCase().includes(searchQuery.toLowerCase()));
    }
  };
    useEffect(() => {
        // Simulating fetching data from the server
        const fetchData = async () => {
          try {
            // Fetch data from server
            const response = await axios.get(`${baseUrl}/ViewAll/Admins`,{
              headers:{
                Authorization:token
              }
            });
            const data = response.data;
            setUsers(data);
          } catch (error) {
            if(error.response && error.response.status===401){
              window.location.href="/unauthorized"
            }
            console.error('Error fetching data:', error);
          }
        };
    
        fetchData();
      }, []);
      // const handleDelete=async(username,email)=>{
      //   MySwal.fire({
      //       title: "Delete ADMIN ?",
      //       text: `Are you sure you want to Delete ADMIN ${username}`,
      //       icon: "warning",
      //       showCancelButton: true,
      //       confirmButtonColor: "#d33",
      //       confirmButtonText: "Delete",
      //       cancelButtonText: "Cancel",
      //     }).then(async (result) => {
      //       if (result.isConfirmed) {
      //         try {
      //           if (email != null) {
      //             const response = await axios.delete(`${baseUrl}/secret/Delete/Admin/${email}`,{
                  
      //               headers: {
      //                 'Authorization': token
      //               }
      //             });
      //             if (response.status===200) {
      //               MySwal.fire({
      //                 title: "Deleted",
      //                 text: `ADMIN ${username} Deleted successfully`,
      //                 icon: "success",
      //                 confirmButtonText: "OK",
      //             }).then(() => {
      //                 window.location.reload();
      //             });
      //           }
      //         }
                
      //         } catch (error) {
      //           if(error.response && error.response.status===404){
      //             MySwal.fire({
      //               icon: 'error',
      //               title: '404',
      //               text: 'ADMIN not found'
      //           });
      //           }else{
      //           MySwal.fire({
      //             icon: 'error',
      //             title: 'ERROR',
      //             text: 'Error Deleting ADMIN'
      //         });
      //         }
      //       }
      //       } 
      //     });
      // };

      const handleDeactivate = async (userId, username, email) => {
        const formData = new FormData();
      
        MySwal.fire({
          title: "De Activate ?",
          text: `Are you sure you want to DeActivate Admin ${username}`,
          icon: "warning",
          showCancelButton: true,
          confirmButtonColor: "#d33",
          confirmButtonText: "De Activate",
          cancelButtonText: "Cancel",
          input: 'text',
          inputAttributes: {
            autocapitalize: 'off',
            placeholder: 'Enter reason for deactivation (required)', // Update placeholder to indicate requirement
            required: true // Add required attribute
          },
          preConfirm: (reason) => { // Handle user input before confirmation
            return new Promise((resolve, reject) => {
              if (reason === "") {
                Swal.showInputError('Please enter a reason for deactivation.');
                reject(); // Reject confirmation if reason is empty
              } else {
                formData.append('email', email);
                formData.append('reason', reason);
                resolve(); // Allow confirmation if reason is provided
              }
            });
          }
        }).then(async (result) => {
          if (result.isConfirmed) {
            try {
              console.log("id", userId);
              if (userId != null) {
                const response = await axios.delete(`${baseUrl}/deactivate/admin`, {
                  data: formData,
                  headers: {
                    'Authorization': token
                  }
                });
      
                if (response.status === 200) {
                  MySwal.fire({
                    title: "De Activated",
                    text: `Admin ${username} De Activated successfully`,
                    icon: "success",
                    confirmButtonColor: "#3085d6",
                    confirmButtonButtonText: "OK"
                  }).then(() => {
                    window.location.reload();
                  });
                } else {
                  // Handle other backend errors (e.g., 400 Bad Request)
                  MySwal.fire('Error', `Deactivation failed with status ${response.status}`, 'error');
                }
              } else {
                // Handle case where userId is null (if applicable)
                MySwal.fire('Error', 'Invalid user ID', 'error');
              }
            } catch (error) {
              if (error.response && error.response.status === 404) {
                MySwal.fire({
                  icon: 'error',
                  title: '404',
                  text: 'Admin not found'
                });
              } else {
                MySwal.fire({
                  icon: 'error',
                  title: 'ERROR',
                  text: 'Error Deactivating Admin'
                });
                console.error('Error during deactivation:', error); // Log detailed error for debugging
              }
            }
          }
        });
      };
      const handleActivate =async (userId,username,email)=>{
        const formData = new FormData();
        formData.append('email', email);
        MySwal.fire({
          title: " Activate ?",
          text: `Are you sure you want to Activate Admin ${username}`,
          icon: "warning",
          showCancelButton: true,
          confirmButtonColor: "#d33",
          confirmButtonText: "Activate",
          cancelButtonText: "Cancel",
        }).then(async (result) => {
          if (result.isConfirmed) {
            try {
              console.log("id",userId);
              if (userId != null) {
                const response = await axios.delete(`${baseUrl}/activate/admin`,{
                 data:formData,
                  headers: {
                    'Authorization': token
                  }
                });
                if (response.status===200) {
                  MySwal.fire({
                    title: "Activated",
                    text: `Admin ${username}  Activated successfully`,
                    icon: "success",
                    confirmButtonText: "OK",
                }).then(() => {
                    window.location.reload();
                });
              }
            }
              
            } catch (error) {
              if(error.response && error.response.status===404){
                MySwal.fire({
                  icon: 'error',
                  title: '404',
                  text: 'Admin not found'
              });
              }else{
              MySwal.fire({
                icon: 'error',
                title: 'ERROR',
                text: 'Error  Activated Admin'
            });
            }
          }
          } 
        });
      };
      
 
    
  return (
    <div className='contentbackground'>
    <div className='contentinner'>
    <div className="tableheader2">
      <h1>Admin Details</h1>
      <div style={{display:'grid',gridTemplateColumns:"10fr 6fr "}}>  
        <input
        className="form-control tabinp"
        type="search"
        placeholder="search by Email"
        aria-label="Search"
        value={searchQuery}
        onChange={(e) => {
          setSearchQuery(e.target.value);
          setFilterOption("search");
        }}      
      />
        <select
            className="selectstyle btn btn-success text-left "
            value={filterOption}
            onChange={(e) => setFilterOption(e.target.value)}
         >
           <option  className='bg-light text-dark ' value="All">All</option>
           <option className='bg-light text-dark' value="Active">Active</option>
           <option className='bg-light text-dark' value="Inactive">Inactive</option>
         </select>
        </div>
        </div>
      <div className="table-container">
        <table className="table table-hover table-bordered table-sm">
          <thead className='thead-dark'>
            <tr>
              <th scope="col">#</th>
              <th scope="col">Username</th>
              <th scope="col">Email</th>
              <th scope='col'>Instituition Name</th>
              <th scope="col">Date of Birth</th>
              <th scope="col">Phone</th>
              <th scope="col"> Skills</th>
              <th scope="col">Status</th>

              <th scope="col">Action</th>
            </tr>
          </thead>
          <tbody>
          {filterData().map((user, index) => (
              <tr key={user.userId}>
                <th scope="row">{index + 1}</th>
                <td className='py-2'>{user.username}</td>
                <td className='py-2'>{user.email}</td>
                <td className='py-2'>{user.institutionName}</td>
                <td className='py-2'>{user.dob}</td>
                <td className='py-2'>{user.phone}</td>
                <td className='py-2'>{user.skills}</td>
                <td className='py-2' >{user.isActive===true? <div className='Activeuser'><i className="fa-solid fa-circle pr-3"></i>Active</div>:<div className='InActiveuser' ><i className="fa-solid fa-circle pr-3"></i>In Active</div>}</td>
                {/* <td className='text-center'>
              <button to={`/trainer/edit/${user.email}`} className='hidebtn' >
                    <i className="fas fa-edit"></i>
                    </button> 
                     <button className='hidebtn icontrash' onClick={()=>handleDelete(user.username,user.email)}><i className="fas fa-trash"></i></button> 
                </td> */}
               
                <td  className='text-center'>
                {user.isActive===true?
                  <button className='hidebtn ' onClick={()=>handleDeactivate(user.userId,user.username,user.email)}>
                  <i className="fa-solid fa-lock"></i>
                  </button>:
                  <button  className='hidebtn ' onClick={()=>handleActivate(user.userId,user.username,user.email)}>
                  <i className="fa-solid fa-lock-open"></i>
                  </button>}
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

export default ViewAdmin
