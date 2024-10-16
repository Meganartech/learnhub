// ErrorBoundary.js
import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI
    return { hasError: true };
  }

  componentDidCatch(error, info) {
    // Log the error to an error reporting service, if needed
    console.error("Error captured by ErrorBoundary: ", error, info);
  }

  render() {
    if (this.state.hasError) {
      // Fallback UI when an error is caught
      return <div className='contentbackgroundforfull'>
        <div className='contentinnerforfull'>
       
        <div className="text-center mt-5 ">
              <h2 className="display-4">Oops! Something Went Wrong</h2>
              <p className="lead">Some Unexpected Error Occured Please Try again later.</p>
              <p>Go back To <a href="/course/admin/edit">Home</a></p>
          </div>
           
        </div></div>;
    }

    return this.props.children; 
  }
}

export default ErrorBoundary;
