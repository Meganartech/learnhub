package com.knowledgeVista.Meeting.zoomclass.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Meeting.zoomclass.Recurrenceclass;

@Repository
public interface Recurrenceclassrepo extends JpaRepository<Recurrenceclass, Long>{

}
