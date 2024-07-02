package com.knowledgeVista.Course;



import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter@NoArgsConstructor
public class videoLessons {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long lessonId;
	   @ManyToOne
	    @JoinColumn(name = "courseId" )
	    private CourseDetail courseDetail;
	    @Column(name="institution")
	    private String institutionName;
	    private String Lessontitle;

	    @Column(length=1000)
	    private String LessonDescription;
	    @Lob
	    @Column(name="thumbnail" ,length=1000000)
	    private byte[] thumbnail;
	    
	    @Transient
	    @Column(nullable = true)
		private MultipartFile videoFile;
	    
	    private Long size;
	    
	    @Column(nullable = true)
	    private String videofilename;
	    @Column(nullable = true)
	    private String fileUrl;
	    

}
