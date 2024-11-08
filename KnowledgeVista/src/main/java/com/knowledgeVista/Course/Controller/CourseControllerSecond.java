package com.knowledgeVista.Course.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class CourseControllerSecond {
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private JwtUtil jwtUtil;
	@Autowired
	private MuserRepositories muserRepository;
		
 	 private static final Logger logger = LoggerFactory.getLogger(CourseControllerSecond.class);

	 
	
	 
	 public ResponseEntity<List<CourseDetail>> popular( String token) {
	     try {
	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	         String role = jwtUtil.getRoleFromToken(token);
	         String email=jwtUtil.getUsernameFromToken(token);
	         String institution="";
		     Optional<Muser> opuser =muserRepository.findByEmail(email);
		     if(opuser.isPresent()) {
		    	 Muser user=opuser.get();
		    	 institution=user.getInstitutionName();
		    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
		   	    	if(!adminIsactive) {
		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		   	    	}
		     }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }
	         if("ADMIN".equals(role)) {
	         List<CourseDetail> allCoursesOrderedByUserCount = coursedetailrepository.findAllByInstitutionNameOrderByUserCountDesc(institution);
	         List<CourseDetail> top4Courses = allCoursesOrderedByUserCount.stream().limit(4).collect(Collectors.toList());
	        
	         top4Courses.forEach(course -> {
	        	 
	             course.setUsers(null);
	             course.setTrainer(null);
	             course.setVideoLessons(null);
	         });

	         return ResponseEntity.ok()
	             .contentType(MediaType.APPLICATION_JSON)
	             .body(top4Courses);
	         }
	         else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	     } catch (Exception e) {
	         e.printStackTrace();    logger.error("", e);; // You can replace this with logging framework like Log4j
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }
	 
	 
	


}
