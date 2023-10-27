package com.fams.training.service.Imp;

import com.fams.training.DTO.ClassDTO;
import com.fams.training.DTO.PageableDTO;
import com.fams.training.DTO.ResponseMessage;
import com.fams.training.DTO.SyllabusDTO;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.exception.DuplicateRecordException;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.repository.TrainingRepository;
import com.fams.training.service.Interface.TrainingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImp implements TrainingService {
    @Autowired
    TrainingRepository trainingRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    //lấy danh sách đã phân trang của program list
    @Override
    public Page<TrainingProgram> getAllPagingTrainingProgram(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
        return trainingRepository.findAll(pageable);
    }

    @Override
    public List<TrainingProgram> getAllTrainingProgram() {
        return trainingRepository.findAll();
    }

//    @Override
//    public void importFile(MultipartFile file, String encoding, char columnSeparator, String scanningMethod, String duplicateHandling) {
//        try {
//            List<TrainingProgram> trainingProgram = importTrainingProgramFromFile(file.getInputStream(), encoding, columnSeparator, scanningMethod, duplicateHandling);
//            trainingRepository.saveAll(trainingProgram);
//        } catch (IOException e) {
//            throw new RuntimeException("fail to store csv data: " + e.getMessage());
//        }
//    }


    //import 1 file csv, có duplicate handling
    @Override
    public List<TrainingProgram> importTrainingProgramFromFile(MultipartFile file, InputStream is, String encoding, char columnSeparator, String scanningMethod, String duplicateHandling) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, encoding));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim().withDelimiter(columnSeparator));) {
            System.out.println("Column Separator: " + columnSeparator);

            List<TrainingProgram> trainingProgramList = new ArrayList<TrainingProgram>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                int id = Integer.parseInt(csvRecord.get(0));
                LocalDate createDate = LocalDate.parse(csvRecord.get("createDate"));
                LocalDate modifyDate = LocalDate.parse(csvRecord.get("modifyDate"));
                LocalDate startTime = LocalDate.parse(csvRecord.get("startTime"));
                String name = csvRecord.get("name");
                Optional<TrainingProgram> existingProgram = Optional.empty();
                List<TrainingProgram> existingProgramList = null;
                boolean bothIdAndNameCheck = false;

                if (scanningMethod.equalsIgnoreCase("id")) {
                    existingProgram = trainingRepository.findById(id);
                } else if (scanningMethod.equalsIgnoreCase("name")) {
                    try {
                        existingProgramList = trainingRepository.findByName(name);
                        System.out.println(existingProgram);
                    } catch (Exception e) {
                        e.printStackTrace(); // Print the exception stack trace for debugging purposes
                    }

                } else if (scanningMethod.equalsIgnoreCase("both")) {
                    existingProgram = trainingRepository.findById(id);
                    existingProgramList = trainingRepository.findByName(name);
                    if (existingProgram.isPresent() || !existingProgramList.isEmpty()) {
                        bothIdAndNameCheck = true;
                    }
                    System.out.println(existingProgram);
                }

                Optional<TrainingProgram> existingProgramById = trainingRepository.findById(id);
                List<TrainingProgram> existingProgramByName = trainingRepository.findByName(name);

                if (existingProgram.isPresent() || existingProgramList != null || bothIdAndNameCheck) {
                    if (duplicateHandling.equalsIgnoreCase("allow")) {
                        int newId = getNextTrainingProgramId();

                        TrainingProgram newProgram = new TrainingProgram(
                                newId,
                                name,
                                csvRecord.get("createBy"),
                                createDate,
                                csvRecord.get("modifyBy"),
                                modifyDate,
                                startTime,
                                csvRecord.get("duration"),
                                csvRecord.get("topicId"),
                                csvRecord.get("status")
                        );
                        trainingRepository.save(newProgram);

                    } else if (duplicateHandling.equalsIgnoreCase("replace")) {
                        //if scanning methode = id or both ==> replace based on id because name is not unique
                        if (scanningMethod.equalsIgnoreCase("id") || scanningMethod.equalsIgnoreCase("both")) {
                            if (existingProgramById.isPresent()) {
                                TrainingProgram newProgram = existingProgramById.get();

                                newProgram.setName(name);
                                newProgram.setCreateBy(csvRecord.get("createBy"));
                                newProgram.setCreateDate(createDate);
                                newProgram.setModifyBy(csvRecord.get("modifyBy"));
                                newProgram.setModifyDate(modifyDate);
                                newProgram.setStartTime(startTime);
                                newProgram.setDuration(csvRecord.get("duration"));
                                newProgram.setTopicId(csvRecord.get("topicId"));
                                newProgram.setStatus(csvRecord.get("status"));

                                trainingRepository.save(newProgram);
                            }
                        } else if (scanningMethod.equalsIgnoreCase("name")) {
                            if (existingProgramByName.size() > 1) {
                                throw new DuplicateRecordException("Rejecting update due to multiple programs with the same name: " + name + ". Total number of record existed in database: " + existingProgramByName.size());
                            } else if (existingProgramByName.size() == 1) {
                                TrainingProgram newProgram = existingProgramByName.get(0);

                                newProgram.setName(name);
                                newProgram.setCreateBy(csvRecord.get("createBy"));
                                newProgram.setCreateDate(createDate);
                                newProgram.setModifyBy(csvRecord.get("modifyBy"));
                                newProgram.setModifyDate(modifyDate);
                                newProgram.setStartTime(startTime);
                                newProgram.setDuration(csvRecord.get("duration"));
                                newProgram.setTopicId(csvRecord.get("topicId"));
                                newProgram.setStatus(csvRecord.get("status"));

                                trainingRepository.save(newProgram);
                            }
                        }

                    } else if (duplicateHandling.equalsIgnoreCase("skip")) {
                        if (scanningMethod.equalsIgnoreCase("both")) {
                            if (bothIdAndNameCheck) {
                                continue;
                            }
                        } else if (scanningMethod.equalsIgnoreCase("id")) {
                            if (existingProgramById.isPresent()) {
                                continue;
                            }
                        } else if (scanningMethod.equalsIgnoreCase("name")) {
                            if (!existingProgramByName.isEmpty()) {
                                continue;
                            }
                        }

                        TrainingProgram trainingProgram = new TrainingProgram(
                                id,
                                name,
                                csvRecord.get("createBy"),
                                createDate,
                                csvRecord.get("modifyBy"),
                                modifyDate,
                                startTime,
                                csvRecord.get("duration"),
                                csvRecord.get("topicId"),
                                csvRecord.get("status")
                        );
                        trainingRepository.save(trainingProgram);
                    }

                } else {
                    TrainingProgram trainingProgram = new TrainingProgram(
                            Integer.parseInt(csvRecord.get(0)),
                            name,
                            csvRecord.get("createBy"),
                            createDate,
                            csvRecord.get("modifyBy"),
                            modifyDate,
                            startTime,
                            csvRecord.get("duration"),
                            csvRecord.get("topicId"),
                            csvRecord.get("status")
                    );
                    trainingRepository.save(trainingProgram);
                }
            }
            return trainingProgramList;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    //tạo mới 1 program
    @Override
    public int createNewTrainingProgram(TrainingProgram trainingProgram) {
        try {
            int id = getNextTrainingProgramId();
            trainingProgram.setTrainingId(id);
            trainingProgram.setStatus("Inactive");
            TrainingProgram savedTrainingProgram = trainingRepository.save(trainingProgram);
            if (savedTrainingProgram != null) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTrainingProgram(Integer id) {
        try {
            trainingRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
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

    //search program bằng id
    @Override
    public TrainingProgram searchTrainingProgram(Integer trainingId) {
        return trainingRepository.findById(trainingId).orElse(null);
    }

    //search 1 program sử dụng keyword
    @Override
    public Page<TrainingProgram> searchTrainingProgramWithKeyword(String name, Pageable pageable) {
        return trainingRepository.findByNameContaining(name, pageable);
    }

    //sort theo field người dùng input
    @Override
    public Page<TrainingProgram> getSortedTrainingProgram(String sortBy, String sortOrder, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return trainingRepository.findAll(pageRequest);
    }



    //x2 1 training program, assign 1 id mới và chuyển status về drafting
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
            duplicateProgram.setStatus("Drafting");

            trainingRepository.save(duplicateProgram);

            return duplicateProgram;
        }
    }

    //lấy id mới nhất để assign (id lớn nhất)
    public int getNextTrainingProgramId() {
        List<TrainingProgram> trainingPrograms = trainingRepository.findAllByOrderByTrainingIdDesc();

        if (!trainingPrograms.isEmpty()) {
            return trainingPrograms.get(0).getTrainingId() + 1;
        } else {
            return 1;
        }
    }

    //deactivate 1 program, status = inactive
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

    //activate 1 program chuyển status = active
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

    //cập nhật thông tin, check status = active ==> tiến hành cập nhật
    public TrainingProgram updateTrainingProgram(Integer trainingId, TrainingProgram updatedProgram) {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);

        if (optionalTrainingProgram.isPresent()) {
            TrainingProgram trainingProgram = optionalTrainingProgram.get();
            if (!trainingProgram.getStatus().equals("Active")) {
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


    //lấy dữ liệu từ service Class bằng RestTemplate (not finalize yet)
    @Override
    public List<ClassDTO> getClassbyTrainingProgramId(Integer id) {
//      ResponseEntity<ResponseMessage> responseEntity = restTemplate.getForEntity("http://localhost:8801/classList", ResponseMessage.class);
        ResponseEntity<ResponseMessage> responseEntity = restTemplate.getForEntity("http://localhost:8801/classList", ResponseMessage.class);

        ResponseMessage responseObject = responseEntity.getBody();

        if (responseObject != null && responseObject.getData() != null) {
            PageableDTO<ClassDTO> pageResponse = objectMapper.convertValue(responseObject.getData(), new TypeReference<PageableDTO<ClassDTO>>() {});
            return pageResponse.getContent().stream()
                    .filter(classDTO -> classDTO.getTrainingProgramId().equals(id))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<SyllabusDTO> getSyllabusByTrainingProgramId(Integer id) {
        try {
            ResponseEntity<List<SyllabusDTO>> responseEntity = restTemplate.exchange(
                    "http://localhost:8802/syllabus/list",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<SyllabusDTO>>() {});

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                List<SyllabusDTO> syllabusDTOList = responseEntity.getBody();

                if (syllabusDTOList != null){
                    return syllabusDTOList.stream()
                            .filter(SyllabusDTO -> SyllabusDTO.getTraining_id().equals(id))
                            .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList(); // Return an empty list if there's an error or no data.
    }







}
