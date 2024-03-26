package com.knowledgeVista.Course.Test.controller;


import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Course.Test.CourseTest;
import com.knowledgeVista.Course.Test.MuserTestActivity;
import com.knowledgeVista.Course.Test.Question;
import com.knowledgeVista.Course.Test.Repository.MusertestactivityRepo;
import com.knowledgeVista.Course.Test.Repository.QuestionRepository;
import com.knowledgeVista.Course.Test.Repository.TestRepository;
import com.knowledgeVista.Course.certificate.certificate;
import com.knowledgeVista.Course.certificate.certificateRepo;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/test")
@CrossOrigin
public class QuestionController {
	 @Autowired
	 private JwtUtil jwtUtil;
	 @Autowired
	    private QuestionRepository questionRepository;
	 @Autowired
	    private TestRepository testrepo;
		@Autowired
		private MuserRepositories muserRepository;
		@Autowired
		private CourseDetailRepository coursedetailrepository;
		@Autowired
		private certificateRepo certificaterepo;
		@Autowired
		private MusertestactivityRepo muserActivityRepo;

		@PostMapping("{testId}/calculateMarks/{courseId}")
		public ResponseEntity<?> calculateMarks(@RequestBody List<Map<String, Object>> answers, 
		                                        @PathVariable Long testId,
		                                        @PathVariable  Long courseId,
		                                        @RequestHeader("Authorization") String token) {
		    Optional<CourseDetail> opcourse=coursedetailrepository.findById(courseId);
		    String username = jwtUtil.getUsernameFromToken(token);
		    Optional<Muser> opuser = muserRepository.findByEmail(username);
		    Optional<CourseTest> optest = testrepo.findById(testId);
		    
		    if (opuser.isPresent() && optest.isPresent() && opcourse.isPresent()) {
		    	CourseDetail course=opcourse.get();
		        Muser user = opuser.get();
		        CourseTest test = optest.get();
		        MuserTestActivity activity = new MuserTestActivity();
		        activity.setCourse(course);
		        activity.setUser(user);
		        activity.setTest(test);
		        activity.setTestDate(LocalDateTime.now().toLocalDate());

		        double passpercentage = test.getPassPercentage();
		        Long noofQuestion = test.getNoOfQuestions();
		        
		        int totalMarks = 0;
		        for (Map<String, Object> answer : answers) {
		            long questionId = Long.parseLong(answer.get("questionId").toString());
		            String selectedAnswer = answer.get("selectedAnswer").toString();

		            Question question = questionRepository.findById(questionId)
		                    .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionId));

		            if (question.getAnswer().equals(selectedAnswer)) {
		                totalMarks++;
		            }
		        }
		        
		        Double markacquired = ((double) totalMarks / noofQuestion) * 100;
		        activity.setPercentage(markacquired);
		        muserActivityRepo.save(activity);
		        
		        String message;
		        String result;
		        if (markacquired >= passpercentage) {
		            message = "Congratulations! You passed the exam with " + Math.round(markacquired) + "%";
		            result = "pass";
		        } else {
		            message = "You have got " + Math.round(markacquired) + "%";
		            result = "fail";
		        }
		        
		        Map<String, String> response = new HashMap<>();
		        response.put("message", message);
		        response.put("result", result);
		        return ResponseEntity.ok(response);
		    } else {
		        // Handle the case when either user or test is not present
		        return ResponseEntity.notFound().build();
		    }
		}
	    
	 
	 
	 
	    @DeleteMapping("/questions/{questionId}")
	    public String deleteQuestion(@PathVariable Long questionId) {
	        // Find the question by its ID
	        Question question = questionRepository.findById(questionId).orElse(null);
	        
	        // If question exists, delete it
	        if (question != null) {
	            questionRepository.delete(question);
	            return "Question with ID " + questionId + " deleted successfully";
	        } else {
	            return "Question with ID " + questionId + " not found";
	        }
	    }
	    //--------------------WORKING--------
	    @PutMapping("/edit/{questionId}")
	    public ResponseEntity<Question> updateQuestion(
	            @PathVariable Long questionId,
	            @RequestParam String questionText,
	            @RequestParam String option1,
	            @RequestParam String option2,
	            @RequestParam String option3,
	            @RequestParam String option4,
	            @RequestParam String answer

	    ) {
	        Question existingQuestion = questionRepository.findById(questionId)
	                .orElse(null);
	     
	        if (existingQuestion == null) {
	            return ResponseEntity.notFound().build();
	        }
	        existingQuestion.setQuestionText(questionText);
	        existingQuestion.setOption1(option1);
	        existingQuestion.setOption2(option2);
	        existingQuestion.setOption3(option3);
	        existingQuestion.setOption4(option4);
	        existingQuestion.setAnswer(answer);
	        Question savedQuestion = questionRepository.save(existingQuestion);

	        // Return the updated Question object in the response
	        return ResponseEntity.ok(savedQuestion);
	    }
}
