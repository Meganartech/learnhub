package com.knowledgeVista.Batch.Weightage.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.knowledgeVista.Batch.Weightage.Weightage;

public interface WeightageRepo extends JpaRepository<Weightage, Long>{

	@Query("SELECT w FROM Weightage w WHERE w.institutionName=:institution")
	Optional<Weightage> findbyInstittionName(String institution);
}
