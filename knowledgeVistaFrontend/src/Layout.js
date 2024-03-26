import React from 'react'
import { Outlet } from 'react-router-dom'
import SlideBar from './course/Components/SlideBar'
import NavBar from './course/Components/NavBar'

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
          </div>

  )
}

export default Layout
