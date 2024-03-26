import login from "../images/login.jpg";

import React from "react";

const AddStaffContainer = () => {
  return (
    <div className="container-fluid ">
      <div className="d-flex flex-column">
        <div className="container-fluid ">
          <div className="d-flex flex-column">
            <div className="container-fluid">
              <div className="card o-hidden border-0 shadow-lg my-5">
                <div className="card-body p-0">
                  <div className="row">
                    <div className="col-lg-4 d-none d-lg-block ">
                      <img src={login} alt="login" />
                    </div>
                    <div className="col-lg-7">
                      <div className="p-5 text-center">
                        <h1 className="h4 text-gray-900 ">
                          Create an Account!
                        </h1>
                      </div>
                      <form className="user">
                        <div className="form-group row">
                          <div className="col-sm-6 mb-3 mb-sm-0">
                            <label className="text-info font-weight-bold">
                              Firstname
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="text"
                              className="form-control form-control-user"
                              placeholder="First Name"
                              required
                              autoFocus
                            />
                          </div>

                          <div className="col-sm-6">
                            <label className="text-info font-weight-bold">
                              Lastname
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="text"
                              className="form-control form-control-user"
                              placeholder="Last Name"
                              required
                            />
                          </div>
                        </div>

                        <div className="form-group">
                          <label className="text-info font-weight-bold mr-3">
                            Profile Image
                          </label>
                          <input
                            type="file"
                            name="imageData"
                            className="btn btn-primary btn-icon-split"
                            aria-describedby="emailHelp"
                          />
                        </div>

                        <div className="form-group">
                          <label className="text-info font-weight-bold">
                            Email
                            <sup>
                              <span className="text-danger">*</span>
                            </sup>
                          </label>
                          <input
                            type="email"
                            className="form-control form-control-user"
                            placeholder="Email Address"
                            required
                          />
                        </div>
                        <div className="form-group row">
                          <div className="col-sm-6 mb-3 mb-sm-0">
                            <label className="text-info font-weight-bold">
                              Set Password
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="password"
                              className="form-control form-control-user"
                              placeholder="Set Password"
                              required
                            />
                          </div>
                          <div className="col-sm-6 mb-3 mb-sm-0">
                            <label className="text-info font-weight-bold">
                              Confirm Password
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="password"
                              className="form-control form-control-user"
                              placeholder="Confirm Password"
                              required
                            />
                          </div>
                        </div>

                        <div className="form-group row">
                          <div className="col-sm-6 mb-3 mb-sm-0">
                            <label className="text-info font-weight-bold">
                              Mobile
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="tel"
                              maxLength="10"
                              className="form-control form-control-user"
                              placeholder="Mobile"
                              required
                            />
                          </div>
                        </div>

                        <div className="form-group row">
                          <div className="col-sm-6 mb-3 mb-sm-0">
                            <label className="text-info font-weight-bold">
                              Address1
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="text"
                              className="form-control form-control-user"
                              placeholder="Address 1"
                              required
                            />
                          </div>
                          <div className="col-sm-6 mb-3 mb-sm-0">
                            <label className="text-info font-weight-bold">
                              Address2
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="text"
                              className="form-control form-control-user"
                              placeholder="Address 2"
                            />
                          </div>
                        </div>

                        <div className="form-group row">
                          <div className="col-sm-4 mb-3 mb-sm-0">
                            <label className="text-info font-weight-bold">
                              City
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="text"
                              className="form-control form-control-user"
                              placeholder="City"
                              required
                            />
                          </div>
                          <div className="col-sm-4">
                            <label className="text-info font-weight-bold">
                              State
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="text"
                              className="form-control form-control-user"
                              placeholder="State"
                              required
                            />
                          </div>
                          <div className="col-sm-4">
                            <label className="text-info font-weight-bold">
                              Pincode
                              <sup>
                                <span className="text-danger">*</span>
                              </sup>
                            </label>
                            <input
                              type="tel"
                              maxLength="6"
                              className="form-control form-control-user"
                              placeholder="Pincode"
                              required
                            />
                          </div>
                        </div>
                        <div className="form-group">
                          <label className="text-info font-weight-bold">
                            Role
                            <sup>
                              <span className="text-danger">*</span>
                            </sup>
                          </label>
                          <select className="form-control form-control-user">
                            <option className="collapse-item"></option>
                          </select>
                        </div>

                        <div className="experience">
                          <div className="form-group row">
                            <div className="col-sm-6 mb-3 mb-sm-0">
                              <input
                                type="text"
                                className="form-control form-control-user"
                                placeholder="Company name"
                                required
                              />
                            </div>
                            <div className="col-sm-6 mb-3 mb-sm-0">
                              <input
                                type="text"
                                className="form-control form-control-user"
                                placeholder="Name of the Role"
                                required
                              />
                            </div>
                          </div>
                          <div className="form-group row">
                            <div className="col-sm-6 mb-3 mb-sm-0">
                              <input
                                type="date"
                                className="form-control form-control-user"
                                placeholder="Starting year"
                                required
                              />
                            </div>
                            <div className="col-sm-6 mb-3 mb-sm-0">
                              <input
                                type="date"
                                className="form-control form-control-user"
                                placeholder="Ending year"
                                required
                              />
                            </div>
                          </div>
                        </div>
                        <div className="row">
                          <div className="col-md-6">
                            <input
                              type="submit"
                              value="Register Account"
                              className="btn btn-primary btn-user btn-block"
                            />
                          </div>
                          <div className="col-md-6">
                            <a className="btn btn-warning btn-user btn-block">
                              Cancel
                            </a>
                          </div>
                        </div>
                      </form>

                      <hr />
                      <div className="text-center mb-3">
                        <a className="small">Go to Dashboard page!</a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddStaffContainer;
