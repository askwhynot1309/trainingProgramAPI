package com.fams.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name ="assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignmentId", nullable = false, length = 500)
    private Integer assignmentId;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "dueDate", nullable = false)
    private LocalDate dueDate;

    @Column(name = "score", nullable = false)
    private int score;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainingId")
    @JsonIgnore
    private TrainingProgram trainingProgram;

    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentId=" + assignmentId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", score=" + score +
                '}';
    }
}
