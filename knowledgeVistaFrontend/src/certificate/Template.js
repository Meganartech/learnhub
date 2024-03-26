import React, { useEffect, useRef } from 'react';
import { useReactToPrint } from 'react-to-print';
import { jsPDF } from 'jspdf';
import sign from "./sign.png"
import "./certificate.css";

const Template = () => {
    


    // useEffect(()=>{
    //     const fetchcertificate=async()=>{
    //         try{
    //             const certificatedata= await fetch()
    //         }
    //     }
    // })
    // const username = "ramesh";
    // const courseName = "React";
    // const markPercentage = "100%";
    // const institutionName = "KnowledgeVista.";
    // const institutionOwnerName = "Ramesh Babu";
    // const institutionAddress = "2a/4 Sinarathoppu2nd st southgate.";
    // const Qualification="CEO knowledgevista groups";
     const componentPdfRef = useRef();

    const currentDate = new Date().toLocaleDateString(); // Get current date in locale format

    // const generatePdf = () => {
    //     const pdf = new jsPDF();
    //     const pdfWidth = pdf.internal.pageSize.getWidth();
    //     const pdfHeight = pdf.internal.pageSize.getHeight();

    //     // Get the HTML content from the component
    //     const componentHtml = componentPdfRef.current;

    //     // Add the HTML content to the PDF
    //     pdf.html(componentHtml, {
    //         callback: () => {
    //             // Save the PDF
    //             pdf.save("certificate.pdf");
    //         },
    //         x: 0,
    //         y: 0,
    //         width: pdfWidth,
    //         height: pdfHeight
    //     });
    // };
    
    // const generatePdf = () => {
    //     const pdf = new jsPDF('p', 'px', 'letter');
    //      const componentHtml = componentPdfRef.current.innerHTML;
    //     const pdfWidth = 400;
    //     const pdfHeight = 200;

    //     pdf.html(componentHtml, {
    //         callback: () => {
    //             pdf.save("certificate.pdf");
    //         },
    //         orientation: 'p',
    //         unit: 'px',
    //         format: [pdfWidth, pdfHeight],
    //         x: 0,
    //         y: 0
    //     });
    // };
    
    
    

    const handlePrint = useReactToPrint({
        content: () => componentPdfRef.current,
        documentTitle: "certificate",
       
    });

    return (
        <div className='bg p-5'>
            <div className='viewcontent p-4'>
            <div ref={componentPdfRef}>
                <div className="certificate">
                    <div className='inner'>
                       {/* <h1 style={{ fontFamily: 'Montserrat', color: '#35477d' }}>Certificate of Completion</h1>
                        <p style={{ fontFamily: 'Roboto' }}>This is to certify that</p>
                        <h2 style={{ fontFamily: 'Montserrat', color: '#35477d' }}>{username}</h2>
                        <p style={{ fontFamily: 'Roboto' }}>has successfully completed the online course of {courseName} on {currentDate}</p>
                        <img src={sign} width="200px" height="90px" alt="Signature" />
                        <h6><b>{institutionOwnerName}</b></h6>
                        <p>{Qualification}</p>
    <p>{institutionAddress}</p>*/}
                    </div>
                </div>
            </div>
            <button onClick={handlePrint} className='btn-primary btn'>Print</button>
            {/* <button onClick={generatePdf} className='btn-primary btn'>Download PDF</button> */}
            </div>
        </div>
    );
};

export default Template;
