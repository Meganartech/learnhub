package com.knowledgeVista.Course.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.CourseDetail;

@Repository
public interface CourseDetailRepository  extends JpaRepository<CourseDetail,Long>{

}
