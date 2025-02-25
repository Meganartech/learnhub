package com.knowledgeVista.Course.Quizz.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.Quizz.QuizAttempt;

import jakarta.transaction.Transactional;

@Repository
public interface QuizzattemptRepo extends JpaRepository<QuizAttempt, Long> {
	
	@Query("SELECT q FROM QuizAttempt q WHERE q.user.userId=:userId AND q.quiz.quizzId=:quizzId")
	Optional<QuizAttempt>findbyquizzIdandUserId(Long userId,Long quizzId);
	@Modifying
	@Transactional
	@Query("DELETE FROM QuizAttempt q WHERE q.quiz.quizzId=:quizzId ")
	void deleteByQuizzId(Long quizzId);

}
