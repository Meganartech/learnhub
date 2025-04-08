package com.knowledgeVista.Batch.Assignment;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
public class AssignmentDTO {
	 private Long id;
	    private String title;
	    private String description;
	    private Integer totalMarks;
	    private Integer passingMarks;
	    private List<AssignmentQuestionDTO> questions;

	    public AssignmentDTO(Long id, String title, String description, Integer totalMarks, Integer passingMarks, List<AssignmentQuestionDTO> questions) {
	        this.id = id;
	        this.title = title;
	        this.description = description;
	        this.totalMarks = totalMarks;
	        this.passingMarks = passingMarks;
	        this.questions = questions;
	    }

	    // Getters

	    public static class AssignmentQuestionDTO {
	        private Long id;
	        private String questionText;

	        public AssignmentQuestionDTO(Long id, String questionText) {
	            this.id = id;
	            this.questionText = questionText;
	        }

	        // Getters
	    }
}
