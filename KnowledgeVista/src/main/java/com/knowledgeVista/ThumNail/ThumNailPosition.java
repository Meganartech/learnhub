package com.knowledgeVista.ThumNail;

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
public class ThumNailPosition {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long ThumpNailPositionId;
	@OneToOne
	@JoinColumn(name="ThumNailID",referencedColumnName="ThumNailId")
	private ThumNailDetails ThumNailId;
	private int AlignmentPosition;
	private int position;
	private int size;
	private byte[] BackgroundImage;
	private String BackgroundColor;
	private Long CourseId;
	private Boolean IsActive;
	private Boolean IsDefault;
	
	

}
