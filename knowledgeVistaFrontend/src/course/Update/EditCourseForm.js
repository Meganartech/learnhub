import React, { useState, useEffect } from "react";
import "../../css/CreateApplication.css";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../../api/utils";
import axios from "axios";
import "../../css/Style.css";
import errorimg from "../../images/errorimg.png";
import { useNavigate, useParams } from "react-router-dom";
const EditCourseForm = ({}) => {
  const MySwal = withReactContent(Swal);

  const token = sessionStorage.getItem("token");
  const [courseEdit, setCourseEdit] = useState([]);
  const { courseId } = useParams();
  const navigate = useNavigate();
  const [errors, setErrors] = useState({
    courseName: "",
    courseDescription: "",
    courseCategory: "",
    amount: "",
    noofseats: "",
    duration: "",
    courseImage: null,
  });
  const [img, setimg] = useState();
  useEffect(() => {
    const fetchcourse = async () => {
      try {
        const response = await axios.get(`${baseUrl}/course/get/${courseId}`, {
          headers: {
            Authorization: token,
          },
        });
        if (!response.status === 200) {
          MySwal.fire({
            icon: "error",
            title: "HTTP Error!",
            text: `HTTP error! Status: ${response.status}`,
          });
        }
        const data = response.data;
        setimg(`data:image/jpeg;base64,${data.courseImage}`);
        setCourseEdit(data);
      } catch (error) {
        MySwal.fire({
          title: "Error!",
          text: "An error occurred while Fetching course in Edit Form. Please try again later.",
          icon: "error",
          confirmButtonText: "OK",
        });
      }
    };
    fetchcourse();
  }, [courseId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCourseEdit({ ...courseEdit, [name]: value });
    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: validateField(name, value),
    }));
  };
  const validateField = (fieldName, fieldValue) => {
    const validations = {
      courseName: (value) => (value ? "" : "Course Name is required"),
      courseDescription: (value) =>
        value ? "" : "Course Description is required",
      courseCategory: (value) => (value ? "" : "Course Category is required"),
      amount: (value) =>
        value && !isNaN(value) ? "" : "Invalid Amount (must be a number)",
      noofseats: (value) =>
        value > 0 ? "" : "Number of Seats must be greater than 0",
      duration: (value) => (value > 0 ? "" : "Duration must be greater than 0"),
    };
    return validations[fieldName](fieldValue);
  };

  // Function to convert file to Base64
  const convertImageToBase64 = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });
  };

  // Handle file input change
  const handleFileChange = (e) => {
    // Update formData with the new file
    const file = e.target.files[0];
    if (file.size > 50 * 1024) {
      setErrors((prevErrors) => ({
        ...prevErrors,
        courseImage: "Image size must be 50kb or smaller",
      }));
      return;
    }
    // Convert the file to base64

    // Update formData with the new file
    setCourseEdit((courseEdit) => ({ ...courseEdit, courseImage: file }));
    convertImageToBase64(file)
      .then((base64Data) => {
        // Set the base64 encoded image in the state
        setimg(base64Data);
      })
      .catch((error) => {
        console.error("Error converting image to base64:", error);
      });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    for (const key in errors) {
      if (errors[key]) {
        // Scroll to the first field with an error (optional)
        document
          .getElementById(key)
          .scrollIntoView({
            behavior: "smooth",
            block: "nearest",
            inline: "start",
          });
        return; // Exit function if there are errors
      }
    }

    const formData = new FormData();
    if (courseEdit.courseName) {
      formData.append("courseName", courseEdit.courseName);
    }
    if (courseEdit.courseDescription) {
      formData.append("courseDescription", courseEdit.courseDescription);
    }
    if (courseEdit.amount) {
      formData.append("courseAmount", courseEdit.amount);
    }
    if (courseEdit.courseCategory) {
      formData.append("courseCategory", courseEdit.courseCategory);
    }
    if (courseEdit.courseImage) {
      formData.append("courseImage", courseEdit.courseImage);
    }
    if (courseEdit.duration) {
      formData.append("Duration", courseEdit.duration);
    }
    if (courseEdit.noofseats) {
      formData.append("Noofseats", courseEdit.noofseats);
    }

    try {
      const response = await axios.patch(
        `${baseUrl}/course/edit/${courseId}`,
        formData,
        {
          headers: {
            Authorization: token,
          },
        }
      );

      if (response.status === 200) {
        MySwal.fire({
          title: "Course Updated",
          text: " course have Updated successfully you can check by refreshing !",
          icon: "success",
          confirmButtonText: "OK",
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.href = "/dashboard/course";
          }
        });
      }
    } catch (error) {
      // Handle network errors or other exceptions
      MySwal.fire({
        title: "Error!",
        text: "An error occurred . Please try again later.",
        icon: "error",
        confirmButtonText: "OK",
      });
    }
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
          <div className="outer">
            <div className="First">
              <h2 className="heading ">Update Course</h2>

              <div className="form-group">
                <label htmlFor="courseName">
                  Course Name <span className="text-danger">*</span>
                </label>
                <div>
                  <input
                    type="text"
                    name="courseName"
                    id="courseName"
                    className={`form-control form-control-lg ${
                      errors.courseName && "is-invalid"
                    }`}
                    placeholder="Enter the Course Name"
                    value={courseEdit.courseName}
                    onChange={handleChange}
                    autoFocus
                  />
                  <div className="invalid-feedback">{errors.courseName}</div>
                </div>
              </div>
              <div className="form-group">
                <label htmlFor="courseDescription">
                  Course Description <span className="text-danger">*</span>
                </label>
                <div>
                  <textarea
                    name="courseDescription"
                    id="courseDescription"
                    rows={5}
                    className={`form-control form-control-lg ${
                      errors.courseDescription && "is-invalid"
                    }`}
                    placeholder="Description about the Course"
                    value={courseEdit.courseDescription}
                    onChange={handleChange}
                  />
                  <div className="invalid-feedback">
                    {errors.courseDescription}
                  </div>
                </div>
              </div>
              <div className="form-group">
                <label htmlFor="courseCategory">
                  Course Category <span className="text-danger">*</span>
                </label>
                <div>
                  <input
                    type="text"
                    name="courseCategory"
                    id="courseCategory"
                    className={`form-control form-control-lg  ${
                      errors.courseCategory && "is-invalid"
                    }`}
                    placeholder="Category"
                    value={courseEdit.courseCategory}
                    onChange={handleChange}
                  />{" "}
                  <div className="invalid-feedback">
                    {errors.courseCategory}
                  </div>
                </div>
              </div>
              <div className="form-group ">
                <label htmlFor="noofseats">
                  No of Seats <span className="text-danger">*</span>
                </label>
                <div>
                  <input
                    name="noofseats"
                    type="number"
                    id="noofseats"
                    value={courseEdit.noofseats}
                    className={`form-control form-control-lg  ${
                      errors.noofseats && "is-invalid"
                    }`}
                    onChange={handleChange}
                  />
                  <div className="invalid-feedback">{errors.noofseats}</div>
                </div>
              </div>
            </div>

            <div className="second">
              <div className="form-group">
                <label htmlFor="courseImage">
                  <h2>
                    Course Image <span className="text-danger">*</span>
                  </h2>
                </label>
                <label
                  htmlFor="courseImage"
                  style={{ width: "200px" }}
                  className={`file-upload-btn ${
                    errors.courseImage && `is-invalid`
                  }`}
                >
                  Upload Image
                </label>
                <div className="invalid-feedback">{errors.courseImage}</div>
                <input
                  type="file"
                  name="courseImage"
                  id="courseImage"
                  accept="image/jpeg, image/png, image/gif"
                  placeholder="Upload Image"
                  className="file-upload"
                  onChange={handleFileChange}
                />
                <img
                  src={img}
                  onError={(e) => {
                    e.target.src = errorimg; // Use the imported error image
                  }}
                  alt="selected pic of course"
                  style={{
                    width: "100px",
                    height: "100px",
                  }}
                />
                <br />
                <div className="form-group mt-1">
                  <label htmlFor="courseAmount">
                    Amount <span className="text-danger">*</span>
                  </label>
                  <div>
                    <input
                      type="number"
                      name="amount"
                      id="amount"
                      className={`form-control form-control-lg mt-1 ${
                        errors.courseAmount && "is-invalid"
                      }`}
                      placeholder="Amount"
                      value={courseEdit.amount}
                      onChange={handleChange}
                    />
                    <div className="invalid-feedback">
                      {errors.courseAmount}
                    </div>
                  </div>
                </div>
                <div className="form-group mt-1">
                  <label htmlFor="duration">
                    Course Duration <span className="text-danger">*</span>
                  </label>
                  <div>
                    <input
                      name="duration"
                      type="number"
                      id="duration"
                      className={`form-control form-control-lg mt-1 ${
                        errors.duration && "is-invalid"
                      }`}
                      value={courseEdit.duration}
                      onChange={handleChange}
                    />
                    <div className="invalid-feedback">{errors.duration}</div>
                  </div>
                </div>

                <div>
                  <button type="submit" className="btn btn-primary w-100">
                    Update Course
                  </button>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditCourseForm;
