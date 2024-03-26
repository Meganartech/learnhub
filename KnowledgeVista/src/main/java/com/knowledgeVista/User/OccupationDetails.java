package com.knowledgeVista.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter@Setter@NoArgsConstructor
public class OccupationDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long OccupationId;
	private String OccupationName;
	private  String Description;

}