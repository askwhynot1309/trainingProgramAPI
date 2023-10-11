package com.fams.training.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "training_program_syllabus")
public class TrainingProgramSyllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private TrainingProgram trainingProgram;

    @ManyToOne
    @JoinColumn(name = "syllabus_id")
    private Syllabus syllabus;

}
