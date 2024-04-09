package com.knowledgeVista.Course.Controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.videoLessons;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Course.Repository.videoLessonRepo;
import com.knowledgeVista.FileService.VideoFileService;
import com.knowledgeVista.ImageCompressing.ImageUtils;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;


@RestController
@RequestMapping("/lessons")
@CrossOrigin
public class videolessonController {

		@Autowired
		private CourseDetailRepository coursedetailrepostory;
		@Autowired
		private VideoFileService fileService;
		@Autowired
		private videoLessonRepo lessonrepo;
		 @Autowired
		 private JwtUtil jwtUtil;
		private final String videoStorageDirectory = "video/";
		
		
		
		 @PostMapping("/save/{courseId}")
		  private ResponseEntity<String> savenote(
				  									@RequestParam("thumbnail") MultipartFile file,
				  								  @RequestParam("Lessontitle") String Lessontitle,
		                                          @RequestParam("LessonDescription") String LessonDescription,
		                                          @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
		                                          @RequestParam(value = "fileUrl", required = false) String fileUrl,
		                                          @PathVariable Long courseId) {
		      try {
		          Optional<CourseDetail> courseDetailOptional = coursedetailrepostory.findById(courseId);
		          if (!courseDetailOptional.isPresent()) {
		              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"CourseDetail not found for courseId: " + courseId + "\"}");
		          }

		          CourseDetail courseDetail = courseDetailOptional.get();
                  videoLessons lesson=new videoLessons();
                   lesson.setLessonDescription(LessonDescription);
                   lesson.setLessontitle(Lessontitle);
                   lesson.setCourseDetail(courseDetail);
                   lesson.setThumbnail(ImageUtils.compressImage(file.getBytes()));
		         
		          if (videoFile != null) {
		              String videoFilePath = fileService.saveVideoFile(videoFile);
		              lesson.setVideofilename(videoFilePath);
		              lesson.setVideoFile(videoFile);
		          } else if (fileUrl != null && !fileUrl.isEmpty()) {
		        	  lesson.setFileUrl(fileUrl);
		          } else {
		              return ResponseEntity.badRequest().body("{\"error\": \"Either video file or file URL must be provided\"}");
		          }

		         lessonrepo.save(lesson);

		              return ResponseEntity.ok("{\"message\": \"Note saved successfully\"}");
		          
		      } catch (Exception e) {
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to save note: " + e.getMessage() + "\"}");
		      }
		  }
			 
		 
		 @GetMapping("/getvideoByid/{lessId}")
			public ResponseEntity<?> getVideoFile(@PathVariable Long lessId) {
//			 if (!jwtUtil.validateToken(token)) {
//	        	 System.out.println("invalid Token");
//	    
//	             // If the token is not valid, return unauthorized status
//	             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//	            
//	         }
	 
			 Optional<videoLessons> optionallesson = lessonrepo.findById(lessId);
			    if (optionallesson.isPresent()) {
			    	videoLessons lesson = optionallesson.get();
			        String filename = lesson.getVideofilename();
			        if (filename != null) { // Check if filename is not null
			            Path filePath = Paths.get(videoStorageDirectory, filename);
			            try {
			                if (filePath.toFile().exists() && filePath.toFile().isFile()) {
			                    Resource resource = new UrlResource(filePath.toUri());
			                    if (resource.exists() && resource.isReadable()) {
			                        HttpHeaders headers = new HttpHeaders();
			                        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
			                        return ResponseEntity.ok()
			                                .headers(headers)
			                                .body(resource);
			                    }
			                }
			            } catch (Exception e) {
			                e.printStackTrace();
			            }
			        }else {
			        	return ResponseEntity.ok(lesson.getFileUrl());
			        }
			    }
			    return ResponseEntity.notFound().build();
			}
		 
		 
		@DeleteMapping("/delete")
		private ResponseEntity<?> deleteLessonsByLessonId(@RequestParam("lessonId")Long lessonId,
				   @RequestParam("Lessontitle") String Lessontitle,
		          @RequestHeader("Authorization") String token){
			 try {
		          // Validate the token
		          if (!jwtUtil.validateToken(token)) {
		              // If the token is not valid, return unauthorized status
		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		          }

		          String role = jwtUtil.getRoleFromToken(token);

		          // Perform authentication based on role
		          if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
		        	  Optional<videoLessons> opvideo =lessonrepo.findById(lessonId);
		        	  if(opvideo.isPresent()) {
		        		  videoLessons videolesson=opvideo.get();
		        		  
		        		  
		        		  if (videolesson.getVideofilename() != null) {
			                    boolean videoFileDeleted = fileService.deleteVideoFile(videolesson.getVideofilename());
			                    if (videoFileDeleted) {
			                    	lessonrepo.deleteById(lessonId);

						        	  return ResponseEntity.ok("{\"message\":\"Lesson " + Lessontitle + " Deleted Successfully\"}");
						        } else {
			                        // Video file deletion failed
			                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			                                .body("{\"message\": \"Failed to delete video file associated with the note\"}");
			                    }
			                } else {
			                    // Video file is null, delete only the note

					        	  lessonrepo.deleteById(lessonId);
					        	  return ResponseEntity.ok("{\"message\":\"Lesson " + Lessontitle + " Deleted Successfully\"}");
					        	  }
		        		  
		        		  
		        		  
		        		  

		        	  }else {
		        		  return ResponseEntity.notFound().build();
		        		  
		        	  }
		          } else {
		              // Return unauthorized status if the role is neither ADMIN nor TRAINER
		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		          }
		      } catch (Exception e) {
		          // Log any other exceptions for debugging purposes
		          e.printStackTrace(); // You can replace this with logging framework like Log4j
		          // Return an internal server error response
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		      }

		}


}
