package com.knowledgeVista.Course.moduleTest.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Course.moduleTest.ModuleTestActivity;
@Repository
public interface ModuleTestActivityRepo extends JpaRepository<ModuleTestActivity, Long> {

}
