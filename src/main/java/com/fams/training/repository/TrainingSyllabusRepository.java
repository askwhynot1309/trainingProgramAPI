package com.fams.training.repository;

import com.fams.training.entity.TrainingProgram;
import com.fams.training.entity.TrainingSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface TrainingSyllabusRepository extends JpaRepository<TrainingSyllabus, Integer> {
    @Transactional(rollbackOn = Exception.class)
    void deleteByTrainingProgram(TrainingProgram trainingProgram);

    List<TrainingSyllabus> findByTrainingProgram(TrainingProgram trainingProgram);
}
