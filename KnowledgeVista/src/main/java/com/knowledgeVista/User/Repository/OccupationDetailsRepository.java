package com.knowledgeVista.User.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.User.OccupationDetails;
@Repository
public interface OccupationDetailsRepository extends JpaRepository< OccupationDetails ,Long>{

}
