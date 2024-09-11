package com.knowledgeVista.User;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@NoArgsConstructor
public class MuserProfileDTO {
	 private byte[] profile;
	    private String countryCode;
	    private String roleName;

	    public MuserProfileDTO(byte[] profile, String countryCode, String roleName) {
	        this.profile = profile;
	        this.countryCode = countryCode;
	        this.roleName = roleName;
	    }

}
