package com.knowledgeVista.Course.Controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class CourseControllerSecond {
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private JwtUtil jwtUtil;
	 
	
	 
	 public ResponseEntity<List<CourseDetail>> popular( String token) {
	     try {
	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	         String role = jwtUtil.getRoleFromToken(token);
	         List<CourseDetail> allCoursesOrderedByUserCount = coursedetailrepository.findAllOrderByUserCountDesc();
	         List<CourseDetail> top4Courses = allCoursesOrderedByUserCount.stream().limit(4).collect(Collectors.toList());
	        
	         top4Courses.forEach(course -> {
	        	  byte[] image= ImageUtils.decompressImage(course.getCourseImage());
			      course.setCourseImage(image);
	             course.setUsers(null);
	             course.setTrainer(null);
	             course.setVideoLessons(null);
	         });

	         return ResponseEntity.ok()
	             .contentType(MediaType.APPLICATION_JSON)
	             .body(top4Courses);
	     } catch (Exception e) {
	         e.printStackTrace(); // You can replace this with logging framework like Log4j
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }

}
