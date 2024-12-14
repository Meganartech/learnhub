package com.knowledgeVista.Payments.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Payments.Course_PartPayment_Structure;
@Repository
public interface partpayrepo extends JpaRepository<Course_PartPayment_Structure, Long> {
	@Query("SELECT u FROM Course_PartPayment_Structure u WHERE u.course = ?1")
	Optional<Course_PartPayment_Structure> findBycourse(CourseDetail course);
}
