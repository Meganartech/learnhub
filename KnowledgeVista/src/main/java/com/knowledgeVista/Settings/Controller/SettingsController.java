package com.knowledgeVista.Settings.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Settings.ViewSettings;
import com.knowledgeVista.Settings.Repo.ViewSettingsRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
@CrossOrigin
public class SettingsController {
	@Autowired
	private ViewSettingsRepo settingsrepo;
	
	 @Autowired
	 private JwtUtil jwtUtil;
	public Boolean isViewCourseinLandingPageEnabled() {
		try {
		 Optional<ViewSettings> setting = settingsrepo.findBySettingName("viewCourseInLanding");
	        return setting.map(s -> s.getSettingValue()).orElse(true);
		}catch(Exception e) {
			e.printStackTrace();
			return true;
		}
	    }
	
	 public Boolean updateViewCourseInLandingPage(Boolean isEnabled,String token) {
		 try {
			 if (!jwtUtil.validateToken(token)) {
	             return false;
	         }
	         String role = jwtUtil.getRoleFromToken(token);
	         if("ADMIN".equals(role)) {
	        	
	        Optional<ViewSettings> setting = settingsrepo.findBySettingName("viewCourseInLanding");
             
	        ViewSettings viewSettings;
	        if (setting.isPresent()) {
	            // Update existing setting
	            viewSettings = setting.get();
	           
		      
	        } else {
	            // Create new setting
	            viewSettings = new ViewSettings();
	            viewSettings.setSettingName("viewCourseInLanding");
	          
	        }
	        // Set the new value
	        viewSettings.setSettingValue(isEnabled);
	      settingsrepo.save(viewSettings);
	     
	        return true;
	         }else {
	        	 return false;
	         }
		 }catch(Exception e) {
			 e.printStackTrace();
			 return false;
		 }
	    }
	 
	 
	 public Boolean isSocialLoginEnabled() {
			try {
			 Optional<ViewSettings> setting = settingsrepo.findBySettingName("SocialLogin");
		        return setting.map(s -> s.getSettingValue()).orElse(true);
			}catch(Exception e) {
				e.printStackTrace();
				return true;
			}
		    }
	 public Boolean updateSocialLogin(Boolean isEnabled,String token) {
		 try {
			 if (!jwtUtil.validateToken(token)) {
	             return false;
	         }
	         String role = jwtUtil.getRoleFromToken(token);
	         if("ADMIN".equals(role)) {
	        	
	        Optional<ViewSettings> setting = settingsrepo.findBySettingName("SocialLogin");
             
	        ViewSettings viewSettings;
	        if (setting.isPresent()) {
	            // Update existing setting
	            viewSettings = setting.get();
	           
		      
	        } else {
	            // Create new setting
	            viewSettings = new ViewSettings();
	            viewSettings.setSettingName("SocialLogin");
	          
	        }
	        // Set the new value
	        viewSettings.setSettingValue(isEnabled);
	      settingsrepo.save(viewSettings);
	     
	        return true;
	         }else {
	        	 return false;
	         }
		 }catch(Exception e) {
			 e.printStackTrace();
			 return false;
		 }
	    }
	 
}
