package com.knowledgeVista.Course.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knowledgeVista.Course.videoLessons;

public interface videoLessonRepo extends JpaRepository<videoLessons, Long> {

	 @Query("SELECT cd FROM videoLessons cd WHERE cd.id = :videoLessons AND cd.institutionName = :institutionName")
	 Optional<videoLessons> findBylessonIdAndInstitutionName(@Param("videoLessons") Long courseId, @Param("institutionName") String institutionName);
   
	 @Query("SELECT vd FROM videoLessons vd WHERE vd.institutionName = :institutionName ")
	 List<videoLessons>findAllByInstitutionName(String institutionName);
	
//	 @Query("SELECT documentPath FROM videoLessons vd WHERE vd.lessonId = :lessonId ")
//	 String findDocumetByid(Long lessonId);
}
