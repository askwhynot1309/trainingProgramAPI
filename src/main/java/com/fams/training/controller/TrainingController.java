package com.fams.training.controller;

import com.fams.training.DTO.PageableDTO;
import com.fams.training.DTO.ResponseMessage;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.exception.DuplicateRecordException;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.service.serviceImp.TrainingServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TrainingController {
    @Autowired
    TrainingServiceImp trainingServiceImp;

    @GetMapping("/trainingList")
    public ResponseEntity<ResponseMessage> getTrainingProgramList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<TrainingProgram> data = trainingServiceImp.getAllPagingTrainingProgram(page, size);

            PageableDTO<TrainingProgram> pageResponse = new PageableDTO<>();
            pageResponse.setContent(data.getContent());
            pageResponse.setPageNumber(data.getNumber());
            pageResponse.setPageSize(data.getSize());
            pageResponse.setTotalElements(data.getTotalElements());
            pageResponse.setTotalPages(data.getTotalPages());
            if (data.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        new ResponseMessage("204", null, "No content found")
                );
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("200", pageResponse, "Success")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("Internal server error", null, "500")
            );
        }
    }



    @GetMapping("/searchWithId/{trainingId}")
    public ResponseEntity<ResponseMessage> searchTrainingProgram(@PathVariable Integer trainingId) {
        try {
            TrainingProgram trainingProgram = trainingServiceImp.searchTrainingProgram(trainingId);

            if (trainingProgram != null) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage("200", trainingProgram, "Success")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage("404", null, "Training program not found")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("500", null, "Internal server error")
            );
        }
    }

//    @GetMapping("/tpwithclass/{trainingId}")
//    public ResponseEntity<ResponseMessage> getTrainingProgramWithClasses(@PathVariable Integer trainingId) {
//        try {
//            Optional<TrainingProgram> trainingProgram = trainingServiceImp.findTrainingProgramWithClasses(trainingId);
//
//            if (trainingProgram.isPresent()) {
//                return ResponseEntity.status(HttpStatus.OK).body(
//                        new ResponseMessage("200", trainingProgram.get(), "Success")
//                );
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                        new ResponseMessage("404", null, "Training program not found")
//                );
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    new ResponseMessage("500", null, "Internal server error")
//            );
//        }
//    }

    @PostMapping("/deactivate/{trainingId}")
    public ResponseEntity<ResponseMessage> deactivateTrainingProgram(@PathVariable Integer trainingId) {
        try {
            trainingServiceImp.deactivateTrainingProgram(trainingId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("200", null, "Training program deactivated successfully")
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("404", null, "Training program not found")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("500", null, "Internal server error")
            );
        }
    }

    @PostMapping("/activate/{trainingId}")
    public ResponseEntity<ResponseMessage> activateTrainingProgram(@PathVariable Integer trainingId) {
        try {
            trainingServiceImp.activateTrainingProgram(trainingId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("200", null, "Training program activated successfully")
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("404", null, "Training program not found")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("500", null, "Internal server error")
            );
        }
    }



    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        String code;
        try {
            if (TrainingServiceImp.hasCSVFormat(file)) {
                trainingServiceImp.importFile(file);
                code = "200";
                List<TrainingProgram> data = trainingServiceImp.getAllTrainingProgram();
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(code, data, message));
            } else {
                code = "400";
                message = "Invalid file format: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(code, null, message));
            }
        } catch (DuplicateRecordException e) {
            code = "409";
            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(code, null, message));
        } catch (Exception e) {
            code = "409";
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(code, null, message));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createNewTrainingProgram(@RequestBody TrainingProgram trainingProgram){
        int id = trainingProgram.getTrainingId();

        if (trainingServiceImp.existsTrainingProgramById(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseMessage("409", null, "Duplicate ID")
            );
        }
        int check = trainingServiceImp.createNewTrainingProgram(trainingProgram);
        if (check > 0){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("200", trainingProgram, "Success")
            );
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseMessage("500", null, "Failed")
        );
    }

    @PutMapping("/update/{trainingId}")
    public ResponseEntity<ResponseMessage> updateTrainingProgram(@PathVariable Integer trainingId, @RequestBody TrainingProgram updatedProgram) {
        try {
            TrainingProgram updatedTrainingProgram = trainingServiceImp.updateTrainingProgram(trainingId, updatedProgram);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("200", updatedTrainingProgram, "Training program updated successfully")
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("404", null, "Training program not found")
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseMessage("400", null, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("500", null, "Internal server error")
            );
        }
    }

     @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteTrainingProgramById(@PathVariable("id") Integer id) {
        int check = trainingServiceImp.deleteTrainingProgram(id);
        if(check > 0){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("200", null, "Delete program ID: "+ id + " success")
            );
        }
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                 new ResponseMessage("500", null, "Delete program ID: "+ id + " fail")
         );
     }

//    @GetMapping("/getSyllabusWithId/{trainingId}")
//    public ResponseEntity<ResponseMessage> getSyllabusListForTrainingProgram(@PathVariable Integer trainingId) {
//        try {
//            List<Syllabus> syllabusList = trainingServiceImp.getSyllabusListForTrainingProgram(trainingId);
//
//            if (syllabusList.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                        new ResponseMessage("404", null, "No syllabus found for the given trainingId")
//                );
//            }
//
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseMessage("200", syllabusList, "Success")
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    new ResponseMessage("500", null, "Internal server error")
//            );
//        }
//    }

    @PostMapping("/searchByMatchingKeywords/{key}")
    public ResponseEntity<ResponseMessage> searchTrainingProgramByKeyword(
            @PathVariable String key,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        try{
            Page<TrainingProgram> data = trainingServiceImp.searchTrainingProgramWithKeyword(key, PageRequest.of(page, size));

            PageableDTO<TrainingProgram> pageResponse = new PageableDTO<>();
            pageResponse.setContent(data.getContent());
            pageResponse.setPageNumber(data.getNumber());
            pageResponse.setPageSize(data.getSize());
            pageResponse.setTotalElements(data.getTotalElements());
            pageResponse.setTotalPages(data.getTotalPages());
            if (data.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage("404", null, "Record not found")
                );
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage("200", pageResponse, "Success")
                );
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("500", null, "Internal server error")
            );
        }
    }
}