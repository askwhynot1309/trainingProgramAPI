package com.fams.training.service.Interface;

import com.fams.training.entity.TrainingProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface TrainingService {
    Page<TrainingProgram> getAllPagingTrainingProgram(int page, int size);
    List<TrainingProgram> getAllTrainingProgram();
//    public void importFile(MultipartFile file, String encoding, char columnSeparator, String scanningMethod, String duplicateHandling);
    public List<TrainingProgram> importTrainingProgramFromFile(MultipartFile file, InputStream is, String encoding,char columnSeparator, String scanningMethod, String duplicateHandling);
    int createNewTrainingProgram(TrainingProgram trainingProgram);
    int deleteTrainingProgram(Integer id);
    public void deactivateTrainingProgram(Integer trainingId);
    public void activateTrainingProgram(Integer trainingId);
    public TrainingProgram searchTrainingProgram(Integer trainingId);
    Page<TrainingProgram> searchTrainingProgramWithKeyword(String name, Pageable pageable);
    Page<TrainingProgram> filterByStatus(String name, Pageable pageable);
    TrainingProgram duplicateTrainingProgram(Integer id);

}