package com.knowledgeVista.Payments;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity@Table
@Getter@Setter
@NoArgsConstructor
public class Course_PartPayment_Structure {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long partpayid;
	private Long courseId;
	private Long paymenttypeId;
	private LocalDate datecreated;
	private Long createdBy;
	private Long AppreovedBy;
	@Column(columnDefinition ="Varchar(100)")
	private String ReferalCode;
	 @OneToMany(mappedBy = "partpay", cascade = CascadeType.REMOVE)
	private List<InstallmentDetails> installmentDetail;

}
