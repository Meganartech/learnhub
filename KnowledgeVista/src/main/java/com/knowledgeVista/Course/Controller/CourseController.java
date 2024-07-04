package com.knowledgeVista.Course.Controller;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.videoLessons;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.Notification.Service.NotificationService;
import com.knowledgeVista.Payments.Course_PartPayment_Structure;
import com.knowledgeVista.Payments.InstallmentDetails;
import com.knowledgeVista.Payments.installmentdetilsrepo;
import com.knowledgeVista.Payments.partpayrepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

import io.jsonwebtoken.io.DecodingException;
import jakarta.transaction.Transactional;

@RestController
public class CourseController {

	
	@Autowired
	private MuserRepositories muserRepository;
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private JwtUtil jwtUtil;
	 
	 @Autowired
	 private installmentdetilsrepo installmentrepo ;
	 
	 @Autowired
	 private partpayrepo partpayrepo;
	 
	 @Autowired
	private NotificationService notiservice;
	
	
//`````````````````````````WORKING``````````````````````````````````
	
	 public ResponseEntity<?> countCourse(String token) {
	     try {
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
	         if (!"ADMIN".equals(role)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	         Long count = coursedetailrepository.countCourseByInstitutionName(institution);
	            Long trainercount = muserRepository.countByRoleNameandInstitutionName("TRAINER", institution);
	            Long usercount = muserRepository.countByRoleNameandInstitutionName("USER", institution);
	            
	            Long  totalAvailableSeats = coursedetailrepository.countTotalAvailableSeats(institution);
	            

	 	       Map<String, Long> response = new HashMap<>();
	 	       response.put("coursecount",count);
	 	       response.put("trainercount",trainercount);
	 	       response.put("usercount", usercount);
	 	       response.put("availableseats", totalAvailableSeats);
	 	       
	         return ResponseEntity.ok().body(response);
	     } catch (DecodingException ex) {
	         // Log the decoding exception
	         ex.printStackTrace(); 
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     } catch (Exception e) {
	         e.printStackTrace(); // You can replace this with logging framework like Log4j
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }

	 //--------------------------working------------------------------------
	

	    public ResponseEntity<?> addCourse( MultipartFile file,  String courseName,String description,
	    		String category,Long Duration,Long Noofseats,Long amount,String paytype,String installmentDataJson, String token) {
		     try {
		    	
		         if (!jwtUtil.validateToken(token)) {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		         }
		         String role = jwtUtil.getRoleFromToken(token);
		         String email=jwtUtil.getUsernameFromToken(token);
		         String username="";
		         String institution="";
			     Optional<Muser> opuser =muserRepository.findByEmail(email);
			     if(opuser.isPresent()) {
			    	 Muser user=opuser.get();
			    	 username=user.getUsername();
			    	 institution=user.getInstitutionName();
			    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
			   	    	if(!adminIsactive) {
			   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			   	    	}
			     }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
		 if("ADMIN".equals(role)) {
	        CourseDetail courseDetail = new CourseDetail();
	        courseDetail.setCourseName(courseName);
	        courseDetail.setCourseDescription(description);
	        courseDetail.setCourseCategory(category);
	        courseDetail.setAmount(amount);
	        courseDetail.setDuration(Duration);
	        courseDetail.setPaytype(paytype);
            courseDetail.setInstitutionName(institution);
	        courseDetail.setNoofseats(Noofseats);
	        try {
	        	 courseDetail.setCourseImage(ImageUtils.compressImage(file.getBytes()));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        CourseDetail savedCourse = coursedetailrepository.save(courseDetail);
	        
	        String courseUrl = "/courses/"+savedCourse.getCourseName()+"/" + savedCourse.getCourseId();
	        savedCourse.setCourseUrl(courseUrl);
	       CourseDetail saved= coursedetailrepository.save(savedCourse);
	       Long courseId=saved.getCourseId();
	       String coursename =saved.getCourseName();
	       String heading="New Course Added !";
	       String link=courseUrl;
	       String notidescription= "A new Course "+coursename + " was added " + saved.getCourseDescription();
	      Long NotifyId =  notiservice.createNotification("CourseAdd",username,notidescription ,email,heading,link, Optional.ofNullable(file));
	        if(NotifyId!=null) {
	        	List<String> notiuserlist = new ArrayList<>(); 
	        	notiuserlist.add("ADMIN");
	        	notiuserlist.add("USER");
	        	notiservice.CommoncreateNotificationUser(NotifyId,notiuserlist,institution);
	        }
	       if("PART".equals(paytype)) {
	       Course_PartPayment_Structure paystructure=new Course_PartPayment_Structure();
	        paystructure.setCourse(savedCourse);
	        paystructure.setCreatedBy(email);
	        paystructure.setDatecreated(LocalDate.now());
	        Course_PartPayment_Structure savedpartpay=partpayrepo.save(paystructure);
	        ObjectMapper mapper = new ObjectMapper();
	    	 JsonNode rootNode = mapper.readValue(installmentDataJson, JsonNode.class);

	    	 if (rootNode.isArray()) {
	    	     for (JsonNode installmentNode : rootNode) {
	    	         Long installmentNumber = installmentNode.path("InstallmentNumber").asLong(); // Assuming "name" is string
	    	         Long installmentAmount = installmentNode.path("InstallmentAmount").asLong(); // Default to 0 if missing
	    	         Long durationInDays = installmentNode.path("DurationInDays").asLong(); // Default to 0 if missing
	    	         InstallmentDetails installment=new InstallmentDetails();
	    	         installment.setDurationInDays(durationInDays);
	    	         installment.setInstallmentNumber(installmentNumber);
	    	         installment.setInstallmentAmount(installmentAmount);
	    	         installment.setPartpay(savedpartpay);
	    	         installmentrepo.save(installment);
	    	          
	    	     }
	    	 } else {
	    	     System.err.println("Invalid JSON format - expected an array");
	    	 }
	       }else {
	    	   System.out.println("full");
	    	   }
	       Map<String, Object> response = new HashMap<>();
           response.put("message", "savedSucessfully");
           response.put("courseId", courseId);
           response.put("coursename", coursename);
	         return ResponseEntity.ok(response);
	    
		 }else {

             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
		     }catch (Exception e) {
			        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		        }
		     }
	
//``````````````````````````````````````````FOR TRAINER COURSE CREATION````````````````````````````````````````

	    public ResponseEntity<?> addCourseByTrainer( MultipartFile file,  String courseName, 
	    		String description, String category,
	    		Long Duration, Long Noofseats, Long amount, String token) 
	    {
		 if (!jwtUtil.validateToken(token)) {
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }

	     String role = jwtUtil.getRoleFromToken(token);
	     String email=jwtUtil.getUsernameFromToken(token);
	       Optional<Muser> optrainer=muserRepository.findByEmail(email);
	       if(optrainer.isPresent()) {
	    	   String username="";
	    		Muser trainer =optrainer.get();
	    		 username=trainer.getUsername();
	    		 String institution= trainer.getInstitutionName();
	    		 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
		   	    	if(!adminIsactive) {
		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		   	    	}
	    		 if(! "TRAINER".equals(trainer.getRole().getRoleName())) {
	                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    		 }
	        CourseDetail courseDetail = new CourseDetail();
	        courseDetail.setCourseName(courseName);
	        courseDetail.setCourseDescription(description);
	        courseDetail.setCourseCategory(category);
	        courseDetail.setAmount(amount);
	        courseDetail.setDuration(Duration);
	        courseDetail.setInstitutionName(institution);
	        courseDetail.setNoofseats(Noofseats);
	        try {
	        	 courseDetail.setCourseImage(ImageUtils.compressImage(file.getBytes()));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        // Save the CourseDetail object
	        CourseDetail savedCourse = coursedetailrepository.save(courseDetail);
	        
	        // Update the courseUrl based on the saved course's ID
	        String courseUrl = "/courses/"+savedCourse.getCourseName()+"/" + savedCourse.getCourseId();
	        savedCourse.setCourseUrl(courseUrl);
	       

	        // Save the updated CourseDetail object
	       CourseDetail saved= coursedetailrepository.save(savedCourse);
	       
	       
	       Long courseId=saved.getCourseId();
	       String coursename =saved.getCourseName();
	      
	    		trainer.getAllotedCourses().add(saved);
	    		  muserRepository.save(trainer);
	    	
	    	String coursenametosend =saved.getCourseName();
		       String heading="New Course Added !";
		       String link=courseUrl;
		       String notidescription= "A new Course "+coursenametosend + " was added " + saved.getCourseDescription();
		      Long NotifyId =  notiservice.createNotification("CourseAdd",username,notidescription ,email,heading,link, Optional.ofNullable(file));
		        if(NotifyId!=null) {
		        	List<String> notiuserlist = new ArrayList<>(); 
		        	notiuserlist.add("ADMIN");
		        	notiuserlist.add("USER");
		        	notiuserlist.add("TRAINER");
		        	notiservice.CommoncreateNotificationUser(NotifyId,notiuserlist,institution);
		        }
	       Map<String, Object> response = new HashMap<>();
        response.put("message", "savedSucessfully");
        response.put("courseId", courseId);
        response.put("coursename", coursename);
	         return ResponseEntity.ok(response);
	     }else {
	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }
	    }
	

	 
	//--------------------------working------------------------------------
	
	    public ResponseEntity<?> updateCourse( String token ,Long courseId, MultipartFile file, String courseName, String description, String category, Long Noofseats, Long Duration, Long amount) {
	        try {
	       	 if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }

	         String role = jwtUtil.getRoleFromToken(token);
	         String email=jwtUtil.getUsernameFromToken(token);
	         String username="";
	         String institution="";
		     Optional<Muser> opuser =muserRepository.findByEmail(email);
		     if(opuser.isPresent()) {
		    	 Muser user=opuser.get();
		    	 username=user.getUsername();
		    	 institution=user.getInstitutionName();
		    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
		   	    	if(!adminIsactive) {
		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		   	    	}
		     }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }
	         if("ADMIN".equals(role)||"TRAINER".equals(role)) {
	       	 
	            Optional<CourseDetail> courseDetailOptional = coursedetailrepository.findById(courseId);
	            if (courseDetailOptional.isPresent()) {
	                CourseDetail existingCourseDetail = courseDetailOptional.get();
	                //need to work
	                List<Muser> users=existingCourseDetail.getUsers();
	                List<Long> ids = new ArrayList<>(); 
	                if(users != null) {
	                for (Muser user : users) {
	                	ids.add(user.getUserId());
	                }
	                }
	                if (!courseName.isEmpty()) {
	                    existingCourseDetail.setCourseName(courseName);
	                    String courseUrl = "/courses/" + existingCourseDetail.getCourseName() + "/" + existingCourseDetail.getCourseId();
	                    existingCourseDetail.setCourseUrl(courseUrl);
	                }
	                if (!description.isEmpty()) {
	                    existingCourseDetail.setCourseDescription(description);
	                }
	                if (!category.isEmpty()) {
	                    existingCourseDetail.setCourseCategory(category);
	                }
	                if (Duration != null) {
	                    existingCourseDetail.setDuration(Duration);
	                }
	                if (Noofseats != null) {
	                    existingCourseDetail.setNoofseats(Noofseats);
	                }
	                if (amount != null) {
	                    existingCourseDetail.setAmount(amount);
	                }
	                existingCourseDetail.setUsers(null);
	                existingCourseDetail.setVideoLessons(null);
	                if (file != null) {
	                    existingCourseDetail.setCourseImage(ImageUtils.compressImage(file.getBytes()));
	                }

	                CourseDetail updatedCourse = coursedetailrepository.saveAndFlush(existingCourseDetail);
	            
	                String heading=" Course Updated !";
	 		       String link=updatedCourse.getCourseUrl();
	 		       String notidescription= " Course "+updatedCourse.getCourseName() + " was Updated " ;
	 		      Long NotifyId =  notiservice.createNotification("CourseAdd",username,notidescription ,email,heading,link, Optional.ofNullable(file));
	 		        if(NotifyId!=null) {
	 		         	notiservice.SpecificCreateNotification(NotifyId, ids);
	 		        	List<String> notiuserlist = new ArrayList<>(); 
	 		        	notiuserlist.add("ADMIN");
	 		        	notiservice.CommoncreateNotificationUser(NotifyId,notiuserlist,institution);
	 		        }
	                return ResponseEntity.ok("{\"message\": \"Saved successfully\"}");
	            } else {
	                return ResponseEntity.notFound().build();
	            }}else {

		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            return ResponseEntity.badRequest().body("{\"message\": \"Error occurred while processing the image\"}");
	        }
	    }
	//--------------------------working-----------------------------------

	 public ResponseEntity<CourseDetail> getCourse( Long courseId ,String token) {
		 if (!jwtUtil.validateToken(token)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
         String role = jwtUtil.getRoleFromToken(token);
         String email=jwtUtil.getUsernameFromToken(token);
         String institution="";
	     Optional<Muser> opuser =muserRepository.findByEmail(email);
	     if(opuser.isPresent()) {
	    	 Muser user=opuser.get();
	    	 institution=user.getInstitutionName();
	    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
	   	    	if(!adminIsactive) {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	   	    	}
	     }else {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }
         if("ADMIN".equals(role)||"TRAINER".equals(role)) {
		    Optional<CourseDetail> courseOptional = coursedetailrepository.findByCourseIdAndInstitutionName(courseId, institution);
		    if (courseOptional.isPresent()) {
		        CourseDetail course = courseOptional.get();
		        byte[] image= ImageUtils.decompressImage(course.getCourseImage());
		        course.setCourseImage(image);
		        course.setUsers(null);
		        course.setTrainer(null);
	            course.setVideoLessons(null);
		        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(course);
		    } else {
		        // Handle the case when the course with the given ID does not exist
		        return ResponseEntity.notFound().build();
		    }}else {

	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		    }
		}

	//--------------------------working------------------------------------

	    public ResponseEntity<List<CourseDetail>> viewCourse(String token ) {
	    	 if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	         String email=jwtUtil.getUsernameFromToken(token);
	         String institution="";
		     Optional<Muser> opuser =muserRepository.findByEmail(email);
		     if(opuser.isPresent()) {
		    	 Muser user=opuser.get();
		    	 institution=user.getInstitutionName();
		    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
		   	    	if(!adminIsactive) {
		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		   	    	}
		     }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }
	        List<CourseDetail> courses = coursedetailrepository.findAllByInstitutionName(institution);
	        
	        for (CourseDetail course : courses) {
	        
	        	 byte[] images =ImageUtils.decompressImage(course.getCourseImage());
	            course.setCourseImage(images);
	            course.setUsers(null);
		        course.setTrainer(null);
	            course.setVideoLessons(null);
	        }
	        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(courses);
	    }

	 //-------------------------Under check------------------------------------

	   public ResponseEntity<?> getAllCourseInfo(  String token, String email) {
		   
	          if (!jwtUtil.validateToken(token)) {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);
	          String adding=jwtUtil.getUsernameFromToken(token);
		         String institution="";
			     Optional<Muser> opaddinguser =muserRepository.findByEmail(adding);
			     if(opaddinguser.isPresent()) {
			    	 Muser addinguser=opaddinguser.get();
			    	 institution=addinguser.getInstitutionName();
			    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
			   	    	if(!adminIsactive) {
			   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			   	    	}
			     }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
	          if ("ADMIN".equals(role)||"TRAINER".equals(role)) {
	        	  Optional<Muser> optionalUser = muserRepository.findByEmail(email);
	      	    if (optionalUser.isPresent()) {
	      	        Muser user = optionalUser.get();
	      	        
	      	        if("USER".equals(user.getRole().getRoleName())) {
	      	        List<CourseDetail> courses = user.getCourses();
	        	  
	               List<Map<String, Object>> courseInfoList = coursedetailrepository.findAllByInstitutionName(institution).stream()
	                 .map(course -> {

			               boolean isSelected = courses.contains(course);
	                       Map<String, Object> courseInfo = Map.of(
	                           "courseId", course.getCourseId(),
	                           "courseName", course.getCourseName(),
	                           "selected", isSelected
	                   );
	                   return courseInfo;
	               })
	               .collect(Collectors.toList());
	       return ResponseEntity.ok().body(courseInfoList);}
	      	        else {
	    	   return ResponseEntity.notFound().build();
	       }
	      	    }
	      	    return ResponseEntity.notFound().build();
	       }else {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    	   
	       }
	   }

	   public ResponseEntity<?> getAllAllotelistInfo( String token,String email) {
		   
	          if (!jwtUtil.validateToken(token)) {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          String role = jwtUtil.getRoleFromToken(token);
	          String adding=jwtUtil.getUsernameFromToken(token);
		         String institution="";
			     Optional<Muser> opaddinguser =muserRepository.findByEmail(adding);
			     if(opaddinguser.isPresent()) {
			    	 Muser addinguser=opaddinguser.get();
			    	 institution=addinguser.getInstitutionName();
			    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
			   	    	if(!adminIsactive) {
			   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			   	    	}
			     }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
	          if ("ADMIN".equals(role)) {
	        	  Optional<Muser> optionalUser = muserRepository.findByEmail(email);
	      	    if (optionalUser.isPresent()) {
	      	    	
	      	        Muser user = optionalUser.get();
	      	        if("TRAINER".equals(user.getRole().getRoleName())) {
	      	        List<CourseDetail> courses = user.getAllotedCourses();
	        	  
	       List<Map<String, Object>> courseInfoList = coursedetailrepository.findAllByInstitutionName(institution)
	               .stream()
	               .map(course -> {

		               boolean isSelected = courses.contains(course);
	                   Map<String, Object> courseInfo = Map.of(
	                           "courseId", course.getCourseId(),
	                           "courseName", course.getCourseName(),
	                           "selected", isSelected
	                   );
	                   return courseInfo;
	               })
	               .collect(Collectors.toList());
	       return ResponseEntity.ok().body(courseInfoList);
	       }
	      	        else {
	    	      	    return ResponseEntity.notFound().build();
	      	        	
	      	        }
	      	    }
	      	    return ResponseEntity.notFound().build();
	       }else {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    	   
	       }
	   }


	   //---------------------WORKING--------------
	 
	   public ResponseEntity<String> deleteCourse( Long courseId ,String token) {
	       try {
	           // Find the course by ID
	    	   if (!jwtUtil.validateToken(token)) {
	               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	           }

	           String role = jwtUtil.getRoleFromToken(token);
		          String email=jwtUtil.getUsernameFromToken(token);
		          
		          String adding=jwtUtil.getUsernameFromToken(token);
			         String institution="";
				     Optional<Muser> opaddinguser =muserRepository.findByEmail(adding);
				     if(opaddinguser.isPresent()) {
				    	 Muser addinguser=opaddinguser.get();
				    	 institution=addinguser.getInstitutionName();
				    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
				   	    	if(!adminIsactive) {
				   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				   	    	}
				     }else {
			             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				     }
				     
		          if("ADMIN".equals(role)||"TRAINER".equals(role)) {
	           Optional<CourseDetail> optionalCourse = coursedetailrepository.findByCourseIdAndInstitutionName(courseId, institution);
	           
	           if (optionalCourse.isPresent()) {
	               CourseDetail course = optionalCourse.get();
	               if("TRAINER".equals(role)) {
		        		Optional< Muser> trainerop= muserRepository.findByEmail(email);
		        		  if(trainerop.isPresent()) {
		        			  Muser trainer =trainerop.get();
		        			  if( !trainer.getAllotedCourses().contains(course)) {

		    		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		        			  }
		        		  }
		        	  }
	               
	               // Clear the lists of trainers and users associated with the course
	               course.getTrainer().clear();
	               course.getUsers().clear();
	               
	               // Save the changes to ensure they are reflected in the database
	               coursedetailrepository.save(course);
	               
	               // Remove references to the course from the user_course table
	               for (Muser user : course.getUsers()) {
	                   user.getCourses().remove(course);
	               }
	               for (Muser user :course.getTrainer()) {

	                   user.getAllotedCourses().remove(course);
	               }
	               
	               // Delete the course
	               coursedetailrepository.delete(course);

	               return ResponseEntity.ok("Course deleted successfully");
	           } else {
	               // If the course with the specified ID does not exist
	               return ResponseEntity.notFound().build();
	           }
	       }else {

	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
	       }catch (DataIntegrityViolationException e) {
	           // If a foreign key constraint violation occurs
	           // Return a custom error response with an appropriate status code and message
	           return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                   .body("The course cannot be deleted. Delete all associated lessons, test.");
	       }
	   }

		 //---------------------WORKING--------------)
	   public ResponseEntity<?> getLessons(Long courseId, String token) {
	       try {
	           if (!jwtUtil.validateToken(token)) {
	               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	           }

	           String role = jwtUtil.getRoleFromToken(token);
	           String adding=jwtUtil.getUsernameFromToken(token);
		         String institution="";
			     Optional<Muser> opaddinguser =muserRepository.findByEmail(adding);
			     if(opaddinguser.isPresent()) {
			    	 Muser addinguser=opaddinguser.get();
			    	 institution=addinguser.getInstitutionName();
			    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
			   	    	if(!adminIsactive) {
			   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			   	    	}
			     }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
	           if (!"ADMIN".equals(role)) {
	        	   Optional<CourseDetail> opcourse=coursedetailrepository.findByCourseIdAndInstitutionName(courseId, institution);
	        	   if(opcourse.isPresent()) {
	        		   CourseDetail course=opcourse.get();
	        		   if(course.getAmount()== 0) {
	        			   return getVideoLessonsResponse(courseId); 
	        		   }
	        	   }else {
	        		   return ResponseEntity.notFound().build();
	        	   }
	               String email = jwtUtil.getUsernameFromToken(token);
	               Optional<Muser> opuser = muserRepository.findByEmail(email);
	               if (!opuser.isPresent()) {
	                   return ResponseEntity.notFound().build();
	               }
	               Muser user = opuser.get();
	               
	               if ("TRAINER".equals(role)) {
	                   if (user.getAllotedCourses().stream().anyMatch(course -> course.getCourseId().equals(courseId))) {
	                       return getVideoLessonsResponse(courseId);
	                   }
	               } else if ("USER".equals(role)) {
	            	   
	                   if (user.getCourses().stream().anyMatch(course -> course.getCourseId().equals(courseId))) {
	                       return getVideoLessonsResponse(courseId);
	                   }
	               }
	               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	           } else {
	        	   Optional<CourseDetail> opcourse=coursedetailrepository.findByCourseIdAndInstitutionName(courseId, institution);
	        	   if(opcourse.isPresent()) {
	        		   CourseDetail course=opcourse.get();
	        			   return getVideoLessonsResponse(courseId); 
	        		   
	        	   }else {
	        		   return ResponseEntity.notFound().build();
	        	   }
	           }
	       } catch (Exception e) {
	           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	       }
	   }

	   private ResponseEntity<?> getVideoLessonsResponse(Long courseId) {
	       Optional<CourseDetail> opcourse = coursedetailrepository.findById(courseId);
	       if (opcourse.isPresent()) {
	           List<videoLessons> videolessonlist = opcourse.get().getVideoLessons();
	           for (videoLessons video : videolessonlist) {
	               video.setCourseDetail(null);
	               video.setVideoFile(null);
	               video.setVideofilename(null);

	               byte[] images = ImageUtils.decompressImage(video.getThumbnail());
	               video.setThumbnail(images);
	           }
	           return ResponseEntity.ok(videolessonlist);
	       }
	       return ResponseEntity.notFound().build();
	   }

		 public ResponseEntity<?> getLessonList( Long courseId , String token) {
			 try {
				 if (!jwtUtil.validateToken(token)) {
		               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		           }

		           String role = jwtUtil.getRoleFromToken(token);
			          String email=jwtUtil.getUsernameFromToken(token);
			          
			          String reqUser=jwtUtil.getUsernameFromToken(token);
				         String institution="";
					     Optional<Muser> opreqsUser =muserRepository.findByEmail(reqUser);
					     if(opreqsUser.isPresent()) {
					    	 Muser requestuser=opreqsUser.get();
					    	 institution=requestuser.getInstitutionName();
					    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
					   	    	if(!adminIsactive) {
					   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
					   	    	}
					     }else {
				             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
					     }
					     
			          if("ADMIN".equals(role)||"TRAINER".equals(role)) {
			
			    Optional<CourseDetail> opcourse = coursedetailrepository.findByCourseIdAndInstitutionName(courseId, institution);
			    if (opcourse.isPresent()) {
			    	
			    	 if("TRAINER".equals(role)) {
			        		Optional< Muser> trainerop= muserRepository.findByEmail(email);
			        		  if(trainerop.isPresent()) {
			        			  Muser trainer =trainerop.get();
			        			  if( !trainer.getAllotedCourses().contains(opcourse.get())) {

			    		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			        			  }
			        		  }
			        	  }
			        List<videoLessons> videolessonlist = opcourse.get().getVideoLessons();
			        List<Map<String, Object>> lessonResponseList = new ArrayList<>();

			        for (videoLessons video : videolessonlist) {
			            Map<String, Object> response = new HashMap<>();
			            response.put("Lessontitle", video.getLessontitle());
			            response.put("lessonId", video.getLessonId());
			            lessonResponseList.add(response);
			        }

			        return ResponseEntity.ok(lessonResponseList);
			    }

			    return ResponseEntity.notFound().build();
			}else {

	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
       } catch (Exception e) {
           e.printStackTrace(); // Print the stack trace for debugging
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error creating test: " + e.getMessage());
       }
		 }
		 
}
