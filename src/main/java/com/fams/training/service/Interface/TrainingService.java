package com.fams.training.service.Interface;

import com.fams.training.DTO.ClassDTO;
import com.fams.training.DTO.SyllabusDTO;
import com.fams.training.DTO.SyllabusRequest;
import com.fams.training.DTO.TrainingProgramDTO;
import com.fams.training.entity.TrainingProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface TrainingService {
    Page<TrainingProgramDTO> getAllPagingTrainingProgram(int page, int size, String status);
    List<TrainingProgram> getAllTrainingProgram();
    public List<TrainingProgram> importTrainingProgramFromFile(MultipartFile file, InputStream is, String encoding,char columnSeparator, String scanningMethod, String duplicateHandling);
    int createNewTrainingProgram(TrainingProgramDTO trainingProgram);
    public TrainingProgram updateTrainingProgram(Integer trainingId, TrainingProgramDTO updatedProgram);
    int deleteTrainingProgram(Integer id);
    public void deactivateTrainingProgram(Integer trainingId);
    public void activateTrainingProgram(Integer trainingId);
    public TrainingProgramDTO searchTrainingProgram(Integer trainingId);
    Page<TrainingProgramDTO> searchTrainingProgramWithKeyword(String name, Pageable pageable);
    public Page<TrainingProgram> getSortedTrainingProgram(String sortBy, String sortOrder, Pageable pageable);
    TrainingProgram duplicateTrainingProgram(Integer id);
    public boolean addOrUpdateSyllabusId(Integer trainingId, List<SyllabusRequest> syllabusRequest);
}
