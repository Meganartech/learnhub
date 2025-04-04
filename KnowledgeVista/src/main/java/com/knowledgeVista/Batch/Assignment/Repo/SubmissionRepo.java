package com.knowledgeVista.Batch.Assignment.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.knowledgeVista.Batch.Assignment.Submission;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, Long> {

}
