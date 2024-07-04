import React, { useState } from 'react'
import { Outlet } from 'react-router-dom'
import SlideBar from './SlideBar'
import NavBar from './NavBar'
import Footer from './Footer'

const Layout = ({searchQuery,handleSearchChange,setSearchQuery}) => {
  const [showSidebar,setShowSidebar]=useState(false); 
  const userRole = sessionStorage.getItem("role");
  const handleSidebarToggle = () => {
    setShowSidebar(!showSidebar);
  };
  const [activeLink, setActiveLink] =  useState(localStorage.getItem('activeLink') ||(userRole==="ADMIN"?"admin/dashboard":"/dashboard/course"));
  return (
    <div >
            <div id="wrapper">          
              
                <div  className={showSidebar?" displayit":" hide"}>
                <SlideBar
               
                activeLink={activeLink}
                setActiveLink={setActiveLink}
                />
                 
               
                </div>   
              <div id="content-wrapper">
                <div id="content">
                
                    <NavBar  
                      showSidebar={showSidebar}
                    handleSidebarToggle={handleSidebarToggle}
                activeLink={activeLink}searchQuery={searchQuery}
                 handleSearchChange={handleSearchChange} 
                 setSearchQuery={setSearchQuery}/>

                
                 <Outlet/>
                
                    </div>
                   
              </div>
              
            </div>
            <Footer/>
          </div>

  )
}

 export default Layout

// import React, { useState } from 'react'
// import { Outlet } from 'react-router-dom'
// import SlideBar from './SlideBar'
// import NavBar from './NavBar'
// import Footer from './Footer'

// const Layout = ({searchQuery,handleSearchChange,setSearchQuery}) => {
//   const [showSidebar,setShowSidebar]=useState(false); 
//   const userRole = sessionStorage.getItem("role");
//   const handleSidebarToggle = () => {
//     setShowSidebar(!showSidebar);
//   };
//   const [activeLink, setActiveLink] =  useState(localStorage.getItem('activeLink') ||(userRole==="ADMIN"?"admin/dashboard":"/dashboard/course"));
//   return (
//     <div >
//             <div id="wrappernav">          
//             <NavBar  
//                       showSidebar={showSidebar}
//                     handleSidebarToggle={handleSidebarToggle}
//                 activeLink={activeLink}searchQuery={searchQuery}
//                  handleSearchChange={handleSearchChange} 
//                  setSearchQuery={setSearchQuery}/>

                
                
//               <div id="wrappercenter">
               
//                 <div  className={showSidebar?" displayit":" hide"}>
//                 <SlideBar
               
//                 activeLink={activeLink}
//                 setActiveLink={setActiveLink}
//                 />
                 
               
//                 </div>  
                   

                
//                  <Outlet/>
                
                   
                   
//               </div>
              
//             </div>
//             <Footer/>
//           </div>

//   )
// }

// export default Layout

