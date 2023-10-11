package com.fams.training.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "trainingProgram")
public class TrainingProgram {
    @Id
    private Integer trainingId;

    @Length(max = 45)
    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignment = new ArrayList<>();

    @Length(max = 45)
    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> resources = new ArrayList<>();

    @Length(max = 45)
    private String name;

    @Length(max = 45)
    private String createBy;

    @Length(max = 45)
    private LocalDate createDate;

    @Length(max = 45)
    private String modifyBy;

    @Length(max = 45)
    private LocalDate modifyDate;

    @Length(max = 45)
    private LocalDate startTime;

    @Length(max = 45)
    private String duration;

    @Length(max = 45)
    private Integer topicId;

    @Length(max = 45)
    private String status;

    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Class> classes = new ArrayList<>();

    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingProgramSyllabus> trainingProgramSyllabuses = new ArrayList<>();

    public TrainingProgram(Integer trainingId, String name, String createBy, LocalDate createDate, String modifyBy, LocalDate modifyDate, LocalDate startTime, String duration, Integer topicId, String status) {
        this.trainingId = trainingId;
        this.name = name;
        this.createBy = createBy;
        this.createDate = createDate;
        this.modifyBy = modifyBy;
        this.modifyDate = modifyDate;
        this.startTime = startTime;
        this.duration = duration;
        this.topicId = topicId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "TrainingProgram{" +
                "trainingId=" + trainingId +
                ", name='" + name + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createDate=" + createDate +
                ", modifyBy='" + modifyBy + '\'' +
                ", modifyDate=" + modifyDate +
                ", startTime=" + startTime +
                ", duration='" + duration + '\'' +
                ", topicId=" + topicId +
                ", status='" + status + '\'' +
                '}';
    }

}
