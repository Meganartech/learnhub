package com.knowledgeVista.Course.Test.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.Test.MuserTestAnswer;
@Repository
public interface MtestAnswerRepo extends JpaRepository<MuserTestAnswer, Long>{

}
