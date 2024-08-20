package com.knowledgeVista.Meeting.zoomclass;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface InviteeRepo extends JpaRepository<ZoomMeetingInvitee, Long> {

	  @Query("SELECT u FROM ZoomMeetingInvitee u WHERE u.email = ?1")
   List<ZoomMeetingInvitee>findByEmail(String email);

}
