package com.knowledgeVista.Course.Test.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.Test.CourseTest;
import com.knowledgeVista.Course.Test.Question;

import jakarta.transaction.Transactional;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
	  @Transactional
	    void deleteByTest(CourseTest test);
}
