import React, { useState } from 'react'

const Paycheck = () => {
    const [amount, setamount] = useState('');

    const handleSubmit = (e)=>{
      e.preventDefault();
      if(amount === ""){
      alert("please enter amount");
      }else{
        var options = {
          key: "rzp_test_e2rQdIcoCi7CTR",
          key_secret:"KMuIcwYtSijRRwO5W3bQ1p4q",
          amount: amount *100,
          currency:"INR",
          name:"STARTUP_PROJECTS",
          description:"for testing purpose",
          handler: function(response){
            alert(response.razorpay_payment_id);
          },
          prefill: {
            name:"Velmurugan",
            email:"mvel1620r@gmail.com",
            contact:"7904425033"
          },
          notes:{
            address:"Razorpay Corporate office"
          },
          theme: {
            color:"#3399cc"
          }
        };
        var pay = new window.Razorpay(options);
        pay.open();
      }
    }
    return (
      <div>
         <div className="App">
       <h2>Razorpay Payment Integration Using React</h2>
       <br/>
       <input type="text"placeholder='Enter Amount'value={amount}onChange={(e)=>setamount(e.target.value)} />
       <br/><br/>
       <button onClick={handleSubmit}>submit</button>
      </div>
      </div>
    )
}

export default Paycheck
