package com.knowledgeVista.Course.Test;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.User.Muser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter@NoArgsConstructor
public class MuserTestActivity {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long activityId;

	   @ManyToOne
	    @JoinColumn(name = "userId")
	    private Muser user;
        @ManyToOne
        @JoinColumn(name="courseId")
        private CourseDetail course;
	    @ManyToOne 
	    @JoinColumn(name = "testId")
	    @JsonBackReference
	    private CourseTest test;
	    
	    private LocalDate testDate;
	    private Long nthAttempt;
	   
	    private Double percentage;
	    
}
