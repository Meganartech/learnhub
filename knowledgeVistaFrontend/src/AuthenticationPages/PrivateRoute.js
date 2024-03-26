import React from 'react'
import { Navigate } from 'react-router-dom';
const PrivateRoute = ({ authenticationRequired, authorizationRequired, children }) => {
  
    const isAuthenticated = sessionStorage.getItem('token') !== null;
    const userRole = sessionStorage.getItem('role'); // Assuming role is stored in sessionStorage

    // Check if authentication is required and user is authenticated
    if (authenticationRequired && !isAuthenticated) {
        return <Navigate to="/login" />;
    }

    // Check if authorization is required and user has the ADMIN role
    if (authorizationRequired && userRole === "USER") {
        return <Navigate to="/unauthorized" />;
    }

    // If authentication and authorization checks pass, render the children
    return <>{children}</>;
 

}

export default PrivateRoute
