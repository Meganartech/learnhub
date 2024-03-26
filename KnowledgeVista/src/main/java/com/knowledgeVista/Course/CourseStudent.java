package com.knowledgeVista.Course;

import com.knowledgeVista.User.Muser;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter@Setter@NoArgsConstructor
public class CourseStudent {
	@Id
    @ManyToOne
    @JoinColumn(name = "courseId")
    private CourseDetail courseDetail;
	 @Id
	    @ManyToOne
	    @JoinColumn(name = "studentId")
	    private Muser student;
	 
    private Boolean isActive;
}
