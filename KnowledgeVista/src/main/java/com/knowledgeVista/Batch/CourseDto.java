package com.knowledgeVista.Batch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CourseDto {
	private Long courseId;
    private String courseName;

    public CourseDto(Long courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }
}
