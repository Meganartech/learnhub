package com.knowledgeVista.Payments;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsettingRepository extends JpaRepository<Paymentsettings, Long> {

	@Query("SELECT u FROM Paymentsettings u WHERE u.institutionName = ?1")
	Optional<Paymentsettings>findByinstitutionName(String institutionName);
}


