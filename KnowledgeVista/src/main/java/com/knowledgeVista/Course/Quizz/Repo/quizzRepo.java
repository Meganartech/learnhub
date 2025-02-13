package com.knowledgeVista.Course.Quizz.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.knowledgeVista.Course.Quizz.Quizz;

@Repository
public interface quizzRepo extends JpaRepository<Quizz, Long>{

	@Modifying
	@Transactional
	@Query("DELETE FROM Quizz q WHERE q.quizzId=:quizzId")
	void deleteQuizzById(@Param("quizzId") Long quizzId);

}
