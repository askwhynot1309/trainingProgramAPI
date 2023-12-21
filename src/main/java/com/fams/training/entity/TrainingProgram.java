package com.fams.training.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Column(name = "trainingId",   length = 25)
    private Integer trainingId;

    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignment = new ArrayList<>();

    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> resources = new ArrayList<>();

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "createBy", length = 50)
    private String createBy;

    @Column(name = "createDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @Column(name = "modifyBy", length = 50)
    private String modifyBy;

    @Column(name = "modifyDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDate;

    @Column(name = "duration")
    private String duration;

    @Column(name = "topicId", length = 25)
    private String topicId;

    @Column(name = "status", length = 25)
    private String status;

    @Column(name = "info", columnDefinition = "TEXT")
    private String info;

    public TrainingProgram(Integer trainingId, String name, String createBy, LocalDateTime createDate, String modifyBy, LocalDateTime modifyDate, String duration, String topicId, String status, String info) {
        this.trainingId = trainingId;
        this.name = name;
        this.createBy = createBy;
        this.createDate = createDate;
        this.modifyBy = modifyBy;
        this.modifyDate = modifyDate;
        this.duration = duration;
        this.topicId = topicId;
        this.status = status;
        this.info = info;
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
                ", duration='" + duration + '\'' +
                ", topicId=" + topicId +
                ", status='" + status + '\'' +
                '}';
    }

}
