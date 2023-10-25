package com.fams.training.DTO;

import com.fams.training.entity.TrainingProgram;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramDetailDTO {
    Integer code;
    TrainingProgram trainingProgramDTO;
    List<SyllabusDTO> syllabusList;
    List<ClassDTO> classList;
    String message;
}
