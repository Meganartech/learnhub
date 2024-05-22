package com.knowledgeVista.DownloadManagement;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.knowledgeVista.Course.Test.Repository.MusertestactivityRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import java.util.List;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/Developer")
public class DeveloperActions {
	 @Autowired
		private MuserRepositories muserrepositories;
		
	    @Autowired
	    private MusertestactivityRepo activityrepo;
	    
	    @Autowired
	    private CustomerdownloadRepo customerdownloadRepo;
	    
	    @Autowired
	    private CustomerLeadsRepo customerleadRepo;
	    
	 @GetMapping("/Delete/Trainer/{email}")
	 public ResponseEntity<?> deleteTrainer(@PathVariable String email) {
	      try {
	          // Validate the token
	         
	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("TRAINER".equals(user.getRole().getRoleName())) {
	                      // Clear user's courses and delete the user
	                	  user.getAllotedCourses().clear();
	                      user.getCourses().clear();
	                      muserrepositories.delete(user);
	                      return ResponseEntity.ok().body("{\"message\": \"Deleted Successfully\"}");
	                  } 
	                  return ResponseEntity.notFound().build();
	              } else {
	                  // Return not found if the user with the given email does not exist
	                  return ResponseEntity.notFound().build();
	              }
	          
	      } catch (Exception e) {
	          // Log any other exceptions for debugging purposes
	          e.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an internal server error response
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	      }
	  }
	  

	 @GetMapping("/Delete/User/{email}") 
	  public ResponseEntity<?> deleteStudent(@PathVariable String email) {
	      try {
	         	              Optional<Muser> existingUser = muserrepositories.findByEmail(email);
	              if (existingUser.isPresent()) {
	                  Muser user = existingUser.get();
	                  if ("USER".equals(user.getRole().getRoleName())) {
	                      // Clear user's courses and delete the user
	                      user.getCourses().clear();
	                      activityrepo.deleteByUser(user);
	                      muserrepositories.delete(user);
	                      return ResponseEntity.ok().body("{\"message\": \"Deleted Successfully\"}");
	                  } 

		                  // Return not found if the user with the given email does not exist
		                  return ResponseEntity.notFound().build();
	                  
	              } else {
	                  // Return not found if the user with the given email does not exist
	                  return ResponseEntity.notFound().build();
	              }
	          
	      } catch (Exception e) {
	          // Log any other exceptions for debugging purposes
	          e.printStackTrace(); // You can replace this with logging framework like Log4j
	          // Return an internal server error response
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	      }
	  }

   
	 @PostMapping("/CustomerEntry")
	    public ResponseEntity<?> saveCustomerEntry(@RequestBody CustomerLeads customerLeads) {
	        try {
	            CustomerLeads custLead = new CustomerLeads();
	            custLead.setName(customerLeads.getName());
	            custLead.setEmail(customerLeads.getEmail());
	            custLead.setCountryCode(customerLeads.getCountryCode());
	            custLead.setPhone(customerLeads.getPhone());
	            custLead.setDescription(customerLeads.getDescription());
	            custLead.setVersion(customerLeads.getVersion());
	            custLead.setCourseCount(customerLeads.getCourseCount());
	            custLead.setTrainerCount(customerLeads.getTrainerCount());
	            custLead.setStudentCount(customerLeads.getStudentCount());
	            custLead.setLicenseType(customerLeads.getLicenseType());
	            custLead.setLicenseValidity(customerLeads.getLicenseValidity());
	            custLead.setIsLicenseExpired(customerLeads.getIsLicenseExpired());

	            Customer_downloads custDown = new Customer_downloads();
	            custDown.setName(customerLeads.getName());
	            custDown.setEmail(customerLeads.getEmail());
	            custDown.setCountryCode(customerLeads.getCountryCode());
	            custDown.setPhone(customerLeads.getPhone());
	            custDown.setDescription(customerLeads.getDescription());
	            custDown.setVersion(customerLeads.getVersion());
	            custDown.setCourseCount(customerLeads.getCourseCount());
	            custDown.setTrainerCount(customerLeads.getTrainerCount());
	            custDown.setStudentCount(customerLeads.getStudentCount());

	            customerleadRepo.save(custLead);
	            customerdownloadRepo.save(custDown);

	            return ResponseEntity.ok().body("Saved Successfully");

	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with logging framework like Log4j
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	 @GetMapping("/CustomerEntries")
	    public ResponseEntity<List<CustomerLeads>> getCustomerEntries() {
	        try {
	            List<CustomerLeads> customerLeads = customerleadRepo.findAll();
	            return ResponseEntity.ok(customerLeads);
	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with logging framework like Log4j
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	 
	 @DeleteMapping("/CustomerEntry")
	    public ResponseEntity<?> deleteCustomerEntry(@RequestParam("id") Long id) {
	        try {
	            if (customerleadRepo.existsById(id)) {
	                customerleadRepo.deleteById(id);
	                return ResponseEntity.ok().body("Deleted Successfully");
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
	            }
	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with a logging framework like Log4j
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }


}

