import React, { useEffect, useState } from 'react';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import baseUrl from '../api/utils';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
const localizer = momentLocalizer(moment);

const StudentCalenderView = () => {
    const MySwal = withReactContent(Swal);
  const [events, setEvents] = useState([]);
  const navigate = useNavigate();
  const token = sessionStorage.getItem('token');
  useEffect(() => {
    const fetchItems = async () => {
      try {
       
        const response = await axios.get(`${baseUrl}/api/zoom/getMyMeetings`, {
          headers: {
            'Authorization': token,
          },
        });
         
        const fetchedEvents = response.data.map((meeting) => {
          const startTime = new Date(meeting.startTime);
         
          const endTime = new Date(startTime.getTime() + meeting.duration * 60000);
      
          return {
            id: meeting.meetingId,
            start: startTime,
            end: endTime,
            joinUrl: meeting.joinUrl,
            title: meeting.topic || '',
          };
        });
        setEvents(fetchedEvents);
      } catch (error) {
        console.error(error);
        throw error
      }
    };

    fetchItems();
  }, []);
  const handlClickJoinUrl = async (event) => {
    try {
        const response =await axios.get(`${baseUrl}/api/zoom/Join/${event.id}`, {
          headers: {
            'Authorization': token,
          },
        });
  
       console.log(response.data)
       if (typeof response.data === 'string' && response.data.startsWith('http')) {
        window.open(response.data, '_blank');
    }
      
    } catch (error) {
      if(error.response && error.response.status===409){
        MySwal.fire({
          title: 'Meeting Not Started',
          text: 'This meeting was not Started Yet. Please try again later.!',
          icon: 'info',
      });
      }else{
      console.error(error);
      // Optionally, show an error message
      // MySwal.fire('Error!', 'An error occurred while deleting the meeting.', 'error');
      throw error
      }
    }
  };
  const eventStyleGetter = (event) => {
    if(event){
      return {
        style: {
          backgroundColor: 'blue',
          color: 'white',
        },
      };
    }
  };
  const handleEventClick = (event) => {
    if (event.joinUrl) {
      handlClickJoinUrl(event);
    } else {
        MySwal.fire({
            title: 'No Join URL Available',
            text: 'This event does not have a join URL associated with it.',
            icon: 'info',
        });
    }
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
        <Calendar
          events={events}
          localizer={localizer}
          startAccessor="start"
          endAccessor="end"
          style={{ height: 500 }}
          eventPropGetter={eventStyleGetter}
          onSelectEvent={handleEventClick}
        />
      </div>
      </div>
    </div>
    </div>
    </div>
  );
};


export default StudentCalenderView