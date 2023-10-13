package com.fams.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="resource")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resourceId", nullable = false, length = 500)
    private Integer resourceId;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "file", nullable = false, length = 500)
    private String file;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainingId")
    @JsonIgnore
    private TrainingProgram trainingProgram;


    @Override
    public String toString() {
        return "Resource{" +
                "resourceId=" + resourceId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}

