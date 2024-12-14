package com.knowledgeVista.Course.Test.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.knowledgeVista.Course.Test.CourseTest;
import com.knowledgeVista.Course.Test.MuserTestActivity;
import com.knowledgeVista.User.Muser;

import jakarta.transaction.Transactional;

public interface MusertestactivityRepo extends JpaRepository<MuserTestActivity, Long> {
	
	List<MuserTestActivity> findByuser(Muser user); 
	  long countByUser(Muser user);
	  @Transactional
	    void deleteByUser(Muser user);
	  
	  @Modifying
	  @Transactional
	  @Query("DELETE FROM MuserTestActivity act WHERE act.test = :test")
	  void deleteByCourseTest(CourseTest test);
	  
	 }
