package com.knowledgeVista.Settings;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.certificate.certificate;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class SettingsController {
	
	 @Autowired
	 private JwtUtil jwtUtil;
	@Autowired
	private PaymentsettingRepository paymentsetting;
	
	@Autowired
	private FeedbackRepository feedback;
	
	@PostMapping("/Paymentsettings")
	public ResponseEntity<?> SavePaymentDetails(@RequestBody Paymentsettings data,
	          @RequestHeader("Authorization") String token) {
		if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }  String role = jwtUtil.getRoleFromToken(token);

        if ("ADMIN".equals(role)) {
        if(paymentsetting.count()>0) {
       	 return new ResponseEntity<>(" payment Data already exists", HttpStatus.BAD_REQUEST);
        }else {
        	paymentsetting.save(data);
        	return ResponseEntity.ok("saved sucessfully");
        }}else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
	}
	
	@GetMapping("/getPaymentDetails")
	public ResponseEntity<?> GetPaymentDetails (
	          @RequestHeader("Authorization") String token){
		try {
			 if (!jwtUtil.validateToken(token)) {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }  String role = jwtUtil.getRoleFromToken(token);

	          // Perform authentication based on role
	          if ("ADMIN".equals(role)) {
			List<Paymentsettings> datalist=paymentsetting.findAll();
			
			if (datalist.isEmpty()) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND)
		            .body("No payment data found");
		    } else {
		        // If there's only one certificate, return it directly
		        if (datalist.size() == 1) {
		            Paymentsettings pay = datalist.get(0);
		            return ResponseEntity.ok()
		                .body(pay);
		            
		        }
		        else {
		        	
		        	 Paymentsettings lastPayment = datalist.get(datalist.size() - 1);
		                return ResponseEntity.ok()
		                    .body(lastPayment);
		        	
		            }
		        }}else {
		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		        }
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		            .body("An error occurred while retrieving payment data");
		    }
		
		
	}
	 @PatchMapping("/update/{payid}")
	public ResponseEntity<?> editpayment(@PathVariable Long payid,
            @RequestParam(value="razorpay_key", required=false) String razorpay_key,
            @RequestParam(value="razorpay_secret_key", required=false) String razorpay_secret_key,
	          @RequestHeader("Authorization") String token){
		 
	 
		 if (!jwtUtil.validateToken(token)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         } 
		 
		 String role = jwtUtil.getRoleFromToken(token);

         // Perform authentication based on role
         if ("ADMIN".equals(role)) {
		 Optional<Paymentsettings> oldsettings=paymentsetting.findById(payid);
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

	
	
	@PostMapping("/feedback")
	public Feedback feedback(@RequestBody Feedback data) {
		System.out.println(data);
		return feedback.save(data);
	}

}
