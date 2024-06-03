import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Partialpaymentsetting = ({handleSubmit,setnextclick,setDurations,durations,installmentData,setInstallmentData ,courseamount}) => {
  const [noOfInstallments, setNoOfInstallments] = useState(2);
  const [noofDuration,setnoofDuration]=useState(1);
  
  const [enablechecked,setenablechecked]=useState(false);
  const navigate =useNavigate();
  // Calculate initial installment data on component mount or noOfInstallments change
  useEffect(() => {
    if (noOfInstallments >= 1) {
      const newInstallmentData = Array(noOfInstallments).fill(null).map((_, index) => ({
        name: `Installment ${index + 1}`,
        amount: courseamount / noOfInstallments,
      }));
      const newDurations = Array(noOfInstallments - 1).fill(15); 
     
      setDurations(newDurations);
      setInstallmentData(newInstallmentData);
      
    }
  }, [noOfInstallments, courseamount]);

  const installmentChange = (e, index) => {
    const newInstallmentAmount = parseFloat(e.target.value);
    if(newInstallmentAmount>0){
    const updatedInstallmentData = [...installmentData];
    updatedInstallmentData[index].amount = newInstallmentAmount;
    
    // Recalculate remaining installments if needed
    if (index < noOfInstallments - 1) {
      let remainingAmount = courseamount; // Start with total course amount
      for (let i = 0; i <= index; i++) { // Subtract updated amounts of previous installments
        remainingAmount -= updatedInstallmentData[i].amount;
      }
      const remainingInstallments = noOfInstallments - index - 1;
      const remainingInstallmentAmount = remainingAmount / remainingInstallments;
      for (let i = index + 1; i < noOfInstallments; i++) {
        updatedInstallmentData[i].amount = remainingInstallmentAmount;
      }
    }

    setInstallmentData(updatedInstallmentData);
}
  };

  const installmentnoChange = (e) => {
    
    const newNoOfInstallments = parseInt(e.target.value, 10);
    if (newNoOfInstallments >= 2) { 
      setNoOfInstallments(newNoOfInstallments);
      setnoofDuration(newNoOfInstallments-1);
    }
  };



  function getOrdinalSuffix(num) {
    const ones = num % 10;
    const tens = Math.floor(num / 10) % 10;
    if (tens === 1) {
      return num + 'th';
    } else {
      switch (ones) {
        case 1:
          return num + 'st';
        case 2:
          return num + 'nd';
        case 3:
          return num + 'rd';
        default:
          return num + 'th';
      }
    }
}

  return (

        
        <div>
        <h2>
          <span style={{ textDecoration: 'underline' }}>Partial Payment Settings</span>
        </h2>
        <h5>
          <input type="checkbox" className="m-4" name='check' onChange={()=>{setenablechecked(!enablechecked)}}/>
          <p htmlFor='check' style={{ display: "inline" }}>
            Enable Partial Payment
          </p>
        </h5>
        {enablechecked?(<>
        <div className='mainform2'>
          <div className='inputgrp2'>
            <label>Course Amount</label>
            <span>:</span>
            <input type='number' value={courseamount} />
          </div>
         
          <div className='inputgrp2'>
            <label> No of Installments </label>
            <span>:</span>
            <input type='number' value={noOfInstallments} onChange={installmentnoChange} />
          </div>
        </div>
        <div className='mainform2 mt-3' style={{ height: '320px' }}>
            <div>
            {installmentData.map((installment, index) => (
            <div key={index}>
              <div className='inputgrp2 pt-2'>
                <label> {installment.name}</label>
                <span>:</span>
                <input
                  type='number'
                  value={installment.amount}
                  onChange={(e) => installmentChange(e, index)}
                />
              </div>
                </div>))}
                </div>
                <div>
                {durations.map((duration, index) => (
          <div className='inputgrp2 pt-2' key={index}>
            <label>
              Duration for {getOrdinalSuffix(index + 2)} installment
            </label>
            <span>:</span>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <input
                type='number'
                value={duration}
                onChange={(e) => {
                  const updatedDurations = [...durations];
                  updatedDurations[index] = parseInt(e.target.value, 10);
                  if(e.target.value>0){
                  setDurations(updatedDurations);
                }
                }}
              />
              <label style={{ marginLeft: '5px' }}>Days</label>
            </div>
          </div>
        ))}
                </div>

            
        </div></>
        ):(<>
        <div className='mainform2'>
          <div className='inputgrp2'>
            <label>Course Amount</label>
            <span>:</span>
            <input type='number' value={courseamount} />
          </div>
         
          <div className='inputgrp2'>
            <label> No of Installments </label>
            <span>:</span>
            <input type='number' className='disabledbox'    value=" "  readOnly/>
          </div>
        </div>
        <div className='mainform2 mt-3' style={{ height: '320px' }}>
            
            <div>
              <div className='inputgrp2 pt-2'>
                <label> Installment 1</label>
                <span>:</span>
                <input
                  type='number'
                  className='disabledbox'
                  readOnly
                />
              </div>
              <div className='inputgrp2 pt-2'>
                <label> Installment 2</label>
                <span>:</span>
                <input
                  type='number'
                  className='disabledbox'
                  readOnly
                />
              </div>
                </div>
                
                <div>
          <div className='inputgrp2 pt-2' >
            <label>
              Duration for 2 nd installment
            </label>
            <span>:</span>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <input
                type='number'
                className='disabledbox'
                readOnly
              />
              <label style={{ marginLeft: '5px' }}>Days</label>
            </div>
          </div>
      
                </div>

            
        </div>
        
        </>)}
        <div className='atbtndiv'>
          <button className='btn btn-primary' onClick={()=>{setnextclick(false)}}>cancel</button>
          <div></div>
          <button className="btn btn-primary" onClick={(e)=>{handleSubmit(e)}}>Save</button>
        </div>
        </div>
    
  );
};

export default Partialpaymentsetting;
