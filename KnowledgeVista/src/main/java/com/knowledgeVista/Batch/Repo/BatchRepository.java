package com.knowledgeVista.Batch.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Batch.Batch;
import com.knowledgeVista.Batch.BatchDto;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
	
	@Query("SELECT b FROM Batch b WHERE b.id = :id AND b.institutionName = :institutionName")
	Optional<Batch> findBatchByIdAndInstitutionName(@Param("id") Long id, @Param("institutionName") String institutionName);
	
//	@Query("SELECT new com.knowledgeVista.Batch.BatchDto( " +
//		       "b.id, b.batchId, b.batchTitle, b.startDate, b.endDate, b.institutionName, b.noOfSeats, b.amount, " +
//		       "CAST(STRING_AGG(c.courseName, ',') AS string), CAST(STRING_AGG(t.username, ',') AS string)) " +
//		       "FROM Batch b " +
//		       "JOIN b.courses c " +
//		       "JOIN b.trainers t " +
//		       "WHERE b.id = :id AND b.institutionName = :institutionName " +
//		       "GROUP BY b.id, b.batchId, b.batchTitle, b.startDate, b.endDate, b.institutionName, b.noOfSeats, b.amount")
//		Optional<BatchDto> findBatchDtoByIdAndInstitutionName(@Param("id") Long id, @Param("institutionName") String institutionName);
	
	@Query("SELECT new com.knowledgeVista.Batch.BatchDto( " +
		       "b.id, b.batchId, b.batchTitle, b.startDate, b.endDate, b.institutionName, b.noOfSeats, b.amount, " +
		       "CAST(STRING_AGG(DISTINCT CAST(c.courseName AS string), ',') AS string), " +
		       "CAST(STRING_AGG(DISTINCT CAST(t.username AS string), ',') AS string), " +
		       "b.BatchImage) " +
		       "FROM Batch b " +
		       "JOIN b.courses c " +
		       "JOIN b.trainers t " +
		       "WHERE b.id = :id AND b.institutionName = :institutionName " +
		       "GROUP BY b.id, b.batchId, b.batchTitle, b.startDate, b.endDate, b.institutionName, b.noOfSeats, b.amount, b.BatchImage")
		Optional<BatchDto> findBatchDtoByIdAndInstitutionName(@Param("id") Long id, @Param("institutionName") String institutionName);


	@Query("SELECT new com.knowledgeVista.Batch.BatchDto( " +
		       "b.id, b.batchId, b.batchTitle, b.startDate, b.endDate, b.institutionName, b.noOfSeats, b.amount, " +
		       "COALESCE(CAST(STRING_AGG(c.courseName, ',') AS string), ''), " +
		       "COALESCE(CAST(STRING_AGG(t.username, ',') AS string), ''), " +
		       "b.BatchImage) " +
		       "FROM Batch b " +
		       "LEFT JOIN b.courses c " +
		       "LEFT JOIN b.trainers t " +
		       "WHERE b.institutionName = :institutionName " +
		       "GROUP BY b.id, b.batchId, b.batchTitle, b.startDate, b.endDate, b.institutionName, b.noOfSeats, b.amount, b.BatchImage")
		List<BatchDto> findAllBatchDtosByInstitution(@Param("institutionName") String institutionName);

	@Query("SELECT b.id, b.batchId, b.batchTitle " +
		       "FROM Batch b " +
		       "WHERE (:batchtitle IS NOT NULL AND :batchtitle <> '' AND LOWER(b.batchTitle) LIKE LOWER(CONCAT('%', :batchtitle, '%'))) " +
		       "AND (:institutionName IS NOT NULL AND :institutionName <> '' AND LOWER(b.institutionName) = LOWER(:institutionName))")
		List<Object[]> searchBatchByBatchNameAndInstitution(
		    @Param("batchtitle") String batchtitle,
		    @Param("institutionName") String institutionName);

		
}

