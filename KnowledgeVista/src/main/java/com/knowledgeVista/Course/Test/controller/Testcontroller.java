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
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;



@RestController
@RequestMapping("/test")
@CrossOrigin
public class Testcontroller {
	   @Autowired
	    private CourseDetailRepository courseDetailRepository;
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
//-----------------------------WORKING for ADMIN View-------------------------
	    @PostMapping("/create/{courseId}")
	    public ResponseEntity<String> createTest(@PathVariable Long courseId, @RequestBody CourseTest test) {
	        try {
	            // Find the course by its ID
	            CourseDetail courseDetail = courseDetailRepository.findById(courseId)
	                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
	            
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

	            return ResponseEntity.ok("Test created successfully");
	        } catch (Exception e) {
	            e.printStackTrace(); // Print the stack trace for debugging
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Error creating test: " + e.getMessage());
	        }
	    }
	  //-----------------------------WORKING for ADMIN View-------------------------	    
	    @GetMapping("/getall/{courseId}")
	    public ResponseEntity<?> getTestsByCourseId(@PathVariable Long courseId) {
	        try {
	            // Find the course by its ID
	            CourseDetail courseDetail = courseDetailRepository.findById(courseId)
	                    .orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with the specified ID: " + courseId));


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
	            }
	            // Return a response for the case where opcoursetest.isPresent() is false
	            return ResponseEntity.notFound().build();
	        } catch (Exception e) {
	            e.printStackTrace(); // Print the stack trace for debugging
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body("Error: " + e.getMessage());
	        }
	    }

	    
	    
	  //-----------------------------WORKING FOR USER LOGIN -------------------------

	  

	        @GetMapping("/getTestByCourseId/{courseId}")
	        public ResponseEntity<?> getTestByCourseId(@PathVariable Long courseId,
	                                                   @RequestHeader("Authorization") String token) {
	            // Validate the JWT token
	            if (!jwtUtil.validateToken(token)) {
	            	System.out.println("unauthorized");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                        .body("{\"error\": \"Invalid Token\"}");
	            }

	            // Retrieve role and email from token
	            String role = jwtUtil.getRoleFromToken(token);
	            String email = jwtUtil.getUsernameFromToken(token);

	            if ("ADMIN".equals(role)) {
      	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

	            } else if ("USER".equals(role)) {
	                // For User role
	                Optional<Muser> opUser = muserRepo.findByEmail(email);
	                if (opUser.isPresent()) {
	                    Muser user = opUser.get();
	                    Optional<CourseDetail> opCourse = courseDetailRepository.findById(courseId);
                          
	                    if (opCourse.isPresent()) {
	                        CourseDetail course = opCourse.get();
	                        if(!user.getCourses().contains(course)) {

	        	            	System.out.println("unauthorized");
	           	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	           	             
	                        }
	                        Optional<CourseTest> opTest = testRepository.findByCourseDetail(course);

	                        if (opTest.isPresent()) {
	                            CourseTest test = opTest.get();
	                            long attemptCount = muserActivityRepo.countByUser(user);

	                            // Check if user exceeds allowed attempts
	                            if (attemptCount >= test.getNoofattempt()) {

	            	            	System.out.println("limit exceeded");
	                                return ResponseEntity.badRequest().body("{\"error\": \"Attempt Limit Exceeded\"}");
	                                
	                            }
	                            

	                            // Prepare test data for response
	                            prepareTestDataForResponse(test);
	                            return ResponseEntity.ok(test);
	                        } else {

	        	            	System.out.println("notfound");
	                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                    .body("{\"error\": \"Test not found for course\"}");
	                        }
	                    } else {

	    	            	System.out.println("notfound");
	                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                .body("{\"error\": \"Course not found with ID: " + courseId + "\"}");
	                    }

	                } else {

		            	System.out.println("notfound");
	                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                            .body("{\"error\": \"User not found\"}");
	                }

	            } else {

	            	System.out.println("forbitten");
	                // Handle other roles if needed
	                return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                        .body("{\"error\": \"Access denied\"}");
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
	    @DeleteMapping("/{testId}")
	    public String deleteCourseTest(@PathVariable Long testId) {
	        // Find the CourseTest by its ID
	        CourseTest courseTest = testRepository.findById(testId).orElse(null);
	        
	        // If CourseTest exists, delete it along with its associated questions
	        if (courseTest != null) {
	            // Delete associated questions first
	            questionRepository.deleteByTest(courseTest);
	            // Then delete the CourseTest itself
	            testRepository.delete(courseTest);
	            return "CourseTest with ID " + testId + " and its associated questions deleted successfully";
	        } else {
	            return "CourseTest with ID " + testId + " not found";
	        }
	    }
	    
//``````````````````````Edit Test Details````````````````````````````````````
	    @PatchMapping("/update/{testId}")
	    public ResponseEntity<?> editTest(@PathVariable Long testId,
	            @RequestParam(value="testName", required=false) String testName,
	            @RequestParam(value="noofattempt", required=false) Long noOfAttempt,
	            @RequestParam(value="passPercentage", required=false) Double passPercentage) {
	        Optional<CourseTest> optest = testRepository.findById(testId);
	        if (optest.isPresent()) {
	            CourseTest test = optest.get();
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
	            return ResponseEntity.ok("Test updated successfully");
	        } else {
	            return ResponseEntity.notFound().build();
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
