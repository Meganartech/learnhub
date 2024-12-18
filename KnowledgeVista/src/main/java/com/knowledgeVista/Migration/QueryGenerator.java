package com.knowledgeVista.Migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Course.certificate.certificateRepo;
import com.knowledgeVista.License.Madmin_Licence;
import com.knowledgeVista.License.licenseRepository;
import com.knowledgeVista.License.mAdminLicenceRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;

@RestController
public class QueryGenerator {
	
	
	@Autowired
	public MuserRepositories muser;
	
	@Autowired
	public certificateRepo certificatere;
	
	@Autowired
	public CourseDetailRepository Course;
	
	@Autowired
	private mAdminLicenceRepo madminRepository;
	
	@Autowired
	private licenseRepository licenseRepository;
	
	@Value("${upload.licence.directory}")
    private String path;
   
	@Value("${upload.backup}")
    private String backupPath;
	
	 @GetMapping("/switch-database")
	 public Madmin_Licence generateInsertStatements() {
	        // Fetch all Muser records
		 try {
//			 List<Muser> Muser = muser.findAll();
			 String institutionName = "Admin";
			 
			 // Add data based on the Admin name for user details  (muser table)
//			 List<MuserMigrationDto>test= muser.findAllByInstitutionNameDto("Admin");
//		        this.writeDataToFile(test, "Muser");
		        
		     // Add data based on the InstitutionName name for certificate(certificate table)
//		     Optional<certificate> certificate=certificatere.findByInstitution(institutionName);
//		        this.writeDataToFile(Certificate, "Certificate");
		        
		     // Add data based on the InstitutionName name for certificate(certificate table) 
//		        List<CourseDetailDto>CourseDetail=Course.findAllByInstitutionNameDto(institutionName);
//		        this.writeDataToFile(CourseDetail, "CourseDetail");

//		     // Add data based on the InstitutionName name for license(license table) 
//			 Optional<License> License= licenseRepository.findByinstitution(institutionName);
//		     this.writeDataToFile(License, "License");
		        
		     // Add data based on the InstitutionName name for license(license table) 
//			 Optional<madminRepository> License= madminRepository.findByInstitutionName(institutionName);
//			 Madmin mad=new Madmin();
			 Madmin_Licence Madmin_Licence = madminRepository.findByInstitutionName(institutionName);
			 List<Madmin_Licence> madminLicence = new ArrayList<>();
			 if (Madmin_Licence != null) {
			     madminLicence.add(Madmin_Licence);
			 }
			 this.writeDataToFile(madminLicence, "MadminLicence");
		        
		        
//			 muser.findByInstitutionName(institutionName)
//		        .ifPresentOrElse(
//		            user -> System.out.println("Found user: " + user.getUsername()),
//		            () -> System.out.println("No user found for institution: " + institutionName)
//		        );
		        
		        
		        // Write the users list to the JSON file
//		        ObjectMapper objectMapper = new ObjectMapper();
//		        objectMapper.registerModule(new JavaTimeModule()); // Register the JSR310 module
//		        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
//		        objectMapper.writeValue(outputFile, users);
		        // Log the output file location
//		        System.out.println("Data successfully saved to: " + outputFile.getAbsolutePath());
		        return Madmin_Licence;
			} catch (Exception e) {
			    throw new RuntimeException("Failed to access field: ", e);
//			    return e;
			}
	    }
	 

	 public void ensureBackupDirectoryExists() {
	        File backupDir = new File(backupPath);
	        if (!backupDir.exists()) {
	            boolean isCreated = backupDir.mkdirs();
	            if (isCreated) {
	                System.out.println("Backup directory created: " + backupPath);
	            } else {
	                System.err.println("Failed to create backup directory: " + backupPath);
	            }
	        } else {
	            System.out.println("Backup directory already exists: " + backupPath);
	        }
	    }
	 
	 @GetMapping("/load-users")
	    public  List<Muser>  loadUsersFromJsonFile() {
	        // Specify the path to the JSON file
	        String directoryPath = path; // Set this to the directory where your file is saved
	        String fileName = "admin.json";
	        File inputFile = new File(directoryPath, fileName);
	        try {
	            // Ensure the file exists before attempting to read
	            if (!inputFile.exists()) {
	            	System.out.println("File not found------------------------ ");
	            }
	            // Read the JSON file into a list of Muser objects
	            ObjectMapper objectMapper = new ObjectMapper();
	            objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTime module
	            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT); // Handle empty strings gracefully
	            List<Muser> users = objectMapper.readValue(inputFile, objectMapper.getTypeFactory().constructCollectionType(List.class, Muser.class));
	            // Save the users into the database
	            muser.saveAll(users);
	            // Return success message
	            return users;
	        } catch (IOException e) {
	            // Handle any errors that occur during file reading or database saving
	            e.printStackTrace();
	            throw new RuntimeException("Failed to access field: ", e);
	        }
	    }
	 
//	 public  List<Muser>  JsonFileToDataBase() {
//	        // Specify the path to the JSON file
//	        String directoryPath = path; // Set this to the directory where your file is saved
//	        String fileName = "admin.json";
//	        File inputFile = new File(directoryPath, fileName);
//	        try {
//	            // Ensure the file exists before attempting to read
//	            if (!inputFile.exists()) {
//	            	System.out.println("File not found------------------------ ");
//	            }
//	            // Read the JSON file into a list of Muser objects
//	            ObjectMapper objectMapper = new ObjectMapper();
//	            objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTime module
//	            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT); // Handle empty strings gracefully
//	            List<Muser> users = objectMapper.readValue(inputFile, objectMapper.getTypeFactory().constructCollectionType(List.class, Muser.class));
//	            // Save the users into the database
//	            muser.saveAll(users);
//	            // Return success message
//	            return users;
//	        } catch (IOException e) {
//	            // Handle any errors that occur during file reading or database saving
//	            e.printStackTrace();
//	            throw new RuntimeException("Failed to access field: ", e);
//	        }
//	    }
	 
//	 public List<Muser> DataBaseToJsonFile() {
//	        // Fetch all Muser records
//		 try {
//			 List<Muser> users = muser.findAll();
//		        // Define the directory and file path
//		        String directoryPath = path; // Specify your desired directory path
//		        System.out.println("Directory path: " + directoryPath);
//		        Path directory = Paths.get(directoryPath);
//		        // Ensure the directory exists
//		        if (!Files.exists(directory)) {
//		            Files.createDirectories(directory);
//		        }
//		        // Define the output file name
//		        String fileName = "admin.json";
//		        File outputFile = new File(directoryPath, fileName);
//		        // Write the users list to the JSON file
//		        ObjectMapper objectMapper = new ObjectMapper();
//		        objectMapper.registerModule(new JavaTimeModule()); // Register the JSR310 module
//		        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
//		        objectMapper.writeValue(outputFile, users);
//		        // Log the output file location
//		        System.out.println("Data successfully saved to: " + outputFile.getAbsolutePath());
//		        return users;
//			} catch (Exception e) {
//			    throw new RuntimeException("Failed to access field: ", e);
////			    return e;
//			}
//	    }
	 
	 
	 public <T> void writeDataToFile(T data, String fileName) {
	        try {
	        	  String directoryPath = backupPath; // Specify your desired directory path
	        	Path directory = Paths.get(directoryPath);
		        // Ensure the directory exists
		        if (!Files.exists(directory)) {
		            Files.createDirectories(directory);
		        }
		        // Define the output file name
		        fileName += ".json";
		        File outputFile = new File(directoryPath, fileName);
	            // Initialize ObjectMapper for JSON serialization
	            ObjectMapper objectMapper = new ObjectMapper();
	            objectMapper.registerModule(new JavaTimeModule()); // For Java 8 date-time support
	            objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON

	            // Handle different types of input
	            if (data instanceof List<?>) {
	                // If the data is a list, directly serialize it
	                objectMapper.writeValue(outputFile, data);
	            } else if (data instanceof Optional<?>) {
	                Optional<?> optionalData = (Optional<?>) data;
	                if (optionalData.isPresent()) {
	                    objectMapper.writeValue(outputFile, optionalData.get());
	                } else {
	                    throw new IllegalArgumentException("Optional value is empty!");
	                }
	             
	            } else {
	                throw new IllegalArgumentException("Unsupported data type: " + data.getClass());
	            }

	            System.out.println("Data successfully saved to: " + outputFile.getAbsolutePath());
	        } catch (Exception e) {
	            throw new RuntimeException("Failed to write data to file", e);
	        }
	 }

	 
	 
}
