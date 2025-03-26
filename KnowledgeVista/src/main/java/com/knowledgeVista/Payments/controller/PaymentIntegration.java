package com.knowledgeVista.Payments.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.knowledgeVista.Batch.Batch;
import com.knowledgeVista.Batch.Repo.BatchRepository;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Email.EmailService;
import com.knowledgeVista.Notification.Service.NotificationService;
import com.knowledgeVista.Payments.Orderuser;
import com.knowledgeVista.Payments.Paymentsettings;
import com.knowledgeVista.Payments.Stripesettings;
import com.knowledgeVista.Payments.repos.OrderuserRepo;
import com.knowledgeVista.Payments.repos.PaymentsettingRepository;
import com.knowledgeVista.Payments.repos.Striperepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentIntegration {

	@Value("${currency}")
	private String currency;

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private PaymentsettingRepository paymentsetting;

	@Autowired
	private OrderuserRepo ordertablerepo;
	@Autowired
	private MuserRepositories muserRepository;

	@Autowired
	private NotificationService notiservice;
	@Autowired
	private Striperepo stripereop;
	@Autowired
	private BatchRepository batchrepo;
	@Autowired
	private EmailService emailService;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentIntegration.class);

	public Paymentsettings getpaydetails(String token) {
		try {
			String email = jwtUtil.getUsernameFromToken(token);
			Optional<Muser> opreq = muserRepository.findByEmail(email);
			String institution = "";
			if (opreq.isPresent()) {
				Muser requser = opreq.get();
				institution = requser.getInstitutionName();
			}
			Optional<Paymentsettings> opdataList = paymentsetting.findByinstitutionName(institution);
			if (opdataList.isPresent()) {
				Paymentsettings data = opdataList.get();
				return data;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			; // or log the error
			return null; // or throw an exception
		}
	}
	public ResponseEntity<String> updateStripepaymentid(HttpServletRequest request, Map<String, String> requestData, String token) {
		    try {
		        String sessionId = requestData.get("sessionId"); // You have the sessionId, not paymentId
		        if (sessionId == null) {
				    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session id is missing");
				}
		        Optional<Orderuser> orderUserOptional = ordertablerepo.findByOrderId(sessionId);
		        if (orderUserOptional.isPresent()) {
		            Orderuser orderUser = orderUserOptional.get();
		            Optional<Stripesettings> opdataList = stripereop.findByinstitutionName(orderUser.getInstitutionName());
					if (opdataList.isPresent()) {
						Stripesettings data = opdataList.get();
		            
		                String stripeApiKey = data.getStripe_secret_key();
		                
		                // Set the Stripe API Key
		                Stripe.apiKey = stripeApiKey;

		                // Retrieve the session using the sessionId
		                Session session = Session.retrieve(sessionId);

		                // Get the PaymentIntent ID from the session
		                String paymentIntentId = session.getPaymentIntent();

		                // Now retrieve the PaymentIntent to get payment details
		                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

		                // Extract relevant payment details from the PaymentIntent
		                long amountPaidInCents = paymentIntent.getAmountReceived(); // Amount paid in cents
		                int amountPaidIn = (int) (amountPaidInCents / 100); // Convert to dollars

		                // Get the status of the payment
		                String status = paymentIntent.getStatus();

		                // Update the OrderUser entity with payment details
		                orderUser.setPaymentId(paymentIntentId);
		                if(status.equals("succeeded")) {
		                orderUser.setStatus("paid");
		                }
		                orderUser.setAmountReceived(amountPaidIn);

		                // Optionally, you can set the payment date (created date of the PaymentIntent)
		                if (paymentIntent.getCreated() != null) {
		                    Date paymentDate = new Date(paymentIntent.getCreated() * 1000L); // Convert to Java Date
		                    orderUser.setDate(paymentDate);
		                }

		             Orderuser  savedorder=   ordertablerepo.save(orderUser);

		             return this.SetBatchToUser(request,savedorder);
		            } else {
		                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment details not found");
		            }

		        } else {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        logger.error("", e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("Error updating payment ID: " + e.getMessage());
		    }
		}

	
	public ResponseEntity<String> updatePaymentId(HttpServletRequest request, Map<String, String> requestData, String token) {
		try {
			String orderId = requestData.get("orderId");
			String paymentId = requestData.get("paymentId");
			if (orderId == null || paymentId == null) {
			    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some Values are missing");
			}

			Optional<Orderuser> orderUserOptional = ordertablerepo.findByOrderId(orderId);
			if (orderUserOptional.isPresent()) {
				Orderuser orderUser = orderUserOptional.get();
				orderUser.setPaymentId(paymentId);
				int amountPaidIn;
				if (getpaydetails(token) != null) {
					String razorpayApiKey = getpaydetails(token).getRazorpay_key();
					String razorpayApiSecret = getpaydetails(token).getRazorpay_secret_key();
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

					Orderuser savedorder = ordertablerepo.save(orderUser); // Update the OrderUser entity with the
																			// paymentId 
					return SetBatchToUser(request ,savedorder);


				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pay details not found");
				}

			} else {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			;
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating payment ID: " + e.getMessage());
		}
	}

private ResponseEntity<String> SetBatchToUser(HttpServletRequest request, Orderuser savedorder){
	Optional<Muser> optionalUser = muserRepository.findById(savedorder.getUserId());
	if(savedorder.getBatchId()==null) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("batchId Not Found");
	}
	
		Optional<Batch> opbatch= batchrepo.findById(savedorder.getBatchId());
	
	       
	if (optionalUser.isPresent() && opbatch.isPresent()) {
		Muser user = optionalUser.get();
		String institution = user.getInstitutionName();
		Batch batch = opbatch.get();
		
		// for notification
		List<Muser> trainers = batch.getTrainers();
		List<Long> ids = new ArrayList<>();
		List<CourseDetail>courses= batch.getCourses();

		for (Muser trainer : trainers) {
			ids.add(trainer.getUserId());
		}

		if (!user.getBatches().contains(batch)) {
			user.getBatches().add(batch);
			muserRepository.save(user);
		}
		if (!batch.getUsers().contains(user)) {
			batch.getUsers().add(user);
			batchrepo.save(batch);
		}


		user.getCourses().addAll(
			    courses.stream()
			           .filter(course -> !user.getCourses().contains(course)) // Filter out existing courses
			           .toList() // Collect remaining courses into a list
			);
    sendEnrollmentMail(request,courses, batch, user);
			muserRepository.save(user);
		String courseUrl = batch.getBatchUrl();
		String heading = " Payment Credited !";
		String link = "/payment/transactionHitory";
		String linkfortrainer = "/payment/trainer/transactionHitory";
		String notidescription = "The payment for Batch " + batch.getBatchTitle() + " was Credited  By "
				+ user.getUsername() + " for installment" + savedorder.getInstallmentnumber();

		Long NotifyId = notiservice.createNotification("Payment", user.getUsername(), notidescription,
				user.getEmail(), heading, link);
		Long NotifyIdfortrainer = notiservice.createNotification("Payment", user.getUsername(),
				notidescription, user.getEmail(), heading, linkfortrainer);

		if (NotifyId != null) {
			List<String> notiuserlist = new ArrayList<>();
			notiuserlist.add("ADMIN");
			notiservice.CommoncreateNotificationUser(NotifyId, notiuserlist, institution);
		}
		if (ids != null) {
			notiservice.SpecificCreateNotification(NotifyIdfortrainer, ids);
		}

		return ResponseEntity.ok().body(courseUrl);

	} else {
		String errorMessage = "";
		if (!optionalUser.isPresent()) {
			errorMessage += "User with ID " + savedorder.getUserId() + " not found. ";
		}
		if (!opbatch.isPresent()) {
			errorMessage += "Course with ID " + savedorder.getBatchId() + " not found. ";
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.trim());
	}
	
}

public void sendEnrollmentMail(HttpServletRequest request,List<CourseDetail> courses,Batch batch,Muser student) {
	List<String> bcc = null;
	List<String> cc = null;
	String institutionname = student.getInstitutionName();
       String domain = request.getHeader("origin"); // Extracts the domain dynamically

         // Fallback if "Origin" header is not present (e.g., direct backend requests)
         if (domain == null || domain.isEmpty()) {
             domain = request.getScheme() + "://" + request.getServerName();
             if (request.getServerPort() != 80 && request.getServerPort() != 443) {
                 domain += ":" + request.getServerPort();
             }
         }

         // Construct the Sign-in Link
         String signInLink = domain + "/login";

	String body = String.format(
	    "<html>"
	        + "<body>"
	        + "<h2>Welcome to LearnHub - Your Learning Journey Begins!</h2>"
	        + "<p>Dear %s,</p>"
	        + "<p>Congratulations! You have been successfully enrolled in <strong>Batch: %s</strong> at LearnHub.</p>"
	        + "<p>Below are your login credentials:</p>"
	        + "<p>In this batch, you will be learning the below  courses:</p>"
	        + "<ul>%s</ul>" // Placeholder for dynamic course list
	        + "<p>To get started:</p>"
	        + "<ul>"
	        + "<li>Log in to your LearnHub account.</li>"
	        + "<li>Access your enrolled courses in My Courses Tab.</li>"
	        + "<li>Engage with trainers and fellow students.</li>"
	        + "<li>Complete assignments and track your progress.</li>"
	        + "</ul>"
	        + "<p>If you need any assistance, our support team is here to help.</p>"


	                  + "<p>Click the link below to sign in:</p>"
	                  + "<p><a href='" + signInLink + "' style='font-size:16px; color:blue;'>Sign In</a></p>"
	        + "<p>Best Regards,<br>LearnHub Team</p>"
	        + "</body>"
	        + "</html>",
	    student.getUsername(),  // Student Name
	    batch.getBatchTitle(),   // Batch Name
	    generateCourseList(batch.getCourses()) // Converts list of courses to HTML list items
	);

	if (institutionname != null && !institutionname.isEmpty()) {
	    try {
	        List<String> emailList = new ArrayList<>();
	        emailList.add(student.getEmail());
	        emailService.sendHtmlEmailAsync(
	            institutionname, 
	            emailList,
	            cc, 
	            bcc, 
	            "Welcome to LearnHub - Batch Enrollment Successful!", 
	            body
	        );
	    } catch (Exception e) {
	        logger.error("Error sending mail: " + e.getMessage());
	    }
	}

	// Helper method to format courses as an HTML list
	

}
private String generateCourseList(List<CourseDetail> courses) {
    StringBuilder courseList = new StringBuilder();
    for (CourseDetail course : courses) {
        courseList.append("<li>").append(course.getCourseName()).append("</li>");
    }
    return courseList.toString();
}

//	public void notifiinstallment(Long courseId, Long userId) {
//		Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
//		Optional<Muser> optionalUser = muserRepository.findById(courseId);
//
//		if (courseOptional.isPresent() && optionalUser.isPresent()) {
//			CourseDetail course = courseOptional.get();
//			Optional<Course_PartPayment_Structure> opPartpay = partpayrepo.findBycourse(course);
//			if (opPartpay.isPresent()) {
//				Course_PartPayment_Structure partpay = opPartpay.get();
//				List<InstallmentDetails> installmentslist = partpay.getInstallmentDetail();
//				int count = ordertablerepo.findCountByUserIDAndCourseID(userId, courseId, "paid");
//				int installmentlength = installmentslist.size();
//				if (installmentlength > count) {
//					InstallmentDetails installment = installmentslist.get(count);
//					Long Duration = installment.getDurationInDays();
//					LocalDate startdate = LocalDate.now();
//					LocalDate datetonotify = startdate.plusDays(Duration);
//					String heading = " Installment Pending!";
//					String link = "/dashboard/course";
//					String notidescription = "Installment date Of " + course.getCourseName() + " for installment "
//							+ installment.getInstallmentNumber() + " was pending";
//
//					Long NotifyId = notiservice.createNotification("Payment", "system", notidescription, "system",
//							heading, link);
//					if (NotifyId != null) {
//						List<Long> ids = new ArrayList<>();
//						ids.add(userId);
//						notiservice.SpecificCreateNotification(NotifyId, ids, datetonotify);
//					}
//				}
//			}
//		}
//
//	}
	
	//===============================BatchPayments=================================
	
	
}
