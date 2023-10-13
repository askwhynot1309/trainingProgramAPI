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
    @Column(name = "trainingId", nullable = false, length = 500)
    private Integer trainingId;

    @Length(max = 45)
    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignment = new ArrayList<>();

    @Length(max = 45)
    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> resources = new ArrayList<>();

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "createBy", nullable = false, length = 500)
    private String createBy;

    @Column(name = "createDate", nullable = false)
    private LocalDate createDate;

    @Column(name = "modifyBy", nullable = false, length = 500)
    private String modifyBy;

    @Column(name = "modifyDate", nullable = false)
    private LocalDate modifyDate;

    @Column(name = "startTime", nullable = false)
    private LocalDate startTime;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Column(name = "topicId", nullable = false, length = 500)
    private String topicId;

    @Column(name = "status", nullable = false, length = 500)
    private String status;


    public TrainingProgram(Integer trainingId, String name, String createBy, LocalDate createDate, String modifyBy, LocalDate modifyDate, LocalDate startTime, String duration, String topicId, String status) {
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
