package com.knowledgeVista.Course.moduleTest.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.moduleTest.SheduleModuleTest;

@Repository
public interface SheduleModuleTestRepo extends JpaRepository<SheduleModuleTest, Long>{
	
	@Query("SELECT q FROM SheduleModuleTest q WHERE q.mtest.mtestId = :mtestId AND q.batch.batchId = :batchId")
	Optional<SheduleModuleTest> findByModuleTestIdAndBatchId(@Param("mtestId") Long mtestId, @Param("batchId") String batchId);
}
