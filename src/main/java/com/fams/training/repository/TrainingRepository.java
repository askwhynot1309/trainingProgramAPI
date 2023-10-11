package com.fams.training.repository;

import com.fams.training.entity.TrainingProgram;
import com.fams.training.entity.TrainingProgramSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<TrainingProgram, Integer> {
    @Query("SELECT tp FROM TrainingProgram tp JOIN FETCH tp.classes WHERE tp.trainingId = :trainingId")
    Optional<TrainingProgram> findTrainingProgramWithClass(@Param("trainingId") Integer trainingId);
}
