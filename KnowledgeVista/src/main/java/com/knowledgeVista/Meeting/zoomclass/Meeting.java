package com.knowledgeVista.Meeting.zoomclass;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table
@Getter
@Setter@NoArgsConstructor
public class Meeting {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pkId;
	 private Long notificationId;
	 private Long MeetingId;
	 private String uuid;
	 private String HostEmail;
	 @Column(length=1000)
	 private String startUrl;
	 @Column(length=1000)
	 private String JoinUrl;
	 private String Password;
	  private String agenda;
	  private Integer duration;

     @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	  private ZoomSettings settings;
	    private String startTime;
	    private String timezone;
	    private String topic;
	    private Integer type;
	    private String createdBy;
		@Override
		public String toString() {
			return "Meeting [pkId=" + pkId + ", MeetingId=" + MeetingId + ", uuid=" + uuid + ", HostEmail=" + HostEmail
					+ ", startUrl=" + startUrl + ", JoinUrl=" + JoinUrl + ", Password=" + Password + ", agenda="
					+ agenda + ", duration=" + duration + ", settings=" + settings + ", startTime=" + startTime
					+ ", timezone=" + timezone + ", topic=" + topic + ", type=" + type + ", createdBy=" + createdBy
					+ "]";
		}
	    
	
}
