package com.fams.training.DTO;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;

@Data
public class SyllabusDTO {
    private Long id;
    private String topicName;
    private String technicalGroup;
    private String version;
    private String trainingAudience;
    private String topicOutline;
    private String trainingMaterial;
    private String trainingPrinciple;
    private String priority;
    private LocalDate createdDate;
    private String createdBy;
    private LocalDate modifiedDate;
    private String modifiedBy;
    private String status;
    private Integer training_id;
}
