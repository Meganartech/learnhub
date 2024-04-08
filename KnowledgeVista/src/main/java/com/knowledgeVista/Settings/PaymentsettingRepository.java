package com.knowledgeVista.Settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsettingRepository extends JpaRepository<Paymentsettings, Long> {

}


