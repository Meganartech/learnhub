package com.knowledgeVista.Course.Test.Repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knowledgeVista.Course.Test.CourseTest;
import com.knowledgeVista.Course.Test.MuserTestActivity;
import com.knowledgeVista.Course.Test.TestHistoryDto;
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
	  
	
	  @Query("""
			    SELECT COALESCE(SUM(mta.percentage), 0.0) 
			    FROM MuserTestActivity mta
			    WHERE mta.nthAttempt = (
			        SELECT MAX(mta2.nthAttempt) 
			        FROM MuserTestActivity mta2 
			        WHERE mta2.user.userId = mta.user.userId 
			          AND mta2.test.testId = mta.test.testId
			    )
			    AND mta.user.email = :email
			    AND mta.test.testId IN :testIds
			""")
			Double getTotalPercentageForUserandTestIDs(@Param("email") String email, @Param("testIds") List<Long> testIds);
	  
	 
	  
	  @Query("""
			    SELECT new com.knowledgeVista.Course.Test.TestHistoryDto(
			        t.courseDetail.courseName, 
			        t.courseDetail.courseId,
			        t.testName, 
			        t.testId, 
			        COALESCE(mta.testDate, NULL) AS testDate, 
			        COALESCE(mta.nthAttempt, 0) AS nthAttempt, 
			        COALESCE(mta.percentage, 0) AS percentage,
			        CASE 
			            WHEN mta.test.testId IS NULL THEN 'N/A' 
			            WHEN COALESCE(mta.percentage, 0) >= t.passPercentage THEN 'PASS' 
			            ELSE 'FAIL' 
			        END AS status
			    )
			    FROM CourseTest t
			    LEFT JOIN MuserTestActivity mta 
			        ON t.testId = mta.test.testId 
			        AND mta.user.email = :email
			    WHERE t.testId IN :testIds
			""")
			Page<TestHistoryDto> getTestHistoryByEmailAndTestIds(
			    @Param("email") String email, 
			    @Param("testIds") List<Long> testIds,
			    Pageable pageable
			);

	 }
