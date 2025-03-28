package com.knowledgeVista.Course.Controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgeVista.Batch.Batch;
import com.knowledgeVista.Batch.Repo.BatchRepository;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.CourseDetailDto;
import com.knowledgeVista.Course.LessonQuizDTO;
import com.knowledgeVista.Course.videoLessons;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Course.Repository.videoLessonRepo;
import com.knowledgeVista.License.licenseRepository;
import com.knowledgeVista.Notification.Service.NotificationService;
import com.knowledgeVista.Payments.repos.OrderuserRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import io.jsonwebtoken.io.DecodingException;

@RestController
public class CourseController {

	
	@Autowired
	private MuserRepositories muserRepository;
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
	 private videoLessonRepo lessonRepo;
	 
	 @Autowired
	private NotificationService notiservice;
	 
	 @Autowired
	 private licenseRepository licencerepo;
	 @Autowired
	 private OrderuserRepo orderuser;
	 @Autowired
	 private BatchRepository batchrepo;
	 
	 
	 @Value("${spring.environment}")
	    private String environment;
	
  	 private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

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
	            Long amountRecived=orderuser.getTotalAmountReceivedByInstitution(institution);
                Long paidcourse=coursedetailrepository.countPaidCoursesByInstitution(institution);
	 	       Map<String, Long> response = new HashMap<>();
	 	       response.put("coursecount",count);
	 	       response.put("trainercount",trainercount);
	 	       response.put("usercount", usercount);
	 	       response.put("availableseats", totalAvailableSeats);
	 	       response.put("paidcourse", paidcourse);
	 	       response.put("amountRecived", amountRecived);
	 	       
	         return ResponseEntity.ok().body(response);
	     } catch (DecodingException ex) {
	         // Log the decoding exception
	         ex.printStackTrace();    logger.error("", ex);; 
	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     } catch (Exception e) {
	         e.printStackTrace();    logger.error("", e);; // You can replace this with logging framework like Log4j
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }

	 //--------------------------working------------------------------------
	

	    public ResponseEntity<?> addCourse( MultipartFile file,  String courseName,String description,
	    		String category,Long Duration,Long Noofseats,String batches,Long amount, String token) {
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
			    	 Long coursecount=coursedetailrepository.countCourseByInstitutionName(institution);
			    	 Long MaxCount=licencerepo.FindCourseCountByinstitution(institution);
			    	 if(coursecount+1 >MaxCount) {
			    		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course Limit Reached Add More Course By Upgrading Your Licence");
			    	 }
			    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
			   	    	if(!adminIsactive) {
			   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			   	    	}
			     }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
		 if("ADMIN".equals(role)||"TRAINER".equals(role)) {
	        CourseDetail courseDetail = new CourseDetail();
	        courseDetail.setCourseName(courseName);
	        courseDetail.setCourseDescription(description);
	        courseDetail.setCourseCategory(category);
	        courseDetail.setAmount(amount);
	        courseDetail.setDuration(Duration);
            courseDetail.setInstitutionName(institution);
	        courseDetail.setNoofseats(Noofseats);
	        courseDetail.setCourseImage(file.getBytes());
	       
	        CourseDetail savedCourse = coursedetailrepository.save(courseDetail);
	        ObjectMapper objectMapper = new ObjectMapper();
	   	 List<Map<String, Object>> batchess = objectMapper.readValue(batches, List.class);
	   	 for (Map<String, Object> batch : batchess) {
        	 Long batchid = ((Number) batch.get("id")).longValue();
        	Optional<Batch> opbatch =batchrepo.findBatchByIdAndInstitutionName(batchid, institution);
        	if(opbatch.isPresent()) {
        		Batch existing=opbatch.get();
        		existing.getCourses().add(savedCourse);
        		if(existing.getAmount()==null) {
        			existing.setAmount(amount);
        		}
        		if(existing.getNoOfSeats()==null) {
        			existing.setNoOfSeats(Noofseats);
        		}
        		batchrepo.save(existing);
        	}
	   	 }
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
	       Map<String, Object> response = new HashMap<>();
           response.put("message", "savedSucessfully");
           response.put("courseId", courseId);
           response.put("coursename", coursename);
	         return ResponseEntity.ok(response);
		 }else {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
		     }catch (Exception e) {
		    	 e.printStackTrace();    logger.error("", e);;
			        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		        }
		     }
	
//``````````````````````````````````````````FOR TRAINER COURSE CREATION````````````````````````````````````````

//	    public ResponseEntity<?> addCourseByTrainer( MultipartFile file,  String courseName, 
//	    		String description, String category,
//	    		Long Duration, Long Noofseats, Long amount, String token) 
//	    {
//		 if (!jwtUtil.validateToken(token)) {
//	         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//	     }
//
//	     jwtUtil.getRoleFromToken(token);
//	     String email=jwtUtil.getUsernameFromToken(token);
//	       Optional<Muser> optrainer=muserRepository.findByEmail(email);
//	       if(optrainer.isPresent()) {
//	    	   String username="";
//	    		Muser trainer =optrainer.get();
//	    		 username=trainer.getUsername();
//	    		 String institution= trainer.getInstitutionName();
//	    		 Long coursecount=coursedetailrepository.countCourseByInstitutionName(institution);
//		    	 Long MaxCount=licencerepo.FindCourseCountByinstitution(institution);
//		    	 if(coursecount+1 >MaxCount) {
//		    		 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Course Limit Reached Add More Course By Upgrading Your Licence");
//		    	 }
//	    		 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
//		   	    	if(!adminIsactive) {
//		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//		   	    	}
//	    		 if(! "TRAINER".equals(trainer.getRole().getRoleName())) {
//	                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//	    		 }
//	        CourseDetail courseDetail = new CourseDetail();
//	        courseDetail.setCourseName(courseName);
//	        courseDetail.setCourseDescription(description);
//	        courseDetail.setCourseCategory(category);
//	        courseDetail.setAmount(amount);
//	        courseDetail.setDuration(Duration);
//	        courseDetail.setInstitutionName(institution);
//	        courseDetail.setNoofseats(Noofseats);
//	        try {
//				courseDetail.setCourseImage(file.getBytes());
//			} catch (IOException e) {
//				courseDetail.setCourseImage(null);
//				e.printStackTrace();    logger.error("", e);;
//			}
//	       
//	        
//	        // Save the CourseDetail object
//	        CourseDetail savedCourse = coursedetailrepository.save(courseDetail);
//	        
//	        // Update the courseUrl based on the saved course's ID
//	        String courseUrl = "/courses/"+savedCourse.getCourseName()+"/" + savedCourse.getCourseId();
//	        savedCourse.setCourseUrl(courseUrl);
//	       
//
//	        // Save the updated CourseDetail object
//	       CourseDetail saved= coursedetailrepository.save(savedCourse);
//	       
//	       
//	       Long courseId=saved.getCourseId();
//	       String coursename =saved.getCourseName();
//	      
//	    		trainer.getAllotedCourses().add(saved);
//	    		  muserRepository.save(trainer);
//	    	
//	    	String coursenametosend =saved.getCourseName();
//		       String heading="New Course Added !";
//		       String link=courseUrl;
//		       String notidescription= "A new Course "+coursenametosend + " was added " + saved.getCourseDescription();
//		      Long NotifyId =  notiservice.createNotification("CourseAdd",username,notidescription ,email,heading,link, Optional.ofNullable(file));
//		        if(NotifyId!=null) {
//		        	List<String> notiuserlist = new ArrayList<>(); 
//		        	notiuserlist.add("ADMIN");
//		        	notiuserlist.add("USER");
//		        	notiuserlist.add("TRAINER");
//		        	notiservice.CommoncreateNotificationUser(NotifyId,notiuserlist,institution);
//		        }
//	       Map<String, Object> response = new HashMap<>();
//        response.put("message", "savedSucessfully");
//        response.put("courseId", courseId);
//        response.put("coursename", coursename);
//	         return ResponseEntity.ok(response);
//	     }else {
//	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//	     }
//	    }
//	

	 
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
	                	Long count=existingCourseDetail.getUserCount();
	                	if(count<=Noofseats) {
	                    existingCourseDetail.setNoofseats(Noofseats);
	                	}
	                }
	                if (amount != null) {
	                    existingCourseDetail.setAmount(amount);
	                }
	                existingCourseDetail.setUsers(null);
	                existingCourseDetail.setVideoLessons(null);
	                if (file != null) {
	                    existingCourseDetail.setCourseImage(file.getBytes());
	                }

	                CourseDetail updatedCourse = coursedetailrepository.saveAndFlush(existingCourseDetail);
	            
	                String heading=" Course Updated !";
	 		       String link=updatedCourse.getCourseUrl();
	 		       String notidescription= " Course "+updatedCourse.getCourseName() + " was Updated " ;
	 		      Long NotifyId =  notiservice.createNotification("CourseAdd",username,notidescription ,email,heading,link, updatedCourse.getCourseImage());
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
	            e.printStackTrace();    logger.error("", e);;
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
		    Optional<CourseDetail> courseOptional = coursedetailrepository.findMinimalCourseDetailbyCourseIdandInstitutionName(courseId, institution);
		    if (courseOptional.isPresent()) {
		        CourseDetail course = courseOptional.get();
		        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(course);
		    } else {
		        // Handle the case when the course with the given ID does not exist
		        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		    }}else {

	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		    }
		}

	//--------------------------working------------------------------------

	    public ResponseEntity<List<CourseDetailDto>> viewCourse(String token ) {
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
		   	     List<CourseDetailDto> courses = coursedetailrepository.findAllByInstitutionNameDto(institution);
		   	  
			        return ResponseEntity.ok()
		            .contentType(MediaType.APPLICATION_JSON)
		            .body(courses);
		     }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }
	       
	    }
	    
	    public ResponseEntity<List<CourseDetailDto>> viewCourseVps() {
	        if (environment.equals("VPS")) {
	            List<CourseDetailDto> courses = coursedetailrepository.findallcoursevps();
	            
	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .body(courses);
	        }
			return null;
	    }


	 //-------------------------Under check------------------------------------

	   public ResponseEntity<?> getAllCourseInfo(  String token, String email) {
		   try {
	          if (!jwtUtil.validateToken(token)) {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }

	          
	          String adding=jwtUtil.getUsernameFromToken(token);
		         String institution="";
			     Optional<Muser> opaddinguser =muserRepository.findByEmail(adding);
			     //adding Admin or trainer is present or not
			     if(opaddinguser.isPresent()) {
			    	 Muser addinguser=opaddinguser.get();
			    	 String role = addinguser.getRole().getRoleName();
			    	 institution=addinguser.getInstitutionName();
			    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
			   	    	if(!adminIsactive) {
			   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			   	    	}
			   	   //user is present or not
			   	   Optional<Muser> optionalUser = muserRepository.findByEmail(email);
			      	    if (optionalUser.isPresent()) {
			      	        Muser user = optionalUser.get();
			   	     if("USER".equals(user.getRole().getRoleName())) {
	          if ("ADMIN".equals(role)) {
	               List<Map<String, Object>> courseInfoList = coursedetailrepository.findAllCourseDetailsByInstitutionName(institution);
	       return ResponseEntity.ok().body(courseInfoList);
	       
	      	    
	       }else if("TRAINER".equals(role)){
	    	   List<Map<String, Object>> courseInfoList = coursedetailrepository.findAllCourseDetailsByInstitutionName(institution);
		       return ResponseEntity.ok().body(courseInfoList);
		       
	       }else {
	       
	       
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("addding is not Admin or Trainer");}
	    	   
	       } return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email is not of user");
	   }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user is not present");
		     }
	       }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ADDING USER NOT PRESENT");
	       }
	   
	   }catch(Exception e) {
		   e.printStackTrace();
		   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		   }
	   }
	   
	    

	   public ResponseEntity<?> getAllAllotelistInfo( String token,String email) {
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
	          if ("ADMIN".equals(role)) {
	        	  Optional<Muser> optionalUser = muserRepository.findByEmail(email);
	      	    if (optionalUser.isPresent()) {
	      	    	
	      	        Muser user = optionalUser.get();
	      	        if("TRAINER".equals(user.getRole().getRoleName())) {
	       List<Map<String, Object>> courseInfoList = coursedetailrepository.findAllCourseDetailsByInstitutionName(institution);
	       return ResponseEntity.ok().body(courseInfoList);
	       }else {
	    	      	    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("user is not a Trainer");
	      	        	
	      	        }
	      	    } return ResponseEntity.status(HttpStatus.NO_CONTENT).body("user is not found");
	      	       
	       }else {
	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    	   
	       }
		   }catch(Exception e) {
			   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
	               
	             

	               coursedetailrepository.delete(course);

	               return ResponseEntity.ok("Course deleted successfully");
	           } else {
	               // If the course with the specified ID does not exist
	               return ResponseEntity.ok("course Not Found");
	           }
	       }else {

	              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	          }
	       }catch (DataIntegrityViolationException e) {
	           // If a foreign key constraint violation occurs
	           // Return a custom error response with an appropriate status code and message
	    	   e.printStackTrace();    logger.error("", e);;
	           return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                   .body("The course cannot be deleted. Delete all associated lessons, test.");
	       }catch(Exception e) {
	    	   logger.error("", e);;
	    	   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
	        		
	        	   
	               String email = jwtUtil.getUsernameFromToken(token);
	               Optional<Muser> opuser = muserRepository.findByEmail(email);
	               if (!opuser.isPresent()) {
	                   return ResponseEntity.notFound().build();
	               }
	               Muser user = opuser.get();
	               
	               if ("TRAINER".equals(role)) {
	            	   if(course.getAmount()== 0) {
	        			   return getVideoLessonsResponse(courseId); 
	        		   }
	                //   if (user.getAllotedCourses().stream().anyMatch(course -> course.getCourseId().equals(courseId))) {
	            	   if(muserRepository.existsByTrainerIdAndCourseId(user.getUserId(), courseId)) {
	                       return getVideoLessonsResponse(courseId);
	                   }
	            	  
	               } else if ("USER".equals(role)) {
	            	   if(course.getAmount()== 0) {
	            		   Boolean enrolled=muserRepository.existsByuserIdAndCourseId(user.getUserId(), courseId);
	            		   if(!enrolled) {
	            			   user.getCourses().add(course);
	            			   muserRepository.save(user);
	            			   return getVideoLessonsResponse(courseId); 
	            		   }
	        			   return getVideoLessonsResponse(courseId); 
	        		   }
	            	   
	                 //  if (user.getCourses().stream().anyMatch(course -> course.getCourseId().equals(courseId))) {
	            	   if(muserRepository.existsByuserIdAndCourseId(user.getUserId(), courseId)) {
	                       return getVideoLessonsResponse(courseId);
	                   }
	               }
	        	   }else {
	        		   return ResponseEntity.notFound().build();
	        	   }
	               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	           } else {
	        	   Optional<CourseDetail> opcourse=coursedetailrepository.findByCourseIdAndInstitutionName(courseId, institution);
	        	   if(opcourse.isPresent()) {
	        		   opcourse.get();
	        			   return getVideoLessonsResponse(courseId); 
	        		   
	        	   }else {
	        		   return ResponseEntity.notFound().build();
	        	   }
	           }
	       } catch (Exception e) {
	    	   e.printStackTrace();    logger.error("", e);;
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
	               video.setQuizz(null);
                   
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
				         String institution=muserRepository.findinstitutionByEmail(email);
					    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
					   	    	if(!adminIsactive) {
					   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
					   	    	}
					    
			          if("ADMIN".equals(role)) {
			List<LessonQuizDTO> res= lessonRepo.findLessonsWithQuizByCourseId(courseId);
			return ResponseEntity.ok(res);
			    }else {
			    	if(checkAllowedOrNotForTrainer(courseId, email)) {
			    		List<LessonQuizDTO> res= lessonRepo.findLessonsWithQuizByCourseId(courseId);
						return ResponseEntity.ok(res);
			    	}
			    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			    }

			
       } catch (Exception e) {
           e.printStackTrace();    logger.error("", e);; // Print the stack trace for debugging
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error getting Lesson: " + e.getMessage());
       }
		 }
	private boolean checkAllowedOrNotForTrainer(Long courseId,String email) {
		
		boolean trainer=muserRepository.FindAllotedOrNotByUserIdAndCourseId(email, courseId);
		return trainer;
	}
private boolean checkAllowedOrNotForuser(Long courseId,String email) {
		
		boolean user=muserRepository.FindEnrolledOrNotByUserIdAndCourseId(email, courseId);
		return user;
	}
	
	
}
