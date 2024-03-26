import React, { useEffect, useState } from 'react';

const MyCertificateList = () => {
    const [myCertificates, setMyCertificates] = useState([]);

    useEffect(() => {
        const fetchCertificates = async () => {
            try {
                const token = sessionStorage.getItem("token");
                const certificateData = await fetch("http://localhost:8080/certificate/getAllCertificate", {
                    method: "GET",
                    headers: {
                        Authorization: token
                    }
                });
                if (certificateData.ok) {
                    const certificateJson = await certificateData.json();
                    console.log(certificateJson)
                    setMyCertificates(certificateJson);
                }
            } catch (error) {
                console.error("Error fetching certificates:", error);
            }
        };
        fetchCertificates();
    }, []);

    return (
        <div className='contentbackground'>
            <div className='contentinner'>
        <div className="mt-4">
            <h2>My Certificates</h2>
            <table className="table table-hover">
                <thead>
                    <tr>
                        <th scope="col" className="text-dark">S.No</th>
                        <th scope="col" className="text-dark">Course Name</th>
                        <th scope="col" className="text-dark">Percentage</th>
                        <th scope="col" className="text-dark">Test Date</th>
                        <th scope="col" className="text-dark">Certificate</th>
                    </tr>
                </thead>
                <tbody>
                    {myCertificates.map((certificate, index) => (
                        <tr key={index}>
                            <td>{index + 1}</td>
                            <td>{certificate.course}</td>
                            <td>{certificate.percentage}</td>
                            <td>{certificate.testDate}</td>
                            <td><a href={`/template/${certificate.activityId}`}><i className="fa-solid fa-download text-primary"></i></a></td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
        </div>
        </div>
    );
};

export default MyCertificateList;
