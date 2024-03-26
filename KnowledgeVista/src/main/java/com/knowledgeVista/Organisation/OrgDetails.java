package com.knowledgeVista.Organisation;

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
public class OrgDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long OrgId;
	private String OrgName;
	private Long ContactNumber;
	private String Address;
	
	

}
