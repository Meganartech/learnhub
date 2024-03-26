import React, { useEffect, useState } from 'react';
import './Course.css';
import { useParams } from 'react-router-dom';
import ReactPlayer from 'react-player';


const Notesview = () => {
  const { lessonId, noteId } = useParams();
  const [lesson, setLesson] = useState(null);
  const [notelist, setNotelist] = useState([]);
  const [selectedNoteIndex, setSelectedNoteIndex] = useState(0);
  const [videoData, setVideoData] = useState(null);

  useEffect(() => {
    const fetchNotes = async () => {
      try {
        const responseNote = await fetch(`http://localhost:8080/Notes/getNote/${lessonId}`);
        if (!responseNote.ok) {
          throw new Error('Failed to fetch notes');
        }
        const noteforlesson = await responseNote.json();
        setNotelist(noteforlesson);
        console.log("notes for lesson",noteforlesson);
       
        const initialNoteIndex = noteforlesson.findIndex((note) => note.notesId === parseInt(noteId));
        setSelectedNoteIndex(initialNoteIndex !== -1 ? initialNoteIndex : 0);
        
        const responseLesson = await fetch(`http://localhost:8080/Lesson/${lessonId}`);
        if (!responseLesson.ok) {
          throw new Error('Failed to fetch lessons');
        }
        const data = await responseLesson.json();
        setLesson(data);

        console.log("lesson",data);
      } catch (error) {
        console.error('Error fetching notes:', error);
      }
    };

    fetchNotes(); // Call the fetchNotes function
  }, [lessonId, noteId]);

  const fetchVideo = async (noteId) => {
    try {
      const response = await fetch(`http://localhost:8080/Notes/getnoteByid/${noteId}`, {
        method: 'GET',
        responseType: 'arraybuffer'
      });
     console.log("got video")
      if (response.ok) {
        const data = await response.arrayBuffer();
        setVideoData(data);
      } else {
        console.error('Failed to fetch video:', response.status, response.statusText);
      }
    } catch (error) {
      console.error('Error fetching video:', error);
    }
  };

  const handleNoteClick = async (note, index) => {
    setSelectedNoteIndex(index);
    if (!note.fileUrl) {
      await fetchVideo(note.notesId);
      console.log(note.notesId)
    }
  };
  


  

  return (
    <div className="bg p-4">
      <div className="viewcontent ">
      {lesson && (
        <div className="lesson">
          <h1>{lesson.lessonTitle}</h1>
          {/* <Link className='btn-btn-primary' to={`/course/viewlessons/${courseId}`}>Go back</Link> */}
          <p>{lesson.lessonDesc}</p>
        </div>
      )}
      <div className="main">
        <div className="VideoFrame">
          {notelist.length > 0 && selectedNoteIndex !== null && (
            <>
              {notelist[selectedNoteIndex].fileUrl ? (
                <iframe
                  width="100%"
                  height="100%"
                  src={notelist[selectedNoteIndex].fileUrl}
                  frameBorder="0"
                  allowFullScreen
                  title="Your Title"
                ></iframe>
              ) : (
               
                <ReactPlayer
                  url={URL.createObjectURL(new Blob([videoData], { type: 'video/mp4' }))}
                  controls
                  width="100%"
                  height="100%"
                  playing={false}
                  config={{
                    file: {
                      attributes: {
                        controlsList: 'nodownload' 
                      }
                    }
                  }}
                />
              )}
            </>
          )}
        </div>
        <div className="list">
          <h3 style={{ color: 'black', textDecoration: 'underline' }}>Playlist</h3>
          <div className="content">
            {notelist.map((note, index) => (
              <div
                className={`notes ${index === selectedNoteIndex ? 'selected' : ''}`}
                key={index}
                onClick={() => handleNoteClick(note, index)}
              >
                <span>{index + 1}.</span>
                <h6>{note.notesTitle}</h6>
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className="notecontent">
        {notelist.length > 0 && selectedNoteIndex !== null && (
          <>
            <h1>{notelist[selectedNoteIndex].notesTitle}</h1>
            <p>{notelist[selectedNoteIndex].notesDesc}</p>
          </>
        )}
      </div>
      {/* <div className="navigationButtons">
        <div></div>
        {selectedNoteIndex > 0 && (
          <button className="btn btn-primary" onClick={handlePreviousNote}>
            Previous
          </button>
        )}
        <button className="btn btn-primary" onClick={handleNextNote}>
          Next
        </button>
        </div>*/}
      </div> 
    </div>
  );
};

export default Notesview;
