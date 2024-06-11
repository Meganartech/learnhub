package com.knowledgeVista.Payments;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrderuserRepo extends JpaRepository<Orderuser,Long>{
	Optional<Orderuser> findByOrderId(String orderId);
	
	@Query("SELECT u FROM Orderuser u WHERE u.userId=:userId")
	List<Orderuser> findAllByUserId(@Param("userId") Long userId);
	
	@Query("SELECT u FROM Orderuser u WHERE u.courseId=:courseId")
	List<Orderuser> findAllBycourseId(@Param("courseId") Long courseId);
	
	@Query("SELECT COUNT(o) FROM Orderuser o WHERE o.userId = ?1 AND o.courseId = ?2 AND status=?3")
	int findCountByUserIDAndCourseID(Long userId, Long courseId,String status);
	
	@Query("SELECT o FROM Orderuser o WHERE o.userId = ?1 AND o.courseId = ?2 AND status=?3")
	List<Orderuser> findAllByUserIDAndCourseID(Long userId, Long courseId,String status);
	
	
}
