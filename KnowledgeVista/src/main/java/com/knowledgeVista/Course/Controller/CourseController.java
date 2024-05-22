package com.knowledgeVista.Course.Controller;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.videoLessons;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

import io.jsonwebtoken.io.DecodingException;
import jakarta.transaction.Transactional;

@RestController
public class CourseController {

	
	@Autowired
	private MuserRepositories muserRepository;
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private JwtUtil jwtUtil;
	
	
//`````````````````````````WORKING``````````````````````````````````

	 public ResponseEntity<?> countCourse(String token) {
	     try {
	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	         String role = jwtUtil.getRoleFromToken(token);
	         // Perform authentication based on role
	         if (!"ADMIN".equals(role)) {
	        		
	             // If the role is not ADMIN, return unauthorized status
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	         Long count = coursedetailrepository.count();
	            Long trainercount = muserRepository.countByRoleName("TRAINER");
	            Long usercount = muserRepository.countByRoleName("USER");
	            Long  totalAvailableSeats = coursedetailrepository.countTotalAvailableSeats();
	            

	 	       Map<String, Long> response = new HashMap<>();
	 	       response.put("coursecount",count);
	 	       response.put("trainercount",trainercount);
	 	       response.put("usercount", usercount);
	 	       response.put("availableseats", totalAvailableSeats);
	 	       
	         return ResponseEntity.ok().body(response);
	     } catch (DecodingException ex) {
	         // Log the decoding exception
	         ex.printStackTrace(); 
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     } catch (Exception e) {
	         e.printStackTrace(); // You can replace this with logging framework like Log4j
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }

	 //--------------------------working------------------------------------
	

	    public ResponseEntity<?> addCourse( MultipartFile file,  String courseName,String description,String category,Long Duration,Long Noofseats,Long amount, String token) {
		     try {
		         if (!jwtUtil.validateToken(token)) {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		         }
		         String role = jwtUtil.getRoleFromToken(token);
		 if("ADMIN".equals(role)) {
	        CourseDetail courseDetail = new CourseDetail();
	        courseDetail.setCourseName(courseName);
	        courseDetail.setCourseDescription(description);
	        courseDetail.setCourseCategory(category);
	        courseDetail.setAmount(amount);
	        courseDetail.setDuration(Duration);
	        

	        courseDetail.setNoofseats(Noofseats);
	        try {
	        	 courseDetail.setCourseImage(ImageUtils.compressImage(file.getBytes()));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        // Save the CourseDetail object
	        CourseDetail savedCourse = coursedetailrepository.save(courseDetail);
	        
	        // Update the courseUrl based on the saved course's ID
	        String courseUrl = "/courses/"+savedCourse.getCourseName()+"/" + savedCourse.getCourseId();
	        savedCourse.setCourseUrl(courseUrl);
	       

	        // Save the updated CourseDetail object
	       CourseDetail saved= coursedetailrepository.save(savedCourse);
	       Long courseId=saved.getCourseId();
	       String coursename =saved.getCourseName();
	       Map<String, Object> response = new HashMap<>();
           response.put("message", "savedSucessfully");
           response.put("courseId", courseId);
           response.put("coursename", coursename);
	         return ResponseEntity.ok(response);
	    
		 }else {

             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
		     }catch (Exception e) {
			        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		        }
		     }
	
//``````````````````````````````````````````FOR TRAINER COURSE CREATION````````````````````````````````````````

	    public ResponseEntity<?> addCourseByTrainer( MultipartFile file,  String courseName, 
	    		String description, String category,
	    		Long Duration, Long Noofseats, Long amount, String token) 
	    {
		 if (!jwtUtil.validateToken(token)) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }

	     String role = jwtUtil.getRoleFromToken(token);
	     String email=jwtUtil.getUsernameFromToken(token);
	     if ( "TRAINER".equals(role)) {
	    	
		 
	        CourseDetail courseDetail = new CourseDetail();
	        courseDetail.setCourseName(courseName);
	        courseDetail.setCourseDescription(description);
	        courseDetail.setCourseCategory(category);
	        courseDetail.setAmount(amount);
	        courseDetail.setDuration(Duration);
	        courseDetail.setNoofseats(Noofseats);
	        try {
	        	 courseDetail.setCourseImage(ImageUtils.compressImage(file.getBytes()));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        // Save the CourseDetail object
	        CourseDetail savedCourse = coursedetailrepository.save(courseDetail);
	        
	        // Update the courseUrl based on the saved course's ID
	        String courseUrl = "/courses/"+savedCourse.getCourseName()+"/" + savedCourse.getCourseId();
	        savedCourse.setCourseUrl(courseUrl);
	       

	        // Save the updated CourseDetail object
	       CourseDetail saved= coursedetailrepository.save(savedCourse);
	       
	       
	       Long courseId=saved.getCourseId();
	       String coursename =saved.getCourseName();
	       Optional<Muser> optrainer=muserRepository.findByEmail(email);
	    	if(optrainer.isPresent()) {
	    		Muser trainer =optrainer.get();
	    		trainer.getAllotedCourses().add(saved);
	    		  muserRepository.save(trainer);
	    	}
	       Map<String, Object> response = new HashMap<>();
        response.put("message", "savedSucessfully");
        response.put("courseId", courseId);
        response.put("coursename", coursename);
	         return ResponseEntity.ok(response);
	     }else {
	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }
	    }
	

	 
	//--------------------------working------------------------------------
	
	    public ResponseEntity<?> updateCourse( String token ,Long courseId, MultipartFile file, String courseName, String description, String category, Long Noofseats, Long Duration, Long amount) {
	        try {
	       	 if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }

	         String role = jwtUtil.getRoleFromToken(token);
	         if("ADMIN".equals(role)||"TRAINER".equals(role)) {
	       	 
	            Optional<CourseDetail> courseDetailOptional = coursedetailrepository.findById(courseId);
	            if (courseDetailOptional.isPresent()) {
	                CourseDetail existingCourseDetail = courseDetailOptional.get();
	                if (!courseName.isEmpty()) {
	                    existingCourseDetail.setCourseName(courseName);
	                    String courseUrl = "/courses/" + existingCourseDetail.getCourseName() + "/" + existingCourseDetail.getCourseId();
	                    existingCourseDetail.setCourseUrl(courseUrl);
	                }
	                if (!description.isEmpty()) {
	                    existingCourseDetail.setCourseDescription(description);
	                }
	                if (!category.isEmpty()) {
	                    existingCourseDetail.setCourseCategory(category);
	                }
	                if (Duration != null) {
	                    existingCourseDetail.setDuration(Duration);
	                }
	                if (Noofseats != null) {
	                    existingCourseDetail.setNoofseats(Noofseats);
	                }
	                if (amount != null) {
	                    existingCourseDetail.setAmount(amount);
	                }
	                existingCourseDetail.setUsers(null);
	                existingCourseDetail.setVideoLessons(null);
	                if (file != null) {
	                    existingCourseDetail.setCourseImage(ImageUtils.compressImage(file.getBytes()));
	                }

	                CourseDetail updatedCourse = coursedetailrepository.saveAndFlush(existingCourseDetail);
	                return ResponseEntity.ok("{\"message\": \"Saved successfully\"}");
	            } else {
	                return ResponseEntity.notFound().build();
	            }}else {

		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            return ResponseEntity.badRequest().body("{\"message\": \"Error occurred while processing the image\"}");
	        }
	    }
	//--------------------------working-----------------------------------

	 public ResponseEntity<CourseDetail> getCourse( Long courseId ,String token) {
		 if (!jwtUtil.validateToken(token)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
         String role = jwtUtil.getRoleFromToken(token);
         if("ADMIN".equals(role)||"TRAINER".equals(role)) {
		    Optional<CourseDetail> courseOptional = coursedetailrepository.findById(courseId);
		    if (courseOptional.isPresent()) {
		        CourseDetail course = courseOptional.get();
		        byte[] image= ImageUtils.decompressImage(course.getCourseImage());
		        course.setCourseImage(image);
		        course.setUsers(null);
		        course.setTrainer(null);
	            course.setVideoLessons(null);
		        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(course);
		    } else {
		        // Handle the case when the course with the given ID does not exist
		        return ResponseEntity.notFound().build();
		    }}else {

	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		    }
		}

	//--------------------------working------------------------------------

	    public ResponseEntity<List<CourseDetail>> viewCourse() {
	        List<CourseDetail> courses = coursedetailrepository.findAll();
	        // Decompress image data for each course
	        
	        for (CourseDetail course : courses) {
	        
	        	 byte[] images =ImageUtils.decompressImage(course.getCourseImage());
	            course.setCourseImage(images);
	            course.setUsers(null);
		        course.setTrainer(null);
	            course.setVideoLessons(null);
	        }
	        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(courses);
	    }

	 //-------------------------Under check------------------------------------

	   public ResponseEntity<?> getAllCourseInfo(  String token, String email) {
		   
	          if (!jwtUtil.validateToken(token)) {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          if ("ADMIN".equals(role)||"TRAINER".equals(role)) {
	        	  Optional<Muser> optionalUser = muserRepository.findByEmail(email);
	      	    if (optionalUser.isPresent()) {
	      	        Muser user = optionalUser.get();
	      	        if("USER".equals(user.getRole().getRoleName())) {
	      	        List<CourseDetail> courses = user.getCourses();
	        	  
	               List<Map<String, Object>> courseInfoList = coursedetailrepository.findAll()
	               .stream()
	                 .map(course -> {

			               boolean isSelected = courses.contains(course);
	                       Map<String, Object> courseInfo = Map.of(
	                           "courseId", course.getCourseId(),
	                           "courseName", course.getCourseName(),
	                           "selected", isSelected
	                   );
	                   return courseInfo;
	               })
	               .collect(Collectors.toList());
	       return ResponseEntity.ok().body(courseInfoList);}
	      	        else {
	    	   return ResponseEntity.notFound().build();
	       }
	      	    }
	      	    return ResponseEntity.notFound().build();
	       }else {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    	   
	       }
	   }

	   public ResponseEntity<?> getAllAllotelistInfo( String token,String email) {
		   
	          if (!jwtUtil.validateToken(token)) {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          if ("ADMIN".equals(role)) {
	        	  Optional<Muser> optionalUser = muserRepository.findByEmail(email);
	      	    if (optionalUser.isPresent()) {
	      	    	
	      	        Muser user = optionalUser.get();
	      	        if("TRAINER".equals(user.getRole().getRoleName())) {
	      	        List<CourseDetail> courses = user.getAllotedCourses();
	        	  
	       List<Map<String, Object>> courseInfoList = coursedetailrepository.findAll()
	               .stream()
	               .map(course -> {

		               boolean isSelected = courses.contains(course);
	                   Map<String, Object> courseInfo = Map.of(
	                           "courseId", course.getCourseId(),
	                           "courseName", course.getCourseName(),
	                           "selected", isSelected
	                   );
	                   return courseInfo;
	               })
	               .collect(Collectors.toList());
	       return ResponseEntity.ok().body(courseInfoList);
	       }
	      	        else {
	    	      	    return ResponseEntity.notFound().build();
	      	        	
	      	        }
	      	    }
	      	    return ResponseEntity.notFound().build();
	       }else {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    	   
	       }
	   }


	   //---------------------WORKING--------------
	 
	   public ResponseEntity<String> deleteCourse( Long courseId ,String token) {
	       try {
	           // Find the course by ID
	    	   if (!jwtUtil.validateToken(token)) {
	               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	           }

	           String role = jwtUtil.getRoleFromToken(token);
		          String email=jwtUtil.getUsernameFromToken(token);
		          if("ADMIN".equals(role)||"TRAINER".equals(role)) {
	           Optional<CourseDetail> optionalCourse = coursedetailrepository.findById(courseId);
	           
	           if (optionalCourse.isPresent()) {
	               CourseDetail course = optionalCourse.get();
	               if("TRAINER".equals(role)) {
		        		Optional< Muser> trainerop= muserRepository.findByEmail(email);
		        		  if(trainerop.isPresent()) {
		        			  Muser trainer =trainerop.get();
		        			  if( !trainer.getAllotedCourses().contains(course)) {

		    		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		        			  }
		        		  }
		        	  }
	               
	               // Clear the lists of trainers and users associated with the course
	               course.getTrainer().clear();
	               course.getUsers().clear();
	               
	               // Save the changes to ensure they are reflected in the database
	               coursedetailrepository.save(course);
	               
	               // Remove references to the course from the user_course table
	               for (Muser user : course.getUsers()) {
	                   user.getCourses().remove(course);
	               }
	               for (Muser user :course.getTrainer()) {

	                   user.getAllotedCourses().remove(course);
	               }
	               
	               // Delete the course
	               coursedetailrepository.delete(course);

	               return ResponseEntity.ok("Course deleted successfully");
	           } else {
	               // If the course with the specified ID does not exist
	               return ResponseEntity.notFound().build();
	           }
	       }else {

	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
	       }catch (DataIntegrityViolationException e) {
	           // If a foreign key constraint violation occurs
	           // Return a custom error response with an appropriate status code and message
	           return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                   .body("The course cannot be deleted. Delete all associated lessons, test.");
	       }
	   }

		 //---------------------WORKING--------------)
	   public ResponseEntity<?> getLessons(Long courseId, String token) {
	       try {
	           if (!jwtUtil.validateToken(token)) {
	               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	           }

	           String role = jwtUtil.getRoleFromToken(token);

	           if (!"ADMIN".equals(role)) {
	               String email = jwtUtil.getUsernameFromToken(token);
	               Optional<Muser> opuser = muserRepository.findByEmail(email);
	               if (!opuser.isPresent()) {
	                   return ResponseEntity.notFound().build();
	               }
	               Muser user = opuser.get();
	               
	               if ("TRAINER".equals(role)) {
	                   if (user.getAllotedCourses().stream().anyMatch(course -> course.getCourseId().equals(courseId))) {
	                       return getVideoLessonsResponse(courseId);
	                   }
	               } else if ("USER".equals(role)) {
	                   if (user.getCourses().stream().anyMatch(course -> course.getCourseId().equals(courseId))) {
	                       return getVideoLessonsResponse(courseId);
	                   }
	               }
	               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	           } else {
	               return getVideoLessonsResponse(courseId);
	           }
	       } catch (Exception e) {
	           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	       }
	   }

	   private ResponseEntity<?> getVideoLessonsResponse(Long courseId) {
	       Optional<CourseDetail> opcourse = coursedetailrepository.findById(courseId);
	       if (opcourse.isPresent()) {
	           List<videoLessons> videolessonlist = opcourse.get().getVideoLessons();
	           for (videoLessons video : videolessonlist) {
	               video.setCourseDetail(null);
	               video.setVideoFile(null);
	               video.setVideofilename(null);

	               byte[] images = ImageUtils.decompressImage(video.getThumbnail());
	               video.setThumbnail(images);
	           }
	           return ResponseEntity.ok(videolessonlist);
	       }
	       return ResponseEntity.notFound().build();
	   }

		 public ResponseEntity<?> getLessonList( Long courseId , String token) {
			 try {
				 if (!jwtUtil.validateToken(token)) {
		               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		           }

		           String role = jwtUtil.getRoleFromToken(token);
			          String email=jwtUtil.getUsernameFromToken(token);
			          if("ADMIN".equals(role)||"TRAINER".equals(role)) {
			
			    Optional<CourseDetail> opcourse = coursedetailrepository.findById(courseId);
			    if (opcourse.isPresent()) {
			    	
			    	 if("TRAINER".equals(role)) {
			        		Optional< Muser> trainerop= muserRepository.findByEmail(email);
			        		  if(trainerop.isPresent()) {
			        			  Muser trainer =trainerop.get();
			        			  if( !trainer.getAllotedCourses().contains(opcourse.get())) {

			    		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			        			  }
			        		  }
			        	  }
			        List<videoLessons> videolessonlist = opcourse.get().getVideoLessons();
			        List<Map<String, Object>> lessonResponseList = new ArrayList<>();

			        for (videoLessons video : videolessonlist) {
			            Map<String, Object> response = new HashMap<>();
			            response.put("Lessontitle", video.getLessontitle());
			            response.put("lessonId", video.getLessonId());
			            lessonResponseList.add(response);
			        }

			        return ResponseEntity.ok(lessonResponseList);
			    }

			    return ResponseEntity.notFound().build();
			}else {

	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
       } catch (Exception e) {
           e.printStackTrace(); // Print the stack trace for debugging
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error creating test: " + e.getMessage());
       }
		 }
		 
}
