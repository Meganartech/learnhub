package com.knowledgeVista.Course.Lessons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter@NoArgsConstructor
public class LessonControls {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	   private Long lesscontrolId;
	   
//	    @OneToOne
//	    @JoinColumn(name = "course_Lesson_id")
//	    private CourseLesson courseLesson;
	    private Boolean isDraft;
	    private Boolean isFree;
	    private Boolean isDownloadable;
	    private Boolean isPrerequisite;
	    private Boolean isDiscussionEnabled;
	    private Boolean isActive;
}
