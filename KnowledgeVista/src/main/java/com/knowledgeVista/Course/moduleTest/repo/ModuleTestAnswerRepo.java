package com.knowledgeVista.Course.moduleTest.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.moduleTest.ModuleTestAnswer;

@Repository
public interface ModuleTestAnswerRepo extends JpaRepository<ModuleTestAnswer, Long> {

}
