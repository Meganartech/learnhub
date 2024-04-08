import React, { useState,useEffect } from 'react';
import '../css/certificate.css';
import { useNavigate } from 'react-router-dom';
const License = () => {
    //.....................................Admin Function............................................
 
  const [categoryName, setCategoryName] = useState('');
  const [tagName, setTagName] = useState('');
  const [categories, setCategories] = useState([]);
  const [errors, setErrors] = useState({});
  const [categoryId, setCategoryId] = useState('');
  const [audioFile, setAudioFile] = useState(null);
  const [thumbnail, setThumbnail] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // const fetchData = async () => {
    //   try {
    //     const categoriesResponse = await axios.get('http://localhost:8080/api/v2/GetAllCategories');
    //     setCategories(categoriesResponse.data);
    //     console.log(categories)
    //   } catch (error) {
    //     console.error(error);
    //   }
    // };

    // fetchData();
  }, []);

  const generateAudioTitle = () => {
    const fileName = audioFile ? audioFile.name : 'Untitled Audio';
    return fileName;
  };

  // const handleSubmit = async (e) => {
  //   e.preventDefault();
  
  //   try {
  //     const formData = new FormData();
  //     const audioData = {
  //       audioFile: audioFile,
  //     };
  //     console.log(audioData)
  //     for (const key in audioData) {
  //       formData.append(key, audioData[key]);
  //     }
  //     fetch('http://localhost:8080/api/v2/uploadfile', formData,{
  //      method: 'POST',
  //         headers: {
  //           'Content-Type': 'multipart/form-data',
  //         },
  //           //  body: JSON.stringify(data),
  //             })
  //     // const response = await axios.post('http://localhost:8080/api/v2/uploadfile', formData, {
  //     //   headers: {
  //     //     'Content-Type': 'multipart/form-data',
  //     //   },
  //     // });
  
  //     // console.log(response.data);
  //     // console.log("audio updated successfully");
  //   } catch (error) {
  //     console.error('Error uploading audio:', error);
  //   }
  //   setCategoryId('')
  //   setAudioFile(null)
  //   setThumbnail(null)
  //   // navigate('/admin');
  // };
  const handleSubmit = async (e) => {
    e.preventDefault();
  
    try {
      const formData = new FormData();
      const audioData = {
        audioFile: audioFile,
      };
      console.log(audioData)
      for (const key in audioData) {
        formData.append(key, audioData[key]);
      }
  
      const response = await fetch('http://localhost:8080/api/v2/uploadfile', {
        method: 'POST',
        body: formData,
        // headers: { // Uncomment these headers if needed
        //   'Content-Type': 'multipart/form-data',
        // },
      });
  
      if (!response.ok) {
        throw new Error('Failed to upload audio file');
      }
  
      // Handle response if needed
      const data = await response.json();
      console.log('Upload successful:', data);
    } catch (error) {
      console.error('Error uploading audio:', error);
    }
  
    // Reset state after upload
    setCategoryId('');
    setAudioFile(null);
    setThumbnail(null);
    window.location.href = "/login";
    // navigate('/admin'); // Uncomment if navigation is needed
  };
  

    const validateForm = () => {
    let isValid = true;
    const errors = {};
    setErrors(errors);
    return isValid;
  };


  return (
    <div className='innerFrame'>
   
   <div className='mainform' style={{ gridTemplateColumns:"none"}}>
    
    
    
   <div className="row justify-content-center">
                <div className="col-lg-5">
                    <div className="card shadow-lg border-0 rounded-lg mt-5">
                        <div>
                            <div className="card-header">
                                <h6 className="text-center" style={{ fontSize:"30px" }}>Licence file</h6>
                            </div>
                            <br></br>
                            <br></br>
                            <div className="card-body">
                            <form className='form-container' onSubmit={handleSubmit}>
                <div className='modal-body text-center'>
                  <br />
                  <br />
                  <h5 className='modal-title modal-header bg-info' id='exampleModalLongTitle'>
                    Add New License File
                  </h5>
                  <input
                    type='file'
                    className='form-control'
                    placeholder='Choose Audio File'
                    name='audioFile'
                    onChange={(e) => setAudioFile(e.target.files[0])}
                  />
                  {errors.audioFile && <div className="error-message">{errors.audioFile}</div>}
                  <br />
                </div>
                <div className='modal-footer'>
                  <input
                    type='submit'
                    value='Upload'
                    className='btn'style={{ backgroundColor:"#17a2b8",color:"White",fontWeight:"500" }}
                  />
                </div>
              </form>



                                </div>
                                <br></br>
                                <br></br>
                        </div>
                    </div>
                </div>
            </div>
    </div>
    {/* <div className='btngrp' style={{height:"7rem"}}>
      <button className='btn btn-primary' style={{width:"8rem"}} onClick={save}>Save</button>
    </div> */}
    </div>
  );
};

export default License;
