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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<TrainingProgram> getAllPagingTrainingProgram(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
        return trainingRepository.findAll(pageable);
    }

    @Override
    public List<TrainingProgram> getAllTrainingProgram() {
        return trainingRepository.findAll();
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
                            csvRecord.get("topicId"),
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

    @Override
    public TrainingProgram searchTrainingProgram(Integer trainingId) {
        return trainingRepository.findById(trainingId).orElse(null);
    }


    @Override
    public Page<TrainingProgram> searchTrainingProgramWithKeyword(String name, Pageable pageable) {
        return trainingRepository.findByNameContaining(name, pageable);
    }

    @Override
    public Page<TrainingProgram> filterByStatus(String key, Pageable pageable) {
        return trainingRepository.findByStatus(key, pageable);
    }

    @Override
    public TrainingProgram duplicateTrainingProgram(Integer id) {
        TrainingProgram program = trainingRepository.findById(id).orElse(null);
        TrainingProgram duplicateProgram = new TrainingProgram();
        if (program == null) {
            return null;
        } else {

            int newestId = getNextTrainingProgramId();
            duplicateProgram.setTrainingId(newestId);
            duplicateProgram.setName(program.getName());
            duplicateProgram.setCreateBy(program.getCreateBy());
            duplicateProgram.setCreateDate(program.getCreateDate());
            duplicateProgram.setModifyBy(program.getModifyBy());
            duplicateProgram.setModifyDate(program.getModifyDate());
            duplicateProgram.setStartTime(program.getStartTime());
            duplicateProgram.setTopicId(program.getTopicId());
            duplicateProgram.setDuration(program.getDuration());
            duplicateProgram.setStatus(program.getStatus());

            trainingRepository.save(duplicateProgram);

            return duplicateProgram;
        }
    }

    public int getNextTrainingProgramId() {
        List<TrainingProgram> trainingPrograms = trainingRepository.findAllByOrderByTrainingIdDesc();

        if (!trainingPrograms.isEmpty()) {
            return trainingPrograms.get(0).getTrainingId() + 1;
        } else {
            return 1;
        }
    }


    @Override
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

    @Override
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

    public TrainingProgram updateTrainingProgram(Integer trainingId, TrainingProgram updatedProgram) {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);

        if (optionalTrainingProgram.isPresent()) {
            TrainingProgram trainingProgram = optionalTrainingProgram.get();
            if(!trainingProgram.getStatus().equals("Active")){
                throw new IllegalStateException("Training program must be active to be updated");
            }

            LocalDate currentDate = LocalDate.now();
            if (trainingProgram.getModifyDate() != null && trainingProgram.getModifyDate().isAfter(currentDate)) {
                throw new IllegalStateException("Invalid modify date");
            }

            trainingProgram.setName(updatedProgram.getName());
            trainingProgram.setCreateDate(updatedProgram.getCreateDate());
            trainingProgram.setCreateBy(updatedProgram.getCreateBy());
            trainingProgram.setDuration(updatedProgram.getDuration());
            trainingProgram.setModifyBy(updatedProgram.getModifyBy());
            trainingProgram.setTopicId(updatedProgram.getTopicId());
            trainingProgram.setStartTime(updatedProgram.getStartTime());
            trainingProgram.setModifyDate(currentDate);


            return trainingRepository.save(trainingProgram);
        } else {
            throw new EntityNotFoundException("Training program not found. Id not found");
        }
    }
}
