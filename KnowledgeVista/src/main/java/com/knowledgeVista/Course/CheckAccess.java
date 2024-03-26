package com.knowledgeVista.Course;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;

@RestController
@RequestMapping("/CheckAccess")
@CrossOrigin
public class CheckAccess {
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	@Autowired
	private MuserRepositories muserRepository;

	@PostMapping("/match")
	public ResponseEntity<?> checkAccess(@RequestBody Map<String, Long> requestData) {
	    try {
	        Long courseId = requestData.get("courseId");
	        Long userId = requestData.get("userId");
	        Optional<CourseDetail> courseOptional = coursedetailrepository.findById(courseId);
	        Optional<Muser> optionalUser = muserRepository.findById(userId);
	        
	        if (courseOptional.isPresent() && optionalUser.isPresent()) {
	            Muser user = optionalUser.get();
	            CourseDetail course = courseOptional.get();
	            String courseUrl=course.getCourseUrl();
	            
	            if (user.getCourses().contains(course)) {
	                // User is already enrolled in the course
	                return ResponseEntity.ok().body(courseUrl);
	            } else {
	                // User is not enrolled in the course
	                return ResponseEntity.badRequest().build();
	            }
	        } else {
	            // Course or user not found
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        // Handle any exceptions
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
}