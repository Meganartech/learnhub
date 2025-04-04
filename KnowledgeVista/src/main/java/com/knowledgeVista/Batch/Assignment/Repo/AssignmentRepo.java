package com.knowledgeVista.Batch.Assignment.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.knowledgeVista.Batch.Assignment.Assignment;

@Repository
public interface AssignmentRepo extends JpaRepository<Assignment, Long> {

}
