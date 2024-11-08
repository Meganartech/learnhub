package com.knowledgeVista.Payments;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Notification.Service.NotificationService;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.knowledgeVista.secretapis.DeleteApis;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
@CrossOrigin
public class PaymentIntegration {


    @Value("${rzp_currency}")
    private String currency;
	 
	 @Autowired
	 private partpayrepo partpayrepo;
	 @Autowired
	 private JwtUtil jwtUtil;
	@Autowired
	private PaymentsettingRepository paymentsetting;
    
	 @Autowired 
	 private CourseDetailRepository coursedetail;
	 @Autowired
	private OrderuserRepo ordertablerepo;
	 @Autowired
	private MuserRepositories muserRepository;
	 
	 @Autowired
	private NotificationService notiservice;
	 
	 private static final Logger logger = LoggerFactory.getLogger(PaymentIntegration.class);

	 
	 public Paymentsettings getpaydetails(String token) {
		    try {
		    	String email=jwtUtil.getUsernameFromToken(token);
		   	     Optional<Muser>opreq=muserRepository.findByEmail(email);
		   	     String institution="";
		   	     if(opreq.isPresent()) {
		   	    	 Muser requser=opreq.get();
		   	    	institution=requser.getInstitutionName();
		   	     }
		       Optional <Paymentsettings> opdataList = paymentsetting.findByinstitutionName(institution);
		        if(opdataList.isPresent()) {
		        	Paymentsettings data=opdataList.get();
		        	return data;
		        }else {
		        	return null;
		        }
		        
		    } catch (Exception e) {
		        e.printStackTrace();    logger.error("", e);; // or log the error
		        return null; // or throw an exception
		    }
		}
 public ResponseEntity<?> createOrderfull( Map<String, Long> requestData,String token) {
     try {
         Long courseId = requestData.get("courseId");
         Long userId = requestData.get("userId");
         
         
         Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
         Optional<Muser> optionalUser = muserRepository.findById(userId);
         
         if (courseOptional.isPresent() && optionalUser.isPresent()) {
             Muser user = optionalUser.get();
             
            if("ADMIN".equals(user.getRole().getRoleName())||"TRAINER".equals(user.getRole().getRoleName())) {
             return ResponseEntity.badRequest().body("Students only can buy the course");
            }
             CourseDetail course = courseOptional.get();
             
//             if (user.getCourses().contains(course)) {
//                 // User is already enrolled in the course
//                 return ResponseEntity.badRequest().body("User is already enrolled in the course");
//             }
             
             List<Orderuser>orderuserlist=ordertablerepo.findAllByUserIDAndCourseID(userId, courseId,"paid");
             int amount = 0;
             Long amt = course.getAmount();
             for(Orderuser order:orderuserlist ) {
            	
            	 amount= amount+order.getAmountReceived();
             }
            if(amount==course.getAmount()) {
            	return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount already paid");
            }else {
            	amt=course.getAmount()-amount;
            }
            
             

             //need to work-----------
             String coursename=course.getCourseName();
             if(getpaydetails(token)!=null) {
            	 
             String razorpayApiKey= getpaydetails(token).getRazorpay_key();
             String razorpayApiSecret=getpaydetails(token).getRazorpay_secret_key();
             String institution=getpaydetails(token).getInstitutionName();
             RazorpayClient client = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
             JSONObject orderRequest = new JSONObject();
             orderRequest.put("amount", amt * 100); // Convert amount to paisa
             orderRequest.put("currency", currency);
             orderRequest.put("receipt", "receipt_" + courseId);
             orderRequest.put("notes", new JSONObject().put("user_id", userId));
             Order order = client.orders.create(orderRequest);
             
             String orderId = order.get("id").toString();
		     String status = order.get("status").toString();
             Orderuser ordertable = new Orderuser();
             ordertable.setCourseId(courseId);
             ordertable.setOrderId(orderId);
             ordertable.setUserId(userId);
             ordertable.setInstitutionName(institution);
             ordertable.setUsername(user.getUsername());
             ordertable.setEmail(user.getEmail());
             ordertable.setInstallmentnumber((long) 1);
             ordertable.setDate(new java.util.Date());
             ordertable.setStatus(status);
             ordertable.setCourseName(coursename);
             Orderuser ordersaved = ordertablerepo.save(ordertable); 
             String description="payment for course "+ordersaved.getCourseName()+" Rs."+amt;
             String name= coursename +" (Rs."+ amt +")";
             Map<String, Object> response = new HashMap<>();
	            response.put("orderId", orderId);
	            response.put("description", description);
	            response.put("name", name);
	            return ResponseEntity.ok(response);
             } else {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("Payment details not found");
             }
         } else {
             // Return an error response if the course is not found
             return ResponseEntity.notFound().build();
         }
     } catch (RazorpayException e) {
         e.printStackTrace();    logger.error("", e);;
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
     } catch (Exception e) {
         e.printStackTrace();    logger.error("", e);;
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
     }
 }

 public ResponseEntity<?> createOrderPart( Map<String, Long> requestData ,String token) {
     try {
         Long courseId = requestData.get("courseId");
         Long userId = requestData.get("userId");
         
         Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
         Optional<Muser> optionalUser = muserRepository.findById(userId);
         
         if (courseOptional.isPresent() && optionalUser.isPresent()) {
             Muser user = optionalUser.get();
             
            if("ADMIN".equals(user.getRole().getRoleName())||"TRAINER".equals(user.getRole().getRoleName())) {
             return ResponseEntity.badRequest().body("Students only can buy the course");
            }
             CourseDetail course = courseOptional.get();
             

             List<Orderuser>orderuserlist=ordertablerepo.findAllByUserIDAndCourseID(userId, courseId,"paid");
             int amount =0;
             for(Orderuser order:orderuserlist ) {
            	
            	 amount= amount+order.getAmountReceived();
             }
            if(amount==course.getAmount()) {
            	return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All installments  paid");
            }
            Optional<Course_PartPayment_Structure>opPartpay=partpayrepo.findBycourse(course);
              if(opPartpay.isPresent()) {
            	 
            	  
            	  Course_PartPayment_Structure partpay=opPartpay.get();
            	  List<InstallmentDetails> installmentslist=partpay.getInstallmentDetail();
            	  int count =ordertablerepo.findCountByUserIDAndCourseID(userId, courseId, "paid");
            	  int installmentlength=installmentslist.size();
	            	  if(installmentlength >count) {
	            	      InstallmentDetails installment=installmentslist.get(count);
	            	      Long installmentamount=installment.getInstallmentAmount();
			            	  if(getpaydetails(token)!=null) {
			                      String razorpayApiKey= getpaydetails(token).getRazorpay_key();
			                      String razorpayApiSecret=getpaydetails(token).getRazorpay_secret_key();
			                      String institution=getpaydetails(token).getInstitutionName();
			                      RazorpayClient client = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
			                      JSONObject orderRequest = new JSONObject();
			                      orderRequest.put("amount", installmentamount * 100); 
			                      orderRequest.put("currency", currency);
			                      orderRequest.put("receipt", "receipt_" + courseId);
			                      orderRequest.put("notes", new JSONObject().put("user_id", userId));
			                      
			                      Order order = client.orders.create(orderRequest);
						             String orderId = order.get("id").toString();
								     String status = order.get("status").toString();
						             Orderuser ordertable = new Orderuser();
						             ordertable.setCourseId(courseId);
						             ordertable.setOrderId(orderId);
						             ordertable.setUserId(userId);
						             ordertable.setCourseName(course.getCourseName());
						             ordertable.setDate(new java.util.Date());
						             ordertable.setStatus(status);
						             ordertable.setInstitutionName(institution);
						             ordertable.setEmail(user.getEmail());
						             ordertable.setUsername(user.getUsername());
						             ordertable.setInstallmentnumber(installment.getInstallmentNumber());

						             Orderuser ordersaved = ordertablerepo.save(ordertable); 
						             String description="payment for course "+ordersaved.getCourseName()+"  Rs. "+installmentamount+ "  for the installment "+ ordersaved.getInstallmentnumber();
						             String name= ordersaved.getCourseName() + "(installment " + ordersaved.getInstallmentnumber() +")" ;
						             Map<String, Object> response = new HashMap<>();
							            response.put("orderId", orderId);
							            response.put("description", description);
							            response.put("name", name);
							            return ResponseEntity.ok(response);
			            
			            	  }else {
			            		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			                              .body("Payment details not found");
			            	  }
	                }else {
	            	 return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All installments paid");
	                }
              }else {
            	 return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Part Payment Details UnAvailable");
               }
         } else {
             // Return an error response if the course is not found
             return ResponseEntity.notFound().build();
         }
     } catch (RazorpayException e) {
         e.printStackTrace();    logger.error("", e);;
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
     } catch (Exception e) {
         e.printStackTrace();    logger.error("", e);;
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
     }
 }


public ResponseEntity<String> updatePaymentId( Map<String, String> requestData ,String token) {
    try {
        String orderId = requestData.get("orderId");
        String paymentId = requestData.get("paymentId");
        
        Optional<Orderuser> orderUserOptional = ordertablerepo.findByOrderId(orderId);
        if (orderUserOptional.isPresent()) {
            Orderuser orderUser = orderUserOptional.get();
            orderUser.setPaymentId(paymentId);
            int amountPaidIn; 
            if(getpaydetails(token)!=null) {
                String razorpayApiKey= getpaydetails(token).getRazorpay_key();
                String razorpayApiSecret=getpaydetails(token).getRazorpay_secret_key();
                // Signature Verification (Crucial)
           
			            RazorpayClient client = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
			            
			           Order detailedOrder = client.orders.fetch(orderId);
			           String amountPaidString = detailedOrder.get("amount_paid").toString();
			           amountPaidIn = Integer.parseInt(amountPaidString) / 100;
					     String status = detailedOrder.get("status").toString();
					     orderUser.setStatus(status);
					     orderUser.setAmountReceived(amountPaidIn);
						         if (detailedOrder.has("created_at")) {
						             Date paymentDate = detailedOrder.get("created_at");
						             orderUser.setDate(paymentDate);
						         }
			
			           Orderuser savedorder= ordertablerepo.save(orderUser); // Update the OrderUser entity with the paymentId
			            
			            Long courseId = orderUser.getCourseId();
			            Long userId = orderUser.getUserId();
			
			            Optional<Muser> optionalUser = muserRepository.findById(userId);
			            Optional<CourseDetail> optionalCourse = coursedetail.findById(courseId);
			            
		            if (optionalUser.isPresent() && optionalCourse.isPresent()) {
		                Muser user = optionalUser.get();
		                String institution=user.getInstitutionName();
		                CourseDetail course = optionalCourse.get();
		                
		                //for notification
		                List<Muser> trainers=course.getTrainer();
		                List<Long> ids = new ArrayList<>();
		                
		                for (Muser trainer : trainers) {
		                	ids.add(trainer.getUserId());
		                }
		                
				                if (!user.getCourses().contains(course)) {
				                     user.getCourses().add(course);
				                     muserRepository.save(user);
				                }
				                this.notifiinstallment(courseId, userId);
		                  String courseUrl=course.getCourseUrl();
		                  String heading=" Payment Credited !";
			       	       String link="/payment/transactionHitory";
			       	       String linkfortrainer="/payment/trainer/transactionHitory";
			       	       String notidescription= "The payment for "+ course.getCourseName() + " was Credited  By "
			       	       + user.getUsername()+" for installment" + savedorder.getInstallmentnumber();
			       	       
			       	      Long NotifyId =  notiservice.createNotification("Payment",user.getUsername(),notidescription ,user.getEmail(),heading,link);
			       	    Long NotifyIdfortrainer =  notiservice.createNotification("Payment",user.getUsername(),notidescription ,user.getEmail(),heading,linkfortrainer);
		       	         
			       	      if(NotifyId!=null) {
			       	        	List<String> notiuserlist = new ArrayList<>(); 
			       	        	notiuserlist.add("ADMIN");
			       	        	notiservice.CommoncreateNotificationUser(NotifyId,notiuserlist,institution);
			       	        }
			       	        if(ids !=null) {
			       	        	notiservice.SpecificCreateNotification(NotifyIdfortrainer, ids);
			       	        }
		                  
		                  return ResponseEntity.ok().body(courseUrl);
		                
					  } else {
					                String errorMessage = "";
					                if (!optionalUser.isPresent()) {
					                    errorMessage += "User with ID " + userId + " not found. ";
					                }
					                if (!optionalCourse.isPresent()) {
					                    errorMessage += "Course with ID " + courseId + " not found. ";
					                }
					                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.trim());
					                }
                    
           } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pay details not found");
           }
            
        }else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
    } catch (Exception e) {
        e.printStackTrace();    logger.error("", e);;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating payment ID: " + e.getMessage());
    }
}

public void  notifiinstallment(Long courseId ,Long userId ) {
	   Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
       Optional<Muser> optionalUser = muserRepository.findById(courseId);
       
       if (courseOptional.isPresent() && optionalUser.isPresent()) {
           CourseDetail course = courseOptional.get();
	       Optional<Course_PartPayment_Structure>opPartpay=partpayrepo.findBycourse(course);
           if(opPartpay.isPresent()) {
        	   Course_PartPayment_Structure partpay=opPartpay.get();
        	   	  List<InstallmentDetails> installmentslist=partpay.getInstallmentDetail();
        	   	  int count =ordertablerepo.findCountByUserIDAndCourseID(userId, courseId, "paid");
        	   	  int installmentlength=installmentslist.size();
        	       	  if(installmentlength >count) {
        	       	      InstallmentDetails installment=installmentslist.get(count);
        	       	      Long Duration=installment.getDurationInDays();
        	       	      LocalDate startdate= LocalDate.now();
        	       	      LocalDate datetonotify=startdate.plusDays(Duration);
		                  String heading=" Installment Pending!";
			       	       String link="/dashboard/course";
			       	       String notidescription= "Installment date Of "+ course.getCourseName() + " for installment "+ installment.getInstallmentNumber() +" was pending";
			       	       
			       	      Long NotifyId =  notiservice.createNotification("Payment","system",notidescription ,"system",heading,link);
			       	     if(NotifyId!=null) {
			       	    	List<Long> ids = new ArrayList<>(); 
			       	    	ids.add(userId);
			       	        notiservice.SpecificCreateNotification(NotifyId, ids,datetonotify);
			       	        }
        	       	  }
           }
       }
   	 
	
}


}
