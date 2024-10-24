package com.knowledgeVista.SocialLogin;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter@Setter@NoArgsConstructor
public class SocialLoginKeys {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column
	    private Long id;
	   private String provider;
	   private String institutionName;
	   private String clientid;
	   private String clientSecret;
	   private String RedirectUrl;
	   
	   
}
