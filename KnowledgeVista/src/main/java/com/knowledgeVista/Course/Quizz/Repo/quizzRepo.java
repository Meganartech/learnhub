package com.knowledgeVista.Course.Quizz.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.knowledgeVista.Course.Quizz.Quizz;
import com.knowledgeVista.Course.Quizz.ShedueleListDto;

@Repository
public interface quizzRepo extends JpaRepository<Quizz, Long>{

	@Modifying
	@Transactional
	@Query("DELETE FROM Quizz q WHERE q.quizzId=:quizzId")
	void deleteQuizzById(@Param("quizzId") Long quizzId);
	
	@Query("SELECT q.lessons.courseDetail.courseId FROM Quizz q WHERE q.quizzId=:quizzId")
	Long getCourseIDFromQuizzId(Long quizzId);
	
	@Query("SELECT COUNT(q) > 0 FROM Quizz q WHERE q.lessons.lessonId = :lessonID")
	boolean existsQuizzByLessonID(@Param("lessonID") Long lessonID);

	
//	@Query("SELECT new com.knowledgeVista.Course.Quizz.ShedueleListDto(q.quizzId, q.quizzName, q.lessons.lessonId, q.lessons.Lessontitle) " +
//		       "FROM Quizz q " +
//		       "JOIN q.lessons l " +
//		       "JOIN l.courseDetail c " +
//		       "JOIN c.batches b " +
//		       "WHERE c.courseId = :courseId " +
//		       "AND b.batchId = :batchId")
//		List<ShedueleListDto> getQuizzShedulesByCourseIdAndBatchId(@Param("courseId") Long courseId, @Param("batchId") String batchId);
	
	@Query("SELECT new com.knowledgeVista.Course.Quizz.ShedueleListDto(q.quizzId, q.quizzName, l.lessonId, l.Lessontitle, s.startDate, s.endDate) " +
		       "FROM Quizz q " +
		       "JOIN q.lessons l " +
		       "JOIN l.courseDetail c " +
		       "JOIN c.batches b " +
		       "LEFT JOIN q.schedules s " +  // Left join to include NULL if no schedule exists
		       "ON s.batch.batchId = :batchId " + // Ensure schedule belongs to the batch
		       "WHERE c.courseId = :courseId " +
		       "AND b.batchId = :batchId")
		List<ShedueleListDto> getQuizzShedulesByCourseIdAndBatchId(
		       @Param("courseId") Long courseId, 
		       @Param("batchId") String batchId);

} 
