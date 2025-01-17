
package com.knowledgeVista.User.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.CourseDetailDto;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Notification.Service.NotificationService;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class AssignCourse {
	@Autowired
	private MuserRepositories muserRepository;
	@Autowired
	private CourseDetailRepository courseDetailRepository;
	@Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
		private NotificationService notiservice;
	 
	 private static final Logger logger = LoggerFactory.getLogger(AssignCourse.class);
//````````````````````````````Assign Course to Student Admin function ```````````````````````````````````	
	
	public ResponseEntity<String> assignCoursesToUser( Long userId,  Map<String, List<Long>> data, String token) {
		try {
		if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtUtil.getRoleFromToken(token);
        String email=jwtUtil.getUsernameFromToken(token);
        String username="";
	     Optional<Muser> opuser =muserRepository.findByEmail(email);
	     if(opuser.isPresent()) {
	    	 Muser userad=opuser.get();
	    	 username=userad.getUsername();
	    	 String intitution=userad.getInstitutionName();
	    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", intitution);
	   	    	if(!adminIsactive) {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	    	}
	     }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }
        if("ADMIN".equals(role) ||"TRAINER".equals(role)) {
        	
        	List<String> fullseatcoursename = new ArrayList<>();
	        Optional<Muser> optionalUser = muserRepository.findById(userId);

	        if (optionalUser.isPresent()) {
	            Muser user = optionalUser.get();
	            String institution=user.getInstitutionName();
	            List<CourseDetail> courselistold=user.getCourses();
	        	List<Long> userlist = new ArrayList<>();
	        	userlist.add(userId);

            	List<Long> trainerlist = new ArrayList<>();
            	List<Long> courseIds = data.get("selectedCourses");
                List<Long> unselectedCourseIds = data.get("unselectedCourses");
               
	            if ("USER".equals(user.getRole().getRoleName())) {
	                List<CourseDetail> coursesToAdd = new ArrayList<>();
	                List<CourseDetail> coursestoremove = new ArrayList<>();
	                
	                coursestoremove =  this.unselectcourses(userId, courselistold, unselectedCourseIds);
	                for (Long courseId : courseIds) {
	                    Optional<CourseDetail> optionalCourse = courseDetailRepository.findById(courseId);
	                    if (optionalCourse.isPresent()) {
	                        CourseDetail course = optionalCourse.get();
                            if(!courselistold.contains(course)) {
                            	
                            	List<Muser> trainers=course.getTrainer();
                            	if(trainers != null) {
                            		for(Muser trainer :trainers) {
                            			trainerlist.add(trainer.getUserId());
                            		}
                            	}
                            	
	                        Long seats = course.getNoofseats();
	                        Long filled = course.getUserCount();
	                        if (seats > filled) {
	                            coursesToAdd.add(course);
	                            try {
	                            String heading="New Course Assigned !";
	             		       String link=course.getCourseUrl();
	             		       String notidescription= "A Course "+course.getCourseName() + " was assigned to you " ;
	             		      Long NotifyId =  notiservice.createNotification("CourseAssigned",username,notidescription ,email,heading,link,course.getCourseImage());
	             		        if(NotifyId!=null) {
	             		        	
	             		        	notiservice.SpecificCreateNotification(NotifyId,userlist);
	             		        }
	             		       String headingT="New Student Assigned !";
	             		       String linkT="/view/Student/profile/"+user.getEmail();
	             		       String notidescriptionT= "A New Student "+ user.getUsername() + " was assigned to your course "+course.getCourseName() ;
	             		      Long NotifyIdT =  notiservice.createNotification("StudentAssigned",username,notidescriptionT ,email,headingT,linkT,user.getProfile());
	             		        if(NotifyIdT!=null) {
	             		        	notiservice.SpecificCreateNotification(NotifyIdT,trainerlist);
	             		        }
	             		        
	             		       String headingA="Course Assigned to Student !";
	             		       String linkA="/view/Student/profile/"+user.getEmail();
	             		       String notidescriptionA= "A Course "+ course.getCourseName() + " was assigned to "+user.getUsername() ;
	             		      Long NotifyIdA =  notiservice.createNotification("StudentAssigned",username,notidescriptionA ,email,headingA,linkA,user.getProfile());
	             		        if(NotifyIdA!=null) {
	             		        	List<String> admin = new ArrayList<>();
	             		          admin.add("ADMIN");
	             		        	notiservice.CommoncreateNotificationUser(NotifyIdA,admin,institution );
	             		        }
	                            }catch(Exception e) {
	                            	logger.error("error in notification",e);
	                            }
	                        } else { 
	                        	fullseatcoursename.add(course.getCourseName());
	                        	}
                            }
	                    } 
	                }
	                String list =String.join(",",fullseatcoursename);
	                String notification="";
	                if(fullseatcoursename.size()>0) {
	               notification= " Seats are filled for the Courses "+list+" Try to Increase the Seats and then Add the Student";
	                }
	                user.getCourses().addAll(coursesToAdd);
	                user.getCourses().removeAll(coursestoremove);  

	                // Save the updated user
	                muserRepository.save(user);
	                return ResponseEntity.ok("{\"message\": \"Courses assigned to user successfully." +notification +"\"}");

	            } else {
	            	 return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                         .body("{\"error\": \"The specified user is not a USER.\"}");
	                 }
	        } else {
	            // If a user with the specified ID doesn't exist, return 404
	        	 return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                     .body("{\"error\": \"User with ID " + userId + " not found.\"}");
	               }}else {
	                   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            	   
	               }
	    } catch (Exception e) {
	        // If an error occurs, return 500
	    	e.printStackTrace();
	    	 logger.error("", e);
	    	  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	    	            .body("{\"error\": \"An error occurred while processing the request.\"}");
	    	     }
	}
	
	
	public List<CourseDetail> unselectcourses(Long userId,List<CourseDetail> courselistold,  List<Long> courseIds) {

        List<CourseDetail> coursesToRemove = new ArrayList<>();
        for (Long courseId : courseIds) {
            Optional<CourseDetail> optionalCourse = courseDetailRepository.findById(courseId);
            if (optionalCourse.isPresent()) {
                CourseDetail course = optionalCourse.get();
                if(courselistold.contains(course)) {
                	coursesToRemove.add(course);
                }
                }
                }
        return coursesToRemove;
                	
	}
	
	
	
	//`````````````````````````````````Assign Course To TRAINER``````````````````````````````````
	
	public ResponseEntity<String> assignCoursesToTrainer( Long userId,  @RequestBody Map<String, List<Long>> data, String token) {
		if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtUtil.getRoleFromToken(token);
        String email=jwtUtil.getUsernameFromToken(token);
        String username="";
	     Optional<Muser> opuser =muserRepository.findByEmail(email);
	     if(opuser.isPresent()) {
	    	 Muser userad=opuser.get();
	    	 username=userad.getUsername();
	    	 String institution=userad.getInstitutionName();
	    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
	   	    	if(!adminIsactive) {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	    	}
	     }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }
        if("ADMIN".equals(role)) {
	    Optional<Muser> optionalUser = muserRepository.findById(userId);

	    if (optionalUser.isPresent()) {
	        Muser trainer = optionalUser.get();
	        List<Long> userlist = new ArrayList<>();
        	userlist.add(userId);
            List<CourseDetail> oldcourses=trainer.getAllotedCourses();
        	List<Long> courseIds = data.get("selectedCourses");
            List<Long> unselectedCourseIds = data.get("unselectedCourses");

        	List<Long> trainerlist = new ArrayList<>();
	        if ("TRAINER".equals(trainer.getRole().getRoleName())) {
	            

	            List<CourseDetail> coursesToAdd = new ArrayList<>();

	            List<CourseDetail> coursesToRemove=this.unselectcourses(userId, oldcourses, unselectedCourseIds);
	            // Iterate through the list of course IDs
	            for (Long courseId : courseIds) {
	                Optional<CourseDetail> optionalCourse = courseDetailRepository.findById(courseId);
	                if (optionalCourse.isPresent()) {
	                    CourseDetail course = optionalCourse.get();
	                    
	                	List<Muser> trainers=course.getTrainer();
                    	if(trainers != null) {
                    		for(Muser trainersingle :trainers) {
                    			trainerlist.add(trainersingle.getUserId());
                    		}
                    	}
                    	
                    	
	                    if(!oldcourses.contains(course)) {
	                    coursesToAdd.add(course);
	                    String heading="New Course Assigned !";
          		       String link=course.getCourseUrl();
          		       String notidescription= "A Course "+course.getCourseName() + " was assigned to you " ;
          		      Long NotifyId =  notiservice.createNotification("CourseAssigned",username,notidescription ,email,heading,link,course.getCourseImage());
          		        if(NotifyId!=null) {
          		        	
          		        	notiservice.SpecificCreateNotification(NotifyId,userlist);
          		        }
          		        
          		      String headingT="New Trainer Assigned !";
        		       String linkT="/view/Student/profile/"+trainer.getEmail();
        		       String notidescriptionT= "A New Trainer "+ trainer.getUsername() + " was Joined with you in the course "+course.getCourseName() ;
        		      Long NotifyIdT =  notiservice.createNotification("StudentAssigned",username,notidescriptionT ,email,headingT,linkT,trainer.getProfile());
        		        if(NotifyIdT!=null) {
        		        	notiservice.SpecificCreateNotification(NotifyIdT,trainerlist);
        		        }
	                    }
	                } else {
	                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course with ID " + courseId + " not found");
	                }
	            }

	            // Add the new courses to the trainer's list of allotted courses
	            trainer.getAllotedCourses().addAll(coursesToAdd);
                trainer.getAllotedCourses().removeAll(coursesToRemove);
	            // Save the updated trainer information
	            muserRepository.save(trainer);

	            return ResponseEntity.ok("Courses assigned to trainer successfully");
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not a trainer");
	        }
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found");
	    }}else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	}

	
	//----------------------------------MyCourses--------------------------------

	public ResponseEntity<List<CourseDetailDto>> getCoursesForUser( String token) {
		
		if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtUtil.getRoleFromToken(token);
        String email = jwtUtil.getUsernameFromToken(token);
		if("USER".equals(role)) {
			 List<CourseDetailDto> courses=muserRepository.findStudentAssignedCoursesByEmail(email);
			  
		        return ResponseEntity.ok(courses);
	    

	        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	
	
	public ResponseEntity<List<CourseDetailDto>> getCoursesForTrainer(String token) {
		if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtUtil.getRoleFromToken(token);
        String email = jwtUtil.getUsernameFromToken(token);
		if("TRAINER".equals(role)) {
			 List<CourseDetailDto> courses=muserRepository.findAllotedCoursesByEmail(email);
	  
	        return ResponseEntity.ok(courses);
	        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    
	}
	
	
//-----------------------Enroll Course---------------------------------------------
//	@PostMapping("/{userId}/{courseId}")
//	public ResponseEntity<String> EnrollCourses(@PathVariable Long userId, @PathVariable Long courseId) {
//	    try {
//	        Optional<Muser> optionalUser = muserRepository.findById(userId);
//
//	        if (optionalUser.isPresent()) {
//	            Muser user = optionalUser.get();
//
//	            Optional<CourseDetail> optionalCourse = courseDetailRepository.findById(courseId);
//	            if (optionalCourse.isPresent()) {
//	                CourseDetail course = optionalCourse.get();
//
//	                // Check if the user is already enrolled in the course
//	                if (user.getCourses().contains(course)) {
//	                    return ResponseEntity.badRequest().body("User is already enrolled in the course");
//	                }
//
//	                // Add the course directly to the user's list of courses
//	                user.getCourses().add(course);
//	                muserRepository.save(user);
//	                return ResponseEntity.ok("Course enrolled successfully");
//	            } else {
//	                // If a course with the specified ID doesn't exist, return 404
//	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course with ID " + courseId + " not found");
//	            }
//	        } else {
//	            // If a user with the specified ID doesn't exist, return 404
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found");
//	        }
//	    } catch (Exception e) {
//	        // If an error occurs, return 500
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to enroll in the course");
//	    }
//	}
//``````````````````````````````

}
