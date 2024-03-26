package com.knowledgeVista.Course.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.CourseDetail;
import com.knowledgeVista.Course.Lessons.CourseLesson;

@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLesson,Long> {
	  List<CourseLesson> findByCourseDetail(CourseDetail  courseDetail);

}
