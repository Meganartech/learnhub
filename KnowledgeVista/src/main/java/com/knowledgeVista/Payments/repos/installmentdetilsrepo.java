package com.knowledgeVista.Payments.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Payments.InstallmentDetails;
@Repository
public interface installmentdetilsrepo extends JpaRepository<InstallmentDetails, Long>{

}
