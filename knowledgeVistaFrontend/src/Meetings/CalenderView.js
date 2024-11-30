
import React, { useEffect, useState } from 'react';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment, { months } from 'moment';
import baseUrl from '../api/utils';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import CustomEvent from './CustomeEvent'; // Import the custom event component
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { event } from 'jquery';
const localizer = momentLocalizer(moment);

const CalenderView = () => {
  
  const MySwal = withReactContent(Swal);
  const [events, setEvents] = useState([]);
  const navigate = useNavigate();
  const token = sessionStorage.getItem('token');

  
  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await axios.get(`${baseUrl}/api/zoom/getMyMeetings`, {
          headers: {
            Authorization: token,
          },
        });
  
        if (response.data) {
          const fetchedEvents = response.data
            .map((meeting) => {
              if (!meeting.startTime) {
                return null; // Skip invalid meetings
              }
  
              const utcStartTime = new Date(meeting.startTime);
              if (isNaN(utcStartTime)) {
                return null; // Skip invalid meetings
              }
  
              // Convert UTC start time to local time
              const localStartTime = new Date(utcStartTime.getTime());
  
              // Add duration to start time to get end time
              const durationInMinutes = Number(meeting.duration) || 0;
              const localEndTime = new Date(localStartTime.getTime() + durationInMinutes * 60000);
  
              return {
                id: meeting.meetingId,
                start: localStartTime.toISOString(),
                end: localEndTime.toISOString(),
                joinUrl: meeting.joinUrl,
                title: meeting.topic || "",
              };
            })
            .filter(Boolean); // Remove null values from invalid meetings
  
          setEvents(fetchedEvents);
        }
      } catch (error) {
        console.error(error);
        throw error
      }
    };
  
    fetchItems();
  }, []);

  const [meetingId, setMeetingId] = useState('');

  // Handle input change
  const handleChange = (e) => {
    setMeetingId(e.target.value);
  };

  // Handle form submission
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault(); 
      if (meetingId) {
        const joinUrl = `https://zoom.us/j/${meetingId}`;
        window.open(joinUrl, '_blank');
      }
    }
  };

  
  const handleDeleteEvent = async (event) => {
    try {
      const result = await MySwal.fire({
        title: "Delete Meeting?",
        text: `Are you sure you want to delete this Meeting ${event.title}?`,
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        confirmButtonText: "Delete",
        cancelButtonText: "Cancel",
      });
  
      if (result.isConfirmed) {
        await axios.delete(`${baseUrl}/api/zoom/delete/${event.id}`, {
          headers: {
            'Authorization': token,
          },
        });
  
        // Remove the deleted event from the state
        setEvents(events.filter(e => e.id !== event.id));
        
        MySwal.fire({
          title: "Deleted!",
          text: " Meeting has been deleted.",
          icon: "success",
        })
      }
      
    } catch (error) {
      console.error(error);
      // Optionally, show an error message
      // MySwal.fire('Error!', 'An error occurred while deleting the meeting.', 'error');
      throw error
    }
  };
  

  const eventStyleGetter = (event) => {
    // Define an array of colors
    const colors = ['#D5C1FF', '#D7F1BD', '#FFC1EE', '#FFDDC1'];
  
    const index = event.id % colors.length; 
  
    return {
      style: {
        backgroundColor: colors[index], // Use the color from the array
        color: 'black',
        height: '30px', // Set the height of the event
        
      }
    };
  };
  

  return (
    <div>
    <div className="page-header"></div>
    <div className='row'>
      <div className='col-sm-12'>
        <div className='card'>
          <div className='card-header'>
        <div className="navigateheaders ">
          <div onClick={() => navigate(-1)}>
            <i className="fa-solid fa-arrow-left"></i>
          </div>
          <div></div>
          <div onClick={() => navigate("/dashboard/course")}>
            <i className="fa-solid fa-xmark"></i>
          </div>
        </div>
        </div>
        <div className='card-body'>
        <div className='calenderheadergrp'>
          <h4>Calendar</h4>
          <input
        type="text"
        className="form-control tabinp"
        placeholder="Join With Id"
        value={meetingId}
        onChange={handleChange}
        onKeyPress={handleKeyPress}
      />
          <button onClick={()=>{window.location.href="/meeting/Shedule"}} className='btn btn-primary'>New Meet</button>
        </div>
        <Calendar
          events={events}
          localizer={localizer}
          startAccessor="start"
          endAccessor="end"
          style={{ height: 500 }}
          components={{
            event: (props) => <CustomEvent {...props} onDelete={handleDeleteEvent} />
          }}
          eventPropGetter={eventStyleGetter}
        />
        </div>
      </div>
      </div>
      </div>
    </div>
  );
};

export default CalenderView;