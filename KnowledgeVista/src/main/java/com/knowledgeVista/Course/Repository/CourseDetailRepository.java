package com.knowledgeVista.Course.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.CourseDetailDto;

@Repository
public interface CourseDetailRepository  extends JpaRepository<CourseDetail,Long>{
	//for alloted list
	@Query("SELECT cd FROM CourseDetail cd WHERE cd.institutionName = :institutionName")
	List<CourseDetail> findAllByInstitutionName(@Param("institutionName") String institutionName);
	//for alloted list
	@Query("SELECT new com.knowledgeVista.Course.CourseDetailDto(cd.courseId, cd.courseName, cd.courseUrl, cd.courseDescription, cd.courseCategory, cd.amount, cd.courseImage, cd.paytype, cd.Duration, cd.institutionName, cd.Noofseats) FROM CourseDetail cd WHERE cd.institutionName = :institutionName")
	List<CourseDetailDto> findAllByInstitutionNameDto(@Param("institutionName") String institutionName);
	@Query("SELECT new com.knowledgeVista.Course.CourseDetailDto(cd.courseId, cd.courseName, cd.courseUrl, cd.courseDescription, cd.courseCategory, cd.amount, cd.courseImage, cd.paytype, cd.Duration, cd.institutionName, cd.Noofseats) FROM CourseDetail cd ")
	List<CourseDetailDto>findallcoursevps();
	
	@Query("SELECT COUNT(cd) FROM CourseDetail cd WHERE cd.institutionName = :institutionName")
	Long countCourseByInstitutionName(@Param("institutionName") String institutionName);


    @Query("SELECT COUNT(cd) FROM CourseDetail cd WHERE cd.amount > 0 AND cd.institutionName = :institution")
    Long countPaidCoursesByInstitution(@Param("institution") String institution);
	 
	 @Query("SELECT cd FROM CourseDetail cd WHERE cd.id = :courseId AND cd.institutionName = :institutionName")
	 Optional<CourseDetail> findByCourseIdAndInstitutionName(@Param("courseId") Long courseId, @Param("institutionName") String institutionName);

	 @Query("SELECT COALESCE(SUM(cd.Noofseats - SIZE(cd.users)), 0) FROM CourseDetail cd WHERE cd.institutionName = :institutionName")
	 Long countTotalAvailableSeats(@Param("institutionName") String institutionName);

	 @Query("SELECT c FROM CourseDetail c ORDER BY SIZE(c.users) DESC")
	 List<CourseDetail> findAllOrderByUserCountDesc();
	 @Query("SELECT c FROM CourseDetail c WHERE c.institutionName = :institutionName ORDER BY SIZE(c.users) DESC")
	 List<CourseDetail> findAllByInstitutionNameOrderByUserCountDesc(@Param("institutionName") String institutionName);

}
