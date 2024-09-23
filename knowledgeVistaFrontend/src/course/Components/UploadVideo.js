import React, { useState } from "react";
import "../../css/certificate.css";
import "../../css/Course.css";
import { useNavigate, useParams } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils";
import axios from "axios";
const UploadVideo = () => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const MySwal = withReactContent(Swal);
  const navigate = useNavigate();
  const { courseId, courseName } = useParams();
  const token = sessionStorage.getItem("token");
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploadType, setUploadType] = useState("video");
  const [videodata, setvideodata] = useState({
    Lessontitle: "",
    LessonDescription: "",
    fileUrl: "",
    thumbnail: null,
    videoFile: null,
    base64Image: null,
    normalurl: "",
  });

  const [errors, setErrors] = useState({
    Lessontitle: "",
    LessonDescription: "",
    fileUrl: "",
    thumbnail: null,
    videoFile: null,
    base64Image: null,
    normalurl: "",
  });
  const handleChange = (e) => {
    const { name, value } = e.target;
    let error = "";
    switch (name) {
      case "Lessontitle":
        error = value.length < 1 ? "Please enter a Video Title" : "";
        setvideodata({ ...videodata, [name]: value });
        break;
      case "LessonDescription":
        error = value.length < 1 ? "Please enter a Video Description" : "";
        setvideodata({ ...videodata, [name]: value });
        break;
      case "normalurl":
        const updatedData = { ...videodata, [name]: value }; // Update normalurl

        const match = value.match(
          /^.*(?:(?:youtu\.be\/|v\/|vi\/|u\/\w\/|embed\/|shorts\/)|(?:(?:watch)?\?v(?:i)?=|\&v(?:i)?=))([^#\&\?]*).*/
        );
        let extractedId;

        if (match) {
          extractedId = match[1];
          const embedUrl = `https://www.youtube.com/embed/${extractedId}`;

          // Combine both normalurl and fileUrl updates in one setvideodata
          setvideodata({ ...updatedData, fileUrl: embedUrl });
        } else {
          setvideodata(updatedData); // Ensure the normalurl is still updated even if the match fails
          error = "Invalid YouTube URL. Please provide a valid URL.";
        }
        break;

      default:
        break;
    }
    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: error,
    }));
  };

  const isSaveDisabled = () => {
    // Check if video title and description have values
    const isVideoTitleValid = videodata.Lessontitle.trim().length > 0;
    const isVideoDescriptionValid =
      videodata.LessonDescription.trim().length > 0;

    // Check if either video file is selected or URL is present and valid
    let isVideoValid = false;
    if (uploadType === "video") {
      isVideoValid = selectedFile !== null;
    } else if (uploadType === "url") {
      isVideoValid =
        /^(https?:\/\/(www\.)?youtube\.com\/embed\/[\w-]+\??[\w-=&]*)$/.test(
          videodata.fileUrl
        );
    }

    // Enable save button only if all conditions are met
    return !(isVideoTitleValid && isVideoDescriptionValid && isVideoValid);
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
    formDataToSend.append("thumbnail", videodata.thumbnail);

    if (uploadType === "video") {
      formDataToSend.append("videoFile", videodata.videoFile);
      formDataToSend.append("fileUrl", null);
    } else {
      formDataToSend.append("fileUrl", videodata.fileUrl);
      formDataToSend.append("videoFile", null);
    }
    for (const [key, value] of formDataToSend.entries()) {
      console.log(`${key}: ${value}`);
    }
    try {
      const response = await axios.post(
        `${baseUrl}/lessons/save/${courseId}`,
        formDataToSend,
        {
          headers: {
            Authorization: token,
          },
        }
      );

      setIsSubmitting(false);
      if (response.status === 200) {
        setvideodata({
          Lessontitle: "",
          LessonDescription: "",
          fileUrl: "",
          thumbnail: null,
          videoFile: null,
          base64Image: null,
        });
        setSelectedFile("");

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
      if (error.response && error.response.status === 413) {
        MySwal.fire({
          title: "Storage Limit Exceeded",
          text: error.response.data,
          icon: "warning",
        });
      } else {
        // Handle network errors or other exceptions
        MySwal.fire({
          title: "Error!",
          text: "Some Unexpected Error occured . Please try again ....later.",
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
      alert("File size exceeds 1 GB limit.");
    } else {
      handleFile(file);
      setvideodata({ ...videodata, videoFile: file, fileUrl: "" });
    }
  };
  const handleFile = (file) => {
    if (file && file.type.includes("video")) {
      setSelectedFile(file);
    } else {
      alert("Please select a video file.");
    }
  };
  const handleUploadTypeChange = (e) => {
    setUploadType(e.target.value);
  };
  const handleDragOver = (e) => {
    e.preventDefault();
  };

  return (
    <div className="contentbackground">
      <div className="contentinner">
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
        <form onSubmit={handleSubmit}>
          {isSubmitting && <div className="loading-spinner"></div>}
          <div className="divider">
            <h2 style={{ textDecoration: "underline" }}>
              Upload Video for {courseName}{" "}
            </h2>
            <div className="innerdivider">
              <div className="textinputs">
                <div className="grp">
                  <label>
                    Video Title <span className="text-danger">*</span>
                  </label>
                  <div>
                    {" "}
                    <input
                      type="text"
                      placeholder="Video Title"
                      name="Lessontitle"
                      value={videodata.Lessontitle}
                      onChange={handleChange}
                      disabled={isSubmitting}
                      className={`form-control form-control-lg mt-1 ${
                        errors.Lessontitle && "is-invalid"
                      }`}
                      required
                    />
                    <div className="invalid-feedback">{errors.Lessontitle}</div>
                  </div>
                </div>
                <div className="grp">
                  <label>
                    Description<span className="text-danger">*</span>
                  </label>
                  <div>
                    <textarea
                      name="LessonDescription"
                      value={videodata.LessonDescription}
                      placeholder="Video Description"
                      rows={4}
                      className={`form-control form-control-lg mt-1 ${
                        errors.LessonDescription && "is-invalid"
                      }`}
                      disabled={isSubmitting}
                      onChange={handleChange}
                    ></textarea>{" "}
                    <div className="invalid-feedback">
                      {errors.LessonDescription}
                    </div>
                  </div>
                </div>
                <div className="thumb">
                  <label id="must">Thumbnail</label>
                  <label
                    htmlFor="fileInput"
                    id="must"
                    style={{ width: "100px", height: "40px" }}
                    className="file-upload-btn"
                  >
                    Upload
                  </label>
                  <input
                    onChange={handleFileChange}
                    disabled={isSubmitting}
                    name="thumbnail"
                    type="file"
                    id="fileInput"
                    className={`file-upload ${
                      errors.fileInput && "is-invalid"
                    }`}
                    accept="image/*"
                  />
                  {videodata.base64Image && (
                    <img
                      src={videodata.base64Image}
                      alt="Selected "
                      style={{ width: "100px", height: "100px" }}
                    />
                  )}
                </div>
              </div>

              <div className="videoinputs">
                <div>
                  <input
                    type="radio"
                    name="uploadType"
                    id="video"
                    value="video"
                    checked={uploadType === "video"}
                    onChange={handleUploadTypeChange}
                    disabled={isSubmitting}
                  />
                  <label htmlFor="video" style={{ marginLeft: "20px" }}>
                    Video <span className="text-danger">*</span>
                  </label>
                </div>
                <div>
                  <input
                    type="radio"
                    name="uploadType"
                    id="url"
                    value="url"
                    checked={uploadType === "url"}
                    onChange={handleUploadTypeChange}
                    disabled={isSubmitting}
                  />
                  <label htmlFor="url" style={{ marginLeft: "20px" }}>
                    Youtube Url <span className="text-danger">*</span>
                  </label>
                </div>

                {uploadType === "url" ? (
                  <>
                    <div>
                      <input
                        type="text"
                        name="normalurl"
                        placeholder="Enter Youtube URL"
                        value={videodata.normalurl}
                        onChange={handleChange}
                        className={`form-control form-control-lg mt-1 urlinput ${
                          errors.normalurl && "is-invalid"
                        }`}
                        disabled={isSubmitting}
                      />
                      <div className="invalid-feedback">{errors.normalurl}</div>
                      {videodata.fileUrl && (
                        <>
                          <div>
                            <input
                              type="text"
                              name="fileUrl"
                              value={videodata.fileUrl}
                              onChange={handleChange}
                              className="disabledbox mt-2"
                              style={{height:"20px"}}
                              readOnly
                              disabled={isSubmitting}
                              data-toggle="tooltip" data-placement="top" title="Embeded Url"
                             
                            />
                          
                            
                          </div>
                          <div>
                            {videodata.fileUrl && (
                              <iframe
                                style={{ marginTop: "10px" }}
                                src={videodata.fileUrl}
                              ></iframe>
                            )}
                          </div>
                        </>
                      )}
                    </div>
                  </>
                ) : (
                  <div
                    className="dropzone"
                    onDrop={handleDrop}
                    onDragOver={handleDragOver}
                  >
                    <p>Drag and drop </p>
                    <p>or</p>
                    <label
                      htmlFor="videoupload"
                      style={{ width: "200px" }}
                      className="file-upload-btn"
                    >
                      {" "}
                      upload Video
                    </label>
                    <input
                      type="file"
                      className="file-upload"
                      id="videoupload"
                      name="file"
                      accept="video/*"
                      onChange={handleFileInputChange}
                      disabled={isSubmitting}
                    />
                    {selectedFile && <p>{selectedFile.name}</p>}
                  </div>
                )}
              </div>
            </div>

            <div className="cornerbtn ">
              <button
                className="btn btn-primary"
                onClick={() => {
                  navigate(-1);
                }}
              >
                Cancel
              </button>
              <button
                className="btn btn-primary"
                disabled={isSaveDisabled()}
                type="submit"
              >
                Save
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default UploadVideo;
