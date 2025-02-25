package com.knowledgeVista.Course.Quizz.DTO;

import java.util.List;

import com.knowledgeVista.Course.Quizz.QuizAttemptAnswer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {
 private Long questionId;
 private String selected;
 



public static class QuizAnswerResult {
    private List<QuizAttemptAnswer> savedAnswers;
    private double score;
	public List<QuizAttemptAnswer> getSavedAnswers() {
		return savedAnswers;
	}
	public void setSavedAnswers(List<QuizAttemptAnswer> savedAnswers) {
		this.savedAnswers = savedAnswers;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
}
}