package com.knowledgeVista.Payments.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Payments.Orderuser;

@Repository
public interface OrderuserRepo extends JpaRepository<Orderuser,Long>{
	Optional<Orderuser> findByOrderId(String orderId);
	
	@Query("SELECT u FROM Orderuser u WHERE u.userId=:userId")
	List<Orderuser> findAllByUserId(@Param("userId") Long userId);
	
	@Query("SELECT u FROM Orderuser u WHERE u.institutionName=:institutionName")
	List<Orderuser> findAllByinstitutionName(@Param("institutionName") String institutionName);
	
	@Query("SELECT u FROM Orderuser u WHERE u.courseId=:courseId AND u.institutionName=:institutionName")
	List<Orderuser> findAllBycourseIdandinstitutionName(@Param("courseId") Long courseId ,@Param("institutionName") String institutionName);
	
	@Query("SELECT COUNT(o) FROM Orderuser o WHERE o.userId = ?1 AND o.courseId = ?2 AND status=?3")
	int findCountByUserIDAndCourseID(Long userId, Long courseId,String status);
	
	@Query("SELECT o FROM Orderuser o WHERE o.userId = ?1 AND o.courseId = ?2 AND status=?3")
	List<Orderuser> findAllByUserIDAndCourseID(Long userId, Long courseId,String status);
   
	@Query("SELECT SUM(o.amountReceived) FROM Orderuser o WHERE o.institutionName = :institutionName AND o.amountReceived > 0")
    Long getTotalAmountReceivedByInstitution(@Param("institutionName") String institutionName);
	
	@Query("""
		    SELECT o 
		    FROM Orderuser o 
		    WHERE o.userId = :userId 
		      AND o.courseId = :courseId 
		      AND o.amountReceived > 0
		""")
		List<Orderuser> findByUserIdAndCourseIdAndAmountReceivedGreaterThanzero(@Param("userId") Long userId, 
		                                                                    @Param("courseId") Long courseId);
		                                                                   
	
}
