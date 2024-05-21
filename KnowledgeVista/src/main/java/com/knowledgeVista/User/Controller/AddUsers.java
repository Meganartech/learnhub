package com.knowledgeVista.User.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.Course.Test.Repository.MusertestactivityRepo;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserRoles;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.Repository.MuserRoleRepository;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import io.jsonwebtoken.io.DecodingException;


@RestController
public class AddUsers {
	@Autowired
	private MuserRepositories muserrepositories;
	 @Autowired
	    private JwtUtil jwtUtil;
    @Autowired
    private MusertestactivityRepo activityrepo;
	@Autowired
	private MuserRoleRepository muserrolerepository;
//===========================================ADMIN ADDING TRAINER==========================================	
	
	  public ResponseEntity<?> addTrainer( String username, String psw,String email,
	          LocalDate dob, String phone, String skills, MultipartFile profile, Boolean isActive, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              System.out.println("Invalid Token");
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"EMAIL ALREADY EXISTS\"}");
	              } else {
	                  MuserRoles roletrainer = muserrolerepository.findByroleName("TRAINER");
	                  Muser trainer = new Muser();
	                  trainer.setUsername(username);
	                  trainer.setEmail(email);
	                  trainer.setIsActive(isActive);
	                  trainer.setPsw(psw);
	                  trainer.setPhone(phone);
	                  trainer.setDob(dob);
	                  trainer.setSkills(skills);
	                  trainer.setRole(roletrainer);
	                  try {
	                      trainer.setProfile(ImageUtils.compressImage(profile.getBytes()));
	                  } catch (IOException e) {
	                      e.printStackTrace();
	                  }
	                  muserrepositories.save(trainer);
	                  return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	              }
	          } else {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
	      } catch (DecodingException ex) {
	          // Log the decoding exception
	          ex.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an error response indicating invalid token
	          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	      } catch (Exception e) {
	          // Log any other exceptions for debugging purposes
	          e.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an internal server error response
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	      }
	  }
	  
//===========================================ADMIN OR TRAINER -ADDING STUDENT======================================================	  
	
	  public ResponseEntity<?> addStudent(String username, String psw, String email,
	          LocalDate dob,String phone, String skills,
	           MultipartFile profile, Boolean isActive, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              System.out.println("Invalid Token");
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role)||"TRAINER".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"EMAIL ALREADY EXISTS\"}");
	              } else {
	                  MuserRoles roletrainer = muserrolerepository.findByroleName("USER");
	                  Muser user = new Muser();
	                  user.setUsername(username);
	                  user.setEmail(email);
	                  user.setIsActive(isActive);
	                  user.setPsw(psw);
	                  user.setPhone(phone);
	                  user.setDob(dob);
	                  user.setRole(roletrainer);
	                  user.setSkills(skills);	                  
	                  try {
	                     user.setProfile(ImageUtils.compressImage(profile.getBytes()));
	                  } catch (IOException e) {
	                      e.printStackTrace();
	                  }
	                  muserrepositories.save(user);
	                  return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	              }
	          } else {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
	      } catch (DecodingException ex) {
	          // Log the decoding exception
	          ex.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an error response indicating invalid token
	          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	      } catch (Exception e) {
	          // Log any other exceptions for debugging purposes
	          e.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an internal server error response
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	      }
	  }

	  
	  public ResponseEntity<?> DeactivateTrainer( String email,String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("TRAINER".equals(user.getRole().getRoleName())) {
	                     user.setIsActive(false);
	                     muserrepositories.save(user);
	                      return ResponseEntity.ok().body("{\"message\": \"DeActivated Successfully\"}");
	                  } 
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
	  public ResponseEntity<?> activateTrainer( String email,String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("TRAINER".equals(user.getRole().getRoleName())) {
	                     user.setIsActive(true);
	                     muserrepositories.save(user);
	                      return ResponseEntity.ok().body("{\"message\": \"DeActivated Successfully\"}");
	                  } 
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
	  
	   
	  public ResponseEntity<?> DeactivateStudent(String email, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("USER".equals(user.getRole().getRoleName())) {
	                     user.setIsActive(false);
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


	  public ResponseEntity<?> activateStudent(String email, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("USER".equals(user.getRole().getRoleName())) {
	                     user.setIsActive(true);
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
