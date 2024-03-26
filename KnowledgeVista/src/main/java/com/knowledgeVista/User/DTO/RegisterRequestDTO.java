package com.knowledgeVista.User.DTO;

import com.knowledgeVista.Organisation.OrgDetails;
import com.knowledgeVista.User.Muser;
import com.knowledgeVista.User.MuserDetails;
import com.knowledgeVista.User.MuserRoles;
import com.knowledgeVista.User.OccupationDetails;
import com.knowledgeVista.User.SkillDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor
public class RegisterRequestDTO {
	    private Muser muser;
	    private MuserRoles muserrole;
	    private MuserDetails muserDetails;
	    private OrgDetails orgDetails;
	    private OccupationDetails occupationDetails;
	    private SkillDetails skillDetails;

}
