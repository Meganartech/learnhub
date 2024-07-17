package com.knowledgeVista.Course.Test.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import org.springframework.web.server.ResponseStatusException;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Course.Test.CourseTest;
import com.knowledgeVista.Course.Test.Question;
import com.knowledgeVista.Course.Test.Repository.MusertestactivityRepo;
import com.knowledgeVista.Course.Test.Repository.QuestionRepository;
import com.knowledgeVista.Course.Test.Repository.TestRepository;
import com.knowledgeVista.Notification.Service.NotificationService;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;



@RestController
public class Testcontroller {
	   @Autowired
	    private CourseDetailRepository courseDetailRepo;
		@Autowired
		private MusertestactivityRepo muserActivityRepo;
		
		@Autowired
		private MuserRepositories muserRepo;
	    @Autowired
	    private QuestionRepository questionRepository;
	    
	    @Autowired
	    private TestRepository testRepository;
	    
		 @Autowired
		 private JwtUtil jwtUtil;
		 
		 @Autowired
			private NotificationService notiservice;

			@Autowired
			private MuserRepositories muserRepository;
//-----------------------------WORKING for ADMIN View-------------------------

	    public ResponseEntity<String> createTest( Long courseId, CourseTest test,String token) {
	        try {
		          if (!jwtUtil.validateToken(token)) {
		              System.out.println("Invalid Token");
		              // If the token is not valid, return unauthorized status
		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		          }

		          String role = jwtUtil.getRoleFromToken(token);
		          String email=jwtUtil.getUsernameFromToken(token);
		          String username="";
		          String institution="";
				     Optional<Muser> opuser =muserRepository.findByEmail(email);
				     if(opuser.isPresent()) {
				    	 Muser user=opuser.get();
				    	 institution=user.getInstitutionName();
				    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
				   	    	if(!adminIsactive) {
				   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				   	    	}
				    	 username=user.getUsername();
				     }else {
			             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				     }
		          if("ADMIN".equals(role)||"TRAINER".equals(role)) {
		        	 
	            // Find the course by its ID
	            CourseDetail courseDetail = courseDetailRepo.findByCourseIdAndInstitutionName(courseId, institution)
	                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
	            List<Muser> users=courseDetail.getUsers();
                List<Long> ids = new ArrayList<>(); 
                for (Muser user : users) {
                	ids.add(user.getUserId());
                }
	            if("TRAINER".equals(role)) {
	        		Optional< Muser> trainerop= muserRepo.findByEmail(email);
	        		  if(trainerop.isPresent()) {
	        			  Muser trainer =trainerop.get();
	        			  if( !trainer.getAllotedCourses().contains(courseDetail)) {

	    		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        			  }
	        		  }
	        	  }
	            Optional <CourseTest> opcoursetest= testRepository.findByCourseDetail(courseDetail);
	            if(opcoursetest.isPresent()) {
	            	return ResponseEntity.badRequest().build();
	            }
	            test.setCourseDetail(courseDetail);
	            int numberOfQuestions = test.getQuestions().size();
	            test.setNoOfQuestions((long) numberOfQuestions);
	            
	            // Save the test along with its questions
	            CourseTest savedTest = testRepository.save(test);
	            
	            // Set the test reference in each question and save them
	            List<Question> questions = test.getQuestions();
	            for (Question question : questions) {
	                question.setTest(savedTest);
	                questionRepository.save(question);
	            }
	            String heading="New Test Added !";
			       String link="/test/start/"+courseDetail.getCourseName()+"/"+courseDetail.getCourseId();
			       String notidescription= "A new test "+savedTest.getTestName() + " was added  in the " + courseDetail.getCourseName();
			      Long NotifyId =  notiservice.createNotification("LessonAdd",username,notidescription ,email,heading,link);
			        if(NotifyId!=null) {
			        	notiservice.SpecificCreateNotification(NotifyId, ids);
			        }
	            return ResponseEntity.ok("Test created successfully");
		          }else {

		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		          }
	        } catch (Exception e) {
	            e.printStackTrace(); // Print the stack trace for debugging
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Error creating test: " + e.getMessage());
	        }
	    }
	  //-----------------------------WORKING for ADMIN View-------------------------	    

	    public ResponseEntity<?> getTestsByCourseIdonly( Long courseId ,String token) {
	        try {
	        	if (!jwtUtil.validateToken(token)) {
		              System.out.println("Invalid Token");
		              // If the token is not valid, return unauthorized status
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
			        	 
	            // Find the course by its ID
	            CourseDetail courseDetail = courseDetailRepo.findByCourseIdAndInstitutionName(courseId, institution)
	                    .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with the specified ID: " + courseId));
	            if("TRAINER".equals(role)) {
	        		Optional< Muser> trainerop= muserRepo.findByEmail(email);
	        		  if(trainerop.isPresent()) {
	        			  Muser trainer =trainerop.get();
	        			  if( !trainer.getAllotedCourses().contains(courseDetail)) {

	    		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        			  }
	        		  }
	        	  }

	            Optional<CourseTest> opcoursetest = testRepository.findByCourseDetail(courseDetail);
	            if (opcoursetest.isPresent()) {
	                CourseTest test = opcoursetest.get();
	                Map<String, Object> testMap = new HashMap<>();
	                testMap.put("coursename", test.getCourseDetail().getCourseName());
	                testMap.put("testId", test.getTestId());
	                testMap.put("testName", test.getTestName());
	                testMap.put("noofattempt", test.getNoofattempt());
	                testMap.put("passPercentage", test.getPassPercentage());
	                testMap.put("noOfQuestions", test.getNoOfQuestions());
	                
	                // Retrieve questions for the current test
	                List<Question> questions = test.getQuestions();
	                if (!questions.isEmpty()) {
	                    List<Map<String, Object>> questionDetails = new ArrayList<>();
	                    for (Question question : questions) {
	                        Map<String, Object> questionMap = new HashMap<>();
	                        questionMap.put("questionId", question.getQuestionId());
	                        questionMap.put("questionText", question.getQuestionText());
	                        questionMap.put("option1", question.getOption1());
	                        questionMap.put("option2", question.getOption2());
	                        questionMap.put("option3", question.getOption3());
	                        questionMap.put("option4", question.getOption4());
	                        questionMap.put("answer", question.getAnswer());
	                        questionDetails.add(questionMap);
	                    }
	                    testMap.put("questions", questionDetails);
	                }
	                
	                return ResponseEntity.ok(testMap);
	            }else {
	            	   return ResponseEntity.notFound().build();
	            }
	            }else {

		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		          }
	         
	        } catch (Exception e) {
	            e.printStackTrace(); // Print the stack trace for debugging
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Error: " + e.getMessage());
	        }
	    }

	    
	    
	  //-----------------------------WORKING FOR USER LOGIN -------------------------

	  
	        public ResponseEntity<?> getTestByCourseId( Long courseId, String token) {
	            // Validate the JWT token
	            if (!jwtUtil.validateToken(token)) {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                        .body("Invalid Token");
	            }

	            // Retrieve role and email from token
	            String role = jwtUtil.getRoleFromToken(token);
	            String email = jwtUtil.getUsernameFromToken(token);
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

	            if ("ADMIN".equals(role)) {
      	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

	            } else if ("USER".equals(role)) {
	                // For User role
	                Optional<Muser> opUser = muserRepo.findByEmail(email);
	                if (opUser.isPresent()) {
	                    Muser user = opUser.get();
	                    Optional<CourseDetail> opCourse = courseDetailRepo.findByCourseIdAndInstitutionName(courseId, institution);
                          
	                    if (opCourse.isPresent()) {
	                        CourseDetail course = opCourse.get();
	                        if(course.getAmount()!=0) {
	                        	
	                        
	                        if(!user.getCourses().contains(course)) {

	           	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are Not Enrolled to  Access  this course");
	           	             
	                        }
	                        }
	                        
	                        Optional<CourseTest> opTest = testRepository.findByCourseDetail(course);

	                        if (opTest.isPresent()) {
	                            CourseTest test = opTest.get();
	                            long attemptCount = muserActivityRepo.countByUser(user);

	                            // Check if user exceeds allowed attempts
	                            if (attemptCount >= test.getNoofattempt()) {
	                                return ResponseEntity.badRequest().body("Attempt Limit Exceeded");
	                                
	                            }
	                            

	                            // Prepare test data for response
	                            prepareTestDataForResponse(test);
	                            return ResponseEntity.ok(test);
	                        } else {

	                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                    .body("Test not found for course");
	                        }
	                    } else {

	                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                .body("Course not found with ID: " + courseId);
	                    }

	                } else {

	                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                            .body("User not found");
	                }

	            } else {

	            	System.out.println("forbitten");
	                // Handle other roles if needed
	                return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                        .body("Access denied");
	            }
	        }
	    private void prepareTestDataForResponse(CourseTest test) {
	        // Remove relationships to avoid serialization issues
	        test.setCourseDetail(null);

	        // Shuffle questions and options
	        List<Question> questions = test.getQuestions();
	        shuffleList(questions);

	        for (Question question : questions) {
	            question.setTest(null);
	            question.setAnswer(null);
	            String[] options = {question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4()};
	            shuffleArray(options);
	            question.setOption1(options[0]);
	            question.setOption2(options[1]);
	            question.setOption3(options[2]);
	            question.setOption4(options[3]);
	        }
	    }
	
	    
	    
//-----------------------------WORKING--------------------------------------------------	    

	    public ResponseEntity<?> deleteCourseTest( Long testId,  String token) {
	        try {
	            if (!jwtUtil.validateToken(token)) {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                        .body("{\"error\": \"Invalid Token\"}");
	            }

	            // Retrieve role and email from token
	            String role = jwtUtil.getRoleFromToken(token);
	            String email = jwtUtil.getUsernameFromToken(token);
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
	            if ("ADMIN".equals(role) || "TRAINER".equals(role)) {

	                // Find the CourseTest by its ID
	                Optional<CourseTest> courseTestOptional = testRepository.findById(testId);
	                if (!courseTestOptional.isPresent()) {
	                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CourseTest with ID " + testId + " not found");
	                }
	                
	                CourseTest courseTest = courseTestOptional.get();
	                Long courseid = courseTest.getCourseDetail().getCourseId();
                   Optional<CourseDetail> opcourseDetail=courseDetailRepo.findByCourseIdAndInstitutionName(courseid, institution);
	               if(opcourseDetail.isPresent()) {
                   if ("TRAINER".equals(role)) {
	                    Optional<Muser> trainerOptional = muserRepo.findByEmail(email);
	                    if (!trainerOptional.isPresent()) {
	                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer with email " + email + " not found");
	                    }
	                    
	                    Muser trainer = trainerOptional.get();
	                    CourseDetail courseDetail=opcourseDetail.get();
	                    if (!trainer.getAllotedCourses().contains(courseDetail)) {
	                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	                    }
	                }

	                // If CourseTest exists, delete it along with its associated questions
	                questionRepository.deleteByTest(courseTest);
	                testRepository.delete(courseTest);
	                return ResponseEntity.ok().body("CourseTest with ID " + testId + " and its associated questions deleted successfully");
	               }else {
	            	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer with email " + email + " not found");
	               }
	               } else {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	            }
	        } catch (Exception e) {
	            // Log the exception (you can replace this with a logging framework like Log4j)
	            e.printStackTrace();
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"An unexpected error occurred\"}");
	        }
	    }	    
//``````````````````````Edit Test Details````````````````````````````````````

public ResponseEntity<?> editTest( Long testId, String testName, Long noOfAttempt, Double passPercentage, String token) {
    try {
    	 if (!jwtUtil.validateToken(token)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                     .body("{\"error\": \"Invalid Token\"}");
         }
    	 String email = jwtUtil.getUsernameFromToken(token);
         String institution="";
		     Optional<Muser> opuser =muserRepository.findByEmail(email);
		     if(opuser.isPresent()) {
		    	 Muser user=opuser.get();
		    	 institution=user.getInstitutionName();
		    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
		   	    	if(!adminIsactive) {
		   	    		System.out.println("first");
		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		   	    	}
		    

         // Retrieve role and email from token
         String role = jwtUtil.getRoleFromToken(token);
         if ("ADMIN".equals(role)||"TRAINER".equals(role)) {
        Optional<CourseTest> optest = testRepository.findById(testId);
        if (optest.isPresent()) {
            CourseTest test = optest.get();
            String instituionoftest=test.getCourseDetail().getInstitutionName();
          
            if(instituionoftest.equals(institution)) {
            if (testName != null) {
                test.setTestName(testName);
            }
            if (noOfAttempt != null) {
                test.setNoofattempt(noOfAttempt);
            }
            if (passPercentage != null) {
                test.setPassPercentage(passPercentage);
            }
            testRepository.saveAndFlush(test);
            return ResponseEntity.ok().body("{\"message\": \"Test updated successfully\"}");
            }else {
            	System.out.println("third");
            	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
         }else {
	    	 System.out.println("sec");
	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	     }}else {
        	System.out.println("fourt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Invalid Token\"}");
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Error updating test: " + e.getMessage() + "\"}");
    }
}

	    
//----------------------WORKING------------------------------------------
	    

	    // Shuffle a list using Fisher-Yates algorithm
	    private <T> void shuffleList(List<T> list) {
	        Random rnd = new Random();
	        for (int i = list.size() - 1; i > 0; i--) {
	            int index = rnd.nextInt(i + 1);
	            T temp = list.get(index);
	            list.set(index, list.get(i));
	            list.set(i, temp);
	        }
	    }

	    // Shuffle an array
	    private void shuffleArray(String[] array) {
	        Random rnd = new Random();
	        for (int i = array.length - 1; i > 0; i--) {
	            int index = rnd.nextInt(i + 1);
	            String temp = array[index];
	            array[index] = array[i];
	            array[i] = temp;
	        }
	    }

	    
	    
	}
