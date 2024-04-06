package com.knowledgeVista.Course.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knowledgeVista.Course.videoLessons;

public interface videoLessonRepo extends JpaRepository<videoLessons, Long> {

}
