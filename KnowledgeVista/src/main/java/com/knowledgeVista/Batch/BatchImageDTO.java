package com.knowledgeVista.Batch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchImageDTO {
	 private Long batchId;
	    private byte[] batchImage;
}
