package com.knowledgeVista.License;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import org.springframework.core.env.Environment;
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
import com.knowledgeVista.Notification.Service.NotificationService;
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
	
	 @Autowired
		private NotificationService notiservice;
	 @Autowired
		private Environment env;
	 @Autowired
		private mAdminLicenceRepo madminrepo;

	    @Value("${upload.licence.directory}")
	    private String licenceUploadDirectory;
	    @Value("${upload.licence.directory}")
	    private String olddir;
	 

	 
	 private String valu;
	 private String valu1;
	 private String file;
	 
	 private  Logger logger = LoggerFactory.getLogger(LicenseController.class);


		public ResponseEntity<?> getAllUser(String token) {
			if (!jwtUtil.validateToken(token)) {
	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	         }
			String uemail = jwtUtil.getUsernameFromToken(token);
            Optional<Muser> opuser= muserrepo.findByEmail(uemail);
            if(opuser.isPresent()) {

         	   Muser user= opuser.get();
         	   String institution=user.getInstitutionName();
         	   Madmin_Licence madmin= madminrepo.findByInstitutionName(institution);
         	
	
         	  Boolean isEmpty=false;
         	  Boolean valid=true;
              Boolean type=false;
  
         	  Optional<License> oplicence=licenseRepository.findByinstitution(institution);
         	    if(oplicence.isEmpty()) {
         	    	 isEmpty = true;
        			 valid =false;
        	    	 type=true;
        	    	 List<Map<String, Object>> dataList = new ArrayList<>();
        	    	  UserListWithStatus userListWithStatusss = new UserListWithStatus(isEmpty, valid,type, dataList);
        	    	
        			    return new ResponseEntity<>(userListWithStatusss,HttpStatus.OK);
         	    }else {
         	    	
         	    License license =oplicence.get();
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
		              
		             
		  				
		            	  ProductName=license.getProduct_name();
		            	  CompanyName=license.getCompany_name();
		            	  
		            	  Type=license.getType();
		            	  StartDate=dateFormat.format(license.getStart_date());
		            	  EndDate=dateFormat.format(license.getEnd_date());
		  			
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
	    			
	    		valid=this.getall(institution, madmin ,license);
	    		
		    UserListWithStatus userListWithStatus = new UserListWithStatus(isEmpty, valid,type, dataList);
         	    
		    return new ResponseEntity<>(userListWithStatus,HttpStatus.OK);
         	    }
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		}
		
		public ResponseEntity<Integer> count(String token) {
			if (!jwtUtil.validateToken(token)) {
				 return new ResponseEntity<>(401, HttpStatus.UNAUTHORIZED);
	         }
			String uemail = jwtUtil.getUsernameFromToken(token);
           Optional<Muser> opuser= muserrepo.findByEmail(uemail);
           if(opuser.isPresent()) {

        	   Muser user= opuser.get();
        	   String institution=user.getInstitutionName();
			
			Optional<License> oplicense = licenseRepository.findByinstitution(institution);
	    
	    	
			Long count = coursedetailrepository.countCourseByInstitutionName(institution);
			String courseString=" ";
//			System.out.println("out side for"+courseString==null);
			if(oplicense.isPresent()) {
				License license=oplicense.get();
				courseString=license.getCourse();
			
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
		}
			else if(!(oplicense.isEmpty())&&courseString.isEmpty()) {
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
          }else {
        	   return new ResponseEntity<>(401, HttpStatus.BAD_REQUEST);
           }
           return new ResponseEntity<>(401, HttpStatus.BAD_REQUEST);
		}
		
		 public boolean getall( String institution ,Madmin_Licence madmin, License license){
		    	

		     
		    	LocalDate currentDate = LocalDate.now();
	            java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
	            long milliseconds = Datecurrent.getTime(); // Get the time in milliseconds
	            java.sql.Timestamp timestamp = new java.sql.Timestamp(milliseconds);
	           String val="";
	            String localFile="";
		    	
		    		localFile=license.getFilename();

		    	 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				  try {
						 DocumentBuilder builder = factory.newDocumentBuilder();
						 if(madmin.getLicenceType().equals("FREE")) {
//							final String licenseSubdirectory = "Free";
//								licenceUploadDirectory = licenceUploadDirectory + licenseSubdirectory + "/";
							 //For Future Use
							 licenceUploadDirectory = licenceUploadDirectory;
						 }
						
							Document document = builder.parse(new File(licenceUploadDirectory+localFile));
							 Element rootElement=document.getDocumentElement();
							 NodeList personList = rootElement.getElementsByTagName("data");
							File file = new File(licenceUploadDirectory+localFile);
							long lastModified = file.lastModified();
							 Date date = new Date(lastModified);
							 SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
							 Element person4 = (Element) personList.item(0);
							  Element trai = (Element) person4.getElementsByTagName("course").item(0);
				              Element stud = (Element) person4.getElementsByTagName("type").item(0);
				              Element vale = (Element) person4.getElementsByTagName("validity").item(0);
				              String tra = trai.getTextContent();
				              String stude = stud.getTextContent();
				              val = vale.getTextContent();

					         String formattedDate = formatter.format(date)+tra+stude+val;

				             DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				             Document doc = dBuilder.parse(file);
				             Element root = doc.getDocumentElement();
				             Element validity2 = doc.createElement("key");
				             this.valu=(Jwts.builder()
				 	                .setSubject(formattedDate)
				 		            .signWith(SignatureAlgorithm.HS256, "yourSecretKeyStringWithAtLeast256BitsLength")
				 		            .compact());
				             		

				             }
				             catch (Exception e) {
				                 e.printStackTrace();
				                 this.valu="123344";
				             }
		    	
		    	
		  
		    	 boolean valid = false; // Initialize valid to false

		    	
		    	    	this.valu1=license.getKey();
				 
//		    -------------------------------------testarea-----------------------------
		    	        	  if ((valu.equals(valu1)) && !(license.getEnd_date().equals(timestamp)) && !(val.isEmpty())) {  
		    	        		 
		    	        		
		    	            valid =  true;
		    	            System.out.println("361"+valid);
		    	            logger.info("License is Valid"+valid);
		    	          
		    	        }
		    	        	else if((val.isEmpty()) && (valu.equals(valu1))) {
		    	        		
			    	        		valid=true;
			    	        		System.out.println("else if "+valid);
				    	        	logger.info("License is valid");
				    	        	logger.info("License validy is unlimited");
				    	        	
			    	        	
			    	        	}
		    	        else
		    	        {
		    	        	logger.info("License is InValid");
		    	        	if(!(valu.equals(valu1)))
		    	        			{
		    	        		
		    	        	valid=false;
		    	        	System.out.println( "383"+valid);
		    	        	logger.info("License is Modified");
		    	        			}
		    	        	else if((license.getEnd_date().equals(timestamp))&&  !(val.isEmpty())) {
		    	        		valid=false;
			    	        	logger.info("License is Expired");
		    	        	
		    	        	}
		    	        }
		    	    

		    	        	  licenceUploadDirectory=olddir;
		     
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
		        	String email = jwtUtil.getUsernameFromToken(token);
		               Optional<Muser> opuser= muserrepo.findByEmail(email);
		               if(opuser.isPresent()) {

		            	   Muser user= opuser.get();
		            	   String institution=user.getInstitutionName();
		            	   Madmin_Licence madmin= madminrepo.findByInstitutionName(institution);
		            	   if(madmin.getLicenceType().equals("FREE")) {
		            			licenceUploadDirectory = licenceUploadDirectory ;
		            			  //in future can be changed as licenceUploadDirectory
//						final String licenseSubdirectory = "Free";
//						licenceUploadDirectory = licenceUploadDirectory + "/" + licenseSubdirectory + "/";
		            		  
		            	   }
		        	
		            	   
		        	License savedAudio = this.saveFile(File);
		        	
		            DocumentBuilder builder = factory.newDocumentBuilder();
		            
		         // Define the date-time formatter for the custom format
		            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		           // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a");

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
		            NodeList personList = rootElement.getElementsByTagName("data");
		            for (int i = 0; i < personList.getLength(); i++) {
		            	 
		                Element person = (Element) personList.item(i);
		                Element product = (Element) person.getElementsByTagName("product_name").item(0);
		                Element company = (Element) person.getElementsByTagName("company_name").item(0);
		                Element storagesize = (Element) person.getElementsByTagName("storagesize").item(0);
		                Element versionName = (Element) person.getElementsByTagName("version").item(0);
		                Element keyName = (Element) person.getElementsByTagName("key").item(0);
		                Element typeName = (Element) person.getElementsByTagName("type").item(0);
		                Element courses = (Element) person.getElementsByTagName("course").item(0);
		                Element trainer =(Element) person.getElementsByTagName("trainer").item(0);
		                Element student=(Element) person.getElementsByTagName("student").item(0);
		                Element validityDate = (Element) person.getElementsByTagName("validity").item(0);

		                String productName = product.getTextContent();
		                String companyName = company.getTextContent();
		                String storage=storagesize.getTextContent();
		                String version = versionName.getTextContent();
		                String key = keyName.getTextContent();
		                String type = typeName.getTextContent();
		                String course = courses.getTextContent();
		                String trainercount=trainer.getTextContent();
		                String studentcount=student.getTextContent();
		                String validity = validityDate.getTextContent();
		               
		                this.licensedetails(productName, companyName,storage, key, validity,course,trainercount,studentcount,type,file,institution);
//---------------------------------------CustomerLeads call-----------------------

		                Integer CourseCount = Integer.parseInt(course);
		                Integer TrainerCount =Integer.parseInt(trainercount);
		                Integer StudentCount=Integer.parseInt(studentcount);
		                Integer validitydays=Integer.parseInt(validity);
		                LocalDate startdate= LocalDate.now();
                        LocalDate endDate = startdate.plusDays(validitydays);
 		               //------------for licence Expired Notificatio-------------
                        String baseUrl = env.getRequiredProperty("base.url");
        	            int port = Integer.parseInt(env.getRequiredProperty("server.port"));
        	            String contextPath = env.getProperty("api.context-path", ""); 
                        
                        
		                RestTemplate restTemplate = new RestTemplate();
		               
		               
		            	   String heading=" Licence Expiring Soon !";
	     	 		       String link="/about";
	     	 		       String notidescription= "Your Licence Was Expiring Soon.. Upload A New Licence File" ;
	     	 		      Long NotifyId =  notiservice.createNotification("CourseAdd","system",notidescription ,"system",heading,link);
	     	 		        if(NotifyId!=null) {
	     	 		        	for (int iv = 1; iv < 6; iv++) {
	     	 		        	  LocalDate notificationDate = endDate.minusDays(iv);
	     	 		        	  notiservice.LicenceExpitedNotification(NotifyId, notificationDate,institution);
	     	 		        	}
	     	 		        }
			                String apiurl3 = baseUrl + port +contextPath+"/Developer/CustomerLeads/" + email;
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
		                
		                

		                String apiurl4 = baseUrl + port +contextPath+"/Developer/CustomerDownload/" + email;
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
		            
//----------------------------------------CustomerLeads---------------------------
 } 
		            licenceUploadDirectory=olddir;
		            return ResponseEntity.ok().body(savedAudio);
		            }else {
		            	   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		               }
		        } catch (ParserConfigurationException | SAXException | IOException e) {
		            e.printStackTrace();
		            return ResponseEntity.badRequest().build();
		        }
		    }
			
			 public  License saveFile(MultipartFile File) throws IOException {
			        
				   this.file=File.getOriginalFilename();
				this.saveLicence(File);
				
			        return null ;
			    }
			   
//		//   -------------------------------
		    public String licensedetails(String product_name, String company_name, String storage,String key, String validity,String course,String trainercount,String studentcount,String type,String file,String institution) {

		   Optional< License >licenseList=licenseRepository.findByinstitution(institution);
		    	if(licenseList.isEmpty())
		    	{
		    	License data =new License();
		        LocalDate currentDate = LocalDate.now();
		        java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
		        int num = validity.isEmpty()?0:Integer.parseInt(validity);
		        Long size = (long) (validity.isEmpty()?0:Integer.parseInt(storage));
		        LocalDate futureDate = currentDate.plusDays(num);
		        java.util.Date Datefuture = java.sql.Date.valueOf(futureDate);
		        data.setStart_date(Datecurrent);
		        data.setEnd_date(Datefuture);
		        data.setCompany_name(company_name);
		        data.setStoragesize(size);
		        data.setKey(key);
		        data.setProduct_name(product_name);
		        data.setCourse(course);
		        data.setTrainer(trainercount);
		        data.setStudents(studentcount);
		        data.setType(type);
		        data.setFilename(file);
		        data.setInstitution(institution);
		        licenseRepository.save(data);   
		    	}
		    	else {
		    		License license =licenseList.get();
		    		if(!(license.getKey().equals(key)))
		    		{
		    			
		    			license.getFilename();
		    			
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
		    			
				        LocalDate currentDate = LocalDate.now();
				        java.util.Date Datecurrent = java.sql.Date.valueOf(currentDate);
				        int num = validity.isEmpty()?0:Integer.parseInt(validity);
				        Long size = (long) (validity.isEmpty()?0:Integer.parseInt(storage));
				        LocalDate futureDate = currentDate.plusDays(num);
				        java.util.Date Datefuture = java.sql.Date.valueOf(futureDate);
				        license.setStart_date(Datecurrent);
				        license.setEnd_date(Datefuture);
				        license.setCompany_name(company_name);
				        license.setStoragesize(size);
				        license.setKey(key);
				        license.setProduct_name(product_name);
				        license.setCourse(course);;
				        license.setType(type);
				        license.setFilename(file);
				        license.setTrainer(trainercount);
				        license.setStudents(studentcount);
				        license.setInstitution(institution);
				        licenseRepository.save(license);  
		    			
		    			
		    		}
		    			
		    		
		    		
		    	}
		    	
		    	
		    	
		        return null;
		    }
		    
		    
		    public String saveLicence(MultipartFile File) throws IOException {
		        String uniqueFileName =File.getOriginalFilename();
		       
		        Path uploadPath = Paths.get(licenceUploadDirectory);
		        if (!Files.exists(uploadPath)) {
		            Files.createDirectories(uploadPath);
		        }
		        String filePath = Paths.get(licenceUploadDirectory).resolve(uniqueFileName).toString();

		        Files.copy(File.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);

		        return "modifiedPath";   
		        
		    }

}
