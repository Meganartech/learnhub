package com.knowledgeVista.Course.Quizz.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.Quizz.QuizzSchedule;
@Repository
public interface QuizzSheduleRepo extends JpaRepository<QuizzSchedule, Long>{
	
	@Query("SELECT q FROM QuizzSchedule q WHERE q.quiz.quizzId = :quizzId AND q.batch.batchId = :batchId")
	Optional<QuizzSchedule> findByQuizzIdAndBatchId(@Param("quizzId") Long quizzId, @Param("batchId") String batchId);


}
