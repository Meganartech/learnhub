package com.knowledgeVista.Notification;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table
public class NotificationDetails {
	@Id
	@GeneratedValue(strategy  = GenerationType.IDENTITY)
	private Long notifyId;
	
	private Long notifyTypeId;
	@Column(columnDefinition = "Varchar(100)")
	private String Description;
	private LocalDate CreatedDate;
	@Column(columnDefinition = "Varchar(100)")
	private String CreatedBy; 
	private Boolean isActive;
	private Boolean for_student;
    private Boolean for_trainer;
    private Boolean for_Admin;
    private Boolean for_All;
    
	

}
