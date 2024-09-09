package com.knowledgeVista.Notification.Controller;

import com.knowledgeVista.Notification.dtos.NotificationDetailsDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Notification.NotificationDetails;
import com.knowledgeVista.Notification.NotificationUser;
import com.knowledgeVista.Notification.Repositories.NotificationDetailsRepo;
import com.knowledgeVista.Notification.Repositories.NotificationUserRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@CrossOrigin
@RestController
public class NotificationController {
	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
	 private MuserRepositories muserRepository;
	 @Autowired
		private NotificationDetailsRepo notidetailRepo;
		@Autowired
		private NotificationUserRepo notiuserRepo;
		
//	public ResponseEntity<?>GetAllNotification(String token){
//		try {
//
//	         if (!jwtUtil.validateToken(token)) {
//	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//	         }
//	         String email=jwtUtil.getUsernameFromToken(token);
//	         Optional<Muser> opmuser= muserRepository.findByEmail(email);
//	         if(opmuser.isPresent()) {
//	        	 Muser user=opmuser.get();
//	        	 String institution=user.getInstitutionName();
//	        	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
//		   	    	if(!adminIsactive) {
//		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//		   	    	}
//	        	 LocalDate today=LocalDate.now();
//	        	 List<Long> ids=notiuserRepo.findNotificationIdsByUserId(user.getUserId(),today);
//
//              	 List<Object> notimap = new ArrayList<>();
//	        
//              	 for(Long id:ids) {
//	        		 
//	        		Optional<NotificationDetails> opnotidetails=notidetailRepo.findById(id);
//	        		 if (opnotidetails.isPresent()) {
//	        			 NotificationDetails notidetails=opnotidetails.get();
//	        			 if(notidetails.getNotimage() !=null) {
//	        			 try {
//	        				 byte[] images = ImageUtils.decompressImage(notidetails.getNotimage()); 
//	        				 notidetails.setNotimage(images);
//	        	            } catch (Exception e) {
//	        	                e.printStackTrace();
//	        	            }
//	        			 }
//	        			 notimap.add(notidetails);
//			            }
//	        		 
//	        		 
//	        	 }
//	        	 return ResponseEntity.ok(notimap);
//	         }else {
//	        	 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//	         }
//	         
//		}catch (Exception e) {
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
//        }
//	}
	
		    public ResponseEntity<?> GetNotiImage( String token, List<Long> notifyIds) {
		        try {
		            // Validate JWT token
		            if (!jwtUtil.validateToken(token)) {
		                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		            }
		           
		            
		            
		            // Extract email from token and get the user
		            String email = jwtUtil.getUsernameFromToken(token);
		            Optional<Muser> opmuser = muserRepository.findByEmail(email);

		            if (opmuser.isPresent()) {
		                opmuser.get();
		                if (notifyIds == null || notifyIds.isEmpty()) {
		                    return ResponseEntity.badRequest().body("Notification ID list cannot be empty.");
		                }
		                // Fetch images for the notification IDs in the list
		                List<NotificationDetails> notificationsWithImages = notidetailRepo.findNotificationsWithImagesByNotifyIdIn(notifyIds);

		                if (!notificationsWithImages.isEmpty()) {
//		                	 List<byte[]> images = notificationsWithImages.stream()
//                                     .map(NotificationDetails::getNotimage)
//                                     .collect(Collectors.toList());
		                	 System.out.println("images" );
		                	 for(NotificationDetails img :notificationsWithImages) {
		                		 System.out.println(img.toString());
		                	 }
             return ResponseEntity.ok(notificationsWithImages);
		                } else {
		                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No images found for the given IDs.");
		                }
		               
		            } else {
		                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		            }
		        } catch (Exception e) {
		        	  e.printStackTrace();
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching images.");
		        }
		    }
		 
		public ResponseEntity<?> GetAllNotification(String token) {
		    try {
		        if (!jwtUtil.validateToken(token)) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		        }

		        String email = jwtUtil.getUsernameFromToken(token);
		        Optional<Muser> opmuser = muserRepository.findByEmail(email);

		        if (opmuser.isPresent()) {
		            Muser user = opmuser.get();
		            String institution = user.getInstitutionName();
		            boolean adminIsactive = muserRepository.getactiveResultByInstitutionName("ADMIN", institution);

		            if (!adminIsactive) {
		                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		            }

		            LocalDate today = LocalDate.now();
		            List<Long> ids = notiuserRepo.findNotificationIdsByUserId(user.getUserId(), today);

		            List<NotificationDetailsDTO> notimap = new ArrayList<>();

		            for (Long id : ids) {
		                Optional<NotificationDetailsDTO> opNotiDetails = notidetailRepo.findCustomNotificationById(id);

		                if (opNotiDetails.isPresent()) {
		                	NotificationDetailsDTO notification = opNotiDetails.get();

		                    
		                        notimap.add(notification);
		                    }
		                
		            }

		            return ResponseEntity.ok(notimap);
		        } else {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		        }

		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		    }
		}

	
	public ResponseEntity<?> MarkALLasRead(String token ,  List<Long> notiIds){
		try {
			if (!jwtUtil.validateToken(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        }
			String email= jwtUtil.getUsernameFromToken(token);
		    Optional<Muser> opuser=muserRepository.findByEmail(email);
		    if(opuser.isPresent()) {
		    	Muser user=opuser.get();
		    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", user.getInstitutionName());
		   	    	if(!adminIsactive) {
		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		   	    	}
				for(Long id :notiIds) {
					NotificationUser notiuser= notiuserRepo.findbyuserIdNotificationId(user.getUserId(), id);
					notiuser.setIs_read(true);
					notiuserRepo.save(notiuser);
				}
				return ResponseEntity.ok().body("");
		    }else {
		    	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		    }
			
		}catch (Exception e) {
		        // If an error occurs, return 500
		    	  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		    	            .body("{\"error\": \"An error occurred while processing the request.\"}");
		    	     }
	 }
	
	public ResponseEntity<?> UreadCount(String token) {
		try {

	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	         String email=jwtUtil.getUsernameFromToken(token);
	         Optional<Muser> opmuser= muserRepository.findByEmail(email);
	         if(opmuser.isPresent()) {
	        	 Muser user=opmuser.get();
	        	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", user.getInstitutionName());
		   	    	if(!adminIsactive) {
		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		   	    	}
	        	 LocalDate today=LocalDate.now();
	        	 Long count=notiuserRepo.CountUnreadNotificationOftheUser(user.getUserId(), false,today);
	        	 return ResponseEntity.ok(count);
	         }else {
	        	 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	         }
	         
		
	}catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
    }

}
	
	public ResponseEntity<?>ClearAll(String token){
		 
		try {
			if (!jwtUtil.validateToken(token)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
         String email=jwtUtil.getUsernameFromToken(token);
         Optional<Muser> opmuser= muserRepository.findByEmail(email);
         if(opmuser.isPresent()) {
        	 Muser user=opmuser.get();
        	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", user.getInstitutionName());
	   	    	if(!adminIsactive) {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	    	}
        	 Long id=user.getUserId();
        	 List<Long> ids=notiuserRepo.findprimaryIdsByUserId(id);
        	 for(Long singleid :ids) {
        		 notiuserRepo.deleteById(singleid);
        	 }
        	 return ResponseEntity.ok().build();
         }else {
        	 return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
         }
		}catch (Exception e) {
			e.printStackTrace();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
         }
	}
}