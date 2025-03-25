package com.knowledgeVista.User.Controller;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.DownloadManagement.CustomerLeads;
import com.knowledgeVista.DownloadManagement.Customer_downloads;
import com.knowledgeVista.Email.EmailService;
import com.knowledgeVista.License.LicenseController;
import com.knowledgeVista.License.Madmin_Licence;
import com.knowledgeVista.License.mAdminLicenceRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserDto;
import com.knowledgeVista.User.MuserProfileDTO;
import com.knowledgeVista.User.MuserRequiredDto;
import com.knowledgeVista.User.MuserRoles;
import com.knowledgeVista.User.Approvals.MuserApprovalRepo;
import com.knowledgeVista.User.Approvals.MuserApprovals;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.Repository.MuserRoleRepository;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class MserRegistrationController {
	@Autowired
	private MuserRepositories muserrepositories;
	 @Autowired
	 private JwtUtil jwtUtil;
	@Autowired
	private MuserRoleRepository muserrolerepository;
	
	@Autowired
	private LicenseController licencecontrol;
@Autowired
private MuserApprovalRepo MuserApproval;

@Autowired
private EmailService emailservice;
	@Autowired
	private mAdminLicenceRepo madminrepo;
	
	  @Value("${spring.environment}")
	    private String environment;
	  
	 @Value("${spring.profiles.active}")
	    private String activeProfile;
	 @Value("${base.url}")
	    private String baseUrl;
	 
	 private static final Logger logger = LoggerFactory.getLogger(MserRegistrationController.class);

	
	 public Long countadmin() {
		 return muserrepositories.countByRoleName("ADMIN");
	 }

	public ResponseEntity<?> registerAdmin( String username, String psw, String email, String institutionName, LocalDate dob,String role,
	                                         String phone, String skills, MultipartFile profile, Boolean isActive,String countryCode) {
	    try {
	    	Long count=muserrepositories.count();
	    	if(environment.equals("VPS") &&  count>1) {
	    		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ADMIN");
	    	}
	        Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	       
	        Optional<Muser>existingInstitute =muserrepositories.findByInstitutionName(institutionName);
	       
	            if (existingUser.isPresent()) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EMAIL");
	            } 
	            if(existingInstitute.isPresent()) {
	            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INSTITUTE");
	            }
	            
	            Optional<MuserRoles> oproleUser = muserrolerepository.findByRoleName("ADMIN");
	            if(oproleUser.isPresent()) {
	            	MuserRoles roleuser=oproleUser.get();
	            	if (username == null || username.trim().isEmpty()) {
	            	    // Extract the part before '@' from the email
	            	    if (email != null && email.contains("@")) {
	            	        username = email.substring(0, email.indexOf("@"));
	            	    }
	            	}
	            Muser user = new Muser();
	            user.setUsername(username);
	            user.setEmail(email);
	            user.setIsActive(isActive);
	            user.setPsw(psw);
	            user.setPhone(phone);
	            user.setDob(dob);
	            user.setSkills(skills);
	            user.setInstitutionName(institutionName);
	            user.setCountryCode(countryCode);	   
	            user.setRole(roleuser);     
	           
	            if (profile != null && !profile.isEmpty()) {                
	            try {
	                user.setProfile(profile.getBytes());
	                
	            } catch (IOException e) {
	                e.printStackTrace();
	                logger.error("", e);
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error compressing image\"}");
	            }
	            }
	          Muser savedadmin=  muserrepositories.save(user);
	          if(environment.equals("SAS")) {
	     		  Madmin_Licence madmin= new Madmin_Licence();
		            madmin.setAdminId(savedadmin.getUserId());
		            madmin.setInstitution(institutionName);
		            madmin.setLicenceType("FREE");
		            madminrepo.save(madmin);
	     		   licencecontrol.uploadSAS(madmin, savedadmin);
	     	   }
	          List<String> bcc = null;
	          List<String> cc = null;
	          String institutionname = institutionName;

	          String body = String.format(
	        		    "<html>"
	        		        + "<body>"
	        		        + "<h2>Welcome to LearnHub Admin Portal!</h2>"
	        		        + "<p>Dear %s,</p>"
	        		        + "<p>We are excited to welcome you as an administrator at LearnHub.</p>"
	        		        + "<p>Here are your login credentials:</p>"
	        		        + "<ul>"
	        		        + "<li><strong>Username (Email):</strong> %s</li>"
	        		        + "<li><strong>Password:</strong> %s</li>"
	        		        + "</ul>"
	        		        + "<p>With your admin access, you can:</p>"
	        		        + "<ul>"
	        		        + "<li>Add and manage courses.</li>"
	        		        + "<li>Add and manage Student and Trainers.</li>"
	        		        + "<li>Approve the Registered Trainer.</li>"
	        		        + "<li>Allot Courses For Trainers .</li>"
	        		        + "<li>Oversee Revenue  Details.</li>"
	        		        + "<li>Oversee student enrollments.</li>"
	        		        + "<li>Update course content.</li>"
	        		        + "<li>Support students in their learning journey.</li>"
	        		        + "<li>And Many More.....</li>"
	        		        + "</ul>"
	        		        + "<p>If you need any assistance, our support team is always here to help.</p>"
	        		        + "<p>We appreciate your dedication and look forward to a great collaboration!</p>"
	        		        + "<p>Best Regards,<br>LearnHub Team</p>"
	        		        + "</body>"
	        		        + "</html>",
	        		    user.getUsername(), // Admin Name
	        		    user.getEmail(), // Admin Username (email)
	        		    psw // Admin Password
	        		);


	          if (institutionname != null && !institutionname.isEmpty()) {
	              try {
	            	  List<String> emailList = new ArrayList<>();
	            	  emailList.add(email);
	                  emailservice.sendHtmlEmailAsync(
	                      institutionname, 
	                      emailList,
	                      cc, 
	                      bcc, 
	                      "Welcome to LearnHub - Start Your Administrator Journey Today!", 
	                      body
	                  );
	              } catch (Exception e) {
	                  logger.error("Error sending mail: " + e.getMessage());
	              }
	          }


	          
	          RestTemplate restTemplate = new RestTemplate();

	          String apiUrl = baseUrl + "/Developer/CustomerDownloads";
	          String apiUrl2 = baseUrl + "/Developer/CustomerLeads";

	          Customer_downloads custDown = new Customer_downloads();
	          custDown.setName(user.getUsername());
	          custDown.setEmail(user.getEmail());
	          custDown.setCountryCode(user.getCountryCode());
	          custDown.setPhone(user.getPhone());

	          CustomerLeads custlead = new CustomerLeads();
	          custlead.setName(user.getUsername());
	          custlead.setEmail(user.getEmail());
	          custlead.setCountryCode(user.getCountryCode());
	          custlead.setPhone(user.getPhone());

	          try {
	              restTemplate.postForEntity(apiUrl, custDown, String.class);
	          } catch (Exception e) {
	              // Log the error but do not send it to the frontend
	              System.err.println("Error posting to CustomerDownloads: " + e.getMessage());
	          }

	          try {
	              restTemplate.postForEntity(apiUrl2, custlead, String.class);
	          } catch (Exception e) {
	              // Log the error but do not send it to the frontend
	              System.err.println("Error posting to CustomerLeads: " + e.getMessage());
	          }
	        return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	            }else {
	            	
	            	   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error getting role\"}");
	   	            
	            }
	    } catch (Exception e) {
	        e.printStackTrace();
	        logger.error("", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Internal Server Error\"}");
	    }
	}
public ResponseEntity<?>RegisterStudent(String username, String psw, String email,  LocalDate dob,String role,
	                                         String phone, String skills, MultipartFile profile, Boolean isActive,String countryCode){
	try {
		if(!environment.equals("VPS")) {
 		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot Register as Student in Sas Environment");
 	}
		Long admincount=muserrepositories.countByRoleName("ADMIN");
		if(admincount==0) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Institution Found");
		}
		if(admincount>1) {
			 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot Register as Student in Sas Environment");
		}
		 Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	       
	       String existingInstitute =muserrepositories.getInstitution("ADMIN");
	       if(existingInstitute.isEmpty()) {
           	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("institution not Found");
           }
	       
	            if (existingUser.isPresent()) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EMAIL");
	            } 
	           
	            
	            Optional<MuserRoles> oproleUser = muserrolerepository.findByRoleName("USER");
	            if(oproleUser.isPresent()) {
	            	MuserRoles roleuser=oproleUser.get();
	            	if (username == null || username.trim().isEmpty()) {
	            	    // Extract the part before '@' from the email
	            	    if (email != null && email.contains("@")) {
	            	        username = email.substring(0, email.indexOf("@"));
	            	    }
	            	}  
	            Muser user = new Muser();
	            user.setUsername(username);
	            user.setEmail(email);
	           user.setPsw(psw);
	            user.setPhone(phone);
	            user.setDob(dob);
	            user.setSkills(skills);
	            user.setInstitutionName(existingInstitute);
	            user.setCountryCode(countryCode);	   
	            user.setRole(roleuser);     
	           
	            if (profile != null && !profile.isEmpty()) {                
	            try {
	                user.setProfile(profile.getBytes());
	                
	            } catch (IOException e) {
	                e.printStackTrace();
	                logger.error("", e);
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error compressing image\"}");
	            }
	            }
	            muserrepositories.save(user);
	            List<String> bcc = null;
	            List<String> cc = null;
	            String institutionname = existingInstitute;

	            String body = String.format(
	                "<html>"
	                    + "<body>"
	                    + "<h2>Welcome to LearnHub!</h2>"
	                    + "<p>Dear %s,</p>"
	                    + "<p>We are excited to have you on board at %s, your gateway to knowledge and growth.</p>"
	                    + "<p>Here are your login credentials to access your courses:</p>"
	                    + "<ul>"
	                    + "<li><strong>Username (Email):</strong> %s</li>"
	                    + "<li><strong>Password:</strong> %s</li>"
	                    + "</ul>"
	                    + "<p>Start exploring your enrolled courses, engage with trainers, and enhance your learning experience.</p>"
	                    + "<p>If you need any support, feel free to reach out to our help desk.</p>"
	                    + "<p>Happy Learning!</p>"
	                    + "<p>Best Regards,<br>%s Team</p>"
	                    + "</body>"
	                    + "</html>",
	                username, // Student Name
	                existingInstitute,
	                email, // Student Username (email)
	                psw, // Student Password
	                existingInstitute
	            );

	            if (institutionname != null && !institutionname.isEmpty()) {
	                try {
	                    List<String> emailList = new ArrayList<>();
	                    emailList.add(email);
	                    emailservice.sendHtmlEmailAsync(
	                        institutionname, 
	                        emailList,
	                        cc, 
	                        bcc, 
	                        "Welcome to LearnHub - Start Your Learning Journey!", 
	                        body
	                    );
	                } catch (Exception e) {
	                    logger.error("Error sending mail: " + e.getMessage());
	                }
	            }

	          return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	            }else {
	            	   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error getting role\"}");
	   	            
	            }
		
	 } catch (Exception e) {
	        e.printStackTrace();
	        logger.error("", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Internal Server Error\"}");
	    }
}

public ResponseEntity<?>RegisterTrainer(String username, String psw, String email,  LocalDate dob,String role,
        String phone, String skills, MultipartFile profile, Boolean isActive,String countryCode){
try {
if(!environment.equals("VPS")) {
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot Register as Trainer in Sas Environment");
}
Long admincount=muserrepositories.countByRoleName("ADMIN");
if(admincount==0) {
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Institution Found");
}
if(admincount>1) {
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot Register as Trainer in Sas Environment");
}
Optional<Muser> existingUser = muserrepositories.findByEmail(email);

String existingInstitute =muserrepositories.getInstitution("ADMIN");
if(existingInstitute.isEmpty()) {
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("institution not Found");
}

if (existingUser.isPresent()) {
return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EMAIL");
} 


Optional<MuserRoles> oproleUser = muserrolerepository.findByRoleName("TRAINER");
if(oproleUser.isPresent()) {
MuserRoles roleuser=oproleUser.get();
if (username == null || username.trim().isEmpty()) {
    // Extract the part before '@' from the email
    if (email != null && email.contains("@")) {
        username = email.substring(0, email.indexOf("@"));
    }
}
MuserApprovals user = new MuserApprovals();
user.setUsername(username);
user.setEmail(email);
user.setIsActive(isActive);
user.setPsw(psw);
user.setPhone(phone);
user.setDob(dob);
user.setSkills(skills);
user.setInstitutionName(existingInstitute);
user.setCountryCode(countryCode);	   
user.setRole(roleuser);     

if (profile != null && !profile.isEmpty()) {                
try {
user.setProfile(profile.getBytes());

} catch (IOException e) {
e.printStackTrace();
logger.error("", e);
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error compressing image\"}");
}
}
MuserApproval.save(user);
return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
}else {
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error getting role\"}");

}

} catch (Exception e) {
e.printStackTrace();
logger.error("", e);
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Internal Server Error\"}");
}
}



	

	public ResponseEntity<?> getUserByEmail( String email, String token) {
	    try {
	    	if (!jwtUtil.validateToken(token)) {
	   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }
	    	 String emailofreq=jwtUtil.getUsernameFromToken(token);
	         String institution="";
		     Optional<Muser> opuser =muserrepositories.findByEmail(emailofreq);
		     if(opuser.isPresent()) {
		    	 Muser user=opuser.get();
		    	 institution=user.getInstitutionName();
		    	 boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", institution);
	       	    	if(!adminIsactive) {
	       	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	       	    	}
		     }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }
	        Optional<MuserRequiredDto> userOptional = muserrepositories.findDetailandProfileByEmailAndInstitution(email, institution);
	        
	        // If the user is found, process the user data
	        if (userOptional.isPresent()) {
	        	MuserRequiredDto user = userOptional.get();
	          
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
	        logger.error("", e);
	        // Return a 500 internal server error response
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(null);
	    }
	}

	    public ResponseEntity<?> getTrainerDetailsByEmail( String email, String token) {

	        try {
	            // Validate the token
	            if (!jwtUtil.validateToken(token)) {
	                // If the token is not valid, return unauthorized status
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
		    	 String emailofreq=jwtUtil.getUsernameFromToken(token);
	            String role = jwtUtil.getRoleFromToken(token);
	            String institution="";
			     Optional<Muser> opuser =muserrepositories.findByEmail(emailofreq);
			     if(opuser.isPresent()) {
			    	 Muser user=opuser.get();
			    	 institution=user.getInstitutionName();
			    	 boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", institution);
		       	    	if(!adminIsactive) {
		       	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		       	    	}
			     }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
	            // Perform authentication based on role
	            if ("ADMIN".equals(role)) {
	            	 Optional<MuserProfileDTO> userOptional = muserrepositories.findProfileAndCountryCodeAndRoleByEmailAndInstitutionName(email, institution);
		                if (userOptional.isPresent()) {
		                	MuserProfileDTO user = userOptional.get();
		                    return ResponseEntity.ok()
		                            .contentType(MediaType.APPLICATION_JSON)
		                            .body(user);
		                    }else {
			   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Trainer not found\"}");
		                }
	            } else {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with logging framework like Log4j
	            logger.error("", e);
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	    
	    
	    public ResponseEntity<?> getStudentDetailsByEmail( String email, String token) {

	        try {
	            // Validate the token
	            if (!jwtUtil.validateToken(token)) {
	                // If the token is not valid, return unauthorized status
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
 
	            String role = jwtUtil.getRoleFromToken(token);
	            String emailofreq=jwtUtil.getUsernameFromToken(token);
	          
	            String institution="";
			     Optional<Muser> opuser =muserrepositories.findByEmail(emailofreq);
			     if(opuser.isPresent()) {
			    	 Muser user=opuser.get();
			    	 institution=user.getInstitutionName();
			    	 boolean adminIsactive=muserrepositories.getactiveResultByInstitutionName("ADMIN", institution);
		       	    	if(!adminIsactive) {
		       	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		       	    	}
			     }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
	            // Perform authentication based on role
	            if ("ADMIN".equals(role)||"TRAINER".equals(role)) {
	                Optional<MuserProfileDTO> userOptional = muserrepositories.findProfileAndCountryCodeAndRoleByEmailAndInstitutionName(email, institution);
	                if (userOptional.isPresent()) {
	                	MuserProfileDTO user = userOptional.get();
	                    return ResponseEntity.ok()
	                            .contentType(MediaType.APPLICATION_JSON)
	                            .body(user);
	                    }else {
		   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Student not found\"}");
	                }
	            } else {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	        } catch (Exception e) {
	            e.printStackTrace(); 
	            logger.error("", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }


    public ResponseEntity<?> getDetailsbyemail(String email, String token){
    	 try {
	            // Validate the token
	            if (!jwtUtil.validateToken(token)) {
	                // If the token is not valid, return unauthorized status
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }

	            String emailofreq=jwtUtil.getUsernameFromToken(token);
	          
	            String institution="";
			     Optional<Muser> opuser =muserrepositories.findByEmail(emailofreq);
			     if(opuser.isPresent()) {
			    	 Muser user=opuser.get();
			    	 if("SYSADMIN".equals(user.getRole().getRoleName())){
			    		 Optional<MuserDto> opadmin=  muserrepositories.findDetailsByEmailforSysadmin(email);
			    		 if (opadmin.isPresent()) {
			                	MuserDto admin = opadmin.get();
			                    return ResponseEntity.ok()
			                            .contentType(MediaType.APPLICATION_JSON)
			                            .body(admin);
			                    }else {
				   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
			                }
			    	 }
			    	 institution=user.getInstitutionName();
			    	 Optional<MuserDto> opdto=muserrepositories.findDetailsByEmailAndInstitution(email, institution);
			    	 if (opdto.isPresent()) {
		                	MuserDto usertosend = opdto.get();
		                    return ResponseEntity.ok()
		                            .contentType(MediaType.APPLICATION_JSON)
		                            .body(usertosend);
		                    }else {
			   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"User not found\"}");
		                }

    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
} catch (Exception e) {
    e.printStackTrace(); 
    logger.error("", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
}
	 
    }  

    public ResponseEntity<?> getAdminDetailsBYEmail( String email, String token) {

        try {
            // Validate the token
            if (!jwtUtil.validateToken(token)) {
                // If the token is not valid, return unauthorized status
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String role = jwtUtil.getRoleFromToken(token);
		    
            // Perform authentication based on role
            if ("SYSADMIN".equals(role)) {
            	Optional<Muser>opuser=muserrepositories.findByEmail(email);
            	if(opuser.isPresent()) {
            		Muser user=opuser.get();
            		
            	 Optional<MuserProfileDTO> userOptional = muserrepositories.findProfileAndCountryCodeAndRoleByEmail(email);
	                if (userOptional.isPresent()) {
	                	
	                	MuserProfileDTO userdto = userOptional.get();
	                	userdto.setLastactive(muserrepositories.findLatestLastActiveByInstitution(user.getInstitutionName()));
	                    return ResponseEntity.ok()
	                            .contentType(MediaType.APPLICATION_JSON)
	                            .body(userdto);
	                    }else {
		   	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Admin not found\"}");
	                }
            	}else {
            		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Admin not found\"}");
            	}
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            // Log any other exceptions for debugging purposes
            e.printStackTrace(); // You can replace this with logging framework like Log4j
            logger.error("", e);
            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }  
}
	
