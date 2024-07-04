package com.knowledgeVista.User.Controller;
import com.knowledgeVista.DownloadManagement.CustomerLeads;
import com.knowledgeVista.DownloadManagement.Customer_downloads;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.License.Madmin_Licence;
import com.knowledgeVista.License.mAdminLicenceRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserRoles;
import com.knowledgeVista.User.Repository.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.knowledgeVista.User.Repository.MuserRepositories;
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
	private Environment env;
	@Autowired
	private mAdminLicenceRepo madminrepo;
	

	public ResponseEntity<?> registerAdmin( String username, String psw, String email, String institutionName, LocalDate dob,String role,
	                                         String phone, String skills, MultipartFile profile, Boolean isActive,String countryCode) {
	    try {
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
	           
	                            
	            try {
	                user.setProfile(ImageUtils.compressImage(profile.getBytes()));
	            } catch (IOException e) {
	                e.printStackTrace();
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error compressing image\"}");
	            }
	          Muser savedadmin=  muserrepositories.save(user);
	            Madmin_Licence madmin= new Madmin_Licence();
	            madmin.setAdminId(savedadmin.getUserId());
	            madmin.setInstitution(institutionName);
	            madmin.setLicenceType("FREE");
	            madminrepo.save(madmin);
	            RestTemplate restTemplate = new RestTemplate();

	            String baseUrl = env.getRequiredProperty("base.url");
	            int port = Integer.parseInt(env.getRequiredProperty("server.port"));
	            String contextPath = env.getProperty("api.context-path", ""); 
	            
	            String apiUrl = baseUrl + port + contextPath+"/Developer/CustomerDownloads";
	            String apiUrl2 = baseUrl + port +contextPath+ "/Developer/CustomerLeads";

                
	            Customer_downloads custDown = new Customer_downloads();
	            custDown.setName(user.getUsername());
	            custDown.setEmail(user.getEmail());
	            custDown.setCountryCode(user.getCountryCode());
	            custDown.setPhone(user.getPhone());
	            
	            CustomerLeads custlead=new CustomerLeads();
	            custlead.setName(user.getUsername());
	            custlead.setEmail(user.getEmail());
	            custlead.setCountryCode(user.getCountryCode());
	            custlead.setPhone(user.getPhone());
	            
	            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, custDown, String.class);

	            ResponseEntity<String> response2 = restTemplate.postForEntity(apiUrl2, custlead, String.class);

	           
	        return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	            }else {
	            	   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error getting role\"}");
	   	            
	            }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Internal Server Error\"}");
	    }
	}

	

	public ResponseEntity<Muser> getUserByEmail( String email, String token) {
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
	        Optional<Muser> userOptional = muserrepositories.findByEmailandInstitutionName(email, institution);
	        
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
	                Optional<Muser> userOptional = muserrepositories.findByEmailandInstitutionName(email, institution);
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
	                Optional<Muser> userOptional = muserrepositories.findByEmailandInstitutionName(email, institution);
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


    
	 
	  

	  
}
	
