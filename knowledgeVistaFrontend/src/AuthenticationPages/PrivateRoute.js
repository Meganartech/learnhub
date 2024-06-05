import React, { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom';
import baseUrl from '../api/utils';
import axios from 'axios';
const PrivateRoute = ({ authenticationRequired, authorizationRequired,onlyadmin,onlyuser, onlytrainer,children ,licence}) => {
    
  const [isvalid, setIsvalid] = useState();
    useEffect(() => {
        const fetchData = async () => {
          try {
            const response = await axios.get(`${baseUrl}/api/v2/GetAllUser`);
            
            const data = response.data;
            setIsvalid(data.valid);
          
           
        } catch (error) {
          if (error.response && error.response.status !== 200) {
            throw new Error('Network response was not ok');
          }
          console.error('Error fetching data:', error);
        }
      };
      fetchData();
      }, []);
     const isAuthenticated = sessionStorage.getItem('token') !== null;
     const userRole = sessionStorage.getItem('role'); 
   
    
    // Check if authentication is required and user is authenticated
    if (authenticationRequired && !isAuthenticated) {
        return <Navigate to="/login" />;
    }

    // Check if authorization is required and user has the ADMIN role
    if (authorizationRequired && userRole === "USER") {
        return <Navigate to="/unauthorized" />;
    }
    
     if(onlyadmin && (userRole ==="TRAINER" || userRole==="USER")){
       
            
    return <Navigate to="/unauthorized" />;
    }   
    

    if(onlytrainer && (userRole ==="ADMIN" || userRole==="USER") ){
         
    return <Navigate to="/unauthorized" />;
    }



    if(onlyuser && (userRole ==="ADMIN" || userRole==="TRAINER")){
        
        return <Navigate to="/unauthorized" />;

    } 



    if (userRole === "ADMIN") {
        if (isvalid===false) {
            if (licence) {
            return <>{children}</>; // Allow access with valid license
            } else {
            return <Navigate to="/about" />;
            }
    }
      } else if (userRole === "TRAINER") {
        if (isvalid===false) {
          return <Navigate to="/unauthorized" />;
        }
      }
   
    // If authentication and authorization checks pass, render the children
    return <>{children}</>;
 

}

export default PrivateRoute