package com.knowledgeVista.Course.Quizz;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShedueleListDto {
	 private Long quizzId;
	 private String quizzName;
	  private Long lessonId;
	 private String Lessontitle;
	 private LocalDateTime startDate;
	    private LocalDateTime endDate;
		public ShedueleListDto(Long quizzId, String quizzName, Long lessonId, String lessontitle,
				LocalDateTime startDate, LocalDateTime endDate) {
			super();
			this.quizzId = quizzId;
			this.quizzName = quizzName;
			this.lessonId = lessonId;
			Lessontitle = lessontitle;
			this.startDate = startDate;
			this.endDate = endDate;
		}
		
}
