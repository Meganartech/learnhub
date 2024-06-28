package com.knowledgeVista.User;

import java.time.LocalDate;


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
public class Madmin {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long madminId;
	    private Long userId;
	    private String licenceType;
	    private LocalDate updatedDate;

}
