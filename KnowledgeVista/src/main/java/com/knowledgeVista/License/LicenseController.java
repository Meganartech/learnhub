package com.knowledgeVista.License;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.DownloadManagement.CustomerLeads;
import com.knowledgeVista.DownloadManagement.Customer_downloads;
import com.knowledgeVista.FileService.LicenceService;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@RestController
public class LicenseController {
	 @Autowired
	 private JwtUtil jwtUtil;

	 @Autowired
	 private MuserRepositories muserrepo;
	 @Autowired
	    private licenseRepository licenseRepository;
	 
	 @Autowired
		private CourseDetailRepository coursedetailrepository;
	 @Autowired
	 private LicenceService licenceservice;
	
	 @Value("${upload.video.directory}")
	    private String audioUploadDirectory;
	 

	    @Value("${upload.licence.directory}")
	    private String licenceUploadDirectory;
	 
	 

	 
	 private String valu;
	 private String valu1;
	 private String file;
	 
	 private  Logger logger = LoggerFactory.getLogger(LicenseController.class);


		public ResponseEntity<UserListWithStatus> getAllUser() {
//		    List<AddUser> getUser = adduserrepository.findAll();
		    Iterable<License> licenseIterable = licenseRepository.findAll();
	    	List<License> licenseList = StreamSupport.stream(licenseIterable.spliterator(), false)
	    	                                         .collect(Collectors.toList());
	    	
	    	
	    	  boolean isEmpty = licenseList.isEmpty();
			boolean valid = !(licenseList.isEmpty())?this.getall():false;
	    	boolean type=true;
	    	
	    			if (!(licenseList.isEmpty())) {	
	    				if(licenseList.get(0).getType().equals("Demo")) {
	    				type=false;
	    				System.out.println("licenseList.get(0).getType()"+licenseList.get(0).getType());
	    				}
	    			}	
	    			List<Map<String, Object>> dataList = new ArrayList<>();
    		        Map<String, Object> data1 = new HashMap<>();
	    			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    			 try {
	    			DocumentBuilder builder = factory.newDocumentBuilder();
	    			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("product_info.xml");
	    			if (inputStream != null) {
	    			 Document document = builder.parse(inputStream);
					 Element rootElement=document.getDocumentElement();
					 NodeList personList = rootElement.getElementsByTagName("data");
					 Element person4 = (Element) personList.item(0);
					  Element contact = (Element) person4.getElementsByTagName("contact").item(0);
		              Element email = (Element) person4.getElementsByTagName("email").item(0);
		              Element ver = (Element) person4.getElementsByTagName("version").item(0);
		              String Contact = contact.getTextContent();
		              String Email = email.getTextContent();
		              String version = ver.getTextContent();
		              String ProductName="";
		              String CompanyName="";
		              String Type="";
		              String StartDate="";
		              String EndDate="";
		              SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		              
		              for (License license : licenseList) {
		  				
		            	  ProductName=license.getProduct_name();
		            	  CompanyName=license.getCompany_name();
//		            	  
		            	  Type=license.getType();
		            	  StartDate=dateFormat.format(license.getStart_date());
		            	  EndDate=dateFormat.format(license.getEnd_date());
		  			}
		              if(StartDate.equals(EndDate)) {
		            	  StartDate="";
		            	  EndDate="";
		              }
		              
		                data1.put("Contact",Contact);
	    		        data1.put("Email", Email);
	    		        data1.put("ProductName",ProductName);
	    		        data1.put("CompanyName", CompanyName);
	    		        data1.put("version",version);
	    		        data1.put("Type", Type);
	    		        data1.put("StartDate",StartDate);
	    		        data1.put("EndDate", EndDate);

	    		       
	    		        dataList.add(data1);
	    		        inputStream.close();
	    			} else {
	    				System.out.println("Failed to read xml");
	    				 throw new IllegalStateException("Failed to load product_info.xml");

	    			}
	    			 }
	    			 catch(Exception e) {
		                 e.printStackTrace();
		             }
	    			
	    		       
//	    		        // Print the list
//	    		        for (Map<String, Object> data : dataList) {
//	    		            System.out.println(data);
//	    		        }
	    	
		  
		    
		   
//		    logger.info(" total no of course  ======="+count.toString());
		    UserListWithStatus userListWithStatus = new UserListWithStatus(isEmpty, valid,type, dataList);
		   
		    return new ResponseEntity<>(userListWithStatus,HttpStatus.OK);
		}
		
	
		
		public ResponseEntity<Integer> count() {
			
			Iterable<License> licenseIterable = licenseRepository.findAll();
	    	List<License> licenseList = StreamSupport.stream(licenseIterable.spliterator(), false)
	    	                                         .collect(Collectors.toList());
	    	
			Long count = coursedetailrepository.count();
			String courseString=" ";
//			System.out.println("out side for"+courseString==null);
			for (License license : licenseList) {
				
				courseString=license.getCourse();
			}
			Long course=0l;
			//System.out.println(courseString.isEmpty());
			if(!(courseString.isEmpty())) {
				course = Long.parseLong(courseString);
				logger.info(course.toString());
				
			}
			
			if(count<course) {
				logger.info("-------------------------------------------------------");
				logger.info("ADD course");
				logger.info("-------------------------------------------------------");
		    return new ResponseEntity<>(200, HttpStatus.OK);
			}
			
			else if(!(licenseList.isEmpty())&&courseString.isEmpty()) {
				logger.info("-------------------------------------------------------");
				logger.info("unlimited course");
				logger.info("-------------------------------------------------------");
			    return new ResponseEntity<>(200, HttpStatus.OK);
			}
			else
			{
				logger.info("-------------------------------------------------------");
				logger.info("limited reached");
				 return new ResponseEntity<>(401, HttpStatus.BAD_REQUEST);
			}
				
		}
		
		 public boolean getall(){
		    	
		    	Iterable<License> licenseIterable = licenseRepository.findAll();
		    	List<License> licenseList = StreamSupport.stream(licenseIterable.spliterator(), false)
		    	                                         .collect(Collectors.toList());
		    	LocalDate currentDate = LocalDate.now();
	            java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
	            long milliseconds = Datecurrent.getTime(); // Get the time in milliseconds
	            java.sql.Timestamp timestamp = new java.sql.Timestamp(milliseconds);
	           String val="";
	            String localFile="";
		    	for (License license : licenseList) {
		    		localFile=license.getFilename();
//		    		 System.out.println("---------------------------------------------------");
//		    	    System.out.println("ID: " + license.getId());
//		    	    System.out.println("Company Name: " + license.getCompany_name());
//		    	    System.out.println("Product Name: " + license.getProduct_name());
//		    	    System.out.println("key " + license.getKey());
//		    	    System.out.println("start_date: " + license.getStart_date());
//		    	    System.out.println("end_date: " + license.getEnd_date());
////		            java.util.Date licenseStartDateUtil = java.sql.Date.valueOf(license.getStart_date().toLocaleString());
//		    	    System.out.println(" start date :"+license.getStart_date()+" present date :"+Datecurrent+" is equal"+license.getEnd_date().equals(timestamp));
//		    	    // Print other fields as needed
		    	}
		    	
		    	 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				  try {
						 DocumentBuilder builder = factory.newDocumentBuilder();
							Document document = builder.parse(new File(licenceUploadDirectory+localFile));
							 Element rootElement=document.getDocumentElement();
							 NodeList personList = rootElement.getElementsByTagName("data");
							File file = new File(licenceUploadDirectory+localFile);
							long lastModified = file.lastModified();
//							System.out.println("----------------------------------------------------------------");
//							System.out.println("Last modified string"+lastModified);
							 Date date = new Date(lastModified);
							// System.out.println("Last modified date"+date);
							 SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
							 Element person4 = (Element) personList.item(0);
							  Element trai = (Element) person4.getElementsByTagName("course").item(0);
				              Element stud = (Element) person4.getElementsByTagName("type").item(0);
				              Element vale = (Element) person4.getElementsByTagName("validity").item(0);
				              String tra = trai.getTextContent();
				              String stude = stud.getTextContent();
				              val = vale.getTextContent();
//				              System.out.println("No of trainer"+tra);
//				              System.out.println("No of student"+stude);
//				              System.out.println("No of student"+val.isEmpty());
					         String formattedDate = formatter.format(date)+tra+stude+val;
//					         System.out.println("Last modified date complete"+formattedDate);
//				             System.out.println("----------------------------------------------------------------");
//				             System.out.println(Jwts.builder()
//				             .setSubject(formattedDate)
//					            .signWith(SignatureAlgorithm.HS256, "yourSecretKeyStringWithAtLeast256BitsLength")
//					            .compact());
				             DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				             Document doc = dBuilder.parse(file);
				             Element root = doc.getDocumentElement();
				             Element validity2 = doc.createElement("key");
				             this.valu=(Jwts.builder()
				 	                .setSubject(formattedDate)
				 		            .signWith(SignatureAlgorithm.HS256, "yourSecretKeyStringWithAtLeast256BitsLength")
				 		            .compact());
				             		
//				             NodeList dataList = root.getElementsByTagName("data");
//				             Element data = (Element) dataList.item(0);
//				             Element type2 = (Element) data.getElementsByTagName("version").item(0);
//				            
//				             Element person1 = (Element) personList.item(0);
//				             Element key_name1 = (Element) person1.getElementsByTagName("key").item(0);
//				             
//				             if(key_name1== null) {
//				             data.insertBefore(validity2, type2.getNextSibling());
//				             doc.normalize();
//				             FileOutputStream fileOutputStream = new FileOutputStream(file);
//				             try {
//				             javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(
//				                     new javax.xml.transform.dom.DOMSource(doc),
//				                     new javax.xml.transform.stream.StreamResult(fileOutputStream));
//				             fileOutputStream.close();
//
//				             logger.info("XML file updated successfully!");
//
				             }
				             catch (Exception e) {
				                 e.printStackTrace();
				                 this.valu="123344";
				             }
		    	
		    	
		  
		    	 boolean valid = false; // Initialize valid to false
//		    	 boolean same = this.areJwtsEqual();
		    	 
		    	// System.out.println("out of the loop"+valid);
		    	    for (License license : licenseList) {
		    	    	this.valu1=license.getKey();
				 
//		    	        if (license.getEnd_date().equals(timestamp)) {
//		    -------------------------------------testarea-----------------------------
		    	        	  if ((valu.equals(valu1)) && !(license.getEnd_date().equals(timestamp)) && !(val.isEmpty())) {  
		    	        		 
		    	        	
		    	            valid =  true;
		    	            logger.info("License is Valid"+valid);
////		    	            System.out.println("inside of the loop IF of same!!!*!*!*!*!*!*!*!"+same);
//		    	            System.out.println("generated valueeee======"+this.valu);
//		    	            System.out.println("license  valueeee======"+this.valu1);
//		    	            System.out.println("license"+license.getEnd_date()+"       "+timestamp+"          "+ !(license.getEnd_date().equals(timestamp)));
		    	            break; 
		    	        }
		    	        	else if((val.isEmpty()) && (valu.equals(valu1))) {
			    	        		valid=true;
				    	        	logger.info("License is valid");
				    	        	logger.info("License validy is unlimited");
				    	        	
			    	        	
			    	        	}
		    	        else
		    	        {
		    	        	logger.info("License is InValid");
		    	        	if(!(valu.equals(valu1)))
		    	        			{
		    	        	valid=false;
		    	        	logger.info("License is Modified");
		    	        			}
		    	        	else if((license.getEnd_date().equals(timestamp))&&  !(val.isEmpty())) {
		    	        		valid=false;
			    	        	logger.info("License is Expired");
		    	        	
		    	        	}
		    	        }
		    	    }

//		    	  List<Addaudio1> allAudio = audiorepository.findAll();
		    	
		    	return valid;
		    }
		 
//		 public boolean areJwtsEqual() {
//		        // Decode and parse the payloads of both JWTs
//		        Claims claims1 = Jwts.parser().parseClaimsJwt("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyOTA0MjAyNDBmcmVlIn0.FTsji32AaS0P-5B2BI3LyN5XJK_J1Z8pI8hcW4Vo_9U").getBody();
//		        Claims claims2 = Jwts.parser().parseClaimsJwt("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyOTA0MjAyNDBmcmVlIn0.FTsji32AaS0P-5B2BI3LyN5XJK_J1Z8pI8hcW4Vo_9U").getBody();
//
//		        // Compare the decoded payloads
//		        return claims1.equals(claims2);
//		    }
//		 
//			-------------------------------------------licensefile--------------------------------------------
		
		    public ResponseEntity<License> upload( MultipartFile File,String lastModifiedDate,String token)
		      {
		    	if (!jwtUtil.validateToken(token)) {
		             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		         }


	                      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        
		        try {
		            // Save audio with file using the service 
		        	License savedAudio = this.saveFile(File);
		            DocumentBuilder builder = factory.newDocumentBuilder();
		            
		         // Define the date-time formatter for the custom format
		            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a");

		            // Parse the string to a LocalDateTime
		            LocalDateTime localDateTime;
		            try {
		                localDateTime = LocalDateTime.parse(lastModifiedDate, formatter);
		            } catch (DateTimeParseException e) {
		                System.err.println("Error parsing date string: " + e.getMessage());
		                return ResponseEntity.badRequest().build();
		            }

		            // Convert LocalDateTime to Instant
		            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

		            // Create a FileTime from the Instant
		            FileTime newModifiedTime = FileTime.from(instant);
		            
		            Path filePath = Paths.get(licenceUploadDirectory + File.getOriginalFilename());
		            Files.setLastModifiedTime(filePath, newModifiedTime);

		            Document document = builder.parse(new File(licenceUploadDirectory  + File.getOriginalFilename()));
		            
		            
		            
					
					
		            Element rootElement = document.getDocumentElement();
		          //  System.out.println("Root Element = " + rootElement.getNodeName());
		            NodeList personList = rootElement.getElementsByTagName("data");
		            for (int i = 0; i < personList.getLength(); i++) {
		                Element person = (Element) personList.item(i);
		                Element product = (Element) person.getElementsByTagName("product_name").item(0);
		                Element company = (Element) person.getElementsByTagName("company_name").item(0);
		                Element versionName = (Element) person.getElementsByTagName("version").item(0);
		                Element keyName = (Element) person.getElementsByTagName("key").item(0);
		                Element typeName = (Element) person.getElementsByTagName("type").item(0);
		                Element courses = (Element) person.getElementsByTagName("course").item(0);
		                Element trainer =(Element) person.getElementsByTagName("trainer").item(0);
		                Element student=(Element) person.getElementsByTagName("student").item(0);
		                Element validityDate = (Element) person.getElementsByTagName("validity").item(0);

		                String productName = product.getTextContent();
		                String companyName = company.getTextContent();
		                String version = versionName.getTextContent();
		                String key = keyName.getTextContent();
		                String type = typeName.getTextContent();
		                String course = courses.getTextContent();
		                String trainercount=trainer.getTextContent();
		                String studentcount=student.getTextContent();
		                String validity = validityDate.getTextContent();
		                
		                
		                this.licensedetails(productName, companyName, key, validity,course,type,file);
//---------------------------------------CustomerLeads call-----------------------

		                Integer CourseCount = Integer.parseInt(course);
		                Integer TrainerCount =Integer.parseInt(trainercount);
		                Integer StudentCount=Integer.parseInt(studentcount);
		                Integer validitydays=Integer.parseInt(validity);
		                LocalDate startdate= LocalDate.now();
		               // System.out.println("startdate"+startdate);
                        LocalDate endDate = startdate.plusDays(validitydays);
                       // System.out.println("endDate"+endDate);
		                
		                RestTemplate restTemplate = new RestTemplate();
		                String email = jwtUtil.getUsernameFromToken(token);
		               Optional<Muser> opuser= muserrepo.findByEmail(email);
		               if(opuser.isPresent()) {
		            	   Muser user= opuser.get();
		                String apiurl3 = "http://localhost:8080/Developer/CustomerLeads/" + email;
		                CustomerLeads updateData = new CustomerLeads();
		                updateData.setEmail(user.getEmail());
		                updateData.setCountryCode(user.getCountryCode());
		                updateData.setPhone(user.getPhone());
		                updateData.setName(user.getUsername());
		                updateData.setLicenseKey(key);
		                updateData.setLicenseType(type);
		                updateData.setVersion(version);
		               updateData.setTrainerCount(TrainerCount);
		               updateData.setStudentCount(StudentCount);
		               updateData.setCourseCount(CourseCount);
		               updateData.setLicenseValidity(validitydays);
		               updateData.setLicencestartdate(startdate);
		               updateData.setLicenceEndDate(endDate);
		               updateData.setIsLicenseExpired(false);
		                restTemplate.put(apiurl3, updateData, String.class);
		                
		                

		                String apiurl4 = "http://localhost:8080/Developer/CustomerDownload/" + email;
		                Customer_downloads custdown=new Customer_downloads();
		                custdown.setCountryCode(user.getCountryCode());
		                custdown.setName(user.getUsername());
		                custdown.setEmail(user.getEmail());
		                custdown.setPhone(user.getPhone());
		                custdown.setCourseCount(CourseCount);
		                custdown.setStudentCount(StudentCount);
		                custdown.setTrainerCount(TrainerCount);
		                custdown.setVersion(version);
		                

		                restTemplate.put(apiurl4, updateData, String.class);
		               }else {
		            	   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		               }
//----------------------------------------CustomerLeads---------------------------
		                

//		                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
//		                System.out.println("product_name: " + productName + " company_name: " + companyName + " version: " + version + " key: " + key + " type: " + type + " validity: " + validity + " Video: " + course + " lastModifiedDate: " + lastModifiedDate);
//		                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		            }
	
		               
			            
		            return ResponseEntity.ok().body(savedAudio);
		        } catch (ParserConfigurationException | SAXException | IOException e) {
		            e.printStackTrace();
		            return ResponseEntity.badRequest().build();
		        }
		    }
			
			 public  License saveFile(MultipartFile File) throws IOException {
			        // Save the audio file to the server and get the file path
			        // this.savFile(File);
				   this.file=File.getOriginalFilename();
				licenceservice.saveLicence(File);
				 
				 
//			        license
			          
			        return null ;
			    }
//			 public String savFile(MultipartFile File) throws IOException {
//			        // Generate a unique file name (you can use other strategies)
//			        String uniqueFileName =File.getOriginalFilename();
//			        System.out.println("audioFile.getOriginalFilename()");
//			        System.out.println(File.getOriginalFilename());
//			        this.file=File.getOriginalFilename();
////			        logger.info(file);
//			        Path uploadPath = Paths.get(audioUploadDirectory);
//			        if (!Files.exists(uploadPath)) {
//			            Files.createDirectories(uploadPath);
//			        }
//			        // Define the file path where the audio file will be stored
//			        String filePath = Paths.get(audioUploadDirectory).resolve(uniqueFileName).toString();
////			        String modifiedPath = filePath.replace("Audio\\", "");
////			        System.out.println(modifiedPath);
//	
//			        // Save the file to the server
//			        Files.copy(File.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
//	
//			        return "modifiedPath";   
//			        
//			    }
//			   
//		//   -------------------------------
		    public String licensedetails(String product_name, String company_name, String key, String validity,String course,String type,String file) {
		    	
		    	Iterable<License> licenseIterable = licenseRepository.findAll();
		    	List<License> licenseList = StreamSupport.stream(licenseIterable.spliterator(), false)
                        .collect(Collectors.toList());
		    	if(licenseList.isEmpty())
		    	{
		    	License data =new License();
		        LocalDate currentDate = LocalDate.now();
		        java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
		        int num = validity.isEmpty()?0:Integer.parseInt(validity);
		        LocalDate futureDate = currentDate.plusDays(num);
		        java.util.Date Datefuture = java.sql.Date.valueOf(futureDate);
		        data.setStart_date(Datecurrent);
		        data.setEnd_date(Datefuture);
		        data.setCompany_name(company_name);
		        data.setKey(key);
		        data.setProduct_name(product_name);
		        data.setCourse(course);;
		        data.setType(type);
		        data.setFilename(file);
		        licenseRepository.save(data);   
		    	}
		    	else {
		    		for (License license : licenseList) {
		    		if(!(license.getKey().equals(key)))
		    		{
		    			
		    			license.getFilename();
		    		//System.out.println("file are same  :"+(file.equals(license.getFilename())));
		    			
		    			 String filePath = licenceUploadDirectory+license.getFilename();
		    		        File file1 = new File(filePath);
		    		        if (file1.exists()&& !(file.equals(license.getFilename()))) {
		    		            // Attempt to delete the file
		    		            if (file1.delete()) {
		    		                logger.info("File deleted successfully.");
		    		            } else {
		    		            	logger.info("Failed to delete the file.");
		    		            }
		    		        } else {
		    		        	logger.info("File does not exist.");
		    		        }
		    			
		    			licenseRepository.deleteAll();
		    			
		    			
		    			
		    			License data =new License();
				        LocalDate currentDate = LocalDate.now();
				        java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
				        int num = validity.isEmpty()?0:Integer.parseInt(validity);
				        LocalDate futureDate = currentDate.plusDays(num);
				        java.util.Date Datefuture = java.sql.Date.valueOf(futureDate);
				        data.setStart_date(Datecurrent);
				        data.setEnd_date(Datefuture);
				        data.setCompany_name(company_name);
				        data.setKey(key);
				        data.setProduct_name(product_name);
				        data.setCourse(course);;
				        data.setType(type);
				        data.setFilename(file);
				        licenseRepository.save(data);   
		    			
		    			
		    		}
		    			
		    		}
		    		
		    	}
		    	
		    	
		    	
		        return null;
		    }

}
