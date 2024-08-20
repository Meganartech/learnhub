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
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <span onClick={() => window.open(event.joinUrl, '_blank')}>{event.title}</span>
      
      <div>
      <i className="fa-solid fa-pencil" onClick={()=> window.location.href=`/meet/edit/${event.id}`}></i>
        <i 
        className="fa-solid fa-trash" 
        style={{ cursor: 'pointer', marginLeft: '10px' }} 
        onClick={handleDeleteClick}
      ></i>
      
      </div>
    </div>
  );
};

export default CustomEvent;
