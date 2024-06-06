package com.knowledgeVista.Course.certificate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Test.MuserTestActivity;
import com.knowledgeVista.Course.Test.Repository.MusertestactivityRepo;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
@RestController
public class certificateController {
	@Autowired
	private certificateRepo certificaterepo;
	 @Autowired
	 private JwtUtil jwtUtil;
		@Autowired
		private MuserRepositories muserRepository;
		
		@Autowired
		private MusertestactivityRepo activityrepo;
	
	
	public ResponseEntity<?> addcertificate(  String institutionName, String ownerName, String qualification, String address,
	         MultipartFile authorizedSign, String token) {
		// @RequestParam("certificateTemplate") MultipartFile certificateTemplate
	    try {
	    	
	    	 if (!jwtUtil.validateToken(token)) {return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("{\"message\": \"Unauthorized access\"}");
	            
	         }

	         String role = jwtUtil.getRoleFromToken(token);
	         if("ADMIN".equals(role)) {
	         if(certificaterepo.count()>0) {
	        	 return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Certificate already exists\"}");
	        
	      
	         }else {
	        	  // Compress the authorized sign image
	 	        byte[] compressedAuthorizedSign = ImageUtils.compressImage(authorizedSign.getBytes());
	 	        // Create a new Certificate object with the extracted data
	 	        certificate certificate = new certificate();
	 	        certificate.setInstitutionName(institutionName);
	 	        certificate.setOwnerName(ownerName);
	 	        certificate.setQualification(qualification);
	 	        certificate.setAddress(address);
	 	        certificate.setAuthorizedSign(compressedAuthorizedSign);
	 	        //certificate.setCertificateTemplate(compressedCertificateTemplate);

	 	        // Save the certificate object to the database
	 	        certificaterepo.save(certificate);
	            return ResponseEntity.ok("{\"message\": \"Certificate updated successfully.\"}");
	 	        }
	         }else {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		                    .body("{\"message\": \"Unauthorized access\"}");
	         }
	        
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("{\"message\": \"An error occurred while updating the certificate: " + e.getMessage() + "\"}");
	    }
	}
	
//================================Edit Mapping=================================

	public ResponseEntity<String> editcertificate( String institutionName, String ownerName, String qualification, String address,
			MultipartFile authorizedSign,Long certificateId, String token
	) {
	    try {
	        // Validate the token
	        if (!jwtUtil.validateToken(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("{\"message\": \"Invalid token\"}");
	        }

	        // Get the role from the token
	        String role = jwtUtil.getRoleFromToken(token);

	        // Check if the user is an admin
	        if (!"Admin".equalsIgnoreCase(role)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("{\"message\": \"Unauthorized access\"}");
	        }

	        // Find the certificate by ID
	        Optional<certificate> optionalCertificate = certificaterepo.findById(certificateId);
	        System.out.println("found");

	        if (optionalCertificate.isPresent()) {
	            certificate certificate = optionalCertificate.get();
                if(authorizedSign!=null) {
	            // Compress the authorized sign image
	            byte[] compressedAuthorizedSign = ImageUtils.compressImage(authorizedSign.getBytes());
	            System.out.println("found image");
	            certificate.setAuthorizedSign(compressedAuthorizedSign);
                }
	            // Update the certificate details
                
	            certificate.setInstitutionName(institutionName);
	            certificate.setOwnerName(ownerName);
	            certificate.setQualification(qualification);
	            certificate.setAddress(address);

	            // Save the updated certificate to the database
	            certificaterepo.saveAndFlush(certificate);

	            // Return a JSON response with a success message
	            return ResponseEntity.ok("{\"message\": \"Certificate updated successfully.\"}");
	        } else {
	            // Certificate not found
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("{\"message\": \"Certificate not found.\"}");
	        }
	    } catch (Exception e) {
	        // Handle internal server error and return a JSON response
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("{\"message\": \"An error occurred while updating the certificate: " + e.getMessage() + "\"}");
	    }
	}
	
	
	
//````````````````````````````WORING````````````````````````````````````````````````````````)
	public ResponseEntity<?> viewCoursecertificate() {
	    List<certificate> certificates = certificaterepo.findAll();
	    
	    if (certificates.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body("No certificates found");
	    } else {
	        // If there's only one certificate, return it directly
	        if (certificates.size() == 1) {
	            certificate certi = certificates.get(0);
	            byte[] sign = ImageUtils.decompressImage(certi.getAuthorizedSign());
	            certi.setAuthorizedSign(sign);
	            return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(certi);
	        }
	        else {
	            // If there are multiple certificates, return all of them
	            for (certificate certi : certificates) {
	                byte[] sign = ImageUtils.decompressImage(certi.getAuthorizedSign());
	                certi.setAuthorizedSign(sign);
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	        	            .body("No certificates found");
	            }
//	            return ResponseEntity.ok()
//	                .contentType(MediaType.APPLICATION_JSON)
//	                .body(certificates);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
        	            .body("No certificates found");
	        }
	    }
	}
	
	
//-------------------Table view of certificates in user---------------------------------

	public ResponseEntity<?> sendAllCertificate( String token) {
	    String username = jwtUtil.getUsernameFromToken(token);
	    Optional<Muser> opuser = muserRepository.findByEmail(username);
	    if (opuser.isPresent()) {
	        Muser user = opuser.get();
	        List<MuserTestActivity> activityDetails = activityrepo.findByuser(user);

	        // Group activityDetails by courseId
	        Map<Long, List<MuserTestActivity>> activityByCourseId = activityDetails.stream()
	                .collect(Collectors.groupingBy(activity -> activity.getCourse().getCourseId()));

	        List<HashMap<String, Object>> allActivityHashMaps = new ArrayList<>();
	        for (List<MuserTestActivity> activityList : activityByCourseId.values()) {
	            for (MuserTestActivity act : activityList) {
	                // Check if the activity percentage is greater than or equal to pass percentage
	                if (act.getPercentage() >= act.getTest().getPassPercentage()) {
	                    HashMap<String, Object> hashMap = new HashMap<>();
	                    hashMap.put("activityId", act.getActivityId());
	                    hashMap.put("user", act.getUser().getUsername());
	                    hashMap.put("course", act.getCourse().getCourseName());
	                    hashMap.put("testDate", act.getTestDate());
	                    hashMap.put("percentage", act.getPercentage());
	                    allActivityHashMaps.add(hashMap); // Add each HashMap to the list
	                    break; // No need to check further activities for this course
	                }
	            }
	        }

	        // Return the list of HashMaps as the response body
	        return ResponseEntity.ok().body(allActivityHashMaps);
	    }
	    return ResponseEntity.ok().body("something fuzzy happen");
	}

public ResponseEntity<?> getByActivityId( Long activityId, String token) {
	
    if (jwtUtil.validateToken(token)) {
        Optional<MuserTestActivity> opActivity = activityrepo.findById(activityId);
        if (opActivity.isPresent()) {
            MuserTestActivity activity = opActivity.get();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user", activity.getUser().getUsername());
            hashMap.put("course", activity.getCourse().getCourseName());
            hashMap.put("testDate", activity.getTestDate());
            hashMap.put("percentage", activity.getPercentage());
            
            return ResponseEntity.ok().body(hashMap);
        } else {
            // Activity not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity not found");
        }
    } else {
        // Invalid token
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
}

}
	
