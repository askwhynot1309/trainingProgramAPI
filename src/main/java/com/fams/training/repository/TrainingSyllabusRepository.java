package com.fams.training.repository;

import com.fams.training.entity.TrainingProgram;
import com.fams.training.entity.TrainingSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface TrainingSyllabusRepository extends JpaRepository<TrainingSyllabus, Integer> {
    @Transactional
    void deleteByTrainingProgram(TrainingProgram trainingProgram);
}
