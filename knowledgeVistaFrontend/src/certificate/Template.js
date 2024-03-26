import React, {useEffect, useRef, useState } from 'react';
import { useReactToPrint } from 'react-to-print';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';

import "./certificate.css";
import { useParams } from 'react-router-dom';

const Template = () => {
  const  {activityId}=useParams();
const [userdata,setuserdata]=useState("");
    const [isnotFound,setisnotFound]=useState();
   
const[sign,setsign]=useState();

    const [defaultcerti,setdefaultcerti]=useState({
        institutionName: '',
        ownerName: '',
        qualification: '',
        address: '',
        authorizedSign: null,
      
      });
      useEffect(() => {
        const token = sessionStorage.getItem("token");
        const fetchCertificate = async () => {
            try {
                const certificatedata = await fetch("http://localhost:8080/certificate/viewAll");
                if (certificatedata.ok) {
                    const certificateJson = await certificatedata.json();
                    setsign(`data:image/jpeg;base64,${certificateJson.authorizedSign}`);
                    setdefaultcerti(certificateJson);
                    setisnotFound(false); // Reset isNotFound to false
                
                const userdata = await fetch(`http://localhost:8080/certificate/getByActivityId/${activityId}`, {
                    method: "GET",
                    headers: {
                        Authorization: token
                    }
                });
                if (userdata.ok) {
                    const userjson = await userdata.json();
                    setuserdata(userjson);
                }}
                else{
                    setisnotFound(true);
                }
            } catch (error) {
                console.error("Error fetching certificate:", error);
            }
        };
        fetchCertificate();
    }, [activityId]);

   

    const componentPdfRef = useRef();
    const componentprintRef=useRef();

    const handlePrint = useReactToPrint({
        content: () => componentprintRef.current,
        documentTitle: "certificate",
    });

    const handleDownloadPdf = () => {
        const input = componentPdfRef.current;

        html2canvas(input, {
            backgroundColor: '#ffffff' // Set the desired background color
        }).then((canvas) => {
            const pdf = new jsPDF('p', 'mm', 'a4');
            const imgData = canvas.toDataURL('image/png');
            const imgWidth = 210; // A4 size width
            const imgHeight = (canvas.height * imgWidth) / canvas.width; // Height in A4 size paper
            pdf.addImage(imgData, 'PNG', 0, 0, imgWidth, imgHeight);
            pdf.save('certificate.pdf');
        });
    };

    return (
        <div className='contentbackground'>
            <div className='contentinner'>
            {isnotFound ?(
             <div className='notfound'>
             <h2>Some Error occured please try again later</h2>
             <a href="#" onClick={() => window.history.back()} className='btn btn-primary'>Go Back</a>
             
           </div>
           ):(
                <div className='wrapper'>
                    <div>
                    <div className="certificate"ref={componentprintRef} >
                        <div className='inner' ref={componentPdfRef}>
                            <h3 style={{ color: '#35477d' }}>Certificate of Completion</h3>
                            <p>This is to certify that</p>
                            <h4 style={{ color: '#35477d' }}>{userdata.user}</h4>
                            <p>has successfully completed the online course of {userdata.course} on {userdata.testDate}</p>
                            <div className='centerimg'><img src={sign} width="100px" height="50px" alt="Signature" /></div>
                            <p>{defaultcerti.ownerName}</p>
                            <p>{defaultcerti.qualification}</p>
                            <p>{defaultcerti.address}</p>
                        </div>
                       
                    </div>
                    <div className='certibtn '>
                          <button onClick={handlePrint} className='btn-primary btn'><i className="fa-solid fa-print"></i> Print</button>
                          <button onClick={handleDownloadPdf} className='btn-primary btn'> <i className="fa-solid fa-download"></i> Download PDF</button>
                    </div>
                    </div>
                </div>)}
           </div>
                
        </div>
    );
};

export default Template;