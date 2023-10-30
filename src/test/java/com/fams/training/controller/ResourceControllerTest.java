package com.fams.training.controller;

import com.fams.training.DTO.Message;
import com.fams.training.DTO.PageableDTO;
import com.fams.training.DTO.ResponseMessage;
import com.fams.training.TrainingManagementApplication;
import com.fams.training.entity.Resource;
import com.fams.training.repository.ResourceRepository;
import com.fams.training.service.Imp.ResourceServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TrainingManagementApplication.class)
@AutoConfigureMockMvc
class ResourceControllerTest {
    @Mock
    private ResourceServiceImp resourceService;

    @InjectMocks
    private ResourceController resourceController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void getAllResourceReturnsSuccess() {
        int page = 0;
        int size = 10;

        Resource resource1 = new Resource();
        resource1.setResourceId(1);
        resource1.setTitle("Resource 1");

        Resource resource2 = new Resource();
        resource2.setResourceId(2);
        resource2.setTitle("Resource 2");

        Page<Resource> resourcePage = new PageImpl<>(Arrays.asList(resource1, resource2));

        when(resourceService.getAllResource(page, size)).thenReturn(resourcePage);

        ResponseEntity<ResponseMessage> responseEntity = resourceController.getAllResource(page, size);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(HttpStatus.OK.value(), Objects.requireNonNull(responseEntity.getBody()).getCode());
        assertEquals(Message.SUCCESS, responseEntity.getBody().getMessage());

        PageableDTO<Resource> data = (PageableDTO<Resource>) responseEntity.getBody().getData();
        assertEquals(2, data.getContent().size());
        assertEquals(0, data.getPageNumber());
        assertEquals(2, data.getTotalElements());
        assertEquals(1, data.getTotalPages());
    }

    @Test
    void getAllResourceReturnsNoContent() {
        int page = 0;
        int size = 10;

        Page<Resource> emptyResourcePage = new PageImpl<>(Collections.emptyList());

        when(resourceService.getAllResource(page, size)).thenReturn(emptyResourcePage);

        ResponseEntity<ResponseMessage> responseEntity = resourceController.getAllResource(page, size);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT.value(), responseEntity.getBody().getCode());
        assertEquals(Message.NO_CONTENT, responseEntity.getBody().getMessage());
        assertNull(responseEntity.getBody().getData());
    }

    @Test
    void getAllResourceReturnsInternalServerError() throws Exception {
        int page = 0;
        int size = 10;

        when(resourceService.getAllResource(page, size)).thenThrow(HttpServerErrorException.InternalServerError.class);

        ResponseEntity<ResponseMessage> responseEntity = resourceController.getAllResource(page, size);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getBody().getCode());
        assertEquals(Message.INTERNAL_SERVER_ERROR, responseEntity.getBody().getMessage());
        assertNull(responseEntity.getBody().getData());
    }

    @Test
    void uploadMaterialsResourceReturnsSuccess() throws Exception {
        String description = "Sample description";
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Sample content".getBytes());

        when(resourceService.uploadTrainingMaterial(description, file)).thenReturn(true);

        mockMvc.perform(
                        multipart("/resource/upload-materials")
                                .file(file)
                                .param("description", description)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value(Message.SUCCESS));
    }

    @Test
    void uploadMaterialsResourceReturnsNoFileFound() throws Exception {
        String description = "Sample description";
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Sample content".getBytes());

        when(resourceService.uploadTrainingMaterial(description, file)).thenReturn(false);

        ResponseEntity<ResponseMessage> responseEntity = resourceController.uploadMaterialsResource(description, file);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT.value(), responseEntity.getBody().getCode());
        assertEquals(Message.NO_CONTENT, responseEntity.getBody().getMessage());
        assertNull(responseEntity.getBody().getData());
    }

    @Test
    void deleteTrainingMaterialReturnsSuccess() throws Exception {
        // Arrange
        int resourceId = 1;
        when(resourceService.deleteMaterials(resourceId)).thenReturn(true);

        // Act
        ResponseEntity<ResponseMessage> responseEntity = resourceController.deleteTrainingMaterial(resourceId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(HttpStatus.OK.value(), responseEntity.getBody().getCode());
        assertNull(responseEntity.getBody().getData());
        assertEquals(Message.SUCCESS, responseEntity.getBody().getMessage());
    }

    @Test
    void deleteTrainingMaterialReturnsFailed() throws Exception {
        int resourceId = 1;
        when(resourceService.deleteMaterials(resourceId)).thenReturn(false);

        ResponseEntity<ResponseMessage> responseEntity = resourceController.deleteTrainingMaterial(resourceId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT.value(), responseEntity.getBody().getCode());
        assertNull(responseEntity.getBody().getData());
        assertEquals(Message.NO_CONTENT, responseEntity.getBody().getMessage());
    }

    @Test
    void downloadTrainingProgramTemplateReturnsSuccess() throws Exception {
        // Arrange
        String title = "Training program template";
        byte[] mockFileData = "Sample content".getBytes();
        Resource mockResource = new Resource();
        mockResource.setFilename("template.txt");

        // Mocking the behavior of resourceService.downloadTrainingProgramTemplate(title)
        when(resourceService.downloadTrainingProgramTemplate(title)).thenReturn(mockFileData);

        // Mocking the behavior of repository.findById(1)
        when(resourceRepository.findById(1)).thenReturn(Optional.of(mockResource));

        // Act
        ResponseEntity<byte[]> responseEntity = resourceController.downloadTrainingProgramTemplate();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        HttpHeaders headers = responseEntity.getHeaders();
        assertEquals("attachment; filename=template.txt", headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getContentType());
        assertArrayEquals(mockFileData, responseEntity.getBody());
    }

    @Test
    void downloadTrainingProgramTemplateReturnsNotFound() throws Exception {
        String title = "Non-existent template";

        when(resourceService.downloadTrainingProgramTemplate(title)).thenReturn(null);

        ResponseEntity<byte[]> responseEntity = resourceController.downloadTrainingProgramTemplate();

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void downloadMaterialsReturnsNotFound() throws Exception {
        when(resourceService.downloadTrainingMaterial(1)).thenReturn(null);

        ResponseEntity<byte[]> responseEntity = resourceController.downloadResource(1);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }



}