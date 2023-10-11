package com.fams.training.repository;

import com.fams.training.entity.Syllabus;
import com.fams.training.entity.TrainingProgramSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyllabusRepository extends JpaRepository<Syllabus, Integer> {
}
