package com.knowledgeVista.Migration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;

@RestController
public class QueryGenerator {
	
	
	@Autowired
	public MuserRepositories muser;
	
	@Value("${upload.licence.directory}")
    private String path;
    
	
	 @GetMapping("/switch-database")
	 public List<Muser> generateInsertStatements() {
	        // Fetch all Muser records
		 try {
			 List<Muser> users = muser.findAll();
//			 Muser user1 = new Muser();
//		        user1.setUserId(1L);
//		        user1.setUsername("SYSADMIN");
//		        user1.setPsw("12345678");
//		        user1.setEmail("sysadmin@gmail.com");
//		        user1.setDob(java.time.LocalDate.of(1990, 1, 1));
//		        user1.setPhone("1234567890");
//		        user1.setSkills("Java,Spring");
//		        user1.setInstitutionName("Meganartech");
//		        user1.setCountryCode("+91");
//		        user1.setIsActive(true);
//		        user1.setLastactive(java.time.LocalDateTime.now());
//
//		        Muser user2 = new Muser();
//		        user2.setUserId(3L);
//		        user2.setUsername("admin1");
//		        user2.setPsw("Admin1@123");
//		        user2.setEmail("admin1@gmail.com");
//		        user2.setDob(java.time.LocalDate.of(2005, 6, 16));
//		        user2.setPhone("9876545678");
//		        user2.setSkills("");
//		        user2.setInstitutionName("sample");
//		        user2.setCountryCode("+91");
//		        user2.setIsActive(true);
//
//		        List<Muser> users = Arrays.asList(user1, user2);
		        
		        // Define the directory and file path
		        String directoryPath = path; // Specify your desired directory path
		        System.out.println("Directory path: " + directoryPath);
		        Path directory = Paths.get(directoryPath);

		        // Ensure the directory exists
		        if (!Files.exists(directory)) {
		            Files.createDirectories(directory);
		        }
		        // Define the output file name
		        String fileName = "admin.json";
		        File outputFile = new File(directoryPath, fileName);
		        
		        // Write the users list to the JSON file
		        ObjectMapper objectMapper = new ObjectMapper();
		        objectMapper.registerModule(new JavaTimeModule()); // Register the JSR310 module
		        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
		        objectMapper.writeValue(outputFile, users);

		        // Log the output file location
		        System.out.println("Data successfully saved to: " + outputFile.getAbsolutePath());
			 
//			 // Define the directory and ensure it exists
//		        String directoryPath = path; // Define your directory here
//		        System.out.print(directoryPath);
//		        Path directory = Paths.get(directoryPath);
//		        if (!Files.exists(directory)) {
//		            Files.createDirectories(directory); // Create the directory if it doesn't exist
//		        }
//		        // Define the output file name with a timestamp for uniqueness
//		        String fileName = "admin.json";
//		        File outputFile = new File(directoryPath, fileName);
//
//		        // Write the users list to the JSON file
//		        ObjectMapper objectMapper = new ObjectMapper();
//		        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print the JSON
//		        objectMapper.writeValue(outputFile, users);

		        // Log the output file location
//		        System.out.println("Data successfully saved to " + outputFile.getAbsolutePath());

//		        InsertQueryGenerator opj = new InsertQueryGenerator();
//	            opj.generateInsertQueries(Muser.class, users);
		        return users;
			} catch (Exception e) {
			    throw new RuntimeException("Failed to access field: ", e);
//			    return e;
			}
	       
	       
	    }

}
