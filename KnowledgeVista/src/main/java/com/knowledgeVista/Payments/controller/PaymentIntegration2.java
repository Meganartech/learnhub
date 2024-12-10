package com.knowledgeVista.Payments.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.paypal.orders.*;
import com.paypal.core.PayPalHttpClient;
import com.paypal.core.PayPalEnvironment;
import com.paypal.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Notification.Service.NotificationService;
import com.knowledgeVista.Payments.Course_PartPayment_Structure;
import com.knowledgeVista.Payments.InstallmentDetails;
import com.knowledgeVista.Payments.Orderuser;
import com.knowledgeVista.Payments.Paypalsettings;
import com.knowledgeVista.Payments.repos.OrderuserRepo;
import com.knowledgeVista.Payments.repos.partpayrepo;
import com.knowledgeVista.Payments.repos.paypalrepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PaymentIntegration2 {
	@Autowired
	private paypalrepo paypalrepo;
	@Autowired
	private partpayrepo partpayrepo;
	  @Value("${paypal.mode}")
	    private String paypalMode;
	  @Autowired
		private OrderuserRepo ordertablerepo;
		@Autowired
		private MuserRepositories muserRepository;
		@Autowired
		private NotificationService notiservice;
		
		@Autowired
		private CourseDetailRepository coursedetail;
		@Autowired
		private JwtUtil jwtUtil;
	private static final Logger logger = LoggerFactory.getLogger(PaymentIntegration.class);
	private Orderuser saveOrderDetails(CourseDetail course, Muser user, Long amt, String orderId, String status,
			String institution, Long courseId, Long userId, Long installmentNumber,String PaymentType) {
		Orderuser orderTable = new Orderuser();
		orderTable.setCourseId(courseId);
		orderTable.setOrderId(orderId);
		orderTable.setUserId(userId);
		orderTable.setPaymentType(PaymentType);
		orderTable.setInstitutionName(institution);
		orderTable.setUsername(user.getUsername());
		orderTable.setEmail(user.getEmail());
		orderTable.setInstallmentnumber(installmentNumber);
		orderTable.setDate(new java.util.Date());
		orderTable.setStatus(status);
		orderTable.setCourseName(course.getCourseName());
		return ordertablerepo.save(orderTable);
	}
	public void notifiinstallment(Long courseId, Long userId) {
		Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
		Optional<Muser> optionalUser = muserRepository.findById(courseId);

		if (courseOptional.isPresent() && optionalUser.isPresent()) {
			CourseDetail course = courseOptional.get();
			Optional<Course_PartPayment_Structure> opPartpay = partpayrepo.findBycourse(course);
			if (opPartpay.isPresent()) {
				Course_PartPayment_Structure partpay = opPartpay.get();
				List<InstallmentDetails> installmentslist = partpay.getInstallmentDetail();
				int count = ordertablerepo.findCountByUserIDAndCourseID(userId, courseId, "paid");
				int installmentlength = installmentslist.size();
				if (installmentlength > count) {
					InstallmentDetails installment = installmentslist.get(count);
					Long Duration = installment.getDurationInDays();
					LocalDate startdate = LocalDate.now();
					LocalDate datetonotify = startdate.plusDays(Duration);
					String heading = " Installment Pending!";
					String link = "/dashboard/course";
					String notidescription = "Installment date Of " + course.getCourseName() + " for installment "
							+ installment.getInstallmentNumber() + " was pending";

					Long NotifyId = notiservice.createNotification("Payment", "system", notidescription, "system",
							heading, link);
					if (NotifyId != null) {
						List<Long> ids = new ArrayList<>();
						ids.add(userId);
						notiservice.SpecificCreateNotification(NotifyId, ids, datetonotify);
					}
				}
			}
		}

	}

	public ResponseEntity<?> handlePaypalCheckout(
	        CourseDetail course, Muser user, Long amt, Long courseId, Long userId, Long installmentNumber, HttpServletRequest httpRequest) {
	    try {
	        // Fetch PayPal settings for the institution
	        Optional<Paypalsettings> opdataList = paypalrepo.FindByInstitutionName(user.getInstitutionName());
	        if (opdataList.isPresent()) {
	            Paypalsettings data = opdataList.get();
	            String clientId = data.getPaypal_client_id();
	            String clientSecret = data.getPaypal_secret_key();

	            // Determine the mode: Use "live" or "sandbox" dynamically based on some condition
	            String mode = paypalMode;
	            
	            // Select environment dynamically based on mode
	            PayPalHttpClient paypalClient;
	            if ("live".equalsIgnoreCase(mode)) {
	                // Use Live Environment (for production)
	                PayPalEnvironment environment = new PayPalEnvironment.Live(clientId, clientSecret);
	                paypalClient = new PayPalHttpClient(environment);
	            } else {
	                // Use Sandbox Environment (for development)
	                PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
	                paypalClient = new PayPalHttpClient(environment);
	            }

	            String clientBaseUrl = httpRequest.getHeader("Origin");
	            if (clientBaseUrl == null) {
	                String referer = httpRequest.getHeader("Referer");
	                if (referer != null) {
	                    System.out.println("Client Base URL: " + referer);
	                    clientBaseUrl = referer.split("/")[0] + "//" + referer.split("/")[2];
	                }
	            }

	            System.out.println("Client Base URL: " + clientBaseUrl);

	            // Define success and cancel URLs
	            String successUrl = clientBaseUrl + "/updatePaypalPayment";
	            String cancelUrl = clientBaseUrl + "/dashboard/course";

	            // Create PayPal Order
	            OrderRequest orderRequest = new OrderRequest();
	            orderRequest.checkoutPaymentIntent("CAPTURE");
	            double exchangeRate = 100.0; // Adjust this rate dynamically based on current exchange rates

	         // Convert INR to USD
	         double amtInUSD = amt / exchangeRate;

	            // Set up purchase unit (product data and price)
	            PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
	            	    .amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(String.valueOf(amtInUSD)))
	            	    .description(course.getCourseName());
	            orderRequest.purchaseUnits(Collections.singletonList(purchaseUnit));

	            ApplicationContext appContext = new ApplicationContext()
	                    .brandName(user.getInstitutionName())
	                    .landingPage("BILLING")
	                    .cancelUrl(cancelUrl)
	                    .returnUrl(successUrl)
	                    .userAction("PAY_NOW");

	            orderRequest.applicationContext(appContext);

	            OrdersCreateRequest paypalOrderRequest = new OrdersCreateRequest().requestBody(orderRequest);
	            HttpResponse<Order> response = paypalClient.execute(paypalOrderRequest);

	            if (response.statusCode() == 201) {
	                Order order = response.result();

	                // Save order details to the database
	                Orderuser savedOrder = saveOrderDetails(
	                        course, user, amt, order.id(), "CREATED", user.getInstitutionName(),
	                        courseId, userId, installmentNumber, "PAYPAL");

	                // Respond with PayPal approval link
	                Map<String, String> approvalLink = new HashMap<>();
	                for (LinkDescription link : order.links()) {
	                    if ("approve".equals(link.rel())) {
	                        approvalLink.put("approvalUrl", link.href());
	                        approvalLink.put("orderid", order.id());
	                        break;
	                    }
	                }

	                return ResponseEntity.ok(approvalLink);
	            } else {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: PayPal Order creation failed");
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PayPal Payment details not found");
	        }
	    } catch (Exception e) {
	        logger.error("Error creating PayPal Checkout: ", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	

	public ResponseEntity<String> updatePayPalPayment(Map<String, String> requestData, String token) {
	    try {
	        String orderId = requestData.get("orderId");
	        String payerId = requestData.get("PayerID");
	        String paypalToken = requestData.get("token");

	        if (orderId == null || payerId == null || paypalToken == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order ID, PayerID, or Token is missing");
	        }

	        Optional<Orderuser> orderUserOptional = ordertablerepo.findByOrderId(orderId);
	        if (orderUserOptional.isPresent()) {
	            Orderuser orderUser = orderUserOptional.get();

	            // Create PayPalHttpClient instance
	            PayPalHttpClient client = createPayPalHttpClient(token);
                if(client.equals(null)) {
                	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error Getting Payment details");
                }
	            // Create request to retrieve payment details
	            OrdersGetRequest request = new OrdersGetRequest(paypalToken);
	            HttpResponse<Order> response = client.execute(request);

	            Order order = response.result();

	            if (order != null) {
	                // Extract details from order
	                String transactionId = order.id();
	                
	                // Parse the amount (String -> double)
	                String amountString = order.purchaseUnits().get(0).amountWithBreakdown().value();
	                double amountPaidInCents = Double.parseDouble(amountString) * 100; // Convert to cents (from dollars)
	                
	                // Get payment status
	                String status = order.status();

	                // Update the OrderUser entity with payment details
	                orderUser.setPaymentId(transactionId);
	                if(status.equals("APPROVED")) {
	                	 orderUser.setStatus("paid");	
	                }
	               
	                orderUser.setAmountReceived((int) amountPaidInCents); // Amount in cents
	                orderUser.setDate(new Date()); // Current date for simplicity

	                Orderuser savedOrder = ordertablerepo.save(orderUser);

	                // Assign course to user
	                Long courseId = orderUser.getCourseId();
	                Long userId = orderUser.getUserId();

	                Optional<Muser> optionalUser = muserRepository.findById(userId);
	                Optional<CourseDetail> optionalCourse = coursedetail.findById(courseId);

	                if (optionalUser.isPresent() && optionalCourse.isPresent()) {
	                    Muser user = optionalUser.get();
	                    CourseDetail course = optionalCourse.get();

	                    // Assign course to user if not already assigned
	                    if (status.equals("APPROVED") && !user.getCourses().contains(course) ) {
	                        user.getCourses().add(course);
	                        muserRepository.save(user);
	                    }

	                    // Send notifications
	                    this.notifiinstallment(courseId, userId);
	                    String courseUrl = course.getCourseUrl();
	                    String heading = "Payment Credited!";
	                    String link = "/payment/transactionHistory";
	                    String notiDescription = "Payment for " + course.getCourseName() + " was credited by "
	                            + user.getUsername() + " for installment " + savedOrder.getInstallmentnumber();

	                    Long notifyId = notiservice.createNotification("Payment", user.getUsername(), notiDescription,
	                            user.getEmail(), heading, link);

	                    if (notifyId != null) {
	                        notiservice.CommoncreateNotificationUser(notifyId, List.of("ADMIN"), user.getInstitutionName());
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
	                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(errorMessage.trim());
	                }
	            } else {
	                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("PayPal payment details not found");
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Order not found");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        logger.error("General Error: ", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error updating payment ID: " + e.getMessage());
	    }
	}

	private PayPalHttpClient createPayPalHttpClient(String token) {
		String email=jwtUtil.getUsernameFromToken(token);
		if(email.equals(null)) {
			return null;
		}
		String institutionName=muserRepository.findinstitutionByEmail(email);
		if(institutionName.equals(null)) {
			return null;
		}
		Optional<Paypalsettings> keys=paypalrepo.FindByInstitutionName(institutionName);
		if(!keys.isPresent()) {
			return null;
		}
		Paypalsettings key=keys.get();
	    // Set up PayPal environment (Sandbox or Production)
	    PayPalEnvironment environment = new PayPalEnvironment.Sandbox(key.getPaypal_client_id(), key.getPaypal_secret_key());
	    return new PayPalHttpClient(environment);
	}}

