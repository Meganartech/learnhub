package com.knowledgeVista.Course.Controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.DTO.CourseRequestDTO;
import com.knowledgeVista.Course.Lessons.CourseLesson;

import com.knowledgeVista.Course.Lessons.LessonNotes;
import com.knowledgeVista.Course.Repository.CourseDetailRepository;
import com.knowledgeVista.Course.Repository.CourseLessonRepository;

import com.knowledgeVista.Course.Repository.LessonNotesRepository;
import com.knowledgeVista.FileService.VideoFileService;

@RestController
@RequestMapping("/Lesson")
@CrossOrigin
public class LessonController {
	@Autowired
	private CourseLessonRepository courselessonrepository;
	@Autowired
	private CourseDetailRepository coursedetailrepostory;

	@Autowired
	private LessonNotesRepository lessonnotesrepository;
	@Autowired
	private VideoFileService fileService;
	
  //`````````````````WORKING```````````````````````````````````````````

	@GetMapping("/getAllLessons/{courseId}")
	public ResponseEntity<?> getCourseLessons(@PathVariable Long courseId) {
	    try {
	        // Retrieve the CourseDetail entity based on the courseId
	        Optional<CourseDetail> courseDetailOptional = coursedetailrepostory.findById(courseId);
	        if (!courseDetailOptional.isPresent()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CourseDetail not found for courseId: " + courseId);
	        }

	        CourseDetail courseDetail = courseDetailOptional.get();

	        // Fetch all CourseLessons associated with the CourseDetail using a custom query
	        List<CourseLesson> courseLessons = courselessonrepository.findByCourseDetail(courseDetail);
	        if(courseLessons.isEmpty()) {
	        	  return ResponseEntity.notFound().build();
	        }

	        // Create a list to hold maps representing CourseLesson entities
	        List<Map<String, Object>> courseLessonMaps = new ArrayList<>();
	        for (CourseLesson lesson : courseLessons) {
	            // Create a map to hold the fields of CourseLesson
	            Map<String, Object> lessonMap = new HashMap<>();
	            lessonMap.put("lessonId", lesson.getLessonId());
	            lessonMap.put("lessonTitle", lesson.getLessonTitle());
	            lessonMap.put("lessonDesc", lesson.getLessonDesc());
	            lessonMap.put("lessonUrl", lesson.getLessonUrl());
	            lessonMap.put("isActive", lesson.getIsActive());

	            // Add the map to the list
	            courseLessonMaps.add(lessonMap);
	        }

	        // Return the list of maps representing CourseLesson entities
	        return ResponseEntity.ok(courseLessonMaps);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Failed to fetch course lessons: " + e.getMessage());
	    }
	}


	
	
	//`````````````````WORKING```````````````````````````````````````````
	  @GetMapping("/{id}")
	    public ResponseEntity<?> getLessonById(@PathVariable("id") Long id) {
	        CourseLesson lesson = courselessonrepository.findById(id).orElse(null);
	        if (lesson == null) {
	            return ResponseEntity.notFound().build();
	        }
	        Map<String, Object> lessonMap = new HashMap<>();
            lessonMap.put("lessonId", lesson.getLessonId());
            lessonMap.put("lessonTitle", lesson.getLessonTitle());
            lessonMap.put("lessonDesc", lesson.getLessonDesc());
            lessonMap.put("lessonUrl", lesson.getLessonUrl());
            lessonMap.put("isActive", lesson.getIsActive());
	        return ResponseEntity.ok(lessonMap);
	    }
	  
	
	 //`````````````````WORKING```````````````````````````````````````````
	  
	  @PostMapping("/save/{courseId}")
	  private ResponseEntity<String> savenote(@RequestParam("lessonTitle") String lessonTitle,
	                                          @RequestParam("lessonDesc") String lessonDesc,
	                                          @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
	                                          @RequestParam("notesTitle") String notesTitle,
	                                          @RequestParam("notesDesc") String notesDesc,
	                                          @RequestParam(value = "fileUrl", required = false) String fileUrl,
	                                          @PathVariable Long courseId) {
	      try {
	          Optional<CourseDetail> courseDetailOptional = coursedetailrepostory.findById(courseId);
	          if (!courseDetailOptional.isPresent()) {
	              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"CourseDetail not found for courseId: " + courseId + "\"}");
	          }

	          CourseDetail courseDetail = courseDetailOptional.get();

	          CourseLesson courseLesson = new CourseLesson();
	          courseLesson.setLessonTitle(lessonTitle);
	          courseLesson.setLessonDesc(lessonDesc);
	          courseLesson.setIsActive(true);
	          courseLesson.setCourseDetail(courseDetail);
	          CourseLesson savedLesson = courselessonrepository.save(courseLesson);
	          Long lessonId = savedLesson.getLessonId();

	          LessonNotes note = new LessonNotes();
	          note.setNotesTitle(notesTitle);
	          note.setNotesDesc(notesDesc);

	          if (videoFile != null) {
	              String videoFilePath = fileService.saveVideoFile(videoFile);
	              note.setVideofilename(videoFilePath);
	              note.setVideoFile(videoFile);
	          } else if (fileUrl != null && !fileUrl.isEmpty()) {
	              note.setFileUrl(fileUrl);
	          } else {
	              return ResponseEntity.badRequest().body("{\"error\": \"Either video file or file URL must be provided\"}");
	          }

	          note.setIsActive(true);

	          Optional<CourseLesson> optionalLesson = courselessonrepository.findById(lessonId);
	          if (optionalLesson.isPresent()) {
	              CourseLesson courseLessonfinal = optionalLesson.get();
	              note.setCourseLesson(courseLessonfinal);
	              lessonnotesrepository.save(note);
	              return ResponseEntity.ok("{\"message\": \"Note saved successfully\"}");
	          } else {
	              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"CourseLesson not found with ID: " + lessonId + "\"}");
	          }
	      } catch (Exception e) {
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to save note: " + e.getMessage() + "\"}");
	      }
	  }

//------------------------------WORKING--------------------------------
	           
	  @DeleteMapping("/deletelesson/{lessonId}")
	  public ResponseEntity<String> deleteCourseLesson(@PathVariable Long lessonId) {
	      try {
	          // Find the course lesson by its ID
	          CourseLesson courseLesson = courselessonrepository.findById(lessonId).orElse(null);

	          if (courseLesson == null) {
	              return ResponseEntity.notFound().build();
	          }

	          // Get the associated lesson notes
	          List<LessonNotes> lessonNotesList = courseLesson.getLessonNotesList();

	          // Delete each lesson note
	          for (LessonNotes lessonNotes : lessonNotesList) {
	              lessonnotesrepository.delete(lessonNotes);
	          }

	          // Delete the course lesson itself
	          courselessonrepository.delete(courseLesson);

	          // Construct the response string with the success message
	          String responseMessage = "{\"message\": \"Course lesson and associated notes deleted successfully\"}";

	          return ResponseEntity.ok(responseMessage);
	      } catch (Exception e) {
	          e.printStackTrace();
	          return ResponseEntity.badRequest().build();
	      }
	  }


}