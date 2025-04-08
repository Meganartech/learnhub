package com.knowledgeVista.Batch.Assignment.Repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Batch.Assignment.Assignment;

@Repository
public interface AssignmentRepo extends JpaRepository<Assignment, Long> {
	@Query("""
			    SELECT
			        a.id AS id,
			        a.title AS title,
			        a.description AS description,
			        a.totalMarks AS totalMarks,
			        a.passingMarks AS passingMarks
			    FROM Assignment a
			    WHERE a.courseDetail.courseId = :courseId
			      AND a.courseDetail.institutionName = :institutionName
			""")
	List<Map<String, Object>> findAssignmentDetailsWithoutCourseAndQuestions(@Param("courseId") Long courseId,
			@Param("institutionName") String institutionName);

	@Query(value = "SELECT " + "a.id AS assignmentId, " + "a.title AS assignmentTitle, "
			+ "s.assignment_date AS assignmentDate " + "FROM assignment a "
			+ "JOIN course_detail c ON a.course_id = c.course_id "
			+ "LEFT JOIN assignment_schedule s ON s.assignment_id = a.id AND s.batch_id = :batchId "
			+ "WHERE c.course_id = :courseId", nativeQuery = true)
	List<Map<String, Object>> getAssignmentSchedulesByCourseIdAndBatchId(@Param("courseId") Long courseId,
			@Param("batchId") Long batchId);

}
