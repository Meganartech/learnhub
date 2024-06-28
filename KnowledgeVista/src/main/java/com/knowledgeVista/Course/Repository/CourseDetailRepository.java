package com.knowledgeVista.Course.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.CourseDetail;

@Repository
public interface CourseDetailRepository  extends JpaRepository<CourseDetail,Long>{
	
	@Query("SELECT cd FROM CourseDetail cd WHERE cd.institutionName = :institutionName")
	List<CourseDetail> findAllByInstitutionName(@Param("institutionName") String institutionName);

	
	@Query("SELECT COUNT(cd) FROM CourseDetail cd WHERE cd.institutionName = :institutionName")
	Long countCourseByInstitutionName(@Param("institutionName") String institutionName);

	 @Query("SELECT COALESCE(SUM(cd.Noofseats - SIZE(cd.users)), 0) FROM CourseDetail cd")
	    Long countTotalAvailableSeats();
	 
	 @Query("SELECT cd FROM CourseDetail cd WHERE cd.id = :courseId AND cd.institutionName = :institutionName")
	 Optional<CourseDetail> findByCourseIdAndInstitutionName(@Param("courseId") Long courseId, @Param("institutionName") String institutionName);

	 @Query("SELECT COALESCE(SUM(cd.Noofseats - SIZE(cd.users)), 0) FROM CourseDetail cd WHERE cd.institutionName = :institutionName")
	 Long countTotalAvailableSeats(@Param("institutionName") String institutionName);

	 @Query("SELECT c FROM CourseDetail c ORDER BY SIZE(c.users) DESC")
	 List<CourseDetail> findAllOrderByUserCountDesc();
	 @Query("SELECT c FROM CourseDetail c WHERE c.institutionName = :institutionName ORDER BY SIZE(c.users) DESC")
	 List<CourseDetail> findAllByInstitutionNameOrderByUserCountDesc(@Param("institutionName") String institutionName);

}
