package com.fams.training.service.Imp;

import com.fams.training.DTO.SyllabusRequest;
import com.fams.training.DTO.TrainingProgramDTO;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.entity.TrainingSyllabus;
import com.fams.training.exception.BadRequestException;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.exception.NotFoundContentException;
import com.fams.training.exception.TestException;
import com.fams.training.repository.TrainingRepository;
import com.fams.training.repository.TrainingSyllabusRepository;
import com.fams.training.service.Interface.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImp implements TrainingService {
    @Autowired
    TrainingRepository trainingRepository;

    @Autowired
    TrainingSyllabusRepository trainingSyllabusRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private PolicyFactory htmlPolicy;

    //get paging training program list
    @Override
    public Page<TrainingProgramDTO> getAllPagingTrainingProgram(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "trainingId"));
        Page<TrainingProgram> trainingProgramPage;
        if (status == null) {
            trainingProgramPage = trainingRepository.findAll(pageable);
        } else {
            trainingProgramPage = trainingRepository.findAllByStatus(status, pageable);
        }
        if (!trainingProgramPage.hasContent()) {
            throw new NotFoundContentException();
        }
        return trainingProgramPage.map(this::mapToDTO);
    }

    public TrainingProgramDTO mapToDTO(TrainingProgram trainingProgram) {
        return TrainingProgramDTO.builder()
                .id(trainingProgram.getTrainingId())
                .name(trainingProgram.getName())
                .createBy(trainingProgram.getCreateBy())
                .createDate(trainingProgram.getCreateDate())
                .modifyBy(trainingProgram.getModifyBy())
                .modifyDate(trainingProgram.getModifyDate())
                .duration(trainingProgram.getDuration())
                .topicId(trainingProgram.getTopicId())
                .status(trainingProgram.getStatus())
                .info(trainingProgram.getInfo())
                .build();
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


    //import 1 file csv with duplicate handling
    @Override
    public List<TrainingProgram> importTrainingProgramFromFile(MultipartFile file, InputStream is, String encoding, char columnSeparator, String scanningMethod, String duplicateHandling) {
        try (
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, encoding));
                CSVParser csvParser = new CSVParser(fileReader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase()
                                .withTrim().withDelimiter(columnSeparator))
        ) {

            List<TrainingProgram> trainingProgramList = new ArrayList<TrainingProgram>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                int id = Integer.parseInt(csvRecord.get(0));
                LocalDateTime createDate = LocalDateTime.now();
                LocalDateTime modifyDate = LocalDateTime.now();
                String name = csvRecord.get("name");
                Optional<TrainingProgram> existingProgram = Optional.empty();

                if (scanningMethod.equalsIgnoreCase("id")) {
                    existingProgram = trainingRepository.findById(id);
                }

//                else if (scanningMethod.equalsIgnoreCase("name")) {
//                    try {
//                        existingProgramList = trainingRepository.findByName(name);
//                        System.out.println(existingProgram);
//                    } catch (Exception e) {
//                        e.printStackTrace(); // Print the exception stack trace for debugging purposes
//                    }
//
//                } else if (scanningMethod.equalsIgnoreCase("both")) {
//                    existingProgram = trainingRepository.findById(id);
//                    existingProgramList = trainingRepository.findByName(name);
//                    if (existingProgram.isPresent() || !existingProgramList.isEmpty()) {
//                        bothIdAndNameCheck = true;
//                    }
//                    System.out.println(existingProgram);
//                }

                Optional<TrainingProgram> existingProgramById = trainingRepository.findById(id);

                if (existingProgram.isPresent()) {
                    if (duplicateHandling.equalsIgnoreCase("allow")) {
                        int newId = getNextTrainingProgramId();

                        TrainingProgram newProgram = new TrainingProgram(
                                newId,
                                name,
                                csvRecord.get("createBy"),
                                createDate,
                                csvRecord.get("modifyBy"),
                                modifyDate,
                                csvRecord.get("duration"),
                                csvRecord.get("topicId"),
                                csvRecord.get("status"),
                                csvRecord.get("info")
                        );
                        trainingRepository.save(newProgram);

                    } else if (duplicateHandling.equalsIgnoreCase("replace")) {
                        //if scanning methode = id or both ==> replace based on id because name is not unique
//                        if (scanningMethod.equalsIgnoreCase("id")) {
                        if (existingProgramById.isPresent()) {
                            TrainingProgram newProgram = existingProgramById.get();

                            getImportData(csvRecord, createDate, modifyDate, name, newProgram);
                        }
//                        }
//                        else if (scanningMethod.equalsIgnoreCase("name")) {
//                            if (existingProgramByName.size() > 1) {
//                                throw new DuplicateRecordException("Rejecting update due to multiple programs with the same name: " + name + ". Total number of record existed in database: " + existingProgramByName.size());
//                            } else if (existingProgramByName.size() == 1) {
//                                TrainingProgram newProgram = existingProgramByName.get(0);
//
//                                getImportData(csvRecord, createDate, modifyDate, name, newProgram);
//                            }
//                        }
                    } else if (duplicateHandling.equalsIgnoreCase("skip")) {
                        if (scanningMethod.equalsIgnoreCase("id")) {
                            if (existingProgramById.isPresent()) {
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
                                csvRecord.get("duration"),
                                csvRecord.get("topicId"),
                                csvRecord.get("status"),
                                csvRecord.get("info")
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
                            csvRecord.get("duration"),
                            csvRecord.get("topicId"),
                            csvRecord.get("status"),
                            csvRecord.get("info")
                    );
                    trainingRepository.save(trainingProgram);
                }
            }
            return trainingProgramList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    private void getImportData(CSVRecord csvRecord, LocalDateTime createDate, LocalDateTime modifyDate, String name, TrainingProgram newProgram) {
        newProgram.setName(name);
        newProgram.setCreateBy(csvRecord.get("createBy"));
        newProgram.setCreateDate(createDate);
        newProgram.setModifyBy(csvRecord.get("modifyBy"));
        newProgram.setModifyDate(modifyDate);
        newProgram.setDuration(csvRecord.get("duration"));
        newProgram.setTopicId(csvRecord.get("topicId"));
        newProgram.setStatus(csvRecord.get("status"));
        newProgram.setInfo(csvRecord.get("info"));

        trainingRepository.save(newProgram);
    }

    //create a new program
    @Override
    @Transactional(rollbackOn = Exception.class)
    public int createNewTrainingProgram(TrainingProgramDTO trainingProgramRequestBody) {
        try {
            trainingProgramRequestBody.setId(getNextTrainingProgramId());
            trainingProgramRequestBody.setModifyDate(LocalDateTime.now());
            trainingProgramRequestBody.setCreateDate(LocalDateTime.now());
            trainingProgramRequestBody.setStatus("Inactive");

            sanitizeTrainingProgramDTO(trainingProgramRequestBody);

            TrainingProgram trainingProgram = mapToEntity(trainingProgramRequestBody);
            trainingRepository.save(trainingProgram);
            if (!trainingProgramRequestBody.getSyllabusRequestList().isEmpty()) {
                Set<SyllabusRequest> uniqueSyllabusRequests = new HashSet<>(trainingProgramRequestBody.getSyllabusRequestList()); //elminated duplicate
                saveToTrainingSyllabus(trainingProgram, uniqueSyllabusRequests);
            }
            return 1;
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }

    public void saveToTrainingSyllabus(TrainingProgram trainingProgram, Set<SyllabusRequest> uniqueSyllabusRequests) {
        for (SyllabusRequest syllabusRequest : uniqueSyllabusRequests) {
            boolean check = isSyllabusIdExist(syllabusRequest.getSyllabusId());
            if (check) {
                TrainingSyllabus trainingSyllabus = TrainingSyllabus.builder()
                        .orderNumber(syllabusRequest.getOrder())
                        .syllabusId(syllabusRequest.getSyllabusId())
                        .trainingProgram(trainingProgram)
                        .build();

                trainingSyllabusRepository.save(trainingSyllabus);
            } else {
                throw new EntityNotFoundException();
            }
        }
    }

    //update training program and syllabus information
    @Override
    @Transactional(rollbackOn = Exception.class)
    public TrainingProgram updateTrainingProgram(Integer trainingId, TrainingProgramDTO updatedProgram) {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);

        if (optionalTrainingProgram.isPresent()) {
            try {
                TrainingProgram trainingProgram = optionalTrainingProgram.get();
                if (!trainingProgram.getStatus().equals("Active")) {
                    throw new IllegalStateException("Training program must be active to be updated");
                }
                LocalDateTime currentDate = LocalDateTime.now();
                if (updatedProgram.getName() != null) {
                    trainingProgram.setName(updatedProgram.getName());

                }
                if (updatedProgram.getDuration() != null) {
                    trainingProgram.setDuration(updatedProgram.getDuration());
                }
                if (updatedProgram.getModifyBy() != null) {
                    trainingProgram.setModifyBy(updatedProgram.getModifyBy());
                }
                if (updatedProgram.getTopicId() != null) {
                    trainingProgram.setTopicId(updatedProgram.getTopicId());
                }
                if (updatedProgram.getInfo() != null) {
                    trainingProgram.setInfo(updatedProgram.getInfo());
                }
                trainingProgram.setModifyDate(currentDate);

                sanitizeTrainingProgram(trainingProgram);

                if (updatedProgram.getSyllabusRequestList() != null) {
                    if (!updatedProgram.getSyllabusRequestList().isEmpty()) {
                        trainingSyllabusRepository.deleteByTrainingProgram(trainingProgram);
                        Set<SyllabusRequest> uniqueSyllabusRequests = new HashSet<>(updatedProgram.getSyllabusRequestList());
                        saveToTrainingSyllabus(trainingProgram, uniqueSyllabusRequests);
                    }

                }

                return trainingRepository.save(trainingProgram);
            } catch (BadRequestException e) {
                throw new BadRequestException();
            }
        } else {
            throw new EntityNotFoundException();
        }
    }

    private TrainingProgram mapToEntity(TrainingProgramDTO trainingProgramDto) {
        return TrainingProgram.builder()
                .trainingId(trainingProgramDto.getId())
                .name(trainingProgramDto.getName())
                .createBy(trainingProgramDto.getCreateBy())
                .createDate(trainingProgramDto.getCreateDate())
                .modifyBy(trainingProgramDto.getModifyBy())
                .modifyDate(trainingProgramDto.getModifyDate())
                .duration(trainingProgramDto.getDuration())
                .topicId(trainingProgramDto.getTopicId())
                .status(trainingProgramDto.getStatus())
                .info(trainingProgramDto.getInfo())
                .build();
    }

    @Override
    public int deleteTrainingProgram(Integer id) {
        try {
            trainingRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {

        return TYPE.equals(file.getContentType());
    }

    public boolean existsTrainingProgramById(Integer id) {
        return trainingRepository.existsById(id);
    }

    //search program with training program id
    @Override
    public TrainingProgramDTO searchTrainingProgram(Integer trainingId) throws TestException {
        TrainingProgram trainingProgram = trainingRepository.findById(trainingId).orElse(null);

        if (trainingProgram != null) {
            return mapToDTO(trainingProgram);
        } else {
            throw new NotFoundContentException();
        }
    }

    //search training program with name as keyword
    @Override
    public Page<TrainingProgramDTO> searchTrainingProgramWithKeyword(String name, Pageable pageable) {
        Page<TrainingProgram> trainingProgramPage = trainingRepository.findByNameContaining(name, pageable);
        if (!trainingProgramPage.hasContent()) {
            throw new NotFoundContentException();
        }
        return trainingProgramPage.map(this::mapToDTO);
    }

    //sort by field the user input
    @Override
    public Page<TrainingProgram> getSortedTrainingProgram(String sortBy, String sortOrder, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<TrainingProgram> trainingProgramPage = trainingRepository.findAll(pageRequest);
        if (trainingProgramPage.isEmpty()) {
            throw new NotFoundContentException();
        }
        return trainingProgramPage;
    }


    //x2 training program
    @Override
    public TrainingProgram duplicateTrainingProgram(Integer id) {
        TrainingProgram program = trainingRepository.findById(id).orElse(null);
        TrainingProgram duplicateProgram = new TrainingProgram();
        if (program == null) {
            throw new NotFoundContentException();
        } else {

            int newestId = getNextTrainingProgramId();
            duplicateProgram.setTrainingId(newestId);
            duplicateProgram.setName(program.getName());
            duplicateProgram.setCreateBy(program.getCreateBy());
            duplicateProgram.setCreateDate(program.getCreateDate());
            duplicateProgram.setModifyBy(program.getModifyBy());
            duplicateProgram.setModifyDate(program.getModifyDate());
            duplicateProgram.setTopicId(program.getTopicId());
            duplicateProgram.setDuration(program.getDuration());
            duplicateProgram.setInfo(program.getInfo());
            duplicateProgram.setStatus("Drafting");

            trainingRepository.save(duplicateProgram);

            return duplicateProgram;
        }
    }

    //get latest id to assign
    public int getNextTrainingProgramId() {
        List<TrainingProgram> trainingPrograms = trainingRepository.findAllByOrderByTrainingIdDesc();

        if (!trainingPrograms.isEmpty()) {
            return trainingPrograms.get(0).getTrainingId() + 1;
        } else {
            return 1;
        }
    }

    //deactivate training program, status => inactive
    @Override
    public void deactivateTrainingProgram(Integer trainingId) throws EntityNotFoundException {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);

        if (optionalTrainingProgram.isPresent()) {
            TrainingProgram trainingProgram = optionalTrainingProgram.get();
            trainingProgram.setStatus("Inactive");
            trainingRepository.save(trainingProgram);
        } else {
            throw new NotFoundContentException();
        }
    }

    //activate training program make status => active
    @Override
    public void activateTrainingProgram(Integer trainingId) throws EntityNotFoundException {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);

        if (optionalTrainingProgram.isPresent()) {
            TrainingProgram trainingProgram = optionalTrainingProgram.get();
            trainingProgram.setStatus("Active");
            trainingRepository.save(trainingProgram);
        } else {
            throw new NotFoundContentException();
        }
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public boolean addOrUpdateSyllabusId(Integer trainingId, List<SyllabusRequest> syllabusRequests) {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);
        if (optionalTrainingProgram.isPresent()) {
            TrainingProgram trainingProgram = optionalTrainingProgram.get();

            trainingSyllabusRepository.deleteByTrainingProgram(trainingProgram);
            System.out.println("QUAY VIDEO");
            List<SyllabusRequest> uniqueSyllabusRequests = removeDuplicates(syllabusRequests);

            for (SyllabusRequest syllabusRequest : uniqueSyllabusRequests) {
                boolean check = isSyllabusIdExist(syllabusRequest.getSyllabusId());
                if (check) {
                    TrainingSyllabus trainingSyllabus = TrainingSyllabus.builder()
                            .orderNumber(syllabusRequest.getOrder())
                            .syllabusId(syllabusRequest.getSyllabusId())
                            .trainingProgram(trainingProgram)
                            .build();

                    trainingSyllabusRepository.save(trainingSyllabus);
                } else {
                    throw new EntityNotFoundException();
                }
            }
            return true;
        } else {
            throw new EntityNotFoundException();
        }
    }


//    @Override
//    public List<ClassDTO> getClassbyTrainingProgramId(Integer id) {
//        ResponseEntity<ResponseMessage> responseEntity = restTemplate.getForEntity("http://localhost:8801/classList", ResponseMessage.class);
//
//        ResponseMessage responseObject = responseEntity.getBody();
//
//        if (responseObject != null && responseObject.getData() != null) {
//            PageableDTO<ClassDTO> pageResponse = objectMapper.convertValue(responseObject.getData(), new TypeReference<PageableDTO<ClassDTO>>() {});
//            return pageResponse.getContent().stream()
//                    .filter(classDTO -> classDTO.getTrainingProgramId().equals(id))
//                    .collect(Collectors.toList());
//        } else {
//            return new ArrayList<>();
//        }
//    }

//    @Override
//    public List<SyllabusDTO> getSyllabusByTrainingProgramId(Integer id) {
//        try {
//            ResponseEntity<List<SyllabusDTO>> responseEntity = restTemplate.exchange(
//                    "http://localhost:8802/syllabus/list",
//                    HttpMethod.GET,
//                    null,
//                    new ParameterizedTypeReference<List<SyllabusDTO>>() {});
//
//            if (responseEntity.getStatusCode() == HttpStatus.OK) {
//                List<SyllabusDTO> syllabusDTOList = responseEntity.getBody();
//
//                if (syllabusDTOList != null){
//                    return syllabusDTOList.stream()
//                            .filter(SyllabusDTO -> SyllabusDTO.getTraining_id().equals(id))
//                            .collect(Collectors.toList());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return Collections.emptyList(); // Return an empty list if there's an error or no data.
//    }

    public List<Long> getSyllabusIdListByTrainingId(Integer trainingId) {
        Optional<TrainingProgram> optionalTrainingProgram = trainingRepository.findById(trainingId);

        if (optionalTrainingProgram.isPresent()) {
            TrainingProgram trainingProgram = optionalTrainingProgram.get();

            List<TrainingSyllabus> trainingSyllabusList = trainingSyllabusRepository.findByTrainingProgram(trainingProgram);

            List<Long> syllabusIdList = new ArrayList<>();
            for (TrainingSyllabus trainingSyllabus : trainingSyllabusList) {
                syllabusIdList.add(trainingSyllabus.getSyllabusId());
            }

            return syllabusIdList;
        } else {
            throw new EntityNotFoundException();
        }
    }


    public boolean isSyllabusIdExist(Long syllabusId) {
        String url = "http://syllabus-service-env.eba-mhspgj5g.ap-northeast-1.elasticbeanstalk.com/syllabus-service/syllabus/find-by-id/" + syllabusId;
        try {
            ResponseEntity<Object> responseEntity = restTemplate.getForEntity(url, Object.class);
            HttpStatus statusCode = responseEntity.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                return true;
            } else if (statusCode == HttpStatus.NOT_FOUND) {
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false; // If an exception is thrown, the syllabus with the ID does not exist
        }
    }


    public static List<SyllabusRequest> removeDuplicates(List<SyllabusRequest> syllabusRequests) {
        Set<Long> seen = new HashSet<>();
        return syllabusRequests.stream()
                .filter(s -> seen.add(s.getSyllabusId()))
                .collect(Collectors.toList());
    }

    public void sanitizeTrainingProgramDTO(TrainingProgramDTO trainingProgramDTO) {
        trainingProgramDTO.setName(htmlPolicy.sanitize(trainingProgramDTO.getName()));
        trainingProgramDTO.setCreateBy(htmlPolicy.sanitize(trainingProgramDTO.getCreateBy()));
        trainingProgramDTO.setDuration(htmlPolicy.sanitize(trainingProgramDTO.getDuration()));
        trainingProgramDTO.setInfo(htmlPolicy.sanitize(trainingProgramDTO.getInfo()));
        trainingProgramDTO.setModifyBy(htmlPolicy.sanitize(trainingProgramDTO.getModifyBy()));
        trainingProgramDTO.setTopicId(htmlPolicy.sanitize(trainingProgramDTO.getTopicId()));
    }

    public void sanitizeTrainingProgram(TrainingProgram trainingProgram) {
        trainingProgram.setName(htmlPolicy.sanitize(trainingProgram.getName()));
        trainingProgram.setDuration(htmlPolicy.sanitize(trainingProgram.getDuration()));
        trainingProgram.setInfo(htmlPolicy.sanitize(trainingProgram.getInfo()));
        trainingProgram.setModifyBy(htmlPolicy.sanitize(trainingProgram.getModifyBy()));
        trainingProgram.setTopicId(htmlPolicy.sanitize(trainingProgram.getTopicId()));
    }

}
