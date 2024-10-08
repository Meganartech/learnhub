import React, { useRef, useEffect } from 'react';

const CourseForm = ({ formData, setFormData, errors, setErrors, setnextclick }) => {
    const courseName = useRef(null);
    const courseCategory = useRef(null);
    const courseDescription = useRef(null);
    const courseAmount = useRef(null);
    const Duration = useRef(null);
    const Noofseats = useRef(null);
    const courseImage = useRef(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        let error = '';

        const numericValue = name === 'courseAmount' || name === 'Duration' || name === 'Noofseats' ? parseFloat(value) : value;

        switch (name) {
            case 'courseName':
                error = value.length < 1 ? 'Please enter a Course Title' : '';
                break;
            case 'courseCategory':
                error = value.length < 1 ? 'Please enter a Course Category' : '';
                break;
            case 'courseDescription':
                error = value.length < 1 ? 'Please enter a Course Description' : '';
                break;
            case 'courseAmount':
                error = numericValue < 0 ? 'Course amount must be greater than or equal to 0' : '';
                break;
            case 'Duration':
                error = numericValue < 1 ? 'Duration must be greater than 0' : '';
                break;
            case 'Noofseats':
                error = numericValue < 1 ? 'Number of seats must be greater than 0' : '';
                break;
            default:
                break;
        }

        setErrors((prevErrors) => ({
            ...prevErrors,
            [name]: error,
        }));

        setFormData((prevFormData) => ({
            ...prevFormData,
            [name]: value,
        }));
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

        const allowedImageTypes = ['image/jpeg', 'image/png'];

        if (!allowedImageTypes.includes(file.type)) {
            setErrors((prevErrors) => ({
                ...prevErrors,
                courseImage: 'Please select an image of type JPG or PNG',
            }));
            return;
        }

        if (file && file.size > 1 * 1024 * 1024) {
            setErrors((prevErrors) => ({
                ...prevErrors,
                courseImage: 'Image size must be 1 MB or smaller',
            }));
            return;
        }

        setFormData((prevFormData) => ({
            ...prevFormData,
            courseImage: file,
        }));

        convertImageToBase64(file)
            .then((base64Data) => {
                setFormData((prevFormData) => ({
                    ...prevFormData,
                    base64Image: base64Data,
                }));
                setErrors((prevErrors) => ({
                    ...prevErrors,
                    courseImage: "",
                }));
            })
            .catch((error) => {
                console.error("Error converting image to base64:", error);
                setErrors((prevErrors) => ({
                    ...prevErrors,
                    courseImage: 'Error converting image to base64',
                }));
            });
    };

    const scrollToError = () => {
        if (errors.courseName) {
            courseName.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
        } else if (errors.courseCategory) {
            courseCategory.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
        } else if (errors.courseDescription) {
            courseDescription.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
        } else if (errors.courseAmount) {
            courseAmount.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
        } else if (errors.Duration) {
            Duration.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
        } else if (errors.Noofseats) {
            Noofseats.current.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' });
        }
    };

    useEffect(() => {
        scrollToError();
    }, [errors]);

    const handleNextClick = () => {
        let valid = true;
        let newErrors = { ...errors };

        if (formData.courseName.length < 1) {
            valid = false;
            newErrors.courseName = 'Please enter a Course Title';
        }

        if (formData.courseCategory.length < 1) {
            valid = false;
            newErrors.courseCategory = 'Please enter a Course Category';
        }

        if (formData.courseDescription.length < 1) {
            valid = false;
            newErrors.courseDescription = 'Please enter a Course Description';
        }

        if (formData.courseAmount < 0) {
            valid = false;
            newErrors.courseAmount = 'Course amount must be greater than or equal to 0';
        }

        if (formData.Duration < 1) {
            valid = false;
            newErrors.Duration = 'Duration must be greater than 0';
        }

        if (formData.Noofseats < 1) {
            valid = false;
            newErrors.Noofseats = 'Number of seats must be greater than 0';
        }

        if (!formData.courseImage) {
            valid = false;
            newErrors.courseImage = 'Please select an image';
        }

        setErrors(newErrors);

        if (valid) {
            setnextclick(true);
        }
    };

    return (
        <form className='createcourseform'>
            <div className='courseform'>
                <div className="form-group">
                    <label>Course Title</label>
                    <input
                        type="text"
                        name="courseName"
                        value={formData.courseName}
                        onChange={handleChange}
                        ref={courseName}
                        className={errors.courseName ? 'inputerror' : ''}
                    />
                    {errors.courseName && <p className="error">{errors.courseName}</p>}
                </div>
                <div className="form-group">
                    <label>Course Category</label>
                    <input
                        type="text"
                        name="courseCategory"
                        value={formData.courseCategory}
                        onChange={handleChange}
                        ref={courseCategory}
                        className={errors.courseCategory ? 'inputerror' : ''}
                    />
                    {errors.courseCategory && <p className="error">{errors.courseCategory}</p>}
                </div>
                <div className="form-group">
                    <label>Course Description</label>
                    <textarea
                        name="courseDescription"
                        value={formData.courseDescription}
                        onChange={handleChange}
                        ref={courseDescription}
                        className={errors.courseDescription ? 'inputerror' : ''}
                    />
                    {errors.courseDescription && <p className="error">{errors.courseDescription}</p>}
                </div>
                <div className="form-group">
                    <label>Course Amount</label>
                    <input
                        type="number"
                        name="courseAmount"
                        value={formData.courseAmount}
                        onChange={handleChange}
                        ref={courseAmount}
                        className={errors.courseAmount ? 'inputerror' : ''}
                    />
                    {errors.courseAmount && <p className="error">{errors.courseAmount}</p>}
                </div>
                <div className="form-group">
                    <label>Duration</label>
                    <input
                        type="number"
                        name="Duration"
                        value={formData.Duration}
                        onChange={handleChange}
                        ref={Duration}
                        className={errors.Duration ? 'inputerror' : ''}
                    />
                    {errors.Duration && <p className="error">{errors.Duration}</p>}
                </div>
                <div className="form-group">
                    <label>Number of Seats</label>
                    <input
                        type="number"
                        name="Noofseats"
                        value={formData.Noofseats}
                        onChange={handleChange}
                        ref={Noofseats}
                        className={errors.Noofseats ? 'inputerror' : ''}
                    />
                    {errors.Noofseats && <p className="error">{errors.Noofseats}</p>}
                </div>
                <div className="form-group">
                    <label>Upload Image</label>
                    <input
                        type="file"
                        accept="image/jpeg, image/png"
                        onChange={handleFileChange}
                        ref={courseImage}
                        className={errors.courseImage ? 'inputerror' : ''}
                    />
                    {errors.courseImage && <p className="error">{errors.courseImage}</p>}
                </div>
                <div className='footerbtn'>
                    <button type="button" className='button' onClick={handleNextClick}>Next</button>
                </div>
            </div>
        </form>
    );
};

export default CourseForm;