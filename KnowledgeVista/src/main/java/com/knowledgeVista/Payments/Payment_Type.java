package com.knowledgeVista.Payments;

import jakarta.persistence.Column;
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
public class Payment_Type {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentTypeId;
	   @Column(columnDefinition = "Varchar(50)")
	private String PaymentTypeName;

}
