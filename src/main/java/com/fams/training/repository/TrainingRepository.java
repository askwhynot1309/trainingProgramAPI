package com.fams.training.repository;

import com.fams.training.entity.TrainingProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<TrainingProgram, Integer> {
    Page<TrainingProgram> findByNameContaining(String keyword, Pageable pageable);
    List<TrainingProgram> findAllByOrderByTrainingIdDesc();
    List<TrainingProgram> findByName(String name);
    Page<TrainingProgram> findAllByStatus(String status,Pageable pageable);

}
