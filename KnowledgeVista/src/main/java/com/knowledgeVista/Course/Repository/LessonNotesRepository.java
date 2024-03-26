package com.knowledgeVista.Course.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.knowledgeVista.Course.Lessons.CourseLesson;
import com.knowledgeVista.Course.Lessons.LessonNotes;

@Repository
public interface LessonNotesRepository extends JpaRepository<LessonNotes, Long>{
    List<LessonNotes> findByCourseLessonLessonId(Long lessonId);

	
}
