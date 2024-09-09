package com.knowledgeVista.SysAdminPackage;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserDto;
import com.knowledgeVista.User.Repository.MuserRepoPageable;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class SysadminController {
	@Autowired
	private MuserRepositories muserrepositories;
	 @Autowired
	 private JwtUtil jwtUtil;
	 
	 @Autowired 
	 private MuserRepoPageable muserPageRepo;
	 
	
	public ResponseEntity<Page<MuserDto>>viewAdmins(String token ,int pageNumber,int pageSize ){
		  try {
			
	        	if (!jwtUtil.validateToken(token)) {
	   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }

	   	     String role = jwtUtil.getRoleFromToken(token);
	   	     if(role.equals("SYSADMIN")) {
	   	    	Pageable pageable = PageRequest.of(pageNumber, pageSize);
	            Page<MuserDto> admins = muserPageRepo.findByRoleName("ADMIN", pageable);
	   	
         return ResponseEntity.ok(admins);
	   	     }else {
	   	      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }

		  } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }
	
	
	public ResponseEntity<Page<MuserDto>>viewTrainers(String token ,int pageNumber,int pageSize ){
		  try {
	        	if (!jwtUtil.validateToken(token)) {
	   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }

	   	     String role = jwtUtil.getRoleFromToken(token);
	   	     if(role.equals("SYSADMIN")) {
	   	    	Pageable pageable = PageRequest.of(pageNumber, pageSize);
	            Page<MuserDto> trainers = muserPageRepo.findByRoleName("TRAINER", pageable);
	   	
       return ResponseEntity.ok(trainers);
	   	     }else {
	   	      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }

		  } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }
	
	public ResponseEntity<Page<MuserDto>>viewStudents(String token,int pageNumber,int pageSize){
		  try {
	        	if (!jwtUtil.validateToken(token)) {
	   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }

	   	     String role = jwtUtil.getRoleFromToken(token);
	   	     if(role.equals("SYSADMIN")) {
	   	    	Pageable pageable = PageRequest.of(pageNumber, pageSize);
	            Page<MuserDto> students = muserPageRepo.findByRoleName("USER", pageable);
	   	
     return ResponseEntity.ok(students);
	   	     }else {
	   	      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }

		  } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

	
	 public ResponseEntity<?> DeactivateAdmin(String reason ,String email, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("SYSADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("ADMIN".equals(user.getRole().getRoleName())) {
	                     user.setIsActive(false);
	                     user.setInactiveDescription(reason);
	                     muserrepositories.save(user);
	                      return ResponseEntity.ok().body("{\"message\": \"Deactivated Successfully\"}");
	                  } 

		                  // Return not found if the user with the given email does not exist
		                  return ResponseEntity.notFound().build();
	                  
	              } else {
	                  // Return not found if the user with the given email does not exist
	                  return ResponseEntity.notFound().build();
	              }
	          } else {
	              // Return unauthorized status if the role is neither ADMIN nor TRAINER
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
	      } catch (Exception e) {
	          // Log any other exceptions for debugging purposes
	          e.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an internal server error response
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	      }
	  }


	  public ResponseEntity<?> activateAdmin(String email, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ( "SYSADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("ADMIN".equals(user.getRole().getRoleName())) {
	                     user.setIsActive(true);
	                     user.setInactiveDescription("");
	                     muserrepositories.save(user);
	                      return ResponseEntity.ok().body("{\"message\": \"Deactivated Successfully\"}");
	                  } 

		                  // Return not found if the user with the given email does not exist
		                  return ResponseEntity.notFound().build();
	                  
	              } else {
	                  // Return not found if the user with the given email does not exist
	                  return ResponseEntity.notFound().build();
	              }
	          } else {
	              // Return unauthorized status if the role is neither ADMIN nor TRAINER
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
	      } catch (Exception e) {
	          // Log any other exceptions for debugging purposes
	          e.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an internal server error response
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	      }
	  }

	
}
	    