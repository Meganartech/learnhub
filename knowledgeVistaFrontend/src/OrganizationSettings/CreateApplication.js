import React from "react";
import login from "../images/login.jpg";
import "./CreateApplication.css";
const CreateApplication = () => {
  return (
    <div>
      <div className="container">
        <div className="card o-hidden border-0 shadow-lg my-5">
          <div className="card-body p-0">
            <div className="row">
              <div className="col-lg-4 d-none d-lg-block ">
                <img className="full-image" src={login} alt="login" />
              </div>
              <div className="col-lg-7">
                <div className="p-5">
                  <div className="text-center">
                    <h1 className="h4 text-gray-900 mb-4">
                      Create an Account!
                    </h1>
                  </div>

                  {/* <div
                    className="alert alert-danger form-group text-center"
                    role="alert"
                  >
                    MAIL ID ALREADY EXIST!
                  </div> */}

                  <form encType="multipart/form-data" className="user">
                    <div className="form-group row">
                      <div className="col-sm-6 mb-3 mb-sm-0">
                        {/* <label className="text-success">Firstname</label> */}
                        <input
                          type="text"
                          className="form-control form-control-lg"
                          id="exampleFirstName"
                          placeholder="First Name"
                          required
                          autoFocus
                        />
                      </div>
                      <div className="col-sm-6 ">
                        {/* <label className="text-success">Lastname</label> */}
                        <input
                          type="text"
                          className="form-control form-control-lg"
                          id="exampleLastName"
                          placeholder="Last Name"
                          required
                        />
                      </div>
                    </div>
                    <div className="form-group">
                      {/* <label className="text-success">Email Address</label> */}
                      <input
                        type="email"
                        className="form-control form-control-lg"
                        id="exampleInputEmail"
                        placeholder="Email Address"
                        required
                      />
                    </div>
                    <div className="form-group row">
                      <div className="col-sm-6">
                        {/* <label className="text-success">Company Name</label> */}
                        <input
                          type="text"
                          className="form-control form-control-lg"
                          id="exampleInputcompname"
                          placeholder="Company Name"
                          required
                        />
                      </div>

                      <div className="col-sm-6 ">
                        {/* <label className="text-success">
                          Company Profile Image
                        </label> */}
                        <input
                          type="file"
                          name="imageData"
                          className="  btn btn-primary btn-icon-split"
                          aria-describedby="fileupload"
                        />
                      </div>
                    </div>

                    <div className="form-group">
                      {/* <label className="text-success">Mobile</label> */}
                      <input
                        type="tel"
                        maxLength="10"
                        className="form-control form-control-lg"
                        placeholder="Mobile"
                        pattern="[0-9]{3}-[0-9]{3}-[0-9]{4}"
                        required
                      />
                    </div>

                    <div className="form-group row">
                      <div className="col-sm-6 mb-3 mb-sm-0">
                        {/* <label className="text-success">Set Password</label> */}
                        <input
                          type="password"
                          className="form-control form-control-lg"
                          id="exampleInputPassword"
                          placeholder="Set Password"
                          required
                        />
                      </div>
                      <div className="col-sm-6 mb-3 mb-sm-0">
                        {/* <label className="text-success">Confirm Password</label> */}
                        <input
                          type="password"
                          className="form-control form-control-lg"
                          id="exampleInputPassword"
                          placeholder="Confirm Password"
                          required
                        />
                      </div>
                    </div>
                    <div className="form-group">
                      {/* <label className="text-success"> Address</label> */}
                      <input
                        type="text"
                        className="form-control form-control-lg"
                        placeholder=" Address"
                        required
                      />
                    </div>

                    {/* <div className="form-group row">
                      <div className="col-sm-6 mb-3 mb-sm-0">
                        <label className="text-success">Address1</label>
                        <input
                          type="text"
                          className="form-control form-control-lg"
                          id="exampleInputPassword"
                          placeholder="Address 1"
                          required
                        />
                      </div>

                      <div className="col-sm-6 mb-3 mb-sm-0">
                        <label className="text-success">Address2</label>
                        <input
                          type="text"
                          className="form-control form-control-lg"
                          id="exampleInputPassword"
                          placeholder="Address 2"
                        />
                      </div>
                    </div>

                    <div className="form-group row">
                      <div className="col-sm-4 mb-3 mb-sm-0">
                        <label className="text-success">City</label>
                        <input
                          type="text"
                          className="form-control form-control-lg"
                          id="exampleFirstName"
                          placeholder="City"
                          required
                        />
                      </div>

                      <div className="col-sm-4">
                        <label className="text-success">State</label>
                        <input
                          type="text"
                          className="form-control form-control-lg"
                          id="exampleLastName"
                          placeholder="State"
                          required
                        />
                      </div>

                      <div className="col-sm-4">
                        <label className="text-success">Pincode</label>
                        <input
                          type="tel"
                          maxLength="6"
                          className="form-control form-control-lg"
                          id="exampleLastName"
                          pattern="[0-9]{3}-[0-9]{3}-[0-9]{4}"
                          placeholder="Pincode"
                          required
                        />
                      </div>
                    </div> */}

                    <div className="form-group">
                      {/* <label className="text-success">Organization Size</label> */}
                      <input
                        type="number"
                        min="1"
                        placeholder="Organization size"
                        pattern="[0-9]{3}-[0-9]{3}-[0-9]{4}"
                        className="form-control form-control-lg"
                        id="exampleLastName"
                        required
                      />
                    </div>

                    <input
                      type="submit"
                      value="Create"
                      className="btn btn-primary btn-lg btn-block"
                    />

                    <hr />
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateApplication;
