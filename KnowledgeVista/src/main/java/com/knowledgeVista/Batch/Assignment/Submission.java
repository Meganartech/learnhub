package com.knowledgeVista.Batch.Assignment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    private Long studentId; // Reference to Student ID

    @ElementCollection
    @CollectionTable(name = "submission_answers", joinColumns = @JoinColumn(name = "submission_id"))
    @MapKeyColumn(name = "question_id") // Key = Question ID
    @Column(name = "answer_text")
    private Map<Long, String> answers; // Map<QuestionID, AnswerText>

    private LocalDateTime submittedAt;
    private boolean isGraded;
    private Integer totalMarksObtained;
    private String feedback;
}

