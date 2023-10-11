package com.fams.training.service.serviceInterface;

import com.fams.training.entity.TrainingProgram;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface TrainingService {
    List<TrainingProgram> getAllTrainingProgram();
    public void importFile(MultipartFile file);
    public List<TrainingProgram> importTrainingProgramFromFile(InputStream is );
    int createNewTrainingProgram(TrainingProgram trainingProgram);
    int deleteTrainingProgram(Integer id);
}
