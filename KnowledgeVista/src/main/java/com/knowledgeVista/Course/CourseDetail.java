package com.knowledgeVista.Course;



import java.util.List;
import com.knowledgeVista.User.Muser;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter@NoArgsConstructor
public class CourseDetail {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name="courseId")
	    private Long courseId;
	    @Column(name="courseName")
	    private String courseName;
	    @Column(name="courseUrl")
	    private String courseUrl;
	    @Column(name="courseDescription" ,length=1000)
	    private String courseDescription;
	    @Column(name="courseCategory")
	    private String courseCategory;
	    @Column(name="amount")
	    private Long amount;
	    @Lob
	    @Column(name="courseImage" ,length=1000000)
	    private byte[] courseImage;
	   
	    
	    
	    @ManyToMany(mappedBy = "courses")
	    private List<Muser> users;
	    
	    
	    @Column(name="Trainer")
	    private String Trainer;
	    
	    @Column(name="Duration")
	    private Long Duration;
	    
	    @Column(name="Noofseats")
	    private Long Noofseats;

	    @OneToMany(mappedBy = "courseDetail")
	    private List<videoLessons> videoLessons;
	    
	    

		
	    
	   
}
