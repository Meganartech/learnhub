package com.knowledgeVista.Payments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
@RestController
public class PaymentListController {
	
	    @Autowired
	    private JwtUtil jwtUtil;
		@Autowired
		private MuserRepositories muserRepository;
		@Autowired
		private OrderuserRepo ordertablerepo;
		 @Autowired
		 private partpayrepo partpayrepo;

		 @Autowired 
		 private CourseDetailRepository coursedetail;
	
	public ResponseEntity<?>ViewMypaymentHistry(String token){
		  try {
		    	
		    	 if (!jwtUtil.validateToken(token)) {
		    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		                    .body("Unauthorized access");
		         }
		    	 String email=jwtUtil.getUsernameFromToken(token);
		    	Optional< Muser> opuser = muserRepository.findByEmail(email);
		    			if(opuser.isPresent()) {
		    				Muser user=opuser.get();
		    				Long userId=user.getUserId();
		              	   List<Orderuser>orderuser =ordertablerepo.findAllByUserId(userId);
		              	   if(orderuser.size()>0) {
		              		  
		              		   return ResponseEntity.ok(orderuser);
		              	   }else {
		              		   return ResponseEntity.notFound().build();
		              	   }
		    			}else {
		    				 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		 		                    .body("Unauthorized access");
		    			}

		  }catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An error occurred : " + e.getMessage() );
		    }
	}
	
	public ResponseEntity<?> ViewPaymentdetails(String token,Long courseId){
		try {
			 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("invalid  Token");
	         }
			 String role=jwtUtil.getRoleFromToken(token);
			 if("USER".equals(role)) {
				 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		                    .body("Students Cannot access this page");
			 }
			 String email=jwtUtil.getUsernameFromToken(token);
	   	     Optional<Muser>opreq=muserRepository.findByEmail(email);
	   	     String institution="";
	   	     if(opreq.isPresent()) {
	   	    	 Muser requser=opreq.get();
	   	    	institution=requser.getInstitutionName();
	   	     }else {
	   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
	   	     }
			 if("ADMIN".equals(role)||"TRAINER".equals(role)) {

		            Optional<CourseDetail> courseOptional = coursedetail.findByCourseIdAndInstitutionName(courseId, institution);
		            if(courseOptional.isPresent()) {
		            	CourseDetail course=courseOptional.get();
			            Optional<Course_PartPayment_Structure>opPartpay=partpayrepo.findBycourse(course);
			            if(opPartpay.isPresent()) {
			            	Course_PartPayment_Structure partpay=opPartpay.get();
			            	  List<InstallmentDetails> installmentslist=partpay.getInstallmentDetail();
			            	   for(InstallmentDetails installment :installmentslist) {
			            		   installment.setPartpay(null);
			            	   }
			            	   return ResponseEntity.ok(installmentslist);
			            }else {
			            	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("partpay details not found");
			            }
		            }else {
		            	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not found");
		            }

			 }else {
				 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
		                    .body("Unauthorized access");
			 }
			
		}catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred : " + e.getMessage() );
	    }
	}
	

public ResponseEntity<?> viewTransactionHistory(String token) {
    try {
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Token");
        }

        String role = jwtUtil.getRoleFromToken(token);
        String email=jwtUtil.getUsernameFromToken(token);
  	     Optional<Muser>opreq=muserRepository.findByEmail(email);
  	     String institution="";
  	     if(opreq.isPresent()) {
  	    	 Muser requser=opreq.get();
  	    	institution=requser.getInstitutionName();
  	     }else {
  	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
  	     }
        if ("USER".equals(role)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Students cannot access this page");
        }

        if ("ADMIN".equals(role) ) {
                List<Orderuser> orderUsers = ordertablerepo.findAllByinstitutionName(institution);

                if (!orderUsers.isEmpty()) {
                    return ResponseEntity.ok(orderUsers);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                // Handle case where user with email is not found (log, return appropriate message)
                return ResponseEntity.notFound().build();
            }
        
    } catch (Exception e) {
        // Log the exception and return a more informative error response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + e.getMessage() );
    }
}

public ResponseEntity<?>ViewMypaymentHistrytrainer(String token){
	  try {
	    	
	    	 if (!jwtUtil.validateToken(token)) {
	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("Unauthorized access");
	         }
	    	 String email=jwtUtil.getUsernameFromToken(token);
	    	Optional< Muser> opuser = muserRepository.findByEmail(email);
	    			if(opuser.isPresent()) {
	    				Muser user=opuser.get();
	    				String institutionName=user.getInstitutionName();
	    				List<CourseDetail> courses =user.getAllotedCourses();
	    				   
		              	 List<Object> courseOrderMap = new ArrayList<>();
	    			        for (CourseDetail course : courses) {
	    			            Long courseId = course.getCourseId();
	    			            List<Orderuser> orderUsersForCourse = ordertablerepo.findAllBycourseIdandinstitutionName(courseId, institutionName);

	    			            if (!orderUsersForCourse.isEmpty()) {
	    			                courseOrderMap.addAll(orderUsersForCourse);
	    			            }
	    			        }
	              	
	              	   if(courseOrderMap.size()>0) {
	              		  
	              		   return ResponseEntity.ok(courseOrderMap);
	              	   }else {
	              		   return ResponseEntity.notFound().build();
	              	   }

	    			}else {
	    				 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	 		                    .body("Unauthorized access");
	    			}

	  }catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred : " + e.getMessage() );
	    }
}



}
