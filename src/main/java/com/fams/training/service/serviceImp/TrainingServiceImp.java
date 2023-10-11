package com.fams.training.service.serviceImp;

import com.fams.training.entity.TrainingProgram;
import com.fams.training.exception.DuplicateRecordException;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.repository.TrainingRepository;
import com.fams.training.service.serviceInterface.TrainingService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImp implements TrainingService {
    @Autowired
    TrainingRepository trainingRepository;
    @Override
    public List<TrainingProgram> getAllTrainingProgram() {
        Sort sortByCreateDateDesc = Sort.by(Sort.Direction.DESC, "createDate");
        return trainingRepository.findAll(sortByCreateDateDesc);
    }

    @Override
    public void importFile(MultipartFile file) {
        try {
            List<TrainingProgram> trainingProgram = importTrainingProgramFromFile(file.getInputStream());
            trainingRepository.saveAll(trainingProgram);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    @Override
    public List<TrainingProgram> importTrainingProgramFromFile(InputStream is ) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<TrainingProgram> trainingProgramList = new ArrayList<TrainingProgram>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Integer id = Integer.valueOf(csvRecord.get(0));
                LocalDate createDate = LocalDate.parse(csvRecord.get("createDate"));
                LocalDate modifyDate = LocalDate.parse(csvRecord.get("modifyDate"));
                LocalDate startTime = LocalDate.parse(csvRecord.get("startTime"));

                Optional<TrainingProgram> existingProgram = trainingRepository.findById(id);
                System.out.println(existingProgram);
                if (existingProgram.isEmpty()){
                    TrainingProgram trainingProgram = new TrainingProgram(
                            Integer.parseInt(csvRecord.get(0)),
                            csvRecord.get("name"),
                            csvRecord.get("createBy"),
                            createDate,
                            csvRecord.get("modifyBy"),
                            modifyDate,
                            startTime,
                            csvRecord.get("duration"),
                            Integer.parseInt(csvRecord.get("topicId")),
                            csvRecord.get("status")
                    );
                    trainingProgramList.add(trainingProgram);
                } else {
                    throw new DuplicateRecordException("Duplicate record detected for ID " + id);
                }
            }

            return trainingProgramList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    @Override
    public int createNewTrainingProgram(TrainingProgram trainingProgram) {
        try{
            trainingRepository.save(trainingProgram);
            return 1;
        } catch (Exception e){
            return 0;
        }
    }

    @Override
    public int deleteTrainingProgram(Integer id) {
        try {
            trainingRepository.deleteById(id);
            return 1;
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }
    public boolean existsTrainingProgramById(Integer id) {
        return trainingRepository.existsById(id);
    }

    public TrainingProgram searchTrainingProgram(Integer trainingId) {
        return trainingRepository.findById(trainingId).orElse(null);
    }

    public Optional<TrainingProgram> findTrainingProgramWithClasses(Integer trainingId) {
        Optional<TrainingProgram> list = trainingRepository.findTrainingProgramWithClass(trainingId);
        return list;
    }

    public void deactivateTrainingProgram(Integer trainingId) {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);

        if (optionalTrainingProgram.isPresent()) {
            TrainingProgram trainingProgram = optionalTrainingProgram.get();
            trainingProgram.setStatus("Inactive");
            trainingRepository.save(trainingProgram);
        } else {
            throw new EntityNotFoundException("Training program not found");
        }
    }

    public void activateTrainingProgram(Integer trainingId) {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);

        if (optionalTrainingProgram.isPresent()) {
            TrainingProgram trainingProgram = optionalTrainingProgram.get();
            trainingProgram.setStatus("Active");
            trainingRepository.save(trainingProgram);
        } else {
            throw new EntityNotFoundException("Training program not found");
        }
    }
}
