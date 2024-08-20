package com.knowledgeVista.User.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserDto;
import com.knowledgeVista.User.Repository.MuserRepoPageable;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.Repository.MuserRoleRepository;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class Listview {
	@Autowired
	private MuserRepositories muserrepositories;
	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired 
	 private MuserRepoPageable muserPageRepo;
	
	
//```````````````WORKING````````````````````````````````````

    public ResponseEntity<Page<MuserDto>> getUsersByRoleName(String token ,int pageNumber,int pageSize) {
        try {
        	if (!jwtUtil.validateToken(token)) {
   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	     }

   	     String role = jwtUtil.getRoleFromToken(token);
   	     String roleu="USER";
   	     String email=jwtUtil.getUsernameFromToken(token);
   	     Optional<Muser>opreq=muserrepositories.findByEmail(email);
   	     String institution="";
   	     if(opreq.isPresent()) {
   	    	 Muser requser=opreq.get();
   	    	institution=requser.getInstitutionName();
   	    	boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", institution);
   	    	if(!adminIsactive) {
   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	    	}
   	     }else {
   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
   	     }
   	     if("ADMIN".equals(role)||"TRAINER".equals(role)){
   	    	Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<MuserDto> users = muserPageRepo.findByRoleNameAndInstitutionName(roleu, institution,pageable);
           
          
            return ResponseEntity.ok(users);
   	     }else {

   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	     }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
    public ResponseEntity<Page<MuserDto>> GetStudentsOfTrainer(String token,int page, int size) {
        try {
        	if (!jwtUtil.validateToken(token)) {
   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	     }

   	     String role = jwtUtil.getRoleFromToken(token);
   	     String email=jwtUtil.getUsernameFromToken(token);
   	     if("TRAINER".equals(role)){
           Optional<Muser> opusers = muserrepositories.findByEmail(email);
            if(opusers.isPresent()) {
            	Muser trainer=opusers.get();
            	String institution=trainer.getInstitutionName();
            	boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", institution);
       	    	if(!adminIsactive) {
       	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       	    	}
       	        Pageable pageable = PageRequest.of(page, size);
       	    	Page<MuserDto> Uniquestudents=muserPageRepo.findStudentsOfTrainer(email, pageable);
            return ResponseEntity.ok(Uniquestudents);
   	     }else {

   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	     }
   	     }else {

      	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      	     }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }  
    
    
    
    
//```````````````WORKING````````````````````````````````````
    
    public ResponseEntity<?> getUserById( Long userId,String token) {
        try {
        	if (!jwtUtil.validateToken(token)) {
      	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      	     }

      	     String role = jwtUtil.getRoleFromToken(token);
      	   String email=jwtUtil.getUsernameFromToken(token);
	   	     Optional<Muser>opreq=muserrepositories.findByEmail(email);
	   	     String institution="";
	   	     if(opreq.isPresent()) {
	   	    	 Muser requser=opreq.get();
	   	    	institution=requser.getInstitutionName();
	   	    	boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", institution);
       	    	if(!adminIsactive) {
       	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       	    	}
	   	     }else {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
	   	     }
      	     if("ADMIN".equals(role)||"TRAINER".equals(role)){
           Optional<Muser> opuser = muserrepositories.findByuserIdandInstitutionName(userId, institution);
         if(opuser.isPresent()) {
        	 Muser user=opuser.get();
         if(user.getProfile()!=null) {
            byte[] decompressedImage = ImageUtils.decompressImage(user.getProfile());
            user.setProfile(decompressedImage);
         }
            user.setCourses(null);
        	user.setAllotedCourses(null);
            return ResponseEntity.ok(user);
         }else {
        	 System.out.println("usernot");
        	 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
         }
      	   }else {

     	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
     	     }
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//```````````````WORKING````````````````````````````````````
public ResponseEntity<Page<MuserDto>> getTrainerByRoleName( String token ,int pageNumber,int pageSize) {
	
    try {
    	if (!jwtUtil.validateToken(token)) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }

	     String role = jwtUtil.getRoleFromToken(token);
	     String roleu="TRAINER";
   	     String email=jwtUtil.getUsernameFromToken(token);
   	     Optional<Muser>opreq=muserrepositories.findByEmail(email);
   	     String institution="";
   	     if(opreq.isPresent()) {
   	    	 Muser requser=opreq.get();
   	    	institution=requser.getInstitutionName();
   	    	boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", institution);
   	    	if(!adminIsactive) {
   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   	    	}
   	     }else {
   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
   	     }
	     if("ADMIN".equals(role)){
	    	 Pageable pageable = PageRequest.of(pageNumber, pageSize);
	    		 Page<MuserDto> users = muserPageRepo.findByRoleNameAndInstitutionName(roleu, institution,pageable);
       
       
        return ResponseEntity.ok(users);
        
	     }else {
	    	  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
public ResponseEntity< List<String>> SearchEmail(String token,String Query){
	try {
		if (!jwtUtil.validateToken(token)) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
	     }
		 String email=jwtUtil.getUsernameFromToken(token);
   	     Optional<Muser>opreq=muserrepositories.findByEmail(email);
   	     
   	     if(opreq.isPresent()) {
   	    	 Muser requser=opreq.get();
   	    	String institutionname=requser.getInstitutionName();
   	    	List<String> listu= muserrepositories.findEmailsByEmailContainingIgnoreCase(Query, institutionname);
   	    	return ResponseEntity.ok(listu);
   	     }else {
   	    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
   	     }
		
	}catch(Exception e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
	}
}

public ResponseEntity< List<String>> SearchEmailTrainer(String token,String query){
	try {
		if (!jwtUtil.validateToken(token)) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
	     }
		 String email=jwtUtil.getUsernameFromToken(token);
   	     Optional<Muser>opreq=muserrepositories.findByEmail(email);
   	     
   	     if(opreq.isPresent()) {
   	    	 Muser requser=opreq.get();
   	    	String institutionname=requser.getInstitutionName();
   	    	if(requser.getRole().getRoleName().equals("TRAINER")) {
   	    	List<String> listu= muserrepositories.findEmailsInAllotedCoursesByUserEmail(email, query);
   	    	return ResponseEntity.ok(listu);
   	    	}else {
   	    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
   	    	}
   	     }else {
   	    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
   	     }
		
	}catch(Exception e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
	}
}
//public List<String> MuserEmailSearch(String institution, String email) {
//    try {
//        return muserrepositories.findEmailsByEmailContainingIgnoreCase(email, institution);
//    } catch (Exception e) {
//        e.printStackTrace(); // Log the exception
//        return new ArrayList<>();  // Return an empty list or handle as needed
//    }
//}
}