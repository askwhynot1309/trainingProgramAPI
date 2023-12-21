package com.fams.training.controller;

import com.fams.training.DTO.*;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.exception.InvalidFileException;
import com.fams.training.repository.TrainingRepository;
import com.fams.training.service.Imp.TrainingServiceImp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainingController {
    @Autowired
    TrainingServiceImp trainingServiceImp;

    @Autowired
    TrainingRepository trainingRepository;

    private static final Logger logger = LogManager.getLogger(TrainingController.class);

    //lấy danh sách đã phân trang của program list
    @GetMapping("/trainingList")
    public ResponseEntity<ResponseMessage> getTrainingProgramList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String status
    ) {
        Page<TrainingProgramDTO> list = trainingServiceImp.getAllPagingTrainingProgram(page, size, status);

        PageableDTO<TrainingProgramDTO> data = parseToPageableDTO(list);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), data, Message.SUCCESS)
        );
    }

    private PageableDTO<TrainingProgramDTO> parseToPageableDTO(Page<TrainingProgramDTO> list) {
        PageableDTO<TrainingProgramDTO> data = new PageableDTO<>();
        data.setContent(list.getContent());
        data.setPageNumber(list.getNumber());
        data.setPageSize(list.getSize());
        data.setTotalElements(list.getTotalElements());
        data.setTotalPages(list.getTotalPages());
        return data;
    }

    //search program bằng id
    @GetMapping("/searchWithId/{trainingId}")
    public ResponseEntity<ResponseMessage> searchTrainingProgram(@PathVariable Integer trainingId) {
        TrainingProgramDTO trainingProgram = trainingServiceImp.searchTrainingProgram(trainingId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), trainingProgram, Message.SUCCESS)
        );
    }

    //deactivate 1 program, status = inactive
    @PostMapping("/deactivate/{trainingId}")
    public ResponseEntity<ResponseMessage> deactivateTrainingProgram(@PathVariable Integer trainingId) {
        trainingServiceImp.deactivateTrainingProgram(trainingId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), null, Message.DEACTIVATED_SUCCESSFUL)
        );
    }

    //activate 1 program chuyển status = active
    @PostMapping("/activate/{trainingId}")
    public ResponseEntity<ResponseMessage> activateTrainingProgram(@PathVariable Integer trainingId) {
        trainingServiceImp.activateTrainingProgram(trainingId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), null, Message.ACTIVATED_SUCCESSFUL)
        );
    }

    //tạo mới 1 program
    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createNewTrainingProgram(@RequestBody TrainingProgramDTO trainingProgram) {
        trainingServiceImp.createNewTrainingProgram(trainingProgram);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), trainingProgram, Message.CREATE_SUCCESSFUL)
        );
    }

    @PutMapping("/update/{trainingId}")
    public ResponseEntity<ResponseMessage> updateTrainingProgram(@PathVariable Integer trainingId, @RequestBody TrainingProgramDTO updatedProgram) {
        trainingServiceImp.updateTrainingProgram(trainingId, updatedProgram);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), null, Message.UPDATE_SUCCESSFUL)
        );
    }

    @PostMapping("/addOrUpdateSyllabusId/{trainingId}")
    public ResponseEntity<ResponseMessage> addOrUpdateSyllabusId(@PathVariable Integer trainingId, @RequestBody List<SyllabusRequest> syllabusRequest) {
        trainingServiceImp.addOrUpdateSyllabusId(trainingId, syllabusRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), null, Message.UPDATE_SUCCESSFUL)
        );
    }

    //search program list theo keyword
    @PostMapping("/searchByMatchingKeywords/{key}")
    public ResponseEntity<ResponseMessage> searchTrainingProgramByKeyword(
            @PathVariable String key,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Page<TrainingProgramDTO> list = trainingServiceImp.searchTrainingProgramWithKeyword(key, PageRequest.of(page, size));

        PageableDTO<TrainingProgramDTO> data = parseToPageableDTO(list);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), data, Message.SUCCESS)
        );
    }

    //sort theo field người dùng input
    @GetMapping("/trainingList/sort")
    public ResponseEntity<ResponseMessage> getSortedTrainingProgram(
            @RequestParam String sortBy,
            @RequestParam String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Page<TrainingProgram> list = trainingServiceImp.getSortedTrainingProgram(sortBy, sortOrder, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy));

        PageableDTO<TrainingProgram> data = new PageableDTO<>();
        data.setContent(list.getContent());
        data.setPageNumber(list.getNumber());
        data.setPageSize(list.getSize());
        data.setTotalElements(list.getTotalElements());
        data.setTotalPages(list.getTotalPages());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), data, Message.SUCCESS)
        );
    }


    //x2 1 training program, assign 1 id mới và chuyển status về drafting
    @PostMapping("/duplicateProgram/{trainingId}")
    public ResponseEntity<ResponseMessage> duplicateTrainingProgram(@PathVariable Integer trainingId) {
        trainingServiceImp.duplicateTrainingProgram(trainingId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage(HttpStatus.OK.value(), null, Message.SUCCESS)
        );
    }

    @GetMapping("/getSyllabusIdByTrainingId/{trainingId}")
    public ResponseEntity<List<Long>> getSyllabusByTrainingId(@PathVariable Integer trainingId) {
        List<Long> SyllabusIdList;
        SyllabusIdList = trainingServiceImp.getSyllabusIdListByTrainingId(trainingId);
        return ResponseEntity.status(HttpStatus.OK).body(SyllabusIdList);
    }

    //import 1 file csv, có duplicate handling
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "encoding", defaultValue = "UTF-8") String encoding,
            @RequestParam(value = "columnSeparator", defaultValue = ",") char columnSeparator,
            @RequestParam(value = "scanningMethod", defaultValue = "id") String scanningMethod,
            @RequestParam(value = "duplicateHandling", defaultValue = "skip") String duplicateHandling
    ) {
        String message;
        try {
            if (!TrainingServiceImp.hasCSVFormat(file)) {
                throw new InvalidFileException();
            }
            trainingServiceImp.importTrainingProgramFromFile(file, file.getInputStream(), encoding, columnSeparator, scanningMethod, duplicateHandling);
            List<TrainingProgram> data = trainingServiceImp.getAllTrainingProgram();
            message = Message.SUCCESS + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(HttpStatus.OK.value(), data, message));
        } catch (InvalidFileException ex) {
            throw ex;
        } catch (Exception e) {
            message = Message.CANT_UPLOAD + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, message));
        }
    }


}