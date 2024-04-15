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
@RequestMapping("/course")
@CrossOrigin
public class CourseController {

	
	@Autowired
	private MuserRepositories muserRepository;
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private JwtUtil jwtUtil;
	
	
//`````````````````````````WORKING``````````````````````````````````

	 @GetMapping("/countcourse")
	 public ResponseEntity<Long> countCourse(@RequestHeader("Authorization") String token) {
	     try {
	         // Validate the token
	    	 
	         if (!jwtUtil.validateToken(token)) {
	        	 System.out.println("invalid Token");
	    
	             // If the token is not valid, return unauthorized status
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            
	         }

	         String role = jwtUtil.getRoleFromToken(token);
	         

	         // Perform authentication based on role
	         if (!"ADMIN".equals(role)) {
	        		
	             // If the role is not ADMIN, return unauthorized status
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }

	         Long count = coursedetailrepository.count();
	         return ResponseEntity.ok().body(count);
	     } catch (DecodingException ex) {
	         // Log the decoding exception
	         ex.printStackTrace(); // You can replace this with logging framework like Log4j

	         // Return an error response indicating invalid token
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     } catch (Exception e) {
	         // Log any other exceptions for debugging purposes
	         e.printStackTrace(); // You can replace this with logging framework like Log4j

	         // Return an internal server error response
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }

	 //--------------------------working------------------------------------
	
	 @PostMapping("/add")
	    public ResponseEntity<?> addCourse(@RequestParam("courseImage") MultipartFile file, 
	    		@RequestParam("courseName") String courseName,
	    		@RequestParam("courseDescription") String description,
	    		@RequestParam("courseCategory") String category,
	    		@RequestParam("Duration") Long Duration,
	    		@RequestParam("Noofseats") Long Noofseats,
	    		@RequestParam("courseAmount") Long amount) {
		 
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
	    }
	
//``````````````````````````````````````````FOR TRAINER COURSE CREATION````````````````````````````````````````
	 @PostMapping("/create/trainer")
	    public ResponseEntity<?> addCourseByTrainer(@RequestParam("courseImage") MultipartFile file, 
	    		@RequestParam("courseName") String courseName,
	    		@RequestParam("courseDescription") String description,
	    		@RequestParam("courseCategory") String category,
	    		@RequestParam("Duration") Long Duration,
	    		@RequestParam("Noofseats") Long Noofseats,
	    		@RequestParam("courseAmount") Long amount,
                @RequestHeader("Authorization") String token) {
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
	 @Transactional
	 @PatchMapping("/edit/{courseId}")
	 public ResponseEntity<?> updateCourse(
	     @PathVariable Long courseId,
	     @RequestParam(value = "courseImage", required = false) MultipartFile file,
	     @RequestParam(value = "courseName", required = false) String courseName,
	     @RequestParam(value = "courseDescription", required = false) String description,
	     @RequestParam(value = "courseCategory", required = false) String category,
	     @RequestParam(value ="Noofseats",required = false) Long Noofseats,
 		@RequestParam(value ="Duration",required = false)Long Duration,
	      @RequestParam(value="courseAmount",required=false) Long amount){

	     Optional<CourseDetail> courseDetailOptional = coursedetailrepository.findById(courseId);
	     if (courseDetailOptional.isPresent()) {
	         CourseDetail existingCourseDetail = courseDetailOptional.get();
	         if(courseName!="") {
	             existingCourseDetail.setCourseName(courseName);
	             String courseUrl="/courses/"+existingCourseDetail.getCourseName()+"/"+existingCourseDetail.getCourseId();
	         
	             existingCourseDetail.setCourseUrl(courseUrl);
	         }
	         if(description!="") {
	             existingCourseDetail.setCourseDescription(description);
	         }
	         if(category !="") {
	             existingCourseDetail.setCourseCategory(category);
	         }
	         if(Duration !=null) {
	        	 existingCourseDetail.setDuration(Duration);
	         }
	         if(Noofseats !=null) {
	        	 existingCourseDetail.setNoofseats(Noofseats);
	         }
	         if(amount !=null) {
	             existingCourseDetail.setAmount(amount);
	         }
	             existingCourseDetail.setUsers(null);
	             existingCourseDetail.setVideoLessons(null);
	             if (file != null) {
	            	    try {
	            	        existingCourseDetail.setCourseImage(ImageUtils.compressImage(file.getBytes()));
	            	    } catch (IOException e) {
	            	        e.printStackTrace(); // Handle the exception properly in your application
	            	    }
	            	}

	         CourseDetail updatedCourse = coursedetailrepository.saveAndFlush(existingCourseDetail);
	         return ResponseEntity.ok("{\"message\": \"Saved successfully\"}");
	     } else {
	         return ResponseEntity.notFound().build();
	     }
	     }
	
	 
	//--------------------------working-----------------------------------
	 @GetMapping("/get/{courseId}")
	 public ResponseEntity<CourseDetail> getCourse(@PathVariable Long courseId) {
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
		    }
		}

	//--------------------------working------------------------------------
	   @GetMapping("/viewAll")
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

	   @GetMapping("/assignList")
	   public ResponseEntity<?> getAllCourseInfo(
		          @RequestHeader("Authorization") String token,
		          @RequestParam("email") String email) {
		   
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
	   
	   
	   
	   @GetMapping("/allotList")
	   public ResponseEntity<?> getAllAllotelistInfo(
		          @RequestHeader("Authorization") String token,
		          @RequestParam("email") String email) {
		   
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
	   @DeleteMapping("/{courseId}")
	   public ResponseEntity<String> deleteCourse(@PathVariable Long courseId) {
	       try {
	           // Find the course by ID
	           Optional<CourseDetail> optionalCourse = coursedetailrepository.findById(courseId);
	           
	           if (optionalCourse.isPresent()) {
	               CourseDetail course = optionalCourse.get();
	               course.getUsers().clear();
	               coursedetailrepository.delete(course);

	               return ResponseEntity.ok("Course deleted successfully");
	           } else {
	               // If the course with the specified ID does not exist
	               return ResponseEntity.notFound().build();
	           }
	       } catch (DataIntegrityViolationException e) {
	           // If a foreign key constraint violation occurs
	           // Return a custom error response with an appropriate status code and message
	           return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                   .body("The course cannot be deleted. Delete all associated lessons and tests first.");
	       }
	   }

		 //---------------------WORKING--------------

		 @GetMapping("/getLessondetail/{courseId}")
	   public ResponseEntity<?>getLessons(@PathVariable Long courseId){
			 Optional<CourseDetail> opcourse = coursedetailrepository.findById(courseId);
			 if(opcourse.isPresent()) {
				 List<videoLessons> videolessonlist=opcourse.get().getVideoLessons();
				  for (videoLessons video :videolessonlist) {
					  video.setCourseDetail(null);
					  video.setVideoFile(null);
					  video.setVideofilename(null);
					  video.setFileUrl(null);
					  byte[] images =ImageUtils.decompressImage(video.getThumbnail());
					  video.setThumbnail(images);
					 
				  }
				  return ResponseEntity.ok(videolessonlist);
				  
			 }
		   return ResponseEntity.notFound().build();
	   }

		 @GetMapping("/getLessonlist/{courseId}")
		 public ResponseEntity<?> getLessonList(@PathVariable Long courseId) {
			    Optional<CourseDetail> opcourse = coursedetailrepository.findById(courseId);
			    if (opcourse.isPresent()) {
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
			}
		 
}
