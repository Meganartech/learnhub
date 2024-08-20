package com.knowledgeVista.User.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.knowledgeVista.License.licenseRepository;
import com.knowledgeVista.Notification.Service.NotificationService;
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
	
	 @Autowired
	private NotificationService notiservice;
	 
	 @Autowired
	 private licenseRepository licencerepo;
//===========================================ADMIN ADDING TRAINER==========================================	
	
	  public ResponseEntity<?> addTrainer( String username, String psw,String email,
	          LocalDate dob, String phone, String skills, MultipartFile profile, Boolean isActive, String countryCode,String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              System.out.println("Invalid Token");
	             
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);
              String adminemail=jwtUtil.getUsernameFromToken(token);
             
	          // Perform authentication based on role
	          if ("ADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              Optional<Muser> existingusername=muserrepositories.findByname(username);
	  	        if(existingusername.isPresent()) {
	  	        	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NAME");
	  	        }
	              if (existingUser.isPresent()) {
	                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EMAIL");
	              } else {
	                  MuserRoles roletrainer = muserrolerepository.findByroleName("TRAINER");
	                  Muser trainer = new Muser();
	                  Optional<Muser>addingadmin=muserrepositories.findByEmail(adminemail);
	                  if(addingadmin.isPresent()) {
	                	  boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", addingadmin.get().getInstitutionName());
	      	   	    	if(!adminIsactive) {
	      	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	      	   	    	}
	      	   	    	Muser adding=addingadmin.get();
	      	   	    	
	                    trainer.setInstitutionName(adding.getInstitutionName());
	                 
	                  trainer.setUsername(username);
	                  trainer.setEmail(email);
	                  trainer.setIsActive(isActive);
	                  trainer.setPsw(psw);
	                  trainer.setPhone(phone);
	                  trainer.setDob(dob);
	                  trainer.setSkills(skills);
	                  trainer.setRole(roletrainer);
	                  trainer.setCountryCode(countryCode);
	                  if (profile != null && !profile.isEmpty()) { 
	                  try {
	                      trainer.setProfile(ImageUtils.compressImage(profile.getBytes()));
	                  } catch (IOException e) {
	                      e.printStackTrace();
	                  }
	                  }
	                Muser savedtrainer=  muserrepositories.save(trainer);
	                  


	                  return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	                  }else {

	    	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	                  }
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
	           MultipartFile profile, Boolean isActive,String countryCode, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              System.out.println("Invalid Token");
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);
	          String emailofadd=jwtUtil.getUsernameFromToken(token);
	          String usernameofadding="";
	          String emailofadding="";
	          String instituiton="";
	          Optional<Muser> optiUser = muserrepositories.findByEmail(emailofadd);
              if (optiUser.isPresent()) {
            	  Muser addinguser= optiUser.get();
            	  usernameofadding=addinguser.getUsername();
            	  emailofadding=addinguser.getEmail();
            	   instituiton=addinguser.getInstitutionName();
            	   
            	   boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", instituiton);
     	   	    	if(!adminIsactive) {
     	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
     	   	    	}
              }else {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
              }

	          // Perform authentication based on role
	          if ("ADMIN".equals(role)||"TRAINER".equals(role)) {
	        	  Optional<Muser> existingusername=muserrepositories.findByname(username);
	  	        if(existingusername.isPresent()) {
	  	        	 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NAME");
	  	        }
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EMAIL");
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
	                  user.setInstitutionName(instituiton);
	                  user.setSkills(skills);	
	                  user.setCountryCode(countryCode);
	                  if (profile != null && !profile.isEmpty()) { 
	                  try {
	                     user.setProfile(ImageUtils.compressImage(profile.getBytes()));
	                  } catch (IOException e) {
	                      e.printStackTrace();
	                  }
	                  }
	                 Muser saveduser= muserrepositories.save(user);
		       	       String heading="New Student Added !";
		       	       String link="/view/Student/profile/"+saveduser.getEmail();
		       	       String notidescription= "A new Student "+saveduser.getUsername() + " was added";
		       	       
		       	      Long NotifyId =  notiservice.createNotification("UserAdd",usernameofadding,notidescription ,emailofadding,heading,link, Optional.ofNullable(profile));
		       	        if(NotifyId!=null) {
		       	        	List<String> notiuserlist = new ArrayList<>(); 
		       	        	notiuserlist.add("ADMIN");
		       	        	notiservice.CommoncreateNotificationUser(NotifyId,notiuserlist,instituiton);
		       	        }
	                 
	                  return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	              }
	          } else {

	                 System.out.println("not a trainer or admin");
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
	      } catch (DecodingException ex) {
	          // Log the decoding exception
	          ex.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an error response indicating invalid token

              System.out.println("catch ");
	          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	      } catch (Exception e) {
	          // Log any other exceptions for debugging purposes
	          e.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an internal server error response
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	      }
	  }

	  
	  public ResponseEntity<?> DeactivateTrainer(String reason,String email,String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role) || "SYSADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("TRAINER".equals(user.getRole().getRoleName())) {
	                     user.setIsActive(false);
	                     user.setInactiveDescription(reason);
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
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role)||"SYSADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("TRAINER".equals(user.getRole().getRoleName())) {
	                     user.setIsActive(true);
	                     user.setInactiveDescription("");
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
	  
	   
	  public ResponseEntity<?> DeactivateStudent(String reason,String email, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role) || "TRAINER".equals(role) ||"SYSADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("USER".equals(user.getRole().getRoleName())) {
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


	  public ResponseEntity<?> activateStudent(String email, String token) {
	      try {
	          // Validate the token
	          if (!jwtUtil.validateToken(token)) {
	              // If the token is not valid, return unauthorized status
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role) || "TRAINER".equals(role) || "SYSADMIN".equals(role)) {
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("USER".equals(user.getRole().getRoleName())) {
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
