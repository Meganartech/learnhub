import React, { useState } from "react";

const AddNotes = ({ notesField, handleNotesChange, handleFileChange }) => {
  const [formData, setFormData] = useState({
    selection: "url",
    inputValue: "",
  });

  const handleSelectionChange = (event) => {
    const { value } = event.target;
    setFormData((prevData) => ({
      ...prevData,
      selection: value,
      inputValue: value === "file" ? "" : prevData.inputValue,
    }));
  };

  return (
    <div>
      <div className="Note mt-4" >
        <div className="mb-3">
          <label htmlFor="notesTitle" className=" deleteNote">
            <h5> Note Title<span className="text-danger">*</span> </h5>
          </label>
          <input
            type="text"
            name="notesTitle"
            className="form-control"
            placeholder="Note Name"
          
            value={notesField.notesTitle}
            required
            onChange={handleNotesChange}
          />
        </div>
        <div className="mb-3">
          <label htmlFor="notesDesc" >
            <h5>Notes Description<span className="text-danger">*</span></h5>
          </label>
          <textarea
            className="form-control"
            id="notesDesc"
            name="notesDesc"
            rows="3"
            required
          
            placeholder="Give Notes Description here......"
            value={notesField.notesDesc}
            onChange={handleNotesChange}
          ></textarea>
        </div>
        <div className="mb-3">
          <label  >
            <h5>File/URL<span className="text-danger">*</span></h5>
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
        </div>
      </div>
    </div>
  );
};

export default AddNotes;
