package com.knowledgeVista.Batch.Event;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.Id;


@SqlResultSetMapping(
	    name = "EventMapping",
	    classes = @ConstructorResult(
	        targetClass = EventDTO.class,
	        columns = {
	            @ColumnResult(name = "title", type = String.class),
	            @ColumnResult(name = "MeetingId", type = Long.class),
	            @ColumnResult(name = "startDateTime", type = LocalDateTime.class),
	            @ColumnResult(name = "endDateTime", type = LocalDateTime.class),
	            @ColumnResult(name = "duration", type = Integer.class),
	            @ColumnResult(name = "startTime", type = String.class),
	            @ColumnResult(name = "batchString", type = String.class),
	            @ColumnResult(name = "type", type = String.class),
	            @ColumnResult(name="batchid",type=Long.class),
	            @ColumnResult(name="batchName",type=String.class),
	            @ColumnResult(name="quizzid",type=Long.class)
	        }
	    )
	)
	@Entity
	public class EventMappingEntity {
	    @Id
	    private Long id; // Just to satisfy JPA, not actually used
	}

