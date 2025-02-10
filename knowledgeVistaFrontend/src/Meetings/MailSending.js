// import React, { useEffect, useState } from "react";
// import ReactQuill from "react-quill";
// import "react-quill/dist/quill.snow.css";
// import Swal from "sweetalert2";
// import withReactContent from "sweetalert2-react-content";
// import { useLocation, useNavigate } from "react-router-dom";
// import axios from "axios";
// import baseUrl from "../api/utils";

// const MailSending = () => {
//   const navigate = useNavigate();
//   const location = useLocation();
//   const token=sessionStorage.getItem("token")
//   const[issubmitting,setissubmitting]=useState(false)
//   const [searchResults, setSearchResults] = useState({
//     to: [],
//     cc: [],
//     bcc: [],
//   });
//   const [searchQuery, setSearchQuery] = useState({ to: "", cc: "", bcc: "" });
//   const MySwal = withReactContent(Swal);
//   const { meetingData, sentence } = location.state || {};
//   const [mailvalues, setmailvalues] = useState({
//     to: [],
//     cc: [],
//     bcc: [],
//     subject: "",
//     body: "",
//   });
//   useEffect(() => {
//     if (meetingData) {
//       const emails =
//         meetingData.settings?.meeting_invitees.map(
//           (invitee) => invitee.email
//         ) || [];

//       setmailvalues((prev) => ({
//         ...prev,
//         to: emails, // Set the 'to' field as the invitees' emails
//         subject: ` ${meetingData.topic}`, // Setting subject
//         body: ` ${sentence}${meetingData.topic}<br>
// Meeting details:<br>
// Join Url:  <a href="${
//           meetingData.join_url
//         }" target="_blank" rel="noopener noreferrer">
// ${meetingData.join_url}
//       </a><br>
// Password: ${meetingData.password}<br>
// Time: ${new Date(meetingData.start_time)}<br>
// TimeZone: ${meetingData.timezone}`,
//       }));
//     }
//   }, [meetingData]);

//   const handleEmailClick = (name, email) => {
//     setmailvalues((prev) => {
//       // Get the current list of emails for the given name, or initialize it as an empty array if it doesn't exist
//       const currentEmails = Array.isArray(prev[name]) ? prev[name] : [];

//       // Check if the email is already in the list
//       const isEmailInList = currentEmails.includes(email);

//       // Update the list based on whether the email is already present
//       const updatedField = isEmailInList
//         ? currentEmails.filter((e) => e !== email) // Remove the email if it's already in the list
//         : [...currentEmails, email]; // Add the email if it's not in the list

//       // Return the updated mail values
//       return {
//         ...prev,
//         [name]: updatedField,
//       };
//     });

//     // Clear the search query and search results for the specific field
//     setSearchQuery((prev) => ({
//       ...prev,
//       [name]: "",
//     }));

//     setSearchResults((prevResults) => ({
//       ...prevResults,
//       [name]: [], // Clear search results for the specific field
//     }));
//   };

//   const handleSearch = async (name, query) => {
//     setSearchQuery((prev) => ({
//       ...prev,
//       [name]: query,
//     }));
//     if (query.length > 1) {
//       const token = sessionStorage.getItem("token");
//       const role = sessionStorage.getItem("role");

//       let url;

//       if (role === "TRAINER") {
//         url = `${baseUrl}/search/usersbyTrainer`;
//       } else if (role === "ADMIN") {
//         url = `${baseUrl}/search/users`;
//       } else {
//         console.error("Role is not defined or not recognized");
//         return;
//       }

//       try {
//         const response = await axios.get(url, {
//           headers: {
//             Authorization: token,
//           },
//           params: {
//             query,
//           },
//         });

//         // Create a Set of selected emails to filter out
//         const selectedEmailsSet = new Set(
//           searchResults[name]?.map((emailObj) => emailObj.email) || []
//         );

//         // Filter users to exclude already selected emails
//         const filteredUsers = response.data.filter(
//           (user) => !selectedEmailsSet.has(user)
//         );

//         // Update the correct part of searchResults state based on the `name`
//         setSearchResults((prev) => ({
//           ...prev,
//           [name]: filteredUsers,
//         }));
//       } catch (error) {
//         console.error("Error fetching users:", error);
//         throw error
//       }
//     } else {
//       // If query length <= 1, clear the corresponding search result
//       setSearchResults((prev) => ({
//         ...prev,
//         [name]: [],
//       }));
//     }
//   };

//   const handleChange = (e) => {
//     const { name, value } = e.target;
//     setmailvalues((prev) => ({
//       ...prev,
//       [name]: value,
//     }));
//   };
//   const handleQuillChange = (value) => {
//     setmailvalues((prev) => ({
//       ...prev,
//       body: value,
//     }));
//   };
//   const modules = {
//     toolbar: [
//       [{ header: "1" }, { header: "2" }, { font: [] }],
//       [{ size: [] }],
//       ["bold", "italic", "underline", "strike", "blockquote"],
//       [{ list: "ordered" }, { list: "bullet" }],
//       ["link",  "video"],
//     ],
//     clipboard: {
//       matchVisual: false,
//     },
//   };

//   const formats = [
//     "header",
//     "font",
//     "size",
//     "bold",
//     "italic",
//     "underline",
//     "strike",
//     "blockquote",
//     "list",
//     "bullet",
//     "link",
//     "image",
//     "video",
//   ];

//   const handleSubmit = async () => {
//     try {
      
//       if (!mailvalues.to || mailvalues.to.length === 0) {
//         MySwal.fire({
//           title: "Warning!",
//           text: "No recipients selected. Please add at least one recipient.",
//           icon: "warning",
//           confirmButtonText: "OK",
//         });
//         return; // Exit the function early if no recipients
//       }
//       setissubmitting(true)
//       const response = await axios.post(`${baseUrl}/sendMail`, mailvalues, {
//         headers: {
//           "Authorization": token
//         }
//       });
//       setissubmitting(false)
//       MySwal.fire({
//         title: "Success!",
//         text: "Email Send Successfully",
//         icon: "success",
//         confirmButtonText: "OK",
//       });
//       navigate(-1);
//     } catch (error) {
//       setissubmitting(false)
//       if (error.response && error.response.status === 404 ){
//         MySwal.fire({
//           title: "Error!",
//           text: "Email Details Not Found .Try Updating Your Account Details",
//           icon: "error",
//           confirmButtonText: "OK",
//         }).then(()=>{
//           navigate("/settings/mailsettings")
//         })
//       }else{
//       // MySwal.fire({
//       //   title: "Error!",
//       //   text: "Failed to Send Email.please Try again after some time",
//       //   icon: "error",
//       //   confirmButtonText: "OK",
//       // });
//       throw error
//     }
//     }
//   };

//   return (
//     <div>
//     <div className="page-header"></div>
//     <div className="card">
//       <div className="card-body">
//       <div className="row">
//       <div className="col-12">
//       <div className={`outerspinner ${issubmitting? 'active' : ''}`}>
//         <div className="spinner"></div>
//       </div>
//         <h2>Add People</h2>
//         <div className="inputlike mb-2">
//           {mailvalues.to.length > 0 && (
//             <div className="listemail">
//               {" "}
//               {mailvalues.to.map((email, index) => (
//                 <div key={index} className="selectedemail">
//                   {email}{" "}
//                   <i
//                     onClick={() => handleEmailClick("to", email)}
//                     className="fa-solid fa-xmark"
//                   ></i>
//                 </div>
//               ))}
//             </div>
//           )}
//           <input
//             type="search"
//             id="customeinpu"
//             name="to"
//             className="form-control"
//             placeholder="To"
//             value={searchQuery.to}
//             onChange={(e) => handleSearch("to", e.target.value)}
//           />
//         </div>
//         {searchResults.to.length > 0 && (
//           <div className="user-list mb-1">
//             {searchResults.to.map((user) => (
//               <div key={user} className="usersingle">
//                 <label
//                   id="must"
//                   className="p-1"
//                   htmlFor={user}
//                   onClick={() => handleEmailClick("to", user)}
//                 >
//                   {user}
//                 </label>
//               </div>
//             ))}
//           </div>
//         )}

//         <div className="inputlike mb-2">
//           {mailvalues.cc.length > 0 && (
//             <div className="listemail">
//               {" "}
//               {mailvalues.cc.map((email, index) => (
//                 <div key={index} className="selectedemail">
//                   {email}{" "}
//                   <i
//                     className="fa-solid fa-xmark"
//                     onClick={() => handleEmailClick("cc", email)}
//                   ></i>
//                 </div>
//               ))}
//             </div>
//           )}
//           <input
//             type="search"
//             id="customeinpu"
//             name="cc"
//             className="form-control"
//             placeholder="CC"
//             value={searchQuery.cc}
//             onChange={(e) => handleSearch("cc", e.target.value)}
//           />
//         </div>
//         {searchResults.cc.length > 0 && (
//           <div className="user-list mb-1">
//             {searchResults.cc.map((user) => (
//               <div key={user} className="usersingle">
//                 <label
//                   id="must"
//                   className="p-1"
//                   htmlFor={user}
//                   onClick={() => handleEmailClick("cc", user)}
//                 >
//                   {user}
//                 </label>
//               </div>
//             ))}
//           </div>
//         )}

//         <div className="inputlike mb-2">
//           {mailvalues.bcc.length > 0 && (
//             <div className="listemail">
//               {" "}
//               {mailvalues.bcc.map((email, index) => (
//                 <div key={index} className="selectedemail">
//                   {email}{" "}
//                   <i
//                     className="fa-solid fa-xmark"
//                     onClick={() => handleEmailClick("bcc", email)}
//                   ></i>
//                 </div>
//               ))}
//             </div>
//           )}
//           <input
//             type="search"
//             id="customeinpu"
//             name="bcc"
//             className="form-control"
//             placeholder="BCC"
//             value={searchQuery.bcc}
//             onChange={(e) => handleSearch("bcc", e.target.value)}
//           />
//         </div>
//         {searchResults.bcc.length > 0 && (
//           <div className="user-list mb-1">
//             {searchResults.bcc.map((user) => (
//               <div key={user} className="usersingle">
//                 <label
//                   id="must"
//                   className="p-1"
//                   htmlFor={user}
//                   onClick={() => handleEmailClick("bcc", user)}
//                 >
//                   {user}
//                 </label>
//               </div>
//             ))}
//           </div>
//         )}
//         <input
//           type="text"
//           id="subject"
//           name="subject"
//           className="form-control  mb-2 inputlike"
//           placeholder="Subject"
//           value={mailvalues.subject}
//           onChange={handleChange}
//         />
//         <div
//           className="mb-2 inputlike"
//           style={{ overflow: "auto", height: "300px", padding: "0px" }}
//         >
//           <ReactQuill
//             value={mailvalues.body}
//             onChange={handleQuillChange}
//             modules={modules}
//             formats={formats}
//             style={{ height: "245px" }}
//           />
//         </div>
//         <div className="cornerbtn">
//           <button
//             className="btn btn-secondary"
//             type="button"
//             onClick={() => {
//               navigate(-1);
//             }}
//           >
//             Cancel
//           </button>
//           <button
//             className="btn btn-primary"
//             type="button"
//             onClick={handleSubmit}
//           >
//             Send
//           </button>
//         </div>
//         </div>
//         </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default MailSending;
