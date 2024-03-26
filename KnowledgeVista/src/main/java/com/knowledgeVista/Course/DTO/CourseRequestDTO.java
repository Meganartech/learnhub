package com.knowledgeVista.Course.DTO;

import java.util.List;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Lessons.CourseLesson;

import com.knowledgeVista.Course.Lessons.LessonNotes;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter@Setter@NoArgsConstructor
public class CourseRequestDTO {
	
	 private CourseDetail courseDetail;
	    
	    private CourseLesson courseLesson;
	    private List<LessonNotes> lessonNotesList;
	    
}
