package com.knowledgeVista.Batch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgeVista.Batch.Batch;
import com.knowledgeVista.Batch.BatchDto;
import com.knowledgeVista.Batch.Repo.BatchRepository;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Controller.CourseController;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

@Service
public class BatchService {

    @Autowired
    private CourseDetailRepository courseDetailRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MuserRepositories muserRepo;
    @Autowired
    private BatchRepository batchrepo;
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    public List<Map<String, Object>> searchCourses(String courseName, String token) {
        // Extract email from the JWT token
        String email = jwtUtil.getUsernameFromToken(token);

        // Retrieve the institution name associated with the email
        String institutionName = muserRepo.findinstitutionByEmail(email);

        // Query the course details repository
        List<Object[]> results = courseDetailRepository.searchCourseIdAndNameByCourseNameByInstitution(courseName, institutionName);

        // Convert List<Object[]> to List<Map<String, Object>>
        return results.stream()
            .map(row -> Map.of(
                "courseId", row[0],    // Map courseId to the first element of the row
                "courseName", row[1],   // Map courseName to the second element of the row
                		"amount",row[2]
            ))
            .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> searchbatch(String batchTitle, String token) {
        // Extract email from the JWT token
        String email = jwtUtil.getUsernameFromToken(token);

        // Retrieve the institution name associated with the email
        String institutionName = muserRepo.findinstitutionByEmail(email);

        // Query the course details repository
        List<Object[]> results = batchrepo.searchBatchByBatchNameAndInstitution(batchTitle, institutionName);

        // Convert List<Object[]> to List<Map<String, Object>>
        return results.stream()
            .map(row -> Map.of(
                "id", row[0],    // Map courseId to the first element of the row
                "batchId", row[1],   // Map courseName to the second element of the row
                		"batchTitle",row[2]
            ))
            .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> searchTrainers(String courseName, String token) {
        // Extract email from the JWT token
        String email = jwtUtil.getUsernameFromToken(token);

        // Retrieve the institution name associated with the email
        String institutionName = muserRepo.findinstitutionByEmail(email);

        // Query the course details repository
        List<Object[]> results = muserRepo.searchTrainerIddAndTrainerNameByInstitution(courseName, institutionName);

        // Convert List<Object[]> to List<Map<String, Object>>
        return results.stream()
            .map(row -> Map.of(
                "userId", row[0],    // Map courseId to the first element of the row
                "username", row[1]   // Map courseName to the second element of the row
            ))
            .collect(Collectors.toList());
    }
   //==================================save Batch======================================
   
    public ResponseEntity<?>SaveBatch(String batchTitle,LocalDate startDate,LocalDate endDate,Long noofSeats,Long amount ,String coursesJson,String trainersJson, MultipartFile batchImage ,String token){
    	try {
    		if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }

	         String role = jwtUtil.getRoleFromToken(token);
	         String email = jwtUtil.getUsernameFromToken(token);
	         String institutionName=muserRepo.findinstitutionByEmail(email);
	         if(institutionName==null) {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User Institution Not Found");
	         }
	         if("ADMIN".equals(role)||"TRAINER".equals(role)) {

    		Batch batch=new Batch();
    		batch.setBatchTitle(batchTitle);
    		batch.setInstitutionName(institutionName);
            batch.setStartDate( startDate);
           batch.setEndDate( endDate);
           batch.setNoOfSeats( noofSeats);
           batch.setAmount(amount);
           ObjectMapper objectMapper = new ObjectMapper();
      	 List<Map<String, Object>> courses = objectMapper.readValue(coursesJson, List.class);
           List<Map<String, Object>> trainers = objectMapper.readValue(trainersJson, List.class);
             // Debugging logs
             System.out.println("Courses: " + courses);
             System.out.println("Trainers: " + trainers);
             List<CourseDetail> courseDetails = new ArrayList<>(); // List to hold valid CourseDetail objects

             for (Map<String, Object> course : courses) {
            	 Long courseId = ((Number) course.get("courseId")).longValue();

                 // Query the CourseDetail repository to find the course by courseId
                 Optional<CourseDetail> optionalCourseDetail = courseDetailRepository.findById(courseId);

                 // If the course is found, add it to the list
                 if (optionalCourseDetail.isPresent()) {
                     CourseDetail courseDetail = optionalCourseDetail.get();
                     courseDetails.add(courseDetail);  // Add the found course to the list
                 } else {
                	 logger.error("Course with ID " + courseId + " not found. Skipping...");
                	 System.out.println("Course with ID " + courseId + " not found. Skipping...");
                 }
             }
               batch.setCourses(courseDetails);
               List<Muser> trainerlist = new ArrayList<>();
               for (Map<String, Object> trainer : trainers) {
              	 Long userid = ((Number) trainer.get("userId")).longValue();

                   // Query the CourseDetail repository to find the course by courseId
                   Optional<Muser> opuser = muserRepo.findtrainerByid(userid);

                   // If the course is found, add it to the list
                   if (opuser.isPresent()) {
                       Muser user = opuser.get();
                       trainerlist.add(user);  // Add the found course to the list
                   } else {
                  	 logger.error("Course with ID " + userid + " not found. Skipping...");
                  	 System.out.println("Course with ID " + userid + " not found. Skipping...");
                   }
               }
               batch.setTrainers(trainerlist);
               if (batchImage != null && !batchImage.isEmpty()) {
                   batch.setBatchImage(batchImage.getBytes());
               }

             // Your business logic to save the batch would go here
               batchrepo.save(batch);
               

             return ResponseEntity.ok("Batch saved successfully!");
	         }else {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Student Cannot add Batch");
	         }
    	}catch (Exception e) {
    		 logger.error("Exception occurs in save Batch", e);;
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }
 //=================================Save Batch   for CourseCreation===========================
    public ResponseEntity<?>SaveBatchforCourseCreation(String batchTitle,LocalDate startDate,LocalDate endDate,String token){
    	try {
    		if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }

	         String role = jwtUtil.getRoleFromToken(token);
	         String email = jwtUtil.getUsernameFromToken(token);
	         String institutionName=muserRepo.findinstitutionByEmail(email);
	         if(institutionName==null|| institutionName.isEmpty()) {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User Institution Not Found");
	         }
	         if("ADMIN".equals(role)||"TRAINER".equals(role)) {

    		Batch batch=new Batch();
    		batch.setBatchTitle(batchTitle);
    		batch.setInstitutionName(institutionName);
            batch.setStartDate( startDate);
           batch.setEndDate( endDate);
      
             Batch savedBatch=batchrepo.save(batch);
               

             return ResponseEntity.ok(savedBatch);
	         }else {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Student Cannot add Batch");
	         }
    	}catch (Exception e) {
    		 logger.error("Exception occurs in save Batch", e);;
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }

    
    
    //=============================Edit Batch==============================
//    public ResponseEntity<?>SaveBatch(Map<String, Object> payload ,String token){
//    	try {
//    		if (!jwtUtil.validateToken(token)) {
//	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//	         }
//
//	         String role = jwtUtil.getRoleFromToken(token);
//	         String email = jwtUtil.getUsernameFromToken(token);
//	         String institutionName=muserRepo.findinstitutionByEmail(email);
//	         if(institutionName==null) {
//	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User Institution Not Found");
//	         }
//	         if("ADMIN".equals(role)||"TRAINER".equals(role)) {
//    		Batch batch=new Batch();
//    		batch.setBatchTitle((String) payload.get("batchTitle"));
//    		batch.setInstitutionName(institutionName);
//            batch.setStartDate( LocalDate.parse((String) payload.get("startDate")));
//           batch.setEndDate( LocalDate.parse((String) payload.get("endDate")));
//           batch.setNoOfSeats( Long.parseLong((String) payload.get("noOfSeats")));
//           batch.setAmount( Long.parseLong((String) payload.get("amount")));
//             List<Map<String, Object>> courses = (List<Map<String, Object>>) payload.get("courses");
//             List<Map<String, Object>> trainers = (List<Map<String, Object>>) payload.get("trainers");
//
//             // Debugging logs
//             System.out.println("Courses: " + courses);
//             System.out.println("Trainers: " + trainers);
//             List<CourseDetail> courseDetails = new ArrayList<>(); // List to hold valid CourseDetail objects
//
//             for (Map<String, Object> course : courses) {
//            	 Long courseId = ((Number) course.get("courseId")).longValue();
//
//                 // Query the CourseDetail repository to find the course by courseId
//                 Optional<CourseDetail> optionalCourseDetail = courseDetailRepository.findById(courseId);
//
//                 // If the course is found, add it to the list
//                 if (optionalCourseDetail.isPresent()) {
//                     CourseDetail courseDetail = optionalCourseDetail.get();
//                     courseDetails.add(courseDetail);  // Add the found course to the list
//                 } else {
//                	 logger.error("Course with ID " + courseId + " not found. Skipping...");
//                	 System.out.println("Course with ID " + courseId + " not found. Skipping...");
//                 }
//             }
//               batch.setCourses(courseDetails);
//               List<Muser> trainerlist = new ArrayList<>();
//               for (Map<String, Object> trainer : trainers) {
//              	 Long userid = ((Number) trainer.get("userId")).longValue();
//
//                   // Query the CourseDetail repository to find the course by courseId
//                   Optional<Muser> opuser = muserRepo.findtrainerByid(userid);
//
//                   // If the course is found, add it to the list
//                   if (opuser.isPresent()) {
//                       Muser user = opuser.get();
//                       trainerlist.add(user);  // Add the found course to the list
//                   } else {
//                  	 logger.error("Course with ID " + userid + " not found. Skipping...");
//                  	 System.out.println("Course with ID " + userid + " not found. Skipping...");
//                   }
//               }
//               batch.setTrainers(trainerlist);
//             // Your business logic to save the batch would go here
//               batchrepo.save(batch);
//               
//
//             return ResponseEntity.ok("Batch saved successfully!");
//	         }else {
//	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Student Cannot add Batch");
//	         }
//    	}catch (Exception e) {
//    		 logger.error("Exception occurs in save Batch", e);;
//    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//    }
    //==========================Get Batch================
    public ResponseEntity<?>GetBatch(Long id,String token){
    	try {
   		 String role = jwtUtil.getRoleFromToken(token);
	         String email = jwtUtil.getUsernameFromToken(token);
	         String institutionName=muserRepo.findinstitutionByEmail(email);
	         if(institutionName==null) {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User Institution Not Found");
	         }
	        Optional<BatchDto> opbatch=batchrepo.findBatchDtoByIdAndInstitutionName(id, institutionName);
	        if(opbatch.isPresent()) {
	        	BatchDto batch=opbatch.get();
	        	return ResponseEntity.ok(batch);
	        }else {
	        	return ResponseEntity.status(HttpStatus.NO_CONTENT).body("batch Not Found");
	        }
	         
    	} catch (Exception e) {
            logger.error("Exception occurred while deleting batch with ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the batch.");
        }
    }
   //===================Get ALl Batch===========================
    public ResponseEntity<?>GetAllBatch(String token){
    	try {
   		 String role = jwtUtil.getRoleFromToken(token);
	         String email = jwtUtil.getUsernameFromToken(token);
	         String institutionName=muserRepo.findinstitutionByEmail(email);
	         if(institutionName==null) {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User Institution Not Found");
	         }
	        List<BatchDto> batch=batchrepo.findAllBatchDtosByInstitution(institutionName);
	        	return ResponseEntity.ok(batch);
	        
	         
    	} catch (Exception e) {
            logger.error("Exception occurred while deleting batch with ID " +e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the batch.");
        }
    }

   //==============================Delete Batch================================
    @Transactional
    public ResponseEntity<?> deleteBatchById(Long id, String token) {
    	try {
    		 String role = jwtUtil.getRoleFromToken(token);
	         String email = jwtUtil.getUsernameFromToken(token);
	         String institutionName=muserRepo.findinstitutionByEmail(email);
	         if(institutionName==null) {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User Institution Not Found");
	         }
	         if("ADMIN".equals(role)||"TRAINER".equals(role)) {
    	
    	
        // Check if the batch exists
        Optional<Batch> optionalBatch = batchrepo.findBatchByIdAndInstitutionName(id,institutionName);
        if (optionalBatch.isEmpty()) {
            // Return 204 No Content if batch is not found
            return ResponseEntity.noContent().build();
        }

     
            Batch batch = optionalBatch.get();

            // Remove the mappings for courses and trainers
            batch.getCourses().clear();
            batch.getTrainers().clear();

            // Save the batch to update the mappings in the database
            batchrepo.save(batch);

            // Now safely delete the batch
            batchrepo.deleteById(id);

            return ResponseEntity.ok("Batch deleted successfully!");
        }else {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Students Cannot Delete Batch");
        }
        } catch (Exception e) {
            logger.error("Exception occurred while deleting batch with ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the batch.");
        }
    }

}
