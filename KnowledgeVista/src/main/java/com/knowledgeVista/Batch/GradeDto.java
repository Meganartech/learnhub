package com.knowledgeVista.Batch;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeDto {
	private String batchName;
    private double weightedTest;
    private double weightedQuiz ;
    private double weightedAttendance ;
    private double totalScore;
    private double weightedAssignment;
    private String result;
	

}
