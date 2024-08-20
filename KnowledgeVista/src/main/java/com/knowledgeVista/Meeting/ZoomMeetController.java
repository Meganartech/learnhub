package com.knowledgeVista.Meeting;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowledgeVista.Meeting.zoomclass.MeetingRequest;



@RestController
@CrossOrigin
@RequestMapping("/api/zoom")
public class ZoomMeetController {
  
private final ZoomMeetingService zoomMeetingService;

public ZoomMeetController(ZoomMeetingService zoomMeetingService) {
    this.zoomMeetingService = zoomMeetingService;
}


@PostMapping("/create-meeting")
public ResponseEntity<?> createMeeting(@RequestBody MeetingRequest meetingReq,@RequestHeader("Authorization") String token) {
    
        return zoomMeetingService.createMeetReq(meetingReq,token);
   
}

@GetMapping("/getMyMeetings")
public ResponseEntity<?>GetMyMeetings(@RequestHeader("Authorization") String token){
	return zoomMeetingService.getMetting(token);
}

@GetMapping("/get/meet/{meetingId}")
public ResponseEntity<?>GetmeetbyMeetingId(@PathVariable Long meetingId ,@RequestHeader("Authorization") String token){
	return zoomMeetingService.getMeetDetailsForEdit(token, meetingId);
}

@PatchMapping("/meet/{meetingId}")
public ResponseEntity<?>EditMeetingByMeetingId(@RequestBody MeetingRequest meetingReq,@PathVariable Long meetingId,@RequestHeader("Authorization") String token){
	return zoomMeetingService.EditZoomMeetReq(meetingReq, meetingId, token);
}
@DeleteMapping("/delete/{meetingId}")
public ResponseEntity<?>DeleteMeeting(@PathVariable Long meetingId ,@RequestHeader("Authorization") String token)
{
	return zoomMeetingService.DeleteMeet(meetingId, token);
}
}
