import React from 'react'

const Dashboard = () => {
  return (
    <div className='contentbackground'>
        <div className='contentinner'>
            <div className='counts '>
              <div className='countchild '>
                <p>Available seats</p>
                <div className=' rounded-circle circleimg' ><h1> <i className="fa-solid fa-wheelchair mt-2"></i></h1></div>
                <p>650 seats</p>
              </div>
              <div className='countchild'>
                <p>Total Students</p>
                <div className=' rounded-circle circleimg' ><h1> <i className="fa-solid fa-person-walking mt-2"></i></h1></div>
                <p>650 students</p>
              </div>
              <div className='countchild'>
                <p>Total Courses</p>
                <div className=' rounded-circle circleimg' ><h1> <i className="fa-solid fa-chart-line mt-2"></i></h1></div>
                <p>600 courses</p>
              </div>
              <div className='countchild'>
                <p>Total Trainers</p>
                <div className=' rounded-circle circleimg' ><h1> <i className="fa-solid fa-person-chalkboard mt-2"></i></h1></div>
                <p>600 Trainers</p>
                </div>
           </div>
            <div className='counts '>
            </div>
        </div>
      
    </div>
  )
}

export default Dashboard
