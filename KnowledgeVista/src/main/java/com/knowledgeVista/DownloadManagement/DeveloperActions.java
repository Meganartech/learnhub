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
	    private CustomerdownloadRepo customerdownloadRepo;
	    
	    @Autowired
	    private CustomerLeadsRepo customerleadRepo;
	    
	   
	 @PostMapping("/CustomerDownloads")
	    public ResponseEntity<?> saveCustomerDownloads(@RequestBody Customer_downloads customerdownloads) {
	        try {
	            

	            Customer_downloads custDown = new Customer_downloads();
	            custDown.setName(customerdownloads.getName());
	            custDown.setEmail(customerdownloads.getEmail());
	            custDown.setCountryCode(customerdownloads.getCountryCode());
	            custDown.setPhone(customerdownloads.getPhone());
	            custDown.setDescription(customerdownloads.getDescription());
	            custDown.setVersion(customerdownloads.getVersion());
	            custDown.setCourseCount(customerdownloads.getCourseCount());
	            custDown.setTrainerCount(customerdownloads.getTrainerCount());
	            custDown.setStudentCount(customerdownloads.getStudentCount());

	            customerdownloadRepo.save(custDown);

	            return ResponseEntity.ok().body("Saved Successfully");

	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with logging framework like Log4j
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }

	 @GetMapping("/getCustomerDownloads")
	    public ResponseEntity<List< Customer_downloads>> getCustomerdownloads() {
	        try {
	            List< Customer_downloads> customerdownloads = customerdownloadRepo.findAll();
	            return ResponseEntity.ok(customerdownloads);
	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with logging framework like Log4j
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	 
	
	 @DeleteMapping("/Customerdownload")
	    public ResponseEntity<?> deleteCustomerdownload(@RequestParam("id") Long id) {
	        try {
	            if (customerdownloadRepo.existsById(id)) {
	            	customerdownloadRepo.deleteById(id);
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
	 
	 //------------------CustomerLeads---------------------

	 @PostMapping("/CustomerLeads")
	    public ResponseEntity<?> saveCustomerleads(@RequestBody CustomerLeads customerLeads) {
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

	            customerleadRepo.save(custLead);

	            return ResponseEntity.ok().body("Saved Successfully");

	        } catch (Exception e) {
	            // Log any other exceptions for debugging purposes
	            e.printStackTrace(); // You can replace this with logging framework like Log4j
	            // Return an internal server error response
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	 
	 @GetMapping("/CustomerLeads")
	    public ResponseEntity<List<CustomerLeads>> getCustomerleads() {
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
	 
	 @DeleteMapping("/Customerlead")
	    public ResponseEntity<?> deleteCustomerlead(@RequestParam("id") Long id) {
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

