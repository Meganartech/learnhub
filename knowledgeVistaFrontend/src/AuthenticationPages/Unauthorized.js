import React from 'react';


const Unauthorized = () => {
    return (
       
      <div className="text-center mt-5">
      <h1 className="display-1 text-danger">401</h1>
      <h2 className="display-4">Oops! It seems you are not authorized to access this page.</h2>
      <p className="lead">Please contact the administrator for assistance..</p>
      <p>Go back To <a href="/dashboard/course">Home</a></p>
  </div>
    );
};

export default Unauthorized;
