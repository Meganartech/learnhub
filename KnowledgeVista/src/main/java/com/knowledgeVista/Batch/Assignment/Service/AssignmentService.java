package com.knowledgeVista.Batch.Assignment.Service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.knowledgeVista.Batch.Assignment.Assignment;
import com.knowledgeVista.Batch.Assignment.Repo.AssignmentRepo;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@Service
public class AssignmentService {
	@Autowired
	private AssignmentRepo assignmentRepo;
	@Autowired
	private CourseDetailRepository courseDetailRepo;
	@Autowired
	private MuserRepositories muserRepo;
	 @Autowired
	 private JwtUtil jwtUtil;
	private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);
	
	public ResponseEntity<?> saveAssignment(String token, Assignment assignment, Long courseId) {
	    try {
	        if (!jwtUtil.validateToken(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	        }
	        String email = jwtUtil.getUsernameFromToken(token);
	        Optional<Muser> optionalUser = muserRepo.findByEmail(email);
	        if (optionalUser.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
	        }
	        Muser addingUser = optionalUser.get();
	        String role = addingUser.getRole().getRoleName();
	        if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Save Assignment");
	        }
	        Optional<CourseDetail> optionalcourse = courseDetailRepo.findById(courseId);
	        if (optionalcourse.isEmpty()) {
	        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Course Not Found");
	        }
	        CourseDetail Course = optionalcourse.get();
	        if ("ADMIN".equals(role)) {
	            return saveAssignmentService(assignment, Course);
	        } else if("TRAINER".equals(role)) {
                   if( Course.getTrainer().contains(addingUser)) {
                	   for(Muser user: Course.getTrainer()) {
                		   System.out.println(user.getUsername());
                	   }
                	   System.out.println("herere");
                	   return saveAssignmentService(assignment, Course);
                   }
	        else {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This Course Was Not Assigned To You");
	        }
	        }else {
	        	 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Students Cannot Acces this Page");
	        }
	    } catch (Exception e) {
	        logger.error("Error Saving Assignment", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred in Saving Assignment: " + e.getMessage());
	    }
	}

	private ResponseEntity<?> saveAssignmentService(Assignment assignment, CourseDetail course) {
	    try {
	        // Set the course
	        assignment.setCourseDetail(course);

	        // Ensure each question references this assignment
	        if (assignment.getQuestions() != null) {
	            assignment.getQuestions().forEach(question -> question.setAssignment(assignment));
	        }

	        // Save the assignment (which will cascade save questions if properly configured)
	        assignmentRepo.save(assignment);

	        return ResponseEntity.ok("Assignment Saved Successfully");
	    } catch (Exception e) {
	        logger.error("Error at SaveAssignment Service", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error at Saving Assignment: " + e.getMessage());
	    }
	}


}
