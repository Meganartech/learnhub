package com.knowledgeVista.User.Controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.User.Muser;


import com.knowledgeVista.User.Repository.*;

import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.knowledgeVista.User.SecurityConfiguration.TokenBlacklist;

@RestController
public class AuthenticationController {
	 @Autowired
	    private MuserRepositories muserRepositories;

	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
	    private TokenBlacklist tokenBlacklist;

	 private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	    public ResponseEntity<String> logout(String token) {
	    	
	      String email=jwtUtil.getUsernameFromToken(token);
	        Optional<Muser> userOptional = muserRepositories.findByEmail(email);
	        if (userOptional.isPresent()) {
	            Muser user = userOptional.get();
	            user.setLastactive(LocalDateTime.now());
                muserRepositories.save(user);
	        tokenBlacklist.blacklistToken(token);
	        }
	        // Respond with a success message
	        return ResponseEntity.ok().body("Logged out successfully");
	    }
	    public ResponseEntity<?>refreshtoken(String token){
	    	try {
	    	String newtoken=jwtUtil.refreshToken(token);
	    	return ResponseEntity.ok().body(newtoken);
	    	
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    		 logger.error("", e);
		         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    	}
	    }
	    
	    public ResponseEntity<?> login( Map<String, String> loginRequest) {
	        String username = loginRequest.get("username");
	        String password = loginRequest.get("password");
	        Optional<Muser> userOptional = muserRepositories.findByEmail(username);
	        if (userOptional.isPresent()) {
	            Muser user = userOptional.get();
	          String institution= user.getInstitutionName();
	            if (user.getPsw().equals(password)) {
	            	if(user.getIsActive().equals(true)) {
	            		if(user.getRole().getRoleName().equals("USER")||user.getRole().getRoleName().equals("TRAINER")) {
			                Boolean isActiveAdmin=muserRepositories.getactiveResultByInstitutionName("ADMIN", institution);            
			                if(isActiveAdmin.equals(true)) {
				            	String Role=user.getRole().getRoleName();
				                String jwtToken = jwtUtil.generateToken(username,Role);
			                    user.setLastactive(LocalDateTime.now());
			                    muserRepositories.save(user);
				                // Prepare response body as JSON
				                Map<String, Object> responseBody = new HashMap<>();
				                responseBody.put("token", jwtToken);
				                responseBody.put("message", "Login successful");
				                responseBody.put("role", user.getRole().getRoleName());
				                responseBody.put("email", user.getEmail());
				                responseBody.put("userid",user.getUserId());
			
				                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody);
				                
			                }else {
	                	  Map<String, Object> responseBody = new HashMap<>();
	                	  responseBody.put("message", "In Active");
	                	  responseBody.put("Description","Your Institution was In Active");
	                	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
	                }
	            		}else {
	            			String Role=user.getRole().getRoleName();
			                String jwtToken = jwtUtil.generateToken(username,Role);
			                user.setLastactive(LocalDateTime.now());
		                    muserRepositories.save(user);
			                // Prepare response body as JSON
			                Map<String, Object> responseBody = new HashMap<>();
			                responseBody.put("token", jwtToken);
			                responseBody.put("message", "Login successful");
			                responseBody.put("role", user.getRole().getRoleName());
			                responseBody.put("email", user.getEmail());
			                responseBody.put("userid",user.getUserId());
		
			                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody);
	            		}
	            	}else {
	            		 Map<String, Object> responseBody = new HashMap<>();
	                	  responseBody.put("message", "In Active");
	                	  responseBody.put("Description",user.getInactiveDescription());
	                	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
	            	}
	            } else {
	           	 Map<String, Object> responseBody = new HashMap<>();
           	  responseBody.put("message", "Incorrect password");
           	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
	            }
	        } else {
	            // User with the provided username doesn't exist
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
	        }
	    }
		
	 

	    
	    //````````````````````````````````````````````````for view`````````````````````````````````````````````

	    public ResponseEntity<?> forgetPassword( String email) {
	        // Finding the user by email
	        Optional<Muser> userOptional = muserRepositories.findByEmail(email);

	        // If the user doesn't exist, return 404 Not Found
	        if (userOptional.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        } else {
	            // If the user exists, return 200 OK
	            return ResponseEntity.ok().build();
	        }
	    }

	    public ResponseEntity<?> resetPassword( String email,  String newPassword) {
	        // Finding the user by email
	        Optional<Muser> userOptional = muserRepositories.findByEmail(email);

	        // If the user doesn't exist, return 404 Not Found
	        if (userOptional.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        } else {
	            Muser validUser = userOptional.get();
	            validUser.setPsw(newPassword);
	            muserRepositories.save(validUser);
	            return ResponseEntity.ok().build();
	        }
	    }
}