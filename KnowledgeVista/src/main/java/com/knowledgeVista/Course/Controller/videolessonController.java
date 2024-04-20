package com.knowledgeVista.Course.Controller;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/lessons")
@CrossOrigin
public class videolessonController {

	@Autowired
	private MuserRepositories muserRepo;

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
		                                          @PathVariable Long courseId,
		                                          @RequestHeader("Authorization") String token
		                                          ) {
		      try {
		    	  if (!jwtUtil.validateToken(token)) {
				         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				     }

				     String role = jwtUtil.getRoleFromToken(token);
				     if ("ADMIN".equals(role)|| "TRAINER".equals(role)) {
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

		              return ResponseEntity.ok("{\"message\": \"Lesson saved successfully\"}");
				     }else {
				    	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				     }
				   
		      } catch (Exception e) {
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to save note: " + e.getMessage() + "\"}");
		      }
		  }
			 
		 
		 
		 @PatchMapping("/edit/{lessonId}")
		 public ResponseEntity<?> EditLessons(@PathVariable Long lessonId,
		     @RequestParam(value="thumbnail" , required = false) MultipartFile file,
		     @RequestParam(value="Lessontitle" , required = false) String Lessontitle,
		     @RequestParam(value="LessonDescription" , required = false) String LessonDescription,
		     @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
		     @RequestParam(value = "fileUrl", required = false) String fileUrl,
		     @RequestHeader("Authorization") String token) {
		     
		     if (!jwtUtil.validateToken(token)) {
		         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }

		     String role = jwtUtil.getRoleFromToken(token);
		     if ("ADMIN".equals(role)||"TRAINER".equals(role)) {
		         try {
		             Optional<videoLessons> opvideo = lessonrepo.findById(lessonId);
		             if (opvideo.isPresent()) {
		                 videoLessons video = opvideo.get();
		                 if (Lessontitle != null && !Lessontitle.isEmpty()) {
		                	    video.setLessontitle(Lessontitle);
		                	}
		                	if (LessonDescription != null && !LessonDescription.isEmpty()) {
		                	    video.setLessonDescription(LessonDescription);
		                	}

		                 if (file != null && !file.isEmpty()) {
		                     video.setThumbnail(ImageUtils.compressImage(file.getBytes()));
		                 }
		                 if (videoFile != null) {
		                     String videoFilePath = fileService.saveVideoFile(videoFile);
		                     video.setVideofilename(videoFilePath);
		                     video.setVideoFile(videoFile);
		                     video.setFileUrl(null);
		                 } else if (fileUrl != null && !fileUrl.isEmpty()) {
		                	 
		                	 if(video.getVideofilename()!=null) {
		                	 boolean videoFileDeleted= fileService.deleteVideoFile(video.getVideofilename());
		                	  if (videoFileDeleted) {
		                		  video.setVideofilename(null);
		                		  video.setFileUrl(fileUrl);  
		                	  }
		                	  }else {
		                		  video.setFileUrl(fileUrl);
		                	  }
		                	 
		                     
		                 } 
		                 lessonrepo.saveAndFlush(video);
		                 return ResponseEntity.ok("{\"message\": \"Lessons Edited successfully\"}");
		             } else {
		                 return ResponseEntity.notFound().build();
		             }
		         } catch (Exception e) {
		             e.printStackTrace();
		             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		         }
		     } else {
		         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		     }
		 }
 
		 
		 
		 @GetMapping("/getvideoByid/{lessId}/{courseId}/{token}")
		    public ResponseEntity<?> getVideoFile(@PathVariable Long lessId,
		                                          @PathVariable Long courseId,
		                                          @PathVariable String token,
		                                          HttpServletRequest request) {
		        try {
		            if (!jwtUtil.validateToken(token)) {
		                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		            }
		            
		            String role = jwtUtil.getRoleFromToken(token);
		            String email = jwtUtil.getUsernameFromToken(token);
		            Optional<Muser> opuser = muserRepo.findByEmail(email);
		            
		            if (!opuser.isPresent()) {
		                return ResponseEntity.notFound().build();
		            }
		            
		            Muser user = opuser.get();
		            
		            if ("USER".equals(role)) {
		                return handleUserRole(lessId, courseId, user,request);
		            } else if ("ADMIN".equals(role)) {
		                return getVideo(lessId ,request);
		            } else if ("TRAINER".equals(role)) {
		                return handleTrainerRole(lessId, courseId, user ,request);
		            } else {
		                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		            }
		            
		        } catch (Exception e) {
		            // Log the exception (you can use a proper logging library)
		            e.printStackTrace();
		            // Return an internal server error response
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		        }
		    }

		    private ResponseEntity<?> handleUserRole(Long lessId, Long courseId, Muser user,HttpServletRequest request) {
		        Optional<CourseDetail> opcourse = coursedetailrepostory.findById(courseId);
		        if (opcourse.isPresent() && user.getCourses().contains(opcourse.get())) {
		            return getVideo(lessId,request);
		        } else {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		        }
		    }

		    private ResponseEntity<?> handleTrainerRole(Long lessId, Long courseId, Muser user,HttpServletRequest request) {
		        Optional<CourseDetail> opcourse = coursedetailrepostory.findById(courseId);
		        if (opcourse.isPresent() && user.getAllotedCourses().contains(opcourse.get())) {
		            return getVideo(lessId ,request);
		        } else {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		        }
		    }
		    
		    private ResponseEntity<?> getVideo(Long lessId, HttpServletRequest request) {
		        try {
		            Optional<videoLessons> optionalLesson = lessonrepo.findById(lessId);
		            if (!optionalLesson.isPresent()) {
		                return ResponseEntity.notFound().build();
		            }
		            
		            videoLessons lesson = optionalLesson.get();
		            String filename = lesson.getVideofilename();
		            
		            if (filename != null) {
		            	Path filePath = Paths.get(videoStorageDirectory, filename);
		    		    try {
		    		        if (filePath.toFile().exists() && filePath.toFile().isFile()) {
		    		            Resource resource = new UrlResource(filePath.toUri());
		    		            if (resource.exists() && resource.isReadable()) {
		    		                HttpHeaders headers = new HttpHeaders();

		    		                // Set the Content-Type based on the file's extension
		    		                String mimeType = Files.probeContentType(filePath);
		    		                if (mimeType == null) {
		    		                    mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		    		                }
		    		                headers.add(HttpHeaders.CONTENT_TYPE, mimeType);

		    		                // Set Content-Disposition to "inline" to stream the video inline
		    		                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");

		    		                // Define the initial chunk size (5 MB)
		    		                final long INITIAL_CHUNK_SIZE = 5 * 1024 * 1024; // 5 MB
		    		                long fileSize = Files.size(filePath);

		    		                // Get the Range header from the request
		    		                String rangeHeader = request.getHeader(HttpHeaders.RANGE);

		    		                if (rangeHeader != null) {
		    		                    // Handle range request from the client
		    		                    String[] ranges = rangeHeader.replace("bytes=", "").split("-");
		    		                    long rangeStart = Long.parseLong(ranges[0]);
		    		                    long rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;

		    		                    // Calculate the content length
		    		                    long contentLength = rangeEnd - rangeStart + 1;

		    		                    System.out.println("Range Start: " + rangeStart + ", Range End: " + rangeEnd + ", Content Length: " + contentLength);
		    		                    // Create a RandomAccessFile to read the specified range
		    		                    try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
		    		                        file.seek(rangeStart);
		    		                        byte[] buffer = new byte[(int) contentLength];
		    		                        file.readFully(buffer);

		    		                        // Create a ByteArrayResource to hold the requested range of bytes
		    		                        ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);

		    		                        // Set the Content-Range header
		    		                        headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));
		    		                        System.out.println("Range Start: " + rangeStart + ", Range End: " + rangeEnd + ", Content Length: " + contentLength);

		    		                        // Return a 206 Partial Content response
		    		                        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
		    		                                .headers(headers)
		    		                                .contentLength(contentLength)
		    		                                .body(byteArrayResource);
		    		                    }
		    		                } else {
		    		                    // No range header, send the initial 5 MB chunk
		    		                    long rangeStart = 0;
		    		                    long rangeEnd = Math.min(INITIAL_CHUNK_SIZE - 1, fileSize - 1);
		    		                    long contentLength = rangeEnd - rangeStart + 1;
	    		                    System.out.println("Range Start: " + rangeStart + ", Range End: " + rangeEnd + ", Content Length: " + contentLength);

		    		                    // Create a RandomAccessFile to read the specified range
		    		                    try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
		    		                        file.seek(rangeStart);
		    		                        byte[] buffer = new byte[(int) contentLength];
		    		                        file.readFully(buffer);

		    		                        // Create a ByteArrayResource to hold the requested range of bytes
		    		                        ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);

		    		                        // Set the Content-Range header
		    		                        headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));

		    		                        // Return a 206 Partial Content response
		    		                        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
		    		                                .headers(headers)
		    		                                .contentLength(contentLength)
		    		                                .body(byteArrayResource);
		    		                    }
		    		                }
		    		            }
		    		        }
		    		    } catch (Exception e) {
		    		        // Handle exceptions
		    		        e.printStackTrace();
		    		    }

		    		    // Return a 404 Not Found response if the file does not exist
		    		    return ResponseEntity.notFound().build();

		            } else {
		                return ResponseEntity.ok(lesson.getFileUrl());
		            }
		        } catch (Exception e) {
		            // Log the exception (you can use a proper logging library)
		            e.printStackTrace();
		            // Return an internal server error response
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		        }
		        
		    }
		

//		    private ResponseEntity<?> getVideo(Long lessId) {
//		        try {
//		            Optional<videoLessons> optionalLesson = lessonrepo.findById(lessId);
//		            if (!optionalLesson.isPresent()) {
//		                return ResponseEntity.notFound().build();
//		            }
//		            
//		            videoLessons lesson = optionalLesson.get();
//		            String filename = lesson.getVideofilename();
//		            
//		            if (filename != null) {
//		                Path filePath = Paths.get(videoStorageDirectory, filename);
//		                if (filePath.toFile().exists() && filePath.toFile().isFile()) {
//		                    Resource resource = new UrlResource(filePath.toUri());
//		                    if (resource.exists() && resource.isReadable()) {
//		                        HttpHeaders headers = new HttpHeaders();
//		                        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//		                        return ResponseEntity.ok()
//		                                .headers(headers)
//		                                .body(resource);
//		                    }
//		                }
//		            } else {
//		                return ResponseEntity.ok(lesson.getFileUrl());
//		            }
//		        } catch (Exception e) {
//		            // Log the exception (you can use a proper logging library)
//		            e.printStackTrace();
//		            // Return an internal server error response
//		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		        }
//		        
//		        return ResponseEntity.notFound().build();
//		    }
//				 
//		 
		   		 
		 
		    
		    
		    
		    
		 //```````````````````````TO get Specific Lesson`````````````````````````````````````
		 @GetMapping("/getLessonsByid/{lessonId}")
		 private ResponseEntity<?>getlessonfromId(@PathVariable("lessonId")Long lessonId,
				@RequestHeader("Authorization") String token){
			 try {
				 if (!jwtUtil.validateToken(token)) {
		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		          }

		          String role = jwtUtil.getRoleFromToken(token);
		          if ("ADMIN".equals(role) || "TRAINER".equals(role)) {
		        	  Optional<videoLessons> oplesson =lessonrepo.findById(lessonId);
		        	  if(oplesson.isPresent()) {
		        		  videoLessons video=oplesson.get();
		        		  video.setCourseDetail(null);
						  video.setVideoFile(null);
						  
						  byte[] images =ImageUtils.decompressImage(video.getThumbnail());
						  video.setThumbnail(images);
		        		  return ResponseEntity.ok(video);		        	  
		        		  }
		        	  return ResponseEntity.notFound().build();
		        	  
		          }
		       
			 else {
		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		          }
		      } catch (Exception e) {
		          e.printStackTrace(); 
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		      }
				 
			 }
			 
		 
		 
		 
		 
		@DeleteMapping("/delete")
		private ResponseEntity<?> deleteLessonsByLessonId(@RequestParam("lessonId")Long lessonId,
				   @RequestParam("Lessontitle") String Lessontitle,
		          @RequestHeader("Authorization") String token){
			 try {
		          // Validate the token
		          if (!jwtUtil.validateToken(token)) {
		              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		          }

		          String role = jwtUtil.getRoleFromToken(token);
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
		
		
		
//		
//		@GetMapping("/{filename}/videofile")
//		public ResponseEntity<Resource> getVideoFile(@PathVariable String filename, HttpServletRequest request) {
//		    Path filePath = Paths.get(videoStorageDirectory, filename);
//		    try {
//		        if (filePath.toFile().exists() && filePath.toFile().isFile()) {
//		            Resource resource = new UrlResource(filePath.toUri());
//		            if (resource.exists() && resource.isReadable()) {
//		                HttpHeaders headers = new HttpHeaders();
//
//		                // Set the Content-Type based on the file's extension
//		                String mimeType = Files.probeContentType(filePath);
//		                if (mimeType == null) {
//		                    mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
//		                }
//		                headers.add(HttpHeaders.CONTENT_TYPE, mimeType);
//
//		                // Set Content-Disposition to "inline" to stream the video inline
//		                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");
//
//		                // Get the Range header from the request
//		                String rangeHeader = request.getHeader(HttpHeaders.RANGE);
//
//		                // Define a small chunk size (e.g., 5kb)
//		                final long CHUNK_SIZE = 5 * 1024 ; // 5kb
//
//		                // Check if a range request is made
//		                if (rangeHeader != null) {
//		                    // Parse the range request
//		                    String[] ranges = rangeHeader.replace("bytes=", "").split("-");
//		                    long rangeStart = Long.parseLong(ranges[0]);
//		                    long rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) : -1;
//
//		                    // Get the total size of the file
//		                    long fileSize = Files.size(filePath);
//
//		                    // If the end of the range is not specified, set it to the end of the file
//		                    if (rangeEnd == -1) {
//		                        rangeEnd = fileSize - 1;
//		                    }
//
//		                    // Calculate the content length
//		                    long contentLength = rangeEnd - rangeStart + 1;
//
//		                    // Check if the range length exceeds the chunk size
//		                    if (contentLength > CHUNK_SIZE) {
//		                        // Limit the range length to the chunk size
//		                        rangeEnd = rangeStart + CHUNK_SIZE - 1;
//		                        contentLength = rangeEnd - rangeStart + 1;
//		                    }
//
//		                    // Print the range start, end, and content length for debugging
//		                    System.out.println("Range Start: " + rangeStart + ", Range End: " + rangeEnd + ", Content Length: " + contentLength);
//
//		                    // Create a RandomAccessFile to read the specified range
//		                    RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r");
//		                    file.seek(rangeStart);
//		                    byte[] buffer = new byte[(int) contentLength];
//		                    file.readFully(buffer);
//
//		                    // Close the file
//		                    file.close();
//
//		                    // Create a ByteArrayResource to hold the requested range of bytes
//		                    ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);
//
//		                    // Set the Content-Range header
//		                    headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));
//
//		                    // Return a 206 Partial Content response
//		                    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//		                            .headers(headers)
//		                            .contentLength(contentLength)
//		                            .body(byteArrayResource);
//		                } 
//		            }
//		        }
//		    } catch (Exception e) {
//		        // Handle any exceptions (e.g., logging the error)
//		        e.printStackTrace();
//		    }
//
//		    // Return a 404 Not Found response if the file does not exist
//		    return ResponseEntity.notFound().build();
//		}

		
		
		@GetMapping("/{filename}/videofile/{token}")
		public ResponseEntity<Resource> getVideoFile(@PathVariable String filename,@PathVariable String token, HttpServletRequest request) {
		    Path filePath = Paths.get(videoStorageDirectory, filename);
		    try {
		        if (filePath.toFile().exists() && filePath.toFile().isFile()) {
		            Resource resource = new UrlResource(filePath.toUri());
		            if (resource.exists() && resource.isReadable()) {
		                HttpHeaders headers = new HttpHeaders();

		                // Set the Content-Type based on the file's extension
		                String mimeType = Files.probeContentType(filePath);
		                if (mimeType == null) {
		                    mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		                }
		                headers.add(HttpHeaders.CONTENT_TYPE, mimeType);

		                // Set Content-Disposition to "inline" to stream the video inline
		                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");

		                // Define the initial chunk size (5 MB)
		                final long INITIAL_CHUNK_SIZE = 5 * 1024 ; // 5 MB
		                long fileSize = Files.size(filePath);

		                // Get the Range header from the request
		                String rangeHeader = request.getHeader(HttpHeaders.RANGE);

		                if (rangeHeader != null) {
		                    // Handle range request from the client
		                    String[] ranges = rangeHeader.replace("bytes=", "").split("-");
		                    long rangeStart = Long.parseLong(ranges[0]);
		                    long rangeEnd = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;

		                    // Calculate the content length
		                    long contentLength = rangeEnd - rangeStart + 1;

		                    // Create a RandomAccessFile to read the specified range
		                    try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
		                        file.seek(rangeStart);
		                        byte[] buffer = new byte[(int) contentLength];
		                        file.readFully(buffer);

		                        // Create a ByteArrayResource to hold the requested range of bytes
		                        ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);

			                    System.out.println("Range Start: " + rangeStart + ", Range End: " + rangeEnd + ", Content Length: " + contentLength);
		                        // Set the Content-Range header
		                        headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));

		                        // Return a 206 Partial Content response
		                        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
		                                .headers(headers)
		                                .contentLength(contentLength)
		                                .body(byteArrayResource);
		                    }
		                } else {
		                    // No range header, send the initial 5 MB chunk
		                    long rangeStart = 0;
		                    long rangeEnd = Math.min(INITIAL_CHUNK_SIZE - 1, fileSize - 1);
		                    long contentLength = rangeEnd - rangeStart + 1;

		                    // Create a RandomAccessFile to read the specified range
		                    try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
		                        file.seek(rangeStart);
		                        byte[] buffer = new byte[(int) contentLength];
		                        file.readFully(buffer);

		                        // Create a ByteArrayResource to hold the requested range of bytes
		                        ByteArrayResource byteArrayResource = new ByteArrayResource(buffer);

		                        // Set the Content-Range header
		                        headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));

		                        // Return a 206 Partial Content response
		                        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
		                                .headers(headers)
		                                .contentLength(contentLength)
		                                .body(byteArrayResource);
		                    }
		                }
		            }
		        }
		    } catch (Exception e) {
		        // Handle exceptions
		        e.printStackTrace();
		    }

		    // Return a 404 Not Found response if the file does not exist
		    return ResponseEntity.notFound().build();
		}



}
