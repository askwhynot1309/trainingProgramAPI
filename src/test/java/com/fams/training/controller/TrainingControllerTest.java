package com.fams.training.controller;


import com.fams.training.DTO.Message;
import com.fams.training.DTO.TrainingProgramDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
                .id(5)
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
        TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
                .id(5)
                .status("Drafting")
                .name("Minh Dinh")
                .build();
        when(trainingServiceImp.createNewTrainingProgram(trainingProgram)).thenThrow(new RuntimeException("Internal server error"));

        String trainingProgramJson = objectMapper.writeValueAsString(trainingProgram);

        mockMvc.perform(
                        post("/api/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(trainingProgramJson)
                )
                .andExpect(status().isInternalServerError());
    }

//    @Test
//    void createTrainingProgramReturnsDuplicateIdResponse() throws Exception {
//        TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
//                .id(1)
//                .build();
//        when(trainingServiceImp.createNewTrainingProgram(trainingProgram)).thenReturn(0);
//        when(trainingServiceImp.existsTrainingProgramById(1)).thenReturn(true);
//
//        String trainingProgramJson = objectMapper.writeValueAsString(trainingProgram);
//
//        mockMvc.perform(
//                        post("/api/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(trainingProgramJson)
//                )
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.code").value("1"))
//                .andExpect(jsonPath("$.data").isEmpty())
//                .andExpect(jsonPath("$.message").value("Duplicate ID"));
//    }

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
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value(Message.DEACTIVATED_SUCCESSFUL));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value(Message.ACTIVATED_SUCCESSFUL));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
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
        TrainingProgramDTO trainingProgram = TrainingProgramDTO.builder()
                .id(1)
                .status("Active")
                .name("Test name")
                .build();

        // Mock the behavior of trainingServiceImp.searchTrainingProgram
        when(trainingServiceImp.searchTrainingProgram(trainingProgramId)).thenReturn(trainingProgram);

        // Perform the GET request
        mockMvc.perform(get("/api/searchWithId/" + trainingProgramId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(Message.SUCCESS))
                .andExpect(jsonPath("$.data.id").value(1))
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
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND))
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
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateTrainingProgramReturnsSuccess() throws Exception {
        Integer trainingProgramId = 1;

        // tạo test data
        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
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
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(Message.UPDATE_SUCCESSFUL));
    }

    @Test
    void updateTrainingProgramReturnsNotFound() throws Exception {
        Integer trainingProgramId = 1;
        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();

        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new EntityNotFoundException("Training program not found. Id not found"));

        mockMvc.perform(put("/api/update/" + trainingProgramId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProgram)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateTrainingProgramReturnsIllegalStateException() throws Exception {
        // Define your test data and expectations
        Integer trainingProgramId = 1;
        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
        updatedProgram.setStatus("Inactive");

        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new IllegalStateException("Training program must be active to be updated"));

        mockMvc.perform(put("/api/update/" + trainingProgramId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProgram)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateTrainingProgramReturnsInternalServerError() throws Exception {
        // Define your test data and expectations
        Integer trainingProgramId = 1;
        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();

        when(trainingServiceImp.updateTrainingProgram(trainingProgramId, updatedProgram)).thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(put("/api/update/" + trainingProgramId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProgram)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    // GET PAGING TRAINING LIST TEST
    @Test
    void getTrainingProgramListReturnsSuccess() throws Exception {
        int page = 0;
        int size = 8;
        TrainingProgramDTO program1 = new TrainingProgramDTO();
        TrainingProgramDTO program2 = new TrainingProgramDTO();
        program1.setId(1); program1.setName("Program 1"); program1.setStatus("Active");
        program2.setId(2); program2.setName("Program 2"); program2.setStatus("Active");

        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Arrays.asList(
                program1,
                program2
        ));

        when(trainingServiceImp.getAllPagingTrainingProgram(page, size)).thenReturn(trainingProgramPage);

        mockMvc.perform(get("/api/trainingList")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(page))
                .andExpect(jsonPath("$.data.pageSize").value(trainingProgramPage.getSize()))
                .andExpect(jsonPath("$.data.totalElements").value(trainingProgramPage.getTotalElements()))
                .andExpect(jsonPath("$.data.totalPages").value(trainingProgramPage.getTotalPages()))
                .andExpect(jsonPath("$.message").value(Message.SUCCESS));
    }

    @Test
    void getTrainingProgramListReturnsNoContent() throws Exception {
        int page = 0;
        int size = 8;
        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Collections.emptyList());

        when(trainingServiceImp.getAllPagingTrainingProgram(page, size)).thenReturn(trainingProgramPage);

        mockMvc.perform(get("/api/trainingList")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value(Message.NO_CONTENT));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
    }

    // SEARCH BY KEYWORD TEST
    @Test
    void searchTrainingProgramByKeywordReturnsSuccess() throws Exception {
        String keyword = "Program";
        int page = 0;
        int size = 8;

        TrainingProgramDTO program1 = new TrainingProgramDTO();
        program1.setId(1);
        program1.setName("Program 1");
        program1.setStatus("Active");

        TrainingProgramDTO program2 = new TrainingProgramDTO();
        program2.setId(2);
        program2.setName("Program 2");
        program2.setStatus("Active");

        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Arrays.asList(
                program1,
                program2
        ));
        when(trainingServiceImp.searchTrainingProgramWithKeyword(keyword, PageRequest.of(page, size))).thenReturn(trainingProgramPage);

        mockMvc.perform(post("/api/searchByMatchingKeywords/" + keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(0))
//                .andExpect(jsonPath("$.data.pageSize").value(size))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.message").value(Message.SUCCESS));
    }



    @Test
    void searchTrainingProgramByKeywordReturnsNotFound() throws Exception {
        String keyword = "not exist";
        int page = 0;
        int size = 8;

        Page<TrainingProgramDTO> trainingProgramPage = new PageImpl<>(Collections.emptyList());

        when(trainingServiceImp.searchTrainingProgramWithKeyword(keyword, PageRequest.of(page, size))).thenReturn(trainingProgramPage);

        mockMvc.perform(post("/api/searchByMatchingKeywords/" + keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(trainingProgram.getSize()))
                .andExpect(jsonPath("$.data.totalElements").value(trainingProgram.getTotalElements()))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.message").value(Message.SUCCESS));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value(Message.NOT_FOUND));
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
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value(Message.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testUploadFile_InvalidFormat() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid_format.txt", // Provide a file with an invalid format
                "text/plain",
                "Invalid content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                        .file(file)
                        .param("encoding", "UTF-8")
                        .param("columnSeparator", ",")
                        .param("scanningMethod", "id")
                        .param("duplicateHandling", "allow"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }


    @Test
    public void testUploadFile() throws Exception {
        // Prepare a sample CSV file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.csv",
                "text/csv", //
                ("trainingId,name,createBy,createDate,modifyBy,modifyDate,startTime,duration,topicId,status\n" +
                        "1,ABC,Wesley,2023/09/05,Minh,2023/02/12,2023/06/17,5,1,Inactive")
                        .getBytes(StandardCharsets.UTF_8)
        );

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                        .file(file)
                        .param("encoding", "UTF-8")
                        .param("columnSeparator", ",")
                        .param("scanningMethod", "id")
                        .param("duplicateHandling", "allow"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUploadFile_InternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.csv",
                "text/csv",
                ("trainingId,name,createBy,createDate,modifyBy,modifyDate,startTime,duration,topicId,status\n" +
                        "1,ABC,Wesley,2023/09/05,Minh,2023/02/12,2023/06/17,5,1,Inactive")
                        .getBytes(StandardCharsets.UTF_8)
        );

        // Mock a scenario where an unexpected error occurs during processing
        doThrow(new RuntimeException("Unexpected error")).when(trainingServiceImp).importTrainingProgramFromFile(any(), any(), any(), anyChar(), anyString(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload")
                        .file(file)
                        .param("encoding", "UTF-8")
                        .param("columnSeparator", ",")
                        .param("scanningMethod", "id")
                        .param("duplicateHandling", "allow"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }




}
