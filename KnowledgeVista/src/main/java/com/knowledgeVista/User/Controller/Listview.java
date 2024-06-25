package com.knowledgeVista.User.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.Repository.MuserRoleRepository;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class Listview {
	@Autowired
	private MuserRepositories muserrepositories;
	 @Autowired
	 private JwtUtil jwtUtil;
	
	
//```````````````WORKING````````````````````````````````````

    public ResponseEntity<List<Muser>> getUsersByRoleName(String token) {
        try {
        	if (!jwtUtil.validateToken(token)) {
   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	     }

   	     String role = jwtUtil.getRoleFromToken(token);
   	     if("ADMIN".equals(role)||"TRAINER".equals(role)){
            List<Muser> users = muserrepositories.findByRoleName("USER");
           
            users.forEach(user -> {
                byte[] decompressedImage = ImageUtils.decompressImage(user.getProfile());
                user.setProfile(decompressedImage);
                user.setCourses(null);
            });
            return ResponseEntity.ok(users);
   	     }else {

   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	     }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
    public ResponseEntity<List<Muser>> GetStudentsOfTrainer(String token) {
        try {
        	if (!jwtUtil.validateToken(token)) {
   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	     }

   	     String role = jwtUtil.getRoleFromToken(token);
   	     String email=jwtUtil.getUsernameFromToken(token);
   	  ArrayList<Muser> students= new ArrayList<Muser>();
   	     if("TRAINER".equals(role)){
           Optional<Muser> opusers = muserrepositories.findByEmail(email);
            if(opusers.isPresent()) {
            	Muser trainer=opusers.get();
            	List<CourseDetail> courses =trainer.getAllotedCourses();
            	for(CourseDetail course : courses) {
            		students.addAll(course.getUsers());
            		
            	}
            }
            List<Muser> Uniquestudents = students.stream().distinct().collect( Collectors.toList());
            Uniquestudents.forEach(user -> {
                byte[] decompressedImage = ImageUtils.decompressImage(user.getProfile());
                user.setProfile(decompressedImage);
                user.setCourses(null);
            });
            return ResponseEntity.ok(Uniquestudents);
   	     }else {

   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	     }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }  
    
    
    
    
//```````````````WORKING````````````````````````````````````
    
    public ResponseEntity<Muser> getUserById( Long userId,String token) {
        try {
        	if (!jwtUtil.validateToken(token)) {
      	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      	     }

      	     String role = jwtUtil.getRoleFromToken(token);
      	     if("ADMIN".equals(role)||"TRAINER".equals(role)){
            Muser user = muserrepositories.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
      
            byte[] decompressedImage = ImageUtils.decompressImage(user.getProfile());
            user.setProfile(decompressedImage);
            user.setCourses(null);

        	user.setAllotedCourses(null);
            return ResponseEntity.ok(user);
      	   }else {

     	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
     	     }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

//```````````````WORKING````````````````````````````````````
public ResponseEntity<List<Muser>> getTrainerByRoleName( String token) {
	
    try {
    	if (!jwtUtil.validateToken(token)) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }

	     String role = jwtUtil.getRoleFromToken(token);
	     if("ADMIN".equals(role)){
	    		 List<Muser> users = muserrepositories.findByRoleName("TRAINER");
       
        users.forEach(user -> {
        	user.setAllotedCourses(null);
            byte[] decompressedImage = ImageUtils.decompressImage(user.getProfile());
            user.setProfile(decompressedImage);
            user.setCourses(null);
        });
        return ResponseEntity.ok(users);
        
	     }else {
	    	  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
}