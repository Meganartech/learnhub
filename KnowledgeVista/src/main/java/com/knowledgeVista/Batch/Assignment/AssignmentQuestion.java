package com.knowledgeVista.Batch.Assignment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "assignment_id", nullable = false)
	private Assignment assignment;

	private String questionText; // Question statement

//	@ElementCollection
//	@CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
//	@Column(name = "option_text")
//	private List<String> options; // Only used for QUIZ
//
//	private String correctOption; // Only for QUIZ

	@Override
	public String toString() {
		return "AssignmentQuestion [id=" + id + ", assignment=" + assignment + ", questionText=" + questionText + "]";
	}
}
