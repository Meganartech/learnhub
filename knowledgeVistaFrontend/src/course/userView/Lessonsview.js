import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
const Lessonsview = () => {
  const MySwal = withReactContent(Swal);
  const [lessons, setLessons] = useState([]);
  const [course, setCourse] = useState([]);
  const [notes, setNotes] = useState({});
  const [selectedLesson, setSelectedLesson] = useState(null);
  const { courseName, courseId } = useParams();

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
        if (!response.ok) {
          throw new Error('Failed to fetch lessons');
        }
        const data = await response.json();
        console.log(data);
        setLessons(data);
      } catch (error) {
        console.error('Error fetching lessons:', error.message);
      }
    };

    fetchLessons();
  }, [courseId]);

  const Startnote = async (e, lesid) => {
    e.preventDefault();
    try {
      // Fetch notes for the selected lesson
      const notesForLesson = await fetchNotesForLesson(lesid);
      
      // Ensure that there are notes available for the lesson
      if (notesForLesson.length > 0) {
        // Extract the ID of the first note
        const firstNoteId = notesForLesson[0].notesId;
  
        // Redirect to the path with the first note's ID
        window.location.href = `/courses/${courseName}/${courseId}/lesson/${lesid}/note/${firstNoteId}`;
      } else {
        MySwal.fire({
          title: "Warning!",
          text: "No Notes Available for this lesson", // Assuming the message is returned from the backend
          icon: "warning",
          confirmButtonText: "OK",
      })
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
      console.log(noteforlesson);
      return noteforlesson;
    } catch (error) {
      console.error('Error fetching notes:', error.message);
      return [];
    }
  };
  
  const handleviewnote = async (e, lesid) => {
    e.preventDefault();
    try {
      // Toggle visibility of notes and update button text
      if (selectedLesson === lesid) {
        setSelectedLesson(null);
        setNotes({});
        return;
      }
  
      // Fetch notes for the selected lesson
      const noteforlesson = await fetchNotesForLesson(lesid);
  
      // Update the notes state for the corresponding lesson ID
      setNotes({ [lesid]: noteforlesson });
      setSelectedLesson(lesid);
    } catch (error) {
      console.error('Error fetching notes:', error.message);
    }
  };
  const testredirect = () => {

  
    window.location.href = `/test/${courseId}`;
  };

  return (
    <div className='lessmain'>
      <div className='lesmaindiv'>
       <div style={{display:"grid",gridTemplateColumns:"30fr 4fr"}}> <h1>{courseName}</h1>
        <button className='btn btn-primary' onClick={testredirect}>Start Test</button></div>
        <div className="coursedesc">
          <h5>{course.courseDescription}</h5>
        </div>
        <div style={{textAlign:"left"}}>
        <h3>Lessons Covered:</h3></div>
        <div className='lessonlistscroll'>
          {lessons.map((lesson) => (
            <div key={lesson.lessonId}>
              <div className='lesdiv'>
                <div className='lesname'>{lesson.lessonTitle}</div>
                <button className='btn btn-warning' onClick={(e) => { handleviewnote(e, lesson.lessonId) }}>
                  {selectedLesson === lesson.lessonId ? 'Close' : 'View'}
                </button>
                <button  onClick={(e)=>{Startnote(e,lesson.lessonId)}} className='btn btn-primary'> Start</button>
                
              </div>
              <div className='notesdiv'>
                {/* Render notes for the corresponding lesson ID only if it's selected */}
                {selectedLesson === lesson.lessonId && notes[selectedLesson] && notes[selectedLesson].map((note, index) => (
                  <div key={note.notesId}>
                    <div className='notetitle'><div>{note.notesTitle}</div>    <a href={`/courses/${courseName}/${courseId}/lesson/${lesson.lessonId}/note/${note.notesId}`}><i className="fa-solid fa-play"></i></a></div>
                    {/* Conditionally render the horizontal line if it's not the last note */}
                    {index !== notes[selectedLesson].length - 1 && <div className="horizontal-line"></div>}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Lessonsview;
