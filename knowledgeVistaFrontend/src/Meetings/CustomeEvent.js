import axios from 'axios';
import React from 'react';
import baseUrl from '../api/utils';

const CustomEvent = ({ event, onDelete }) => {
  if (!event) {
    return null; // or some placeholder UI
  }
  const token=sessionStorage.getItem("token");
  const handlClickJoinUrl = async (event) => {
    try {
      console.log("hii in handlClickJoinUrl",event)
        const response =await axios.get(`${baseUrl}/api/zoom/Join/${event.id}`, {
          headers: {
            'Authorization': token,
          },
        });
  
       console.log(response.data);
       if (typeof response.data === 'string' && response.data.startsWith('http')) {
        window.open(response.data, '_blank');
    }
      
    } catch (error) {
      console.error(error);
      // Optionally, show an error message
      // MySwal.fire('Error!', 'An error occurred while deleting the meeting.', 'error');
      throw error
    }
  };
  const handleDeleteClick = (e) => {
    e.stopPropagation(); // Prevent the event click handler from triggering
    onDelete(event); // Call the onDelete function with the event data
  };

  return (
    <div  className='meet'>
      <span onClick={()=>{handlClickJoinUrl(event)}} className='event-title'>{event.title}</span>
      
      <div>
      <i className="fa-solid fa-pencil" title= {`edit ${event.title}`} onClick={()=> window.location.href=`/meet/edit/${event.id}`}></i>
        <i 
        className="fa-solid fa-trash" 
        title= {`delete ${event.title}`}
        style={{ cursor: 'pointer', marginLeft: '10px' }} 
        onClick={handleDeleteClick}
      ></i>
      
      </div>
    </div>
  );
};

export default CustomEvent;
