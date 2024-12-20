package com.knowledgeVista.Course.Controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
@CrossOrigin
public class CheckAccess {
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	@Autowired
	private MuserRepositories muserRepository;
	 @Autowired
	 private JwtUtil jwtUtil;


  	 private static final Logger logger = LoggerFactory.getLogger(CheckAccess.class);

	 public ResponseEntity<?> checkAccess( Map<String, Long> requestData,  String token) {
	     try {
	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }

	         String role = jwtUtil.getRoleFromToken(token);
	         String email = jwtUtil.getUsernameFromToken(token);

	         Long courseId = requestData.get("courseId");
	         Optional<CourseDetail> courseOptional = coursedetailrepository.findById(courseId);
	         Optional<Muser> optionalUser = muserRepository.findByEmail(email);

	         if ("ADMIN".equals(role)) {
	             if (courseOptional.isPresent()) {
	                 CourseDetail course = courseOptional.get();
	                 String courseUrl = course.getCourseUrl();
	                 return ResponseEntity.ok().body(courseUrl);
	             }
	         } else if ("TRAINER".equals(role)) {
	             if (optionalUser.isPresent() && courseOptional.isPresent()) {
	                 Muser user = optionalUser.get();
	                 CourseDetail course = courseOptional.get();
	                 if (user.getAllotedCourses().contains(course)) {
	                     String courseUrl = course.getCourseUrl();
	                     return ResponseEntity.ok().body(courseUrl);
	                 }
	             }
	         } else if ("USER".equals(role)) {
	             if (optionalUser.isPresent() && courseOptional.isPresent()) {
	                 Muser user = optionalUser.get();
	                 CourseDetail course = courseOptional.get();
	                 if (user.getCourses().contains(course)) {
	                     String courseUrl = course.getCourseUrl();
	                     return ResponseEntity.ok().body(courseUrl);
	                 }
	             }
	         }
	         
	         // If role is not ADMIN, TRAINER, or USER, or if course or user not found
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

	     } catch (Exception e) {
	         // Handle any exceptions
//	    	 e.printStackTrace();    logger.error("", e);;
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }
}