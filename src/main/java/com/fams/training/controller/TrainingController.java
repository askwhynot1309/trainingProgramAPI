package com.fams.training.controller;

import com.fams.training.DTO.*;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.service.Imp.TrainingServiceImp;
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

    //lấy danh sách đã phân trang của program list
    @GetMapping("/trainingList")
    public ResponseEntity<ResponseMessage> getTrainingProgramList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        try {
            Page<TrainingProgramDTO> list = trainingServiceImp.getAllPagingTrainingProgram(page, size);

            PageableDTO<TrainingProgramDTO> data = getData(list);
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        new ResponseMessage(HttpStatus.NO_CONTENT.value(), null, Message.NO_CONTENT)
                );
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage(HttpStatus.OK.value(), data, Message.SUCCESS)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
            );
        }
    }

    private PageableDTO<TrainingProgramDTO> getData(Page<TrainingProgramDTO> list) {
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
        try {
            TrainingProgramDTO trainingProgram = trainingServiceImp.searchTrainingProgram(trainingId);

            if (trainingProgram != null) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage(HttpStatus.OK.value(), trainingProgram, Message.SUCCESS)
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
            );
        }
    }

    @GetMapping("/searchByIdForRestTemplate/{trainingId}")
    public ResponseEntity<TrainingProgramDTO> searchTrainingProgramByIdForRestTemplate(@PathVariable Integer trainingId) {
        try {
            TrainingProgramDTO trainingProgram = trainingServiceImp.searchTrainingProgram(trainingId);

            if (trainingProgram != null) {
                return ResponseEntity.status(HttpStatus.OK).body(trainingProgram);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @GetMapping("/getProgramDetail/{trainingId}")
//    public ResponseEntity<TrainingProgramDetailDTO> getTrainingProgramDetail(@PathVariable Integer trainingId){
//        try {
//            List<ClassDTO> classList = trainingServiceImp.getClassbyTrainingProgramId(trainingId);
//            List<SyllabusDTO> syllabusList = trainingServiceImp.getSyllabusByTrainingProgramId(trainingId);
//            TrainingProgramDTO trainingProgram = trainingServiceImp.searchTrainingProgram(trainingId);
//
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new TrainingProgramDetailDTO(0, trainingProgram, syllabusList, classList, "Success")
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    new TrainingProgramDetailDTO(1, null, null, null, "Internal server error")
//            );
//        }
//    }

    //deactivate 1 program, status = inactive
    @PostMapping("/deactivate/{trainingId}")
    public ResponseEntity<ResponseMessage> deactivateTrainingProgram(@PathVariable Integer trainingId) {
        try {
            trainingServiceImp.deactivateTrainingProgram(trainingId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage(HttpStatus.OK.value(), null, Message.DEACTIVATED_SUCCESSFUL)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
            );
        }
    }

    //activate 1 program chuyển status = active
    @PostMapping("/activate/{trainingId}")
    public ResponseEntity<ResponseMessage> activateTrainingProgram(@PathVariable Integer trainingId) {
        try {
            trainingServiceImp.activateTrainingProgram(trainingId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage(HttpStatus.OK.value(), null, Message.ACTIVATED_SUCCESSFUL)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
            );
        }
    }

    //import 1 file csv, có duplicate handling
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<ResponseMessage> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("encoding") String encoding,
            @RequestParam("columnSeparator") char columnSeparator,
            @RequestParam("scanningMethod") String scanningMethod,
            @RequestParam("duplicateHandling") String duplicateHandling
    ) {
        String message;
        try {
            if (TrainingServiceImp.hasCSVFormat(file)) {
                trainingServiceImp.importTrainingProgramFromFile(file, file.getInputStream(), encoding, columnSeparator, scanningMethod, duplicateHandling);
                List<TrainingProgram> data = trainingServiceImp.getAllTrainingProgram();
                message = Message.SUCCESS + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(HttpStatus.OK.value(), data, message));
            } else {
                message = Message.INVALID_FILE + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(HttpStatus.BAD_REQUEST.value(), null, message));
            }
        } catch (Exception e) {
            message = Message.CANT_UPLOAD + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, message));
        }
    }

    //tạo mới 1 program
    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createNewTrainingProgram(@RequestBody TrainingProgramDTO trainingProgram){
        try {
            if (trainingProgram == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
                );

            int check = trainingServiceImp.createNewTrainingProgram(trainingProgram);
            if (check > 0){
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage(HttpStatus.OK.value(), trainingProgram, Message.CREATE_SUCCESSFUL)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
        );
    }

    @PutMapping("/update/{trainingId}")
    public ResponseEntity<ResponseMessage> updateTrainingProgram(@PathVariable Integer trainingId, @RequestBody TrainingProgramDTO updatedProgram) {
        try {
            TrainingProgram data = trainingServiceImp.updateTrainingProgram(trainingId, updatedProgram);
            if (data != null){
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage(HttpStatus.OK.value(), null, Message.UPDATE_SUCCESSFUL)
                );
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseMessage(HttpStatus.BAD_REQUEST.value(), null, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
            );
        }
        return null;
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

    //search program list theo keyword
    @PostMapping("/searchByMatchingKeywords/{key}")
    public ResponseEntity<ResponseMessage> searchTrainingProgramByKeyword(
            @PathVariable String key,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ){
        try{
            Page<TrainingProgramDTO> list = trainingServiceImp.searchTrainingProgramWithKeyword(key, PageRequest.of(page, size));

            PageableDTO<TrainingProgramDTO> data = getData(list);
            if (list.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
                );
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage(HttpStatus.OK.value(), data, Message.SUCCESS)
                );
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
            );
        }
    }

    //sort theo field người dùng input
    @GetMapping("/trainingList/sort")
    public ResponseEntity<ResponseMessage> getSortedTrainingProgram(
            @RequestParam String sortBy,
            @RequestParam String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        try {
            Page<TrainingProgram> list = trainingServiceImp.getSortedTrainingProgram(sortBy, sortOrder, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy));

            PageableDTO<TrainingProgram> data = new PageableDTO<>();
            data.setContent(list.getContent());
            data.setPageNumber(list.getNumber());
            data.setPageSize(list.getSize());
            data.setTotalElements(list.getTotalElements());
            data.setTotalPages(list.getTotalPages());

            if (list.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
                );
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseMessage(HttpStatus.OK.value(), data, Message.SUCCESS)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, Message.INTERNAL_SERVER_ERROR)
            );
        }
    }


    //x2 1 training program, assign 1 id mới và chuyển status về drafting
    @PostMapping("/duplicateProgram/{trainingId}")
    public ResponseEntity<ResponseMessage> duplicateTrainingProgram(@PathVariable Integer trainingId){
        TrainingProgram data = trainingServiceImp.duplicateTrainingProgram(trainingId);
        if (data == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage(HttpStatus.NOT_FOUND.value(), null, Message.NOT_FOUND)
            );
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage(HttpStatus.OK.value(), null, Message.SUCCESS)
            );
        }
    }

    //lấy dữ liệu từ service Class bằng RestTemplate (not finalized yet)
//    @GetMapping("/classById/{id}")
//    public ResponseEntity<ResponseMessage> getClassesByProgramId(@PathVariable Integer id) {
//        return ResponseEntity.ok(new ResponseMessage("0", trainingServiceImp.getClassbyTrainingProgramId(id), "Get classes with training program Id success"));
//    }
//
//    @GetMapping("/syllabusById/{id}")
//    public ResponseEntity<ResponseMessage> getSyllabusByProgramId(@PathVariable Integer id) {
//        return ResponseEntity.ok(new ResponseMessage("0", trainingServiceImp.getSyllabusByTrainingProgramId(id), "Get classes with training program Id success"));
//    }
}