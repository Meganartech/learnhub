import React from 'react'
import { Outlet } from 'react-router-dom'
import SlideBar from './SlideBar'
import NavBar from './NavBar'
import Footer from './Footer'

const Layout = ({isToggled,setIsToggled,searchQuery,handleSearchChange,setSearchQuery}) => {
    
  return (
    <div id="page-top" className={isToggled ? "sidebar-toggled" : ""}>
            <div id="wrapper">          
                <SlideBar
                  isToggled={isToggled}
                  setIsToggled={setIsToggled}
                />    
              <div id="content-wrapper">
                <div id="content">
                
                    <NavBar setIsToggled={setIsToggled} searchQuery={searchQuery} handleSearchChange={handleSearchChange} setSearchQuery={setSearchQuery}/>

                    
                 <Outlet/>
                    </div>
                   
              </div>
              
            </div>
            <Footer/>
          </div>

  )
}

export default Layout
