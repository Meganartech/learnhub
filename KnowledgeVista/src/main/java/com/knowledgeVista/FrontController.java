package com.knowledgeVista;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Controller.CheckAccess;
import com.knowledgeVista.Course.Controller.CourseController;
import com.knowledgeVista.Course.Controller.CourseControllerSecond;
import com.knowledgeVista.Course.Controller.videolessonController;
import com.knowledgeVista.Course.Test.CourseTest;
import com.knowledgeVista.Course.Test.controller.QuestionController;
import com.knowledgeVista.Course.Test.controller.Testcontroller;
import com.knowledgeVista.Course.certificate.certificateController;
import com.knowledgeVista.License.License;
import com.knowledgeVista.License.LicenseController;
import com.knowledgeVista.License.UserListWithStatus;
import com.knowledgeVista.Notification.Controller.NotificationController;
import com.knowledgeVista.Payments.PaymentIntegration;
import com.knowledgeVista.Payments.PaymentListController;
import com.knowledgeVista.Payments.Paymentsettings;
import com.knowledgeVista.Payments.SettingsController;
import com.knowledgeVista.Settings.Feedback;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Controller.AddUsers;
import com.knowledgeVista.User.Controller.AssignCourse;
import com.knowledgeVista.User.Controller.AuthenticationController;
import com.knowledgeVista.User.Controller.Edituser;
import com.knowledgeVista.User.Controller.Listview;
import com.knowledgeVista.User.Controller.MserRegistrationController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@CrossOrigin
public class FrontController {
	  @Value("${spring.profiles.active}")
	    private String activeProfile;
	  
	@Autowired
    private CourseController courseController;
	
	@Autowired
	private CourseControllerSecond coursesec;
	
	@Autowired 
	private videolessonController videoless;
	
	@Autowired
	private CheckAccess check;
	
	@Autowired 
	private QuestionController Question;
	
	@Autowired
	private Testcontroller testcontroller;
	
	@Autowired
	private PaymentIntegration payment;
	
	@Autowired(required = false)
	private PaymentListController paylist;
	
	@Autowired
	private LicenseController licence;
	
	@Autowired
	private SettingsController settings;
	
	@Autowired
	private AddUsers adduser;
	
	@Autowired
	private AssignCourse assign;
	
	@Autowired 
	private AuthenticationController authcontrol;
	
	@Autowired
	private Edituser edit;
	
	@Autowired
	private Listview listview;
	
	@Autowired
	private MserRegistrationController muserreg;
	
	@Autowired
	private certificateController  certi;
	
	@Autowired
	private NotificationController noticontroller;

//----------------------------COURSECONTROLLER----------------------------
    
	 @GetMapping("/course/countcourse")
	 public ResponseEntity<?> countCoursefront(@RequestHeader("Authorization") String token) {
		 return courseController.countCourse(token);
	 }

	 @PostMapping("/course/add")
	    public ResponseEntity<?> addCourse(@RequestParam("courseImage") MultipartFile file, 
	    		@RequestParam("courseName") String courseName,
	    		@RequestParam("courseDescription") String description,
	    		@RequestParam("courseCategory") String category,
	    		@RequestParam("Duration") Long Duration,
	    		@RequestParam("Noofseats") Long Noofseats,
	    		@RequestParam("courseAmount") Long amount,
	    		@RequestParam("paytype")String paytype,
                @RequestParam(value="InstallmentDetails", required=false) String installmentDataJson,
	    		@RequestHeader("Authorization") String token) {
		 return courseController.addCourse(file, courseName, description, category, Duration, Noofseats, amount,paytype,installmentDataJson, token);
	 }
	 
	 
	 @PostMapping("/course/create/trainer")
	    public ResponseEntity<?> addCourseByTrainer(@RequestParam("courseImage") MultipartFile file, 
	    		@RequestParam("courseName") String courseName,
	    		@RequestParam("courseDescription") String description,
	    		@RequestParam("courseCategory") String category,
	    		@RequestParam("Duration") Long Duration,
	    		@RequestParam("Noofseats") Long Noofseats,
	    		@RequestParam("courseAmount") Long amount,
             @RequestHeader("Authorization") String token) {
		 
		 return courseController.addCourseByTrainer(file, courseName, description, category, Duration, Noofseats, amount, token);
	 }
	 @Transactional
	 @PatchMapping("/course/edit/{courseId}")
	 public ResponseEntity<?> updateCourse(
	     @PathVariable Long courseId,
	     @RequestParam(value = "courseImage", required = false) MultipartFile file,
	     @RequestParam(value = "courseName", required = false) String courseName,
	     @RequestParam(value = "courseDescription", required = false) String description,
	     @RequestParam(value = "courseCategory", required = false) String category,
	     @RequestParam(value ="Noofseats",required = false) Long Noofseats,
 		@RequestParam(value ="Duration",required = false)Long Duration,
	      @RequestParam(value="courseAmount",required=false) Long amount,
	      @RequestHeader("Authorization") String token){
		 
		 return courseController.updateCourse(token,courseId, file, courseName, description, category, Noofseats, Duration, amount);
	 
	 }
	 
	 

	 @GetMapping("/course/get/{courseId}")
	 public ResponseEntity<CourseDetail> getCourse(@PathVariable Long courseId, @RequestHeader("Authorization") String token) {
		 return courseController.getCourse(courseId,token);
	 }
	 
	 @GetMapping("/course/viewAll")
	    public ResponseEntity<List<CourseDetail>> viewCourse(@RequestHeader("Authorization") String token) {
		 return courseController.viewCourse(token);
		 }
	 

	   @GetMapping("/course/assignList")
	   public ResponseEntity<?> getAllCourseInfo(
		          @RequestHeader("Authorization") String token,
		          @RequestParam("email") String email) {
		   return courseController.getAllCourseInfo(token, email);
	   }
	 
	   

	   @GetMapping("/course/allotList")
	   public ResponseEntity<?> getAllAllotelistInfo(
		          @RequestHeader("Authorization") String token,
		          @RequestParam("email") String email) {
		   return courseController.getAllAllotelistInfo(token, email);
	   }
	 


	   @DeleteMapping("/course/{courseId}")
	   public ResponseEntity<String> deleteCourse(@PathVariable Long courseId,@RequestHeader("Authorization") String token) {
		   return courseController.deleteCourse(courseId,token);
	   }
	   
	   @GetMapping("/course/getLessondetail/{courseId}")
	   public ResponseEntity<?> getLessons(@PathVariable Long courseId,
	                                        @RequestHeader("Authorization") String token) {
		   return courseController.getLessons(courseId, token);
	   }

		 @GetMapping("/course/getLessonlist/{courseId}")
		 public ResponseEntity<?> getLessonList(@PathVariable Long courseId,@RequestHeader("Authorization") String token) {
			 return courseController.getLessonList(courseId,token);
		 }
//----------------------------COURSE CONTROLLER SECOND-----------------------------------

		 @GetMapping("/courseControl/popularCourse")
		 public ResponseEntity<List<CourseDetail>> popular(@RequestHeader("Authorization") String token) {
			 return coursesec.popular(token);
		 }
		 
//----------------------------videolessonController-------------------------------
		 @PostMapping("/lessons/save/{courseId}")
		  public ResponseEntity<?> savenote(
				  									@RequestParam("thumbnail") MultipartFile file,
				  								  @RequestParam("Lessontitle") String Lessontitle,
		                                          @RequestParam("LessonDescription") String LessonDescription,
		                                          @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
		                                          @RequestParam(value = "fileUrl", required = false) String fileUrl,
		                                          @PathVariable Long courseId,
		                                          @RequestHeader("Authorization") String token
		                                          ) {
			 return videoless.savenote(file, Lessontitle, LessonDescription, videoFile, fileUrl, courseId, token);
		 }
		 
		 @PatchMapping("/lessons/edit/{lessonId}")
		 public ResponseEntity<?> EditLessons(@PathVariable Long lessonId,
		     @RequestParam(value="thumbnail" , required = false) MultipartFile file,
		     @RequestParam(value="Lessontitle" , required = false) String Lessontitle,
		     @RequestParam(value="LessonDescription" , required = false) String LessonDescription,
		     @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
		     @RequestParam(value = "fileUrl", required = false) String fileUrl,
		     @RequestHeader("Authorization") String token) {
			 return videoless.EditLessons(lessonId, file, Lessontitle, LessonDescription, videoFile, fileUrl, token);
		 }
		 
		 @GetMapping("/lessons/getvideoByid/{lessId}/{courseId}/{token}")
		    public ResponseEntity<?> getVideoFile(@PathVariable Long lessId,
		                                          @PathVariable Long courseId,
		                                          @PathVariable String token,
		                                          HttpServletRequest request) {
			
			return videoless.getVideoFile(lessId, courseId, token, request);
		 }
		 
		 @GetMapping("/lessons/getLessonsByid/{lessonId}")
		 public ResponseEntity<?>getlessonfromId(@PathVariable("lessonId")Long lessonId,
				@RequestHeader("Authorization") String token){
			 return videoless.getlessonfromId(lessonId, token);
		 }
		 
			@DeleteMapping("/lessons/delete")
			public ResponseEntity<?> deleteLessonsByLessonId(@RequestParam("lessonId")Long lessonId,
					   @RequestParam("Lessontitle") String Lessontitle,
			          @RequestHeader("Authorization") String token){
				return videoless.deleteLessonsByLessonId(lessonId, Lessontitle, token);
			}
 //-------------------------CheckAccess -------------------------------------
			 @PostMapping("/CheckAccess/match")
			 public ResponseEntity<?> checkAccess(@RequestBody Map<String, Long> requestData, @RequestHeader("Authorization") String token) {
				
				 return check.checkAccess(requestData, token);
			 }
//---------------------------QuestionController-----------------------

				@PostMapping("/test/calculateMarks/{courseId}")
				public ResponseEntity<?> calculateMarks(@RequestBody List<Map<String, Object>> answers,
				                                        @PathVariable Long courseId,
				                                        @RequestHeader("Authorization") String token) {
					return Question.calculateMarks(answers, courseId, token);
				}
				
				
				   @GetMapping("/test/getQuestion/{questionId}")
					public ResponseEntity<?> getQuestion(@PathVariable Long questionId,
			                @RequestHeader("Authorization") String token) {
					   return Question.getQuestion(questionId, token);
				   }
				   

				@DeleteMapping("/test/questions/{questionId}")
					public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId,
					                                        @RequestHeader("Authorization") String token) {
					return Question.deleteQuestion(questionId, token);
				}
				
				@PatchMapping("/test/edit/{questionId}")
				public ResponseEntity<?> updateQuestion( @PathVariable Long questionId,
				        @RequestParam String questionText, @RequestParam String option1,@RequestParam String option2,
				        @RequestParam String option3, @RequestParam String option4,
				        @RequestParam String answer, @RequestHeader("Authorization") String token) {
					return Question.updateQuestion(questionId, questionText, option1, option2, option3, option4, answer, token);
				}
				@PostMapping("/test/add/{testId}")
				public ResponseEntity<?> Addmore(
				        @PathVariable Long testId, @RequestParam String questionText,
				        @RequestParam String option1, @RequestParam String option2,
				        @RequestParam String option3, @RequestParam String option4,
				        @RequestParam String answer, @RequestHeader("Authorization") String token) {
					return Question.Addmore(testId, questionText, option1, option2, option3, option4, answer, token);
				}
				
//--------------------------------Test Controller-------------------------
				@PostMapping("/test/create/{courseId}")
			    public ResponseEntity<String> createTest(@PathVariable Long courseId, @RequestBody CourseTest test,
			    		@RequestHeader("Authorization") String token) {
					return testcontroller.createTest(courseId, test ,token);
				}
				
				@GetMapping("/test/getall/{courseId}")
			    public ResponseEntity<?> getTestsByCourseIdonly(@PathVariable Long courseId,
			    		@RequestHeader("Authorization") String token) {
					return testcontroller.getTestsByCourseIdonly(courseId,token);
				}
		        @GetMapping("/test/getTestByCourseId/{courseId}")
		        public ResponseEntity<?> getTestByCourseId(@PathVariable Long courseId,
		                                                   @RequestHeader("Authorization") String token) {
		        	return testcontroller.getTestByCourseId(courseId, token);
		        }
		        
		        @DeleteMapping("/test/{testId}")
			    public ResponseEntity<?> deleteCourseTest(@PathVariable Long testId,
			    		@RequestHeader("Authorization") String token) {
		        	return testcontroller.deleteCourseTest(testId,token);
		        }
		        
		        @PatchMapping("/test/update/{testId}")
		        public ResponseEntity<?> editTest(@PathVariable Long testId,
		                @RequestParam(value="testName", required=false) String testName,
		                @RequestParam(value="noofattempt", required=false) Long noOfAttempt,
		                @RequestParam(value="passPercentage", required=false) Double passPercentage,
		                @RequestHeader("Authorization") String token) {
		        	return testcontroller.editTest(testId, testName, noOfAttempt, passPercentage, token);
		        }
		        
//----------------------PaymentIntegration----------------------	
		        @PostMapping("/full/buyCourse/create")
		        public ResponseEntity<?> createOrderfull(@RequestBody Map<String, Long> requestData ,@RequestHeader("Authorization") String token) {
		        	 if (paylist != null && activeProfile.equals("demo")) {

		          		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
	            	   }else {
		        	return payment.createOrderfull(requestData,token);
	            	   }
		        } 
		        @PostMapping("/part/buyCourse/create")
		        public ResponseEntity<?> createOrderPart(@RequestBody Map<String, Long> requestData ,@RequestHeader("Authorization") String token) {
		        	 if (paylist != null && activeProfile.equals("demo")) {

		          		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
	            	   }else {
		        	return payment.createOrderPart(requestData,token);
	            	   }
		        }
               @PostMapping("/buyCourse/payment")
             public ResponseEntity<String> updatePaymentId(@RequestBody Map<String, String> requestData,@RequestHeader("Authorization") String token) {
            	   if (paylist != null && activeProfile.equals("demo")) {

              		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
            	   }else {
            	   return payment.updatePaymentId(requestData ,token);
            	   }
               }
               
               
               
//-------------------------paymentListcontrller-------------
               @GetMapping("/myPaymentHistory")
               public ResponseEntity<?>ViewMypaymentHistry(@RequestHeader("Authorization") String token){
            	   if (paylist != null && activeProfile.equals("demo")) {

              		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
            	   }else {
            		   return paylist.ViewMypaymentHistry(token);
            		      
            	   }
               }
               
               @GetMapping("/viewPaymentList/{courseId}")
               public ResponseEntity<?> ViewPaymentdetails(@RequestHeader("Authorization") String token,@PathVariable Long courseId){
            	   if (paylist != null && activeProfile.equals("demo")) {

              		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
            	
               }else {
            	   return paylist.ViewPaymentdetails(token, courseId);    
        	   }
               }
               
               @GetMapping("/viewAllTransactionHistory")
               public ResponseEntity<?>viewTransactionHistory(@RequestHeader("Authorization") String token){
            	   if (paylist != null && activeProfile.equals("demo")) {

              		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
            	   }
            	   else {
            		   return paylist.viewTransactionHistory(token); 
            	   }
               }
            	   
           @GetMapping("/viewAllTransactionHistoryForTrainer")
           public ResponseEntity<?>ViewMypaymentHistrytrainer(@RequestHeader("Authorization") String token){
        	   if (paylist != null && activeProfile.equals("demo")) {

          		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
        	 
        	   }
        	   else {
        		   return paylist.ViewMypaymentHistrytrainer(token); 
        	   }
           }
               
//-------------------------LicenseController-----------------------
               @GetMapping("/api/v2/GetAllUser")
       		public ResponseEntity<?> getAllUser(  @RequestHeader("Authorization") String token) {
            	   return licence.getAllUser(token);
               }
           	@GetMapping("/api/v2/count")
    		public ResponseEntity<Integer> count(@RequestHeader("Authorization") String token) {
           		return licence.count(token);
           	}
           		
           		@PostMapping("/api/v2/uploadfile")
    		    public ResponseEntity<License> upload(@RequestParam("audioFile") MultipartFile File,@RequestParam("lastModifiedDate") String lastModifiedDate,@RequestHeader("Authorization") String token){
           			return licence.upload(File,lastModifiedDate,token);
           		}
           	
 //------------------------SettingsController------------------------
           		@PostMapping("/api/Paymentsettings")
           		public ResponseEntity<?> SavePaymentDetails(@RequestBody Paymentsettings data,
           		          @RequestHeader("Authorization") String token) {
           		 if (paylist != null && activeProfile.equals("demo")) {

          		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
          	   }else {
           			return settings.SavePaymentDetails(data, token);
          	   }
           		}
           		@GetMapping("/api/getPaymentDetails")
           		public ResponseEntity<?> GetPaymentDetails (
           		          @RequestHeader("Authorization") String token){
           		 if (paylist != null && activeProfile.equals("demo")) {

            		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
          	   }else {
           			return settings.GetPaymentDetails(token);
          	   }
           		}
           		@PatchMapping("/api/update/{payid}")
           		public ResponseEntity<?> editpayment(@PathVariable Long payid,
           	            @RequestParam(value="razorpay_key", required=false) String razorpay_key,
           	            @RequestParam(value="razorpay_secret_key", required=false) String razorpay_secret_key,
           		          @RequestHeader("Authorization") String token){
           		 if (paylist != null && activeProfile.equals("demo")) {

            		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment functionality disabled");
          	   }else {
           			return settings.editpayment(payid, razorpay_key, razorpay_secret_key, token);
          	   }
           		}

           		@PostMapping("/api/feedback")
           		public Feedback feedback(@RequestBody Feedback data) {
           			
           			return settings.feedback(data);
           		}
//--------------------AddUser---------------------------
           	 @PostMapping("/admin/addTrainer") 
       	  public ResponseEntity<?> addTrainer(
       	          @RequestParam("username") String username,
       	          @RequestParam("psw") String psw,
       	          @RequestParam("email") String email,
       	          @RequestParam("dob") LocalDate dob,
       	          @RequestParam("phone") String phone,
       	          @RequestParam("skills") String skills,
       	          @RequestParam("profile") MultipartFile profile,
       	          @RequestParam("isActive") Boolean isActive,
                  @RequestParam("countryCode")String countryCode,
       	          @RequestHeader("Authorization") String token) {
           		 return adduser.addTrainer(username, psw, email, dob, phone, skills, profile, isActive,countryCode, token);
           	 }
           	 
           	@PostMapping("/admin/addStudent") 
      	  public ResponseEntity<?> addStudent(
      	          @RequestParam("username") String username,
      	          @RequestParam("psw") String psw,
      	          @RequestParam("email") String email,
      	          @RequestParam("dob") LocalDate dob,
      	          @RequestParam("phone") String phone,
      	          @RequestParam("skills") String skills,
      	          @RequestParam("profile") MultipartFile profile,
      	          @RequestParam("isActive") Boolean isActive,
                  @RequestParam("countryCode")String countryCode,
      	          @RequestHeader("Authorization") String token) {
           		return adduser.addStudent(username, psw, email, dob, phone, skills, profile, isActive,countryCode ,token);
           	}
           	

      	  @DeleteMapping("/admin/deactivate/trainer")
      	  public ResponseEntity<?> DeactivateTrainer(
      	          @RequestParam("email") String email,
      	          @RequestHeader("Authorization") String token) {
      		  return adduser.DeactivateTrainer(email, token);
      	  }
      	  
      	  @DeleteMapping("/admin/Activate/trainer")
      	  public ResponseEntity<?> activateTrainer(
      	          @RequestParam("email") String email,
      	          @RequestHeader("Authorization") String token) {
      		  return adduser.activateTrainer(email, token);
      	  }
      	  

    	  @DeleteMapping("/admin/deactivate/Student")	  
    	  public ResponseEntity<?> DeactivateStudent(
    	          @RequestParam("email") String email,
    	          @RequestHeader("Authorization") String token) {
    		  return adduser.DeactivateStudent(email, token);
    	  }
    	  
    	  @DeleteMapping("/admin/Activate/Student")	  
    	  public ResponseEntity<?> activateStudent(
    	          @RequestParam("email") String email,
    	          @RequestHeader("Authorization") String token) {
    		  return adduser.activateStudent(email, token);
    	  }
    	  
 //----------------------Assign course---------------------
    		@PostMapping("/AssignCourse/{userId}/courses")
    		public ResponseEntity<String> assignCoursesToUser(@PathVariable Long userId, @RequestBody Map<String, List<Long>> data, 
    				@RequestHeader("Authorization") String token) {
    			return assign.assignCoursesToUser(userId, data, token);
    		}
    		
    		@PostMapping("/AssignCourse/trainer/{userId}/courses")
    		public ResponseEntity<String> assignCoursesToTrainer(@PathVariable Long userId, @RequestBody Map<String, List<Long>> data,
    				 @RequestHeader("Authorization") String token) {
    			return assign.assignCoursesToTrainer(userId, data, token);
    		}
    		
    		@GetMapping("/AssignCourse/student/courselist")
    		public ResponseEntity<List<CourseDetail>> getCoursesForUser(
    		          @RequestHeader("Authorization") String token) {
    			return assign.getCoursesForUser(token);
    		}
    		
    		@GetMapping("/AssignCourse/Trainer/courselist")
    		public ResponseEntity<List<CourseDetail>> getCoursesForTrainer(
    		          @RequestHeader("Authorization") String token) {
    			return assign.getCoursesForTrainer(token);
    		}
           		
 //--------------------------Authentication Controller------------------
           
    		@PostMapping("/refreshtoken")
    		public ResponseEntity<?> Refresh(@RequestHeader("Authorization")String token){
    			return authcontrol.refreshtoken(token);
    		}
    	    @PostMapping("/logout")
    	    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
    	    	return authcontrol.logout(token);
    	    }

    	    @Transactional
    	    @PostMapping("/login")
    	    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
    	    	return authcontrol.login(loginRequest);
    	    }
    	    
    	   
    	    @Transactional
    	    @PostMapping("/forgetpassword")
    	    public ResponseEntity<?> forgetPassword(@RequestParam("email") String email) {
    	    	return authcontrol.forgetPassword(email);
    	    }

    	    @Transactional
    	    @PostMapping("/resetpassword")
    	    public ResponseEntity<?> resetPassword(@RequestParam("email") String email, @RequestParam("password") String newPassword) {
    	    	return authcontrol.resetPassword(email, newPassword);
    	    }
    	    
//---------------------------EDIT USER------------------------------------
    	    @PatchMapping("/Edit/Student/{email}")
    		 public ResponseEntity<?> updateStudent(
    		     @PathVariable("email") String originalEmail,
    		     @RequestParam("username") String username,
    		     @RequestParam("email") String newEmail,
    		     @RequestParam("dob") LocalDate dob,
    		     @RequestParam("phone") String phone,
    		     @RequestParam("skills") String skills,
    		     @RequestParam(value="profile", required=false) MultipartFile profile,
    		     @RequestParam("isActive") Boolean isActive,
                 @RequestParam("countryCode")String countryCode,
    		     @RequestHeader("Authorization") String token
    		 ) {
    	    	return edit.updateStudent(originalEmail, username, newEmail, dob, phone, skills, profile, isActive ,countryCode,token);
    	    }
    	    
    	    
    		 @PatchMapping("/Edit/Trainer/{email}")
    		 public ResponseEntity<?> updateTrainer(
    		     @PathVariable("email") String originalEmail,
    		     @RequestParam("username") String username,
    		     @RequestParam("email") String newEmail,
    		     @RequestParam("dob") LocalDate dob,
    		     @RequestParam("phone") String phone,
    		     @RequestParam("skills") String skills,
    		     @RequestParam(value="profile", required=false) MultipartFile profile,
    		     @RequestParam("isActive") Boolean isActive,
                 @RequestParam("countryCode")String countryCode,
    		     @RequestHeader("Authorization") String token
    		 ) {
    			 return edit.updateTrainer(originalEmail, username, newEmail, dob, phone, skills, profile, isActive,countryCode, token);
    		 }
    		 
    		 @PatchMapping("/Edit/self")
    		 public ResponseEntity<?> EditProfile(
    		     @RequestParam("username") String username,
    		     @RequestParam("email") String newEmail,
    		     @RequestParam("dob") LocalDate dob,
    		     @RequestParam("phone") String phone,
    		     @RequestParam("skills") String skills,
    		     @RequestParam(value="profile", required=false) MultipartFile profile,
    		     @RequestParam("isActive") Boolean isActive,
    		     @RequestHeader("Authorization") String token,
                 @RequestParam("countryCode")String countryCode
    		 ) {
    			 return edit.EditProfile(username, newEmail, dob, phone, skills, profile, isActive,countryCode, token);
    		 }
    		 @GetMapping("/Edit/profiledetails")
    		 public ResponseEntity<?> NameandProfile(
    		     @RequestHeader("Authorization") String token) {
    			 return edit.NameandProfile(token);
    			 
    		 }
 //----------------------------ListView------------------------

    		    @GetMapping("/view/users")
    		    public ResponseEntity<List<Muser>> getUsersByRoleName(@RequestHeader("Authorization") String token) {
    		    	return listview.getUsersByRoleName(token);
    		    }
    		    @GetMapping("/view/users/{userId}")
    		    public ResponseEntity<?> getUserById(@PathVariable Long userId,@RequestHeader("Authorization") String token) {
    		    	return listview.getUserById(userId,token);
    		    }
    		    @GetMapping("/view/Trainer")
    		    public ResponseEntity<List<Muser>> getTrainerByRoleName(@RequestHeader("Authorization") String token) {
    		    	return listview.getTrainerByRoleName(token);
    		    }
    		    
    		    @GetMapping("/view/Mystudent")
    		    public ResponseEntity<List<Muser>> GetStudentsOfTrainer(@RequestHeader("Authorization") String token){
    		    	return listview.GetStudentsOfTrainer(token);
    		    }
//------------------------MuserRegistrationController------------------------------

    			@PostMapping("/admin/register")
    			public ResponseEntity<?> registerAdmin(@RequestParam("username") String username,
    			                                          @RequestParam("psw") String psw,
    			                                          @RequestParam("email") String email,
    			                                          @RequestParam("institutionname") String institutionName,
    			                                          @RequestParam("dob") LocalDate dob,
    			                                          @RequestParam("role")String role,
    			                                          @RequestParam("phone") String phone,
    			                                          @RequestParam("skills") String skills,
    			                                          @RequestParam("profile") MultipartFile profile,
    			                                          @RequestParam("isActive") Boolean isActive,
    			                                          @RequestParam("countryCode")String countryCode) {
    				return muserreg.registerAdmin(username, psw, email, institutionName, dob,role, phone, skills, profile, isActive,countryCode);
    			}

    			@GetMapping("/student/users/{email}")
    			public ResponseEntity<Muser> getUserByEmail(@PathVariable String email,
    					@RequestHeader("Authorization") String token) {
    				return muserreg.getUserByEmail(email,token);
    			}
    			

    		    @GetMapping("/student/admin/getTrainer/{email}")
    		    public ResponseEntity<?> getTrainerDetailsByEmail(@PathVariable String email,
    		                            @RequestHeader("Authorization") String token) {
    		    	return muserreg.getTrainerDetailsByEmail(email, token);
    		    }
    		    

    		    @GetMapping("/student/admin/getstudent/{email}")
    		    public ResponseEntity<?> getStudentDetailsByEmail(@PathVariable String email,
    		    		@RequestHeader("Authorization") String token) {
    		    	return muserreg.getStudentDetailsByEmail(email, token);
    		    }
    		   
    		    
    			
    			  
 //--------------------------certificate Contoller----------------------
    			  
    			  @PostMapping("/certificate/add")
    				public ResponseEntity<?> addcertificate( @RequestParam("institutionName") String institutionName,
    				        @RequestParam("ownerName") String ownerName,
    				        @RequestParam("qualification") String qualification,
    				        @RequestParam("address") String address,
    				        @RequestParam("authorizedSign") MultipartFile authorizedSign,
    				        @RequestHeader("Authorization") String token
    				       ) {
    				  return certi.addcertificate(institutionName, ownerName, qualification, address, authorizedSign, token);
    			  }
    			  @PatchMapping("/certificate/Edit")
    				public ResponseEntity<String> editcertificate(
    				    @RequestParam("institutionName") String institutionName,
    				    @RequestParam("ownerName") String ownerName,
    				    @RequestParam("qualification") String qualification,
    				    @RequestParam("address") String address,
    				    @RequestParam(value="authorizedSign", required=false) MultipartFile authorizedSign,
    				    @RequestParam("certificateId") Long certificateId,
    				    @RequestHeader("Authorization") String token
    				) {
    				  return certi.editcertificate(institutionName, ownerName, qualification, address, authorizedSign, certificateId, token);
    			  }
    			  
    				@GetMapping("/certificate/viewAll")
    				public ResponseEntity<?> viewCoursecertificate(@RequestHeader("Authorization") String token) {
    					return certi.viewCoursecertificate(token);
    				}
    				@GetMapping("/certificate/getAllCertificate")
    				public ResponseEntity<?> sendAllCertificate(@RequestHeader("Authorization") String token) {
    					return certi.sendAllCertificate(token);
    				}	
    					

               @GetMapping("/certificate/getByActivityId/{activityId}")
              public ResponseEntity<?> getByActivityId(@PathVariable Long activityId,
                                         @RequestHeader("Authorization") String token) {
            	   return certi.getByActivityId(activityId, token);
    				}
               
               
 //-----------------------------------Notification Controller-------------------------------------------------
               @GetMapping("/notifications")
               public ResponseEntity<?>GetAllNotification( @RequestHeader("Authorization") String token){
            	   return noticontroller.GetAllNotification(token);
               }
               
               @PostMapping("/MarkAllASRead")
               public ResponseEntity<?>MarkALLAsRead(@RequestHeader("Authorization") String token,@RequestBody List<Long> notiIds ){
            	   return noticontroller.MarkALLasRead(token, notiIds);
               }
               
               @GetMapping("/unreadCount")
               public ResponseEntity<?>UreadCount(@RequestHeader("Authorization") String token){
            	   return noticontroller.UreadCount(token);
               }
               
               @GetMapping("/clearAll")
               public ResponseEntity<?>ClearAll(@RequestHeader("Authorization") String token){
            	   return noticontroller.ClearAll(token);
               }
}

