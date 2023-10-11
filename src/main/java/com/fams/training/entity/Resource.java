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
    private Integer resourceId;

    @Length(max = 45)
    private String title;

    @Length(max = 45)
    private String description;

    @Length(max = 45)
    private String file;

    @Length(max = 45)
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
