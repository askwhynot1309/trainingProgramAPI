package com.fams.training.controller;

import com.fams.training.DTO.ClassDTO;
import com.fams.training.DTO.PageableDTO;
import com.fams.training.DTO.ResponseMessage;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.exception.DuplicateRecordException;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.service.Imp.TrainingServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("/trainingList")
    public ResponseEntity<ResponseMessage> getTrainingProgramList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<TrainingProgram> list = trainingServiceImp.getAllPagingTrainingProgram(page, size);

            PageableDTO<TrainingProgram> data = new PageableDTO<>();
            data.setContent(list.getContent());
            data.setPageNumber(list.getNumber());
            data.setPageSize(list.getSize());
            data.setTotalElements(list.getTotalElements());
            data.setTotalPages(list.getTotalPages());
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        new ResponseMessage("1", null, "No content found")
                );
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", data, "Success")
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
                        new ResponseMessage("0", trainingProgram, "Success")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage("1", null, "Training program not found")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("1", null, "Internal server error")
            );
        }
    }

    @PostMapping("/deactivate/{trainingId}")
    public ResponseEntity<ResponseMessage> deactivateTrainingProgram(@PathVariable Integer trainingId) {
        try {
            trainingServiceImp.deactivateTrainingProgram(trainingId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", null, "Training program deactivated successfully")
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("1", null, "Training program not found")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("1", null, "Internal server error")
            );
        }
    }

    @PostMapping("/activate/{trainingId}")
    public ResponseEntity<ResponseMessage> activateTrainingProgram(@PathVariable Integer trainingId) {
        try {
            trainingServiceImp.activateTrainingProgram(trainingId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", null, "Training program activated successfully")
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("1", null, "Training program not found")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("1", null, "Internal server error")
            );
        }
    }



    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("encoding") String encoding,
            @RequestParam("columnSeparator") char columnSeparator,
            @RequestParam("scanningMethod") String scanningMethod,
            @RequestParam("duplicateHandling") String duplicateHandling
    ) {
        String message = "";
        String code;
        try {
            if (TrainingServiceImp.hasCSVFormat(file)) {
                trainingServiceImp.importTrainingProgramFromFile(file, file.getInputStream(), encoding, columnSeparator, scanningMethod, duplicateHandling);
                code = "0";
                List<TrainingProgram> data = trainingServiceImp.getAllTrainingProgram();
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(code, data, message));
            } else {
                code = "1";
                message = "Invalid file format: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(code, null, message));
            }
        } catch (DuplicateRecordException e) {
            code = "1";
            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(code, null, message));
        } catch (Exception e) {
            code = "1";
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(code, null, message));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createNewTrainingProgram(@RequestBody TrainingProgram trainingProgram){
        int id = trainingProgram.getTrainingId();

        if (trainingServiceImp.existsTrainingProgramById(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseMessage("1", null, "Duplicate ID")
            );
        }
        int check = trainingServiceImp.createNewTrainingProgram(trainingProgram);
        if (check > 0){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", trainingProgram, "Success")
            );
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseMessage("1", null, "Failed")
        );
    }

    @PutMapping("/update/{trainingId}")
    public ResponseEntity<ResponseMessage> updateTrainingProgram(@PathVariable Integer trainingId, @RequestBody TrainingProgram updatedProgram) {
        try {
            TrainingProgram data = trainingServiceImp.updateTrainingProgram(trainingId, updatedProgram);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", data, "Training program updated successfully")
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("1", null, "Training program not found. Id not found")
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseMessage("1", null, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("1", null, "Internal server error")
            );
        }
    }

//     @DeleteMapping("/delete/{id}")
//    public ResponseEntity<ResponseMessage> deleteTrainingProgramById(@PathVariable("id") Integer id) {
//        int check = trainingServiceImp.deleteTrainingProgram(id);
//        if(check > 0){
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseMessage("200", null, "Delete program ID: "+ id + " success")
//            );
//        }
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                 new ResponseMessage("500", null, "Delete program ID: "+ id + " fail")
//         );
//     }

    @PostMapping("/searchByMatchingKeywords/{key}")
    public ResponseEntity<ResponseMessage> searchTrainingProgramByKeyword(
            @PathVariable String key,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        try{
            Page<TrainingProgram> list = trainingServiceImp.searchTrainingProgramWithKeyword(key, PageRequest.of(page, size));

            PageableDTO<TrainingProgram> data = new PageableDTO<>();
            data.setContent(list.getContent());
            data.setPageNumber(list.getNumber());
            data.setPageSize(list.getSize());
            data.setTotalElements(list.getTotalElements());
            data.setTotalPages(list.getTotalPages());
            if (list.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage("1", null, "Record not found")
                );
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage("0", data, "Success")
                );
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("1", null, "Internal server error")
            );
        }
    }

    @GetMapping("/trainingList/filterByStatus")
    public ResponseEntity<ResponseMessage> getFilterByStatusTrainingProgram(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        try {
            Page<TrainingProgram> list = trainingServiceImp.filterByStatus(status, PageRequest.of(page, size));

            PageableDTO<TrainingProgram> data = new PageableDTO<>();
            data.setContent(list.getContent());
            data.setPageNumber(list.getNumber());
            data.setPageSize(list.getSize());
            data.setTotalElements(list.getTotalElements());
            data.setTotalPages(list.getTotalPages());

            if (list.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage("1", null, "Record not found")
                );
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage("0", data, "Success")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage("1", null, "Internal server error")
            );
        }
    }

    @PostMapping("/duplicateProgram/{trainingId}")
    public ResponseEntity<ResponseMessage> duplicateTrainingProgram(@PathVariable Integer trainingId){
        TrainingProgram data = trainingServiceImp.duplicateTrainingProgram(trainingId);
        if (data == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("1", null, "Program not found to duplicate")
            );
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", null, "Duplicate successfully")
            );
        }
    }

    @GetMapping("/classes/{id}")
    public ResponseEntity<ResponseMessage> getClassesByProgramId(@PathVariable Integer id) {
        return ResponseEntity.ok(new ResponseMessage("0", trainingServiceImp.getClassbyTrainingProgramId(id), "Get classes with training program Id success"));
    }
}