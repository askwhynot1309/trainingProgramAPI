package com.fams.training.controller;

import com.fams.training.entity.ResponseMessage;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.exception.DuplicateRecordException;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.repository.TrainingRepository;
import com.fams.training.service.serviceImp.TrainingServiceImp;
import com.fams.training.service.serviceInterface.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ResponseMessage> getTrainingProgramList() {
        List<TrainingProgram> programList;
        try {
            programList = trainingServiceImp.getAllTrainingProgram();
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("200", programList, "Success")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("Internal server error", null, "500")
            );
        }
    }

    @GetMapping("/search/{trainingId}")
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

    @GetMapping("/tpwithclass/{trainingId}")
    public ResponseEntity<ResponseMessage> getTrainingProgramWithClasses(@PathVariable Integer trainingId) {
        try {
            Optional<TrainingProgram> trainingProgram = trainingServiceImp.findTrainingProgramWithClasses(trainingId);

            if (trainingProgram.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage("200", trainingProgram.get(), "Success")
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

//    @PutMapping("/update/{id}")
//    public ResponseEntity<ResponseMessage> updateTrainingProgram(@PathVariable("id") Integer id ,@RequestBody TrainingProgram trainingProgram){
//        TrainingProgram trainingProgram1 = new TrainingProgram()
//    }

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
}