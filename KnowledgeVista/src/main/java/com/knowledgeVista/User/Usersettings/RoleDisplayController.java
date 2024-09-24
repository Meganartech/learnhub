package com.knowledgeVista.User.Usersettings;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
@CrossOrigin
public class RoleDisplayController {
	
	@Autowired
	private RoleDisplayRepo Roledisplayrepo;
	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
		private MuserRepositories muserRepository;
	
	
	public ResponseEntity<?>getdisplayNames(String token){
		try {
			if (!jwtUtil.validateToken(token)) {return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized access");
         }
         String email=jwtUtil.getUsernameFromToken(token); 
          String institution=muserRepository.findinstitutionByEmail(email);
          if(institution==null) {
        	  return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
          }
          Optional<Role_display_name> opdisplay=Roledisplayrepo.getdisplayname(institution);
          if(opdisplay.isEmpty()) {
        	  return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
          }
        	  Role_display_name display= opdisplay.get();
        	  return ResponseEntity.ok(display);
         
		} catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred  " + e.getMessage() );
	    }
		
	}


	 
	 @Transactional
	 public ResponseEntity<?> UpdateDisplayName(String token, Role_display_name displayName){
		 try {
				if (!jwtUtil.validateToken(token)) {return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("Unauthorized access");
	         }
				Roledisplayrepo.save(displayName);
				
				return ResponseEntity.ok("updated");
			
			
		} catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred  " + e.getMessage() );
	    }

}
	 public ResponseEntity<?> postDisplayname(String token, Role_display_name roledisplaynames){
		try {
			 
	         String email=jwtUtil.getUsernameFromToken(token); 
	          String institution=muserRepository.findinstitutionByEmail(email);
	          if(institution==null) {
	        	  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access"); 
	          }
	          roledisplaynames.setInsitution(institution);
	          
	          Roledisplayrepo.save(roledisplaynames);
	          return ResponseEntity.ok("saved"); 
		 } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An error occurred while updating the certificate: " + e.getMessage() );
		    }
	 }
}
