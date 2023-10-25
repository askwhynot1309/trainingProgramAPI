package com.fams.training.controller;


import com.fams.training.TrainingManagementApplication;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.service.Imp.TrainingServiceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = TrainingManagementApplication.class)
@AutoConfigureMockMvc
class TrainingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainingServiceImp trainingServiceImp;

    private final TrainingController controller = new TrainingController();
    @Test
    void searchTrainingProgram() {
    }

    @Test
    void createTrainingProgramGivenIdExistsReturnsOk() throws Exception {
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .trainingId(5)
                .status("Drafting")
                .name("Minh Dinh dep trai")
                .build();
        String trainingProgramJson = objectMapper.writeValueAsString(trainingProgram);

        when(trainingServiceImp.createNewTrainingProgram(trainingProgram)).thenReturn(1);

        mockMvc.perform(
                        post("/api/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(trainingProgramJson)

                )
                .andExpect(status().isOk());
    }

    @Test
    void createTrainingProgramReturnsInternalServerError() throws Exception {
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .trainingId(5)
                .status("Drafting")
                .name("Minh Dinh")
                .build();
        when(trainingServiceImp.createNewTrainingProgram(trainingProgram)).thenReturn(0);

        String trainingProgramJson = objectMapper.writeValueAsString(trainingProgram);

        mockMvc.perform(
                        post("/api/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(trainingProgramJson)
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createTrainingProgramReturnsDuplicateIdResponse() throws Exception {
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .trainingId(1)
                .build();
        when(trainingServiceImp.createNewTrainingProgram(trainingProgram)).thenReturn(0);
        when(trainingServiceImp.existsTrainingProgramById(1)).thenReturn(true);

        String trainingProgramJson = objectMapper.writeValueAsString(trainingProgram);

        mockMvc.perform(
                        post("/api/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(trainingProgramJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Duplicate ID"));
    }

    @Test
    void deactivateTrainingProgramReturnsOk() throws Exception {
        int trainingProgramId = 1;
        String idJson = objectMapper.writeValueAsString(trainingProgramId);

        doNothing().when(trainingServiceImp).deactivateTrainingProgram(1);

        mockMvc.perform(
                        post("/api/deactivate/" + trainingProgramId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Training program deactivated successfully"));
    }

    @Test
    void deactivateTrainingProgramWhenIdNotFoundReturnsNotFound() throws Exception {
        int trainingProgramId = 9;

        doThrow(new EntityNotFoundException("Training program not found")).when(trainingServiceImp).deactivateTrainingProgram(trainingProgramId);

        mockMvc.perform(
                        post("/api/deactivate/" + trainingProgramId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Training program not found"));
    }

    @Test
    void deactivateTrainingProgramReturnsInternalServerError() throws Exception {
        int trainingProgramId = 1;

        // Mock the service to throw an unexpected exception
        doThrow(new RuntimeException("Internal server error")).when(trainingServiceImp).deactivateTrainingProgram(trainingProgramId);

        mockMvc.perform(
                        post("/api/deactivate/" + trainingProgramId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void activateTrainingProgramReturnsOk() throws Exception {
        int trainingProgramId = 1;

        doNothing().when(trainingServiceImp).activateTrainingProgram(1);

        mockMvc.perform(
                        post("/api/activate/" + trainingProgramId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Training program activated successfully"));
    }

    @Test
    void activateTrainingProgramWhenIdNotFoundReturnsNotFound() throws Exception {
        int trainingProgramId = 9;

        doThrow(new EntityNotFoundException("Training program not found")).when(trainingServiceImp).activateTrainingProgram(trainingProgramId);

        mockMvc.perform(
                        post("/api/activate/" + trainingProgramId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Training program not found"));
    }

    @Test
    void activateTrainingProgramReturnsInternalServerError() throws Exception {
        int trainingProgramId = 1;

        // Mock the service to throw an unexpected exception
        doThrow(new RuntimeException("Internal server error")).when(trainingServiceImp).activateTrainingProgram(trainingProgramId);

        mockMvc.perform(
                        post("/api/activate/" + trainingProgramId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void duplicateTrainingProgramReturnsOk() throws Exception {
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .trainingId(1)
                .build();
        String idJson = objectMapper.writeValueAsString(1);

        when(trainingServiceImp.duplicateTrainingProgram(1)).thenReturn(trainingProgram);

        mockMvc.perform(
                        post("/api/duplicateProgram/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(idJson)
                )
                .andExpect(status().isOk());
    }

    @Test
    void duplicateTrainingProgramReturnsNotFound() throws Exception {
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .trainingId(1)
                .build();
        String idJson = objectMapper.writeValueAsString(1);

        when(trainingServiceImp.duplicateTrainingProgram(1)).thenReturn(null);

        mockMvc.perform(
                        post("/api/duplicateProgram/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(idJson)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void searchTrainingProgramReturnsSuccess() throws Exception {
        // Define your test data and expectations
        Integer trainingProgramId = 1;

        // Create a sample TrainingProgram object
        TrainingProgram trainingProgram = TrainingProgram.builder()
                .trainingId(1)
                .status("Active")
                .name("Test name")
                .build();

        // Mock the behavior of trainingServiceImp.searchTrainingProgram
        when(trainingServiceImp.searchTrainingProgram(trainingProgramId)).thenReturn(trainingProgram);

        // Perform the GET request
        mockMvc.perform(get("/api/searchWithId/" + trainingProgramId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.trainingId").value(1))
                .andExpect(jsonPath("$.data.name").value("Test name"))
                .andExpect(jsonPath("$.data.status").value("Active"));
    }

    @Test
    void searchTrainingProgramReturnsNotFound() throws Exception {
        // Define your test data and expectations
        Integer trainingProgramId = 2;

        when(trainingServiceImp.searchTrainingProgram(trainingProgramId)).thenReturn(null);

        // Perform the GET request
        mockMvc.perform(get("/api/searchWithId/" + trainingProgramId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.message").value("Training program not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void searchTrainingProgramReturnsInternalServerError() throws Exception {
        // Define your test data and expectations
        Integer trainingProgramId = 3;

        // Mock the behavior of trainingServiceImp.searchTrainingProgram to throw an exception
        when(trainingServiceImp.searchTrainingProgram(trainingProgramId)).thenThrow(new RuntimeException("Something went wrong"));

        // Perform the GET request
        mockMvc.perform(get("/api/searchWithId/" + trainingProgramId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.message").value("Internal server error"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateTrainingProgramReturnsSuccess() throws Exception {
        Integer trainingProgramId = 1;

        // tạo test data
        TrainingProgram updatedProgram = new TrainingProgram();
        updatedProgram.setName("Updated Program");
        updatedProgram.setCreateDate(LocalDate.of(2023, 10, 10));
        updatedProgram.setStatus("Active");

        // tạo test data
        TrainingProgram sampleUpdatedProgram = new TrainingProgram();
        sampleUpdatedProgram.setTrainingId(trainingProgramId);
        sampleUpdatedProgram.setName("Updated Program");
        sampleUpdatedProgram.setCreateDate(LocalDate.of(2023, 10, 10));
        sampleUpdatedProgram.setStatus("Active");

        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenReturn(sampleUpdatedProgram);

        mockMvc.perform(put("/api/update/" + trainingProgramId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProgram)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.message").value("Training program updated successfully"))
                .andExpect(jsonPath("$.data.trainingId").value(1))
                .andExpect(jsonPath("$.data.name").value("Updated Program"))
                .andExpect(jsonPath("$.data.status").value("Active"))
                .andExpect(jsonPath("$.data.createDate").value("2023-10-10"));
    }

    @Test
    void updateTrainingProgramReturnsNotFound() throws Exception {
        Integer trainingProgramId = 1;
        TrainingProgram updatedProgram = new TrainingProgram();

        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new EntityNotFoundException("Training program not found. Id not found"));

        mockMvc.perform(put("/api/update/" + trainingProgramId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProgram)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.message").value("Training program not found. Id not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateTrainingProgramReturnsIllegalStateException() throws Exception {
        // Define your test data and expectations
        Integer trainingProgramId = 1;
        TrainingProgram updatedProgram = new TrainingProgram();
        updatedProgram.setStatus("Inactive");

        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new IllegalStateException("Training program must be active to be updated"));

        mockMvc.perform(put("/api/update/" + trainingProgramId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProgram)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.message").value("Training program must be active to be updated"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateTrainingProgramReturnsInternalServerError() throws Exception {
        // Define your test data and expectations
        Integer trainingProgramId = 1;
        TrainingProgram updatedProgram = new TrainingProgram();

        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(put("/api/update/" + trainingProgramId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProgram)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.message").value("Internal server error"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    // GET PAGING TRAINING LIST TEST
    @Test
    void getTrainingProgramListReturnsSuccess() throws Exception {
        int page = 0;
        int size = 8;
        TrainingProgram program1 = new TrainingProgram();
        TrainingProgram program2 = new TrainingProgram();
        program1.setTrainingId(1); program1.setName("Program 1"); program1.setStatus("Active");
        program2.setTrainingId(2); program2.setName("Program 2"); program2.setStatus("Active");

        Page<TrainingProgram> trainingProgramPage = new PageImpl<>(Arrays.asList(
                program1,
                program2
        ));

        when(trainingServiceImp.getAllPagingTrainingProgram(page, size)).thenReturn(trainingProgramPage);

        mockMvc.perform(get("/api/trainingList")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(page))
                .andExpect(jsonPath("$.data.pageSize").value(trainingProgramPage.getSize()))
                .andExpect(jsonPath("$.data.totalElements").value(trainingProgramPage.getTotalElements()))
                .andExpect(jsonPath("$.data.totalPages").value(trainingProgramPage.getTotalPages()))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void getTrainingProgramListReturnsNoContent() throws Exception {
        int page = 0;
        int size = 8;
        Page<TrainingProgram> trainingProgramPage = new PageImpl<>(Collections.emptyList());

        when(trainingServiceImp.getAllPagingTrainingProgram(page, size)).thenReturn(trainingProgramPage);

        mockMvc.perform(get("/api/trainingList")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("No content found"));
    }
    @Test
    void getTrainingProgramListReturnsInternalServerError() throws Exception {
        int page = 0;
        int size = 8;

        when(trainingServiceImp.getAllPagingTrainingProgram(page, size)).thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(get("/api/trainingList")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("Internal server error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("500"));
    }

    // SEARCH BY KEYWORD TEST
    @Test
    void searchTrainingProgramByKeywordReturnsSuccess() throws Exception {
        String keyword = "Program";
        int page = 0;
        int size = 8;

        TrainingProgram program1 = new TrainingProgram();
        program1.setTrainingId(1);
        program1.setName("Program 1");
        program1.setStatus("Active");

        TrainingProgram program2 = new TrainingProgram();
        program2.setTrainingId(2);
        program2.setName("Program 2");
        program2.setStatus("Active");

        Page<TrainingProgram> trainingProgramPage = new PageImpl<>(Arrays.asList(
                program1,
                program2
        ));
        when(trainingServiceImp.searchTrainingProgramWithKeyword(keyword, PageRequest.of(page, size))).thenReturn(trainingProgramPage);

        mockMvc.perform(post("/api/searchByMatchingKeywords/" + keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(0))
//                .andExpect(jsonPath("$.data.pageSize").value(size))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.message").value("Success"));
    }



    @Test
    void searchTrainingProgramByKeywordReturnsNotFound() throws Exception {
        String keyword = "not exist";
        int page = 0;
        int size = 8;

        Page<TrainingProgram> trainingProgramPage = new PageImpl<>(Collections.emptyList());

        when(trainingServiceImp.searchTrainingProgramWithKeyword(keyword, PageRequest.of(page, size))).thenReturn(trainingProgramPage);

        mockMvc.perform(post("/api/searchByMatchingKeywords/" + keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Record not found"));
    }

    @Test
    void searchTrainingProgramByKeywordReturnsInternalServerError() throws Exception {
        String keyword = "java";
        int page = 0;
        int size = 8;

        when(trainingServiceImp.searchTrainingProgramWithKeyword(keyword, PageRequest.of(page, size)))
                .thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(post("/api/searchByMatchingKeywords/" + keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void getSortedTrainingProgramReturnsSuccess() throws Exception {
        String sortBy = "name";
        String sortOrder = "asc";
        int page = 0;
        int size = 8;

        TrainingProgram program1 = new TrainingProgram();
        program1.setTrainingId(1);
        program1.setName("Program 1");
        program1.setStatus("Active");

        TrainingProgram program2 = new TrainingProgram();
        program2.setTrainingId(2);
        program2.setName("Program 2");
        program2.setStatus("Active");

        Page<TrainingProgram> trainingProgram = new PageImpl<>(Arrays.asList(program1, program2));

        when(trainingServiceImp.getSortedTrainingProgram(sortBy, sortOrder, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy))).thenReturn(trainingProgram);

        mockMvc.perform(get("/api/trainingList/sort")
                        .param("sortBy", sortBy)
                        .param("sortOrder", sortOrder)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(trainingProgram.getSize()))
                .andExpect(jsonPath("$.data.totalElements").value(trainingProgram.getTotalElements()))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void getSortedTrainingProgramReturnsNotFound() throws Exception {
        String sortBy = "name";
        String sortOrder = "asc";
        int page = 0;
        int size = 8;

        Page<TrainingProgram> emptyPage = Page.empty();

        when(trainingServiceImp.getSortedTrainingProgram(sortBy, sortOrder, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/trainingList/sort")
                        .param("sortBy", sortBy)
                        .param("sortOrder", sortOrder)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Record not found"));
    }

    @Test
    void getSortedTrainingProgramReturnsInternalServerError() throws Exception {
        String sortBy = "name";
        String sortOrder = "asc";
        int page = 0;
        int size = 8;

        when(trainingServiceImp.getSortedTrainingProgram(sortBy, sortOrder, PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy))).thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(get("/api/trainingList/sort")
                        .param("sortBy", sortBy)
                        .param("sortOrder", sortOrder)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void uploadFileReturnsSuccess() throws Exception {
        // Prepare mock data
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "csv content".getBytes());
        String encoding = "UTF-8";
        char columnSeparator = ',';
        String scanningMethod = "Method1";
        String duplicateHandling = "Ignore";

        TrainingProgram program1 = new TrainingProgram();
        program1.setTrainingId(1);
        program1.setName("Program 1");
        program1.setStatus("Active");

        TrainingProgram program2 = new TrainingProgram();
        program2.setTrainingId(2);
        program2.setName("Program 2");
        program2.setStatus("Active");

        List<TrainingProgram> testData = Arrays.asList(program1, program2);

        // Mock the behavior
        when(trainingServiceImp.hasCSVFormat(file)).thenReturn(true);
        when(trainingServiceImp.importTrainingProgramFromFile(any(), any(), eq(encoding), eq(columnSeparator), eq(scanningMethod), eq(duplicateHandling))).thenReturn(testData);

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                        .file(file)
                        .param("encoding", encoding)
                        .param("columnSeparator", String.valueOf(columnSeparator))
                        .param("scanningMethod", scanningMethod)
                        .param("duplicateHandling", duplicateHandling)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].trainingId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Program 1"))
                .andExpect(jsonPath("$.data[0].status").value("Active"))
                .andExpect(jsonPath("$.data[1].trainingId").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Program 2"))
                .andExpect(jsonPath("$.data[1].status").value("Active"))
                .andExpect(jsonPath("$.message").value("Uploaded the file successfully: test.csv"));
    }

    @Test
    void uploadFileReturnsBadRequestForInvalidFormat() throws Exception {
        // Prepare mock data
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "txt content".getBytes());
        String encoding = "UTF-8";
        char columnSeparator = ',';
        String scanningMethod = "Method1";
        String duplicateHandling = "Ignore";

        // Mock the behavior
        when(trainingServiceImp.hasCSVFormat(file)).thenReturn(false);

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                        .file(file)
                        .param("encoding", encoding)
                        .param("columnSeparator", String.valueOf(columnSeparator))
                        .param("scanningMethod", scanningMethod)
                        .param("duplicateHandling", duplicateHandling)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Invalid file format: test.txt"));
    }



}