package com.knowledgeVista.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.User.MuserDetails;
@Repository
public interface MuserDetailRepository extends JpaRepository<MuserDetails,Long>{

}
