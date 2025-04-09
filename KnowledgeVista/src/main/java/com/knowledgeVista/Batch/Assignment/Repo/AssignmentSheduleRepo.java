package com.knowledgeVista.Batch.Assignment.Repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Batch.Assignment.AssignmentSchedule;

@Repository
public interface AssignmentSheduleRepo extends JpaRepository<AssignmentSchedule, Long> {
	@Query("SELECT q FROM AssignmentSchedule q WHERE q.Assignment.id = :AssignmentId AND q.batch.id = :batchId")
	Optional<AssignmentSchedule> findByAssignmentIdAndBatchId(Long batchId, Long AssignmentId);

	@Query("SELECT q.AssignmentDate FROM AssignmentSchedule q WHERE q.Assignment.id = :AssignmentId AND q.batch.id = :batchId")
	LocalDate findSheduleDateByAssignmentIDAndbatchID(Long batchId, Long AssignmentId);
}
