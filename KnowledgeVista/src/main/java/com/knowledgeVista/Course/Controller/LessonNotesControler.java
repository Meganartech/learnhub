package com.knowledgeVista.Course.Controller;

import java.nio.file.Paths;
import org.springframework.http.MediaType;
import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.knowledgeVista.Course.Lessons.CourseLesson;
import com.knowledgeVista.Course.Lessons.LessonNotes;
import com.knowledgeVista.Course.Repository.CourseLessonRepository;

import com.knowledgeVista.Course.Repository.LessonNotesRepository;
import com.knowledgeVista.FileService.VideoFileService;



import org.springframework.core.io.Resource;


@RestController
@RequestMapping("/Notes")
@CrossOrigin
public class LessonNotesControler {

	@Autowired
	private LessonNotesRepository lessonnoterepository;
	@Autowired
	private CourseLessonRepository courselessonrepository;
   
	@Autowired
	private VideoFileService fileService;
	
	private final String videoStorageDirectory = "video/";

	//------------------------WORKING NEED TO IMPLEMENT-------------------------
	@PostMapping("/save/{lessonId}")
	private ResponseEntity<?> savenote(@RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
	                                    @RequestParam("notesTitle") String notesTitle,
	                                    @RequestParam("notesDesc") String notesDesc,
	                                    @RequestParam(value = "fileUrl", required = false) String fileUrl,
	                                    @PathVariable Long lessonId) { 
	    try {  
	        // Create a new LessonNotes object and set its properties
	        LessonNotes note = new LessonNotes();
	        note.setNotesTitle(notesTitle);
	        note.setNotesDesc(notesDesc);
	        
	        if (videoFile != null) {
	            // If videoFile is provided, save it
	            String videoFilePath = fileService.saveVideoFile(videoFile);
	            note.setVideofilename(videoFilePath);
	            note.setVideoFile(videoFile);
	        } else if (fileUrl != null && !fileUrl.isEmpty()) {
	            // If only fileUrl is provided
	            note.setFileUrl(fileUrl);
	        } else {
	            // Neither videoFile nor fileUrl is provided
	            return ResponseEntity.badRequest().body("{\"message\": \"Either video file or file URL must be provided\"}");
	        }
	        
	        note.setIsActive(true);

	        // Retrieve the course lesson from the repository
	        Optional<CourseLesson> optionalLesson = courselessonrepository.findById(lessonId);
	        
	        if (optionalLesson.isPresent()) {
	            CourseLesson courseLesson = optionalLesson.get();
	            note.setCourseLesson(courseLesson);
	            lessonnoterepository.save(note);
	            return ResponseEntity.ok("{\"message\": \"Note saved successfully\"}");
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"CourseLesson not found for lessonId: " + lessonId + "\"}");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Failed to save note: " + e.getMessage() + "\"}");
	    }
	}


//------------------------------------WORKING---------------------------------------

	@GetMapping("/getNote/{lessonId}")
	private ResponseEntity<?> getnote (@PathVariable Long lessonId)
	{
		try {
			Optional<CourseLesson> optionallesson=courselessonrepository.findById(lessonId);
			if(optionallesson.isPresent()) {
				List<LessonNotes> lessonlist=lessonnoterepository.findByCourseLessonLessonId(lessonId);
				   List<Map<String, Object>> notesMap = new ArrayList<>();
				   for(LessonNotes note:lessonlist)
				   {
					   Map<String, Object> noteMap = new HashMap<>();
					   noteMap.put("notesId", note.getNotesId());
					   noteMap.put("notesTitle", note.getNotesTitle());
					   noteMap.put("notesDesc", note.getNotesDesc());
					   noteMap.put("IsActive", note.getIsActive());
					   noteMap.put("fileUrl", note.getFileUrl());
					   
					   notesMap.add(noteMap);
				   }
				   return ResponseEntity.ok(notesMap);
				
			}else {
				  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for lessonId: " + lessonId);
			}
		}catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Failed to fetch course Notes: " + e.getMessage());
	    }
	}
	
	
//	@GetMapping("/getnoteByid/{notesId}")
//	public ResponseEntity<Resource> getVideoFile(@PathVariable Long notesId) {
//	    Optional<LessonNotes> optionalNote = lessonnoterepository.findById(notesId);
//	    if (optionalNote.isPresent()) {
//	        LessonNotes note = optionalNote.get();
//	        String filename = note.getVideofilename();
//	        Path filePath = Paths.get(videoStorageDirectory, filename);
//	        try {
//	            if (filePath.toFile().exists() && filePath.toFile().isFile()) {
//	                Resource resource = new UrlResource(filePath.toUri());
//	                if (resource.exists() && resource.isReadable()) {
//	                    HttpHeaders headers = new HttpHeaders();
//	                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//	                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
//	                    return ResponseEntity.ok()
//	                            .headers(headers)
//	                            .body(resource);
//	                }
//	            }
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	    }
//	    return ResponseEntity.notFound().build();
//	}
	@GetMapping("/getnoteByid/{notesId}")
	public ResponseEntity<Resource> getVideoFile(@PathVariable Long notesId) {
	    Optional<LessonNotes> optionalNote = lessonnoterepository.findById(notesId);
	    if (optionalNote.isPresent()) {
	        LessonNotes note = optionalNote.get();
	        String filename = note.getVideofilename();
	        if (filename != null) { // Check if filename is not null
	            Path filePath = Paths.get(videoStorageDirectory, filename);
	            try {
	                if (filePath.toFile().exists() && filePath.toFile().isFile()) {
	                    Resource resource = new UrlResource(filePath.toUri());
	                    if (resource.exists() && resource.isReadable()) {
	                        HttpHeaders headers = new HttpHeaders();
	                        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
	                        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
	                        return ResponseEntity.ok()
	                                .headers(headers)
	                                .body(resource);
	                    }
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    return ResponseEntity.notFound().build();
	}




	
	//``````````````````optional to check video `````````````````````````
	@GetMapping("/{filename}/videofile")
    public ResponseEntity<Resource> getVideoFi(@PathVariable String filename) {
		

		
        Path filePath = Paths.get(videoStorageDirectory,filename);
        try {
            // Check if the file exists
            if (filePath.toFile().exists() && filePath.toFile().isFile()) {
                // Return the audio file as a resource
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() && resource.isReadable()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(resource);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }
	//`````````````````````````````````````WORKING NOT USED IN FRONTEND`````````````````````````````````````````
	
	 @DeleteMapping("/deletenote/{id}")
	    public ResponseEntity<String> deleteNote(@PathVariable Long id) {
	        try {
	            Optional<LessonNotes> optionalNote = lessonnoterepository.findById(id);

	            if (optionalNote.isPresent()) {
	                LessonNotes lessonNote = optionalNote.get();
	                if (lessonNote.getVideofilename() != null) {
	                    boolean videoFileDeleted = fileService.deleteVideoFile(lessonNote.getVideofilename());
	                    if (videoFileDeleted) {
	                        lessonnoterepository.deleteById(id);
	                        return ResponseEntity.ok("{\"message\": \"Note and associated video file deleted successfully\"}");
	                    } else {
	                        // Video file deletion failed
	                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                .body("{\"message\": \"Failed to delete video file associated with the note\"}");
	                    }
	                } else {
	                    // Video file is null, delete only the note
	                    lessonnoterepository.deleteById(id);
	                    return ResponseEntity.ok("{\"message\": \"Note without associated video file deleted successfully\"}");
	                }
	            } else {
	                // Note with given ID not found
	                return ResponseEntity.notFound().build();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.badRequest().build();
	        }
	    }

	
}
