package com.knowledgeVista.Meeting.zoomclass.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowledgeVista.Meeting.zoomclass.ZoomSettings;

@Repository
public interface ZoomsettingRepo extends JpaRepository<ZoomSettings, Long> {

}
