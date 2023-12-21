package com.fams.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="resource")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resourceId", nullable = false, length = 25)
    private Integer resourceId;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "status", length = 25)
    private String status;

    @Column(name = "filename", length = 50)
    private String filename;

    @Column(name = "uploadedBy", length = 50, nullable = true)
    private String uploadedBy;

    @Column(name ="typeFile", length = 50)
    private  String typeFile;

    @Column(name = "uploadDateTime")
    private LocalDateTime uploadDateTime;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainingId")
    @JsonIgnore
    private TrainingProgram trainingProgram;

}
