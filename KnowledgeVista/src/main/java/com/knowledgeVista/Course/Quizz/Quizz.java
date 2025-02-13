package com.knowledgeVista.Course.Quizz;

import java.util.List;

import com.knowledgeVista.Course.videoLessons;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter@Setter@NoArgsConstructor
public class Quizz {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long quizzId;
	 private String quizzName;
	 private String instituionName;
	  @OneToOne
	    @JoinColumn(name = "lessonId", referencedColumnName = "lessonId", unique = true) // Foreign key in Quizz table
	    private videoLessons lessons;
	 @OneToMany(mappedBy = "quizz", cascade = CascadeType.REMOVE,orphanRemoval = true)
	    private List<Quizzquestion> Quizzquestions;
}
