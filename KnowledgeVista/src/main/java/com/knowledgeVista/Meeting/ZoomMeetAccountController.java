package com.knowledgeVista.Meeting;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;


@RestController
@CrossOrigin
public class ZoomMeetAccountController {
	@Autowired
	private ZoomAccountkeyrepo zoomacrepo;
	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
		private MuserRepositories muserRepository;
	
	@PostMapping("/zoom/save/Accountdetails")
	public ResponseEntity<?>SaveAccountDetails(@RequestBody ZoomAccountKeys accountdetails ,@RequestHeader("Authorization") String token){
		 try {
		    	
	    	 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    	 }
	         String email=jwtUtil.getUsernameFromToken(token); 
	         Optional<Muser> opuser=muserRepository.findByEmail(email);
	         if(opuser.isPresent()) {
	        	 Muser user=opuser.get();
	        	 String role=user.getRole().getRoleName();
	        	 String InstitutionName=user.getInstitutionName();
	        	 if("ADMIN".equals(role)) {
	        		 accountdetails.setInstitution_name(InstitutionName);
	        		 zoomacrepo.save(accountdetails);
	        		 return ResponseEntity.ok("Data Saved SuccessFully");
	        	 }else {
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
	         }else {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not Found");
	         }
	         
		  } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An error occurred while updating the certificate: " + e.getMessage() );
		    }
	}
	@PatchMapping("/zoom/Edit/Accountdetails")
	public ResponseEntity<?>EditAccountDetails(@RequestBody ZoomAccountKeys accountdetails ,@RequestHeader("Authorization") String token){
		 try {
		    	
	    	 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    	 }
	         String email=jwtUtil.getUsernameFromToken(token); 
	         Optional<Muser> opuser=muserRepository.findByEmail(email);
	         if(opuser.isPresent()) {
	        	 Muser user=opuser.get();
	        	 String role=user.getRole().getRoleName();
	        	 if("ADMIN".equals(role)) {
	        		 zoomacrepo.save(accountdetails);
	        		 return ResponseEntity.ok("Data Saved SuccessFully");
	        	 }else {
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
	         }else {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not Found");
	         }
	         
		  } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An error occurred while updating the certificate: " + e.getMessage() );
		    }
	}
    @GetMapping("/zoom/get/Accountdetails")
    public ResponseEntity<?> getMethodName(@RequestHeader("Authorization") String token) {
    	 try {
		    	
	    	 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    	 }
	         String email=jwtUtil.getUsernameFromToken(token); 
	         Optional<Muser> opuser=muserRepository.findByEmail(email);
	         if(opuser.isPresent()) {
	        	 Muser user=opuser.get();
	        	 String role=user.getRole().getRoleName();
	        	 String InstitutionName=user.getInstitutionName();
	        	 if("ADMIN".equals(role)) {
	        		 Optional<ZoomAccountKeys> opaccountsettings=zoomacrepo.findbyInstitutionName(InstitutionName);
	        		 if(opaccountsettings.isPresent()) {
	        			 ZoomAccountKeys accountset=opaccountsettings.get();
	        			 return ResponseEntity.ok(accountset);
	        		 }else {
	    	        	 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        	 }
	        	 }else {
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
	         }else {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not Found");
	         }
	         
		  } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An error occurred while updating the certificate: " + e.getMessage() );
		    }

	        	 }
    
  
   //SysAdmin------------------------------------------------------
    @PostMapping("/SysAdmin/zoom/save/Accountdetails")
	public ResponseEntity<?>SaveAccountDetailsSYS(@RequestBody ZoomAccountKeys accountdetails ,@RequestHeader("Authorization") String token){
		 try {
		    	
	    	 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    	 }
	         String role=jwtUtil.getRoleFromToken(token);
	       
	        	 if("SYSADMIN".equals(role)) {
	        		 accountdetails.setInstitution_name("SYSADMIN");
	        		 zoomacrepo.save(accountdetails);
	        		 return ResponseEntity.ok("Data Saved SuccessFully");
	        	 }else {
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
	         
	         
		  } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An error occurred while updating the certificate: " + e.getMessage() );
		    }
	}
    
    
    @PatchMapping("/SysAdmin/zoom/Edit/Accountdetails")
	public ResponseEntity<?>EditAccountDetailsSYS(@RequestBody ZoomAccountKeys accountdetails ,@RequestHeader("Authorization") String token){
		 try {
		    	
	    	 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    	 }
	         String role=jwtUtil.getRoleFromToken(token); 
	        
	        	 if("SYSADMIN".equals(role)) {
	        		 zoomacrepo.save(accountdetails);
	        		 return ResponseEntity.ok("Data Saved SuccessFully");
	        	 }else {
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
	        
	         
		  } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An error occurred while updating the certificate: " + e.getMessage() );
		    }
    
}
    
    @GetMapping("/SysAdmin/zoom/get/Accountdetails")
    public ResponseEntity<?> getMethodNameSYS(@RequestHeader("Authorization") String token) {
    	 try {
		    	
	    	 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    	 }
	         String role=jwtUtil.getRoleFromToken(token); 
	        
	        	 if("SYSADMIN".equals(role)) {
	        		 Optional<ZoomAccountKeys> opaccountsettings=zoomacrepo.findbyInstitutionName("SYSADMIN");
	        		 if(opaccountsettings.isPresent()) {
	        			 ZoomAccountKeys accountset=opaccountsettings.get();
	        			 return ResponseEntity.ok(accountset);
	        		 }else {
	    	        	 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        	 }
	        	 }else {
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
	        
	         
		  } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An error occurred while updating the certificate: " + e.getMessage() );
		    }

	        	 }
   
    
    
    
}
