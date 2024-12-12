package com.knowledgeVista.User.LabellingItems.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.LabellingItems.Labelingitems;
import com.knowledgeVista.User.LabellingItems.Repo.LabellingitemsRepo;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
@CrossOrigin
public class LadellingitemController {
	@Autowired
	private LabellingitemsRepo labellingrepo;
	@Autowired
	private MuserRepositories muserrepositories;
	 @Autowired
	 private JwtUtil jwtUtil;

	 
	 public ResponseEntity<?>SaveLabellingitems(String token,  String siteUrl,String title, MultipartFile sitelogo,  MultipartFile siteicon,  MultipartFile titleicon){
		  try {
	        	if (!jwtUtil.validateToken(token)) {
	   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }
	   	     String role = jwtUtil.getRoleFromToken(token);
	   	     if(!"ADMIN".equals(role)) {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }
	   	     String email=jwtUtil.getUsernameFromToken(token);
	   	     Optional<Muser>opreq=muserrepositories.findByEmail(email);
	   	     String institution="";
	   	     if(opreq.isPresent()) {
	   	    	 Muser requser=opreq.get();
	   	    	institution=requser.getInstitutionName();
	   	    	Optional<Labelingitems>label=labellingrepo.FindbyInstitution(institution);
	   	    	if(label.isPresent()) {
	   	    		Labelingitems existinglabels=label.get();
	   	    		
	   	    		existinglabels.setSiteUrl(siteUrl);
	   	    		existinglabels.setTitle(title);
	   	    		
	   	    	 if (siteicon != null && !siteicon.isEmpty()) {
	   	          existinglabels.setSiteicon(siteicon.getBytes());
	   	      }

	   	      if (sitelogo != null && !sitelogo.isEmpty()) {
	   	          existinglabels.setSitelogo(sitelogo.getBytes());
	   	      }

	   	      if (titleicon != null && !titleicon.isEmpty()) {
	   	          existinglabels.setTitleicon(titleicon.getBytes());
	   	      }

	   	      labellingrepo.save(existinglabels);
	   	      return ResponseEntity.ok("Updated ");
	   	  } else {
	   	      Labelingitems labels = new Labelingitems();
	   	      labels.setSiteUrl(siteUrl);
              labels.setTitle(title);
	   	      // Check if each file is not null before calling getBytes()
	   	      if (siteicon != null && !siteicon.isEmpty()) {
	   	          labels.setSiteicon(siteicon.getBytes());
	   	      }

	   	      if (sitelogo != null && !sitelogo.isEmpty()) {
	   	          labels.setSitelogo(sitelogo.getBytes());
	   	      }

	   	      if (titleicon != null && !titleicon.isEmpty()) {
	   	          labels.setTitleicon(titleicon.getBytes());
	   	      }

	   	      labels.setInstitutionName(institution);
	   	      labellingrepo.save(labels);

	   	    	return ResponseEntity.ok("saved ");
	   	    	}
	 }else {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	 }
		  }catch (Exception e) {
		    e.printStackTrace();
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
}
	 
	 public ResponseEntity<?>getLabelingitems(String token){
		  try {
	        	if (!jwtUtil.validateToken(token)) {
	   	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }
	   	     String role = jwtUtil.getRoleFromToken(token);
	   	     if(!"ADMIN".equals(role)) {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	     }
	   	     String email=jwtUtil.getUsernameFromToken(token);
	   	     Optional<Muser>opreq=muserrepositories.findByEmail(email);
	   	     String institution="";
	   	     if(opreq.isPresent()) {
	   	    	 Muser requser=opreq.get();
	   	    	institution=requser.getInstitutionName();
	   	    	Optional<Labelingitems>labels=labellingrepo.FindbyInstitution(institution);
	   	       if(labels.isPresent()) {
	   	    	   return ResponseEntity.ok(labels);
	   	       }else {
	   	    	   return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	   	       }
		            
	  }else {
		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	 }
		  }catch (Exception e) {
		    e.printStackTrace();
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
}
	 public ResponseEntity<?>getLabelingitemsforall(){
		 try {
			 String instutionname=muserrepositories.getInstitution("ADMIN");
			 if(instutionname.isEmpty()||instutionname.equals(null)) {
				 return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			 }else {
				 Optional<Labelingitems>labels=labellingrepo.FindbyInstitution(instutionname);
		   	       if(labels.isPresent()) {
		   	    	   return ResponseEntity.ok(labels);
		   	       }else {
		   	    	   return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		   	       }
			 }
		 }catch(Exception e) {
			 e.printStackTrace();
			 return ResponseEntity.internalServerError().build();
		 }
	 
	 }
}
