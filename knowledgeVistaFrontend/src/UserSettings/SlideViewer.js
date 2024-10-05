import React, { useState, useEffect } from "react";
import axios from "axios";
import baseUrl from "../api/utils";
import { useLocation, useNavigate } from "react-router-dom";

const SlideViewer = () => {
  const token = sessionStorage.getItem("token");
  const location = useLocation();
  const [currentSlide, setCurrentSlide] = useState(1);
  const [miniatures, setMiniatures] = useState([]);
  const navigate = useNavigate();
  const { doc, lessonId } = location.state || {};
  const [actualwidth,setactualwidth]=useState("")
  const [slideImages, setSlideImages] = useState([]); // To store all slide images
  const [totalSlides, setTotalSlides] = useState(0); // To store total slides count
  const [width, setWidth] = useState(null); // Initial width dynamically set
  const [aspectRatio, setAspectRatio] = useState(1); // Aspect ratio of the image
  const [zoomInputValue, setZoomInputValue] = useState("100%");

  // Zoom in functionality
  const zoomIn = () => {
    setZoomInputValue((prevValue) => {
      const numericValue = parseInt(prevValue.replace("%", ""));
      const newZoom = Math.min(numericValue + 10, 500); // Max zoom 500%
      console.log("newzoom",newZoom)
      console.log("calculated",(newZoom / 100) * actualwidth);
      setWidth((newZoom / 100) * actualwidth); // Adjust width based on new zoom level
      console.log("actual",actualwidth)
      console.log("width",width)
      return `${newZoom}%`;
    });
  };

  // Zoom out functionality
  const zoomOut = () => {
    setZoomInputValue((prevValue) => {
      const numericValue = parseInt(prevValue.replace("%", ""));
      const newZoom = Math.max(numericValue - 10, 10); // Min zoom 10%
      console.log("newzoom",newZoom)
      console.log("calculated",(newZoom / 100) * actualwidth);
      setWidth((newZoom / 100) * actualwidth); // Adjust width based on new zoom level
      console.log("actual",actualwidth)
      console.log("width",width)
      return `${newZoom}%`;
    });
  };
  

  const handleZoomChange = (event) => {
    setZoomInputValue(event.target.value);
    const inputValue = event.target.value.replace('%', '');
    
    const numericValue = Number(inputValue);
    if (!isNaN(numericValue)) {
      const newZoom = Math.max(numericValue - 10, 10); // Min zoom 10%
      setWidth((newZoom / 100) * actualwidth); // Adjust width
     // setZoomInputValue(`${newZoom}%`);
    }
  };

  // Clear input value on focus
  const handleZoomInputFocus = () => {
    setZoomInputValue(""); // Clear input value
  };

  // Fetch slide image by slide number
  const fetchSlideImage = async (slideNumber) => {
    try {
      const response = await axios.get(
        `${baseUrl}/slide/${doc.documentPath}/${slideNumber}`,
        {
          responseType: "json",
          headers: {
            Authorization: token,
          },
        }
      );

      const { image, totalSlides: fetchedTotalSlides } = response.data;
      const imageUrl = `data:image/png;base64,${image}`;

      if (totalSlides === 0 && fetchedTotalSlides) {
        setTotalSlides(fetchedTotalSlides);
      }

      setSlideImages((prevImages) => {
        const updatedImages = [...prevImages];
        updatedImages[slideNumber - 1] = imageUrl;
        return updatedImages;
      });
    } catch (error) {
      console.error("Error fetching slide image:", error);
    }
  };

  // Fetch all slides
  useEffect(() => {
  
    const fetchAllSlides = async () => {
      for (let i = 1; i <= totalSlides; i++) {
        await fetchSlideImage(i);
      }
    };

    if (totalSlides > 0) {
      fetchAllSlides();
    }
  }, [totalSlides]);

  // Fetch initial slide
  useEffect(() => {
    fetchSlideImage(1);
  }, []);

  // Fetch miniatures
  useEffect(() => {
    const fetchMinis = async () => {
      try {
        const miniResponse = await axios.get(
          `${baseUrl}/getmini/${lessonId}/${doc.id}`,
          {
            headers: {
              Authorization: token,
            },
          }
        );
        setMiniatures(miniResponse.data);
      } catch (error) {
        console.error("Error fetching miniatures:", error);
      }
    };

    if (lessonId && doc && doc.id) {
      fetchMinis();
    }
  }, [lessonId, doc]);

  // Handle scroll event to track current slide
  const handleScroll = (event) => {
    const container = event.target;
    const scrollPosition = container.scrollTop;
    const slideHeight = container.scrollHeight / totalSlides;

    // Calculate the current slide based on the scroll position
    const newCurrentSlide = Math.ceil(scrollPosition / slideHeight);
    if (newCurrentSlide < totalSlides) {
      setCurrentSlide(newCurrentSlide + 1);
    }
    if (currentSlide <= totalSlides) {
      const activemini = document.querySelector(".activemini");
      activemini.scrollIntoView({
        behavior: "smooth",
        block: "nearest",
        inline: "start",
      });
    }
  };

  // Scroll to the desired slide
  const goToSlide = (slideNumber) => {
    const slideContainer = document.querySelector(".slider_div");
    const slideHeight = slideContainer.scrollHeight / totalSlides;
    slideContainer.scrollTo({
      top: (slideNumber - 1) * slideHeight,
      behavior: "smooth",
    });
  };

  // Handle image load to get its natural dimensions and calculate the aspect ratio
  const handleImageLoad = (event) => {
    const { naturalWidth, naturalHeight } = event.target;
    setactualwidth(naturalWidth)
    setAspectRatio(naturalWidth / naturalHeight); // Calculate aspect ratio
    setWidth(naturalWidth); // Set initial width based on natural dimensions
  };

  return (
    <div className="contentbackground">
      <div className="contentinner">
        <div className="navigateheaders">
          <div onClick={() => navigate(-1)}>
            <i className="fa-solid fa-arrow-left"></i>
          </div>
          <div></div>
          <div onClick={() => navigate("/dashboard/course")}>
            <i className="fa-solid fa-xmark"></i>
          </div>
        </div>
        <div className="wrapppt">
          <div className="headdocu">
            <div>{doc?.documentName}</div>
            <div className="pageshow">
              <span className="pageshowchild">
                <span className="currentslide">{currentSlide}</span> / {totalSlides}
              </span>
              <span className="p-1">
                <i onClick={zoomOut} className="fa-solid fa-minus zoombtn"></i>
                <input
                  type="text"
                  className="m-1 currentslide"
                  value={zoomInputValue}
                  onChange={handleZoomChange}
                  onFocus={handleZoomInputFocus} // Clear input value on focus
                  min={10}
                  max={500}
                />
                <i onClick={zoomIn} className="fa-solid fa-plus zoombtn"></i>
              </span>
            </div>
          </div>
          <div className="miniandoriginal">
            <div className="text-center outermini">
              {miniatures &&
                miniatures.length > 0 &&
                miniatures.map((mini, index) => (
                  <div
                    key={index}
                    className="miniimg"
                    onClick={() => goToSlide(mini.pageNumber)} // Scroll to the slide
                  >
                    <img
                      className={`${currentSlide === mini.pageNumber ? "activemini" : ""}`}
                      src={`data:image/jpeg;base64,${mini.miniatureContent}`}
                      
                    />
                    <p>{mini.pageNumber}</p>
                  </div>
                ))}
            </div>
            <div
              className="slider_div"
              style={{ backgroundColor: "white", overflowY: "scroll" }}
              onScroll={handleScroll} // Add scroll event handler
            >
              <div
                style={{
                  width: `${width}px`,
                  height: `${width / aspectRatio}px`, // Maintain aspect ratio
                  transition: "height 0.3s ease, width 0.3s ease",
                  margin: "20px auto",
                  position: "relative",
                  top: "20px",
                }}
              >
                {slideImages.map((image, index) =>
                  image ? (
                    <img
                      key={index}
                      src={image}
                      className="mt-2"
                      style={{ width: "100%", height: "100%" }}
                      alt={`Slide ${index + 1}`}
                      onLoad={handleImageLoad}
                    />
                  ) : (
                    <div key={index} style={{ width: "100%", height: "100%", backgroundColor: "lightgrey" }}>
                      Loading...
                    </div>
                  )
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SlideViewer;
