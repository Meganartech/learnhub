package com.knowledgeVista.Payments.controller;

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
import com.knowledgeVista.Payments.Course_PartPayment_Structure;
import com.knowledgeVista.Payments.InstallmentDetails;
import com.knowledgeVista.Payments.Orderuser;
import com.knowledgeVista.Payments.Paymentsettings;
import com.knowledgeVista.Payments.Stripesettings;
import com.knowledgeVista.Payments.repos.OrderuserRepo;
import com.knowledgeVista.Payments.repos.PaymentsettingRepository;
import com.knowledgeVista.Payments.repos.Striperepo;
import com.knowledgeVista.Payments.repos.partpayrepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class PaymentIntegration {

	@Value("${currency}")
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
	@Autowired
	private Striperepo stripereop;
	
	@Autowired
	private PaymentIntegration2 payint2;
	
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
	
	private ResponseEntity<?> handleStripeCheckout(CourseDetail course, Muser user, Long amt,
			Long courseId, Long userId, Long installmentNumber, HttpServletRequest request) {
		try {
// Fetch Stripe settings for the institution
			Optional<Stripesettings> opdataList = stripereop.findByinstitutionName(user.getInstitutionName());
			if (opdataList.isPresent()) {
				Stripesettings data = opdataList.get();

				String clientBaseUrl = request.getHeader("Origin");
				if (clientBaseUrl == null) {
					String referer = request.getHeader("Referer");
					if (referer != null) {
						System.out.println("Client Base URL: " + referer);
// Extract base URL from the Referer
						clientBaseUrl = referer.split("/")[0] + "//" + referer.split("/")[2];
					}
				}
				Stripe.apiKey = data.getStripe_secret_key();
				System.out.println("Client Base URL: " + clientBaseUrl);

// Define success and cancel URLs
				String successUrl = clientBaseUrl + "/updatePayment";
				String cancelUrl = clientBaseUrl + "/dashboard/course";

// Set up product data for Stripe
				SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
						.builder().setName(course.getCourseName()).build();

// Set up price data  

				if ((amt * 100) < 5000 && "INR".equals(currency)) {
				    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				        .body("The transaction amount is too small. Please ensure the amount is at least ₹50.");
				}

				SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
						.setCurrency(currency).setUnitAmount(amt * 100) // Amount in cents
						.setProductData(productData).build();

// Set up line item
				SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder().setPriceData(priceData)
						.setQuantity(1L).build();

// Build Stripe session
				SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT)
						.setSuccessUrl(successUrl).setCancelUrl(cancelUrl).addLineItem(lineItem).build();

				Session session = Session.create(params);

// Check if the session ID is not null
				if (session.getId() != null) {
// Save order details to the database

// Respond with the session ID to the frontend
					Map<String, String> response = new HashMap<>();
					response.put("sessionId", session.getId());
					return ResponseEntity.ok(response);
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Session ID is null");
				}

			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stripe Payment details not found");
			}

		} catch (Exception e) {
			logger.error("Error creating Stripe Checkout: ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stripe keys are invalid. Please check your Stripe keys.");
		}
	}

	private ResponseEntity<?> handleRazorpayOrder(CourseDetail course, Muser user, Long amt, 
			Long courseId, Long userId, Long installmentumber) {
		try {
			Optional<Paymentsettings> opdataList = paymentsetting.findByinstitutionName(user.getInstitutionName());
			if (opdataList.isPresent()) {
				Paymentsettings data = opdataList.get();

				// Fetch Razorpay credentials
				String razorpayApiKey = data.getRazorpay_key();
				String razorpayApiSecret = data.getRazorpay_secret_key();
				String institution = data.getInstitutionName();

				// Initialize Razorpay client
				RazorpayClient client = new RazorpayClient(razorpayApiKey, razorpayApiSecret);

				// Create Razorpay order
				JSONObject orderRequest = new JSONObject();
				orderRequest.put("amount", amt * 100); // Convert amount to paisa
				orderRequest.put("currency", currency);
				orderRequest.put("receipt", "receipt_" + courseId);
				orderRequest.put("notes", new JSONObject().put("user_id", userId));
				Order order = client.orders.create(orderRequest);

				// Extract order details
				String orderId = order.get("id").toString();
				String status = order.get("status").toString();

				// Save order details to the database
				Orderuser savedOrder = saveOrderDetails(course, user, amt, orderId, status, institution, courseId,
						userId, installmentumber,"RAZORPAY");
				Map<String, Object> response = new HashMap<>();
				response.put("orderId", savedOrder.getOrderId());
				response.put("description", "Payment for course " + savedOrder.getCourseName() + " Rs." + amt);
				response.put("name", savedOrder.getCourseName() + " (Rs." + amt + ")");

				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Razorpay Payment details not found");
			}
		} catch (RazorpayException e) {
			logger.error("Error creating Razorpay order: ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Razorpay keys are invalid. Please check your Razorpay keys.");
		}
	}

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
public ResponseEntity<?>getordersummaryFull(Map<String, Long> requestData, String token){
	try {
		Long courseId = requestData.get("courseId");
		Long userId = requestData.get("userId");
		if (courseId == null || userId == null) {
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some Values are missing");
		}
		Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
		Optional<Muser> optionalUser = muserRepository.findById(userId);

		if (courseOptional.isPresent() && optionalUser.isPresent()) {
			
			Muser user = optionalUser.get();

			if ("ADMIN".equals(user.getRole().getRoleName()) || "TRAINER".equals(user.getRole().getRoleName())) {
				return ResponseEntity.badRequest().body("Students only can buy the course");
			}
			CourseDetail course = courseOptional.get();
            if (!user.getCourses().contains(course)) {
            	 Long seats = course.getNoofseats();
                 Long filled = course.getUserCount();
                 if (seats <= filled) {
                	 return ResponseEntity.badRequest().body("Seats are Filled For This Course");
                 }
            }
			List<Orderuser> orderuserlist = ordertablerepo.findAllByUserIDAndCourseID(userId, courseId, "paid");
			int amount = 0;
			Long amt = course.getAmount();
			for (Orderuser order : orderuserlist) {

				amount = amount + order.getAmountReceived();
			}
			if (amount == course.getAmount()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount already paid");
			} else {
				amt = course.getAmount() - amount;
			}
			 Map<String, Object> response = new HashMap<>();
	            response.put("amount", amt);
	            response.put("userId", user.getUserId());
				response.put("courseId", course.getCourseId());
	            response.put("courseAmount", course.getAmount());
	            response.put("coursename", course.getCourseName());
				response.put("url","/full/buyCourse/create");
	            response.put("paytype", "Full");
	            response.put("installment", 1);
			return ResponseEntity.ok(response);
		} else {
			// Return an error response if the course is not found
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot find course");
		}
	} catch (Exception e) {
		e.printStackTrace();
		logger.error("", e);
		;
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Error creating order: " + e.getMessage());
	}

}

public ResponseEntity<?>getOrderSummaryPart(Map<String, Long> requestData,  String token){
	try {
		Long courseId = requestData.get("courseId");
		Long userId = requestData.get("userId");
		if (courseId == null || userId == null) {
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some Values are missing");
		}
		Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
		Optional<Muser> optionalUser = muserRepository.findById(userId);

		if (courseOptional.isPresent() && optionalUser.isPresent()) {
			Muser user = optionalUser.get();

			if ("ADMIN".equals(user.getRole().getRoleName()) || "TRAINER".equals(user.getRole().getRoleName())) {
				return ResponseEntity.badRequest().body("Students only can buy the course");
			}
			CourseDetail course = courseOptional.get();
			 if (!user.getCourses().contains(course)) {
            	 Long seats = course.getNoofseats();
                 Long filled = course.getUserCount();
                 if (seats <= filled) {
                	 return ResponseEntity.badRequest().body("Seats are Filled For This Course");
                 }
            }
			List<Orderuser> orderuserlist = ordertablerepo.findAllByUserIDAndCourseID(userId, courseId, "paid");
			int amount = 0;
			for (Orderuser order : orderuserlist) {

				amount = amount + order.getAmountReceived();
			}
			if (amount == course.getAmount()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All installments  paid");
			}
			Optional<Course_PartPayment_Structure> opPartpay = partpayrepo.findBycourse(course);
			if (opPartpay.isPresent()) {

				Course_PartPayment_Structure partpay = opPartpay.get();
				List<InstallmentDetails> installmentslist = partpay.getInstallmentDetail();
				int count = ordertablerepo.findCountByUserIDAndCourseID(userId, courseId, "paid");
				int installmentlength = installmentslist.size();
				if (installmentlength > count) {
					InstallmentDetails installment = installmentslist.get(count);
					Long installmentamount = installment.getInstallmentAmount();
					Map<String, Object> response = new HashMap<>();
					response.put("userId", user.getUserId());
					response.put("courseId", course.getCourseId());
					response.put("url", "/part/buyCourse/create");
					response.put("amount", installmentamount);
		            response.put("courseAmount", course.getAmount());
		            response.put("coursename", course.getCourseName());
		            response.put("paytype", "PART");
		            response.put("installment", installment.getInstallmentNumber());
				return ResponseEntity.ok(response);

					
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All installments paid");
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Part Payment Details UnAvailable");
			}
		} else {
			// Return an error response if the course is not found
			return ResponseEntity.notFound().build();
		}
	} catch (Exception e) {
		e.printStackTrace();
		logger.error("", e);
		;
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Error creating order: " + e.getMessage());
	}

}
	public ResponseEntity<?> createOrderfull(Map<String, Long> requestData, String gateway, String token,
			HttpServletRequest request) {
		try {
			Long courseId = requestData.get("courseId");
			Long userId = requestData.get("userId");
			if (courseId == null || userId == null) {
			    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some Values are missing");
			}
			Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
			Optional<Muser> optionalUser = muserRepository.findById(userId);

			if (courseOptional.isPresent() && optionalUser.isPresent()) {
				Muser user = optionalUser.get();

				if ("ADMIN".equals(user.getRole().getRoleName()) || "TRAINER".equals(user.getRole().getRoleName())) {
					return ResponseEntity.badRequest().body("Students only can buy the course");
				}
				CourseDetail course = courseOptional.get();
				List<Orderuser> orderuserlist = ordertablerepo.findAllByUserIDAndCourseID(userId, courseId, "paid");
				int amount = 0;
				Long amt = course.getAmount();
				for (Orderuser order : orderuserlist) {

					amount = amount + order.getAmountReceived();
				}
				if (amount == course.getAmount()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount already paid");
				} else {
					amt = course.getAmount() - amount;
				}
				if ("RAZORPAY".equals(gateway)) {
					return handleRazorpayOrder(course, user, amt,  courseId, userId, 1L);
				} else if ("STRIPE".equals(gateway)) {
					return handleStripeCheckout(course, user, amt, courseId, userId, 1L,request);
				}else if("PAYPAL".equals(gateway)){
					return payint2.handlePaypalCheckout(course, user, amt, courseId, userId, 1L,request);
				}else {
				
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported payment gateway");
				}

			} else {
				// Return an error response if the course is not found
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot find course");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			;
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating order: " + e.getMessage());
		}
	}

	public ResponseEntity<?> createOrderPart(Map<String, Long> requestData, String gateway, String token) {
		try {
			Long courseId = requestData.get("courseId");
			Long userId = requestData.get("userId");
			if (courseId == null || userId == null) {
			    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some Values are missing");
			}
			Optional<CourseDetail> courseOptional = coursedetail.findById(courseId);
			Optional<Muser> optionalUser = muserRepository.findById(userId);

			if (courseOptional.isPresent() && optionalUser.isPresent()) {
				Muser user = optionalUser.get();

				if ("ADMIN".equals(user.getRole().getRoleName()) || "TRAINER".equals(user.getRole().getRoleName())) {
					return ResponseEntity.badRequest().body("Students only can buy the course");
				}
				CourseDetail course = courseOptional.get();

				List<Orderuser> orderuserlist = ordertablerepo.findAllByUserIDAndCourseID(userId, courseId, "paid");
				int amount = 0;
				for (Orderuser order : orderuserlist) {

					amount = amount + order.getAmountReceived();
				}
				if (amount == course.getAmount()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All installments  paid");
				}
				Optional<Course_PartPayment_Structure> opPartpay = partpayrepo.findBycourse(course);
				if (opPartpay.isPresent()) {

					Course_PartPayment_Structure partpay = opPartpay.get();
					List<InstallmentDetails> installmentslist = partpay.getInstallmentDetail();
					int count = ordertablerepo.findCountByUserIDAndCourseID(userId, courseId, "paid");
					int installmentlength = installmentslist.size();
					if (installmentlength > count) {
						InstallmentDetails installment = installmentslist.get(count);
						Long installmentamount = installment.getInstallmentAmount();
						if (getpaydetails(token) != null) {
							if ("RAZORPAY".equals(gateway)) {
								return handleRazorpayOrder(course, user, installmentamount, courseId, userId,
										installment.getInstallmentNumber());
							} else {
								return ResponseEntity.status(HttpStatus.BAD_REQUEST)
										.body("Unsupported payment gateway");
							}
//			                      String razorpayApiKey= getpaydetails(token).getRazorpay_key();
//			                      String razorpayApiSecret=getpaydetails(token).getRazorpay_secret_key();
//			                      String institution=getpaydetails(token).getInstitutionName();
//			                      RazorpayClient client = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
//			                      JSONObject orderRequest = new JSONObject();
//			                      orderRequest.put("amount", installmentamount * 100); 
//			                      orderRequest.put("currency", currency);
//			                      orderRequest.put("receipt", "receipt_" + courseId);
//			                      orderRequest.put("notes", new JSONObject().put("user_id", userId));
//			                      
//			                      Order order = client.orders.create(orderRequest);
//						             String orderId = order.get("id").toString();
//								     String status = order.get("status").toString();
//						             Orderuser ordertable = new Orderuser();
//						             ordertable.setCourseId(courseId);
//						             ordertable.setOrderId(orderId);
//						             ordertable.setUserId(userId);
//						             ordertable.setCourseName(course.getCourseName());
//						             ordertable.setDate(new java.util.Date());
//						             ordertable.setStatus(status);
//						             ordertable.setInstitutionName(institution);
//						             ordertable.setEmail(user.getEmail());
//						             ordertable.setUsername(user.getUsername());
//						             ordertable.setInstallmentnumber(installment.getInstallmentNumber());
//
//						             Orderuser ordersaved = ordertablerepo.save(ordertable); 
//						             String description="payment for course "+ordersaved.getCourseName()+"  Rs. "+installmentamount+ "  for the installment "+ ordersaved.getInstallmentnumber();
//						             String name= ordersaved.getCourseName() + "(installment " + ordersaved.getInstallmentnumber() +")" ;
//						             Map<String, Object> response = new HashMap<>();
//							            response.put("orderId", orderId);
//							            response.put("description", description);
//							            response.put("name", name);
//							            return ResponseEntity.ok(response);

						} else {
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
									.body("Payment details not found");
						}
					} else {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All installments paid");
					}
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Part Payment Details UnAvailable");
				}
			} else {
				// Return an error response if the course is not found
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			;
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating order: " + e.getMessage());
		}
	}
		public ResponseEntity<String> updateStripepaymentid(Map<String, String> requestData, String token) {
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

		                // Proceed with the rest of your logic for notifications, course updates, etc.
		                Long courseId = orderUser.getCourseId();
		                Long userId = orderUser.getUserId();

		                Optional<Muser> optionalUser = muserRepository.findById(userId);
		                Optional<CourseDetail> optionalCourse = coursedetail.findById(courseId);

		                if (optionalUser.isPresent() && optionalCourse.isPresent()) {
		                	Muser user = optionalUser.get();
							String institution = user.getInstitutionName();
							CourseDetail course = optionalCourse.get();

							// for notification
							List<Muser> trainers = course.getTrainer();
							List<Long> ids = new ArrayList<>();

							for (Muser trainer : trainers) {
								ids.add(trainer.getUserId());
							}

							if (status.equals("succeeded") &&!user.getCourses().contains(course)) {
								user.getCourses().add(course);
								muserRepository.save(user);
							}
							this.notifiinstallment(courseId, userId);
							String courseUrl = course.getCourseUrl();
							String heading = " Payment Credited !";
							String link = "/payment/transactionHitory";
							String linkfortrainer = "/payment/trainer/transactionHitory";
							String notidescription = "The payment for " + course.getCourseName() + " was Credited  By "
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
		                        errorMessage += "User with ID " + userId + " not found. ";
		                    }
		                    if (!optionalCourse.isPresent()) {
		                        errorMessage += "Course with ID " + courseId + " not found. ";
		                    }
		                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.trim());
		                }

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

	
	public ResponseEntity<String> updatePaymentId(Map<String, String> requestData, String token) {
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

					Long courseId = orderUser.getCourseId();
					Long userId = orderUser.getUserId();

					Optional<Muser> optionalUser = muserRepository.findById(userId);
					Optional<CourseDetail> optionalCourse = coursedetail.findById(courseId);

					if (optionalUser.isPresent() && optionalCourse.isPresent()) {
						Muser user = optionalUser.get();
						String institution = user.getInstitutionName();
						CourseDetail course = optionalCourse.get();

						// for notification
						List<Muser> trainers = course.getTrainer();
						List<Long> ids = new ArrayList<>();

						for (Muser trainer : trainers) {
							ids.add(trainer.getUserId());
						}

						if (!user.getCourses().contains(course)) {
							user.getCourses().add(course);
							muserRepository.save(user);
						}
						this.notifiinstallment(courseId, userId);
						String courseUrl = course.getCourseUrl();
						String heading = " Payment Credited !";
						String link = "/payment/transactionHitory";
						String linkfortrainer = "/payment/trainer/transactionHitory";
						String notidescription = "The payment for " + course.getCourseName() + " was Credited  By "
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

}
