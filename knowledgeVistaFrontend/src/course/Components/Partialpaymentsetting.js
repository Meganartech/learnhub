import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const Partialpaymentsetting = ({
  enablechecked,
  setenablechecked,
  handleSubmit,
  setnextclick,
  setDurations,
  durations,
  installmentData,
  setInstallmentData,
  courseamount,
}) => {
  const [noOfInstallments, setNoOfInstallments] = useState(2);
  const [noofDuration, setnoofDuration] = useState(1);

  const navigate = useNavigate();
  // Calculate initial installment data on component mount or noOfInstallments change
  useEffect(() => {
    if (noOfInstallments >= 1) {
      let newInstallmentData = [];
      let newDurations = [];

      for (let i = 0; i < noOfInstallments; i++) {
        newInstallmentData.push({
          InstallmentNumber: `${i + 1}`,
          InstallmentAmount: courseamount / noOfInstallments,
          DurationInDays: i === 0 ? 0 : 15,
        });

        if (i < noOfInstallments - 1) {
          newDurations.push(15);
        }
      }

      setInstallmentData(newInstallmentData);
      setDurations(newDurations);
    } else {
      setInstallmentData([]);
      setDurations([]);
    }
  }, [noOfInstallments, courseamount]);

  const installmentChange = (e, index) => {
    const newInstallmentAmount = parseFloat(e.target.value);
    if (newInstallmentAmount > 0) {
      const updatedInstallmentData = [...installmentData];
      updatedInstallmentData[index].InstallmentAmount = newInstallmentAmount;

      // Recalculate remaining installments if needed
      if (index < noOfInstallments - 1) {
        let remainingAmount = courseamount; // Start with total course amount
        for (let i = 0; i <= index; i++) {
          // Subtract updated amounts of previous installments
          remainingAmount -= updatedInstallmentData[i].InstallmentAmount;
        }
        const remainingInstallments = noOfInstallments - index - 1;
        const remainingInstallmentAmount =
          remainingAmount / remainingInstallments;
        for (let i = index + 1; i < noOfInstallments; i++) {
          updatedInstallmentData[i].InstallmentAmount =
            remainingInstallmentAmount;
        }
      }

      setInstallmentData(updatedInstallmentData);
    }
  };

  const installmentnoChange = (e) => {
    const newNoOfInstallments = parseInt(e.target.value, 10);
    if (newNoOfInstallments >= 2) {
      setNoOfInstallments(newNoOfInstallments);
      setnoofDuration(newNoOfInstallments - 1);
    }
  };

  function getOrdinalSuffix(num) {
    const ones = num % 10;
    const tens = Math.floor(num / 10) % 10;
    if (tens === 1) {
      return num + "th";
    } else {
      switch (ones) {
        case 1:
          return num + "st";
        case 2:
          return num + "nd";
        case 3:
          return num + "rd";
        default:
          return num + "th";
      }
    }
  }
  return (
    <div className="row">
      <div className="col-12">
        <div className="navigateheaders">
          <div
            onClick={() => {
              setnextclick(false);
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
        <h2>Setting up a Course</h2>

        <hr />
        <h5>
          <input
            type="checkbox"
            className="m-4"
            name="check"
            checked={enablechecked}
            onChange={() => {
              setenablechecked(!enablechecked);
            }}
          />
          <h4 htmlFor="check" style={{ display: "inline" }}>
            Enable Partial Payment
          </h4>
        </h5>
        {enablechecked ? (
          <>
  <div className="row">
    <div className="col-md-6">
      <div className="form-group row">
        <label className="col-sm-4 col-form-label">Course Amount</label>
        <div className="col-sm-8">
          <input type="number" className="form-control" value={courseamount} />
        </div>
      </div>
    </div>

    <div className="col-md-6">
      <div className="form-group row">
        <label className="col-sm-4 col-form-label">No of Installments</label>
        <div className="col-sm-8">
          <input
            type="number"
            className="form-control"
            value={noOfInstallments}
            onChange={installmentnoChange}
          />
        </div>
      </div>
    </div>
  </div>

            <div className="row mt-3" style={{marginBottom:"10px",minHeight:"200px", maxHeight: "250px",overflow:"auto" }}>
              <div className="col-md-6">
                {installmentData.map((installment, index) => (
                  <div key={index}>
                    <div className="form-group row pt-2">
                      <label  className="col-sm-4 col-form-label"> installment{installment.InstallmentNumber}</label>
                      <div className="col-sm-8">
                      <input
                        type="number"
                          className="form-control"
                        value={installment.InstallmentAmount}
                        onChange={(e) => installmentChange(e, index)}
                      />
                      </div>
                    </div>
                  </div>
                ))}
              </div>
              <div className="pt-5 col-md-6">
                {durations.map((duration, index) => (
                  <div className="form-group row pt-2" key={index}>
                    <label  className="col-sm-4 col-form-label">
                      Duration for {getOrdinalSuffix(index + 2)} installment
                    </label>
                    <div className="col-sm-8">
                    <div style={{ display: "flex", alignItems: "center" }}>
                      <input
                        type="number"
                          className="form-control"
                        value={duration}
                        onChange={(e) => {
                          const updatedDurations = [...durations];
                          updatedDurations[index] = parseInt(
                            e.target.value,
                            10
                          );
                          if (e.target.value > 0) {
                            setDurations(updatedDurations);
                            const installmentdata = [...installmentData];
                            installmentdata[index + 1].DurationInDays =
                              parseInt(e.target.value, 10);
                          }
                        }}
                      />
                      <label style={{ marginLeft: "5px" }}>Days</label>
                    </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </>
        ) : (
          <>
            <div className="row">
    <div className="col-md-6">
      <div className="form-group row">
                <label  className="col-sm-4 col-form-label">Course Amount</label>
                <div className="col-sm-8">
                <input type="number" className="form-control" value={courseamount} />
                </div>
                </div>
              </div>
              <div className="col-md-6">
              <div className="form-group row">
                <label  className="col-sm-4 col-form-label"> No of Installments </label>
                <div className="col-sm-8">
                <input
                  type="number"
                  className="form-control"
                  value=" "
                  readOnly
                />
                </div>
              </div>
            </div>
            </div>
            <div className="row mt-3" style={{marginBottom:"10px",minHeight:"200px", maxHeight: "250px",overflow:"auto" }}>
            <div className="col-md-6">
                <div className="form-group row pt-2">
                  <label  className="col-sm-4 col-form-label"> Installment 1</label>
                  <div className="col-sm-8">
                  <input type="number" className="form-control" readOnly />
                </div>
                </div>
                <div className="form-group row pt-2">
                <label  className="col-sm-4 col-form-label"> Installment 2</label>
                  <div className="col-sm-8">
                  <input type="number" className="form-control" readOnly />
                </div>
                </div>
              </div>

              <div className="pt-5 col-md-6">
                <div className="form-group row pt-5">
                  <label  className="col-sm-4 col-form-label">Duration for 2 nd installment</label>
                  <div className="col-sm-8">
                  <div style={{ display: "flex", alignItems: "center" }}>
                    <input type="number" className="form-control" readOnly />
                    <label style={{ marginLeft: "5px" }}>Days</label>
                  </div>
                  </div>
                </div>
              </div>
            </div>
          </>
        )}
        <div className="cornerbtn">
          <button
            className="btn btn-secondary"
            onClick={() => {
              setnextclick(false);
            }}
          >
            cancel
          </button>
          <button
            className="btn btn-primary"
            onClick={(e) => {
              handleSubmit(e);
            }}
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );
};

export default Partialpaymentsetting;
