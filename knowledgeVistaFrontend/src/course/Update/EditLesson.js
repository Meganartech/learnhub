import React, { useEffect, useState } from 'react'
import "../../css/certificate.css"
import "../../css/Course.css"
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from '../../api/utils';
import axios from 'axios';

const EditLesson = () => {
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
  const MySwal = withReactContent(Swal);
  
  const {courseName,Lessontitle,lessonId,courseId}=useParams();

    const [selectedFile, setSelectedFile] = useState(null);
    const [uploadType, setUploadType] = useState('video');
   const[videodata,setvideodata]=useState({
    lessontitle:"",
    lessonDescription:"",
    fileUrl:"",
    thumbnail:null,
    videoFile:null,
    base64Image: null,
   })
  
   const [errors, setErrors] = useState({
    lessontitle:"",
    lessonDescription:"",
    fileUrl:"",
    thumbnail:null,
    videoFile:null,
    base64Image: null,
    })
    
  const token=sessionStorage.getItem("token")
   

useEffect(() => {
    const fetchVideoData = async () => {
        try {
            const response = await axios.get(`${baseUrl}/lessons/getLessonsByid/${lessonId}`, {
                headers: {
                    "Authorization": token
                }
            });
            const data = await response.data;
           
            if (response.status===200) {
                setvideodata(data);
                if(data.videofilename===null){
                    setUploadType('url')
                }
                console.log(videodata)
                if (data.thumbnail) {
                    setvideodata(prevVideoData => ({
                    ...prevVideoData,
                     base64Image: `data:image/jpeg;base64,${data.thumbnail}`
                }));
                setSelectedFile(Prevfile=>({
                    ...Prevfile,name:data.videofilename
                }))
                }
            }   
        } catch (error) {
          if(error.response){
            if(error.response.status===401){
              MySwal.fire({
                title: "Un Authorized",
                text: "you are UnAuthorized to access this Page Redirecting to  back",
                icon: "error",
              }).then((result) => {
                if (result.isConfirmed) {
                  navigate(-1);
                   }
              });
            }else if(error.response.status===404){
              MySwal.fire({
                title: "Not Found",
                text: `No lessons found with lesson name ${Lessontitle}`,
                icon: "error",
              }).then((result) => {
                if (result.isConfirmed) {
                  navigate(-1);
                   }
              });
            }
          }else{
            MySwal.fire({
              title: "Error!",
              text: "An error occurred . Please try again later.",
              icon: "error",
              confirmButtonText: "OK",
            });
          }
        }
    }

    fetchVideoData(); // Call the async function

    // Add any dependencies if needed
}, [lessonId, token]);

   const handleChange = (e) => {
    const { name, value } = e.target;
    let error = '';
    switch (name) {
      case 'lessontitle':
        error = value.length < 1 ? 'Please enter a Video Title' : '';
        break;
      case 'lessonDescription':
          error = value.length < 1 ? 'Please enter a Video Description' : '';
          break;
      case 'fileUrl':
        error = /^(https?:\/\/(www\.)?youtube\.com\/embed\/[\w-]+\??[\w-=&]*)$/.test(value) ? '' : 'Please enter the youtube Embed src url only ';
            break;

            default:
              break;
    }
    setErrors(prevErrors => ({
      ...prevErrors,
      [name]: error
    }));
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
  const isSaveDisabled = () => {
    // Check if video title and description have values
    const isVideoTitleValid = videodata.lessontitle.trim().length > 0;
    const isVideoDescriptionValid = videodata.lessonDescription.trim().length > 0;

    // Check if image is selected and converted to base64
    const isThumbnailValid = videodata.base64Image !== null;

    // Check if either video file is selected or URL is present and valid
    let isVideoValid = false;
    if (uploadType === 'video') {
        isVideoValid = selectedFile !== null;
    } else if (uploadType === 'url') {
        isVideoValid = /^(https?:\/\/(www\.)?youtube\.com\/embed\/[\w-]+\??[\w-=&]*)$/.test(videodata.fileUrl);
    }

    // Enable save button only if all conditions are met
    return !(isVideoTitleValid && isVideoDescriptionValid && isThumbnailValid && isVideoValid);
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
  const handleEdit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);

    
    const formDataToSend = new FormData();
    if (videodata.lessontitle) {
        formDataToSend.append("Lessontitle", videodata.lessontitle);
    }
    if (videodata.lessonDescription) {
        formDataToSend.append("LessonDescription", videodata.lessonDescription);
    }
   
    if (videodata.thumbnail) {
        formDataToSend.append("thumbnail", videodata.thumbnail);
    }

    // Send video file if available
    if (videodata.videoFile) {
        formDataToSend.append("videoFile", videodata.videoFile);
    }

    // Send file URL if available
    if (videodata.fileUrl) {
        formDataToSend.append("fileUrl", videodata.fileUrl);
    }
    // for (const [key, value] of formDataToSend.entries()) {
    //     console.log(`${key}: ${value}`);
    // }
    try {
        const response = await axios.patch(`${baseUrl}/lessons/edit/${videodata.lessonId}`, formDataToSend,{
            headers: {
                "Authorization": token
            }
        });
        if (response.status===200) {
            window.location.href = `/lessonList/${courseName}/${courseId}`;
            setIsSubmitting(false);
        } 
    } catch (error) { 
      if(error.response && error.response.status===401){
        setIsSubmitting(false);
        window.location.href = "/unauthorized";
      }else if(error.response && error.response===404){
        setIsSubmitting(false);
          MySwal.fire({
            title: "Not Found!",
            text: " The Lesson Not FOund",
            icon: "warning",
            confirmButtonText: "OK",
          });
      }else{
      setIsSubmitting(false);
       MySwal.fire({
      title: "error!",
      text: "Some Unexpected Error occured . Please try again later.",
      icon: "error",
      confirmButtonText: "OK",
    });
  }
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
        <form onSubmit={handleEdit}>
        {isSubmitting && (
        <div className="loading-spinner">

        </div>
      )}
        <div className='divider'>
            <h2 style={{textDecoration:"underline"}}>Edit Lesson {Lessontitle} </h2>
            <div className='innerdivider'>
            <div className='textinputs'>
            <div className='grp'>
                <label>Video Title</label>
                <div>
                <input type='text'
                 placeholder='Video Title'
                 name='lessontitle'
                 value={videodata.lessontitle}
                 onChange={handleChange}
                 className={`form-control form-control-lg mt-1 ${errors.lessontitle && 'is-invalid'}`}
                
                 disabled={isSubmitting}
                 required/>
                   <div className="invalid-feedback">
                {errors.lessontitle}
              </div>
              </div>
            </div>
            <div className='grp'>
                <label>Description</label>
                <div>
                <textarea 
                name='lessonDescription'
                value={videodata.lessonDescription}
                className={`form-control form-control-lg mt-1 ${errors.lessonDescription && 'is-invalid'}`}
                
                placeholder='Video Description'
                rows={4}
                disabled={isSubmitting}
                 onChange={handleChange}
                ></textarea>
                <div className="invalid-feedback">
                {errors.lessonDescription}
              </div>
              </div>
            </div>
            <div style={{display:"grid",gridTemplateColumns:"1fr 1fr 0fr 1fr",gap:"10px",padding:"20px"}}>
                <label>Thumbnail</label>
                    <label htmlFor='thumbnail' style={{width:"100px", height:"40px"}} className='file-upload-btn'>
                    Upload
                    </label>
                    <input
               onChange={handleFileChange}
               disabled={isSubmitting}
                    name='thumbnail'
                    type='file'
                    id='thumbnail'
                    className='file-upload'
                    accept='image/*'
                    /> 
                     {videodata.base64Image && ( 
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
              <div>
                <input 
                type="text"
                 placeholder="Enter Youtube URL" 
                 name='fileUrl'
                 value={videodata.fileUrl} 
                 onChange={handleChange}
                  disabled={isSubmitting}
                  className={`form-control form-control-lg mt-1 urlinput ${errors.fileUrl && 'is-invalid'}`}
               />
                    <div className="invalid-feedback">
                    {errors.fileUrl}
                  </div>
             </div>
            ) : (
                <div
                className="dropzone"
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                >
                <p>Drag and drop </p>
                <p>or</p>
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
        
            <div className='cornerbtn '>           
                 <button className='btn btn-primary' disabled={isSaveDisabled()} type='submit'>Save</button> 
                 <button className='btn btn-primary' onClick={()=>{    navigate(-1);} }>Cancel</button>
            </div>
       </div>
      
       </form>
    </div>

</div>
  )
}

export default EditLesson
