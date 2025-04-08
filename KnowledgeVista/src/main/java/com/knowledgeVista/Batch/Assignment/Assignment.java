package com.knowledgeVista.Batch.Assignment;

import java.util.List;

import com.knowledgeVista.Course.CourseDetail;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@Column(length = 1000)
	private String description;
	private Integer totalMarks;
	private Integer passingMarks;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private CourseDetail courseDetail;

	@OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AssignmentQuestion> questions; // List of essay-type questions
	@OneToMany(mappedBy = "Assignment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AssignmentSchedule> schedules;

	@Override
	public String toString() {
		return "Assignment [id=" + id + ", title=" + title + ", description=" + description + ", totalMarks="
				+ totalMarks + ", passingMarks=" + passingMarks + ", courseDetail=" + courseDetail + ", questions="
				+ questions + "]";
	}
}
