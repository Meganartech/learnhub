    // //http://localhost:8080/lessons/1713440593842_Godzilla vs Kong (2021) Single Part (640x360).mp4/videofile
    // //http://localhost:8080/lessons/1713438821894_vdo.mp4/videofile

    // import React, { useState, useRef, useEffect } from 'react';
    // import ReactPlayer from 'react-player';
    
    // const VideoPlayer = () => {
    //     const [videoUrl, setVideoUrl] = useState('');
    //     const playerRef = useRef(null);
    
    //     // Set the initial video URL
    //     useEffect(() => {
    //         setVideoUrl(`http://localhost:8080/lessons/1713440593842_Godzilla vs Kong (2021) Single Part (640x360).mp4/videofile`);
        
    //     }, []);
    
    //     // Handler for onProgress event
    //     const handleOnProgress = (progress) => {
    //         // Check if progress and its buffered and played properties exist
    //         if (progress && progress.buffered && progress.played) {
    //             // Calculate buffered amount
    //             const bufferedEnd = progress.buffered.end && typeof progress.buffered.end === 'function' ? progress.buffered.end() : 0;
    //             const playedEnd = progress.played.end && typeof progress.played.end === 'function' ? progress.played.end() : 0;
        
    //             // Calculate buffered amount
    //             const bufferedAmount = bufferedEnd - playedEnd;
        
    //             // If the buffered amount is low, request more data
    //             if (bufferedAmount < 0.1) {
    //                 // Determine the range to request based on the current played end and a buffer size
    //                 const rangeStart = Math.floor(playedEnd * playerRef.current.getDuration());
    //                 const rangeSize = 5 * 1024 * 1024; // 5 MB buffer size
    //                 const rangeEnd = Math.min(rangeStart + rangeSize, playerRef.current.getDuration());
        
    //                 // Create a new URL with range query parameters
    //                 const rangeUrl = `${videoUrl}?rangeStart=${rangeStart}&rangeEnd=${rangeEnd}`;
        
    //                 // Set the new video URL to request the specified range from the server
    //                 setVideoUrl(rangeUrl);
    //             }
    //         }
    //     };
        
        
    
    //     // Handler for onSeek event
      
    //     const handleOnSeek = (progress) => {
    //         if (progress.buffered && progress.buffered.length > 0) {
    //             const bufferedEnd = progress.buffered.end(0);
    //             // Do something with bufferedEnd
    //         } else {
    //             console.error("Buffered data is not available.");
    //         }
    //     };
        
    //     return (
    //         <div>
    //             <ReactPlayer
    //                 ref={playerRef}
    //                 url={videoUrl}
    //                 controls
    //                 width="500px"
    //                 height="500px"
    //                 onProgress={handleOnProgress}
    //                 onSeek={handleOnSeek}
    //             progressInterval={1000} // Update progress every 1000ms
    //             config={{
    //                 file: {
    //                     attributes: {
    //                         controlsList: 'nodownload'
    //                     }
    //                 }
    //             }}
                    
    //             />
    //         </div>
    //     );
    // };
    
    // export default VideoPlayer;


    //https://stackoverflow.com/questions/53209646/react-player-and-authorization-required-problem
      
    import React, { useState, useRef, useEffect } from 'react';
    import ReactPlayer from 'react-player';
    
    const VideoPlayer = () => {
        const playerRef = useRef(null);
        const token=sessionStorage.getItem("token")
        const lessId=12
        const courseId=3;
       // const [videoUrl, setVideoUrl] = useState(`http://localhost:8080/lessons/1713440593842_Godzilla vs Kong (2021) Single Part (640x360).mp4/videofile/${token}`);
       const [videoUrl, setVideoUrl] = useState();
       
      //http://localhost:8080/lessons/getvideoByid/${lessId}/${courseId}/${token}
      useEffect(() => {
        
          try {
           
            fetchvideo();
               
               } catch (error) {
             console.log(error)
            }
          
      
    
      }, []);
      const fetchvideo =()=>{
        setVideoUrl(`http://localhost:8080/lessons/getvideoByid/${lessId}/${courseId}/${token}`)
            
      }
    
        const handleOnProgress = (progress) => {
                    // Check if progress and its buffered and played properties exist
                    if (progress && progress.buffered && progress.played) {
                        // Calculate buffered amount
                        const bufferedEnd = progress.buffered.end && typeof progress.buffered.end === 'function' ? progress.buffered.end() : 0;
                        const playedEnd = progress.played.end && typeof progress.played.end === 'function' ? progress.played.end() : 0;
                
                        // Calculate buffered amount
                        const bufferedAmount = bufferedEnd - playedEnd;
                
                        // If the buffered amount is low, request more data
                        if (bufferedAmount < 0.1) {
                            // Determine the range to request based on the current played end and a buffer size
                            const rangeStart = Math.floor(playedEnd * playerRef.current.getDuration());
                            const rangeSize = 5 * 1024 * 1024; // 5 MB buffer size
                            const rangeEnd = Math.min(rangeStart + rangeSize, playerRef.current.getDuration());
                
                            // Create a new URL with range query parameters
                            const rangeUrl = `${videoUrl}?rangeStart=${rangeStart}&rangeEnd=${rangeEnd}`;
                
                            // Set the new video URL to request the specified range from the server
                            setVideoUrl(rangeUrl);
                        }
                    }
                };
                
                
            
            //     // Handler for onSeek event
              
                const handleOnSeek = (progress) => {
                    if (progress.buffered && progress.buffered.length > 0) {
                        const bufferedEnd = progress.buffered.end(0);
                        // Do something with bufferedEnd
                    } else {
                        console.error("Buffered data is not available.");
                    }
                };

        
        return (
            <div>
                 <ReactPlayer
              //  ref={playerRef}
                url={videoUrl}
                controls
                width="500px"
                height="500px"
                onProgress={handleOnProgress}
                onSeek={handleOnSeek}
                progressInterval={1000}
                config={{
                    file: {
                      attributes: {
                        controlsList: 'nodownload'
                      }
                    }
                  }}
            />
            </div>
        );
    };
    
    export default VideoPlayer;
    