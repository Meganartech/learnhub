import React, { useEffect, useState } from 'react';
import baseUrl from '../api/utils';
import axios from 'axios';
import message from "../images/message.png";
const Notification = ({ setisopen ,isopen,setcount ,handlemarkallasRead}) => {
  const [notifications, setnotifications] = useState([]);
  const token = sessionStorage.getItem('token');
  
  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await axios.get(`${baseUrl}/notifications`, {
          headers: {
            Authorization: token,
          },
        });

        if (response.status === 200) {
          const data = response.data;
          setnotifications(data);
    //       const notificationIds = notifications.map(notification => notification.notifyId);
    // const markread=  await axios.post(`${baseUrl}/MarkAllASRead`, notificationIds, {
    //     headers: {
    //       Authorization: token,
    //     },
        
    //   });
    //   if(markread.status===200){

    //     fetchUnreadCount();
    //   }
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
    // const fetchUnreadCount = async () => {
    //   try {
    //     const response = await axios.get(`${baseUrl}/unreadCount`, {
    //       headers: {
    //         Authorization: token,
    //       },
    //     });
  
    //     if (response.status === 200) {
    //       const data = response.data;
    //       setcount(data);
    //     }
    //   } catch (error) {
    //     console.error("Error fetching unread count:", error);
    //   }
    // };
  
    if (isopen) {
   
 
      fetchItems();
    }
  }, [setcount]);
  const handleclearAll=async ()=>{
    try{
      const response = await axios.get(`${baseUrl}/clearAll`, {
        headers: {
          Authorization: token,
        },
      });
      if(response.status===200){
        window.location.reload();
      }
  } catch (error) {
    console.error('Error fetching data:', error);
  }
  }
    
  
   

  const filteredNotifications = React.useMemo(() => {
    if (!notifications.length) return [];

    const groupedByDate = notifications.reduce((acc, notification) => {
      const formattedDate = new Date(notification.createdDate).toLocaleDateString('en-US'); // Format date for grouping
      acc[formattedDate] = acc[formattedDate] || [];
      acc[formattedDate].push(notification);
      return acc;
    }, {});

    // Convert grouped notifications to array and sort by date (descending)
    const sortedNotifications = Object.entries(groupedByDate)
      .map(([date, notificationsByDate]) => ({ date, notifications: notificationsByDate }))
      .sort((a, b) => new Date(b.date) - new Date(a.date));

    return sortedNotifications;
  }, [notifications]);

  return (
    <div
      id="notipanel"
      className="notificationPanel dropdown-menu-left shadow animated--grow-in"
    >
      <div className="headcontrols">
        <a href='#' onClick={handleclearAll} >Clear All</a>
        <a href='#'
        onClick={()=>{
  const notificationIds = notifications.map(notification => notification.notifyId); handlemarkallasRead(notificationIds)}}>Mark All As Read </a>
        <i className="fa-solid fa-xmark p-1 alignright" onClick={() => setisopen(false)}></i>
      </div>
      <div className="scrollclass pr-2">
        {filteredNotifications && filteredNotifications.length>0 ?(
       <> {filteredNotifications.map((group, index) => (
          <div key={index}>
            {group.date !== filteredNotifications[index - 1]?.date && (
                <h6 className="text-left text-muted">
                {group.date === new Date().toLocaleDateString('en-US') ? 'Today' : group.date ===  new Date(new Date().getTime() - (1 * 24 * 60 * 60 * 1000)).toLocaleDateString('en-US') ? 'Yesterday' : group.date}
              </h6>
            )}
            {group.notifications .reverse().map((notification, notificationIndex) => (
              <div
                key={notificationIndex}
                className="notificationElement dropdown-item"

                onClick={()=>{window.location.href=`${notification.link}`}}

              >
                 <img
                      src={`${notification.notimage ? `data:image/jpeg;base64,${notification.notimage}` : message}`}
                      alt="pic"
                      width="100px"
                      height="120px"
                    />
                    <div className='p-1' >
                    <p><b>{notification.heading}</b></p>
                    <p className='notificationcontent '>
                    {notification.description.length > 80
                      ? notification.description.substring(0, 80) + "..."
                      : notification.description}
                  </p>
                <small className="text-muted">Created By {notification.username}</small>
                {/* <small className="text-muted">Updated {new Date(notification.createdDate).toLocaleDateString('en-US')}</small> */}
                </div>
              </div>
            ))}
        
          </div>
        ))}
           </> ):<div className='text-center mt-5'> No Notification Found</div>}
      </div>
    </div>
  );
};

export default Notification;
