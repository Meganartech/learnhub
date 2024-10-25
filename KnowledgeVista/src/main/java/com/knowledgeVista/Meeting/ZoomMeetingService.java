package com.knowledgeVista.Meeting;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgeVista.Meeting.zoomclass.InviteeRepo;
import com.knowledgeVista.Meeting.zoomclass.Meeting;
import com.knowledgeVista.Meeting.zoomclass.MeetingRequest;
import com.knowledgeVista.Meeting.zoomclass.MeetingRequest.MeetingInvitee;
import com.knowledgeVista.Meeting.zoomclass.MeetingRequest.Settings;
import com.knowledgeVista.Meeting.zoomclass.Meetrepo;
import com.knowledgeVista.Meeting.zoomclass.ZoomMeetingInvitee;
import com.knowledgeVista.Meeting.zoomclass.ZoomSettings;
import com.knowledgeVista.Meeting.zoomclass.calenderDto;
import com.knowledgeVista.Notification.Repositories.NotificationDetailsRepo;
import com.knowledgeVista.Notification.Repositories.NotificationUserRepo;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.Repository.MuserRepositories;
import com.knowledgeVista.User.SecurityConfiguration.JwtUtil;
import com.knowledgeVista.zoomJar.ZoomMethods;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


@Service
public class ZoomMeetingService {

	    private final ObjectMapper objectMapper;
	    private final ZoomTokenService zoomTokenService;
	   
		 @Autowired
		 private JwtUtil jwtUtil;
		 @Autowired
			private MuserRepositories muserRepository;

        @Autowired
        private SaveMeetDataService saveservice;
        @Autowired
        private InviteeRepo inviteerepo;
        @Autowired
        private Meetrepo meetrepo;
        @Autowired
        private NotificationDetailsRepo notidetail;
        @Autowired
        private NotificationUserRepo notiuser;
       
        
        @Autowired
		 private ZoomMethods zoomMethod;
        
	    public ZoomMeetingService(ZoomTokenService zoomTokenService) {
	        
	        this.objectMapper = new ObjectMapper();
	        this.zoomTokenService = zoomTokenService;
	        
	    }

       public ResponseEntity<?>createMeetReq(MeetingRequest meetingReq,String token){
    	   try {
		    	
  	    	 if (!jwtUtil.validateToken(token)) {
  	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
  	    	 }
  	         String email=jwtUtil.getUsernameFromToken(token); 
  	         Optional<Muser> opuser=muserRepository.findByEmail(email);
  	         if(opuser.isPresent()) {
  	        	 Muser user=opuser.get();
  	        	 String role=user.getRole().getRoleName();
  	        	 String InstitutionName=user.getInstitutionName();
  	        	 if("ADMIN".equals(role)||"TRAINER".equals(role)) {
  	        		  String accessToken = zoomTokenService.getAccessToken(InstitutionName);
  	    	        System.out.println("accessToken= "+accessToken);
  	    	        if (accessToken == null || accessToken.isEmpty()) {
  	    	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Token Generation Failed");
  	    	        }
  	    	      String json = this.convertToZoomApiJson(meetingReq);
  	        		String res= zoomMethod.createMeeting(json, accessToken);
  	        		 saveservice.saveMeetData(res, email);
  	        		return ResponseEntity.ok(res);
  	        	 }else {
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
	         }else {
	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not Found");
	         }
	         
		  } catch (Exception e) {
			  e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		        		
		                .body("An error occurred while creating the meeting: " + e.getMessage() );
		    }
       }
       

       
       public ResponseEntity<?>EditZoomMeetReq(MeetingRequest meetingReq,Long MeetingId,String token){
    	   try {
		    	
    	    	 if (!jwtUtil.validateToken(token)) {
    	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    	    	 }
    	         String email=jwtUtil.getUsernameFromToken(token); 
    	         Optional<Muser> opuser=muserRepository.findByEmail(email);
    	         if(opuser.isPresent()) {
    	        	 Muser user=opuser.get();
    	        	 String role=user.getRole().getRoleName();
    	        	 String InstitutionName=user.getInstitutionName();
    	        	 if("ADMIN".equals(role) ||"TRAINER".equals(role)) {
    	        		  String accessToken = zoomTokenService.getAccessToken(InstitutionName);
    	    	        System.out.println("accessTokennnnnnnnnnnnnnn"+accessToken);
    	    	        if (accessToken == null || accessToken.isEmpty()) {
    	    	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Token Generation Failed");
    	    	        }
    	    	        String json = convertToZoomApiJsonForEdit(meetingReq);
    	    	        
    	    	        String res = zoomMethod.EditMeet(MeetingId, json, accessToken);
    	    	    	
    	    	        if (res != null && !res.contains("error")) {
    	    	            // Save the response data
    	    	            saveservice.PatchsaveData(email, res, MeetingId);
    	    	            return ResponseEntity.ok(res);
    	    	        } else {
    	    	        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to Update");
    	    	          }
    	        	
    	        		
    	        		
    	        	 }else {
  	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  	        	 }
  	         }else {
  	        	 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not Found");
  	         }
  	         
  		  } catch (Exception e) {
  			e.printStackTrace();
  		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
  		                .body("An error occurred while updating the certificate: " + e.getMessage() );
  		    }
		}


       
       
      
	  
       private String convertToZoomApiJsonForEdit(MeetingRequest meetingRequest) throws JsonProcessingException {
		    Map<String, Object> zoomApiMap = new HashMap<>();

		    // Basic fields
		    zoomApiMap.put("topic", meetingRequest.getTopic());
		    zoomApiMap.put("type", meetingRequest.getType()); // Replace with appropriate value
//		    ZonedDateTime utcTime = meetingRequest.getStartTime().atZone(ZoneOffset.UTC);
//
//		    // Define formatter for UTC without milliseconds
//		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
//
//		    // Format the start time as a String
//		    String startTimeString = utcTime.format(formatter);
//
//		    System.out.println("Start time: " + startTimeString);
		    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		    String startTimeString =  meetingRequest.getStartTime().format(formatter);
		   System.out.println("Starttime"+startTimeString);
		    zoomApiMap.put("start_time",startTimeString);

		    // Add to zoomApiMap
		    zoomApiMap.put("start_time", startTimeString);
		   	zoomApiMap.put("timezone", meetingRequest.getTimezone());
	  //  zoomApiMap.put("host_email","akshaya2112001@gmail.com");
		  // 	zoomApiMap.put("pre_schedule", true);
		    zoomApiMap.put("agenda", meetingRequest.getAgenda()); // Optional based on Zoom API
		    zoomApiMap.put("duration", meetingRequest.getDuration()); // Optional based on Zoom API
//		    zoomApiMap.put("default_password", meetingRequest.isDefaultPassword());
//		    zoomApiMap.put("schedule_for", "akshaya2112001@gmail.com");

		    // Recurrence (if present)
//		    if (meetingRequest.getRecurrence() != null) {
//		        zoomApiMap.put("recurrence", convertRecurrenceToJson(meetingRequest.getRecurrence()));
//		    }

		    // Settings (if present)
		    if (meetingRequest.getSettings() != null) {
		        zoomApiMap.put("settings", convertSettingsToJson(meetingRequest.getSettings()));
		    }

		    // Additional fields based on Zoom API requirements (replace placeholders)
//		    zoomApiMap.put("password", meetingRequest.getPassword()); // Optional based on Zoom API
//		    zoomApiMap.put("template_id", meetingRequest.getTemplateId()); // Optional based on Zoom API
//		   
		    return objectMapper.writeValueAsString(zoomApiMap);
		}

	   
	   
	   private String convertToZoomApiJson(MeetingRequest meetingRequest) throws JsonProcessingException {
		    Map<String, Object> zoomApiMap = new HashMap<>();

		    // Basic fields
		    zoomApiMap.put("topic", meetingRequest.getTopic());
		    zoomApiMap.put("type", meetingRequest.getType()); // Replace with appropriate value
		    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		    String startTimeString =  meetingRequest.getStartTime().format(formatter);
		   System.out.println("Starttime"+startTimeString);
		    zoomApiMap.put("start_time",startTimeString);
		    zoomApiMap.put("timezone", meetingRequest.getTimezone());
	  //  zoomApiMap.put("host_email","akshaya2112001@gmail.com");
		  // 	zoomApiMap.put("pre_schedule", true);
		    zoomApiMap.put("agenda", meetingRequest.getAgenda()); // Optional based on Zoom API
		    zoomApiMap.put("duration", meetingRequest.getDuration()); // Optional based on Zoom API
//		    zoomApiMap.put("default_password", meetingRequest.isDefaultPassword());
//		    zoomApiMap.put("schedule_for", "akshaya2112001@gmail.com");

		    // Recurrence (if present)
//		    if (meetingRequest.getRecurrence() != null) {
//		        zoomApiMap.put("recurrence", convertRecurrenceToJson(meetingRequest.getRecurrence()));
//		    }

		    // Settings (if present)
		    if (meetingRequest.getSettings() != null) {
		        zoomApiMap.put("settings", convertSettingsToJson(meetingRequest.getSettings()));
		    }

		    // Additional fields based on Zoom API requirements (replace placeholders)
//		    zoomApiMap.put("password", meetingRequest.getPassword()); // Optional based on Zoom API
//		    zoomApiMap.put("template_id", meetingRequest.getTemplateId()); // Optional based on Zoom API
//		   
		    return objectMapper.writeValueAsString(zoomApiMap);
		}

//		private Map<String, Object> convertRecurrenceToJson(Recurrence recurrence) {
//		    Map<String, Object> recurrenceMap = new HashMap<>();
//		    recurrenceMap.put("type", recurrence.getType());
//		    recurrenceMap.put("repeat_interval", recurrence.getRepeatInterval());
//		    recurrenceMap.put("end_date_time", recurrence.getEndDateTime());
//		    recurrenceMap.put("end_times", recurrence.getEndTimes());
//		    recurrenceMap.put("monthly_day", recurrence.getMonthlyDay());
//		    recurrenceMap.put("monthly_week", recurrence.getMonthlyWeek());
//		    recurrenceMap.put("monthly_week_day", recurrence.getMonthlyWeekDay());
//		    recurrenceMap.put("weekly_days", recurrence.getWeeklyDays());
//		 
//		    return recurrenceMap;
//		}

		private Map<String, Object> convertSettingsToJson(Settings settings) {
		    Map<String, Object> settingsMap = new HashMap<>();

		    settingsMap.put("host_video", settings.isHostVideo());
		    settingsMap.put("participant_video", settings.isParticipantVideo());
		    settingsMap.put("mute_upon_entry", settings.isMuteUponEntry());
	//	    settingsMap.put("waiting_room", settings.isWaitingRoom());
		    settingsMap.put("audio", settings.getAudio());
	//	    settingsMap.put("audio_conference_info", settings.getAudioConferenceInfo());
		    settingsMap.put("auto_recording", settings.getAutoRecording());
		 //   settingsMap.put("calendar_type", settings.getCalendarType());
	//	    settingsMap.put("close_registration", settings.isCloseRegistration());
		    //settingsMap.put("contact_email", settings.getContactEmail());
	//	    settingsMap.put("contact_name", settings.getContactName());
		    settingsMap.put("email_notification", settings.isEmailNotification());
//		    settingsMap.put("encryption_type", settings.getEncryptionType());
	//	    settingsMap.put("focus_mode", settings.isFocusMode());
//		    settingsMap.put("global_dial_in_countries", settings.getGlobalDialInCountries());
	//	    settingsMap.put("jbh_time", settings.getJbhTime());
		    settingsMap.put("join_before_host", settings.isJoinBeforeHost());
	//	    settingsMap.put("meeting_authentication", settings.isMeetingAuthentication());
		    settingsMap.put("mute_upon_entry", settings.isMuteUponEntry());
		    settingsMap.put("participant_video", settings.isParticipantVideo());
//		    settingsMap.put("private_meeting", settings.isPrivateMeeting());
		    settingsMap.put("registrants_confirmation_email",true);
		    settingsMap.put("registrants_email_notification", true);
//		    settingsMap.put("registration_type", settings.getRegistrationType());
//		    settingsMap.put("show_share_button", settings.isShowShareButton());
//		    settingsMap.put("use_pmi", settings.isUsePmi());
//		    settingsMap.put("waiting_room", settings.isWaitingRoom());
//		    settingsMap.put("watermark", settings.isWatermark());
//		    settingsMap.put("host_save_video_order", settings.isHostSaveVideoOrder());
//		    settingsMap.put("alternative_host_update_polls", settings.isAlternativeHostUpdatePolls());
//		    settingsMap.put("internal_meeting", settings.isInternalMeeting());
//		    settingsMap.put("participant_focused_meeting", settings.isParticipantFocusedMeeting());
		    settingsMap.put("push_change_to_calendar", settings.isPushChangeToCalendar());
//		    settingsMap.put("auto_start_meeting_summary", settings.isAutoStartMeetingSummary());
//		    settingsMap.put("auto_start_ai_companion_questions", settings.isAutoStartAiCompanionQuestions());
//		    settingsMap.put("device_testing", settings.isDeviceTesting());
		    //settingsMap.put("alternative_hosts", settings.getAlternativeHosts());
            //settingsMap.put("aalternative_hosts_email_notification", true);

//		    if (settings.getAdditionalDataCenterRegions() != null) {
//		       // settingsMap.put("additional_data_center_regions", settings.getAdditionalDataCenterRegions());
//		    }
//		    if (settings.getAuthenticationException() != null) {
//		       // settingsMap.put("authentication_exception", convertAuthenticationExceptionsToJson(settings.getAuthenticationException()));
//		    }
		    if (settings.getMeetingInvitees() != null) {
		        settingsMap.put("meeting_invitees", convertMeetingInviteesToJson(settings.getMeetingInvitees()));
		    }

		    return settingsMap;
		}
//		private List<Map<String, Object>> convertAuthenticationExceptionsToJson(List<AuthenticationException> authenticationExceptions) {
//		    List<Map<String, Object>> exceptionsList = new ArrayList<>();
//		    for (AuthenticationException exception : authenticationExceptions) {
//		        Map<String, Object> exceptionMap = new HashMap<>();
//		        exceptionMap.put("email", exception.getEmail());
//		        exceptionMap.put("name", exception.getName());
//		        exceptionsList.add(exceptionMap);
//		    }
//		    return exceptionsList;
//		}

		private List<Map<String, Object>> convertMeetingInviteesToJson(List<MeetingInvitee> meetingInvitees) {
		    List<Map<String, Object>> inviteesList = new ArrayList<>();
		    for (MeetingInvitee invitee : meetingInvitees) {
		        Map<String, Object> inviteeMap = new HashMap<>();
		        inviteeMap.put("email", invitee.getEmail());
		        inviteesList.add(inviteeMap);
		    }
		    return inviteesList;
		}
	   
		
		public ResponseEntity<?>getMetting(String token){
			try {
				 if (!jwtUtil.validateToken(token)) {
	  	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	  	    	 }
	  	         String email=jwtUtil.getUsernameFromToken(token); 
	  	         Optional<Muser> opuser=muserRepository.findByEmail(email);
	  	         if(opuser.isPresent()) {
	  	        	 opuser.get();
	  	        	  List<calenderDto> meetingDetailsList = new ArrayList<>();
	  	        	List<ZoomMeetingInvitee>invitees= inviteerepo.findByEmail(email);
	  	        	for(ZoomMeetingInvitee invitee :invitees) {
	  	        		calenderDto item=new calenderDto();
	  	        		ZoomSettings settings=invitee.getZoomSettings();
	  	        		item=meetrepo.findByZoomsettings(settings);
	  	        		if(item!=null) {
	  	        		meetingDetailsList.add(item) ; 
	  	        		}
	  	        	}
	  	        	return ResponseEntity.ok(meetingDetailsList);
	  	       }else {
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
			}catch(Exception e) {
				
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
		
		
		
		
		@Transactional
		public ResponseEntity<?>DeleteMeet(Long MeetingId,String token){
			try {
				 if (!jwtUtil.validateToken(token)) {
					  System.out.println("token unauthorized");
	  	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	  	    	 }
	  	         String email=jwtUtil.getUsernameFromToken(token); 
	  	         Optional<Muser> opuser=muserRepository.findByEmail(email);
	  	         if(opuser.isPresent()){
	  	        	 Muser user=opuser.get();
	  	        	 Optional<Meeting> optionalMeeting = meetrepo.FindByMeetingId(MeetingId);
	  	            if (optionalMeeting.isEmpty()) {
	  	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found");
	  	            }
	  	            Meeting meeting = optionalMeeting.get();
	  	            // Delete the Zoom meeting using the Zoom API
	  	            String accessToken = zoomTokenService.getAccessToken(user.getInstitutionName());
	  	            
                   boolean res=zoomMethod.deleteMeeting(MeetingId, accessToken);
	  	            if (res) {
	  	                // Meeting deleted successfully
	  	            	Long notificationId=meeting.getNotificationId();
	  	            	notiuser.deleteByNotificationId(notificationId);
	  	            	notidetail.deleteByNotifyId(notificationId);
	  	                meetrepo.delete(meeting); // Delete from your database
	  	                return ResponseEntity.ok("Meeting deleted successfully");
	  	            } else {
	  	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	  	            }
	  	        	
	  	       }else {
	  	    	   System.out.println("user unauthorized");
	        		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	        	 }
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
		
		public ResponseEntity<?>getMeetDetailsForEdit(String token, Long MeetingId ){
			try{
				if (!jwtUtil.validateToken(token)) {
	  	    		 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	  	    	 }
	  	         String email=jwtUtil.getUsernameFromToken(token); 
	  	         Optional<Muser> opuser=muserRepository.findByEmail(email);
	  	         if(opuser.isPresent()){
	  	        	 opuser.get();
	  	        	 Optional<Meeting> optionalMeeting = meetrepo.FindByMeetingId(MeetingId);
	  	        	 if(optionalMeeting.isPresent()) {
	  	        		 Meeting meeting=optionalMeeting.get();
	  	        	 MeetingRequest meettosend=new MeetingRequest();
	  	        	 
	  	        	 meettosend.setTopic(meeting.getTopic());
	  	        	 meettosend.setAgenda(meeting.getAgenda());
	  	        	 meettosend.setDuration(meeting.getDuration());
	  	        	String utcDateString = meeting.getStartTime();
	  	        	meettosend.setStartingTime(utcDateString);
	  	           OffsetDateTime offsetDateTime = OffsetDateTime.parse(utcDateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	  	           LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
	  	            meettosend.setStartTime(localDateTime);
	  	            meettosend.setTimezone(meeting.getTimezone());
	  	            meettosend.setType(meeting.getType());
	  	            ZoomSettings settings= meeting.getSettings();
	  	            Settings set=new Settings();
	  	            set.setAudio(settings.getAudio());
	  	            set.setAutoRecording(settings.getAutoRecording());
	  	            set.setHostVideo(settings.isHostVideo());
	  	            set.setMuteUponEntry(settings.isMuteUponEntry());
	  	            set.setJoinBeforeHost(settings.isJoinBeforeHost());
	  	            set.setEmailNotification(settings.isEmailNotification());
	  	            set.setMuteUponEntry(settings.isMuteUponEntry());
	  	            set.setParticipantVideo(settings.isParticipantVideo());
	  	            set.setPushChangeToCalendar(settings.isPushChangeToCalendar());
	  	           List<ZoomMeetingInvitee> invitees=meeting.getSettings().getMeetingInvitees();
	  	         List<MeetingInvitee> newInvitees = new ArrayList<>();
	  	           for(ZoomMeetingInvitee invitee:invitees) {
	  	        	 MeetingInvitee newInvite=new MeetingInvitee();
	  	        	 newInvite.setEmail(invitee.getEmail());
	  	        	 newInvitees.add(newInvite);
	  	           }
	  	           set.setMeetingInvitees(newInvitees);
	  	           meettosend.setSettings(set);
	  	           return ResponseEntity.ok(meettosend);
	  	        	 }else {
	  	        		 return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	  	        	 }
	  	         }else {
	  	        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
	  		  	  
	  	         }
			}catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			
		}

}
		
		
	   




