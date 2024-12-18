package com.knowledgeVista.Migration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.knowledgeVista.User.MuserRoles;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter@Setter@NoArgsConstructor
public class MuserMigrationDto {
	 private Long userId;
	    private String username;
	    private String psw;
	    private String email;
	    private String phone;
	    private Boolean isActive;
	    private LocalDate dob;
	    private String skills;
	    private String institutionName;
	    private byte[] profile;
	    private String countryCode;
	    private LocalDateTime lastactive;
	    private String inactiveDescription;
	    private MuserRoles role;
	    
		public MuserMigrationDto(Long userId, String username, String psw, String email, String phone, Boolean isActive,
				LocalDate dob, String skills, String institutionName, byte[] profile, String countryCode,
				LocalDateTime lastactive, String inactiveDescription, MuserRoles role) {
			super();
			this.userId = userId;
			this.username = username;
			this.psw = psw;
			this.email = email;
			this.phone = phone;
			this.isActive = isActive;
			this.dob = dob;
			this.skills = skills;
			this.institutionName = institutionName;
			this.profile = profile;
			this.countryCode = countryCode;
			this.lastactive = lastactive;
			this.inactiveDescription = inactiveDescription;
			this.role = role;
		}

	    

}
