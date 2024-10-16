import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // Updated import for navigation
import baseUrl from './api/utils';

const RedirectComponent = ({ vpsonly, admincount, children,sasonly }) => {
    const [activeProfile, setActiveProfile] = useState(sessionStorage.getItem("Activeprofile"));
    const [loading, setLoading] = useState(true);
    const [adminCount, setAdminCount] = useState(0); // State to hold the admin count
    const navigate = useNavigate(); // Use the useNavigate hook for navigation

    useEffect(() => {
        const fetchActive = async () => {
            try {
                const active = await axios.get(`${baseUrl}/Active/Environment`);
                sessionStorage.setItem("Activeprofile", active.data);
                setActiveProfile(active.data);
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        };

        // Only fetch if activeProfile is not already in sessionStorage
        if (!activeProfile) {
            fetchActive();
        } else {
            setLoading(false);  // If already available, stop loading
        }
    }, [activeProfile]);

    useEffect(() => {
        const countAdmin = async () => {
            try {
                const count = await axios.get(`${baseUrl}/count/admin`);
                setAdminCount(count.data); // Set the admin count in state
               
            } catch (error) {
                console.log(error);
            }
        };

        if (admincount) {
            countAdmin(); // Fetch admin count if required
        }
    }, [admincount]);

    // Handle loading state
    if (loading) {
        return <div className="outerspinner active">
        <div className="spinner"></div>
      </div>;
    }
   if(sasonly && activeProfile !=="SAS" ){
    navigate("/notfound");
   }
    // Check the admin count and navigate if needed

    if (admincount   && adminCount >0){
        navigate("/notfound");
        return null; // Prevent rendering children if navigation occurs
    }

    // Check for vpsonly condition
    if (vpsonly && activeProfile !== "VPS") {
        navigate("/login");
        return null; // Prevent rendering children if navigation occurs
    }

    return <>{children}</>;
};

export default RedirectComponent;
