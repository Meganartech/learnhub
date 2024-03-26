package com.knowledgeVista.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Organisation.OrgDetails;
@Repository
public interface OrgDetailRepository extends JpaRepository<OrgDetails,Long> {

}
