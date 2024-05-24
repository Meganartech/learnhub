package com.knowledgeVista.Course.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.Test.Repository.MusertestactivityRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;

@RestController
@RequestMapping("/Delete")
public class DeleteApis {
	 @Autowired
		private MuserRepositories muserrepositories;
		
	    @Autowired
	    private MusertestactivityRepo activityrepo;
	    @GetMapping("/Delete/Trainer/{email}")
		 public ResponseEntity<?> deleteTrainer(@PathVariable String email) {
		      try {
		          // Validate the token
		         
		              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
		              if (existingUser.isPresent()) {
		                  Muser user = existingUser.get();
		                  if ("TRAINER".equals(user.getRole().getRoleName())) {
		                      // Clear user's courses and delete the user
		                	  user.getAllotedCourses().clear();
		                      user.getCourses().clear();
		                      muserrepositories.delete(user);
		                      return ResponseEntity.ok().body("{\"message\": \"Deleted Successfully\"}");
		                  } 
		                  return ResponseEntity.notFound().build();
		              } else {
		                  // Return not found if the user with the given email does not exist
		                  return ResponseEntity.notFound().build();
		              }
		          
		      } catch (Exception e) {
		          // Log any other exceptions for debugging purposes
		          e.printStackTrace(); // You can replace this with logging framework like Log4j
		          // Return an internal server error response
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		      }
		  }
		  

		 @GetMapping("/Delete/User/{email}") 
		  public ResponseEntity<?> deleteStudent(@PathVariable String email) {
		      try {
		         	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
		              if (existingUser.isPresent()) {
		                  Muser user = existingUser.get();
		                  if ("USER".equals(user.getRole().getRoleName())) {
		                      // Clear user's courses and delete the user
		                      user.getCourses().clear();
		                      activityrepo.deleteByUser(user);
		                      muserrepositories.delete(user);
		                      return ResponseEntity.ok().body("{\"message\": \"Deleted Successfully\"}");
		                  } 

			                  // Return not found if the user with the given email does not exist
			                  return ResponseEntity.notFound().build();
		                  
		              } else {
		                  // Return not found if the user with the given email does not exist
		                  return ResponseEntity.notFound().build();
		              }
		          
		      } catch (Exception e) {
		          // Log any other exceptions for debugging purposes
		          e.printStackTrace(); // You can replace this with logging framework like Log4j
		          // Return an internal server error response
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		      }
		  }



}
