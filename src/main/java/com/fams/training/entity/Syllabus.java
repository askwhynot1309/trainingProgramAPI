package com.fams.training.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="syllabus")
public class Syllabus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer topicId;

    @Length(max = 45)
    private String topicName;

    @Length(max = 45)
    private String technicalGroup;

    @Length(max = 45)
    private String version;

    @Length(max = 45)
    private String trainingAudience;

    @Length(max = 45)
    private String topicOutline;

    @Length(max = 45)
    private String trainingMaterials;

    @Length(max = 45)
    private String trainingPrinciples;

    @Length(max = 45)
    private String priority;

    @Length(max = 45)
    private String publishStatus;

    @Length(max = 45)
    private LocalDate createDate;

    @OneToMany(mappedBy = "syllabus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingProgramSyllabus> trainingProgramSyllabus = new ArrayList<>();
}
