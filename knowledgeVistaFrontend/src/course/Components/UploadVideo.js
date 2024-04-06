import React, { useState } from 'react'
import "../../certificate/certificate.css"
import { useParams } from 'react-router-dom';

import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
const UploadVideo = () => {
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const MySwal = withReactContent(Swal);
  
  const {courseId,courseName}=useParams();

    const [selectedFile, setSelectedFile] = useState(null);
    const [uploadType, setUploadType] = useState('video');
   const[videodata,setvideodata]=useState({
    Lessontitle:"",
    LessonDescription:"",
    fileUrl:"",
    thumbnail:null,
    videoFile:null,
    base64Image: null,
   })
   const handleChange = (e) => {
    const { name, value } = e.target;
    setvideodata({ ...videodata, [name]: value });
  };
  const convertImageToBase64 = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });
  };
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    
    // Update formData with the new file
    setvideodata((prevVideodata) => ({ ...prevVideodata, thumbnail: file }));
    
    // Convert the file to base64
    convertImageToBase64(file)
      .then((base64Data) => {
        // Set the base64 encoded image in the state
        setvideodata((prevVideodata) => ({
          ...prevVideodata,
          base64Image: base64Data,
        }));
      })
      .catch((error) => {
        console.error("Error converting image to base64:", error);
      });
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    const formDataToSend = new FormData();
    formDataToSend.append("Lessontitle", videodata.Lessontitle);
    formDataToSend.append("LessonDescription", videodata.LessonDescription);
    formDataToSend.append("thumbnail",videodata.thumbnail)
    
    if (videodata.videoFile) {
      formDataToSend.append("videoFile", videodata.videoFile);
    }else{
      formDataToSend.append("fileUrl", videodata.fileUrl);
    }
    console.log(videodata.Lessontitle);
    console.log(videodata.LessonDescription)
    console.log(videodata.fileUrl)
    console.log(videodata.thumbnail)
    console.log(videodata.videoFile)

    try {
      const response = await fetch(`http://localhost:8080/lessons/save/${courseId}`, {
        method: "POST",
        body: formDataToSend,
      });
      console.log(formDataToSend);
      await response.json();
      setIsSubmitting(false);
      if (response.ok) {
       setvideodata({
        Lessontitle:"",
        LessonDescription:"",
        fileUrl:"",
        thumbnail:null,
        videoFile:null,
        base64Image: null,
       });
       setSelectedFile("")
        MySwal.fire({
          title: "video added",
          text: "Video added sucessfully !",
          icon: "success",
        }).then((result) => {
          if (result.isConfirmed) {
           
            window.location.href = `/lessonList/${courseName}/${courseId}`;
          }
        });
      }
    } catch (error) {
      setIsSubmitting(false);
      // Handle network errors or other exceptions
      MySwal.fire({
        title: "Error!",
        text: "Some Unexpected Error occured . Please try again later.",
        icon: "error",
        confirmButtonText: "OK",
      });
    }
  };
    const handleDrop = (e) => {
      e.preventDefault();
      const file = e.dataTransfer.files[0];
      handleFile(file);
    };
    const handleFileInputChange = (e) => {
      const file = e.target.files[0];
      const maxSize = 1024 * 1024 * 1024; // 1 GB in bytes
    
      if (file && file.size > maxSize) {
        alert('File size exceeds 1 GB limit.');
      } else {
        handleFile(file);
        setvideodata({ ...videodata, videoFile: file, fileUrl: "" });
      }
    };
    
    
    const handleFile = (file) => {
      if (file && file.type.includes('video')) {
        setSelectedFile(file);
      } else {
        alert('Please select a video file.');
      }
    };
    const handleUploadTypeChange = (e) => {
        setUploadType(e.target.value);
      };
    
  
    const handleDragOver = (e) => {
      e.preventDefault();
    };
  
  return (
    <div className='contentbackground'>
        <div className='contentinner'>
        <form onSubmit={handleSubmit}>
        <div className='divider'>
        {isSubmitting && (
        <div class="loading-spinner"></div>

      )}
        
            <h2 style={{textDecoration:"underline"}}>Upload Video for {courseName}</h2>
            <div className='innerdivider'>
            <div className='textinputs'>
            <div className='grp'>
                <label>Video Title</label>
                <input type='text'
                 placeholder='Video Title'
                 name='Lessontitle'
                 value={videodata.Lessontitle}
                 onChange={handleChange}
                 disabled={isSubmitting}
                 required/>
            </div>
            <div className='grp'>
                <label>Description</label>
                <textarea 
                name='LessonDescription'
                value={videodata.LessonDescription}
                placeholder='Video Description'
                rows={5}
                disabled={isSubmitting}
                 onChange={handleChange}
                ></textarea>
            </div>
            <div style={{display:"grid",gridTemplateColumns:"1fr 1fr 0fr 1fr",gap:"10px",padding:"20px"}}>
                <label>Thumbnail</label>
                    <label htmlFor='fileInput' style={{width:"100px", height:"40px"}} className='file-upload-btn'>
                    Upload
                    </label>
                    <input
               onChange={handleFileChange}
               disabled={isSubmitting}
                    name='thumbnail'
                    type='file'
                    id='fileInput'
                    className='file-upload'
                    accept='image/*'
                    />  {videodata.base64Image && ( 
                      <img
                        src={videodata.base64Image}
                        alt="Selected "
                        style={{ width:"100px",height:"100px"}}
                      />
                    )}

            </div>
        
        </div>

            <div className='videoinputs'>   
                <div>
                <input
                type="radio"
                name="uploadType"
                id="video"
                value="video"
                checked={uploadType === 'video'}
                onChange={handleUploadTypeChange}
                disabled={isSubmitting}
                />
                <label htmlFor="video" style={{marginLeft:"20px"}} >Video</label>
                 </div>
                 <div>
                <input
                type="radio"
                name="uploadType"
                id="url"
                value="url"
                checked={uploadType === 'url'}
                onChange={handleUploadTypeChange}
                disabled={isSubmitting}
                />
                <label htmlFor="url" style={{marginLeft:"20px"}}>Youtube Url</label>
            </div>

            {uploadType === 'url' ? (
                <input 
                type="text"
                 placeholder="Enter Youtube URL" 
                 name='fileUrl'
                 value={videodata.fileUrl} 
                 onChange={handleChange}
                  disabled={isSubmitting}
              className='urlinput' />
            ) : (
                <div
                className="dropzone"
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                >
                <p>Drag and drop </p>
                <p>Or</p>
                <label htmlFor='videoupload' style={{width:"200px"}} className='file-upload-btn'>  upload Video</label>
                <input
                    type="file"
                    className='file-upload'
                    id='videoupload'
                    name="file"
                    accept="video/*"
                    onChange={handleFileInputChange}
                    disabled={isSubmitting}
                />
                 {selectedFile && (
                <p>{selectedFile.name}</p>
              
            )}
                </div>
            )}
            </div>
            </div>
            <div className='cornerbtn'>           
                 <button className='btn btn-primary' type='submit'>Save</button> 
                 <button className='btn btn-primary'>Cancel</button>
            </div>
   
       </div>
       </form>
    </div>

</div>
      

  )
}

export default UploadVideo
