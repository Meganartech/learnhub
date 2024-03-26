package com.knowledgeVista.ThumNail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter@Setter@NoArgsConstructor
public class ThumNailDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long ThumNailId;
	private String title;
	private String Description;
	private String CourseLink;
	 @OneToOne(mappedBy = "ThumNailId")
	    private ThumNailPosition thumNailPosition;

}
