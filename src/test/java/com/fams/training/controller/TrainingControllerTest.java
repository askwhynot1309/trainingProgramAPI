//package com.fams.training.controller;
//
//
//import com.fams.training.DTO.Message;
//import com.fams.training.DTO.ResponseMessage;
//import com.fams.training.DTO.SyllabusRequest;
//import com.fams.training.DTO.TrainingProgramDTO;
//import com.fams.training.TrainingManagementApplication;
//import com.fams.training.entity.TrainingProgram;
//import com.fams.training.exception.EntityNotFoundException;
//import com.fams.training.service.Imp.TrainingServiceImp;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.hamcrest.Matchers.containsString;
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//
//@SpringBootTest(classes = TrainingManagementApplication.class)
//@AutoConfigureMockMvc
//class TrainingControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private TrainingServiceImp trainingServiceImp;
//
//    private final TrainingController controller = new TrainingController();
//    @Test
//    void searchTrainingProgram() {
//    }
//
//    @Test
//    void createTrainingProgramGivenIdExistsReturnsOk() throws Exception {
//        TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
//                .id(5)
//                .status("Drafting")
//                .name("Minh Dinh dep trai")
//                .build();
//        String trainingProgramJson = objectMapper.writeValueAsString(trainingProgram);
//
//        when(trainingServiceImp.createNewTrainingProgram(trainingProgram)).thenReturn(1);
//
//        mockMvc.perform(
//                        post("/api/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(trainingProgramJson)
//
//                )
//                .andExpect(status().isOk());
//    }
//
//
//    @Test
//    void createTrainingProgramReturnsInternalServerError() throws Exception {
//        TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
//                .id(5)
//                .status("Drafting")
//                .name("Minh Dinh")
//                .build();
//        when(trainingServiceImp.createNewTrainingProgram(trainingProgram)).thenThrow(new RuntimeException("Internal server error"));
//
//        String trainingProgramJson = objectMapper.writeValueAsString(trainingProgram);
//
//        mockMvc.perform(
//                        post("/api/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(trainingProgramJson)
//                )
//                .andExpect(status().isInternalServerError());
//    }
//
////    @Test
////    public void testCreateNewTrainingProgram_NullInput() {
////        // Act
////        ResponseEntity<ResponseMessage> response = controller.createNewTrainingProgram(null);
////
////        // Assert
////        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
////        assertNotNull(response.getBody());
////        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getCode());
////        assertNull(response.getBody().getData());
////        assertEquals(Message.NOT_FOUND, response.getBody().getMessage());
////    }
//
////    @Test
////    public void testCreateNewTrainingProgram_BadRequest() throws Exception {
////        // Arrange
////        TrainingProgramDTO trainingProgram = new TrainingProgramDTO();
////        trainingProgram.setName("Test");
////        trainingProgram.setCreateBy("Test");
////
////        when(trainingServiceImp.createNewTrainingProgram(any(TrainingProgramDTO.class))).thenReturn(0);
////
////        // Act
////        mockMvc.perform(post("/api/create")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(trainingProgram)))
////                .andExpect(status().isBadRequest())
////                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
////                .andExpect(jsonPath("$.data").doesNotExist());
////    }
//
////    @Test
////    void createTrainingProgramReturnsDuplicateIdResponse() throws Exception {
////        TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
////                .id(1)
////                .build();
////        when(trainingServiceImp.createNewTrainingProgram(trainingProgram)).thenReturn(0);
////        when(trainingServiceImp.existsTrainingProgramById(1)).thenReturn(true);
////
////        String trainingProgramJson = objectMapper.writeValueAsString(trainingProgram);
////
////        mockMvc.perform(
////                        post("/api/create")
////                                .contentType(MediaType.APPLICATION_JSON)
////                                .content(trainingProgramJson)
////                )
////                .andExpect(status().isConflict())
////                .andExpect(jsonPath("$.code").value("1"))
////                .andExpect(jsonPath("$.data").isEmpty())
////                .andExpect(jsonPath("$.message").value("Duplicate ID"));
////    }
//
//    @Test
//    void deactivateTrainingProgramReturnsOk() throws Exception {
//        int trainingProgramId = 1;
//        String idJson = objectMapper.writeValueAsString(trainingProgramId);
//
//        doNothing().when(trainingServiceImp).deactivateTrainingProgram(1);
//
//        mockMvc.perform(
//                        post("/api/deactivate/" + trainingProgramId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.data").isEmpty())
//                .andExpect(jsonPath("$.message").value(Message.DEACTIVATED_SUCCESSFUL));
//    }
//
//    @Test
//    void deactivateTrainingProgramWhenIdNotFoundReturnsNotFound() throws Exception {
//        int trainingProgramId = 9;
//
//        doThrow(new EntityNotFoundException()).when(trainingServiceImp).deactivateTrainingProgram(trainingProgramId);
//
//        mockMvc.perform(
//                        post("/api/deactivate/" + trainingProgramId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
//                .andExpect(jsonPath("$.data").isEmpty())
//                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND));
//    }
//
//    @Test
//    void deactivateTrainingProgramReturnsInternalServerError() throws Exception {
//        int trainingProgramId = 1;
//
//        // Mock the service to throw an unexpected exception
//        doThrow(new RuntimeException("Internal server error")).when(trainingServiceImp).deactivateTrainingProgram(trainingProgramId);
//
//        mockMvc.perform(
//                        post("/api/deactivate/" + trainingProgramId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.data").isEmpty())
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
//    }
//
//    @Test
//    void activateTrainingProgramReturnsOk() throws Exception {
//        int trainingProgramId = 1;
//
//        doNothing().when(trainingServiceImp).activateTrainingProgram(1);
//
//        mockMvc.perform(
//                        post("/api/activate/" + trainingProgramId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.data").isEmpty())
//                .andExpect(jsonPath("$.message").value(Message.ACTIVATED_SUCCESSFUL));
//    }
//
//    @Test
//    void activateTrainingProgramWhenIdNotFoundReturnsNotFound() throws Exception {
//        int trainingProgramId = 9;
//
//        doThrow(new EntityNotFoundException()).when(trainingServiceImp).activateTrainingProgram(trainingProgramId);
//
//        mockMvc.perform(
//                        post("/api/activate/" + trainingProgramId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
//                .andExpect(jsonPath("$.data").isEmpty())
//                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND));
//    }
//
//    @Test
//    void activateTrainingProgramReturnsInternalServerError() throws Exception {
//        int trainingProgramId = 1;
//
//        // Mock the service to throw an unexpected exception
//        doThrow(new RuntimeException("Internal server error")).when(trainingServiceImp).activateTrainingProgram(trainingProgramId);
//
//        mockMvc.perform(
//                        post("/api/activate/" + trainingProgramId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.data").isEmpty())
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
//    }
//
//    @Test
//    void duplicateTrainingProgramReturnsOk() throws Exception {
//        TrainingProgram trainingProgram = TrainingProgram.builder()
//                .trainingId(1)
//                .build();
//        String idJson = objectMapper.writeValueAsString(1);
//
//        when(trainingServiceImp.duplicateTrainingProgram(1)).thenReturn(trainingProgram);
//
//        mockMvc.perform(
//                        post("/api/duplicateProgram/1")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(idJson)
//                )
//                .andExpect(status().isOk());
//    }
//
////    @Test
////    void duplicateTrainingProgramReturnsNotFound() throws Exception {
////        TrainingProgram trainingProgram = TrainingProgram.builder()
////                .trainingId(1)
////                .build();
////        String idJson = objectMapper.writeValueAsString(1);
////
////        when(trainingServiceImp.duplicateTrainingProgram(1)).thenReturn(null);
////
////        mockMvc.perform(
////                        post("/api/duplicateProgram/1")
////                                .contentType(MediaType.APPLICATION_JSON)
////                                .content(idJson)
////                )
////                .andExpect(status().isNotFound());
////    }
//
//    @Test
//    void searchTrainingProgramReturnsSuccess() throws Exception {
//        // Define your test data and expectations
//        Integer trainingProgramId = 1;
//
//        // Create a sample TrainingProgram object
//        TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
//                .id(1)
//                .status("Active")
//                .name("Test name")
//                .build();
//
//        // Mock the behavior of trainingServiceImp.searchTrainingProgram
//        when(trainingServiceImp.searchTrainingProgram(trainingProgramId)).thenReturn(trainingProgram);
//
//        // Perform the GET request
//        mockMvc.perform(get("/api/searchWithId/" + trainingProgramId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.message").value(Message.SUCCESS))
//                .andExpect(jsonPath("$.data.id").value(1))
//                .andExpect(jsonPath("$.data.name").value("Test name"))
//                .andExpect(jsonPath("$.data.status").value("Active"));
//    }
//
////    @Test
////    void searchTrainingProgramReturnsNotFound() throws Exception {
////        // Define your test data and expectations
////        Integer trainingProgramId = 2;
////
////        when(trainingServiceImp.searchTrainingProgram(trainingProgramId)).thenReturn(null);
////
////        // Perform the GET request
////        mockMvc.perform(get("/api/searchWithId/" + trainingProgramId))
////                .andExpect(status().isNotFound())
////                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
////                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND))
////                .andExpect(jsonPath("$.data").doesNotExist());
////    }
//
//    @Test
//    void searchTrainingProgramReturnsInternalServerError() throws Exception {
//        // Define your test data and expectations
//        Integer trainingProgramId = 3;
//
//        // Mock the behavior of trainingServiceImp.searchTrainingProgram to throw an exception
//        when(trainingServiceImp.searchTrainingProgram(trainingProgramId)).thenThrow(new RuntimeException("Something went wrong"));
//
//        // Perform the GET request
//        mockMvc.perform(get("/api/searchWithId/" + trainingProgramId))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR))
//                .andExpect(jsonPath("$.data").doesNotExist());
//    }
//
//    @Test
//    void updateTrainingProgramReturnsSuccess() throws Exception {
//        Integer trainingProgramId = 1;
//
//        // tạo test data
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//        updatedProgram.setName("Updated Program");
<<<<<<< fe45d2479fe3ca14cfa2438be944b139ed08bba4
=======
//        updatedProgram.setCreateDate(LocalDate.of(2023, 10, 10));
>>>>>>> ed53b0d4f3a987d002b56a9eaf7536ca53a223ec
//        updatedProgram.setStatus("Active");
//
//        // tạo test data
//        TrainingProgram sampleUpdatedProgram = new TrainingProgram();
//        sampleUpdatedProgram.setTrainingId(trainingProgramId);
//        sampleUpdatedProgram.setName("Updated Program");
<<<<<<< fe45d2479fe3ca14cfa2438be944b139ed08bba4
=======
//        sampleUpdatedProgram.setCreateDate(LocalDate.of(2023, 10, 10));
>>>>>>> ed53b0d4f3a987d002b56a9eaf7536ca53a223ec
//        sampleUpdatedProgram.setStatus("Active");
//
//        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenReturn(sampleUpdatedProgram);
//
//        mockMvc.perform(put("/api/update/" + trainingProgramId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedProgram)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.message").value(Message.UPDATE_SUCCESSFUL));
//    }
//
//    @Test
//    void updateTrainingProgramReturnsNotFound() throws Exception {
//        Integer trainingProgramId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//
//        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new EntityNotFoundException());
//
//        mockMvc.perform(put("/api/update/" + trainingProgramId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedProgram)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
//                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND))
//                .andExpect(jsonPath("$.data").doesNotExist());
//    }
//
////    @Test
////    void updateTrainingProgramReturnsBadRequest() throws Exception {
////        int trainingProgramId = 1;
////        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
////
////        when(trainingServiceImp.updateTrainingProgram(any(Integer.class), any(TrainingProgramDTO.class))).thenReturn(null);
////
////        mockMvc.perform(put("/api/update/" + trainingProgramId)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(updatedProgram)))
////                .andExpect(status().isBadRequest())
////                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
////                .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
////                .andExpect(jsonPath("$.data").doesNotExist());
////    }
//
////    @Test
////    void updateTrainingProgramReturnsIllegalStateException() throws Exception {
////        // Define your test data and expectations
////        Integer trainingProgramId = 1;
////        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
////        updatedProgram.setStatus("Inactive");
////
////        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new IllegalStateException("Training program must be active to be updated"));
////
////        mockMvc.perform(put("/api/update/" + trainingProgramId)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(updatedProgram)))
////                .andExpect(status().isBadRequest())
////                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
////                .andExpect(jsonPath("$.data").doesNotExist());
////    }
//
//    @Test
//    void updateTrainingProgramReturnsInternalServerError() throws Exception {
//        // Define your test data and expectations
//        Integer trainingProgramId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//
//        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new RuntimeException("Something went wrong"));
//
//        mockMvc.perform(put("/api/update/" + trainingProgramId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedProgram)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR))
//                .andExpect(jsonPath("$.data").doesNotExist());
//    }
//
//    // GET PAGING TRAINING LIST TEST
//    @Test
//    void getTrainingProgramListReturnsSuccess() throws Exception {
//        int page = 0;
//        int size = 8;
<<<<<<< fe45d2479fe3ca14cfa2438be944b139ed08bba4
//        String status = "Active";
=======
>>>>>>> ed53b0d4f3a987d002b56a9eaf7536ca53a223ec
//        TrainingProgramDTO program1 = new TrainingProgramDTO();
//        TrainingProgramDTO program2 = new TrainingProgramDTO();
//        program1.setId(1); program1.setName("Program 1"); program1.setStatus("Active");
//        program2.setId(2); program2.setName("Program 2"); program2.setStatus("Active");
<<<<<<< fe45d2479fe3ca14cfa2438be944b139ed08bba4
=======
//
//        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Arrays.asList(
//                program1,
//                program2
//        ));
>>>>>>> ed53b0d4f3a987d002b56a9eaf7536ca53a223ec
//
//        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Arrays.asList(
//                program1,
//                program2
//        ));
//
//        when(trainingServiceImp.getAllPagingTrainingProgram(page, size, status)).thenReturn(trainingProgramPage);
//
//        mockMvc.perform(get("/api/trainingList")
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.data.content").isArray())
//                .andExpect(jsonPath("$.data.pageNumber").value(page))
//                .andExpect(jsonPath("$.data.pageSize").value(trainingProgramPage.getSize()))
//                .andExpect(jsonPath("$.data.totalElements").value(trainingProgramPage.getTotalElements()))
//                .andExpect(jsonPath("$.data.totalPages").value(trainingProgramPage.getTotalPages()))
//                .andExpect(jsonPath("$.message").value(Message.SUCCESS));
//    }
//
////    @Test
////    void getTrainingProgramListReturnsNoContent() throws Exception {
////        int page = 0;
////        int size = 8;
////        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Collections.emptyList());
////
////        when(trainingServiceImp.getAllPagingTrainingProgram(page, size)).thenReturn(trainingProgramPage);
////
////        mockMvc.perform(get("/api/trainingList")
////                        .param("page", String.valueOf(page))
////                        .param("size", String.valueOf(size))
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isNoContent())
////                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
////                .andExpect(jsonPath("$.data").doesNotExist())
////                .andExpect(jsonPath("$.message").value(Message.NO_CONTENT));
////    }
//    @Test
//    void getTrainingProgramListReturnsInternalServerError() throws Exception {
<<<<<<< fe45d2479fe3ca14cfa2438be944b139ed08bba4
//        int page = 0;
//        int size = 8;
//        String status = "Active";
//        when(trainingServiceImp.getAllPagingTrainingProgram(page, size, status)).thenThrow(new RuntimeException("Internal Server Error"));
//
//        mockMvc.perform(get("/api/trainingList")
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.data").doesNotExist())
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
//    }
//
//    // SEARCH BY KEYWORD TEST
//    @Test
//    void searchTrainingProgramByKeywordReturnsSuccess() throws Exception {
//        String keyword = "Program";
//        int page = 0;
//        int size = 8;
//
//        TrainingProgramDTO program1 = new TrainingProgramDTO();
//        program1.setId(1);
//        program1.setName("Program 1");
//        program1.setStatus("Active");
//
=======
//        int page = 0;
//        int size = 8;
//
//        when(trainingServiceImp.getAllPagingTrainingProgram(page, size)).thenThrow(new RuntimeException("Internal Server Error"));
//
//        mockMvc.perform(get("/api/trainingList")
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.data").doesNotExist())
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
//    }
//
//    // SEARCH BY KEYWORD TEST
//    @Test
//    void searchTrainingProgramByKeywordReturnsSuccess() throws Exception {
//        String keyword = "Program";
//        int page = 0;
//        int size = 8;
//
//        TrainingProgramDTO program1 = new TrainingProgramDTO();
//        program1.setId(1);
//        program1.setName("Program 1");
//        program1.setStatus("Active");
//
>>>>>>> ed53b0d4f3a987d002b56a9eaf7536ca53a223ec
//        TrainingProgramDTO program2 = new TrainingProgramDTO();
//        program2.setId(2);
//        program2.setName("Program 2");
//        program2.setStatus("Active");
//
//        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Arrays.asList(
//                program1,
//                program2
//        ));
//        when(trainingServiceImp.searchTrainingProgramWithKeyword(keyword, PageRequest.of(page, size))).thenReturn(trainingProgramPage);
//
//        mockMvc.perform(post("/api/searchByMatchingKeywords/" + keyword)
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.data.content").isArray())
//                .andExpect(jsonPath("$.data.pageNumber").value(0))
////                .andExpect(jsonPath("$.data.pageSize").value(size))
//                .andExpect(jsonPath("$.data.totalPages").value(1))
//                .andExpect(jsonPath("$.message").value(Message.SUCCESS));
//    }
//
//
//
////    @Test
////    void searchTrainingProgramByKeywordReturnsNotFound() throws Exception {
////        String keyword = "not exist";
////        int page = 0;
////        int size = 8;
////
////        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Collections.emptyList());
////
////        when(trainingServiceImp.searchTrainingProgramWithKeyword(keyword, PageRequest.of(page, size))).thenReturn(trainingProgramPage);
////
////        mockMvc.perform(post("/api/searchByMatchingKeywords/" + keyword)
////                        .param("page", String.valueOf(page))
////                        .param("size", String.valueOf(size))
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isNotFound())
////                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
////                .andExpect(jsonPath("$.data").doesNotExist())
////                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND));
////    }
//
//    @Test
//    void searchTrainingProgramByKeywordReturnsInternalServerError() throws Exception {
//        String keyword = "java";
//        int page = 0;
//        int size = 8;
//
//        when(trainingServiceImp.searchTrainingProgramWithKeyword(keyword, PageRequest.of(page, size)))
//                .thenThrow(new RuntimeException("Internal Server Error"));
//
//        mockMvc.perform(post("/api/searchByMatchingKeywords/" + keyword)
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.data").doesNotExist())
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
//    }
//
//    @Test
//    void getSortedTrainingProgramReturnsSuccess() throws Exception {
//        String sortBy = "name";
//        String sortOrder = "asc";
//        int page = 0;
//        int size = 8;
//
//        TrainingProgram program1 = new TrainingProgram();
//        program1.setTrainingId(1);
//        program1.setName("Program 1");
//        program1.setStatus("Active");
//
//        TrainingProgram program2 = new TrainingProgram();
//        program2.setTrainingId(2);
//        program2.setName("Program 2");
//        program2.setStatus("Active");
//
//        Page<TrainingProgram> trainingProgram = new PageImpl<>(Arrays.asList(program1, program2));
//
//        when(trainingServiceImp.getSortedTrainingProgram(sortBy, sortOrder, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy))).thenReturn(trainingProgram);
//
//        mockMvc.perform(get("/api/trainingList/sort")
//                        .param("sortBy", sortBy)
//                        .param("sortOrder", sortOrder)
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.data.content").isArray())
//                .andExpect(jsonPath("$.data.pageNumber").value(0))
//                .andExpect(jsonPath("$.data.pageSize").value(trainingProgram.getSize()))
//                .andExpect(jsonPath("$.data.totalElements").value(trainingProgram.getTotalElements()))
//                .andExpect(jsonPath("$.data.totalPages").value(1))
//                .andExpect(jsonPath("$.message").value(Message.SUCCESS));
//    }
//
////    @Test
////    void getSortedTrainingProgramReturnsNotFound() throws Exception {
////        String sortBy = "name";
////        String sortOrder = "asc";
////        int page = 0;
////        int size = 8;
////
////        Page<TrainingProgram> emptyPage = Page.empty();
////
////        when(trainingServiceImp.getSortedTrainingProgram(sortBy, sortOrder, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy))).thenReturn(emptyPage);
////
////        mockMvc.perform(get("/api/trainingList/sort")
////                        .param("sortBy", sortBy)
////                        .param("sortOrder", sortOrder)
////                        .param("page", String.valueOf(page))
////                        .param("size", String.valueOf(size))
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isNotFound())
////                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
////                .andExpect(jsonPath("$.data").doesNotExist())
////                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND));
////    }
//
//    @Test
//    void getSortedTrainingProgramReturnsInternalServerError() throws Exception {
//        String sortBy = "name";
//        String sortOrder = "asc";
//        int page = 0;
//        int size = 8;
//
//        when(trainingServiceImp.getSortedTrainingProgram(sortBy, sortOrder, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy))).thenThrow(new RuntimeException("Internal server error"));
//
//        mockMvc.perform(get("/api/trainingList/sort")
//                        .param("sortBy", sortBy)
//                        .param("sortOrder", sortOrder)
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.data").doesNotExist())
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
//    }
//
//    @Test
//    public void testUploadFile_InvalidFormat() throws Exception {
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "invalid_format.txt", // Provide a file with an invalid format
//                "text/plain",
//                "Invalid content".getBytes(StandardCharsets.UTF_8)
//        );
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
//                        .file(file)
//                        .param("encoding", "UTF-8")
//                        .param("columnSeparator", ",")
//                        .param("scanningMethod", "id")
//                        .param("duplicateHandling", "allow"))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
//    }
//
//
//    @Test
//    public void testUploadFile() throws Exception {
//        // Prepare a sample CSV file
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "sample.csv",
//                "text/csv", //
//                ("trainingId,name,createBy,createDate,modifyBy,modifyDate,startTime,duration,topicId,status\n" +
//                        "1,ABC,Wesley,2023/09/05,Minh,2023/02/12,2023/06/17,5,1,Inactive")
//                        .getBytes(StandardCharsets.UTF_8)
//        );
//
//        // Perform the request
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
//                        .file(file)
//                        .param("encoding", "UTF-8")
//                        .param("columnSeparator", ",")
//                        .param("scanningMethod", "id")
//                        .param("duplicateHandling", "allow"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Test
//    public void testUploadFile_InternalServerError() throws Exception {
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "sample.csv",
//                "text/csv",
//                ("trainingId,name,createBy,createDate,modifyBy,modifyDate,startTime,duration,topicId,status\n" +
//                        "1,ABC,Wesley,2023/09/05,Minh,2023/02/12,2023/06/17,5,1,Inactive")
//                        .getBytes(StandardCharsets.UTF_8)
//        );
//
//        // Mock a scenario where an unexpected error occurs during processing
//        doThrow(new RuntimeException("Unexpected error")).when(trainingServiceImp).importTrainingProgramFromFile(any(), any(), any(), anyChar(), anyString(), anyString());
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
//                        .file(file)
//                        .param("encoding", "UTF-8")
//                        .param("columnSeparator", ",")
//                        .param("scanningMethod", "id")
//                        .param("duplicateHandling", "allow"))
//                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
//    }
//
//    @Test
//    public void testGetSyllabusByTrainingId_ValidTrainingId() throws Exception {
//        Integer validTrainingId = 1;
//        List<Long> expectedSyllabusIds = Arrays.asList(123L, 456L);
//
//        // Set up mock behavior
//        when(trainingServiceImp.getSyllabusIdListByTrainingId(validTrainingId)).thenReturn(expectedSyllabusIds);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/getSyllabusIdByTrainingId/{trainingId}", validTrainingId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value(123L))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[1]").value(456L));
//    }
//
//    @Test
//    public void testGetSyllabusByTrainingId_InvalidTrainingId() throws Exception {
//        Integer invalidTrainingId = 999; // Assuming this is invalid
//
//        // Set up mock behavior
//        when(trainingServiceImp.getSyllabusIdListByTrainingId(invalidTrainingId)).thenThrow(EntityNotFoundException.class);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/getSyllabusIdByTrainingId/{trainingId}", invalidTrainingId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isNotFound());
//    }
//
<<<<<<< fe45d2479fe3ca14cfa2438be944b139ed08bba4
////    @Test
////    public void testSearchTrainingProgramByIdForRestTemplate_ValidTrainingId() throws Exception {
////        Integer validTrainingId = 1;
////        TrainingProgramDTO expectedTrainingProgram = new TrainingProgramDTO(); // Populate with expected data
////
////        // Set up mock behavior
////        when(trainingServiceImp.searchTrainingProgram(validTrainingId)).thenReturn(expectedTrainingProgram);
////
////        mockMvc.perform(MockMvcRequestBuilders
////                        .get("/api/searchByIdForRestTemplate/{trainingId}", validTrainingId)
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.status().isOk());
////    }
//
////    @Test
////    public void testSearchTrainingProgramByIdForRestTemplate_InvalidTrainingId() throws Exception {
////        Integer invalidTrainingId = 999; // Assuming this is invalid
////
////        // Set up mock behavior
////        when(trainingServiceImp.searchTrainingProgram(invalidTrainingId)).thenReturn(null);
////
////        mockMvc.perform(MockMvcRequestBuilders
////                        .get("/api/searchByIdForRestTemplate/{trainingId}", invalidTrainingId)
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.status().isNotFound());
////    }
//
////    @Test
////    public void testSearchTrainingProgramByIdForRestTemplate_Exception() throws Exception {
////        Integer validTrainingId = 1;
////
////        // Set up mock behavior
////        when(trainingServiceImp.searchTrainingProgram(validTrainingId)).thenThrow(new RuntimeException("Internal server error"));
////
////        mockMvc.perform(MockMvcRequestBuilders
////                        .get("/api/searchByIdForRestTemplate/{trainingId}", validTrainingId)
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
////    }
//
//    @Test
=======
//    @Test
//    public void testSearchTrainingProgramByIdForRestTemplate_ValidTrainingId() throws Exception {
//        Integer validTrainingId = 1;
//        TrainingProgramDTO expectedTrainingProgram = new TrainingProgramDTO(); // Populate with expected data
//
//        // Set up mock behavior
//        when(trainingServiceImp.searchTrainingProgram(validTrainingId)).thenReturn(expectedTrainingProgram);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/searchByIdForRestTemplate/{trainingId}", validTrainingId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
////    @Test
////    public void testSearchTrainingProgramByIdForRestTemplate_InvalidTrainingId() throws Exception {
////        Integer invalidTrainingId = 999; // Assuming this is invalid
////
////        // Set up mock behavior
////        when(trainingServiceImp.searchTrainingProgram(invalidTrainingId)).thenReturn(null);
////
////        mockMvc.perform(MockMvcRequestBuilders
////                        .get("/api/searchByIdForRestTemplate/{trainingId}", invalidTrainingId)
////                        .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(MockMvcResultMatchers.status().isNotFound());
////    }
//
//    @Test
//    public void testSearchTrainingProgramByIdForRestTemplate_Exception() throws Exception {
//        Integer validTrainingId = 1;
//
//        // Set up mock behavior
//        when(trainingServiceImp.searchTrainingProgram(validTrainingId)).thenThrow(new RuntimeException("Internal server error"));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/searchByIdForRestTemplate/{trainingId}", validTrainingId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
//    }
//
//    @Test
>>>>>>> ed53b0d4f3a987d002b56a9eaf7536ca53a223ec
//    public void testAddOrUpdateSyllabusId_Success() throws Exception {
//        // Arrange
//        Integer trainingId = 1;
//        List<SyllabusRequest> syllabusRequests = Arrays.asList(
//                new SyllabusRequest(1, 69L),
//                new SyllabusRequest(2, 70L)
//        );
//
//        when(trainingServiceImp.addOrUpdateSyllabusId(any(Integer.class), any(List.class))).thenReturn(true);
//
//        // Act
//        mockMvc.perform(post("/api/addOrUpdateSyllabusId/{trainingId}", trainingId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(syllabusRequests)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.data").doesNotExist())
//                .andExpect(jsonPath("$.message").value(Message.UPDATE_SUCCESSFUL));
//    }
//
////    @Test
////    public void testAddOrUpdateSyllabusId_BadRequest() throws Exception {
////        // Arrange
////        Integer trainingId = 1;
////        List<SyllabusRequest> syllabusRequests = Arrays.asList(
////                new SyllabusRequest(1, 69L),
////                new SyllabusRequest(2, 70L)
////        );
////
////        when(trainingServiceImp.addOrUpdateSyllabusId(any(Integer.class), any(List.class))).thenReturn(false);
////
////        // Act
////        mockMvc.perform(post("/api/addOrUpdateSyllabusId/{trainingId}", trainingId)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(syllabusRequests)))
////                .andExpect(status().isBadRequest())
////                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
////                .andExpect(jsonPath("$.data").doesNotExist())
////                .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST));
////    }
//
////    @Test
////    public void testAddOrUpdateSyllabusId_EntityNotFoundException() throws Exception {
////        // Arrange
////        Integer trainingId = 1;
////        List<SyllabusRequest> syllabusRequests = Arrays.asList(
////                new SyllabusRequest(1, 69L),
////                new SyllabusRequest(2, 70L)
////        );
////
////        when(trainingServiceImp.addOrUpdateSyllabusId(any(Integer.class), any(List.class))).thenThrow(new EntityNotFoundException());
////
////        // Act
////        mockMvc.perform(post("/api/addOrUpdateSyllabusId/{trainingId}", trainingId)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(syllabusRequests)))
////                .andExpect(status().isBadRequest())
////                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
////                .andExpect(jsonPath("$.data").doesNotExist())
////                .andExpect(jsonPath("$.message").value("Syllabus Id not found"));
////    }
//
//    @Test
//    public void testAddOrUpdateSyllabusIdInternalServerError() throws Exception {
//        // Arrange
//        Integer trainingId = 1;
//        List<SyllabusRequest> syllabusRequests = Arrays.asList(
//                new SyllabusRequest(1, 69L),
//                new SyllabusRequest(2, 70L)
//        );
//
//        when(trainingServiceImp.addOrUpdateSyllabusId(any(Integer.class), any(List.class))).thenThrow(new RuntimeException("Internal server error"));
//
//        // Act
//        mockMvc.perform(post("/api/addOrUpdateSyllabusId/{trainingId}", trainingId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(syllabusRequests)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
//                .andExpect(jsonPath("$.data").doesNotExist())
//                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
//    }
//}
