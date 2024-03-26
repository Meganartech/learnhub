package com.knowledgeVista.Course.Lessons;

import java.util.List;

import com.knowledgeVista.Course.CourseDetail;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter@Setter@NoArgsConstructor
public class CourseLesson {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long lessonId;
	    private String lessonTitle;
	    @Column(length=1000)
	    private String lessonDesc;
	    private String lessonUrl;
	    private Boolean IsActive;
	    
//	    @OneToOne
//	    @JoinColumn(name = "lesson_id") // Specify a unique name for the join column
//	    private LessonControls lessonControl;
	    
	    @ManyToOne
	    @JoinColumn(name = "courseId" )
	    private CourseDetail courseDetail;
	    
	    @OneToMany(mappedBy = "courseLesson" ) // Mapping many LessonNotes to a single CourseLesson
	    private List<LessonNotes> lessonNotesList;
	 
	    
}
