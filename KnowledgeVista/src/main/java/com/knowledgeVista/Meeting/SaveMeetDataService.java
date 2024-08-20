package com.knowledgeVista.Meeting;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgeVista.Meeting.zoomclass.InviteeRepo;
import com.knowledgeVista.Meeting.zoomclass.Meeting;
import com.knowledgeVista.Meeting.zoomclass.Meetrepo;
import com.knowledgeVista.Meeting.zoomclass.ZoomMeetingInvitee;
import com.knowledgeVista.Meeting.zoomclass.ZoomSettings;
import com.knowledgeVista.Meeting.zoomclass.ZoomsettingRepo;
import com.knowledgeVista.Notification.Service.NotificationService;


@Service
public class SaveMeetDataService {
@Autowired
private Meetrepo meetrepo;
@Autowired
private ZoomsettingRepo settingrepo;
@Autowired
private InviteeRepo inviteRepo;
@Autowired
private NotificationService notiservice;
public void PatchsaveData(String email,String jsonString,Long meetingId) {
	try {
	    ObjectMapper mapper = new ObjectMapper();
	    System.out.println("json: " + jsonString);
	    JsonNode rootNode = mapper.readTree(jsonString); // Parse JSON to JsonNode

	    Optional<Meeting> opmeeting = meetrepo.FindByMeetingId(meetingId);
	    Meeting meeting = opmeeting.orElse(null);
	    
	    if (meeting == null) {
	        System.out.println("Meeting with ID " + meetingId + " not found");
	        return;
	    }
	    
	    // Update meeting details
	    if (rootNode.has("uuid")) {
	        meeting.setUuid(rootNode.get("uuid").asText());
	    }
	    if (rootNode.has("id")) {
	        meeting.setMeetingId(rootNode.get("id").asLong());
	    }
	    if (rootNode.has("type")) {
	        meeting.setType(rootNode.get("type").asInt());
	    }
	    if (rootNode.has("agenda")) {
	        meeting.setAgenda(rootNode.get("agenda").asText());
	    }
	    if (rootNode.has("host_email")) {
	        meeting.setHostEmail(rootNode.get("host_email").asText());
	    }
	    if (rootNode.has("start_url")) {
	        meeting.setStartUrl(rootNode.get("start_url").asText());
	    }
	    if (rootNode.has("join_url")) {
	        meeting.setJoinUrl(rootNode.get("join_url").asText());
	    }
	    if (rootNode.has("password")) {
	        meeting.setPassword(rootNode.get("password").asText());
	    }
	    if (rootNode.has("topic")) {
	        meeting.setTopic(rootNode.get("topic").asText());
	    }
	    if (rootNode.has("duration")) {
	        meeting.setDuration(rootNode.get("duration").asInt());
	    }
	    if (rootNode.has("start_time")) {
	        String startTimeStr = rootNode.get("start_time").asText();
	        try {
	            meeting.setStartTime(startTimeStr);
	        } catch (DateTimeParseException e) {
	            System.err.println("Failed to parse start time: " + startTimeStr);
	        }
	    }
	    if (rootNode.has("timezone")) {
	        meeting.setTimezone(rootNode.get("timezone").asText());
	    }
	    meeting.setCreatedBy(email);

	    // Extract settings
	    ZoomSettings settings;
	    if (rootNode.has("settings")) {
	        JsonNode settingsNode = rootNode.get("settings");
	        if (meeting.getSettings() == null) {
	            settings = new ZoomSettings();
	        } else {
	            settings = meeting.getSettings();
	        }
	        settings.setAudio(settingsNode.get("audio").asText());
	        settings.setAutoRecording(settingsNode.get("auto_recording").asText());
	        settings.setEmailNotification(settingsNode.get("email_notification").asBoolean());
	        settings.setHostVideo(settingsNode.get("host_video").asBoolean());
	        settings.setJoinBeforeHost(settingsNode.get("join_before_host").asBoolean());
	        settings.setMuteUponEntry(settingsNode.get("mute_upon_entry").asBoolean());

	        // Clear existing invitees
	        if (settings.getMeetingInvitees() != null) {
	            settings.getMeetingInvitees().clear();
	        }

	        Set<String> existingEmails = new HashSet<>();
	        List<ZoomMeetingInvitee> inviteesToSave = new ArrayList<>();
	        if (rootNode.get("settings").has("meeting_invitees")) {
	            JsonNode inviteesNode = rootNode.get("settings").get("meeting_invitees");
	            if (inviteesNode.isArray()) {
	                for (JsonNode inviteeNode : inviteesNode) {
	                    if (inviteeNode.has("email")) {
	                        String inviteeEmail = inviteeNode.get("email").asText().trim();
	                        if (!inviteeEmail.isEmpty() && !existingEmails.contains(inviteeEmail)) {
	                            ZoomMeetingInvitee invitee = new ZoomMeetingInvitee();
	                            invitee.setEmail(inviteeEmail);
	                            invitee.setZoomSettings(settings);
	                            inviteesToSave.add(invitee);
	                            existingEmails.add(inviteeEmail);
	                        }
	                    }
	                }
	            }
	        }
	        meeting.setSettings(settings);
	        meeting.getSettings().getMeetingInvitees().addAll(inviteesToSave);
	        meetrepo.save(meeting);

	        if (!inviteesToSave.isEmpty()) {
	            Long notid = notiservice.createNotification("MeetingUpdated", "system", "Meeting Updated Click To Join the meeting", "System", meeting.getTopic(), meeting.getJoinUrl());
	            meeting.setNotificationId(notid);
	            if (notid != null) {
	                notiservice.SpecificCreateNotificationusingEmail(notid, new ArrayList<>(existingEmails));
	            }
	        }
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
}


	public void saveMeetData(String jsonString,String email) {
		try {
	
			 ObjectMapper mapper = new ObjectMapper();
			
			 System.out.println("json"+jsonString);
			   JsonNode rootNode = mapper.readTree(jsonString); 
			   // Parse JSON to JsonNode

			          Meeting meeting = new Meeting();
			          if (rootNode.has("uuid")) {
			          meeting.setUuid(rootNode.get("uuid").asText());
			          }
			          if (rootNode.has("id")) {
			          meeting.setMeetingId(rootNode.get("id").asLong());
			          }
			          if (rootNode.has("type")) {
			         meeting.setType(rootNode.get("type").asInt());
			          }
			          if (rootNode.has("agenda")) {
                     meeting.setAgenda(rootNode.get("agenda").asText());
			          }
			          if (rootNode.has("host_email")) {
			          meeting.setHostEmail(rootNode.get("host_email").asText());
			          }

			          if (rootNode.has("start_url")) {
			              meeting.setStartUrl(rootNode.get("start_url").asText());
			             
			          }
			          if (rootNode.has("join_url")) {
			          meeting.setJoinUrl(rootNode.get("join_url").asText());
			          }
			         String Joinurl=rootNode.get("join_url").asText();
			         if (rootNode.has("password")) {
			          meeting.setPassword(rootNode.get("password").asText());
			         }
			         String topic="";
			         if (rootNode.has("topic")) {
			          meeting.setTopic(rootNode.get("topic").asText());
                         topic=rootNode.get("topic").asText();
			         }
			         if (rootNode.has("duration")) {
			          meeting.setDuration(rootNode.get("duration").asInt());
			          
			         }
			         if (rootNode.has("start_time")) {
			          String startTimeStr = rootNode.get("start_time").asText();
			         
			          
			          try {
			              meeting.setStartTime(startTimeStr);
			             
			          } catch (DateTimeParseException e) {
			              System.err.println("Failed to parse start time: " + startTimeStr);
			          }
			         
			         }
			         if (rootNode.has("timezone")) {
			          meeting.setTimezone(rootNode.get("timezone").asText());
			        
			         }
			          meeting.setCreatedBy(email);
			         

			          // Extract settings
			          if (rootNode.has("settings")) {
			              JsonNode settingsNode = rootNode.get("settings");
			              ZoomSettings settings = new ZoomSettings();
			              settings.setAudio(settingsNode.get("audio").asText());
			              settings.setAutoRecording(settingsNode.get("auto_recording").asText());
			              settings.setEmailNotification(settingsNode.get("email_notification").asBoolean());
			              settings.setHostVideo(settingsNode.get("host_video").asBoolean());
			              settings.setJoinBeforeHost(settingsNode.get("join_before_host").asBoolean());
			              settings.setMuteUponEntry(settingsNode.get("mute_upon_entry").asBoolean());
			              // ... Extract other settings as needed
			              settingrepo.save(settings);

			              // Extract invitees
			              if (settingsNode.has("meeting_invitees")) {
			            	    JsonNode inviteesNode = settingsNode.get("meeting_invitees");
			            	    if (inviteesNode.isArray()) {
			            	        List<ZoomMeetingInvitee> invitees = new ArrayList<>();
			            	        List<String> notiuserlist = new ArrayList<>(); 
			            	        for (JsonNode inviteeNode : inviteesNode) {
			            	            if (inviteeNode.has("email")) {
			            	                String estring = inviteeNode.get("email").asText();
			            	                if (estring != null && !estring.trim().isEmpty()) { // Check if the email is not empty
			            	                   
			            	                     ZoomMeetingInvitee invitee = new ZoomMeetingInvitee();
			            	                    invitee.setEmail(estring);
			            	                    System.out.println("email=" + invitee.getEmail());
			            	                    invitee.setZoomSettings(settings);
			            	                    notiuserlist.add(estring);
			            	                    invitees.add(invitee);
			            	                }
			            	            }
			            	        }
			            	        boolean emailExists = invitees.stream()
	                                          .anyMatch(invitee -> invitee.getEmail().equalsIgnoreCase(email));

	            // Add the parameter email if it's not in the list
	            if (!emailExists) {
	                ZoomMeetingInvitee parameterInvitee = new ZoomMeetingInvitee();
	                parameterInvitee.setEmail(email);
	                parameterInvitee.setZoomSettings(settings);
	                invitees.add(parameterInvitee);
	                notiuserlist.add(email);
	            }
			            	        if (!invitees.isEmpty()) {
			            	        	  Long notid= notiservice.createNotification("MeetingSheduled", "system", "New Meeting Sheduled Click To Join the meeting", "System",topic , Joinurl);
		            	                    meeting.setNotificationId(notid);
			            	        	  if(notid!=null) {
		            	                    	 notiservice.SpecificCreateNotificationusingEmail(notid,notiuserlist );
		            	                     }
			            	            inviteRepo.saveAll(invitees);
			            	            settings.setMeetingInvitees(invitees);
			            	        }
			            	    }
			            	}


			              meeting.setSettings(settings);
			            meetrepo.save(meeting);
                          
			              settingrepo.save(settings);
			          } else {
			              // Save the meeting without settings if settings are not present
			              meetrepo.save(meeting);
			          }
			      
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
