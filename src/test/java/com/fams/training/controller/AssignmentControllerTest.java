package com.fams.training.controller;

import com.fams.training.DTO.AssignmentDTO;
import com.fams.training.TrainingManagementApplication;
import com.fams.training.service.Imp.AssignmentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TrainingManagementApplication.class)
@AutoConfigureMockMvc
class AssignmentControllerTest {
    @Mock
    AssignmentServiceImpl assignmentService;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AssignmentController assignmentController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllAssignmentsReturnsListOfAssignments() {
        // Arrange
        AssignmentDTO assignmentDTO = new AssignmentDTO();
        assignmentDTO.setAssignmentId(1);
        assignmentDTO.setTitle("Assignment 1");

        AssignmentDTO assignmentDTO1 = new AssignmentDTO();
        assignmentDTO1.setAssignmentId(2);
        assignmentDTO1.setTitle("Assignment 2");

        List<AssignmentDTO> mockAssignments = Arrays.asList(assignmentDTO, assignmentDTO1);

        when(assignmentService.getAllAssignments()).thenReturn(mockAssignments);

        ResponseEntity<List<AssignmentDTO>> responseEntity = assignmentController.getAllAssignments();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().size());
        assertEquals(1, responseEntity.getBody().get(0).getAssignmentId());
        assertEquals("Assignment 1", responseEntity.getBody().get(0).getTitle());
        assertEquals(2, responseEntity.getBody().get(1).getAssignmentId());
        assertEquals("Assignment 2", responseEntity.getBody().get(1).getTitle());
    }

    @Test
    void getAllAssignmentsReturnsEmptyList() {
        // Arrange
        List<AssignmentDTO> mockAssignments = Collections.emptyList();

        when(assignmentService.getAllAssignments()).thenReturn(mockAssignments);

        // Act
        ResponseEntity<List<AssignmentDTO>> responseEntity = assignmentController.getAllAssignments();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(0, responseEntity.getBody().size());
    }

    @Test
    void getAssignmentByIdReturnsAssignment() throws Exception {
        // Mock assignment
        AssignmentDTO mockAssignment = new AssignmentDTO();
        mockAssignment.setAssignmentId(1);
        mockAssignment.setTitle("Assignment 1");

        when(assignmentService.getAssignmentById(1)).thenReturn(mockAssignment);

        MvcResult result = mockMvc.perform(get("/assignment/get-by-id/1"))
                .andExpect(status().isOk())
                .andReturn();
        ResponseEntity<AssignmentDTO> responseEntity = assignmentController.getAssignmentById(1);


        // Parse and check the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Assignment 1", Objects.requireNonNull(responseEntity.getBody()).getTitle());
    }

    @Test
    void getAssignmentByIdReturnsNotFound() throws Exception {
        // Mock service behavior
        when(assignmentService.getAssignmentById(1)).thenReturn(null);

        // Perform the GET request
        ResponseEntity<AssignmentDTO> responseEntity = assignmentController.getAssignmentById(1);


        // Check that the response body is empty
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void createAssignmentReturnsCreated() throws Exception {
        // Prepare a sample AssignmentDTO
        AssignmentDTO requestAssignment = new AssignmentDTO();
        requestAssignment.setTitle("Sample Assignment");
        requestAssignment.setDescription("Sample description");
        requestAssignment.setScore(10);
        requestAssignment.setDueDate(LocalDate.now());

        // Define the behavior of assignmentService.createAssignment
        AssignmentDTO responseAssignment = new AssignmentDTO();
        responseAssignment.setAssignmentId(1);
        responseAssignment.setTitle("Sample Assignment");
        responseAssignment.setDescription("Sample description");
        responseAssignment.setScore(10);
        responseAssignment.setDueDate(LocalDate.now());

        Mockito.when(assignmentService.createAssignment(Mockito.any(AssignmentDTO.class))).thenReturn(responseAssignment);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/assignment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAssignment)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignmentId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Sample Assignment"));
    }

    @Test
    public void updateAssignment_ReturnsUpdatedAssignment() throws Exception {
        // Arrange
        Integer assignmentId = 1;
        AssignmentDTO updatedDTO = new AssignmentDTO();
        updatedDTO.setAssignmentId(assignmentId);
        updatedDTO.setDueDate(LocalDate.now());
        updatedDTO.setDescription("Assignment 1");
        updatedDTO.setScore(10);
        updatedDTO.setTitle("Assignment 1");

        when(assignmentService.updateAssignment(assignmentId, updatedDTO)).thenReturn(updatedDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/assignment/update/"+ assignmentId)
                        .content(objectMapper.writeValueAsString(updatedDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.assignmentId").value(assignmentId));
    }

    @Test
    public void updateAssignment_NotFound() throws Exception {
        // Arrange
        Integer assignmentId = 1;
        AssignmentDTO updatedDTO = new AssignmentDTO();
        updatedDTO.setAssignmentId(assignmentId);
        updatedDTO.setDueDate(LocalDate.now());

        Mockito.when(assignmentService.updateAssignment(assignmentId, updatedDTO)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/assignment/update/" + assignmentId)
                        .content(objectMapper.writeValueAsString(updatedDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}