package com.fams.training.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusRequest {
    private Integer order;
    private Long syllabusId;
}
