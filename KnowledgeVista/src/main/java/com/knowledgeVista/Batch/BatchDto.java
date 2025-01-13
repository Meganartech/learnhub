package com.knowledgeVista.Batch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BatchDto {
	private Long id;
	private String batchId;
	private String batchTitle;
	private LocalDate startDate;
	private LocalDate endDate;
	private String institutionName;
	private Long noOfSeats;
	private Long amount;
	private String courseNames; // For aggregated course names
	private String trainerNames; // For aggregated trainer names
	private byte[] batchImage;
	private String duration; // Add this for formatted duration
    // Constructor, getters and setters

    public String getDuration() {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM"); // "MMM" for abbreviated month
        String startMonth = this.startDate.format(monthFormatter);
        String endMonth = this.endDate.format(monthFormatter);
        return startMonth + " to " + endMonth;
    }
	// Constructor matching the query
	public BatchDto(Long id, String batchId, String batchTitle, LocalDate startDate, LocalDate endDate,
			String institutionName, Long noOfSeats, Long amount, String courseNames, String trainerNames) {
		this.id = id;
		this.batchId = batchId;
		this.batchTitle = batchTitle;
		this.startDate = startDate;
		this.endDate = endDate;
		this.institutionName = institutionName;
		this.noOfSeats = noOfSeats;
		this.amount = amount;
		this.courseNames = courseNames;
		this.trainerNames = trainerNames;
	}

	public BatchDto(Long id, String batchId, String batchTitle, LocalDate startDate, LocalDate endDate,
			String institutionName, Long noOfSeats, Long amount, String courseNames, String trainerNames,
			byte[] batchImage) {
		this.id = id;
		this.batchId = batchId;
		this.batchTitle = batchTitle;
		this.startDate = startDate;
		this.endDate = endDate;
		this.institutionName = institutionName;
		this.noOfSeats = noOfSeats;
		this.amount = amount;
		this.courseNames = courseNames;
		this.trainerNames = trainerNames;
		this.batchImage = batchImage;
	}
}
