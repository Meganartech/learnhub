package com.knowledgeVista.User.Controller;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserRoles;
import com.knowledgeVista.User.Repository.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.knowledgeVista.User.Repository.MuserRepositories;
import jakarta.transaction.Transactional;
@RestController
@RequestMapping("/student")
@CrossOrigin
public class MserRegistrationController {
	@Autowired
	private MuserRepositories muserrepositories;

	@Autowired
	private MuserRoleRepository muserrolerepository;
//	@Autowired
//	private MuserDetailRepository muserdetailrepository;
//	@Autowired
//	private OccupationDetailsRepository occupationdetailrepository;
//	@Autowired
//	private OrgDetailRepository orgdetailrepository;
//	@Autowired
//	private SkillDetailRepository skilldetailrepository;
	
//	@PostMapping("/register")
//	public ResponseEntity<String> registerStudent(@RequestBody RegisterRequestDTO registrationrequestdto) {
//		
//		
//		   Muser user = registrationrequestdto.getMuser();
//		 String   email=user.getEmail();
//		    
//		 long userCount = muserrepositories.count();
//		    // Check if email exists
//		    Muser existingUser = muserrepositories.findByEmail(email);
//		muserrepositories.findByEmail(email);
//		
//		 if ( userCount > 0) {
//			if(existingUser!=null) {
//			   
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"EMAIL ALREADY EXISTS\"}");
//
//			}else {
//			MuserRoles roleUser=muserrolerepository.findByroleName("USER");
//			 user.setRole(roleUser);
//			 }
//		}else{
//			MuserRoles roleuser= new MuserRoles();
//			roleuser.setRoleName("USER");
//			roleuser.setIsActive(true);
//			 muserrolerepository.save(roleuser); 
//			 
//			 MuserRoles role= new MuserRoles();
//			 role.setRoleName("ADMIN");
//			 role.setIsActive(true);
//			 MuserRoles savedroleadmin = muserrolerepository.save(role);
//			 user.setRole(savedroleadmin);
//		}
//		 
////	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
////			String encodedPassword = passwordEncoder.encode(user.getPsw());
////			user.setPsw(encodedPassword);
//		    
//		
//		  		    // Save Muser
//		    muserrepositories.save(user);
//		    
//	  
//		   MuserDetails muserdetail=  registrationrequestdto.getMuserDetails();
//		   muserdetail.setUserId(user);
//		   OccupationDetails occ= registrationrequestdto.getOccupationDetails();
//		    occupationdetailrepository.save(occ);
//		    muserdetail.setOccupationId(occ);
//////		   OrgDetails org=orgdetailrepository.save(registrationrequestdto.getOrgDetails());
//////		   muserdetail.setOrgId(org);
//	       SkillDetails skill=skilldetailrepository.save(registrationrequestdto.getSkillDetails());
//	       muserdetail.setSkillId(skill);
//	       muserdetailrepository.save(registrationrequestdto.getMuserDetails());
//	
//	
//	
//	
//	 return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
//	
//	}
	
	
//	@PostMapping("/trainerRegister")
//	public ResponseEntity<?>addtrainer()

	@PostMapping("/register")
	public ResponseEntity<?> registerStudent(@RequestParam("username") String username,
			                                 @RequestParam("psw") String psw,
			                                 @RequestParam("email") String email,
			                                 @RequestParam("dob") LocalDate dob,
			                                 @RequestParam("phone") String phone,
			                                 @RequestParam("profile")MultipartFile profile,
			                                 @RequestParam ("isActive") Boolean isActive) {
		long userCount = muserrepositories.count();
		Optional<Muser> existingUser = muserrepositories.findByEmail(email);
		 if ( userCount > 0) {
			 if(existingUser.isPresent()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"EMAIL ALREADY EXISTS\"}");
				}else {
				MuserRoles roleUser=muserrolerepository.findByroleName("USER");
				Muser user=new Muser();
		        user.setUsername(username);
				user.setEmail(email);
				user.setIsActive(isActive);
				user.setPsw(psw);
				user.setPhone(phone);
				user.setDob(dob);
				user.setRole(roleUser);
					try {
			       	 user.setProfile(ImageUtils.compressImage(profile.getBytes()));
			       } catch (IOException e) {
			           e.printStackTrace();
			       }
					 muserrepositories.save(user);
				}
		 }else{
					MuserRoles roleuser= new MuserRoles();
					 roleuser.setRoleName("USER");
					 roleuser.setIsActive(true);
					 roleuser.setUsers(null);
					 muserrolerepository.save(roleuser); 
					 
					 MuserRoles role= new MuserRoles();
					 role.setRoleName("ADMIN");
					 role.setIsActive(true);
					 role.setUsers(null);
					 MuserRoles savedroleadmin = muserrolerepository.save(role);
					 Muser user=new Muser();
				        user.setUsername(username);
						user.setEmail(email);
						user.setIsActive(isActive);
						user.setPsw(psw);
						user.setPhone(phone);
						user.setDob(dob);
						user.setRole(savedroleadmin);
							try {
					       	 user.setProfile(ImageUtils.compressImage(profile.getBytes()));
					       } catch (IOException e) {
					           e.printStackTrace();
					       }
							 muserrepositories.save(user);
				}
		 return ResponseEntity.ok().body("{\"message\": \"saved Successfully\"}");
	}
	
	
	

	    @GetMapping("/users/{email}")
	    public ResponseEntity<Muser> getUserByEmail(@PathVariable String email) {
	        Optional<Muser> userOptional = muserrepositories.findByEmail(email);
	        if (userOptional.isPresent()) {
	            Muser user = userOptional.get();
	          
	        
	            byte[] profileImage = ImageUtils.decompressImage(user.getProfile());
	            user.setProfile(profileImage);
	           user.setCourses(null);
	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .body(user);
	           
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
       
       
      

       
  @Transactional
	  @GetMapping("/viewall")
	  public  ResponseEntity<List<Muser>> viewUsers() {
	        List<Muser> users = muserrepositories.findAll();
	        // Decompress image data for each course
	        
	        for (Muser user : users) {
	        	
	        	 byte[] images =ImageUtils.decompressImage(user.getProfile());
	            user.setProfile(images);
	         
	        }
	        return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(users);
	    }
	  
 
	  @GetMapping("/usersbyid/{id}")
	  public ResponseEntity<Muser> getUserByid(@PathVariable Long id) {
	      Optional<Muser> userOptional = muserrepositories.findById(id);
	      if(userOptional.isPresent()) {
	          Muser user = userOptional.get();
	          byte[] profileImage = ImageUtils.decompressImage(user.getProfile());
	          user.setProfile(profileImage);
	          return ResponseEntity.ok()
	                  .contentType(MediaType.APPLICATION_JSON)
	                  .body(user);
	      } else {
	          // User not found, return an appropriate response
	          return ResponseEntity.notFound().build();
	      }
	  }
	  
//	@PostMapping("/addTrainer")  
	  
	  
	  
	  

	
}