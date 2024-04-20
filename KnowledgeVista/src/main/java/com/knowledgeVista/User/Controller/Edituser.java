package com.knowledgeVista.User.Controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.Repository.MuserRoleRepository;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
@RequestMapping("/Edit")
@CrossOrigin
public class Edituser {
	@Autowired
	private MuserRepositories muserrepositories;
	 @Autowired
	 private JwtUtil jwtUtil;
	
	 @PatchMapping("/Student/{email}")
	 public ResponseEntity<?> updateStudent(
	     @PathVariable("email") String originalEmail,
	     @RequestParam("username") String username,
	     @RequestParam("email") String newEmail,
	     @RequestParam("dob") LocalDate dob,
	     @RequestParam("phone") String phone,
	     @RequestParam("skills") String skills,
	     @RequestParam(value="profile", required=false) MultipartFile profile,
	     @RequestParam("isActive") Boolean isActive,
	     @RequestHeader("Authorization") String token
	 ) {
	     try {
	         // Validate the token
	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized access\"}");
	         }

	         // Get the role from the token
	         String role = jwtUtil.getRoleFromToken(token);

	         // Only ADMIN and TRAINER roles are allowed
	         if (!role.equals("ADMIN") && !role.equals("TRAINER")) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized access\"}");
	         }

	         // Fetch the student by the original email
	         Optional<Muser> opStudent = muserrepositories.findByEmail(originalEmail);
	         if (!opStudent.isPresent()) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Student not found\"}");
	         }

	         // Get the student object
	         Muser student = opStudent.get();

	         // If the role is not USER, return an error
	         if (!"USER".equals(student.getRole().getRoleName())) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Student not found\"}");
	         }

	         // Update student data
	         student.setUsername(username);

	         // Check if the email has changed
	         if (!originalEmail.equals(newEmail)) {
	             // Check if the new email already exists
	             Optional<Muser> existingUser = muserrepositories.findByEmail(newEmail);
	             if (existingUser.isPresent()) {
	                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"EMAIL ALREADY EXISTS\"}");
	             } else {
	                 // Update the email
	                 student.setEmail(newEmail);
	             }
	         }

	         // Update other student properties
	         student.setDob(dob);
	         student.setPhone(phone);
	         student.setSkills(skills);
	         student.setIsActive(isActive);

	         // Compress and set profile image
	         if (profile != null && !profile.isEmpty()) {
	             student.setProfile(ImageUtils.compressImage(profile.getBytes()));
	         }

	         // Save the updated student
	         muserrepositories.save(student);

	         // Return successful response
	         return ResponseEntity.ok("{\"message\": \"Student updated successfully\"}");

	     } catch (Exception e) {
	         // Log any exceptions for debugging purposes
	         e.printStackTrace(); // Use a logging framework like Log4j in production

	         // Return an internal server error response
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"An error occurred while updating the student\"}");
	     }
	 }
	 
	 
	 
	 @PatchMapping("/Trainer/{email}")
	 public ResponseEntity<?> updateTrainer(
	     @PathVariable("email") String originalEmail,
	     @RequestParam("username") String username,
	     @RequestParam("email") String newEmail,
	     @RequestParam("dob") LocalDate dob,
	     @RequestParam("phone") String phone,
	     @RequestParam("skills") String skills,
	     @RequestParam(value="profile", required=false) MultipartFile profile,
	     @RequestParam("isActive") Boolean isActive,
	     @RequestHeader("Authorization") String token
	 ) {
	     try {
	         // Validate the token
	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized access\"}");
	         }

	         // Get the role from the token
	         String role = jwtUtil.getRoleFromToken(token);

	         // Only ADMIN and TRAINER roles are allowed
	         if (!role.equals("ADMIN")) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized access\"}");
	         }

	         // Fetch the student by the original email
	         Optional<Muser> opStudent = muserrepositories.findByEmail(originalEmail);
	         if (!opStudent.isPresent()) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Trainer not found\"}");
	         }

	         // Get the student object
	         Muser student = opStudent.get();

	         // If the role is not USER, return an error
	         if (!"TRAINER".equals(student.getRole().getRoleName())) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Trainer not found\"}");
	         }

	         // Update student data
	         student.setUsername(username);

	         // Check if the email has changed
	         if (!originalEmail.equals(newEmail)) {
	             // Check if the new email already exists
	             Optional<Muser> existingUser = muserrepositories.findByEmail(newEmail);
	             if (existingUser.isPresent()) {
	                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"EMAIL ALREADY EXISTS\"}");
	             } else {
	                 // Update the email
	                 student.setEmail(newEmail);
	             }
	         }

	         // Update other student properties
	         student.setDob(dob);
	         student.setPhone(phone);
	         student.setSkills(skills);
	         student.setIsActive(isActive);

	         // Compress and set profile image
	         if (profile != null && !profile.isEmpty()) {
	             student.setProfile(ImageUtils.compressImage(profile.getBytes()));
	         }

	         // Save the updated student
	         muserrepositories.save(student);

	         // Return successful response
	         return ResponseEntity.ok("{\"message\": \"Trainer updated successfully\"}");

	     } catch (Exception e) {
	         // Log any exceptions for debugging purposes
	         e.printStackTrace(); // Use a logging framework like Log4j in production

	         // Return an internal server error response
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"An error occurred while updating the Trainer\"}");
	     }
	 }

	 
	 
	 @PatchMapping("/self")
	 public ResponseEntity<?> EditProfile(
	     @RequestParam("username") String username,
	     @RequestParam("email") String newEmail,
	     @RequestParam("dob") LocalDate dob,
	     @RequestParam("phone") String phone,
	     @RequestParam("skills") String skills,
	     @RequestParam(value="profile", required=false) MultipartFile profile,
	     @RequestParam("isActive") Boolean isActive,
	     @RequestHeader("Authorization") String token
	 ) {
	     try {
	         // Validate the token
	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Unauthorized access\"}");
	         }

	         String role = jwtUtil.getRoleFromToken(token);
	         String originalEmail=jwtUtil.getUsernameFromToken(token);
	         Optional<Muser> opStudent = muserrepositories.findByEmail(originalEmail);
	         if (!opStudent.isPresent()) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"user not found\"}");
	         }
	         Muser student = opStudent.get();

	        
	         student.setUsername(username);

	         // Check if the email has changed
	         if (!originalEmail.equals(newEmail)) {
	             // Check if the new email already exists
	             Optional<Muser> existingUser = muserrepositories.findByEmail(newEmail);
	             if (existingUser.isPresent()) {
	                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"EMAIL ALREADY EXISTS\"}");
	             } else {
	                 // Update the email
	                 student.setEmail(newEmail);
	             }
	         }

	         // Update other student properties
	         student.setDob(dob);
	         student.setPhone(phone);
	         student.setSkills(skills);
	         student.setIsActive(isActive);

	         // Compress and set profile image
	         if (profile != null && !profile.isEmpty()) {
	             student.setProfile(ImageUtils.compressImage(profile.getBytes()));
	         }

	         // Save the updated student
	         muserrepositories.save(student);

	         // Return successful response
	         return ResponseEntity.ok("{\"message\": \" your Profile updated successfully\"}");

	     } catch (Exception e) {
	         // Log any exceptions for debugging purposes
	         e.printStackTrace(); // Use a logging framework like Log4j in production

	         // Return an internal server error response
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"An error occurred while updating your profile please try again later\"}");
	     }
	 }

	 
	 @GetMapping("/profiledetails")
	 private ResponseEntity<?> NameandProfile(
	     @RequestHeader("Authorization") String token) {
		 
		 String email= jwtUtil.getUsernameFromToken(token);
		 
		Optional<Muser> opuser= muserrepositories.findByEmail(email);
		if(opuser.isPresent()) {
			Muser user=opuser.get();
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("name", user.getUsername());
            // Add profile image if needed
            byte[] images = ImageUtils.decompressImage(user.getProfile());
            responseBody.put("profileImage", images);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseBody);
			
		}
		return ResponseEntity.notFound().build();
	 }

	
}