package com.knowledgeVista.Batch.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Batch.BatchInstallmentdetails;

@Repository
public interface BatchInstallmentDetailsRepo extends JpaRepository<BatchInstallmentdetails,Long> {

}
