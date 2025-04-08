package com.knowledgeVista.Batch.Assignment;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.knowledgeVista.Course.CourseDetail;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column( length = 1000)
    private String description;
    private Integer totalMarks;
    private Integer passingMarks;
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "course_id", nullable = false) 
    private CourseDetail courseDetail; 

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentQuestion> questions; // List of essay-type questions

	@Override
	public String toString() {
		return "Assignment [id=" + id + ", title=" + title + ", description=" + description + ", totalMarks="
				+ totalMarks + ", passingMarks=" + passingMarks + ", courseDetail=" + courseDetail + ", questions="
				+ questions + "]";
	}
}

