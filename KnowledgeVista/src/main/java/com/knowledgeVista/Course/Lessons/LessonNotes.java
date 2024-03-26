package com.knowledgeVista.Course.Lessons;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter@NoArgsConstructor
public class LessonNotes {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long notesId;
	    private String notesTitle;
	    @Column(length=1000)
	    private String notesDesc ;
	    private Boolean IsActive;
	    
	    @Transient
	    @Column(nullable = true)
		private MultipartFile videoFile;
	    @Column(nullable = true)
	    private String videofilename;
	    @Column(nullable = true)
	    private String fileUrl;
	    @ManyToOne 
	   
	    @JoinColumn(name = "lessonNotes_id") 
	    private CourseLesson courseLesson;

}
