package com.knowledgeVista.Attendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.knowledgeVista.Attendance.Repo.AttendanceRepo;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;


@Service
public class AttendanceService {
	@Autowired
	private AttendanceRepo attendanceRepo;
	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
		private MuserRepositories muserRepository;
	private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

	
	public ResponseEntity<?> getAttendance(String token, Long userId, Pageable pageable) {
	    try {
	        if (!jwtUtil.validateToken(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	        }
	        String role = jwtUtil.getRoleFromToken(token);
	        if (userId == null) {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	        }
	        if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
	            Page<AttendanceDto> attendancePage = attendanceRepo.findAttendanceByUserId(userId, pageable);
	            return ResponseEntity.ok(attendancePage);
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Students Cannot access This Page");
	        }
	    } catch (Exception e) {
	        logger.error("Error at getAttendance in AttendanceService for user ID {}: {}", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching attendance data");
	    }
	}

	  

public ResponseEntity<?> getMyAttendance(String token, Pageable pageable) {
    try {
    	 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    	 }  	
	         String role=jwtUtil.getRoleFromToken(token); 
	         if("USER".equals(role)) {
	        	 String email=jwtUtil.getUsernameFromToken(token);
	        	 Long userId=muserRepository.findidByEmail(email);
	        	 if(userId==null) {
	        		 return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	        	 }
	        	 Page<AttendanceDto> attendancePage = attendanceRepo.findAttendanceByUserId(userId, pageable);		          
		            return ResponseEntity.ok(attendancePage);
	         }else
	         {return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only Students Can access This Page");      
	    	       
	        	 
	        	
	         }
    } catch (Exception e) {
        logger.error("Error at getAttendance in AttendanceService for user ID {}: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching attendance data");
    }
}
}
