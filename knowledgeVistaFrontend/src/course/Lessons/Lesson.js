import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { Link } from "react-router-dom";

const Lesson = () => {
  const { courseId } = useParams();
  const {courseName}=useParams();
  const MySwal = withReactContent(Swal);
  const [lessons, setLessons] = useState([]);
  const [course, setCourse] = useState({});
  const [notes, setNotes] = useState({});
  const [selectedLesson, setSelectedLesson] = useState(null);
  const [currentLessonId, setCurrentLessonId] = useState();
  const [notFound, setNotFound] = useState(false);
  const [formData, setFormData] = useState({
    selection: "url",
    inputValue: "",
  });
  const [notesField, setNotesField] = useState({
    notesTitle: "",
    notesDesc: "",
    fileUrl: "",
    videoFile: null,
  });
  const [showNoteForm, setShowNoteForm] = useState(false);

  const toggleNoteForm = (e, id) => {
    e.preventDefault();
    setShowNoteForm(!showNoteForm);
    setCurrentLessonId(id);
  };

  const handleNotesChange = (e) => {
    const { name, value } = e.target;
    setNotesField({ ...notesField, [name]: value });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setNotesField({ ...notesField, videoFile: file, fileUrl: "" });
  };

  const handleSelectionChange = (event) => {
    const { value } = event.target;
    setFormData((prevData) => ({
      ...prevData,
      selection: value,
      inputValue: value === "file" ? "" : prevData.inputValue,
    }));
  };

  useEffect(() => {
    const fetchLessons = async () => {
      try {
        const response1 = await fetch(`http://localhost:8080/course/get/${courseId}`);
        if (!response1.ok) {
          throw new Error('Failed to fetch course');
        }
        const coursedata = await response1.json();
        setCourse(coursedata);

        const response = await fetch(`http://localhost:8080/Lesson/getAllLessons/${courseId}`);
        if (response.status === 404) {
          setNotFound(true);
        }
        const data = await response.json();
        setLessons(data);
      } catch (error) {
        console.error('Error fetching lessons:', error.message);
      }
    };

    fetchLessons();
  }, []);

  const Startnote = async (e, lessonId) => {
    e.preventDefault();
    try {
      const notesForLesson = await fetchNotesForLesson(lessonId);
      if (notesForLesson.length > 0) {
        const firstNoteId = notesForLesson[0].notesId;
        window.location.href = `/courses/${course.courseName}/${courseId}/lesson/${lessonId}/note/${firstNoteId}`;
      } else {
        MySwal.fire({
          title: "Warning!",
          text: "No Notes Available for this lesson",
          icon: "warning",
          confirmButtonText: "OK",
        });
      }
    } catch (error) {
      console.error('Error:', error.message);
    }
  };

  const fetchNotesForLesson = async (lessonId) => {
    try {
      const responsenote = await fetch(`http://localhost:8080/Notes/getNote/${lessonId}`);
      if (!responsenote.ok) {
        throw new Error('Failed to fetch notes');
      }
      const noteforlesson = await responsenote.json();
      return noteforlesson;
    } catch (error) {
      console.error('Error fetching notes:', error.message);
      return [];
    }
  };

  const handleviewnote = async (e, lessonId) => {
    e.preventDefault();
    try {
      if (selectedLesson === lessonId) {
        setSelectedLesson(null);
        setNotes({});
        return;
      }

      const noteforlesson = await fetchNotesForLesson(lessonId);
      setNotes({ [lessonId]: noteforlesson });
      setSelectedLesson(lessonId);
    } catch (error) {
      console.error('Error fetching notes:', error.message);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const formData = new FormData();
      formData.append("notesTitle", notesField.notesTitle);
      formData.append("notesDesc", notesField.notesDesc);
    
      if (notesField.videoFile) {
        formData.append("videoFile", notesField.videoFile);
      } else {
        formData.append("fileUrl", notesField.fileUrl);
      }

      const response = await fetch(`http://localhost:8080/Notes/save/${currentLessonId}`, {
        method: "POST",
        body: formData,
      });

      if (response.ok) {
        const data = await response.json();
        MySwal.fire({
          title: "Success!",
          text: data.message,
          icon: "success",
          confirmButtonText: "OK",
        }).then((result) => {
          if (result.isConfirmed) {
            setNotesField({
              notesTitle: "",
              notesDesc: "",
              fileUrl: "",
              videoFile: null,
            });
          }
        });
      }
    } catch (error) {
      console.error("Exception:", error);
      MySwal.fire({
        title: "Error!",
        text: "An error occurred while registering. Please try again later.",
        icon: "error",
        confirmButtonText: "OK",
      });
    }
  };

  const deleteNote = async (noteId) => {
    try {
      const response = await fetch(`http://localhost:8080/Notes/deletenote/${noteId}`, {
        method: 'DELETE',
      });
      const data = await response.json();
      if (response.ok) {
        MySwal.fire({
          position: 'top-end',
          icon: 'success',
          title: data.message,
          showConfirmButton: false,
          timer: 3000,
          toast: true,
        });
      }
    } catch (error) {
      MySwal.fire({
        position: 'top-end',
        icon: "error",
        title: "Error Occurred on deleting the Note",
        showConfirmButton: false,
        timer: 3000,
        toast: true,
      });
    }
  };

  const deleteLesson = async (lessonId) => {
    try {
      const response = await fetch(`http://localhost:8080/Lesson/deletelesson/${lessonId}`, {
        method: 'DELETE',
      });
      const data = await response.json();
      if (response.ok) {
        MySwal.fire({
          position: 'top-end',
          icon: 'success',
          title: data.message,
          showConfirmButton: false,
          timer: 3000,
          toast: true,
        });
      }
    } catch (error) {
      MySwal.fire({
        position: 'top-end',
        icon: "error",
        title: "Error Occurred on deleting the Lesson",
        showConfirmButton: false,
        timer: 3000,
        toast: true,
      });
      console.error("Exception:", error);
    }
  };

  return (
    //course.css
    <div className='lessmain'>
      {notFound ? (
        <div className='notfoundlesson'>
          <div className='notfoundinner'>
            
          <h2>No Lessons found for this course.</h2>
          <Link to={`/course/Addlesson/${courseName}/${courseId}`}className='btn btn-primary'>Add Lesson</Link></div>
          
        </div>
      ) : (
        <div className='lesmaindiv'style={{ height: "80%" }} >
          <div style={{display:'grid',gridTemplateColumns:"10fr 1fr",alignContent:"center"}}>
          <h1>{course.courseName}</h1> <Link to={`/course/Addlesson/${courseName}/${courseId}`}className='btn btn-primary'>Add Lesson</Link></div>
          
          <div className="coursedesc">
            <h5>{course.courseDescription}</h5>
          </div>
          <div style={{ textAlign: "left" }}>
            <h3>Lessons Covered:</h3>
          </div>
          <div className='lessonlistscroll'>
            {lessons.map((lesson) => (
              <div key={lesson.lessonId}>
                <div className='lesdiv'>
                  <div className='lesname'>{lesson.lessonTitle}</div>
                  <button className='bttoncust' onClick={() => { deleteLesson(lesson.lessonId) }}>
                    <i className="fa-solid fa-trash"></i>
                  </button>
                  <button className='bttoncust' onClick={(e) => { toggleNoteForm(e, lesson.lessonId) }}>
                    <i className="fa-solid fa-plus"></i>
                  </button>
                  <button className='bttoncust' onClick={(e) => { handleviewnote(e, lesson.lessonId) }}>
                    {selectedLesson === lesson.lessonId ? <i className="fa-solid fa-eye-slash"></i> : <i className="fa-solid fa-eye"></i>}
                  </button>
                  <button onClick={(e) => { Startnote(e, lesson.lessonId) }} className='bttoncust'><i class="fa-solid fa-play"></i></button>
                </div>
                <div className='notesdiv'>
                  {selectedLesson === lesson.lessonId && notes[selectedLesson] && notes[selectedLesson].map((note, index) => (
                    <div key={note.notesId}>
                      <div className='notetitle'>
                        <div>{note.notesTitle}</div>
                        {notes[selectedLesson].length > 1 && <button className='bttoncust' onClick={() => { deleteNote(note.notesId) }}><i className="fa-solid fa-trash"></i></button>}
                        <button className='bttoncust'>
                          <a href={`/courses/${course.courseName}/${courseId}/lesson/${lesson.lessonId}/note/${note.notesId}`}>
                            <i className="fa-solid fa-play"></i>
                          </a>
                        </button>
                      </div>
                      {index !== notes[selectedLesson].length - 1 && <div className="horizontal-line"></div>}
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
          <div>
          </div>
        </div>
      )}
      {showNoteForm && (
        <div className='lesmaindiv' style={{ height: "80%" }}>
          <div style={{ textAlign: "left", padding: "10px" }}>
            <h3 className='mt-1'>Add Note </h3>
            <div className="mb-3 ">
              <label htmlFor="notesTitle" className=" mt-3 ">
                <h5> Note<span className="text-danger">*</span></h5>
              </label>
              <input
                type="text"
                name="notesTitle"
                className="form-control"
                style={{
                  backgroundColor: "white",
                  boxShadow: "0 0 5px rgba(0, 0, 0, 0.2)"
                }}
                placeholder="Untitled"
                value={notesField.notesTitle}
                required
                onChange={handleNotesChange}
              />
            </div>
            <div className="mb-3">
              <label htmlFor="notesDesc" >
                <h5>Notes Description <span className="text-danger">*</span></h5>
              </label>
              <textarea
                className="form-control"
                id="notesDesc"
                name="notesDesc"
                rows="3"
                required
                style={{
                  backgroundColor: "white",
                  boxShadow: "0 0 5px rgba(0, 0, 0, 0.2)"
                }}
                placeholder="Give Notes Description here......"
                value={notesField.notesDesc}
                onChange={handleNotesChange}
              ></textarea>
            </div>
            <div className="mb-3">
              <label htmlFor="notesDesc">
                <h5>File/URL <span className="text-danger">*</span></h5>
              </label>
              <div className="fileurlinput">
                {formData.selection === "file" && (
                  <div>
                    <input
                      type="file"
                      name="file"
                      required
                      onChange={handleFileChange}
                    />
                  </div>
                )}
                {formData.selection === "url" && (
                  <div>
                    <input
                      type="text"
                      name="fileUrl"
                      value={notesField.fileUrl}
                      required
                      style={{
                        backgroundColor: "white",
                        boxShadow: "0 0 5px rgba(0, 0, 0, 0.2)"
                      }}
                      className="urlbox form-control"
                      onChange={handleNotesChange}
                      placeholder="Enter URL"
                    />
                  </div>
                )}
                <select
                  value={formData.selection}
                  className="form-control select"
                  onChange={handleSelectionChange}
                >
                  <option value="file">Upload File</option>
                  <option value="url">Enter URL</option>
                </select>
              </div>
              <div className="submitbtn text-center mt-5">
                <button className='btn btn-primary' onClick={(e) => handleSubmit(e, selectedLesson)}>save</button>
                <button className='btn btn-warning ' onClick={toggleNoteForm} >cancel</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Lesson;

