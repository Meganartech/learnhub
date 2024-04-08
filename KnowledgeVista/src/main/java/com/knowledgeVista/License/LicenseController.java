package com.knowledgeVista.License;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




@CrossOrigin
@RestController
@RequestMapping("/api/v2/")
public class LicenseController {
	
	
	 @Autowired
	    private licenseRepository licenseRepository;
	
	 @Value("${upload.video.directory}")
	    private String audioUploadDirectory;
	 
	 private static final String filename="video/data.xml";

		@GetMapping("/GetAllUser")
		public ResponseEntity<UserListWithStatus> getAllUser() {
//		    List<AddUser> getUser = adduserrepository.findAll();
		    Iterable<License> licenseIterable = licenseRepository.findAll();
	    	List<License> licenseList = StreamSupport.stream(licenseIterable.spliterator(), false)
	    	                                         .collect(Collectors.toList());
		    boolean isEmpty = licenseList.isEmpty();
		    boolean valid =this.getall();
		   
		    
		    UserListWithStatus userListWithStatus = new UserListWithStatus(isEmpty, valid);
		   
		    return new ResponseEntity<>(userListWithStatus,HttpStatus.OK);
		}
		
		 public boolean getall(){
		    	
		    	Iterable<License> licenseIterable = licenseRepository.findAll();
		    	List<License> licenseList = StreamSupport.stream(licenseIterable.spliterator(), false)
		    	                                         .collect(Collectors.toList());
		    	LocalDate currentDate = LocalDate.now();
	            java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
	            long milliseconds = Datecurrent.getTime(); // Get the time in milliseconds
	            java.sql.Timestamp timestamp = new java.sql.Timestamp(milliseconds);
		    	for (License license : licenseList) {
		    		 System.out.println("---------------------------------------------------");
		    	    System.out.println("ID: " + license.getId());
		    	    System.out.println("Company Name: " + license.getCompany_name());
		    	    System.out.println("Product Name: " + license.getProduct_name());
		    	    System.out.println("key " + license.getKey());
		    	    System.out.println("start_date: " + license.getStart_date());
		    	    System.out.println("end_date: " + license.getEnd_date());
//		            java.util.Date licenseStartDateUtil = java.sql.Date.valueOf(license.getStart_date().toLocaleString());
		    	    System.out.println(" start date :"+license.getStart_date()+" present date :"+Datecurrent+" is equal"+license.getEnd_date().equals(timestamp));
		    	    // Print other fields as needed
		    	}
		    	 boolean valid = false; // Initialize valid to false
		    	
		    	 System.out.println("out of the loop"+valid);
		    	    for (License license : licenseList) {
		    	        // Check the validity condition
		    	        if (license.getEnd_date().equals(timestamp)) {
//		    -------------------------------------testarea-----------------------------
//		    	        	  if (license.getStart_date().equals(timestamp)) {  
		    	        	
		    	            valid =  false; // Set valid to true if at least one license is valid
		    	            System.out.println("inside of the loop"+valid);
		    	            break; // No need to continue checking, we already found a valid license
		    	        }
		    	        else
		    	        {
		    	        	valid=true;
		    	        	 System.out.println("inside of the loop"+valid);
		    	        }
		    	    }

//		    	  List<Addaudio1> allAudio = audiorepository.findAll();
		    	
		    	return valid;
		    }
		 
//			-------------------------------------------licensefile--------------------------------------------
			@PostMapping("/uploadfile")
		    public ResponseEntity<License> upload(@RequestParam("audioFile") MultipartFile File)
		      {
		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				
		        try {
		            // Save audio with file using the service 
		        	License savedAudio = this.saveFile(File);
		            DocumentBuilder builder = factory.newDocumentBuilder();
		    		
		    		
					Document document = builder.parse(new File(filename));
					
					Element rootElement=document.getDocumentElement();
					System.out.println("Root Element ="+rootElement.getNodeName());
					 NodeList personList = rootElement.getElementsByTagName("data");
			            for (int i = 0; i < personList.getLength(); i++) {
			                Element person = (Element) personList.item(i);
			                // Get the <name> element inside each <person> element
			                Element product = (Element) person.getElementsByTagName("product_name").item(0);
			                Element company = (Element) person.getElementsByTagName("company_name").item(0);
			                Element version_name = (Element) person.getElementsByTagName("version").item(0);
			                Element key_name = (Element) person.getElementsByTagName("key").item(0);
			                Element type_name = (Element) person.getElementsByTagName("type").item(0);
			                Element validity_date = (Element) person.getElementsByTagName("validity").item(0);
			                // Get the text content of the <name> element
			                String product_name = product.getTextContent();
			                String company_name = company.getTextContent();
			                String version = version_name.getTextContent();
			                String key = key_name.getTextContent();
			                String type = type_name.getTextContent();
			                String validity = validity_date.getTextContent();
			                
			                this.licensedetails(product_name, company_name, key, validity);
			                
			                
			                System.out.println("product_name:" + product_name+" company_name: "+company_name+" version: "+version+" key: "+key+" type: "+type+" validity: "+validity);
			            }
	
		               
			            
		            return ResponseEntity.ok().body(savedAudio);
		        } catch (ParserConfigurationException |SAXException|IOException  e) {
		            e.printStackTrace();
		            return ResponseEntity.badRequest().build();
		        }
		    }
			
			 public  License saveFile(MultipartFile File) throws IOException {
			        // Save the audio file to the server and get the file path
			         this.savFile(File);
//			        license
			          
			        return null ;
			    }
			 public String savFile(MultipartFile File) throws IOException {
			        // Generate a unique file name (you can use other strategies)
			        String uniqueFileName =File.getOriginalFilename();
			        System.out.println("audioFile.getOriginalFilename()");
			        System.out.println(File.getOriginalFilename());
			        
			        // Define the file path where the audio file will be stored
			        String filePath = Paths.get(audioUploadDirectory).resolve(uniqueFileName).toString();
//			        String modifiedPath = filePath.replace("Audio\\", "");
//			        System.out.println(modifiedPath);
	
	
			        // Save the file to the server
			        Files.copy(File.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
	
			        return "modifiedPath";   
			        
			    }
			   
		//   -------------------------------
		    public String licensedetails(String product_name, String company_name, String key, String validity) {
		    	License data =new License();
		        LocalDate currentDate = LocalDate.now();
		        java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
		        int num = Integer.parseInt(validity);
		        LocalDate futureDate = currentDate.plusDays(num);
		        java.util.Date Datefuture = java.sql.Date.valueOf(futureDate);
		        data.setStart_date(Datecurrent);
		        
//		        if(utilDatefuture.equals(utilDatecurrent))
//		        {
//		        	System.out.println("same");
//		        }
//		        else {
//		        	System.out.println(utilDatefuture +" "+ utilDatecurrent);
//		        }
		      
		        data.setEnd_date(Datefuture);
		        data.setCompany_name(company_name);
		        data.setKey(key);
		        data.setProduct_name(product_name);
		        licenseRepository.save(data);   
		        this.getall();
		    	
		    	
		    	
		        return null;
		    }

}
