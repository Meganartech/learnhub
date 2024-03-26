package com.knowledgeVista.Course.Test.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.knowledgeVista.Course.Test.MuserTestActivity;
import com.knowledgeVista.User.Muser;

public interface MusertestactivityRepo extends JpaRepository<MuserTestActivity, Long> {
	
	List<MuserTestActivity> findByuser(Muser user); 

}
