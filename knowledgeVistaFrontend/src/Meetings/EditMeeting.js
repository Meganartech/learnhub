import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import axios from "axios";
import baseUrl from "../api/utils";
import moment from "moment-timezone";

const EditMeeting = () => {
  const { meetingId } = useParams();
  const navigate = useNavigate();
  const MySwal = withReactContent(Swal);
  const today = new Date();
  const formattedDate = today.toISOString().split("T")[0];
  const token = sessionStorage.getItem("token");
  const Productversion = sessionStorage.getItem("LicenceVersion");
  const [issubmitting, setissubmitting] = useState(false);
  const timezones = [
    { name: "Pacific/Midway", id: "Pacific/Midway" },
    { name: "Samoa", id: "Samoa" },
    { name: "Pacific/Pago_Pago", id: "Pacific/Pago_Pago" },
    { name: "Pacific/Honolulu", id: "Pacific/Honolulu" },
    { name: "America/Anchorage", id: "America/Anchorage" },
    { name: "America/Vancouver", id: "America/Vancouver" },
    { name: "America/Los_Angeles", id: "America/Los_Angeles" },
    { name: "America/Tijuana", id: "America/Tijuana" },
    { name: "America/Edmonton", id: "America/Edmonton" },
    { name: "America/Denver", id: "America/Denver" },
    { name: "America/Phoenix", id: "America/Phoenix" },
    { name: "America/Mazatlan", id: "America/Mazatlan" },
    { name: "America/Winnipeg", id: "America/Winnipeg" },
    { name: "America/Regina", id: "America/Regina" },
    { name: "America/Chicago", id: "America/Chicago" },
    { name: "America/Mexico_City", id: "America/Mexico_City" },
    { name: "America/Guatemala", id: "America/Guatemala" },
    { name: "America/El_Salvador", id: "America/El_Salvador" },
    { name: "America/Managua", id: "America/Managua" },
    { name: "America/Costa_Rica", id: "America/Costa_Rica" },
    { name: "America/Montreal", id: "America/Montreal" },
    { name: "America/New_York", id: "America/New_York" },
    { name: "America/Indianapolis", id: "America/Indianapolis" },
    { name: "America/Panama", id: "America/Panama" },
    { name: "America/Bogota", id: "America/Bogota" },
    { name: "America/Lima", id: "America/Lima" },
    { name: "America/Halifax", id: "America/Halifax" },
    { name: "America/Puerto_Rico", id: "America/Puerto_Rico" },
    { name: "America/Caracas", id: "America/Caracas" },
    { name: "America/Santiago", id: "America/Santiago" },
    { name: "America/St_Johns", id: "America/St_Johns" },
    { name: "America/Montevideo", id: "America/Montevideo" },
    { name: "America/Araguaina", id: "America/Araguaina" },
    {
      name: "America/Argentina/Buenos_Aires",
      id: "America/Argentina/Buenos_Aires",
    },
    { name: "America/Godthab", id: "America/Godthab" },
    { name: "America/Sao_Paulo", id: "America/Sao_Paulo" },
    { name: "Atlantic/Azores", id: "Atlantic/Azores" },
    { name: "Canada/Atlantic", id: "Canada/Atlantic" },
    { name: "Atlantic/Cape_Verde", id: "Atlantic/Cape_Verde" },
    { name: "UTC", id: "UTC" },
    { name: "Etc/Greenwich", id: "Etc/Greenwich" },
    { name: "Europe/Belgrade", id: "Europe/Belgrade" },
    { name: "Bratislava", id: "Europe/Bratislava" },
    { name: "Ljubljana", id: "Europe/Ljubljana" },
    { name: "CET", id: "CET" },
    { name: "Sarajevo", id: "Europe/Sarajevo" },
    { name: "Skopje", id: "Europe/Skopje" },
    { name: "Zagreb", id: "Europe/Zagreb" },
    { name: "Atlantic/Reykjavik", id: "Atlantic/Reykjavik" },
    { name: "Europe/Dublin", id: "Europe/Dublin" },
    { name: "Europe/London", id: "Europe/London" },
    { name: "Europe/Lisbon", id: "Europe/Lisbon" },
    { name: "Africa/Casablanca", id: "Africa/Casablanca" },
    { name: "Africa/Nouakchott", id: "Africa/Nouakchott" },
    { name: "Europe/Oslo", id: "Europe/Oslo" },
    { name: "Europe/Copenhagen", id: "Europe/Copenhagen" },
    { name: "Europe/Brussels", id: "Europe/Brussels" },
    { name: "Europe/Berlin", id: "Europe/Berlin" },
    { name: "Amsterdam", id: "Europe/Amsterdam" },
    { name: "Rome", id: "Europe/Rome" },
    { name: "Stockholm", id: "Europe/Stockholm" },
    { name: "Vienna", id: "Europe/Vienna" },
    { name: "Europe/Luxembourg", id: "Europe/Luxembourg" },
    { name: "Europe/Paris", id: "Europe/Paris" },
    { name: "Europe/Zurich", id: "Europe/Zurich" },
    { name: "Europe/Madrid", id: "Europe/Madrid" },
    { name: "Africa/Bangui", id: "Africa/Bangui" },
    { name: "Africa/Algiers", id: "Africa/Algiers" },
    { name: "Africa/Tunis", id: "Africa/Tunis" },
    { name: "Africa/Harare", id: "Africa/Harare" },
    { name: "Africa/Nairobi", id: "Africa/Nairobi" },
    { name: "Europe/Warsaw", id: "Europe/Warsaw" },
    { name: "Europe/Prague", id: "Europe/Prague" },
    { name: "Europe/Budapest", id: "Europe/Budapest" },
    { name: "Europe/Sofia", id: "Europe/Sofia" },
    { name: "Europe/Istanbul", id: "Europe/Istanbul" },
    { name: "Europe/Athens", id: "Europe/Athens" },
    { name: "Europe/Bucharest", id: "Europe/Bucharest" },
    { name: "Asia/Nicosia", id: "Asia/Nicosia" },
    { name: "Asia/Beirut", id: "Asia/Beirut" },
    { name: "Asia/Damascus", id: "Asia/Damascus" },
    { name: "Asia/Jerusalem", id: "Asia/Jerusalem" },
    { name: "Asia/Amman", id: "Asia/Amman" },
    { name: "Africa/Tripoli", id: "Africa/Tripoli" },
    { name: "Africa/Cairo", id: "Africa/Cairo" },
    { name: "Africa/Johannesburg", id: "Africa/Johannesburg" },
    { name: "Europe/Moscow", id: "Europe/Moscow" },
    { name: "Asia/Baghdad", id: "Asia/Baghdad" },
    { name: "Asia/Kuwait", id: "Asia/Kuwait" },
    { name: "Asia/Riyadh", id: "Asia/Riyadh" },
    { name: "Asia/Bahrain", id: "Asia/Bahrain" },
    { name: "Qatar", id: "Asia/Qatar" },
    { name: "Asia/Aden", id: "Asia/Aden" },
    { name: "Asia/Tehran", id: "Asia/Tehran" },
    { name: "Africa/Khartoum", id: "Africa/Khartoum" },
    { name: "Africa/Djibouti", id: "Africa/Djibouti" },
    { name: "Africa/Mogadishu", id: "Africa/Mogadishu" },
    { name: "Asia/Dubai", id: "Asia/Dubai" },
    { name: "Asia/Muscat", id: "Asia/Muscat" },
    { name: "Asia/Baku", id: "Asia/Baku" },
    { name: "Asia/Kabul", id: "Asia/Kabul" },
    { name: "Asia/Yekaterinburg", id: "Asia/Yekaterinburg" },
    { name: "Asia/Tashkent", id: "Asia/Tashkent" },
    { name: "Asia/Calcutta", id: "Asia/Calcutta" },
    { name: "Asia/Kathmandu", id: "Asia/Kathmandu" },
    { name: "Asia/Novosibirsk", id: "Asia/Novosibirsk" },
    { name: "Asia/Almaty", id: "Asia/Almaty" },
    { name: "Asia/Dacca", id: "Asia/Dacca" },
    { name: "Asia/Krasnoyarsk", id: "Asia/Krasnoyarsk" },
    { name: "Asia/Bangkok", id: "Asia/Bangkok" },
    { name: "Asia/Saigon", id: "Asia/Saigon" },
    { name: "Asia/Jakarta", id: "Asia/Jakarta" },
    { name: "Asia/Irkutsk", id: "Asia/Irkutsk" },
    { name: "Asia/Shanghai", id: "Asia/Shanghai" },
    { name: "Asia/Hong_Kong", id: "Asia/Hong_Kong" },
    { name: "Asia/Taipei", id: "Asia/Taipei" },
    { name: "Asia/Kuala_Lumpur", id: "Asia/Kuala_Lumpur" },
    { name: "Asia/Singapore", id: "Asia/Singapore" },
    { name: "Australia/Perth", id: "Australia/Perth" },
    { name: "Asia/Yakutsk", id: "Asia/Yakutsk" },
    { name: "Asia/Seoul", id: "Asia/Seoul" },
    { name: "Asia/Tokyo", id: "Asia/Tokyo" },
    { name: "Australia/Darwin", id: "Australia/Darwin" },
    { name: "Australia/Adelaide", id: "Australia/Adelaide" },
    { name: "Asia/Vladivostok", id: "Asia/Vladivostok" },
    { name: "Pacific/Port_Moresby", id: "Pacific/Port_Moresby" },
    { name: "Australia/Brisbane", id: "Australia/Brisbane" },
    { name: "Australia/Sydney", id: "Australia/Sydney" },
    { name: "Australia/Hobart", id: "Australia/Hobart" },
    { name: "Asia/Magadan", id: "Asia/Magadan" },
    { name: "Pacific/Noumea", id: "Pacific/Noumea" },
    { name: "Asia/Kamchatka", id: "Asia/Kamchatka" },
    { name: "Pacific/Fiji", id: "Pacific/Fiji" },
    { name: "Pacific/Auckland", id: "Pacific/Auckland" },
    { name: "Asia/Kolkata", id: "Asia/Kolkata" },
    { name: "Europe/Kiev", id: "Europe/Kiev" },
    { name: "America/Tegucigalpa", id: "America/Tegucigalpa" },
    { name: "Pacific/Apia", id: "Pacific/Apia" },
  ];

  const [searchQuery, setSearchQuery] = useState("");
  const [users, setUsers] = useState([]);
  const [selectedEmails, setSelectedEmails] = useState([]);
  const [formData, setFormData] = useState({
    date: "",
    time: "",
    ampm: "",
    hours:"",
    minutes:"",
  });

  const [zoomrequest, setzoomrequest] = useState({
    agenda: "",
    duration: "",
    settings: {
      audio: "",
      autoRecording: "",
      emailNotification: true,
      hostVideo: false,
      joinBeforeHost: true,
      meetingInvitees: [
        
      ],
      muteUponEntry: true,
      participantVideo: false,
      pushChangeToCalendar: true,
    },
    startTime: "",
    timezone: "",
    topic: "",
    type: 2,
  });
  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await axios.get(
          `${baseUrl}/api/zoom/get/meet/${meetingId}`,
          {
            headers: {
              Authorization: token,
            },
          }
        );
        if (response.data) {
          setzoomrequest(response.data);
          console.log(response.data)
          const startTime = response.data.startingTime; // UTC start time
          const starttimefordate=response.data.startTime;
          const localdate=new Date(starttimefordate);
          
          const localStartTime = new Date(startTime);

  // Format the local date
  const formattedDate = localStartTime.toLocaleDateString('en-CA'); // 'en-CA' gives you the "YYYY-MM-DD" format

          const time = localStartTime.toLocaleTimeString('en-US', {
            hour: '2-digit',
            minute: '2-digit',
            hour12: true,
          }).slice(0, 5); // Get "hh:mm" format
          
          // Determine AM/PM
          const ampm = localStartTime.getHours() >= 12 ? 'PM' : 'AM';
          
          // Calculate hours and minutes for the duration
          let hours = 0;
          let minutes = 0;
          if (zoomrequest.duration) {
            hours = Math.floor(zoomrequest.duration / 60);
            minutes = zoomrequest.duration % 60;
          }
          
          // Populate form data
          setFormData({
            ...formData,
            date: formattedDate,
            time: time,
            ampm: ampm,
            hours: hours,
            minutes: minutes,
          });
          
          setSelectedEmails([...response.data.settings.meetingInvitees]);
          
          console.log("12-hour format:", time);
          getupdatedstartime(formattedDate,time,ampm)
          
          
        }
      } catch (error) {
        console.error(error);
        throw error
      }
    };

    fetchItems()
  }, []);

  const handleAutoRecordingChange = (event) => {
    const val = event.target.checked ? "local" : "none";
    console.log("autorec", zoomrequest.settings.autoRecording);
    setzoomrequest((prevZoomRequest) => ({
      ...prevZoomRequest,
      settings: {
        ...prevZoomRequest.settings,
        autoRecording: val,
      },
    }));
  };
  const handleOptionsChange = (event) => {
    const { name, checked } = event.target;
    setzoomrequest((prevZoomRequest) => ({
      ...prevZoomRequest,
      settings: {
        ...prevZoomRequest.settings,
        [name]: checked,
      },
    }));
  };

  const handleEmailClick = (email) => {
    // Create a new object with the "email" property
    const newInvitee = { email: email };

    setSelectedEmails((prev) => {
      // Check if the email already exists in the selectedEmails array
      const existingEmailIndex = prev.findIndex((e) => e.email === email);

      let updatedEmails;

      // If email exists, remove it from the list
      if (existingEmailIndex !== -1) {
        updatedEmails = prev.filter((e, index) => index !== existingEmailIndex);
      } else {
        // If email doesn't exist, add the new object to the list
        updatedEmails = [...prev, newInvitee];
      }

      // Update zoomrequest.settings.meetingInvitees efficiently
      setzoomrequest((prevZoomRequest) => ({
        ...prevZoomRequest,
        settings: {
          ...prevZoomRequest.settings,
          meetingInvitees: updatedEmails,
        },
      }));

      // Return the updated emails array
      return updatedEmails;
    });
    setSearchQuery("");
    setUsers("");
    // Optional: Log for debugging or analytics
    console.log("Selected emails:", selectedEmails);
    console.log("Updated zoom request:", zoomrequest);
  };

  const handleSearch = async (query) => {
    setSearchQuery(query);
    if (query.length > 1) {
      const token = sessionStorage.getItem("token"); // Adjust based on where you store the token
      const role = sessionStorage.getItem("role"); // Retrieve the role from session storage

      let url;
      
      if (role === "TRAINER") {
        url = `${baseUrl}/search/usersbyTrainer`;
      } else if (role === "ADMIN") {
        url = `${baseUrl}/search/users`;
      } else {
        console.error("Role is not defined or not recognized");
        return;
      }
      try {
        const response = await axios.get(url, {
          headers: {
            Authorization: token,
          },
          params: {
            query,
          },
        });
        const selectedEmailsSet = new Set(
          selectedEmails.map((emailObj) => emailObj.email)
        );
        const filteredUsers = response.data.filter(
          (user) => !selectedEmailsSet.has(user)
        );

        setUsers(filteredUsers);
      } catch (error) {
        console.error("Error fetching users:", error);
        throw error
      }
    } else {
      setUsers([]);
    }
  };
  
  const generateTimeOptions = () => {
    const times = [];
    const start = moment().startOf("day"); // Start at 00:00
    for (let i = 0; i < 24 * 2; i++) {
      // 24 hours with 30 minutes intervals
      times.push(start.format("hh:mm")); // Format as "h:mm" for 12-hour format without AM/PM
      start.add(30, "minutes");
    }
    return times;
  };
  
  const originalList = generateTimeOptions();
  const timeOptions = [...new Set(originalList)]; // Remove any duplicates, if necessary

  const getupdatedstartime = (date,time,ampm) => {
    
  
    const timezone = zoomrequest.timezone;
  
    // Convert 12-hour time to 24-hour time
    let [hours, minutes] = time.split(":").map(Number);
    if (ampm === "PM" && hours < 12) {
      hours += 12;
    } else if (ampm === "AM" && hours === 12) {
      hours = 0;
    }
  
    // Combine date and time into a single string
    const dateTimeString = `${date} ${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
  
    // Convert to the specified timezone and format without 'Z'
    const formattedStartTime = moment.tz(dateTimeString, timezone).format("YYYY-MM-DDTHH:mm:ss");
  
    // Update zoomrequest state with the newly calculated startTime
    setzoomrequest((prevRequest) => ({
      ...prevRequest,
      startTime: formattedStartTime,
    }));
  
    console.log("Updated start time:", formattedStartTime);
  };
  
  const updateStartTime = (event) => {
    const { name, value } = event.target;
    console.log("value=", value);
    // Update formData for controlled inputs
    setFormData((prevData) => {
      const newFormData = {
        ...prevData,
        [name]: value,
      };
  
      const { date, time, ampm } = newFormData;
      console.log(date)
      const timezone = zoomrequest.timezone;
  
      if (!date || !time || !ampm || !timezone) return prevData;
  
      // Convert 12-hour time to 24-hour time
      let [hours, minutes] = time.split(":").map(Number);
      if (ampm === "PM" && hours < 12) {
        hours += 12;
      } else if (ampm === "AM" && hours === 12) {
        hours = 0;
      }
  
      // Combine date and time into a single string
      const dateTimeString = `${date} ${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
  
      // Convert to the specified timezone and format without 'Z'
      const formattedStartTime = moment.tz(dateTimeString, timezone).format("YYYY-MM-DDTHH:mm:ss");
  
      // Update zoomrequest state with the newly calculated startTime
      setzoomrequest((prevRequest) => ({
        ...prevRequest,
        startTime: formattedStartTime,
      }));
  
      console.log("Updated start time:", formattedStartTime);
  
      return newFormData;
    });
  };
  
  

  const handleTimeChange = (event) => {
    const hours = parseInt(document.getElementById("hrs").value, 10);
    const minutes = parseInt(document.getElementById("min").value, 10);
    const totalMinutes = hours * 60 + minutes;

    setzoomrequest((prevState) => ({
      ...prevState,
      duration: totalMinutes,
    }));
  };

  const handleChangeFields = (e) => {
    const { name, value } = e.target;

    setzoomrequest((prevState) => ({
      ...prevState,
      [name]: value,
    }));
    
  };
  const handleSubmit = async (e) => {
    e.preventDefault();

    console.log(zoomrequest)
   
  

    try {

      setissubmitting(true);
      const response = await axios.patch(
        `${baseUrl}/api/zoom/meet/${meetingId}`,
        zoomrequest,
        {
          headers: {
            Authorization: token,
          },
        }
      );
      setissubmitting(false)
      MySwal.fire({
        title: "updated!",
        text: "Meeting Updated Successfully",
        icon: "success",
        confirmButtonText: "OK",
      }).then(() => {
        const sentence = "Meeting Updated:"
        navigate('/mailSending', { state: { meetingData: response.data ,sentence} });
       // window.location.reload();
      });
    } catch (error) {
      setissubmitting(false);
      if (error.response && error.response.status === 400) {
        MySwal.fire({
          title: "Error!",
          text: "Failed to Generate Access Token",
          icon: "error",
          confirmButtonText: "OK",
        });
      }else{
      // Handle network errors or other exceptions
      // MySwal.fire({
      //   title: "Error!",
      //   text: "Some Unexpected Error occurred. Please try again later.",
      //   icon: "error",
      //   confirmButtonText: "OK",
      // });
      throw error
      }
    }
  };

  return (
    <div className="contentbackground">
      <div className="contentinner p-3">
      <div className={`outerspinner ${issubmitting? 'active' : ''}`}>
        <div className="spinner"></div>
      </div>
        <div className="divider">
          <div className="navigateheaders">
            <div
              onClick={() => {
                navigate(-1);
              }}
            >
              <i className="fa-solid fa-arrow-left"></i>
            </div>
            <div></div>
            <div
              onClick={() => {
                navigate("/dashboard/course");
              }}
            >
              <i className="fa-solid fa-xmark"></i>
            </div>
          </div>
          <h2 style={{ textDecoration: "underline" }}>Edit a Meeting</h2>
          <div className="formgroup pt-1 maxheight">
            <div className="inputgrp">
              <label htmlFor="topic">
                Meeting Title <span className="text-danger">*</span>
              </label>
              <span>:</span>
              <div>
                <input
                  type="text"
                  id="topic"
                  value={zoomrequest.topic}
                  name="topic"
                  onChange={handleChangeFields}
                  className="form-control .form-control-sm  "
                  placeholder="Title"
                  autoFocus
                  required
                />
              </div>
            </div>
            <div className="inputgrp">
              <label htmlFor="agenda">
                Meeting Descrition <span className="text-danger">*</span>
              </label>
              <span>:</span>
              <div>
                <input
                  type="text"
                  id="agenda"
                  name="agenda"
                  onChange={handleChangeFields}
                  value={zoomrequest.agenda === null ? "" : zoomrequest.agenda}
                  className="form-control .form-control-sm  "
                  placeholder="Description"
                  required
                />
              </div>
            </div>
            <div className="inputgrp">
              <label htmlFor="timezone">
                Time Zone <span className="text-danger">*</span>
              </label>
              <span>:</span>
              <div>
                <select
                  id="timezone"
                  className="form-control .form-control-sm "
                  name="timezone"
                  value={zoomrequest.timezone}
                  onChange={handleChangeFields}
                  required
                >
                  {timezones.map((code, index) => (
                    <option key={index} value={code.id}>
                      {code.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="inputgrp">
              <label htmlFor="when">
                When <span className="text-danger">*</span>
              </label>
              <span>:</span>
              <div className="datetimegrp">
                <input
                  type="date"
                  id="date"
                  name="date"
                  min={formattedDate}
                  value={formData.date}
                  onChange={updateStartTime}
                  className="form-control .form-control-sm "
                  required
                />
                <select
                  id="time"
                  name="time"
                  value={formData.time}
                  onChange={updateStartTime}
                  className="form-control .form-control-sm "
                >
                  {timeOptions.map((option, index) => (
                    <option key={index} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
                <select
                  id="ampm"
                  name="ampm"
                  value={formData.ampm}
                  onChange={updateStartTime}
                  className="form-control .form-control-sm "
                >
                  <option value="AM">AM</option>
                  <option value="PM">PM</option>
                </select>
              </div>
            </div>

            {Productversion != null && Productversion === "FREE" ? (
              <div className="inputgrp">
                <label htmlFor="duration">
                  Duration <span className="text-danger">*</span>
                </label>
                <span>:</span>
                <div>
                  <input
                    className="disabledbox"
                    readOnly
                    style={{ width: "90%" }}
                    value={zoomrequest.duration}
                  />{" "}
                  min
                </div>
              </div>
            ) : (
              <div className="inputgrp">
                <label htmlFor="duration">
                  Duration <span className="text-danger">*</span>
                </label>
                <span>:</span>
                <div className="datetimegrp">
                  <select
                    id="hrs"
                    value={formData.hours}
                    name="hrs"
                    onChange={handleTimeChange}
                    className="form-control .form-control-sm  "
                  >
                    <option value="0">0</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                    <option value="6">6</option>
                  </select>
                  <p className="mt-3">hours</p>
                  <select
                    id="min"
                    name="min"
                    value={formData.minutes}
                    onChange={handleTimeChange}
                    className="form-control .form-control-sm  "
                  >
                    <option value="0">0</option>
                    <option value="15">15</option>
                    <option value="30">30</option>
                    <option value="45">45</option>
                  </select>
                  <p className="mt-3">Minutes</p>
                </div>
              </div>
            )}

            <div className="inputgrp">
              <label htmlFor="invitees">
                Invitees
                <span className="text-danger">*</span>
              </label>
              <span>:</span>
              <div>
                <div className="inputlike">
                  {selectedEmails.length > 0 && (
                    <div className="listemail">
                      {" "}
                      {selectedEmails.map((email, index) => (
                        <div key={index} className="selectedemail">
                          {email.email}{" "}
                          <i
                            onClick={() => handleEmailClick(email.email)}
                            className="fa-solid fa-xmark"
                          ></i>
                        </div>
                      ))}
                    </div>
                  )}

                  <input
                    type="input"
                    id="customeinpu"
                    placeholder="search member..."
                    value={searchQuery}
                    onChange={(e) => handleSearch(e.target.value)}
                  />
                </div>
                {users.length > 0 && (
                  <div className="user-list">
                    {users.map((user) => (
                      <div key={user} className="usersingle">
                        <label
                          id="must"
                          className="p-1"
                          htmlFor={user}
                          onClick={() => handleEmailClick(user)}
                        >
                          {user}
                        </label>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>

            <div className="inputgrp">
              <label htmlFor="video">
                Video <span className="text-danger">*</span>
              </label>
              <span>:</span>
              <div>
                <div className="zoomset">
                  <p>Host</p>
                  <input
                    type="radio"
                    name="hostVideo"
                    value="true" // Add a value to the input
                    checked={zoomrequest.settings.hostVideo}
                    onChange={(e) => {
                      setzoomrequest((prevZoomRequest) => ({
                        ...prevZoomRequest,
                        settings: {
                          ...prevZoomRequest.settings,
                          hostVideo: e.target.value === "true", // Update hostVideo based on the value
                        },
                      }));
                    }}
                  />
                  <p>On</p>
                  <input
                    type="radio"
                    name="hostVideo"
                    value="false" // Add a value to the input
                    checked={!zoomrequest.settings.hostVideo}
                    onChange={(e) => {
                      setzoomrequest((prevZoomRequest) => ({
                        ...prevZoomRequest,
                        settings: {
                          ...prevZoomRequest.settings,
                          hostVideo: e.target.value === "true",
                        },
                      }));
                    }}
                  />
                  <p>Off</p>
                </div>
                <div className="zoomset">
                  <p>Participant</p>
                  <input
                    type="radio"
                    name="participantVideo"
                    value="true" // Add a value to the input
                    checked={zoomrequest.settings.participantVideo}
                    onChange={(e) => {
                      setzoomrequest((prevZoomRequest) => ({
                        ...prevZoomRequest,
                        settings: {
                          ...prevZoomRequest.settings,
                          participantVideo: e.target.value === "true", // Update participantVideo based on the value
                        },
                      }));
                    }}
                  />
                  <p>On</p>
                  <input
                    type="radio"
                    name="participantVideo"
                    value="false" // Add a value to the input
                    checked={!zoomrequest.settings.participantVideo}
                    onChange={(e) => {
                      setzoomrequest((prevZoomRequest) => ({
                        ...prevZoomRequest,
                        settings: {
                          ...prevZoomRequest.settings,
                          participantVideo: e.target.value === "true", // Update hostVideo based on the value
                        },
                      }));
                    }}
                  />
                  <p>Off</p>
                </div>
              </div>
            </div>
            <div className="inputgrp">
              <label htmlFor="options">
                Options <span className="text-danger">*</span>
              </label>
              <span>:</span>
              <div>
                <div className="zoomopt">
                  <input
                    type="checkbox"
                    name="joinBeforeHost"
                    checked={zoomrequest.settings.joinBeforeHost}
                    onChange={handleOptionsChange}
                  />
                  <p>Allow Participant to Join Anytime</p>
                </div>
                <div className="zoomopt">
                  <input
                    type="checkbox"
                    name="muteUponEntry"
                    checked={zoomrequest.settings.muteUponEntry}
                    onChange={handleOptionsChange}
                  />
                  <p>Mute Participant upon Entry</p>
                </div>
                <div className="zoomopt">
                {Productversion!=null && Productversion==="FREE" ? <div className="boxtype"></div> :(
                  <input
                    type="checkbox"
                    name="autoRecording"
                    checked={zoomrequest.settings.autoRecording === "local"}
                    onChange={handleAutoRecordingChange}
                  />)}
                  <p>Automatically Record Video On Local Computer</p>
                </div>
              </div>
            </div>
          </div>
          <div className="cornerbtn">
            <button
              className="btn btn-warning"
              type="button"
              onClick={() => {
                navigate(-1);
              }}
            >
              Cancel
            </button>
            <button
              className="btn btn-primary"
              onClick={handleSubmit}
              disabled={issubmitting}
            >
              Update
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditMeeting;
