package com.knowledgeVista.Payments;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity@Table@Getter
@Setter@NoArgsConstructor
public class InstallmentDetails {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long Id;
	private Long InstallmentNumber;
	private Long InstallmentAmount;
	private Long DurationInDays;
	 @ManyToOne
	 @JoinColumn(name = "partpayid", referencedColumnName = "partpayid")
	    private Course_PartPayment_Structure partpay;
	 
	 @Override
		public String toString() {
			return "InstallmentDetails [Id=" + Id + ", InstallmentNumber=" + InstallmentNumber + ", InstallmentAmount="
					+ InstallmentAmount + ", DurationInDays=" + DurationInDays + ", partpay=" + partpay + "]";
		}
}
