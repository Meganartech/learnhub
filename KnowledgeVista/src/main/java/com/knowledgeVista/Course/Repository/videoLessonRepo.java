package com.knowledgeVista.Course.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.LessonQuizDTO;
import com.knowledgeVista.Course.videoLessons;

public interface videoLessonRepo extends JpaRepository<videoLessons, Long> {

	 @Query("SELECT cd FROM videoLessons cd WHERE cd.id = :videoLessons AND cd.institutionName = :institutionName")
	 Optional<videoLessons> findBylessonIdAndInstitutionName(@Param("videoLessons") Long courseId, @Param("institutionName") String institutionName);
   
	 @Query("SELECT vd FROM videoLessons vd WHERE vd.institutionName = :institutionName ")
	 List<videoLessons>findAllByInstitutionName(String institutionName);
	 @Query("SELECT cd.courseDetail FROM videoLessons cd WHERE cd.lessonId = :lessonId ")
	 Optional<CourseDetail> FindbyCourseByLessonId(Long lessonId);
	
//	 @Query("SELECT documentPath FROM videoLessons vd WHERE vd.lessonId = :lessonId ")
//	 String findDocumetByid(Long lessonId);
	 
	 @Query("SELECT SUM(v.size) FROM videoLessons v WHERE v.institutionName = :institutionName")
	 Long findTotalSizeByInstitution(@Param("institutionName") String institutionName);
	 
	 
//	 @Query("SELECT new com.knowledgeVista.Migration.VideoLessonsMigrationDto(cd.lessonId, cd.institutionName, cd.lessontitle, cd.lessonDescription, cd.thumbnail, cd.size, cd.videofilename, cd.fileUrl) FROM videoLessons cd WHERE cd.institutionName = :institutionName")
//	 List<VideoLessonsMigrationDto> findAllByVideoLessonsMigrationDto(@Param("institutionName") String institutionName);
//		
	 @Query("""
			    SELECT new com.knowledgeVista.Course.LessonQuizDTO(
			        v.lessonId, v.Lessontitle, q.quizzId, q.quizzName) 
			    FROM videoLessons v
			    LEFT JOIN v.quizz q
			    WHERE v.courseDetail.courseId = :courseId
			""")
			List<LessonQuizDTO> findLessonsWithQuizByCourseId(@Param("courseId") Long courseId);

}
