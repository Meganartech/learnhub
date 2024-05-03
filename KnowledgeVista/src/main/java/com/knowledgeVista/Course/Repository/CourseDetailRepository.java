package com.knowledgeVista.Course.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.CourseDetail;

@Repository
public interface CourseDetailRepository  extends JpaRepository<CourseDetail,Long>{
	 @Query("SELECT COALESCE(SUM(cd.Noofseats - SIZE(cd.users)), 0) FROM CourseDetail cd")
	    Long countTotalAvailableSeats();
	 
	 @Query("SELECT c FROM CourseDetail c ORDER BY SIZE(c.users) DESC")
	 List<CourseDetail> findAllOrderByUserCountDesc();
}
