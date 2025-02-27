package com.knowledgeVista.Batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@Service
public class GradeService {
	
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MuserRepositories muserRepo;
    private static final Logger logger = LoggerFactory.getLogger(GradeService.class);
	
//	public ResponseEntity<?>getGrades(String token){
//		try {
//			if (!jwtUtil.validateToken(token)) {
//	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//	         }
//
//	         String role = jwtUtil.getRoleFromToken(token);
//	         String email = jwtUtil.getUsernameFromToken(token);
//	         String institutionName=muserRepo.findinstitutionByEmail(email);
//	         if(institutionName==null) {
//	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User Institution Not Found");
//	         }
//	         if(!"USER".equals(role)) {
//	        	 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("only Studennts Can Access This Page");
//	         }
//		}catch (Exception e) {
//			// TODO: handle exception
//			logger.error("error Fetching Grade Points"+e);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//	}

}
