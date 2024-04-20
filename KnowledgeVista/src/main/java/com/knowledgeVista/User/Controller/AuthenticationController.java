package com.knowledgeVista.User.Controller;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;


import com.knowledgeVista.User.Repository.*;

import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.knowledgeVista.User.SecurityConfiguration.TokenBlacklist;

@RestController
@CrossOrigin
public class AuthenticationController {
	 @Autowired
	    private MuserRepositories muserRepositories;

	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
	    private TokenBlacklist tokenBlacklist;

	    @PostMapping("/logout")
	    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
	        // Extract the token from the Authorization header
	        // Check if the token is valid (e.g., not expired)
	        // Blacklist the token to invalidate it
	        tokenBlacklist.blacklistToken(token);

	        // Respond with a success message
	        return ResponseEntity.ok().body("Logged out successfully");
	    }
	    @Transactional
	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
	        String username = loginRequest.get("username");
	        String password = loginRequest.get("password");

	        Optional<Muser> userOptional = muserRepositories.findByEmail(username);

	        if (userOptional.isPresent()) {
	            Muser user = userOptional.get();
	            if (user.getPsw().equals(password)) {
	                // Correct username and password
	                // Generate JWT token
	            	String Role=user.getRole().getRoleName();
	                String jwtToken = jwtUtil.generateToken(username,Role);

	                // Prepare response body as JSON
	                Map<String, Object> responseBody = new HashMap<>();
	                responseBody.put("token", jwtToken);
	                responseBody.put("message", "Login successful");
	                responseBody.put("role", user.getRole().getRoleName());
	                responseBody.put("email", user.getEmail());
	                responseBody.put("userid",user.getUserId());

	                // Return response with JSON body
	                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody);
	            } else {
	                // Incorrect password
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Incorrect password\"}");
	            }
	        } else {
	            // User with the provided username doesn't exist
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
	        }
	    }
		
	 
	 
//	 @GetMapping("/dashboard")
//	 public ResponseEntity<String> getDashboard(@RequestHeader("Authorization") String token) {
//	     if (jwtUtil.validateToken(token)) {
//	         // User is authenticated
//	         // Return dashboard data
//	         return ResponseEntity.ok("Dashboard data");
//	     } else {
//	         // Unauthorized access
//	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
//	     }
//	 }

	 
	    //````````````````````````````````````````````````for view`````````````````````````````````````````````
	    @GetMapping("/getuser")
	 
	    	  public String getuser() {
	    	        StringBuilder result = new StringBuilder();
	    	        
	    	        // Retrieve all users from the database
	    	        Iterable<Muser> users = muserRepositories.findAll();
	    	        
	    	        // Iterate through each user
	    	        for (Muser user : users) {
	    	            // Append user details to the result string
	    	            result.append("User ID: ").append(user.getUserId()).append("\n");
	    	            result.append("Username: ").append(user.getUsername()).append("\n");
	    	            result.append("Password: ").append(user.getPsw()).append("\n");
	    	            result.append("Email: ").append(user.getEmail()).append("\n");
	    	            result.append("Date of Birth: ").append(user.getDob()).append("\n");
	    	            result.append("Role: ").append(user.getRole().getRoleName()).append("\n");
	    	            result.append("Active: ").append(user.getIsActive()).append("\n");
	    	            // Add other fields similarly
	    	            
	    	            // Add a separator between users
	    	            result.append("--------------------\n");
	    	        }
	    	        
	    	        return result.toString();
	    	    }
	  

	    
	    

	    
	    //````````````````````````````````````````````````for view`````````````````````````````````````````````
	    @Transactional
	    @PostMapping("/forgetpassword")
	    public ResponseEntity<?> forgetPassword(@RequestParam("email") String email) {
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

	    @Transactional
	    @PostMapping("/resetpassword")
	    public ResponseEntity<?> resetPassword(@RequestParam("email") String email, @RequestParam("password") String newPassword) {
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