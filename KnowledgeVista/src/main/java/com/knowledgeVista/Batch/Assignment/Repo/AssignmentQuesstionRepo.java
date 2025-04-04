package com.knowledgeVista.Batch.Assignment.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Batch.Assignment.AssignmentQuestion;

@Repository
public interface AssignmentQuesstionRepo extends JpaRepository<AssignmentQuestion, Long>{

}
