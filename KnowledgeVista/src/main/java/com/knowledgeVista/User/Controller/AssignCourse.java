package com.knowledgeVista.User.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
@RequestMapping("/AssignCourse")
@CrossOrigin
public class AssignCourse {
	@Autowired
	private MuserRepositories muserRepository;
	@Autowired
	private CourseDetailRepository courseDetailRepository;
	@Autowired
	 private JwtUtil jwtUtil;
	
//````````````````````````````Assign Course to Student Admin function ```````````````````````````````````	
	@PostMapping("/{userId}/courses")
	public ResponseEntity<String> assignCoursesToUser(@PathVariable Long userId, @RequestBody List<Long> courseIds) {
	    try {
	        Optional<Muser> optionalUser = muserRepository.findById(userId);

	        if (optionalUser.isPresent() ) {
	            Muser user = optionalUser.get();
	            if("USER".equals(user.getRole().getRoleName())) {

	            List<CourseDetail> coursesToAdd = new ArrayList<>();

	            for (Long courseId : courseIds) {
	                Optional<CourseDetail> optionalCourse = courseDetailRepository.findById(courseId);
	                if (optionalCourse.isPresent()) {
	                    CourseDetail course = optionalCourse.get();
	                    // Add the course to the list to be assigned to the user
	                    coursesToAdd.add(course);
	                } else {
	                    // If a course with the specified ID doesn't exist, return 404
	                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course with ID " + courseId + " not found");
	                }
	            }

	            // Add all the courses to the user's list of courses
	            user.getCourses().addAll(coursesToAdd);
	            muserRepository.save(user);
	            return ResponseEntity.ok("Courses assigned to user successfully");
	        }
	            return ResponseEntity.notFound().build();
	        } else {
	            // If a user with the specified ID doesn't exist, return 404
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found");
	        }
	    } catch (Exception e) {
	        // If an error occurs, return 500
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to assign courses to user");
	    }
	}
	
	//`````````````````````````````````Assign Course To TRAINER``````````````````````````````````
	@PostMapping("/trainer/{userId}/courses")
	public ResponseEntity<String> assignCoursesToTRAINER(@PathVariable Long userId, @RequestBody List<Long> courseIds) {
	    try {
	        Optional<Muser> optionalUser = muserRepository.findById(userId);

	        if (optionalUser.isPresent()) {
	            Muser user = optionalUser.get();
	            if("TRAINER".equals(user.getRole().getRoleName())) {

	            List<CourseDetail> coursesToAdd = new ArrayList<>();

	            for (Long courseId : courseIds) {
	                Optional<CourseDetail> optionalCourse = courseDetailRepository.findById(courseId);
	                if (optionalCourse.isPresent()) {
	                    CourseDetail course = optionalCourse.get();
	                    // Add the course to the list to be assigned to the user
	                    coursesToAdd.add(course);
	                } else {
	                    // If a course with the specified ID doesn't exist, return 404
	                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course with ID " + courseId + " not found");
	                }
	            }

	            // Add all the courses to the user's list of courses
	            user.getCourses().addAll(coursesToAdd);
	            muserRepository.save(user);
	            return ResponseEntity.ok("Courses assigned to user successfully");
	        } return ResponseEntity.notFound().build();
        } else {
	            // If a user with the specified ID doesn't exist, return 404
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found");
	        }
	    } catch (Exception e) {
	        // If an error occurs, return 500
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to assign courses to user");
	    }
	}

	
	//----------------------------------MyCourses--------------------------------

	@GetMapping("/student/courselist")
	public ResponseEntity<List<CourseDetail>> getCoursesForUser(
	          @RequestHeader("Authorization") String token) {
		if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtUtil.getRoleFromToken(token);
        String email = jwtUtil.getUsernameFromToken(token);
		if("USER".equals(role)) {
	    Optional<Muser> optionalUser = muserRepository.findByEmail(email);
	    if (optionalUser.isPresent()) {
	        Muser user = optionalUser.get();
	        
	        List<CourseDetail> courses = user.getCourses();
	        
	        // Set courseLessons to null for each course
	      
	        for (CourseDetail course : courses) {
	            course.setCourseLessons(null);
	            course.setUsers(null);
	            course.setVideoLessons(null);
	            byte[] images =ImageUtils.decompressImage(course.getCourseImage());
	            course.setCourseImage(images);
	           
	        }
	    

	        return ResponseEntity.ok(courses);
	        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("/Trainer/courselist")
	public ResponseEntity<List<CourseDetail>> getCoursesForTrainer(
	          @RequestHeader("Authorization") String token) {
		if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = jwtUtil.getRoleFromToken(token);
        String email = jwtUtil.getUsernameFromToken(token);
		if("TRAINER".equals(role)) {
	    Optional<Muser> optionalUser = muserRepository.findByEmail(email);
	    if (optionalUser.isPresent()) {
	        Muser user = optionalUser.get();
	        
	        List<CourseDetail> courses = user.getCourses();
	        
	        // Set courseLessons to null for each course
	      
	        for (CourseDetail course : courses) {
	            course.setCourseLessons(null);
	            course.setUsers(null);
	            course.setVideoLessons(null);
	            byte[] images =ImageUtils.decompressImage(course.getCourseImage());
	            course.setCourseImage(images);
	           
	        }
	    

	        return ResponseEntity.ok(courses);
	        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
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
