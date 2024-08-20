package com.knowledgeVista.Meeting.zoomclass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoomsettingRepo extends JpaRepository<ZoomSettings, Long> {

}
