package com.knowledgeVista.Migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
			 
		        return users;
			} catch (Exception e) {
			    throw new RuntimeException("Failed to access field: ", e);
//			    return e;
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

}
