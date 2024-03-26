package com.knowledgeVista.Course.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.Lessons.LessonControls;
@Repository
public interface LessonControlRepository extends JpaRepository<LessonControls, Long>{

}
