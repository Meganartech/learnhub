import React from 'react';

const CustomEvent = ({ event, onDelete }) => {
  if (!event) {
    return null; // or some placeholder UI
  }
  const handleDeleteClick = (e) => {
    e.stopPropagation(); // Prevent the event click handler from triggering
    onDelete(event); // Call the onDelete function with the event data
  };

  return (
    <div  className='meet'>
      <span onClick={() => window.open(event.joinUrl, '_blank')} className='event-title'>{event.title}</span>
      
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
