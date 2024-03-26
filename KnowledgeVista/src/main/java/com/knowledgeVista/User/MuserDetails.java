package com.knowledgeVista.User;

import com.knowledgeVista.Organisation.OrgDetails;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter@Setter@NoArgsConstructor
public class MuserDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long MuserDetailsID;
	@OneToOne
	@JoinColumn(name="UserId",referencedColumnName="userId")
	private Muser UserId;
	
	@OneToOne
	@JoinColumn(name="OrgId",referencedColumnName="OrgId")
	private OrgDetails OrgId;
	
	@OneToOne
	@JoinColumn(name="OccupationID",referencedColumnName="OccupationId")
	private OccupationDetails OccupationId;
	
	@OneToOne
	@JoinColumn(name="SkillId",referencedColumnName="SkillId")
	private SkillDetails SkillId;
	private String website;
	private String twitterUrl;
	private String FacebookUrl;
	private String LinkedinUrl;
	
	
	

}
