package com.fams.training.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {
    private Integer assignmentId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private int score;
}
