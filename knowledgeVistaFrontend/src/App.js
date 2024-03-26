import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { useState ,useEffect } from "react";
import StudentRegister from "./Student/StudentRegister";
import ForgetPassword from "./AuthenticationPages/forgetpassword";
import Login from "./AuthenticationPages/login";
import AddLessonForm from "./course/Lessons/AddLessonFrom";
import CreateApplication from "./OrganizationSettings/CreateApplication";
import ResetPassword from "./AuthenticationPages/ResetPassword";
import React from "react";
import PrivateRoute from "./AuthenticationPages/PrivateRoute";
import Missing from "./AuthenticationPages/Missing";
import Unauthorized from "./AuthenticationPages/Unauthorized";
import Notesview from "./course/userView/Notesview";
import Lessonsview from "./course/userView/Lessonsview";
import AttenTestList from "./course/Test/AttenTestList";
import AttenTest from "./course/Test/AttenTest";
import CertificateInputs from "./certificate/CertificateInputs";
import Template from "./certificate/Template";
import ProfileView from "./Student/ProfileView";
import EditCourse from "./course/Update/EditCourse";
import CourseView from "./course/Components/CourseView";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import AddCourse from "./course/Add/AddCourse";
import CourseDetails from "./course/Lessons/CourseDetails";
import Lesson from "./course/Lessons/Lesson";
import AddLesson from "./course/Lessons/AddLesson";
import Layout from "./Layout";
import CreateTest from "./course/Test/CreateTest";
import TestList from "./course/Test/TestList";
import ViewStudentList from "./ViewStudentList";
import AssignCourse from "./AssignCourse";
import Paycheck from "./Paycheck";
import Mycourse from "./Student/Mycourse";


function App() {
  const MySwal = withReactContent(Swal);
  const [isToggled, setIsToggled] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [course, setCourse] = useState([]);
  
  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };
  const filteredCourses = course.filter((item) => {
    const name = item.courseName ? item.courseName.toLowerCase() : "";
    return name.includes(searchQuery.toLowerCase());
  });
  
  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await fetch("http://localhost:8080/course/viewAll");

        const data = await response.json();

        setCourse(data);
   
      } catch (error) {
       console.error(error);
      }
    };

    fetchItems();
  }, []);

  return (
    <Router>
      <div className="App ">
        <Routes>
         
                  <Route element={<Layout  
                  isToggled={isToggled}
                  setIsToggled={setIsToggled}
                  searchQuery={searchQuery}
                  handleSearchChange={handleSearchChange}
                  setSearchQuery={setSearchQuery}/>}>

                      {/* <Route path="/" element={<CreateApplication />} /> */}
                      <Route path="/mycourses" element={<PrivateRoute authenticationRequired={true} ><Mycourse/></PrivateRoute>}/>
                      <Route path="/dashboard/course" element={<PrivateRoute authenticationRequired={true}><CourseView  filteredCourses={filteredCourses} /></PrivateRoute>} />
                      <Route path="/course/admin/edit" element={<PrivateRoute authenticationRequired={true}><EditCourse  filteredCourses={filteredCourses} /></PrivateRoute>} />
                      <Route path="/course/Addlesson/:courseId" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><AddLesson /></PrivateRoute>} />
                      <Route path="/course/AddTest/:courseId" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><CreateTest /></PrivateRoute>} />
                      <Route path="/course/testlist/:courseId" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><TestList /></PrivateRoute>} />
                      <Route path="/course/viewlessons/:courseId" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><Lesson/></PrivateRoute>} />
                      <Route path="/courses/:courseName/:courseId/lesson/:lessonId/note/:noteId" element={<Notesview/>} />
                      <Route path="/test/:courseId" element={<AttenTestList/>}/>
                      <Route path="/test/start/:courseId/:testId" element={<AttenTest/>}/>
                      <Route path="/courses/:courseName/:courseId" element={<Lessonsview/>}/>
                      <Route path="/course/addcourse" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><AddCourse /></PrivateRoute>} />
                      <Route path="/course/dashboard/profile" element={<PrivateRoute authenticationRequired={true}><ProfileView/></PrivateRoute>}/>
                      <Route path="/course/edit/:courseId" element={<CourseDetails/>}/>
                      <Route path="/certificate" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><CertificateInputs/></PrivateRoute>}/>
                      <Route path="/template" element={<PrivateRoute authenticationRequired={true}><Template/></PrivateRoute>}/>
                      <Route path="/assignCourse/:userId" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><AssignCourse /></PrivateRoute>} />
                      <Route path="/view/Students" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><ViewStudentList/></PrivateRoute>}/>
                      <Route path="/certificate" element={<PrivateRoute authenticationRequired={true} authorizationRequired={true}><CertificateInputs/></PrivateRoute> }/>
                  </Route>
  
          <Route path="/" element={<StudentRegister/>}/>
          <Route path="/unauthorized" element={<Unauthorized/>}/>
          {/* <Route path="/StudentRegistration" element={<StudentRegister />} /> */}
          <Route path="/forgot-password" element={<ForgetPassword />} />
          <Route path="/login" element={<Login />} />
          <Route path="/resetpassword" element={<ResetPassword />}></Route>
      
          <Route path="/template" element={<Template/>}/>
          <Route path="*" element={<Missing/>}/>
          <Route path="/pay" element={<Paycheck/>}/>
        </Routes>
      </div>
    </Router>
  );
}

export default App;
