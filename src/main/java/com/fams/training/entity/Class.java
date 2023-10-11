package com.fams.training.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "class")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer classId;

    @Length(max = 45)
    private String className;

    @Length(max = 45)
    private String classCode;

    @Length(max = 45)
    private LocalDate createdDate;

    @Length(max = 45)
    private String createdBy;

    @Length(max = 45)
    private String modifyBy;

    @Length(max = 45)
    private LocalDate modifyDate;

    @Length(max = 45)
    private int duration;

    @Length(max = 45)
    private String status;

    @Length(max = 45)
    private String location;

    @Length(max = 45)
    private String fsu;

    @Length(max = 45)
    private LocalDate startDate;

    @Length(max = 45)
    private LocalDate endDate;

    @Length(max = 45)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainingId")
    @JsonIgnore
    private TrainingProgram trainingProgram;
}
