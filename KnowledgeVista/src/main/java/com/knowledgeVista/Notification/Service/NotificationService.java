package com.knowledgeVista.Notification.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.Notification.NotificationDetails;
import com.knowledgeVista.Notification.NotificationType;
import com.knowledgeVista.Notification.NotificationUser;
import com.knowledgeVista.Notification.Repositories.NotificationDetailsRepo;
import com.knowledgeVista.Notification.Repositories.NotificationTypeRepo;
import com.knowledgeVista.Notification.Repositories.NotificationUserRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;

@Service
public class NotificationService {

	@Autowired
	private NotificationDetailsRepo notidetailRepo;
	@Autowired
	private NotificationUserRepo notiuserRepo;
	@Autowired
	private MuserRepositories muserrepositories;
	@Autowired
	private NotificationTypeRepo notitypeRepo;
	
	
  //Create Notification with Multipart
	
    public Long  createNotification(String type,String username, String description, String createdBy,String heading,String link ,Optional<MultipartFile> file) {
    	NotificationType notificationType = notitypeRepo.findByType(type)
                .orElseGet(() -> {
                    NotificationType newType = new NotificationType(type);
                    return notitypeRepo.save(newType); // Save and return the new type
                });
        
        NotificationDetails notiDetails= new NotificationDetails();
        notiDetails.setHeading(heading);
        notiDetails.setLink(link);
        if (file.isPresent()) { // Check if file is present (for approach 2)
            try {
                notiDetails.setNotimage(ImageUtils.compressImage(file.get().getBytes()));; 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        notiDetails.setNotifyTypeId(notificationType.getNotifyTypeId());
        notiDetails.setUsername(username);
        notiDetails.setCreatedBy(createdBy);
        notiDetails.setCreatedDate(LocalDate.now());
        notiDetails.setIsActive(true);
        notiDetails.setDescription(description);
        NotificationDetails savedNotiDetails= notidetailRepo.save(notiDetails);
        return (savedNotiDetails.getNotifyId());
    }
    
    //Create Notification with byte image
    public Long  createNotification(String type,String username, String description, String createdBy,String heading,String link ,byte[] file) {
    	NotificationType notificationType = notitypeRepo.findByType(type)
                .orElseGet(() -> {
                    NotificationType newType = new NotificationType(type);
                    return notitypeRepo.save(newType); // Save and return the new type
                });
        
        NotificationDetails notiDetails= new NotificationDetails();
        notiDetails.setHeading(heading);
        notiDetails.setLink(link);
      
        notiDetails.setNotimage(file);
        
        notiDetails.setNotifyTypeId(notificationType.getNotifyTypeId());
        notiDetails.setUsername(username);
        notiDetails.setCreatedBy(createdBy);
        notiDetails.setCreatedDate(LocalDate.now());
        notiDetails.setIsActive(true);
        notiDetails.setDescription(description);
        NotificationDetails savedNotiDetails= notidetailRepo.save(notiDetails);
        return (savedNotiDetails.getNotifyId());
    }
 //create notification without pic   
    public Long  createNotification(String type,String username, String description, String createdBy,String heading,String link ) {
    	NotificationType notificationType = notitypeRepo.findByType(type)
                .orElseGet(() -> {
                    NotificationType newType = new NotificationType(type);
                    return notitypeRepo.save(newType); // Save and return the new type
                });
        
        NotificationDetails notiDetails= new NotificationDetails();
        notiDetails.setHeading(heading);
        notiDetails.setLink(link); 
        notiDetails.setNotifyTypeId(notificationType.getNotifyTypeId());
        notiDetails.setUsername(username);
        notiDetails.setCreatedBy(createdBy);
        notiDetails.setCreatedDate(LocalDate.now());
        notiDetails.setIsActive(true);
        notiDetails.setDescription(description);
        NotificationDetails savedNotiDetails= notidetailRepo.save(notiDetails);
        return (savedNotiDetails.getNotifyId());
    }
    
    
    public Boolean SpecificCreateNotification(Long notificationId, List<Long> userlist) {
    	for(Long singleuser :userlist) {
    		 NotificationUser notificationUser = new NotificationUser();
			 notificationUser.setUserid(singleuser);
			 notificationUser.setNotificationId(notificationId);
			 notificationUser.setIs_read(false);
			 notificationUser.setIs_Active(true);
			 notiuserRepo.save(notificationUser);
    	}
    	return(true);
    }
    
    public Boolean SpecificCreateNotification(Long notificationId, List<Long> userlist,LocalDate datetonotify) {
    	for(Long singleuser :userlist) {
    		 NotificationUser notificationUser = new NotificationUser();
			 notificationUser.setUserid(singleuser);
			 notificationUser.setNotificationId(notificationId);
			 notificationUser.setIs_read(false);
			 notificationUser.setIs_Active(true);
			 notificationUser.setDatetonotify(datetonotify);
			 notiuserRepo.save(notificationUser);
    	}
    	return(true);
    }
    public Boolean LicenceExpitedNotification(Long notificationId ,LocalDate datetonotify,String institution) {
    	List<Muser> listofadmins =muserrepositories.findByRoleNameAndInstitutionName("ADMIN", institution);
		for(Muser admin :listofadmins) {
			 NotificationUser notificationUser = new NotificationUser();
			 notificationUser.setUserid(admin.getUserId());
			 notificationUser.setNotificationId(notificationId);
			 notificationUser.setIs_read(false);
			 notificationUser.setIs_Active(true);
			 notificationUser.setDatetonotify(datetonotify);
			 notiuserRepo.save(notificationUser);
		}
    	return(true);
    }
    
    public Boolean CommoncreateNotificationUser(Long notificationId , List<String> userlist,String institution) {
    	for(String singleuser :userlist) {
    		if("ADMIN".equals(singleuser)) {
    			List<Muser> listofadmins =muserrepositories.findByRoleNameAndInstitutionName(singleuser, institution);
    			for(Muser admin :listofadmins) {
    				 NotificationUser notificationUser = new NotificationUser();
    				 notificationUser.setUserid(admin.getUserId());
    				 notificationUser.setNotificationId(notificationId);
    				 notificationUser.setIs_read(false);
    				 notificationUser.setIs_Active(true);
    				 notiuserRepo.save(notificationUser);
    			}
    		}
    		if("TRAINER".equals(singleuser)) {
    			List<Muser> listofTrainer =muserrepositories.findByRoleNameAndInstitutionName(singleuser, institution);
    			for(Muser user :listofTrainer) {
    				 NotificationUser notificationTrainer = new NotificationUser();
    				 notificationTrainer.setUserid(user.getUserId());
    				 notificationTrainer.setNotificationId(notificationId);
    				 notificationTrainer.setIs_read(false);
    				 notificationTrainer.setIs_Active(true);
    				 notiuserRepo.save(notificationTrainer);
    		}
    		}
    		if("USER".equals(singleuser)) {
    			List<Muser> listofuser =muserrepositories.findByRoleNameAndInstitutionName(singleuser, institution);
    			for(Muser user :listofuser) {
    				 NotificationUser notificationUser = new NotificationUser();
    				 notificationUser.setUserid(user.getUserId());
    				 notificationUser.setNotificationId(notificationId);
    				 notificationUser.setIs_read(false);
    				 notificationUser.setIs_Active(true);
    				 notiuserRepo.save(notificationUser);
    		}
    	}
    	}
    	return(true);
    }
    
    
    
    
}
