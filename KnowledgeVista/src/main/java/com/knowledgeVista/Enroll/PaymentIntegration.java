package com.knowledgeVista.Enroll;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Settings.PaymentsettingRepository;
import com.knowledgeVista.Settings.Paymentsettings;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/buyCourse")
@CrossOrigin
public class PaymentIntegration {


    @Value("${rzp_currency}")
    private String currency;


	@Autowired
	private PaymentsettingRepository paymentsetting;
    
	 @Autowired 
	 private CourseDetailRepository coursedetail;
	 @Autowired
	private OrderuserRepo ordertablerepo;
	 @Autowired
	private MuserRepositories muserRepository;
	 
	 public Paymentsettings getpaydetails() {
		    try {
		        List<Paymentsettings> dataList = paymentsetting.findAll();
		        
		        if (dataList.isEmpty()) {
		            return null; // or throw an exception
		        } else {
		            if (dataList.size() == 1) {
		                return dataList.get(0);
		            } else {
		                return dataList.get(dataList.size() - 1);
		            }
		        }
		    } catch (Exception e) {
		        e.printStackTrace(); // or log the error
		        return null; // or throw an exception
		    }
		}
 @PostMapping("/create")
 public ResponseEntity<?> createOrder(@RequestBody Map<String, Long> requestData) {
     try {
         Long courseId = requestData.get("courseId");
         Long userId = requestData.get("userId");
         
         Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
         Optional<Muser> optionalUser = muserRepository.findById(userId);
         
         if (courseOptional.isPresent() && optionalUser.isPresent()) {
             Muser user = optionalUser.get();
             CourseDetail course = courseOptional.get();
             
             if (user.getCourses().contains(course)) {
                 // User is already enrolled in the course
                 return ResponseEntity.badRequest().body("User is already enrolled in the course");
             }
             
             Long amt = course.getAmount(); 
             if(getpaydetails()!=null) {
             String razorpayApiKey= getpaydetails().getRazorpay_key();
             String razorpayApiSecret=getpaydetails().getRazorpay_secret_key();
             
             RazorpayClient client = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
             JSONObject orderRequest = new JSONObject();
             orderRequest.put("amount", amt * 100); // Convert amount to paisa
             orderRequest.put("currency", currency);
             orderRequest.put("receipt", "receipt_" + courseId);
             orderRequest.put("notes", new JSONObject().put("user_id", userId));

             Order order = client.orders.create(orderRequest);

             String orderId = order.get("id").toString();
             Orderuser ordertable = new Orderuser();
             ordertable.setCourseId(courseId);
             ordertable.setOrderId(orderId);
             ordertable.setUserId(userId);
             ordertablerepo.save(ordertable);
             
             return ResponseEntity.ok(orderId);
             } else {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("Payment details not found");
             }
         } else {
             // Return an error response if the course is not found
             return ResponseEntity.notFound().build();
         }
     } catch (RazorpayException e) {
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
     } catch (Exception e) {
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
     }
 }


@PostMapping("/payment")
public ResponseEntity<String> updatePaymentId(@RequestBody Map<String, String> requestData) {
    try {
        String orderId = requestData.get("orderId");
        String paymentId = requestData.get("paymentId");

        Optional<Orderuser> orderUserOptional = ordertablerepo.findByOrderId(orderId);
        if (orderUserOptional.isPresent()) {
            Orderuser orderUser = orderUserOptional.get();
            orderUser.setPaymentId(paymentId);
            ordertablerepo.save(orderUser); // Update the OrderUser entity with the paymentId
            
            Long courseId = orderUser.getCourseId();
            Long userId = orderUser.getUserId();

            Optional<Muser> optionalUser = muserRepository.findById(userId);
            Optional<CourseDetail> optionalCourse = coursedetail.findById(courseId);
            
            if (optionalUser.isPresent() && optionalCourse.isPresent()) {
                Muser user = optionalUser.get();
                CourseDetail course = optionalCourse.get();

                // Check if the user is already enrolled in the course
                if (user.getCourses().contains(course)) {
                    return ResponseEntity.badRequest().body("You are already enrolled in the course");
                }
                String courseUrl=course.getCourseUrl();

                // Add the course directly to the user's list of courses
                user.getCourses().add(course);
                muserRepository.save(user);
             // Inside your controller method
                return ResponseEntity.ok().body(courseUrl);


            } else {
                // If a user or course with the specified ID doesn't exist, return 404
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating payment ID: " + e.getMessage());
    }
}

}
