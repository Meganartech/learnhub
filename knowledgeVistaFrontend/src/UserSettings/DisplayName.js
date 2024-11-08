import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import baseUrl from "../api/utils";
import axios from "axios";
const DisplayName = () => {
  const [initialsave, setinitialsave] = useState(false);
  const MySwal = withReactContent(Swal);
  const token = sessionStorage.getItem("token");
  const [isnotFound, setisnotFound] = useState(false);
  const navigate = useNavigate();
  const[errors,seterrors]=useState({
    admin_name:"",
    trainer_name: "",
     student_name: "",
  })
  const [displayname,setdisplayname]=useState({
  admin_name:"",
  trainer_name: "",
   student_name: "",
   isActive:true
  })
  const[defaultname,setdefaultname]=useState({
    id:"",
    insitution:"",
    admin_name:"",
    trainer_name: "",
     student_name: "",
     isActive:true
  })
  useEffect(() => {
    if(token){
      const fetchDisplayNameSettings = async () => {
        try {
          const response = await axios.get(`${baseUrl}/get/displayName`, {
            headers: {
              "Authorization": token
            }
          });
          if (response.status === 200) {
            const data = response.data;
            console.log(data)
            setdisplayname(data);
            setdefaultname(data);
            
        } 
        } catch (error) {
          if (error.response) {
            if (error.response.status === 404) {
              setisnotFound(true);
              console.log("notfound",isnotFound)
              setinitialsave(true);
            } else if (error.response.status === 401) {
              window.location.href = "/unauthorized";
            }else{
              throw error
            }
          }
        }
      };
      fetchDisplayNameSettings();
    }
    }, []); 
  const save =async (e) => {
    e.preventDefault();
    if(initialsave){
try{
    const response=await axios.post(`${baseUrl}/post/displayname`, displayname, {
        headers: {
          'Authorization': token
        }
      })
      if (response.status === 200) {
        sessionStorage.setItem("displayname", JSON.stringify(displayname));
        MySwal.fire({
          title: "Saved !",
          text: "Role Details Saved Sucessfully" ,
          icon: "success",
          confirmButtonText: "OK",
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.reload();
          }   });
        setisnotFound(false)
      } 
}catch(error){
  // MySwal.fire({
  //   icon: 'error',
  //   title: 'Some Error Occurred',
  //   text: "error occured"
  // });
  if (error.response.status === 404) {
    console.log("notfound")
  } else if (error.response.status === 401) {
    window.location.href = "/unauthorized";
  }else{
    throw error
  }
}  
  }else{ 
    if(displayname.id){
    axios.patch(`${baseUrl}/edit/displayname`,displayname ,{
    headers:{
      "Authorization":token
      }
  })
  .then(response => {
    if (response.status=== 200) {
      sessionStorage.setItem("displayname", JSON.stringify(displayname));
      MySwal.fire({
        title: "Updated",
        text: "Role Details Saved Sucessfully" ,
        icon: "success",
        confirmButtonText: "OK",
      }).then((result) => {
        if (result.isConfirmed) {
          window.location.reload();
        }   });
      setisnotFound(false)
    } 
  })
  .catch(error => {
  //  if(error.response.status===401){
  //   window.location.href="/unauthorized"
  //  } else{
    // MySwal.fire({
    //   icon: 'error',
    //   title: 'Some Error Occurred',
    //   text: error.data
    // });
    if (error.response.status === 404) {
      console.log("notfound")
    } else if (error.response.status === 401) {
      window.location.href = "/unauthorized";
    }else{
      throw error
    }
  });
    
  }

}
    
  };
const handleInputsChange=(e)=>{
const{name,value}=e.target
setdisplayname((prev)=>({
  ...prev,
  [name]:value
}))
}

const getinputs=(
    <div>
      <div className='navigateheaders'>
    <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
    <div></div>
    <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
    </div>
    <div className='innerFrameforset '>
 <h2 className='mb-5'style={{ textDecoration: "underline" }}>Role Display Name</h2>
 <div className='formgroup pt-4' >
 <div className='inputgrp'>
           <label htmlFor='admin_name'> Admin Name<span className="text-danger">*</span></label>
           <span>:</span>
           <div>
           <input
             id='admin_name'
             name='admin_name'
             placeholder='Admin Name'
             value={displayname.admin_name}
             className={`form-control .form-control-sm  ${errors.admin_name && 'is-invalid'}`}
             onChange={handleInputsChange}
           />
           <div className="invalid-feedback">
             {errors.admin_name}
           </div>
           </div>
         </div>

         <div className='inputgrp'>
           <label htmlFor='trainer_name'> Trainer Name<span className="text-danger">*</span></label>
           <span>:</span>
           <div>
           <input
             id='trainer_name'
             name='trainer_name'
             placeholder='Trainer Name'
             value={displayname.trainer_name}
             className={`form-control .form-control-sm  ${errors.trainer_name && 'is-invalid'}`}
             onChange={handleInputsChange}
           />
           <div className="invalid-feedback">
             {errors.trainer_name}
           </div>
           </div>
         </div>

         <div className='inputgrp'>
           <label htmlFor='student_name'> Student Name<span className="text-danger">*</span></label>
           <span>:</span>
           <div>
           <input
             id='student_name'
             name='student_name'
             placeholder='Student Name'
             value={displayname.student_name}
             className={`form-control .form-control-sm  ${errors.student_name && 'is-invalid'}`}
             onChange={handleInputsChange}
           />
           <div className="invalid-feedback">
             {errors.student_name}
           </div>
           </div>
         </div>
 </div>
 <div className='btngrp'>
       <button className='btn btn-primary' 
       onClick={save}
        >
         Save</button>
     </div>
     </div>
    </div>
    );
    const Edit=(e)=>{
      e.preventDefault();
      setisnotFound(true);
    }
  const defaultinputs=(  <div>
    <div className='navigateheaders'>
     <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-arrow-left"></i></div>
     <div></div>
     <div onClick={()=>{navigate(-1)}}><i className="fa-solid fa-xmark"></i></div>
     </div>
     <div className='innerFrameforset '>
     <h2 className='mb-5'style={{ textDecoration: "underline" }}>Role Display Name</h2>
     <div className='formgroup pt-4' >
         <div className='inputgrp'>
           <label htmlFor='admin_name'>Admin Name <span className="text-danger">*</span></label>
           <span>:</span> 
           <input
             id='admin_name'
             placeholder='Admin Name'
             value={defaultname.admin_name}
             readOnly
            className='disabledbox'
           />
         </div>
         <div className='inputgrp'>
           <label htmlFor='trainer_name'>Trainer Name <span className="text-danger">*</span></label>
           <span>:</span>          
           <input
             id='trainer_name'
             placeholder='Trainer Name'
             value={defaultname.trainer_name}
             readOnly
            className='disabledbox'
           />
         </div>
         <div className='inputgrp'>
           <label htmlFor='student_name'>Student Name<span className="text-danger">*</span></label>
           <span>:</span>
            <input
             id='student_name'
             placeholder='Student Name'
             className='disabledbox'
             readOnly
             value={defaultname.student_name}
           />
         </div> 
     </div>
     <div className='btngrp' >
       <button className='btn btn-primary' onClick={Edit}>Edit</button>
     </div>
     
 </div>
 </div>
 )
  return (
    <div className="contentbackground">
      <div className="contentinner">
      {isnotFound ? getinputs :defaultinputs }
      </div>
    </div>
  );
};

export default DisplayName;
