package com.knowledgeVista.Payments;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Settings.Feedback;
import com.knowledgeVista.Settings.FeedbackRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class PaymentSettingsController {
	
	 @Autowired
	 private JwtUtil jwtUtil;
	@Autowired
	private PaymentsettingRepository paymentsetting;
	
	@Autowired
	private FeedbackRepository feedback;
	
	@Autowired
	private MuserRepositories muserRepository;
	
	public ResponseEntity<?> SavePaymentDetails( Paymentsettings data, String token) {
		
		if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }  
		String role = jwtUtil.getRoleFromToken(token);
		String email=jwtUtil.getUsernameFromToken(token);
  	     Optional<Muser>opreq=muserRepository.findByEmail(email);
  	     String institution="";
  	     if(opreq.isPresent()) {
  	    	 Muser requser=opreq.get();
  	    	institution=requser.getInstitutionName();
  	    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
	   	    	if(!adminIsactive) {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	    	}
  	     }else {
  	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
  	     }
        if ("ADMIN".equals(role)) {
        	Optional<Paymentsettings> oppaysetting=paymentsetting.findByinstitutionName(institution);
        if(oppaysetting.isPresent()) {

         	 return new ResponseEntity<>("payment Data already exists", HttpStatus.BAD_REQUEST);
        }else {
        	data.setInstitutionName(institution);
        	paymentsetting.save(data);
        	return ResponseEntity.ok("saved sucessfully");
        	
        }
        }else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
	}
	

	public ResponseEntity<?> GetPaymentDetails ( String token){
		try {
			 if (!jwtUtil.validateToken(token)) {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }  String role = jwtUtil.getRoleFromToken(token);
	          String email=jwtUtil.getUsernameFromToken(token);
	   	     Optional<Muser>opreq=muserRepository.findByEmail(email);
	   	     String institution="";
	   	     if(opreq.isPresent()) {
	   	    	 Muser requser=opreq.get();
	   	    	institution=requser.getInstitutionName();
	   	     boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
	   	    	if(!adminIsactive) {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	    	}
	   	     }else {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
	   	     }
	          // Perform authentication based on role
	          if ("ADMIN".equals(role)) {
			Optional<Paymentsettings> opdatalist=paymentsetting.findByinstitutionName(institution);
			
		        // If there's only one certificate, return it directly
		        if (opdatalist.isPresent()) {
		            Paymentsettings pay = opdatalist.get();
		            return ResponseEntity.ok()
		                .body(pay);  
		        }else {
		        	return ResponseEntity.status(HttpStatus.NOT_FOUND)
				            .body("No payment data found");
		        }
		        
		        
			}else {
		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		        }
		    } catch (Exception e) {
		    	e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body("An error occurred while retrieving payment data");
		    }
	}
	
	public ResponseEntity<?> editpayment( Long payid, String razorpay_key, String razorpay_secret_key, String token){
		 
	 
		 if (!jwtUtil.validateToken(token)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         } 
		 
		 String role = jwtUtil.getRoleFromToken(token);
		 String email=jwtUtil.getUsernameFromToken(token);
   	     Optional<Muser>opreq=muserRepository.findByEmail(email);
   	     String institution="";
   	     if(opreq.isPresent()) {
   	    	 Muser requser=opreq.get();
   	    	institution=requser.getInstitutionName();
   	     boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
	    	if(!adminIsactive) {
	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    	}
   	     }else {
   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
   	     }
         // Perform authentication based on role
         if ("ADMIN".equals(role)) {
		 Optional<Paymentsettings> oldsettings=paymentsetting.findByinstitutionName(institution);
		 if (oldsettings.isPresent()) {
			 Paymentsettings  updatedsettings =oldsettings.get();
			 if(razorpay_key!=null) {
				 updatedsettings.setRazorpay_key(razorpay_key);
			 }
			 if(razorpay_secret_key!= null) {
				 updatedsettings.setRazorpay_secret_key(razorpay_secret_key);
			 }
			 paymentsetting.saveAndFlush(updatedsettings);
	            return ResponseEntity.ok("settings updated successfully");
		 }else {
	            return ResponseEntity.notFound().build();
	        }
         }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        } 
		 
		 
	 }

	
	
	
	public Feedback feedback( Feedback data) {
		System.out.println(data);
		return feedback.save(data);
	}

}
