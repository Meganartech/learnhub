import React, { useEffect, useRef, useState } from 'react';
import ReactPlayer from 'react-player';
import { Link, useParams } from 'react-router-dom';

const ViewVideo = () => {
  const { courseId, courseName } = useParams();
  const [lessonId, setLessonId] = useState(null);
  const [AllLessons, setAllLessons] = useState([]);
  const [currentLessonIndex, setCurrentLessonIndex] = useState(0);
  const [videoType, setVideoType] = useState('local');
  const [videoSource, setVideoSource] = useState('');
  const [currentLesson, setCurrentLesson] = useState(null);
  const role=sessionStorage.getItem("role");
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`http://localhost:8080/course/getLessondetail/${courseId}`);
        if (!response.ok) {
          throw new Error('Failed to fetch data');
        }
        const lessonList = await response.json();
        setAllLessons(lessonList);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [courseId]);

  useEffect(() => {
    if (AllLessons.length > 0 && lessonId === null) {
      setLessonId(AllLessons[0].lessonId);
    }
  }, [AllLessons, lessonId]);

  useEffect(() => {
    if (lessonId !== null) {
      fetchVideo(lessonId);
    }
  }, [lessonId]);

  useEffect(() => {
    if (currentLessonIndex >= 0 && currentLessonIndex < AllLessons.length) {
      setLessonId(AllLessons[currentLessonIndex].lessonId);
    }
  }, [currentLessonIndex, AllLessons]);

  useEffect(() => {
    // Update the currently playing lesson whenever lessonId changes
    if (lessonId !== null) {
      const playingLesson = AllLessons.find(lesson => lesson.lessonId === lessonId);
      setCurrentLesson(playingLesson);
    

    }
  }, [lessonId, AllLessons]);

  const fetchVideo = async (lessId) => {
    try {
      const response = await fetch(`http://localhost:8080/lessons/getvideoByid/${lessId}`);
      if (response.ok) {
        const contentType = response.headers.get('Content-Type');
        if (contentType && contentType.includes('application/octet-stream')) {
          setVideoType('local');
          const data = await response.arrayBuffer();
          setVideoSource(data);
        } else if (contentType && contentType.includes('text/plain')) {
          setVideoType('youtube');
          const data = await response.text();
          setVideoSource(data);
        } else {
          console.error('Unsupported video type');
        }
      } else {
        console.error('Failed to fetch video:', response.status, response.statusText);
      }
    } catch (error) {
      console.error('Error fetching video:', error);
    }
  };

  const handleNoteClick = async (lessId) => {
    const lessonIndex = AllLessons.findIndex(lesson => lesson.lessonId === lessId);
    fetchVideo(lessId);
    setCurrentLessonIndex(lessonIndex);
  };

  const handleVideoEnd = () => {
    setCurrentLessonIndex((prevIndex) => prevIndex + 1);
  };

  const handleNextButtonClick = () => {
    if (currentLessonIndex < AllLessons.length - 1) {
      setCurrentLessonIndex(currentLessonIndex + 1);
    }
  };

  const handlePreviousButtonClick = () => {
    if (currentLessonIndex > 0) {
      setCurrentLessonIndex(currentLessonIndex - 1);
    }
  };

  return (
    <div className='contentbackground'>
  <div className='contentinner'>
    {AllLessons.length > 0 ? (
      <div className='vdoplusbtn'>
        <div style={{display:"grid",gridTemplateColumns:"9fr 1fr"}}>
        <h1 style={{ textAlign: 'center' }}>{courseName}</h1>
        <Link to={`/test/start/${courseName}/${courseId}`} className='btn btn-primary' style={{height:"40px"}}> 
                Start Test</Link>
        </div>
        <div>
          <div className="main">
            <div className="VideoFrame">
              {videoType === 'local' ? (
                <ReactPlayer
                  width="90%"
                  height="80%"
                  url={videoSource ? URL.createObjectURL(new Blob([videoSource], { type: 'video/mp4' })) : null}
                  controls
                  playing
                  light={currentLesson ? `data:image/jpeg;base64,${currentLesson.thumbnail}` : null}
                  onEnded={handleVideoEnd}
                  config={{
                    file: {
                      attributes: {
                        controlsList: 'nodownload'
                      }
                    }
                  }}
                />
              ) : videoType === 'youtube' ? (
                <ReactPlayer
                  width="90%"
                  height="80%"
                  controls
                  url={videoSource ? videoSource : null}
                  light={currentLesson ? `data:image/jpeg;base64,${currentLesson.thumbnail}` : null}
                  onEnded={handleVideoEnd}
                />
              ) : null}
              {currentLesson && (
                <div>
                  <h2>{currentLesson.lessontitle}</h2>
                  <p style={{ textIndent: "100px" }}>{currentLesson.lessonDescription}</p>
                </div>
              )}
            </div>
            <div className="list">
              <h3 style={{ color: 'black', textDecoration: 'underline' }}>Lessons Covered</h3>
              <div className="content" >
                {AllLessons.map((lesson, index) => (
                  <div key={index}
                    className={`notes ${lesson.lessonId === lessonId ? 'current-lesson' : ''}`}
                    onClick={() => handleNoteClick(lesson.lessonId)}
                    style={{ border: lesson.lessonId === lessonId ? '3px solid green' : 'none' }}
                  >
                    <div className="child">
                      <img
                        src={`data:image/jpeg;base64,${lesson.thumbnail}`}
                        style={{ width: "100%", height: "100%", }}
                        alt="thumbnail"
                      />
                    </div>
                    <div className='child'>
                      <h4>{lesson.lessontitle}</h4>
                      <h6>{lesson.lessonDescription}</h6>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
        <div style={{ textAlign: 'right' }}>
          {currentLessonIndex > 0 && (
            <button onClick={handlePreviousButtonClick} className='btn btn-primary'>Previous</button>
          )}
          {currentLessonIndex < AllLessons.length - 1 && (
            <button onClick={handleNextButtonClick} className='btn btn-primary ml-2'>Next</button>
          )}
        </div>
      </div>
    ) : (
      (role === "ADMIN" || role === "TRAINER") ? (
        <div className='enroll'>
          <h3 className='mt-4'>No Lessons Found for {courseName}</h3>
          <Link to={`/uploadvideo/${courseName}/${courseId}`} className='btn btn-primary'>Add Now</Link>
        </div>
      ) : (
        <div className='enroll'>
        <h3 className='mt-4'>No Lessons Found for {courseName}</h3>
        
      <Link to="/dashboard/course" className='btn btn-primary'>Go Back</Link>
        </div>
      )
    )}
  </div>
</div>

  )}

export default ViewVideo;
