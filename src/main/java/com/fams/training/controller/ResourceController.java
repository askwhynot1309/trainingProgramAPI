package com.fams.training.controller;

import com.fams.training.DTO.PageableDTO;
import com.fams.training.DTO.ResponseMessage;
import com.fams.training.entity.Resource;
import com.fams.training.repository.ResourceRepository;
import com.fams.training.service.Interface.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceRepository repository;

    @GetMapping("/get-all-resource")
    public ResponseEntity<ResponseMessage> getAllResource(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<Resource> list = resourceService.getAllResource(page, size);

            PageableDTO<Resource> data = new PageableDTO<>();
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
                    new ResponseMessage("1", null, "Internal server error")
            );
        }
    }

    @PostMapping("/upload-materials")
    public ResponseEntity<ResponseMessage> uploadMaterialsResource(
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file
    ) {
        if (resourceService.uploadTrainingMaterial(description, file)) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", null, "Upload successfully")
            );
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    new ResponseMessage("1", null, "No file found")
            );
        }
    }

    @GetMapping("/download-materials/{resourceId}")
    public ResponseEntity<byte[]> downloadResource(@PathVariable int resourceId) {
        byte[] fileData = resourceService.downloadTrainingMaterial(resourceId);
        if (fileData != null) {
            Resource resource = repository.findById(resourceId).orElse(null);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download-training-template")
    public ResponseEntity<byte[]> downloadTrainingProgramTemplate() {
        String title = "Training program template";
        byte[] fileData = resourceService.downloadTrainingProgramTemplate(title);
        if (fileData != null) {
            Resource resource = repository.findById(1).orElse(null);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete-materials/{resourceId}")
    public ResponseEntity<ResponseMessage> deleteTrainingMaterial(@PathVariable int resourceId) {
        boolean deleted = resourceService.deleteMaterials(resourceId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessage("0", null, "Material deleted successfully")
            );
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    new ResponseMessage("1", null, "Failed to delete material")
            );
        }
    }

}
