import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import "./errorhandling"
import App from "./App";
import { GlobalStateProvider } from "./Context/GlobalStateProvider";
import ErrorBoundary from "./ErrorBoundary"; // Import the ErrorBoundary

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <GlobalStateProvider>
      <ErrorBoundary>  
        <App />
      </ErrorBoundary>
    </GlobalStateProvider>
  </React.StrictMode>
);
