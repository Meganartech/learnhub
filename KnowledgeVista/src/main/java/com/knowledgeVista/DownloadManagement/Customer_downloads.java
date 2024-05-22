package com.knowledgeVista.DownloadManagement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter@Setter@NoArgsConstructor
public class Customer_downloads {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotNull
	    @Column(nullable = false)
	    private String name;

	    @NotNull
	    @Column(nullable = false)
	    private String email;

	    @NotNull
	    @Column(nullable = false)
	    private String countryCode;

	    @NotNull
	    @Column(nullable = false)
	    private String phone;
	    private String description;
	    private String version;
	    private Integer courseCount;
	    private Integer trainerCount;
	    private Integer studentCount;


}
