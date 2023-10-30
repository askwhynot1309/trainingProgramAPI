package com.fams.training.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "trainingsyllabus")
public class TrainingSyllabus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long syllabusId;

    private Integer orderNumber;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private TrainingProgram trainingProgram;

}
