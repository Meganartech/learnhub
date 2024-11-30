package com.knowledgeVista.Migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

	 @Autowired
	    private DatabaseCreationService databaseCreationService;
	 
	 @Autowired
	    private DynamicDataSourceService dynamicDataSourceService;

//	    @GetMapping("/switch-database")
	    public String switchDatabase() {
	    	String dbName="backup";
	    	System.out.println("hello");
	        try {
	            dynamicDataSourceService.switchDataSource(dbName);
	            return "Switched to database: " + dbName;
	        } catch (Exception e) {
	            return "Failed to switch database: " + e.getMessage();
	        }
	    }

	    @GetMapping("/reset-database")
	    public String resetDatabase() {
	        try {
	        	String dbName="learnhub";
	            dynamicDataSourceService.resetToDefaultDataSource();
	            return "Reset to the original database";
	        } catch (Exception e) {
	            return "Failed to reset database: " + e.getMessage();
	        }
	    }
	 
    @PostMapping("/create")
    public String createDatabase() {
    	String databaseName="backup";
        databaseCreationService.createDatabase(databaseName);
        return "Database '" + databaseName + "' created successfully.";
    }
}
