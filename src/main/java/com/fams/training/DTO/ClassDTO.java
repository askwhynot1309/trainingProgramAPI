package com.fams.training.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {
    private String className;

    private String createdBy;

    private LocalDate createdDate;

    private LocalDate endDate;

    private String modifiedBy;

    private LocalDate modifiedDate;

    private LocalDate startDate;

    private Boolean status;

    private Integer syllabusId;

    private Integer trainingProgramId;
}
