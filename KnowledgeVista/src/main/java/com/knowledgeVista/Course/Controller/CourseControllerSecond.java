package com.knowledgeVista.Course.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.videoLessons;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Course.Repository.videoLessonRepo;
import com.knowledgeVista.License.licenseRepository;
import com.knowledgeVista.Payments.Orderuser;
import com.knowledgeVista.Payments.OrderuserRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.UserStats;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@RestController
public class CourseControllerSecond {
	@Autowired
	private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private JwtUtil jwtUtil;
	@Autowired
	private MuserRepositories muserRepository;	
 	@Autowired
	private videoLessonRepo lessonrepo;
	@Autowired
	private licenseRepository licencerepo;
	@Autowired
	private OrderuserRepo orderrepo;
	
	private static final Logger logger = LoggerFactory.getLogger(CourseControllerSecond.class);
	
	public ResponseEntity<?>getstoragedetails(String token){
		try {
		 if (!jwtUtil.validateToken(token)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
         String role = jwtUtil.getRoleFromToken(token);
         if(!"ADMIN".equals(role)) {
        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
         }
         String email=jwtUtil.getUsernameFromToken(token);
         Optional<Muser> opuser =muserRepository.findByEmail(email);
	     if(opuser.isPresent()) {
	    	 Muser user=opuser.get();
	    	String institution=user.getInstitutionName(); 
	    	Long totalStorageUsed = lessonrepo.findTotalSizeByInstitution(institution);
	    	Long maxStorageLimitBytes = licencerepo.FindstoragesizeByinstitution(institution) * (1024L * 1024 * 1024);

	    	// Calculate in bytes first
	    	Long balanceStorageBytes = maxStorageLimitBytes - (totalStorageUsed != null ? totalStorageUsed : 0);

	    	// Convert bytes to GB and ensure integer-only values
	    	long maxStorageLimitGB = maxStorageLimitBytes / (1024 * 1024 * 1024);
	    	long totalStorageUsedGB = (totalStorageUsed != null ? totalStorageUsed : 0) / (1024 * 1024 * 1024);
	    	long balanceStorageGB = balanceStorageBytes / (1024 * 1024 * 1024);

	    	// Prepare response
	    	Map<String, Object> response = new HashMap<>();

	    	// Check if values are less than 1 GB, if so, show in MB instead with no decimals
	    	if (maxStorageLimitGB < 1) {
	    	    response.put("total", String.format("%d MB", maxStorageLimitBytes / (1024 * 1024)));
	    	} else {
	    	    response.put("total", String.format("%d GB", maxStorageLimitGB));
	    	}

	    	if (totalStorageUsedGB < 1) {
	    	    response.put("StorageUsed", String.format("%d MB", (totalStorageUsed != null ? totalStorageUsed : 0) / (1024 * 1024)));
	    	} else {
	    	    response.put("StorageUsed", String.format("%d GB", totalStorageUsedGB));
	    	}

	    	if (balanceStorageGB < 1) {
	    	    response.put("balanceStorage", String.format("%d MB", balanceStorageBytes / (1024 * 1024)));
	    	} else {
	    	    response.put("balanceStorage", String.format("%d GB", balanceStorageGB));
	    	}
	    	return ResponseEntity.ok(response);


	    	
	    	}else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }

	} catch (Exception e) {
	         e.printStackTrace(); logger.error("", e);// You can replace this with logging framework like Log4j
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 
	}
	public double calculateRemainingAmount(Long userId) {
	    // Fetch user by ID
	    Muser user = muserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

	    double totalRemainingAmount = 0.0;
	    for (CourseDetail course : user.getCourses()) {
	        List<Orderuser> payments = orderrepo.findByUserIdAndCourseIdAndAmountReceivedGreaterThanzero(user.getUserId(), course.getCourseId());

	        int totalReceived = payments.stream()
	                                    .mapToInt(Orderuser::getAmountReceived)
	                                    .sum();
	        double remainingAmount = Math.max(0, course.getAmount() - totalReceived);  // Ensure no negative amounts

	        totalRemainingAmount += remainingAmount;

	    }

	    return totalRemainingAmount;
	}
	public ResponseEntity<?>getAllStudentCourseDetails( String token){
		  try {
		         if (!jwtUtil.validateToken(token)) {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		         }
		         String role = jwtUtil.getRoleFromToken(token);
		         if(!"ADMIN".equals(role)) {
		        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
		         }
		         String email=jwtUtil.getUsernameFromToken(token);
		         Optional<Muser> opuser =muserRepository.findByEmail(email);
			     if(opuser.isPresent()) {
			    	 Muser user=opuser.get();
			    	String institution=user.getInstitutionName();
			    	List<UserStats> studentstats=muserRepository.findStudentStatsByInstitutionName(institution);
			    	for(UserStats student :studentstats) {
			    		student.setPending(this.calculateRemainingAmount(student.getUserId()));
			    	}
			    	return ResponseEntity.ok(studentstats);
			    	
			      }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
		  } catch (Exception e) {
		         e.printStackTrace();logger.error("", e); // You can replace this with logging framework like Log4j
		         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		     }
		 }
	 
	public ResponseEntity<?>getAllTrainerhandlingUsersAndCourses( String token){
		  try {
		         if (!jwtUtil.validateToken(token)) {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		         }
		         String role = jwtUtil.getRoleFromToken(token);
		         if(!"ADMIN".equals(role)) {
		        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
		         }
		         String email=jwtUtil.getUsernameFromToken(token);
		         Optional<Muser> opuser =muserRepository.findByEmail(email);
			     if(opuser.isPresent()) {
			    	 Muser user=opuser.get();
			    	String institution=user.getInstitutionName();
			    	List<UserStats> trainerstats=muserRepository.findTrainerStatsByInstitutionName(institution);
			    	return ResponseEntity.ok(trainerstats);
			    	
			      }else {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			     }
		  } catch (Exception e) {
		         e.printStackTrace();logger.error("", e); // You can replace this with logging framework like Log4j
		         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		     }
		 }
	
	 
	 public ResponseEntity<List<CourseDetail>> popular( String token) {
	     try {
	         if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	         String role = jwtUtil.getRoleFromToken(token);
	         String email=jwtUtil.getUsernameFromToken(token);
	         String institution="";
		     Optional<Muser> opuser =muserRepository.findByEmail(email);
		     if(opuser.isPresent()) {
		    	 Muser user=opuser.get();
		    	 institution=user.getInstitutionName();
		    	 boolean adminIsactive=muserRepository.getactiveResultByInstitutionName("ADMIN", institution);
		   	    	if(!adminIsactive) {
		   	    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		   	    	}
		     }else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }
	         if("ADMIN".equals(role)) {
	         List<CourseDetail> allCoursesOrderedByUserCount = coursedetailrepository.findAllByInstitutionNameOrderByUserCountDesc(institution);
	         List<CourseDetail> top4Courses = allCoursesOrderedByUserCount.stream().limit(4).collect(Collectors.toList());
	        
	         top4Courses.forEach(course -> {
	        	 
	             course.setUsers(null);
	             course.setTrainer(null);
	             course.setVideoLessons(null);
	         });

	         return ResponseEntity.ok()
	             .contentType(MediaType.APPLICATION_JSON)
	             .body(top4Courses);
	         }
	         else {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
	     } catch (Exception e) {
	         e.printStackTrace();    logger.error("", e); // You can replace this with logging framework like Log4j
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }
	 
	 
	


}
