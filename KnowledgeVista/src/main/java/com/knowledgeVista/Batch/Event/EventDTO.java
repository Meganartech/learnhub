package com.knowledgeVista.Batch.Event;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class EventDTO {
private String title;
private Long MeetingId;
private LocalDateTime startDateTime;
private LocalDateTime endDateTime;
private Integer duration;
private String startTime;
private String batchString;
private String type; 
private Long batchid;
private String batchName;
private Long quizzid;
public EventDTO(String title, Long MeetingId, LocalDateTime startDateTime, LocalDateTime endDateTime,
        Integer duration, String startTime, String batchString, String type,Long batchid,String batchName,Long quizzid) {
this.title = title;
this.MeetingId = MeetingId;
this.startDateTime = startDateTime;
this.endDateTime = endDateTime;
this.duration = duration;
this.startTime = startTime;
this.batchString = batchString;
this.type = type;
this.batchid=batchid;
this.batchName=batchName;
this.quizzid=quizzid;
}
}

