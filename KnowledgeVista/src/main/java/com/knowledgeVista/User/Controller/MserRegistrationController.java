package com.knowledgeVista.User.Controller;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserRoles;
import com.knowledgeVista.User.Repository.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

import io.jsonwebtoken.io.DecodingException;
import jakarta.transaction.Transactional;
@RestController
@RequestMapping("/student")
@CrossOrigin
public class MserRegistrationController {
	@Autowired
	private MuserRepositories muserrepositories;
	 @Autowired
	 private JwtUtil jwtUtil;
	@Autowired
	private MuserRoleRepository muserrolerepository;
	

	@PostMapping("/register")
	public ResponseEntity<?> registerStudent(@RequestParam("username") String username,
	                                          @RequestParam("psw") String psw,
	                                          @RequestParam("email") String email,
	                                          @RequestParam("dob") LocalDate dob,
	                                          @RequestParam("phone") String phone,
	                                          @RequestParam("skills") String skills,
	                                          @RequestParam("profile") MultipartFile profile,
	                                          @RequestParam("isActive") Boolean isActive) {
	    try {
	        long userCount = muserrepositories.count();
	        Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	        if (userCount > 0) {
	            if (existingUser.isPresent()) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"EMAIL ALREADY EXISTS\"}");
	            } else {
	                MuserRoles roleUser = muserrolerepository.findByroleName("USER");
	                Muser user = new Muser();
	                user.setUsername(username);
	                user.setEmail(email);
	                user.setIsActive(isActive);
	                user.setPsw(psw);
	                user.setPhone(phone);
	                user.setDob(dob);
	                user.setSkills(skills);
	                user.setRole(roleUser);
	                try {
	                    user.setProfile(ImageUtils.compressImage(profile.getBytes()));
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error compressing image\"}");
	                }
	                muserrepositories.save(user);
	            }
	        } else {
	            MuserRoles roleuser = new MuserRoles();
	            roleuser.setRoleName("USER");
	            roleuser.setIsActive(true);
	            roleuser.setUsers(null);
	            muserrolerepository.save(roleuser);

	            MuserRoles roletrainer = new MuserRoles();
	            roletrainer.setRoleName("TRAINER");
	            roletrainer.setIsActive(true);
	            roletrainer.setUsers(null);
	            muserrolerepository.save(roletrainer);

	            MuserRoles role = new MuserRoles();
	            role.setRoleName("ADMIN");
	            role.setIsActive(true);
	            role.setUsers(null);
	            MuserRoles savedroleadmin = muserrolerepository.save(role);
	            Muser user = new Muser();
	            user.setUsername(username);
	            user.setEmail(email);
	            user.setIsActive(isActive);
	            user.setPsw(psw);
	            user.setPhone(phone);
	            user.setDob(dob);
	            user.setSkills(skills);
	            user.setRole(savedroleadmin);
	            try {
	                user.setProfile(ImageUtils.compressImage(profile.getBytes()));
	            } catch (IOException e) {
	                e.printStackTrace();
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error compressing image\"}");
	            }
	            muserrepositories.save(user);
	        }
	        return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Internal Server Error\"}");
	    }
	}

	

	@GetMapping("/users/{email}")
	public ResponseEntity<Muser> getUserByEmail(@PathVariable String email) {
	    try {
	        // Attempt to find a user by email
	        Optional<Muser> userOptional = muserrepositories.findByEmail(email);
	        
	        // If the user is found, process the user data
	        if (userOptional.isPresent()) {
	            Muser user = userOptional.get();

	            // Decompress the profile image and set it in the user object
	            byte[] profileImage = ImageUtils.decompressImage(user.getProfile());
	            user.setProfile(profileImage);
               user.setAllotedCourses(null);
	            user.setCourses(null);
	            user.setPsw(null);
	            user.setRole(null);
	            // Return the user object in the response
	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .body(user);
	        } else {
	            // If the user is not found, return a 404 not found response
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        // Log the exception for debugging and auditing purposes
	        e.printStackTrace(); // Consider using a proper logging framework in production code

	        // Return a 500 internal server error response
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(null);
	    }
	}

       

	    @GetMapping("/admin/getTrainer/{email}")
	    public ResponseEntity<?> getTrainerDetailsByEmail(@PathVariable String email,
	                                                          @RequestHeader("Authorization") String token) {

	        try {
	            // Validate the token
	            if (!jwtUtil.validateToken(token)) {
	                // If the token is not valid, return unauthorized status
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }

	            String role = jwtUtil.getRoleFromToken(token);

	            // Perform authentication based on role
	            if ("ADMIN".equals(role)) {
	                Optional<Muser> userOptional = muserrepositories.findByEmail(email);
	                if (userOptional.isPresent()) {
	                    Muser user = userOptional.get();
	                    if("TRAINER".equals(user.getRole().getRoleName())) {
	                    byte[] profileImage = ImageUtils.decompressImage(user.getProfile());
	                    user.setProfile(profileImage);
	                    user.setAllotedCourses(null);
	                    user.setCourses(null);

	                    return ResponseEntity.ok()
	                            .contentType(MediaType.APPLICATION_JSON)
	                            .body(user);}
	                    else {
	   	   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Trainer not found\"}");
	                            }
	                    
	                } else {
	   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Trainer not found\"}");
	                }
	            } else {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with logging framework like Log4j
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	    
	    
	    @GetMapping("/admin/getstudent/{email}")
	    public ResponseEntity<?> getStudentDetailsByEmail(@PathVariable String email,
	                                                          @RequestHeader("Authorization") String token) {

	        try {
	            // Validate the token
	            if (!jwtUtil.validateToken(token)) {
	                // If the token is not valid, return unauthorized status
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }

	            String role = jwtUtil.getRoleFromToken(token);

	            // Perform authentication based on role
	            if ("ADMIN".equals(role)||"TRAINER".equals(role)) {
	                Optional<Muser> userOptional = muserrepositories.findByEmail(email);
	                if (userOptional.isPresent()) {
	                    Muser user = userOptional.get();
	                    if("USER".equals(user.getRole().getRoleName())) {
	                    byte[] profileImage = ImageUtils.decompressImage(user.getProfile());
	                    user.setProfile(profileImage);
	                    user.setCourses(null);
	                    user.setAllotedCourses(null);

	                    return ResponseEntity.ok()
	                            .contentType(MediaType.APPLICATION_JSON)
	                            .body(user);}
	                    else {

	   	   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Student not found\"}");
	                            }
	                    
	                } else {
		   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Student not found\"}");
	                }
	            } else {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with logging framework like Log4j
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }


    
  @Transactional
	  @GetMapping("/viewall")
	  public  ResponseEntity<List<Muser>> viewUsers() {
	        List<Muser> users = muserrepositories.findAll();
	        // Decompress image data for each course
	        
	        for (Muser user : users) {
	        	
	        	 byte[] images =ImageUtils.decompressImage(user.getProfile());
	            user.setProfile(images);
	         
	        }
	        return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(users);
	    }
	  
 
	  @GetMapping("/usersbyid/{id}")
	  public ResponseEntity<Muser> getUserByid(@PathVariable Long id) {
	      Optional<Muser> userOptional = muserrepositories.findById(id);
	      if(userOptional.isPresent()) {
	          Muser user = userOptional.get();
	          byte[] profileImage = ImageUtils.decompressImage(user.getProfile());
	          user.setProfile(profileImage);
	          return ResponseEntity.ok()
	                  .contentType(MediaType.APPLICATION_JSON)
	                  .body(user);
	      } else {
	          // User not found, return an appropriate response
	          return ResponseEntity.notFound().build();
	      }
	  }
	  
}
	
