package com.knowledgeVista.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.User.SkillDetails;
@Repository
public interface SkillDetailRepository  extends JpaRepository<SkillDetails,Long>{

}
