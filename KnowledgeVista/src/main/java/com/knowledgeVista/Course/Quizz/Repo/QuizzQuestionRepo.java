package com.knowledgeVista.Course.Quizz.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.knowledgeVista.Course.Quizz.Quizzquestion;

@Repository
public interface QuizzQuestionRepo extends JpaRepository<Quizzquestion, Long> {

	@Query("SELECT new com.knowledgeVista.Course.Quizz.Quizzquestion(a.questionId, a.questionText, a.option1, a.option2, a.option3, a.option4, a.answer) FROM Quizzquestion a WHERE a.quizz.quizzId = :quizzId")
	List<Quizzquestion> findByQuizzId(Long quizzId);
	
	@Query("SELECT new com.knowledgeVista.Course.Quizz.Quizzquestion(a.questionId, a.questionText, a.option1, a.option2, a.option3, a.option4, a.answer) FROM Quizzquestion a WHERE a.questionId = :questionId")
	Optional<Quizzquestion> findByIdrequired(Long questionId);
	
	@Modifying
	@Transactional 
	@Query("DELETE FROM Quizzquestion q WHERE q.questionId IN :questionIds AND q.quizz.quizzId = :quizzId")
	void deleteByQuestionIdsAndQuizzId(@Param("questionIds") List<Long> questionIds, @Param("quizzId") Long quizzId);

	@Query("SELECT COUNT(q) FROM Quizzquestion q WHERE q.quizz.quizzId = :quizzId")
	Long countByQuizzId(@Param("quizzId") Long quizzId);


}
