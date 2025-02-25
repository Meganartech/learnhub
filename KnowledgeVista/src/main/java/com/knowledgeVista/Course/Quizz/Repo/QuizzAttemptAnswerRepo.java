package com.knowledgeVista.Course.Quizz.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.Quizz.QuizAttemptAnswer;

@Repository
public interface QuizzAttemptAnswerRepo extends JpaRepository<QuizAttemptAnswer, Long> {

}
