package com.knowledgeVista.Payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface installmentdetilsrepo extends JpaRepository<InstallmentDetails, Long>{

}
