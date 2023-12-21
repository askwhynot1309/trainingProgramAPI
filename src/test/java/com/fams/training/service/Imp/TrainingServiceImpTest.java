//package com.fams.training.service.Imp;
//
//import com.fams.training.DTO.SyllabusRequest;
//import com.fams.training.DTO.TrainingProgramDTO;
//import com.fams.training.entity.TrainingProgram;
//import com.fams.training.entity.TrainingSyllabus;
//import com.fams.training.exception.EntityNotFoundException;
//import com.fams.training.exception.NotFoundContentException;
//import com.fams.training.repository.TrainingRepository;
//import com.fams.training.repository.TrainingSyllabusRepository;
//import org.apache.commons.csv.CSVRecord;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.*;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Method;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//@SpringBootTest
//public class TrainingServiceImpTest {
//
//    @InjectMocks
//    private TrainingServiceImp trainingService;
//
//    @MockBean
//    private TrainingRepository trainingRepository;
//
//    @MockBean
//    private TrainingSyllabusRepository trainingSyllabusRepository;
//
//    @MockBean
//    RestTemplate restTemplate;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//
//    @Test
//    public void testGetAllTrainingProgram() {
//        TrainingProgram program1 = new TrainingProgram(1, "Java Programming", "John", LocalDateTime.now(), "Alice", LocalDateTime.now(), "3 months", "123", "Active", "Test data");
//        TrainingProgram program2 = new TrainingProgram(2, "Python Programming", "Alice", LocalDateTime.now(), "Bob", LocalDateTime.now(), "2 months", "456", "Inactive", "Test data");
//        List<TrainingProgram> trainingPrograms = Arrays.asList(program1, program2);
//
//        Mockito.when(trainingRepository.findAll()).thenReturn(trainingPrograms);
//
//        List<TrainingProgram> result = trainingService.getAllTrainingProgram();
//
//        assertEquals(trainingPrograms, result);
//    }
//
//
//    @Test
//    public void testImportTrainingProgramFromFile() throws IOException {
//        String csvData = "trainingId,name,createBy,createDate,modifyBy,modifyDate,startTime,duration,topicId,status\n"
//                + "1,Training1,User1,2023-10-24,User1,2023-10-24,2023-10-24,3 days,Topic1,Active\n"
//                + "2,Training2,User2,2023-10-24,User2,2023-10-24,2023-10-24,5 days,Topic2,Active\n"
//                + "3,Training3,User3,2023-10-24,User3,2023-10-24,2023-10-24,4 days,Topic3,Inactive\n";
//
//        InputStream is = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
//
//        MockMultipartFile file = new MockMultipartFile("file", "training_data.csv", "text/csv", is);
//
//        List<TrainingProgram> trainingProgramList = new ArrayList<>();
//        when(trainingRepository.saveAll(trainingProgramList)).thenReturn(trainingProgramList);
//
//        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(file, is, "UTF-8", ',', "id", "allow");
//
//
//    }
//
//
//    @Test
//    public void testImportTrainingProgramFromFile_WhenFileReadError() throws IOException {
//        BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);
//        when(mockBufferedReader.readLine()).thenThrow(new IOException("Simulated IOException"));
//
//        MockMultipartFile file = new MockMultipartFile("file", "training_data.csv", "text/csv", new byte[0]);
//        InputStream is = mock(InputStream.class);
//        Assertions.assertThrows(RuntimeException.class, () -> {
//            trainingService.importTrainingProgramFromFile(file, is, "UTF-8", ',', "id", "allow");
//        });
//    }
//
//
//    @Test
//    public void testGetAllPagingTrainingProgram() {
//        int page = 0;
//        int size = 10;
//
//        List<TrainingProgram> mockTrainingPrograms = createMockTrainingPrograms();
//
//        Page<TrainingProgram> expectedPage = new PageImpl<>(mockTrainingPrograms);
//        when(trainingRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);
//
//        Page<TrainingProgramDTO> result = trainingService.getAllPagingTrainingProgram(page, size, null);
//
//        verify(trainingRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("trainingId"))));
//
//
//        List<TrainingProgram> expectedList = expectedPage.getContent();
//        List<TrainingProgramDTO> resultList = result.getContent();
//
//        assertEquals(expectedList.size(), resultList.size());
//
//        for (int i = 0; i < expectedList.size(); i++) {
//            assertEquals(expectedList.get(i).getTrainingId(), resultList.get(i).getId());
//            assertEquals(expectedList.get(i).getName(), resultList.get(i).getName());
//        }
//    }
//
//
//    private List<TrainingProgram> createMockTrainingPrograms() {
//        List<TrainingProgram> trainingPrograms = new ArrayList<>();
//        for (int i = 1; i <= 20; i++) {
//            TrainingProgram program = new TrainingProgram();
//            program.setTrainingId(i);
//            program.setName("Training Program " + i);
//            program.setCreateDate(LocalDateTime.now());
//            trainingPrograms.add(program);
//        }
//        return trainingPrograms;
//    }
//
//
////    @Test
////    public void testCreateNewTrainingProgram() {
////        // Tạo đối tượng TrainingProgramDTO với dữ liệu mẫu
////        TrainingProgramDTO newProgramDTO = new TrainingProgramDTO();
////        newProgramDTO.setId(1);
////        newProgramDTO.setName("New Training Program");
////        newProgramDTO.setCreateBy("John Doe");
////        newProgramDTO.setCreateDate(LocalDate.now());
////        newProgramDTO.setModifyBy("g2");
////        newProgramDTO.setModifyDate(LocalDate.now());
////        newProgramDTO.setStartTime(LocalDate.now());
////        newProgramDTO.setDuration(String.valueOf(Duration.ofHours(2)));
////        newProgramDTO.setTopicId(String.valueOf(42));
////        newProgramDTO.setStatus("Inactive");
////
////        // Tạo đối tượng TrainingProgram mẫu
////        TrainingProgram newProgramEntity = new TrainingProgram();
////        when(trainingRepository.save(any(TrainingProgram.class))).thenReturn(newProgramEntity);
////
////        // Tạo danh sách SyllabusRequest mẫu
////        List<SyllabusRequest> syllabusRequests = new ArrayList<>();
////        SyllabusRequest request1 = new SyllabusRequest();
////        request1.setOrder(1);
////        request1.setSyllabusId(101L);
////        syllabusRequests.add(request1);
////
////        SyllabusRequest request2 = new SyllabusRequest();
////        request2.setOrder(2);
////        request2.setSyllabusId(102L);
////        syllabusRequests.add(request2);
////
////        newProgramDTO.setSyllabusRequestList(syllabusRequests);
////
////        // Tạo đối tượng TrainingSyllabus mẫu
////        TrainingSyllabus syllabus = new TrainingSyllabus();
////        when(trainingSyllabusRepository.save(syllabus)).thenReturn(new TrainingSyllabus());
////
////        // Gọi phương thức createNewTrainingProgram
////        int result = trainingService.createNewTrainingProgram(newProgramDTO);
////
////        // Kiểm tra kết quả
////        verify(trainingRepository).save(any(TrainingProgram.class));
////        verify(trainingSyllabusRepository, times(syllabusRequests.size())).save(any(TrainingSyllabus.class));
////
////        TrainingProgram newProgram = new TrainingProgram();
////        when(trainingRepository.save(newProgram)).thenReturn(newProgram);
////
////        int result = trainingService.createNewTrainingProgram(newProgram);
////
////        verify(trainingRepository).save(newProgram);
////        assertEquals(1, result);
////    }
////
////    private TrainingProgram mapToEntity(TrainingProgramDTO trainingProgramDto) {
////        TrainingProgram trainingProgram = TrainingProgram.builder()
////                .trainingId(trainingProgramDto.getId())
////                .name(trainingProgramDto.getName())
////                .createBy(trainingProgramDto.getCreateBy())
////                .createDate(trainingProgramDto.getCreateDate())
////                .modifyBy(trainingProgramDto.getModifyBy())
////                .modifyDate(trainingProgramDto.getModifyDate())
////                .startTime(trainingProgramDto.getStartTime())
////                .duration(trainingProgramDto.getDuration())
////                .topicId(trainingProgramDto.getTopicId())
////                .status(trainingProgramDto.getStatus())
////                .build();
////        return trainingProgram;
////    }
//
//    @Test
//    public void testDeleteTrainingProgram() {
//
//        Integer id = 1;
//        doNothing().when(trainingRepository).deleteById(id);
//
//        int result = trainingService.deleteTrainingProgram(id);
//
//        verify(trainingRepository).deleteById(id);
//        assertEquals(1, result);
//    }
//
//    @Test
//    public void testDeactivateTrainingProgram() {
//
//        Integer id = 1;
//        TrainingProgram program = new TrainingProgram();
//        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
//        when(trainingRepository.save(program)).thenReturn(program);
//
//        trainingService.deactivateTrainingProgram(id);
//
//        verify(trainingRepository).findById(id);
//        verify(trainingRepository).save(program);
//        assertEquals("Inactive", program.getStatus());
//    }
//
//    @Test
//    public void testActivateTrainingProgram() {
//        Integer id = 1;
//        TrainingProgram program = new TrainingProgram();
//        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
//        when(trainingRepository.save(program)).thenReturn(program);
//
//        trainingService.activateTrainingProgram(id);
//
//        verify(trainingRepository).findById(id);
//        verify(trainingRepository).save(program);
//        assertEquals("Active", program.getStatus());
//    }
//
////    @Test
////    public void testUpdateTrainingProgram() {
////        Integer trainingId = 1;
////        TrainingProgram existingProgram = new TrainingProgram();
////        existingProgram.setTrainingId(trainingId);
////        existingProgram.setStatus("Active");
////        existingProgram.setModifyDate(LocalDate.now().minusDays(1));
////        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingProgram));
////        when(trainingRepository.save(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
////
////        TrainingProgram updatedProgram = new TrainingProgram();
////        updatedProgram.setName("Updated Program");
////        updatedProgram.setCreateDate(LocalDate.now());
////        updatedProgram.setCreateBy("John Doe");
////        updatedProgram.setDuration("2 days");
////        updatedProgram.setModifyBy("Jane Smith");
////        updatedProgram.setTopicId("123");
////        updatedProgram.setStartTime(LocalDate.now());
////
////        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
////
////        verify(trainingRepository).findById(trainingId);
////        verify(trainingRepository).save(any(TrainingProgram.class));
////        assertEquals(updatedProgram.getName(), result.getName());
////    }
//
//
////    @Test
////    public void testUpdateTrainingProgram_InvalidStatus() {
////        Integer id = 1;
////        TrainingProgram program = new TrainingProgram();
////        program.setStatus("Inactive");
////        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
////
////        TrainingProgram updatedProgram = new TrainingProgram();
////
////        assertThrows(IllegalStateException.class, () -> trainingService.updateTrainingProgram(id, updatedProgram));
////    }
//
//    //    @Test
////    public void testSearchTrainingProgram() {
////        Integer id = 1;
////        TrainingProgram program = new TrainingProgram();
////        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
////
////        TrainingProgram result = trainingService.searchTrainingProgram(id);
////
////        verify(trainingRepository).findById(id);
////        assertEquals(program, result);
////    }
//    @Test
//    void testSearchTrainingProgram() {
//        TrainingProgram trainingProgram = new TrainingProgram();
//        when(trainingRepository.findById(anyInt())).thenReturn(Optional.of(trainingProgram));
//
//        TrainingProgramDTO result = trainingService.searchTrainingProgram(1);
//
//        verify(trainingRepository, times(1)).findById(anyInt());
//    }
//
//    @Test
//      void testSearchTrainingProgram_whenIdDoesNotExist() {
//        when(trainingRepository.findById(anyInt())).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundContentException.class, () -> trainingService.searchTrainingProgram(1));
//    }
//
////    @Test
////    public void testSearchTrainingProgram_NotFound() {
////        Integer id = 1;
////        when(trainingRepository.findById(id)).thenReturn(Optional.empty());
////
////        TrainingProgram result = trainingService.searchTrainingProgram(id);
////
////        verify(trainingRepository).findById(id);
////        assertNull(result);
////    }
//
//    @Test
//    public void testExistsTrainingProgramById() {
//        Integer id = 1;
//        when(trainingRepository.existsById(id)).thenReturn(true);
//
//        boolean exists = trainingService.existsTrainingProgramById(id);
//
//        verify(trainingRepository).existsById(id);
//        assertTrue(exists);
//    }
//
//
////    @Test
////    public void testSearchTrainingProgramWithKeyword() {
////        // Mock dữ liệu
////        String keyword = "Java";
////        List<TrainingProgram> trainingPrograms = new ArrayList<>();
////        TrainingProgram program1 = new TrainingProgram();
////        program1.setTrainingId(1);
////        program1.setName("Java Programming");
////        trainingPrograms.add(program1);
////
////        TrainingProgram program2 = new TrainingProgram();
////        program2.setTrainingId(2);
////        program2.setName("Advanced Java");
////        trainingPrograms.add(program2);
////
////        Pageable pageable = PageRequest.of(0, 10);
////        Page<TrainingProgram> page = new PageImpl<>(trainingPrograms);
////
////        // Khi gọi findByNameContaining, trả về trang của các chương trình đào tạo chứa từ khoá
////        doReturn(page).when(trainingRepository).findByNameContaining(keyword, pageable);
////
////        // Gọi phương thức searchTrainingProgramWithKeyword
////        Page<TrainingProgram> result = trainingService.searchTrainingProgramWithKeyword(keyword, pageable);
////
////        // Kiểm tra xem số lượng bản ghi trả về có đúng không
////        assertEquals(result.getTotalElements(), 2);
////    }
//
//
//    @Test
//    public void testDuplicateTrainingProgram() {
//        Integer id = 1;
//
//        TrainingProgram program = new TrainingProgram(1, "Java Programming", "John", LocalDateTime.now(), "Alice", LocalDateTime.now(), "3 months", "123", "Drafting", "Test data");
//
//        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
//        when(trainingRepository.findAllByOrderByTrainingIdDesc()).thenReturn(Collections.singletonList(program));
//
//        TrainingProgram duplicatedProgram = trainingService.duplicateTrainingProgram(id);
//
//        assertNotNull(duplicatedProgram);
//        assertEquals(program.getName(), duplicatedProgram.getName());
//        assertEquals(program.getCreateBy(), duplicatedProgram.getCreateBy());
//        assertEquals(program.getCreateDate(), duplicatedProgram.getCreateDate());
//        assertEquals(program.getModifyBy(), duplicatedProgram.getModifyBy());
//        assertEquals(program.getModifyDate(), duplicatedProgram.getModifyDate());
//        assertEquals(program.getTopicId(), duplicatedProgram.getTopicId());
//        assertEquals(program.getDuration(), duplicatedProgram.getDuration());
//        assertEquals(program.getStatus(), duplicatedProgram.getStatus());
//    }
//
////    @Test
////    public void testDuplicateTrainingProgramWithNonExistingId() {
////        Integer id = 1;
////
////        when(trainingRepository.findById(id)).thenReturn(Optional.empty());
////
////        TrainingProgram duplicatedProgram = trainingService.duplicateTrainingProgram(id);
////
////        assertNull(duplicatedProgram);
////    }
//
//
//    @Test
//    public void testGetNextTrainingProgramIdWithEmptyList() {
//        when(trainingRepository.findAllByOrderByTrainingIdDesc()).thenReturn(Collections.emptyList());
//
//        int nextId = trainingService.getNextTrainingProgramId();
//
//        assertEquals(1, nextId);
//    }
//
//
//    @Test
//    public void testImportTrainingProgramFromFile_WhenDuplicateHandlingIsAllow() throws IOException {
//        String csvData = "trainingId,name,createBy,createDate,modifyBy,modifyDate,startTime,duration,topicId,status\n"
//                + "1,Training1,User1,2023-10-24,User1,2023-10-24,2023-10-24,3 days,Topic1,Active\n"
//                + "2,Training2,User2,2023-10-24,User2,2023-10-24,2023-10-24,5 days,Topic2,Active\n"
//                + "3,Training3,User3,2023-10-24,User3,2023-10-24,2023-10-24,4 days,Topic3,Inactive\n";
//
//        InputStream is = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
//
//        MockMultipartFile file = new MockMultipartFile("file", "training_data.csv", "text/csv", is);
//
//        List<TrainingProgram> existingPrograms = new ArrayList<>();
//        when(trainingRepository.saveAll(existingPrograms)).thenReturn(existingPrograms);
//
//        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(file, is, "UTF-8", ',', "id", "allow");
//
//        assertEquals(existingPrograms, importedPrograms);
//    }
//
//    @Test
//    void testImportTrainingProgramFromFile_skipExistingProgram() throws IOException {
//        // Arrange
//        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/plain", "1,2023-10-24,2023-10-24,2023-10-24,ProgramName,createBy,modifyBy,60,topicId,status".getBytes());
//        String encoding = "UTF-8";
//        char columnSeparator = ',';
//        String scanningMethod = "id";
//        String duplicateHandling = "skip";
//
//        int existingProgramId = 1;
//
//        when(trainingRepository.findById(existingProgramId)).thenReturn(Optional.of(new TrainingProgram()));
//
//        // Act
//        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(file, file.getInputStream(), encoding, columnSeparator, scanningMethod, duplicateHandling);
//
//        // Assert
//        assertEquals(0, importedPrograms.size()); // Should skip existing program
//    }
//
//
//    @Test
//    public void testImportTrainingProgramFromFile_WhenNoExistingPrograms_AllowDuplicates() throws IOException {
//
//        String csvData = "1,2023-10-24,2023-10-24,2023-10-24,ProgramName,createBy,modifyBy,60,topicId,status";
//        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/plain", csvData.getBytes());
//        when(trainingRepository.findById(1)).thenReturn(Optional.empty());
//        when(trainingRepository.findByName("ProgramName")).thenReturn(List.of());
//
//
//        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(file, file.getInputStream(), "UTF-8", ',', "id", "allow");
//
//
//        for (TrainingProgram program : importedPrograms) {
//            System.out.println("Imported program: " + program.toString());
//        }
//
//    }
//
////    @Test
////    public void testCreateNewTrainingProgram_Success() {
////        TrainingProgram trainingProgram = new TrainingProgram();
////        trainingProgram.setTrainingId(1);
////        trainingProgram.setName("Training1");
////        Mockito.when(trainingRepository.save(trainingProgram)).thenReturn(trainingProgram);
////        int result = trainingService.createNewTrainingProgram(trainingProgram);
////        assertEquals(1, result);
////    }
////
////    @Test
////    public void testCreateNewTrainingProgram_Failure() {
////        TrainingProgram trainingProgram = new TrainingProgram();
////        trainingProgram.setTrainingId(1);
////        trainingProgram.setName("Training1");
////        Mockito.when(trainingRepository.save(trainingProgram)).thenThrow(new RuntimeException("Save operation failed"));
////        int result = trainingService.createNewTrainingProgram(trainingProgram);
////        assertEquals(0, result);
////    }
//
//
//    @Test
//    public void testDeleteTrainingProgram_Success() {
//        Integer idToDelete = 1;
//        Mockito.doNothing().when(trainingRepository).deleteById(idToDelete);
//        int result = trainingService.deleteTrainingProgram(idToDelete);
//        assertEquals(1, result);
//    }
//
//    @Test
//    public void testDeleteTrainingProgram_Failure() {
//        Integer idToDelete = 1;
//        Mockito.doThrow(new RuntimeException("Delete operation failed")).when(trainingRepository).deleteById(idToDelete);
//
//        int result = trainingService.deleteTrainingProgram(idToDelete);
//
//        assertEquals(0, result);
//    }
//
//
//    @Test
//    public void testDeactivateTrainingProgram_Success() {
//        Integer trainingId = 1;
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setStatus("Active");
//
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
//        Mockito.when(trainingRepository.save(Mockito.any())).thenReturn(trainingProgram);
//
//        trainingService.deactivateTrainingProgram(trainingId);
//
//        assertEquals("Inactive", trainingProgram.getStatus());
//    }
//
//    @Test
//    public void testDeactivateTrainingProgram_NotFound() {
//        Integer trainingId = 1;
//
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundContentException.class, () -> trainingService.deactivateTrainingProgram(trainingId));
//    }
//
//    @Test
//    public void testActivateTrainingProgram_Success() {
//        Integer trainingId = 1;
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setStatus("Inactive");
//
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
//        Mockito.when(trainingRepository.save(Mockito.any())).thenReturn(trainingProgram);
//
//        trainingService.activateTrainingProgram(trainingId);
//
//        assertEquals("Active", trainingProgram.getStatus());
//    }
//
//    @Test
//    public void testActivateTrainingProgram_NotFound() {
//        Integer trainingId = 1;
//
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundContentException.class, () -> trainingService.activateTrainingProgram(trainingId));
//    }
//
////    @Test
////    public void testUpdateTrainingProgram_Success() {
////        Integer trainingId = 1;
////        TrainingProgram trainingProgram = new TrainingProgram();
////        trainingProgram.setStatus("Active");
////
////        TrainingProgram updatedProgram = new TrainingProgram();
////        updatedProgram.setName("Updated Program");
////        // Set other properties of updatedProgram
////
////        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
////        Mockito.when(trainingRepository.save(Mockito.any())).thenReturn(updatedProgram);
////
////        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
////
////        assertEquals(updatedProgram.getName(), result.getName());
////        // Assert other properties of the updated program
////    }
//
////    @Test
////    public void testUpdateTrainingProgram_NotFound() {
////        Integer trainingId = 1;
////
////        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());
////
////        assertThrows(EntityNotFoundException.class, () -> trainingService.updateTrainingProgram(trainingId, new TrainingProgram()));
////    }
//
//    //    @Test
////    public void testUpdateTrainingProgram_Inactive() {
////        Integer trainingId = 1;
////        TrainingProgram trainingProgram = new TrainingProgram();
////        trainingProgram.setStatus("Inactive");
////
////        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
////
////        assertThrows(IllegalStateException.class, () -> trainingService.updateTrainingProgram(trainingId, new TrainingProgram()));
////    }
////    @Test
////    public void testUpdateTrainingProgramInvalidModifyDate() {
////        // Create data for testing
////        Integer trainingId = 1;
////        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
////        // Set properties of updatedProgram as needed
////        // ...
////        TrainingProgram existingTrainingProgram = new TrainingProgram();
////        existingTrainingProgram.setStatus("Active");
////        existingTrainingProgram.setModifyDate(LocalDate.now().plusDays(1)); // Modify date is in the future
////        // Set other properties of existingTrainingProgram as needed
////        // ...
////
////        // Mock the behavior of the trainingRepository when the modify date is in the future
////        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));
////
////        // Perform the test
////        assertThrows(IllegalStateException.class, () -> {
////            trainingService.updateTrainingProgram(trainingId, updatedProgram);
////        });
////    }
//
//
//    @Test
//    public void testUpdateTrainingProgramInactiveStatus() {
//        // Create data for testing
//        Integer trainingId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//        // Set properties of updatedProgram as needed
//        // ...
//        TrainingProgram existingTrainingProgram = new TrainingProgram();
//        existingTrainingProgram.setStatus("Inactive");
//        // Set other properties of existingTrainingProgram as needed
//        // ...
//
//        // Mock the behavior of the trainingRepository when the status is "Inactive"
//        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));
//
//        // Perform the test
//        assertThrows(IllegalStateException.class, () -> {
//            trainingService.updateTrainingProgram(trainingId, updatedProgram);
//        });
//    }
//
//    @Test
//    public void testUpdateTrainingProgramTrainingNotFound() {
//
//        Integer trainingId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//
//
//        // Mock the behavior of the trainingRepository when the training is not found
//        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());
//
//        // Perform the test
//        assertThrows(EntityNotFoundException.class, () -> {
//            trainingService.updateTrainingProgram(trainingId, updatedProgram);
//        });
//    }
//
//    @Test
//    void testUpdateTrainingProgramUpdateName() {
//        // Mock data
//        Integer trainingId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//        updatedProgram.setName("New Training Name");
//
//        TrainingProgram existingTrainingProgram = new TrainingProgram();
//        existingTrainingProgram.setStatus("Active");
//        existingTrainingProgram.setTrainingId(trainingId);
//
//        // Mock repository behavior
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));
//        Mockito.when(trainingRepository.save(any(TrainingProgram.class))).thenReturn(existingTrainingProgram);
//
//        // Perform the update
//        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
//
//        // Assertions
//        assertNotNull(result);
//        assertEquals(updatedProgram.getName(), result.getName());
//    }
//
//    @Test
//    void testUpdateTrainingProgramUpdateDuration() {
//        // Mock data
//        Integer trainingId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//        updatedProgram.setDuration("New duration");
//
//        TrainingProgram existingTrainingProgram = new TrainingProgram();
//        existingTrainingProgram.setStatus("Active");
//        existingTrainingProgram.setTrainingId(trainingId);
//
//        // Mock repository behavior
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));
//        Mockito.when(trainingRepository.save(any(TrainingProgram.class))).thenReturn(existingTrainingProgram);
//
//        // Perform the update
//        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
//
//        // Assertions
//        assertNotNull(result);
//        assertEquals(updatedProgram.getDuration(), result.getDuration());
//    }
//
//    @Test
//    void testUpdateTrainingProgramUpdateModifyBy() {
//        // Mock data
//        Integer trainingId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//        updatedProgram.setModifyBy("New ModifyBy");
//
//        TrainingProgram existingTrainingProgram = new TrainingProgram();
//        existingTrainingProgram.setStatus("Active");
//        existingTrainingProgram.setTrainingId(trainingId);
//
//        // Mock repository behavior
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));
//        Mockito.when(trainingRepository.save(any(TrainingProgram.class))).thenReturn(existingTrainingProgram);
//
//        // Perform the update
//        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
//
//        // Assertions
//        assertNotNull(result);
//        assertEquals(updatedProgram.getModifyBy(), result.getModifyBy());
//    }
//
//    @Test
//    void testUpdateTrainingProgramUpdateTopicId() {
//        // Mock data
//        Integer trainingId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//        updatedProgram.setTopicId("New TopicId");
//
//        TrainingProgram existingTrainingProgram = new TrainingProgram();
//        existingTrainingProgram.setStatus("Active");
//        existingTrainingProgram.setTrainingId(trainingId);
//
//        // Mock repository behavior
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));
//        Mockito.when(trainingRepository.save(any(TrainingProgram.class))).thenReturn(existingTrainingProgram);
//
//        // Perform the update
//        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
//
//        // Assertions
//        assertNotNull(result);
//        assertEquals(updatedProgram.getTopicId(), result.getTopicId());
//    }
//
//    @Test
//    void testUpdateTrainingProgramUpdateInfo() {
//        // Mock data
//        Integer trainingId = 1;
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//        updatedProgram.setInfo("New Info");
//
//        TrainingProgram existingTrainingProgram = new TrainingProgram();
//        existingTrainingProgram.setStatus("Active");
//        existingTrainingProgram.setTrainingId(trainingId);
//
//        // Mock repository behavior
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));
//        Mockito.when(trainingRepository.save(any(TrainingProgram.class))).thenReturn(existingTrainingProgram);
//
//        // Perform the update
//        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
//
//        // Assertions
//        assertNotNull(result);
//        assertEquals(updatedProgram.getInfo(), result.getInfo());
//    }
//
//
//    @Test
//    public void testHasCSVFormatWithCSVFile() {
//        MockMultipartFile csvFile = new MockMultipartFile("file", "training_data.csv", "text/csv", new byte[0]);
//
//        boolean result = trainingService.hasCSVFormat(csvFile);
//
//        assertTrue(result);
//    }
//
//    @Test
//    public void testHasCSVFormatWithNonCSVFile() {
//        MockMultipartFile nonCSVFile = new MockMultipartFile("file", "training_data.txt", "text/plain", new byte[0]);
//
//        boolean result = trainingService.hasCSVFormat(nonCSVFile);
//
//        assertFalse(result);
//    }
//
//
////    @Test
////    public void testImportTrainingProgramFromFile_AllowDuplicateById() throws IOException {
////        File csvFile = new File("D:\\CV\\MOCK_DATA-BASE.csv");
////        FileInputStream fileInputStream = new FileInputStream(csvFile);
////        MultipartFile multipartFile = new MockMultipartFile("MOCK_DATA-BASE.csv", "MOCK_DATA-BASE.csv", "MOCK_DATA-BASE/csv", fileInputStream);
////
////        when(trainingRepository.findById(1)).thenReturn(Optional.of(new TrainingProgram()));
////        when(trainingRepository.findById(2)).thenReturn(Optional.empty());
////
////        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");
////
////        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(
////                multipartFile,
////                multipartFile.getInputStream(),
////                "UTF-8",
////                ',',
////                "id",
////                "allow"
////        );
////
////        assertEquals(10, importedPrograms.size());
////        verify(trainingRepository, times(0)).save(any(TrainingProgram.class));
////        verify(trainingRepository, times(2)).save(any(TrainingProgram.class));
////    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_ReplaceById() {
//        // Test trường hợp import và thay thế dựa trên ID
//        // Đảm bảo rằng chương trình đào tạo đã tồn tại với ID đã cho sẽ được thay thế bằng dữ liệu từ CSV.
//    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_AllowDuplicateByName() {
//        // Test trường hợp import cho phép trùng tên chương trình đào tạo
//        // Bạn cần tạo dữ liệu CSV với các tên chương trình trùng nhau và kiểm tra xem liệu chúng có được thêm vào không.
//    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_ReplaceByName() {
//        // Test trường hợp import và thay thế dựa trên tên chương trình đào tạo
//        // Đảm bảo rằng chương trình đào tạo đã tồn tại với tên đã cho sẽ được thay thế bằng dữ liệu từ CSV.
//    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_AllowDuplicateByIdAndName() {
//        // Test trường hợp import cho phép trùng cả ID và tên chương trình đào tạo
//        // Bạn cần tạo dữ liệu CSV sao cho ID và tên đều trùng và kiểm tra xem liệu chúng có được thêm vào không.
//    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_ReplaceByIdAndName() {
//        // Test trường hợp import và thay thế dựa trên cả ID và tên chương trình đào tạo
//        // Đảm bảo rằng chương trình đào tạo đã tồn tại với cả ID và tên đã cho sẽ được thay thế bằng dữ liệu từ CSV.
//    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_SkipDuplicateById() {
//        // Test trường hợp import và bỏ qua dựa trên ID
//        // Đảm bảo rằng chương trình đào tạo đã tồn tại với ID đã cho sẽ không bị thêm vào.
//    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_SkipDuplicateByName() {
//        // Test trường hợp import và bỏ qua dựa trên tên chương trình đào tạo
//        // Đảm bảo rằng chương trình đào tạo đã tồn tại với tên đã cho sẽ không bị thêm vào.
//    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_SkipDuplicateByIdAndName() {
//        // Test trường hợp import và bỏ qua dựa trên cả ID và tên chương trình đào tạo
//        // Đảm bảo rằng chương trình đào tạo đã tồn tại với cả ID và tên đã cho sẽ không bị thêm vào.
//    }
//
//    @Test
//    public void testImportTrainingProgramFromFile_ExceptionHandling() {
//        // Test trường hợp xử lý ngoại lệ khi có lỗi trong quá trình import từ CSV
//        // Đảm bảo rằng ngoại lệ được xử lý đúng cách và thông báo lỗi được đưa ra.
//    }
//
//
//    @Test
//    public void testGetSortedTrainingProgram() {
//        // Mock the behavior of the trainingRepository
//        Page<TrainingProgram> mockTrainingProgramPage = createMockTrainingProgramPage();
//        when(trainingRepository.findAll(any(PageRequest.class))).thenReturn(mockTrainingProgramPage);
//
//        // Call the method to be tested
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<TrainingProgram> result = trainingService.getSortedTrainingProgram("fieldName", "ASC", pageable);
//
//        // Verify the method's behavior
//        assertThat(result).isNotNull();
//
//        // Verify that the repository method was called with the correct arguments
//        verify(trainingRepository).findAll(any(PageRequest.class));
//    }
//
//    private Page<TrainingProgram> createMockTrainingProgramPage() {
//        // Create a mock Page of TrainingPrograms for testing
//        List<TrainingProgram> trainingProgramList = new ArrayList<>();
//
//        // Add TrainingProgram objects to the list for testing
//        TrainingProgram trainingProgram1 = new TrainingProgram();
//        trainingProgram1.setTrainingId(1);
//        trainingProgram1.setName("Program 1");
//        trainingProgram1.setDuration("Description for Program 1");
//
//        trainingProgramList.add(trainingProgram1);
//
//
//        return new PageImpl<>(trainingProgramList);
//    }
//
//    @Test
//    void testCreateNewTrainingProgram_Success() {
//        // Arrange
//        TrainingProgramDTO mockTrainingProgramDTO = new TrainingProgramDTO();
//        mockTrainingProgramDTO.setSyllabusRequestList(new ArrayList<>());
//        TrainingProgram mockTrainingProgram = new TrainingProgram();
//
//        // Act
//        when(trainingRepository.save(any(TrainingProgram.class))).thenReturn(mockTrainingProgram);
//        int result = trainingService.createNewTrainingProgram(mockTrainingProgramDTO);
//
//        // Assert
//        assertEquals(1, result);
//        verify(trainingRepository, times(1)).save(any(TrainingProgram.class));
//    }
//
//
////    @Test
////    public void testCreateNewTrainingProgram_Failure() {
////        // Arrange
////        TrainingProgramDTO requestBody = new TrainingProgramDTO();
////        // Set any required properties in the requestBody
////
////        when(trainingRepository.save(any(TrainingProgram.class))).thenThrow(new RuntimeException("Error"));
////
////        // Act
////        int result = trainingService.createNewTrainingProgram(requestBody);
////
////        // Assert
////        assertEquals(0, result);
////    }
//
//    @Test
//    void testSearchTrainingProgramWithKeyword() {
//        // Arrange
//        String name = "test";
//        Pageable pageable = PageRequest.of(0, 10);
//        TrainingProgram mockTrainingProgram = new TrainingProgram();
//        Page<TrainingProgram> mockPage = new PageImpl<>(List.of(mockTrainingProgram));
//
//        // Act
//        when(trainingRepository.findByNameContaining(name, pageable)).thenReturn(mockPage);
//        Page<TrainingProgramDTO> result = trainingService.searchTrainingProgramWithKeyword(name, pageable);
//
//        // Assert
//        assertEquals(1, result.getContent().size());
//        verify(trainingRepository, times(1)).findByNameContaining(name, pageable);
//    }
//
//    @Test
//    void testSearchTrainingProgramWithKeywordNoContentFound() {
//        String keyword = "No content";
//        Page<TrainingProgram> trainingProgramPage = Page.empty();
//
//        Mockito.when(trainingRepository.findByNameContaining(eq(keyword), any(Pageable.class)))
//                .thenReturn(trainingProgramPage);
//
//        assertThrows(NotFoundContentException.class, () -> {
//            trainingService.searchTrainingProgramWithKeyword(keyword, Pageable.unpaged());
//        });
//    }
//
//    @Test
//    void testSearchTrainingProgramWithKeyword_Exception() {
//        // Arrange
//        String name = "test";
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // Act
//        when(trainingRepository.findByNameContaining(name, pageable)).thenThrow(new RuntimeException("Test exception"));
//
//        // Assert
//        assertThrows(RuntimeException.class, () -> trainingService.searchTrainingProgramWithKeyword(name, pageable));
//        verify(trainingRepository, times(1)).findByNameContaining(name, pageable);
//    }
//
//    @Test
//    void testIsSyllabusIdExist_Success() {
//        // Arrange
//        Long syllabusId = 1L;
//        String url = "http://syllabus-service-env.eba-pifngxja.ap-northeast-1.elasticbeanstalk.com/syllabus-service/syllabus/find-by-id/" + syllabusId;
//        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);
//
//        // Act
//        when(restTemplate.getForEntity(url, Object.class)).thenReturn(responseEntity);
//        boolean result = trainingService.isSyllabusIdExist(syllabusId);
//
//        // Assert
//        assertTrue(result);
//        verify(restTemplate, times(1)).getForEntity(url, Object.class);
//    }
//
//    @Test
//    void testIsSyllabusIdExist_NotFound() {
//        // Arrange
//        Long syllabusId = 1L;
//        String url = "http://syllabus-service-env.eba-pifngxja.ap-northeast-1.elasticbeanstalk.com/syllabus-service/syllabus/find-by-id/" + syllabusId;
//        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
//
//        // Act
//        when(restTemplate.getForEntity(url, Object.class)).thenReturn(responseEntity);
//        boolean result = trainingService.isSyllabusIdExist(syllabusId);
//
//        // Assert
//        assertFalse(result);
//        verify(restTemplate, times(1)).getForEntity(url, Object.class);
//    }
//
//    @Test
//    void testIsSyllabusIdExist_Exception() {
//        // Arrange
//        Long syllabusId = 1L;
//        String url = "http://syllabus-service-env.eba-pifngxja.ap-northeast-1.elasticbeanstalk.com/syllabus-service/syllabus/find-by-id/" + syllabusId;
//
//        // Act
//        when(restTemplate.getForEntity(url, Object.class)).thenThrow(new RestClientException("Test exception"));
//
//        // Assert
//        assertFalse(trainingService.isSyllabusIdExist(syllabusId));
//        verify(restTemplate, times(1)).getForEntity(url, Object.class);
//    }
//
//    @Test
//    void testAddOrUpdateSyllabusId_NotFound() {
//        // Arrange
//        Integer trainingId = 1;
//        List<SyllabusRequest> syllabusRequests = new ArrayList<>();
//
//        // Act
//        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());
//
//        // Assert
//        assertThrows(EntityNotFoundException.class, () -> trainingService.addOrUpdateSyllabusId(trainingId, syllabusRequests));
//        verify(trainingRepository, times(1)).findById(trainingId);
//    }
//
//    @Test
//    void testAddOrUpdateSyllabusId_EmptyList() {
//        // Arrange
//        Integer trainingId = 1;
//        List<SyllabusRequest> syllabusRequests = new ArrayList<>();
//        TrainingProgram mockTrainingProgram = new TrainingProgram();
//
//        // Act
//        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(mockTrainingProgram));
//        doNothing().when(trainingSyllabusRepository).deleteByTrainingProgram(mockTrainingProgram);
//
//        // Assert
//        assertTrue(trainingService.addOrUpdateSyllabusId(trainingId, syllabusRequests));
//        verify(trainingRepository, times(1)).findById(trainingId);
//        verify(trainingSyllabusRepository, times(1)).deleteByTrainingProgram(mockTrainingProgram);
//    }
//
////    @Test
////    void testAddOrUpdateSyllabusId_ValidSyllabusRequests() {
////        // Arrange
////        Integer trainingId = 1;
////        SyllabusRequest mockSyllabusRequest = new SyllabusRequest();
////        List<SyllabusRequest> syllabusRequests = new ArrayList<>(List.of(mockSyllabusRequest));
////        TrainingProgram mockTrainingProgram = new TrainingProgram();
////
////        // Create a spy for the service
////        TrainingServiceImp spyService = spy(trainingService);
////
////        // Act
////        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(mockTrainingProgram));
////        doNothing().when(trainingSyllabusRepository).deleteByTrainingProgram(mockTrainingProgram);
////        when(trainingSyllabusRepository.save(any(TrainingSyllabus.class))).thenReturn(new TrainingSyllabus());
////        when(spyService.isSyllabusIdExist(any(Long.class))).thenReturn(true);
////
////        // Assert
////        assertTrue(spyService.addOrUpdateSyllabusId(trainingId, syllabusRequests));
////        verify(trainingRepository, times(1)).findById(trainingId);
////        verify(trainingSyllabusRepository, times(1)).deleteByTrainingProgram(mockTrainingProgram);
////        verify(trainingSyllabusRepository, times(1)).save(any(TrainingSyllabus.class));
////    }
////
////    @Test
////    void testAddOrUpdateSyllabusId_InvalidSyllabusRequest() {
////        // Arrange
////        Integer trainingId = 1;
////        SyllabusRequest mockSyllabusRequest = new SyllabusRequest();
////        List<SyllabusRequest> syllabusRequests = new ArrayList<>(List.of(mockSyllabusRequest));
////        TrainingProgram mockTrainingProgram = new TrainingProgram();
////
////        // Create a spy for the service
////        TrainingServiceImp spyService = spy(trainingService);
////
////        // Act
////        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(mockTrainingProgram));
////        doNothing().when(trainingSyllabusRepository).deleteByTrainingProgram(mockTrainingProgram);
////        when(spyService.isSyllabusIdExist(any(Long.class))).thenReturn(false);
////
////        // Assert
////        assertThrows(EntityNotFoundException.class, () -> spyService.addOrUpdateSyllabusId(trainingId, syllabusRequests));
////        verify(trainingRepository, times(1)).findById(trainingId);
////        verify(trainingSyllabusRepository, times(1)).deleteByTrainingProgram(mockTrainingProgram);
////    }
//
////    @Test
////    void testGetImportData() throws Exception {
////        // Arrange
////        CSVRecord csvRecord = mock(CSVRecord.class);
////        when(csvRecord.get("createBy")).thenReturn("createBy");
////        when(csvRecord.get("modifyBy")).thenReturn("modifyBy");
////        when(csvRecord.get("duration")).thenReturn("duration");
////        when(csvRecord.get("topicId")).thenReturn("topicId");
////        when(csvRecord.get("status")).thenReturn("status");
////        when(csvRecord.get("info")).thenReturn("info");
////
////        LocalDate createDate = LocalDate.now();
////        LocalDate modifyDate = LocalDate.now();
////        String name = "name";
////
////        TrainingProgram newProgram = new TrainingProgram();
////
////        // Use reflection to access the private method
////        Method getImportDataMethod = TrainingServiceImp.class.getDeclaredMethod("getImportData", CSVRecord.class, LocalDate.class, LocalDate.class, String.class, TrainingProgram.class);
////        getImportDataMethod.setAccessible(true);
////
////        // Act
////        getImportDataMethod.invoke(trainingService, csvRecord, createDate, modifyDate, name, newProgram);
////
////        // Assert
////        verify(trainingRepository, times(1)).save(newProgram);
////        assertEquals("name", newProgram.getName());
////        assertEquals("createBy", newProgram.getCreateBy());
////        assertEquals(createDate, newProgram.getCreateDate());
////        assertEquals("modifyBy", newProgram.getModifyBy());
////        assertEquals(modifyDate, newProgram.getModifyDate());
////        assertEquals("duration", newProgram.getDuration());
////        assertEquals("topicId", newProgram.getTopicId());
////        assertEquals("status", newProgram.getStatus());
////        assertEquals("info", newProgram.getInfo());
////    }
//
//
//    @Test
//    void testGetSyllabusIdListByTrainingId_whenIdExists() {
//        TrainingProgram trainingProgram = new TrainingProgram();
//        TrainingSyllabus trainingSyllabus1 = new TrainingSyllabus();
//        TrainingSyllabus trainingSyllabus2 = new TrainingSyllabus();
//        trainingSyllabus1.setSyllabusId(1L);
//        trainingSyllabus2.setSyllabusId(2L);
//
//        when(trainingRepository.findById(anyInt())).thenReturn(Optional.of(trainingProgram));
//        when(trainingSyllabusRepository.findByTrainingProgram(any(TrainingProgram.class))).thenReturn(Arrays.asList(trainingSyllabus1, trainingSyllabus2));
//
//        List<Long> result = trainingService.getSyllabusIdListByTrainingId(1);
//
//        assertTrue(result.contains(1L));
//        assertTrue(result.contains(2L));
//    }
//
//    @Test
//    void testGetSyllabusIdListByTrainingId_whenIdDoesNotExist() {
//        when(trainingRepository.findById(anyInt())).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> trainingService.getSyllabusIdListByTrainingId(1));
//    }
//
//    @Test
//    void testSaveToTrainingSyllabusSuccess() {
//        // Mock data
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setTrainingId(1);
//
//        Set<SyllabusRequest> uniqueSyllabusRequests = new HashSet<>();
//        uniqueSyllabusRequests.add(new SyllabusRequest(1, 1L));
//
//        TrainingServiceImp spyService = spy(trainingService);
//
//        doReturn(true).when(spyService).isSyllabusIdExist(1L);
//
//        spyService.saveToTrainingSyllabus(trainingProgram, uniqueSyllabusRequests);
//
//        Mockito.verify(trainingSyllabusRepository, Mockito.times(uniqueSyllabusRequests.size()))
//                .save(any(TrainingSyllabus.class));
//    }
//
//    @Test
//    void testSaveToTrainingSyllabusSyllabusIdNotExist() {
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setTrainingId(1);
//
//        Set<SyllabusRequest> uniqueSyllabusRequests = new HashSet<>();
//        uniqueSyllabusRequests.add(new SyllabusRequest(1, 2L));
//
//        TrainingServiceImp spyService = spy(trainingService);
//
//        doReturn(false).when(spyService).isSyllabusIdExist(2L);
//
//        assertThrows(EntityNotFoundException.class, () -> {
//            spyService.saveToTrainingSyllabus(trainingProgram, uniqueSyllabusRequests);
//        });
//
//        Mockito.verify(trainingSyllabusRepository, Mockito.never()).save(any(TrainingSyllabus.class));
//    }
//
//
//
//
//}
