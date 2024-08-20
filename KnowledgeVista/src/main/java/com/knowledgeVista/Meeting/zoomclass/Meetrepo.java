package com.knowledgeVista.Meeting.zoomclass;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Meetrepo extends JpaRepository<Meeting, Long>{

	
	@Query("SELECT NEW com.knowledgeVista.Meeting.zoomclass.calenderDto(u.MeetingId,u.topic, u.JoinUrl, u.startTime,u.duration) FROM Meeting u WHERE u.settings = ?1")
	calenderDto findByZoomsettings(ZoomSettings settings);
	
	@Query("SELECT u From Meeting u WHERE u.MeetingId=?1")
   Optional<Meeting> FindByMeetingId(Long MeetingId);
	
	
	
}

