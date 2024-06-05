package com.knowledgeVista.Payments;

import java.time.LocalDate;

import com.knowledgeVista.Course.CourseDetail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter@Setter@NoArgsConstructor
public class Course_Payment_Structure {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	private Long paymenttypeId;
	private Long amount;
	private LocalDate Date;
	private Long CreatedBy;
	private Long ApprevedBy; 
	
	 @ManyToOne
	    @JoinColumn(name = "courseId")
	 private CourseDetail course;
	
	

}
