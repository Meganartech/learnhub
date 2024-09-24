package com.knowledgeVista;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.multipart.MultipartFile;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.CourseDetailDto;
import com.knowledgeVista.Course.Controller.CheckAccess;
import com.knowledgeVista.Course.Controller.CourseController;
import com.knowledgeVista.Course.Controller.CourseControllerSecond;
import com.knowledgeVista.Course.Controller.videolessonController;
import com.knowledgeVista.Course.Test.CourseTest;
import com.knowledgeVista.Course.Test.controller.QuestionController;
import com.knowledgeVista.Course.Test.controller.Testcontroller;
import com.knowledgeVista.Course.certificate.certificateController;
import com.knowledgeVista.Email.EmailController;
import com.knowledgeVista.Email.EmailRequest;
import com.knowledgeVista.Email.Mailkeys;
import com.knowledgeVista.License.LicenceControllerSecond;
import com.knowledgeVista.License.LicenseController;
import com.knowledgeVista.Meeting.ZoomAccountKeys;
import com.knowledgeVista.Meeting.ZoomMeetAccountController;
import com.knowledgeVista.Meeting.ZoomMeetingService;
import com.knowledgeVista.Meeting.zoomclass.MeetingRequest;
import com.knowledgeVista.Notification.Controller.NotificationController;
import com.knowledgeVista.Payments.PaymentIntegration;
import com.knowledgeVista.Payments.PaymentListController;
import com.knowledgeVista.Payments.Paymentsettings;
import com.knowledgeVista.Payments.PaymentSettingsController;
import com.knowledgeVista.Settings.Feedback;
import com.knowledgeVista.SysAdminPackage.SysadminController;
import com.knowledgeVista.User.MuserDto;
import com.knowledgeVista.User.Controller.AddUsers;
import com.knowledgeVista.User.Controller.AssignCourse;
import com.knowledgeVista.User.Controller.AuthenticationController;
import com.knowledgeVista.User.Controller.Edituser;
import com.knowledgeVista.User.Controller.Listview;
import com.knowledgeVista.User.Controller.MserRegistrationController;
import com.knowledgeVista.User.Usersettings.RoleDisplayController;
import com.knowledgeVista.User.Usersettings.Role_display_name;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@CrossOrigin
public class FrontController {
	  @Value("${spring.profiles.active}")
	    private String activeProfile;
	  @Value("${spring.environment}")
	    private String environment;
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
	private LicenceControllerSecond licencesec;
	
	@Autowired
	private PaymentSettingsController settings;
	
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

	
	@Autowired
	private SysadminController sysadmin;
	
	@Autowired
	private ZoomMeetingService zoomMeetingService;
	
	@Autowired
	private ZoomMeetAccountController zoomaccountconfig;
	
	@Autowired
	private EmailController emailcontroller;
	
	@Autowired
	private RoleDisplayController displayctrl; 
	
//-------------------ACTIVE PROFILE------------------
	@GetMapping("/Active/Environment")
	public String getActiEnvironment() {
		return environment;
	}
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
	    public ResponseEntity<List<CourseDetailDto>> viewCourse(@RequestHeader("Authorization") String token) {
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
				  									@RequestParam(value="thumbnail",required=false) MultipartFile file,
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
       		public ResponseEntity<?> getAllUserforLicencecheck(  @RequestHeader("Authorization") String token) {
            	   if(environment.equals("SAS")) {
            		   return licence.getAllUserSAS(token);
           	   }
            	   else if(environment.equals("VPS")) {
            		   return licence.getAllUser();
            	   }
            	   else {
            		   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Licence get functionality disabled");
                        
            	   }
               }
           	@GetMapping("/api/v2/count")
    		public ResponseEntity<Integer> count(@RequestHeader("Authorization") String token) {
           		return licence.count(token);
           	}
           		
           		@PostMapping("/api/v2/uploadfile")
    		    public ResponseEntity<?> upload(@RequestParam("audioFile") MultipartFile File,@RequestParam("lastModifiedDate") String lastModifiedDate,@RequestHeader("Authorization") String token){
           		   if(environment.equals("VPS")) {
           			   return licence.upload(File,lastModifiedDate,token);
           		   }else{
           			   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Licence upload functionality disabled");
                   	
           		   }
           		   }
           		@PostMapping("/api/Sysadmin/uploadLicence")
    		    public ResponseEntity<?> uploadLicence(@RequestParam("audioFile") MultipartFile File,@RequestHeader("Authorization") String token){
           		   if(environment.equals("SAS")) {
           			   return licence.uploadBysysAdmin(File,token);
           		   }else{
           			   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Licence Cannot Be uploaded By SysAdmin");
                   	
           		   }
           		}
           		
  //----------------------------LICENCE CONTROLLER SECOND---------------------------
           		@GetMapping("/licence/getinfo")
           		public ResponseEntity<?>GetLicenceDetails(@RequestHeader("Authorization") String token){
           			return licencesec.GetLicenseDetails(token);
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
       	          @RequestParam(value="skills",required=false) String skills,
       	       @RequestParam(value="profile", required=false) MultipartFile profile,
       	          @RequestParam("isActive") Boolean isActive,
                  @RequestParam(value="countryCode",defaultValue = "+91")String countryCode,
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
      	          @RequestParam(value="skills",required=false) String skills,
      	        @RequestParam(value="profile", required=false) MultipartFile profile,
      	          @RequestParam("isActive") Boolean isActive,
                  @RequestParam(value="countryCode",defaultValue = "+91")String countryCode,
      	          @RequestHeader("Authorization") String token) {
           		return adduser.addStudent(username, psw, email, dob, phone, skills, profile, isActive,countryCode ,token);
           	}
           	

      	  @DeleteMapping("/admin/deactivate/trainer")
      	  public ResponseEntity<?> DeactivateTrainer(
      	          @RequestParam("email") String email,
      	        @RequestParam("reason") String reason,
      	          @RequestHeader("Authorization") String token) {
      		  return adduser.DeactivateTrainer(reason,email, token);
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
    	          @RequestParam("reason") String reason,
    	          @RequestHeader("Authorization") String token) {
    		  return adduser.DeactivateStudent(reason,email, token);
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
    		     @RequestParam(name="skills",required=false) String skills,
    		     @RequestParam(value="profile", required=false) MultipartFile profile,
    		     @RequestParam("isActive") Boolean isActive,
                 @RequestParam(name="countryCode",defaultValue = "+91")String countryCode,
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
    		     @RequestParam(name="skills",required=false) String skills,
    		     @RequestParam(value="profile", required=false) MultipartFile profile,
    		     @RequestParam("isActive") Boolean isActive,
                 @RequestParam(name="countryCode",defaultValue = "+91")String countryCode,
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
    		     @RequestParam(name="skills",required=false) String skills,
    		     @RequestParam(value="profile", required=false) MultipartFile profile,
    		     @RequestParam("isActive") Boolean isActive,
    		     @RequestHeader("Authorization") String token,
                 @RequestParam(name="countryCode",defaultValue = "+91")String countryCode
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
    		    public ResponseEntity<?> getUsersByRoleName(@RequestHeader("Authorization") String token,
    		    		 @RequestParam(defaultValue = "0") int pageNumber, 
                         @RequestParam(defaultValue = "10") int pageSize) {
    		    	return listview.getUsersByRoleName(token,pageNumber, pageSize);
    		    }
    		    @GetMapping("/view/users/{userId}")
    		    public ResponseEntity<?> getUserById(@PathVariable Long userId,@RequestHeader("Authorization") String token) {
    		    	return listview.getUserById(userId,token);
    		    }
    		    @GetMapping("/view/Trainer")
    		    public ResponseEntity<?> getTrainerByRoleName(@RequestHeader("Authorization") String token,
    		    		 @RequestParam(defaultValue = "0") int pageNumber, 
                         @RequestParam(defaultValue = "10") int pageSize) {
    		    	return listview.getTrainerByRoleName(token ,pageNumber, pageSize);
    		    }
    		    
    		    @GetMapping("/view/Mystudent")
    		    public ResponseEntity<Page<MuserDto>> GetStudentsOfTrainer(@RequestHeader("Authorization") String token,
    		    		 @RequestParam(defaultValue = "0") int pageNumber, 
                         @RequestParam(defaultValue = "10") int pageSize){
    		    	return listview.GetStudentsOfTrainer(token,pageNumber, pageSize);
    		    }
    		    @GetMapping("/search/users")
    		    public  ResponseEntity<List<String>> getusersSearch(@RequestHeader("Authorization") String token, @RequestParam("query") String query){
    		   return listview.SearchEmail(token, query);
    		    }
    		    @GetMapping("/search/usersbyTrainer")
    		    public  ResponseEntity<List<String>> getusersSearchbytrainer(@RequestHeader("Authorization") String token, @RequestParam("query") String query){
    		   return listview.SearchEmailTrainer(token, query);
    		    }
    		    @GetMapping("/admin/search")
    		    public ResponseEntity<Page<MuserDto>> searchAdmin(
    		            @RequestParam(value = "username", required = false) String username,
    		            @RequestParam(value = "email", required = false) String email,
    		            @RequestParam(value = "phone", required = false) String phone,
    		            @RequestParam(value = "dob", required = false)   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
    		            @RequestParam("institutionName") String institutionName,
    		            @RequestParam(value = "skills", required = false) String skills,
    		            @RequestParam(value = "page", defaultValue = "0") int page,
    		            @RequestParam(value = "size", defaultValue = "10") int size,
    		            @RequestHeader("Authorization") String token
    		            ) {
    		    	return listview.searchAdmin(username, email, phone, dob, institutionName, skills, page, size,token);
    		    }
    		    
    		    @GetMapping("/trainer/search")
    		    public ResponseEntity<Page<MuserDto>> searchTrainer(
    		            @RequestParam(value = "username", required = false) String username,
    		            @RequestParam(value = "email", required = false) String email,
    		            @RequestParam(value = "phone", required = false) String phone,
    		            @RequestParam(value = "dob", required = false)   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
    		            @RequestParam("institutionName") String institutionName,
    		            @RequestParam(value = "skills", required = false) String skills,
    		            @RequestParam(value = "page", defaultValue = "0") int page,
    		            @RequestParam(value = "size", defaultValue = "10") int size,
    	    		     @RequestHeader("Authorization") String token) {
    		    	return listview.searchTrainer(username, email, phone, dob, institutionName, skills, page, size, token);
    		    }
    		    @GetMapping("/users/search")
    		    public ResponseEntity<Page<MuserDto>> searchUsers(
    		            @RequestParam(value = "username", required = false) String username,
    		            @RequestParam(value = "email", required = false) String email,
    		            @RequestParam(value = "phone", required = false) String phone,
    		            @RequestParam(value = "dob", required = false)   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
    		            @RequestParam("institutionName") String institutionName,
    		            @RequestParam(value = "skills", required = false) String skills,
    		            @RequestParam(value = "page", defaultValue = "0") int page,
    		            @RequestParam(value = "size", defaultValue = "10") int size,
   	    		     @RequestHeader("Authorization") String token) {
    		    	return listview.searchUser(username, email, phone, dob, institutionName, skills, page, size, token);
    		    }
    		    
    		    @GetMapping("/Institution/search/Trainer")
    		    public ResponseEntity<Page<MuserDto>> searchTrainerByadmin(
    		            @RequestParam(value = "username", required = false) String username,
    		            @RequestParam(value = "email", required = false) String email,
    		            @RequestParam(value = "phone", required = false) String phone,
    		            @RequestParam(value = "dob", required = false)   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
    		            
    		            @RequestParam(value = "skills", required = false) String skills,
    		            @RequestParam(value = "page", defaultValue = "0") int page,
    		            @RequestParam(value = "size", defaultValue = "10") int size,
    		            @RequestHeader("Authorization") String token
    		            ) {
    		    	return listview.searchTrainerByAdmin(username, email, phone, dob, skills, page, size,token);
    		    }
    		    @GetMapping("/Institution/search/User")
    		    public ResponseEntity<Page<MuserDto>> searchUserByadmin(
    		            @RequestParam(value = "username", required = false) String username,
    		            @RequestParam(value = "email", required = false) String email,
    		            @RequestParam(value = "phone", required = false) String phone,
    		            @RequestParam(value = "dob", required = false)   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
    		            
    		            @RequestParam(value = "skills", required = false) String skills,
    		            @RequestParam(value = "page", defaultValue = "0") int page,
    		            @RequestParam(value = "size", defaultValue = "10") int size,
    		            @RequestHeader("Authorization") String token
    		            ) {
    		    	return listview.searchUserByAdminorTrainer(username, email, phone, dob, skills, page, size,token);
    		    }
    		    @GetMapping("/Institution/search/Mystudent")
    		    public ResponseEntity<Page<MuserDto>> searchMystudent(
    		            @RequestParam(value = "username", required = false) String username,
    		            @RequestParam(value = "email", required = false) String email,
    		            @RequestParam(value = "phone", required = false) String phone,
    		            @RequestParam(value = "dob", required = false)   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
    		            
    		            @RequestParam(value = "skills", required = false) String skills,
    		            @RequestParam(value = "page", defaultValue = "0") int page,
    		            @RequestParam(value = "size", defaultValue = "10") int size,
    		            @RequestHeader("Authorization") String token
    		            ) {
    		    	return listview.searchStudentsOfTrainer(username, email, phone, dob, skills, page, size,token);
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
    			                                          @RequestParam(name="skills",required=false ) String skills,
    			                                          @RequestParam(name="profile" ,required=false) MultipartFile profile,
    			                                          @RequestParam("isActive") Boolean isActive,
    			                                          @RequestParam(name="countryCode",defaultValue = "+91")String countryCode) {
    				return muserreg.registerAdmin(username, psw, email, institutionName, dob,role, phone, skills, profile, isActive,countryCode);
    			}

    			@GetMapping("/student/users/{email}")
    			public ResponseEntity<?> getUserByEmail(@PathVariable String email,
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
    		   @GetMapping("/details/{email}")
    		   public ResponseEntity<?> getDetailsbyemail(@PathVariable String email,
   		    		@RequestHeader("Authorization") String token) {
   		    	return muserreg.getDetailsbyemail(email, token);
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
               @PostMapping("/getImages")
   		    public ResponseEntity<?> GetNotiImage(@RequestHeader("Authorization") String token,
   		                                          @RequestBody List<Long> notifyIds) {
            	   return noticontroller.GetNotiImage(token,notifyIds);
            	   
               }
               
               
 //------------------------------------------SYSADMIN CONTROl------------------------------              
               
               @GetMapping("/ViewAll/Admins")
               public ResponseEntity<?> ViewAllAdmins(
                       @RequestHeader("Authorization") String token,
                      @RequestParam(defaultValue = "0") int pageNumber, 
                    @RequestParam(defaultValue = "10") int pageSize  ){
            	   
                 return sysadmin.viewAdmins(token, pageNumber, pageSize);
               }

               @GetMapping("/ViewAll/Trainers")
               public ResponseEntity<?>ViewAllTrainers(@RequestHeader("Authorization") String token,
            		   @RequestParam(defaultValue = "0") int pageNumber, 
                       @RequestParam(defaultValue = "10") int pageSize){
            	   return sysadmin.viewTrainers(token, pageNumber, pageSize);
               }
               @GetMapping("/ViewAll/Students")
               public ResponseEntity<?>ViewAllStudents(@RequestHeader("Authorization") String token,
            		   @RequestParam(defaultValue = "0") int pageNumber, 
                       @RequestParam(defaultValue = "10") int pageSize){
            	   return sysadmin.viewStudents(token, pageNumber, pageSize);
               }
               @DeleteMapping("/activate/admin")
               public ResponseEntity<?>ActiveteAdmin(@RequestParam("email") String email, 
            		   @RequestHeader("Authorization") String token){
            	   return sysadmin.activateAdmin(email, token);
               }
               @DeleteMapping("/deactivate/admin")
               public ResponseEntity<?>DeActiveteAdmin(@RequestParam("email") String email,
            		   @RequestParam("reason") String reason, @RequestHeader("Authorization") String token){
            	   return sysadmin.DeactivateAdmin(reason,email, token);
               }
               
    //--------------------ZOOM-------------------------------

@PostMapping("/api/zoom/create-meeting")
public ResponseEntity<?> createMeeting(@RequestBody MeetingRequest meetingReq,@RequestHeader("Authorization") String token) {
    
        return zoomMeetingService.createMeetReq(meetingReq,token);
   
}

@GetMapping("/api/zoom/getMyMeetings")
public ResponseEntity<?>GetMyMeetings(@RequestHeader("Authorization") String token){
	return zoomMeetingService.getMetting(token);
}

@GetMapping("/api/zoom/get/meet/{meetingId}")
public ResponseEntity<?>GetmeetbyMeetingId(@PathVariable Long meetingId ,@RequestHeader("Authorization") String token){
	return zoomMeetingService.getMeetDetailsForEdit(token, meetingId);
}

@PatchMapping("/api/zoom/meet/{meetingId}")
public ResponseEntity<?>EditMeetingByMeetingId(@RequestBody MeetingRequest meetingReq,@PathVariable Long meetingId,@RequestHeader("Authorization") String token){
	return zoomMeetingService.EditZoomMeetReq(meetingReq, meetingId, token);
}
@DeleteMapping("/api/zoom/delete/{meetingId}")
public ResponseEntity<?>DeleteMeeting(@PathVariable Long meetingId ,@RequestHeader("Authorization") String token)
{
	return zoomMeetingService.DeleteMeet(meetingId, token);
}
//---------------------------ZOOM ACCOUNT CONTROLLER_------------
@PostMapping("/zoom/save/Accountdetails")
public ResponseEntity<?>SaveAccountDetails(@RequestBody ZoomAccountKeys accountdetails ,@RequestHeader("Authorization") String token){
	return zoomaccountconfig.SaveAccountDetails(accountdetails, token);
}
@PatchMapping("/zoom/Edit/Accountdetails")
public ResponseEntity<?>EditAccountDetails(@RequestBody ZoomAccountKeys accountdetails ,@RequestHeader("Authorization") String token){
	return zoomaccountconfig.EditAccountDetails(accountdetails, token);
}

@GetMapping("/zoom/get/Accountdetails")
public ResponseEntity<?> getMethodName(@RequestHeader("Authorization") String token) {
	return zoomaccountconfig.getMethodName(token);
}
@PostMapping("/SysAdmin/zoom/save/Accountdetails")
	public ResponseEntity<?>SaveAccountDetailsSYS(@RequestBody ZoomAccountKeys accountdetails ,@RequestHeader("Authorization") String token){
		 return zoomaccountconfig.SaveAccountDetailsSYS(accountdetails, token);
}
@PatchMapping("/SysAdmin/zoom/Edit/Accountdetails")
	public ResponseEntity<?>EditAccountDetailsSYS(@RequestBody ZoomAccountKeys accountdetails ,@RequestHeader("Authorization") String token){
		 return zoomaccountconfig.EditAccountDetailsSYS(accountdetails, token);
}

@GetMapping("/SysAdmin/zoom/get/Accountdetails")
public ResponseEntity<?> getMethodNameSYS(@RequestHeader("Authorization") String token) {
	return zoomaccountconfig.getMethodNameSYS(token);
}
   //-------------------EMAIL CONTROLLER--------------------------
               @PostMapping("/sendMail")
         	  public ResponseEntity<?> sendMail( @RequestHeader("Authorization") String token,@RequestBody EmailRequest emailRequest) {
               return emailcontroller.sendMail(token, emailRequest);
               }
               
               @GetMapping("/get/mailkeys")
         	  public ResponseEntity<?>getMailkeys(@RequestHeader("Authorization") String token){
            	   return emailcontroller.getMailkeys(token);
               }
               
               @PatchMapping("/Edit/mailkeys")
         	  public ResponseEntity<?>UpdateMailkeys(@RequestHeader("Authorization") String token,@RequestBody Mailkeys mailkeys){
         		  return emailcontroller.UpdateMailkeys(token, mailkeys);
               }
               
               @PostMapping("/save/mailkeys")
         	  public ResponseEntity<?> saveMail(
         	          @RequestHeader("Authorization") String token,@RequestBody Mailkeys mailkeys) {
         	      return emailcontroller.saveMail(token, mailkeys);
               }
               
//-------------------------------------------ROLE DISPLAY CONTROLLER----------------------------------------------------
               @GetMapping("/get/displayName")
           	public ResponseEntity<?>getdisplayNames(@RequestHeader("Authorization") String token){
            	   return displayctrl.getdisplayNames(token);
               }
               
               @PatchMapping("/edit/displayname")
          	 public ResponseEntity<?> UpdateDisplayName(@RequestHeader("Authorization") String token,@RequestBody Role_display_name displayName){
            	   return displayctrl.UpdateDisplayName(token, displayName);
               }
               
               @PostMapping("/post/displayname")
          	 public ResponseEntity<?> postDisplayname(@RequestHeader("Authorization") String token,@RequestBody Role_display_name roledisplaynames){
          		return displayctrl.postDisplayname(token, roledisplaynames);
               }
            	   
          		
}

