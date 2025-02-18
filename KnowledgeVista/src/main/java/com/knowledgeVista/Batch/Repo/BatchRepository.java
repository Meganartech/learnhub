package com.knowledgeVista.Batch.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Batch.Batch;
import com.knowledgeVista.Batch.BatchDto;
import com.knowledgeVista.Batch.CourseDto;
import com.knowledgeVista.Batch.SearchDto;
import com.knowledgeVista.Batch.TrainerDto;
import com.knowledgeVista.Course.CourseDetailDto;
import com.knowledgeVista.Course.CourseDetailDto.courseIdNameImg;
import com.knowledgeVista.User.Muser;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
	
	@Query("SELECT b FROM Batch b WHERE b.id = :id AND b.institutionName = :institutionName")
	Optional<Batch> findBatchByIdAndInstitutionName(@Param("id") Long id, @Param("institutionName") String institutionName);
	
	@Query("SELECT b FROM Batch b WHERE b.batchId = :id AND b.institutionName = :institutionName")
	Optional<Batch> findBatchByIdAndInstitutionName(@Param("id") String id, @Param("institutionName") String institutionName);
	
	@Query("SELECT new com.knowledgeVista.Course.CourseDetailDto$courseIdNameImg(c.courseId, c.courseName, c.courseImage) " +
		       "FROM Batch b " +
		       "LEFT JOIN b.courses c " +
		       "WHERE b.batchId = :id AND b.institutionName = :institutionName")
		List<courseIdNameImg> findCoursesOfBatchByBatchId(@Param("id") String id, @Param("institutionName") String institutionName);
		@Query("SELECT b FROM Batch b JOIN b.courses c WHERE c.courseId = :courseId AND b.institutionName = :institutionName")
		List<Batch> findByCoursesCourseIdAndInstitutionName(@Param("courseId") Long courseId, @Param("institutionName") String institutionName);
		
	@Query("SELECT new com.knowledgeVista.Batch.BatchDto( " +
		       "b.id, b.batchId, b.batchTitle, b.startDate, b.endDate, b.institutionName, b.noOfSeats, b.amount, " +
		       "COALESCE(CAST(STRING_AGG(c.courseName, ',') AS string), ''), " +
		       "COALESCE(CAST(STRING_AGG(t.username, ',') AS string), ''), " +
		       "b.BatchImage) " +
		       "FROM Batch b " +
		       "LEFT JOIN b.courses c " +
		       "LEFT JOIN b.trainers t " +
		       "WHERE b.id = :id AND b.institutionName = :institutionName " +
		       "GROUP BY b.id, b.batchId, b.batchTitle, b.startDate, b.endDate, b.institutionName, b.noOfSeats, b.amount")
		Optional<BatchDto> findBatchDtoByIdAndInstitutionName(@Param("id") Long id, @Param("institutionName") String institutionName);


	@Query("SELECT new com.knowledgeVista.Batch.CourseDto(c.courseId, c.courseName) " +
		       "FROM Batch b " +
		       "JOIN b.courses c " +
		       "WHERE b.id = :id AND b.institutionName = :institutionName")
		List<CourseDto> findCoursesByBatchIdAndInstitutionName(@Param("id") Long id, @Param("institutionName") String institutionName);
	@Query("SELECT new com.knowledgeVista.Batch.TrainerDto(t.userId, t.username) " +
		       "FROM Batch b " +
		       "JOIN b.trainers t " +
		       "WHERE b.id = :id AND b.institutionName = :institutionName")
		List<TrainerDto> findTrainersByBatchIdAndInstitutionName(@Param("id") Long id, @Param("institutionName") String institutionName);

	@Query("SELECT b FROM Batch b  WHERE b.institutionName = :institutionName")
		List<Batch> findAllBatchDtosByInstitution(@Param("institutionName") String institutionName);
	

	 @Query("SELECT new com.knowledgeVista.Batch.SearchDto(u.id, u.batchTitle, 'BATCH') " +
	  	       "FROM Batch u " +
	  	       "WHERE u.institutionName = :institutionName " +
	  	       "AND LOWER(u.batchTitle) LIKE LOWER(CONCAT('%', :Query, '%'))")
	  	List<SearchDto> findbatchAsSearchDto(@Param("Query") String Query, @Param("institutionName") String institutionName);

	@Query("SELECT b.id, b.batchId, b.batchTitle " +
		       "FROM Batch b " +
		       "WHERE (:batchtitle IS NOT NULL AND :batchtitle <> '' AND LOWER(b.batchTitle) LIKE LOWER(CONCAT('%', :batchtitle, '%'))) " +
		       "AND (:institutionName IS NOT NULL AND :institutionName <> '' AND LOWER(b.institutionName) = LOWER(:institutionName))")
		List<Object[]> searchBatchByBatchNameAndInstitution(
		    @Param("batchtitle") String batchtitle,
		    @Param("institutionName") String institutionName);
		
		
		   @Query("SELECT c.id FROM Batch b " +
		           "JOIN b.courses c " +
		           "WHERE b.id = :id")
		    List<Long> findCourseIdsByBatchId(@Param("id") Long id);
		   
		   @Query("SELECT t.email FROM Batch b " +
		           "JOIN b.trainers t " +
		           "WHERE b.id = :id")
		    List<String> findtrainersByBatchId(@Param("id") Long id);
		   @Query("SELECT u.email FROM Batch b " +
		           "JOIN b.users u " +
		           "WHERE b.id = :id")
		    List<String> findusersByBatchId(@Param("id") Long id);
		   
		   @Query("SELECT u FROM Batch b " +
		           "JOIN b.users u " +
		           "WHERE b.id = :id")
		    List<Muser> findAllusersByBatchId(@Param("id") Long id);

		
}

