package com.fams.training.service.Imp;

import com.fams.training.DTO.SyllabusRequest;
import com.fams.training.DTO.TrainingProgramDTO;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.entity.TrainingSyllabus;
import com.fams.training.exception.EntityNotFoundException;
import com.fams.training.repository.TrainingRepository;
import com.fams.training.repository.TrainingSyllabusRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
public class TrainingServiceImpTest {

    @InjectMocks
    private TrainingServiceImp trainingService;

    @MockBean
    private TrainingRepository trainingRepository;
    private TrainingSyllabusRepository trainingSyllabusRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetAllTrainingProgram() {
        TrainingProgram program1 = new TrainingProgram(1, "Java Programming", "John", LocalDate.now(), "Alice", LocalDate.now(), LocalDate.now(), "3 months", "123", "Active");
        TrainingProgram program2 = new TrainingProgram(2, "Python Programming", "Alice", LocalDate.now(), "Bob", LocalDate.now(), LocalDate.now(), "2 months", "456", "Inactive");
        List<TrainingProgram> trainingPrograms = Arrays.asList(program1, program2);

        Mockito.when(trainingRepository.findAll()).thenReturn(trainingPrograms);

        List<TrainingProgram> result = trainingService.getAllTrainingProgram();

        assertEquals(trainingPrograms, result);
    }


    @Test
    public void testImportTrainingProgramFromFile() throws IOException {
        String csvData = "trainingId,name,createBy,createDate,modifyBy,modifyDate,startTime,duration,topicId,status\n"
                + "1,Training1,User1,2023-10-24,User1,2023-10-24,2023-10-24,3 days,Topic1,Active\n"
                + "2,Training2,User2,2023-10-24,User2,2023-10-24,2023-10-24,5 days,Topic2,Active\n"
                + "3,Training3,User3,2023-10-24,User3,2023-10-24,2023-10-24,4 days,Topic3,Inactive\n";

        InputStream is = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile file = new MockMultipartFile("file", "training_data.csv", "text/csv", is);

        List<TrainingProgram> trainingProgramList = new ArrayList<>();
        when(trainingRepository.saveAll(trainingProgramList)).thenReturn(trainingProgramList);

        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(file, is, "UTF-8", ',', "id", "allow");


    }


    @Test
    public void testImportTrainingProgramFromFile_WhenFileReadError() throws IOException {
        BufferedReader mockBufferedReader = Mockito.mock(BufferedReader.class);
        when(mockBufferedReader.readLine()).thenThrow(new IOException("Simulated IOException"));

        MockMultipartFile file = new MockMultipartFile("file", "training_data.csv", "text/csv", new byte[0]);
        InputStream is = mock(InputStream.class);
        Assertions.assertThrows(RuntimeException.class, () -> {
            trainingService.importTrainingProgramFromFile(file, is, "UTF-8", ',', "id", "allow");
        });
    }


    @Test
    public void testGetAllPagingTrainingProgram() {
        int page = 0;
        int size = 10;

        List<TrainingProgram> mockTrainingPrograms = createMockTrainingPrograms();

        Page<TrainingProgram> expectedPage = new PageImpl<>(mockTrainingPrograms);
        when(trainingRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<TrainingProgramDTO> result = trainingService.getAllPagingTrainingProgram(page, size);

        verify(trainingRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Order.desc("createDate"))));


        List<TrainingProgram> expectedList = expectedPage.getContent();
        List<TrainingProgramDTO> resultList = result.getContent();

        assertEquals(expectedList.size(), resultList.size());

        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i).getTrainingId(), resultList.get(i).getId());
            assertEquals(expectedList.get(i).getName(), resultList.get(i).getName());
        }
    }


    private List<TrainingProgram> createMockTrainingPrograms() {
        List<TrainingProgram> trainingPrograms = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            TrainingProgram program = new TrainingProgram();
            program.setTrainingId(i);
            program.setName("Training Program " + i);
            program.setCreateDate(LocalDate.now());
            trainingPrograms.add(program);
        }
        return trainingPrograms;
    }


//    @Test
//    public void testCreateNewTrainingProgram() {
//        // Tạo đối tượng TrainingProgramDTO với dữ liệu mẫu
//        TrainingProgramDTO newProgramDTO = new TrainingProgramDTO();
//        newProgramDTO.setId(1);
//        newProgramDTO.setName("New Training Program");
//        newProgramDTO.setCreateBy("John Doe");
//        newProgramDTO.setCreateDate(LocalDate.now());
//        newProgramDTO.setModifyBy("g2");
//        newProgramDTO.setModifyDate(LocalDate.now());
//        newProgramDTO.setStartTime(LocalDate.now());
//        newProgramDTO.setDuration(String.valueOf(Duration.ofHours(2)));
//        newProgramDTO.setTopicId(String.valueOf(42));
//        newProgramDTO.setStatus("Inactive");
//
//        // Tạo đối tượng TrainingProgram mẫu
//        TrainingProgram newProgramEntity = new TrainingProgram();
//        when(trainingRepository.save(any(TrainingProgram.class))).thenReturn(newProgramEntity);
//
//        // Tạo danh sách SyllabusRequest mẫu
//        List<SyllabusRequest> syllabusRequests = new ArrayList<>();
//        SyllabusRequest request1 = new SyllabusRequest();
//        request1.setOrder(1);
//        request1.setSyllabusId(101L);
//        syllabusRequests.add(request1);
//
//        SyllabusRequest request2 = new SyllabusRequest();
//        request2.setOrder(2);
//        request2.setSyllabusId(102L);
//        syllabusRequests.add(request2);
//
//        newProgramDTO.setSyllabusRequestList(syllabusRequests);
//
//        // Tạo đối tượng TrainingSyllabus mẫu
//        TrainingSyllabus syllabus = new TrainingSyllabus();
//        when(trainingSyllabusRepository.save(syllabus)).thenReturn(new TrainingSyllabus());
//
//        // Gọi phương thức createNewTrainingProgram
//        int result = trainingService.createNewTrainingProgram(newProgramDTO);
//
//        // Kiểm tra kết quả
//        verify(trainingRepository).save(any(TrainingProgram.class));
//        verify(trainingSyllabusRepository, times(syllabusRequests.size())).save(any(TrainingSyllabus.class));
//
//        TrainingProgram newProgram = new TrainingProgram();
//        when(trainingRepository.save(newProgram)).thenReturn(newProgram);
//
//        int result = trainingService.createNewTrainingProgram(newProgram);
//
//        verify(trainingRepository).save(newProgram);
//        assertEquals(1, result);
//    }
//
//    private TrainingProgram mapToEntity(TrainingProgramDTO trainingProgramDto) {
//        TrainingProgram trainingProgram = TrainingProgram.builder()
//                .trainingId(trainingProgramDto.getId())
//                .name(trainingProgramDto.getName())
//                .createBy(trainingProgramDto.getCreateBy())
//                .createDate(trainingProgramDto.getCreateDate())
//                .modifyBy(trainingProgramDto.getModifyBy())
//                .modifyDate(trainingProgramDto.getModifyDate())
//                .startTime(trainingProgramDto.getStartTime())
//                .duration(trainingProgramDto.getDuration())
//                .topicId(trainingProgramDto.getTopicId())
//                .status(trainingProgramDto.getStatus())
//                .build();
//        return trainingProgram;
//    }

    @Test
    public void testDeleteTrainingProgram() {

        Integer id = 1;
        doNothing().when(trainingRepository).deleteById(id);

        int result = trainingService.deleteTrainingProgram(id);

        verify(trainingRepository).deleteById(id);
        assertEquals(1, result);
    }

    @Test
    public void testDeactivateTrainingProgram() {

        Integer id = 1;
        TrainingProgram program = new TrainingProgram();
        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
        when(trainingRepository.save(program)).thenReturn(program);

        trainingService.deactivateTrainingProgram(id);

        verify(trainingRepository).findById(id);
        verify(trainingRepository).save(program);
        assertEquals("Inactive", program.getStatus());
    }

    @Test
    public void testActivateTrainingProgram() {
        Integer id = 1;
        TrainingProgram program = new TrainingProgram();
        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
        when(trainingRepository.save(program)).thenReturn(program);

        trainingService.activateTrainingProgram(id);

        verify(trainingRepository).findById(id);
        verify(trainingRepository).save(program);
        assertEquals("Active", program.getStatus());
    }

//    @Test
//    public void testUpdateTrainingProgram() {
//        Integer trainingId = 1;
//        TrainingProgram existingProgram = new TrainingProgram();
//        existingProgram.setTrainingId(trainingId);
//        existingProgram.setStatus("Active");
//        existingProgram.setModifyDate(LocalDate.now().minusDays(1));
//        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingProgram));
//        when(trainingRepository.save(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
//
//        TrainingProgram updatedProgram = new TrainingProgram();
//        updatedProgram.setName("Updated Program");
//        updatedProgram.setCreateDate(LocalDate.now());
//        updatedProgram.setCreateBy("John Doe");
//        updatedProgram.setDuration("2 days");
//        updatedProgram.setModifyBy("Jane Smith");
//        updatedProgram.setTopicId("123");
//        updatedProgram.setStartTime(LocalDate.now());
//
//        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
//
//        verify(trainingRepository).findById(trainingId);
//        verify(trainingRepository).save(any(TrainingProgram.class));
//        assertEquals(updatedProgram.getName(), result.getName());
//    }


//    @Test
//    public void testUpdateTrainingProgram_InvalidStatus() {
//        Integer id = 1;
//        TrainingProgram program = new TrainingProgram();
//        program.setStatus("Inactive");
//        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
//
//        TrainingProgram updatedProgram = new TrainingProgram();
//
//        assertThrows(IllegalStateException.class, () -> trainingService.updateTrainingProgram(id, updatedProgram));
//    }

//    @Test
//    public void testSearchTrainingProgram() {
//        Integer id = 1;
//        TrainingProgram program = new TrainingProgram();
//        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
//
//        TrainingProgram result = trainingService.searchTrainingProgram(id);
//
//        verify(trainingRepository).findById(id);
//        assertEquals(program, result);
//    }

//    @Test
//    public void testSearchTrainingProgram_NotFound() {
//        Integer id = 1;
//        when(trainingRepository.findById(id)).thenReturn(Optional.empty());
//
//        TrainingProgram result = trainingService.searchTrainingProgram(id);
//
//        verify(trainingRepository).findById(id);
//        assertNull(result);
//    }

    @Test
    public void testExistsTrainingProgramById() {
        Integer id = 1;
        when(trainingRepository.existsById(id)).thenReturn(true);

        boolean exists = trainingService.existsTrainingProgramById(id);

        verify(trainingRepository).existsById(id);
        assertTrue(exists);
    }


//    @Test
//    public void testSearchTrainingProgramWithKeyword() {
//        // Mock dữ liệu
//        String keyword = "Java";
//        List<TrainingProgram> trainingPrograms = new ArrayList<>();
//        TrainingProgram program1 = new TrainingProgram();
//        program1.setTrainingId(1);
//        program1.setName("Java Programming");
//        trainingPrograms.add(program1);
//
//        TrainingProgram program2 = new TrainingProgram();
//        program2.setTrainingId(2);
//        program2.setName("Advanced Java");
//        trainingPrograms.add(program2);
//
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<TrainingProgram> page = new PageImpl<>(trainingPrograms);
//
//        // Khi gọi findByNameContaining, trả về trang của các chương trình đào tạo chứa từ khoá
//        doReturn(page).when(trainingRepository).findByNameContaining(keyword, pageable);
//
//        // Gọi phương thức searchTrainingProgramWithKeyword
//        Page<TrainingProgram> result = trainingService.searchTrainingProgramWithKeyword(keyword, pageable);
//
//        // Kiểm tra xem số lượng bản ghi trả về có đúng không
//        assertEquals(result.getTotalElements(), 2);
//    }


    @Test
    public void testDuplicateTrainingProgram() {
        Integer id = 1;

        TrainingProgram program = new TrainingProgram(1, "Java Programming", "John", LocalDate.now(), "Alice", LocalDate.now(), LocalDate.now(), "3 months", "123", "Drafting");

        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));
        when(trainingRepository.findAllByOrderByTrainingIdDesc()).thenReturn(Collections.singletonList(program));

        TrainingProgram duplicatedProgram = trainingService.duplicateTrainingProgram(id);

        assertNotNull(duplicatedProgram);
        assertEquals(program.getName(), duplicatedProgram.getName());
        assertEquals(program.getCreateBy(), duplicatedProgram.getCreateBy());
        assertEquals(program.getCreateDate(), duplicatedProgram.getCreateDate());
        assertEquals(program.getModifyBy(), duplicatedProgram.getModifyBy());
        assertEquals(program.getModifyDate(), duplicatedProgram.getModifyDate());
        assertEquals(program.getStartTime(), duplicatedProgram.getStartTime());
        assertEquals(program.getTopicId(), duplicatedProgram.getTopicId());
        assertEquals(program.getDuration(), duplicatedProgram.getDuration());
        assertEquals(program.getStatus(), duplicatedProgram.getStatus());
    }

    @Test
    public void testDuplicateTrainingProgramWithNonExistingId() {
        Integer id = 1;

        when(trainingRepository.findById(id)).thenReturn(Optional.empty());

        TrainingProgram duplicatedProgram = trainingService.duplicateTrainingProgram(id);

        assertNull(duplicatedProgram);
    }


    @Test
    public void testGetNextTrainingProgramIdWithEmptyList() {
        when(trainingRepository.findAllByOrderByTrainingIdDesc()).thenReturn(Collections.emptyList());

        int nextId = trainingService.getNextTrainingProgramId();

        assertEquals(1, nextId);
    }


    @Test
    public void testImportTrainingProgramFromFile_WhenDuplicateHandlingIsAllow() throws IOException {
        String csvData = "trainingId,name,createBy,createDate,modifyBy,modifyDate,startTime,duration,topicId,status\n"
                + "1,Training1,User1,2023-10-24,User1,2023-10-24,2023-10-24,3 days,Topic1,Active\n"
                + "2,Training2,User2,2023-10-24,User2,2023-10-24,2023-10-24,5 days,Topic2,Active\n"
                + "3,Training3,User3,2023-10-24,User3,2023-10-24,2023-10-24,4 days,Topic3,Inactive\n";

        InputStream is = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile file = new MockMultipartFile("file", "training_data.csv", "text/csv", is);

        List<TrainingProgram> existingPrograms = new ArrayList<>();
        when(trainingRepository.saveAll(existingPrograms)).thenReturn(existingPrograms);

        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(file, is, "UTF-8", ',', "id", "allow");

        assertEquals(existingPrograms, importedPrograms);
    }

    @Test
    void testImportTrainingProgramFromFile_skipExistingProgram() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/plain", "1,2023-10-24,2023-10-24,2023-10-24,ProgramName,createBy,modifyBy,60,topicId,status".getBytes());
        String encoding = "UTF-8";
        char columnSeparator = ',';
        String scanningMethod = "id";
        String duplicateHandling = "skip";

        int existingProgramId = 1;

        when(trainingRepository.findById(existingProgramId)).thenReturn(Optional.of(new TrainingProgram()));

        // Act
        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(file, file.getInputStream(), encoding, columnSeparator, scanningMethod, duplicateHandling);

        // Assert
        assertEquals(0, importedPrograms.size()); // Should skip existing program
    }


    @Test
    public void testImportTrainingProgramFromFile_WhenNoExistingPrograms_AllowDuplicates() throws IOException {

        String csvData = "1,2023-10-24,2023-10-24,2023-10-24,ProgramName,createBy,modifyBy,60,topicId,status";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/plain", csvData.getBytes());
        when(trainingRepository.findById(1)).thenReturn(Optional.empty());
        when(trainingRepository.findByName("ProgramName")).thenReturn(List.of());


        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(file, file.getInputStream(), "UTF-8", ',', "id", "allow");


        for (TrainingProgram program : importedPrograms) {
            System.out.println("Imported program: " + program.toString());
        }

    }

//    @Test
//    public void testCreateNewTrainingProgram_Success() {
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setTrainingId(1);
//        trainingProgram.setName("Training1");
//        Mockito.when(trainingRepository.save(trainingProgram)).thenReturn(trainingProgram);
//        int result = trainingService.createNewTrainingProgram(trainingProgram);
//        assertEquals(1, result);
//    }
//
//    @Test
//    public void testCreateNewTrainingProgram_Failure() {
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setTrainingId(1);
//        trainingProgram.setName("Training1");
//        Mockito.when(trainingRepository.save(trainingProgram)).thenThrow(new RuntimeException("Save operation failed"));
//        int result = trainingService.createNewTrainingProgram(trainingProgram);
//        assertEquals(0, result);
//    }


    @Test
    public void testDeleteTrainingProgram_Success() {
        Integer idToDelete = 1;
        Mockito.doNothing().when(trainingRepository).deleteById(idToDelete);
        int result = trainingService.deleteTrainingProgram(idToDelete);
        assertEquals(1, result);
    }

    @Test
    public void testDeleteTrainingProgram_Failure() {
        Integer idToDelete = 1;
        Mockito.doThrow(new RuntimeException("Delete operation failed")).when(trainingRepository).deleteById(idToDelete);

        int result = trainingService.deleteTrainingProgram(idToDelete);

        assertEquals(0, result);
    }


    @Test
    public void testDeactivateTrainingProgram_Success() {
        Integer trainingId = 1;
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setStatus("Active");

        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
        Mockito.when(trainingRepository.save(Mockito.any())).thenReturn(trainingProgram);

        trainingService.deactivateTrainingProgram(trainingId);

        assertEquals("Inactive", trainingProgram.getStatus());
    }

    @Test
    public void testDeactivateTrainingProgram_NotFound() {
        Integer trainingId = 1;

        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.deactivateTrainingProgram(trainingId));
    }

    @Test
    public void testActivateTrainingProgram_Success() {
        Integer trainingId = 1;
        TrainingProgram trainingProgram = new TrainingProgram();
        trainingProgram.setStatus("Inactive");

        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
        Mockito.when(trainingRepository.save(Mockito.any())).thenReturn(trainingProgram);

        trainingService.activateTrainingProgram(trainingId);

        assertEquals("Active", trainingProgram.getStatus());
    }

    @Test
    public void testActivateTrainingProgram_NotFound() {
        Integer trainingId = 1;

        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.activateTrainingProgram(trainingId));
    }

//    @Test
//    public void testUpdateTrainingProgram_Success() {
//        Integer trainingId = 1;
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setStatus("Active");
//
//        TrainingProgram updatedProgram = new TrainingProgram();
//        updatedProgram.setName("Updated Program");
//        // Set other properties of updatedProgram
//
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
//        Mockito.when(trainingRepository.save(Mockito.any())).thenReturn(updatedProgram);
//
//        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
//
//        assertEquals(updatedProgram.getName(), result.getName());
//        // Assert other properties of the updated program
//    }

//    @Test
//    public void testUpdateTrainingProgram_NotFound() {
//        Integer trainingId = 1;
//
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> trainingService.updateTrainingProgram(trainingId, new TrainingProgram()));
//    }

    //    @Test
//    public void testUpdateTrainingProgram_Inactive() {
//        Integer trainingId = 1;
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setStatus("Inactive");
//
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
//
//        assertThrows(IllegalStateException.class, () -> trainingService.updateTrainingProgram(trainingId, new TrainingProgram()));
//    }
    @Test
    public void testUpdateTrainingProgramInvalidModifyDate() {
        // Create data for testing
        Integer trainingId = 1;
        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
        // Set properties of updatedProgram as needed
        // ...
        TrainingProgram existingTrainingProgram = new TrainingProgram();
        existingTrainingProgram.setStatus("Active");
        existingTrainingProgram.setModifyDate(LocalDate.now().plusDays(1)); // Modify date is in the future
        // Set other properties of existingTrainingProgram as needed
        // ...

        // Mock the behavior of the trainingRepository when the modify date is in the future
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));

        // Perform the test
        assertThrows(IllegalStateException.class, () -> {
            trainingService.updateTrainingProgram(trainingId, updatedProgram);
        });
    }


    @Test
    public void testUpdateTrainingProgramInactiveStatus() {
        // Create data for testing
        Integer trainingId = 1;
        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
        // Set properties of updatedProgram as needed
        // ...
        TrainingProgram existingTrainingProgram = new TrainingProgram();
        existingTrainingProgram.setStatus("Inactive");
        // Set other properties of existingTrainingProgram as needed
        // ...

        // Mock the behavior of the trainingRepository when the status is "Inactive"
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));

        // Perform the test
        assertThrows(IllegalStateException.class, () -> {
            trainingService.updateTrainingProgram(trainingId, updatedProgram);
        });
    }

    @Test
    public void testUpdateTrainingProgramTrainingNotFound() {

        Integer trainingId = 1;
        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();


        // Mock the behavior of the trainingRepository when the training is not found
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        // Perform the test
        assertThrows(EntityNotFoundException.class, () -> {
            trainingService.updateTrainingProgram(trainingId, updatedProgram);
        });
    }

//    @Test
//    public void testUpdateTrainingProgram_InvalidModifyDate() {
//    public void testUpdateTrainingProgramAttributesAndSyllabus() {
//        // Create data for testing
//        Integer trainingId = 1;
//        TrainingProgram trainingProgram = new TrainingProgram();
//        trainingProgram.setStatus("Active");
//        trainingProgram.setModifyDate(LocalDate.now().plusDays(1));
//
//        Mockito.when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(trainingProgram));
//        TrainingProgramDTO updatedProgram = new TrainingProgramDTO();
//        // Set properties of updatedProgram as needed
//        // ...
//        TrainingProgram existingTrainingProgram = new TrainingProgram();
//        existingTrainingProgram.setStatus("Active");
//        LocalDate currentDate = LocalDate.now();
//        existingTrainingProgram.setModifyDate(currentDate.minusDays(1)); // Modify date is in the past
//        // Set other properties of existingTrainingProgram as needed
//        // ...
//
//        // Mock the behavior of the trainingRepository and trainingSyllabusRepository
//        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingTrainingProgram));
//        doNothing().when(trainingSyllabusRepository).deleteByTrainingProgram(existingTrainingProgram);
//
//        TrainingProgram savedTrainingProgram = new TrainingProgram();
//        when(trainingRepository.save(existingTrainingProgram)).thenReturn(savedTrainingProgram);
//
//        // Perform the test
//        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);
//
//        assertThrows(IllegalStateException.class, () -> trainingService.updateTrainingProgram(trainingId, new TrainingProgram()));
//        // Perform assertions
//        // Verify that the properties of existingTrainingProgram have been updated correctly based on updatedProgram
//        assertEquals(updatedProgram.getName(), existingTrainingProgram.getName());
//        assertEquals(updatedProgram.getCreateBy(), existingTrainingProgram.getCreateBy());
//        assertEquals(updatedProgram.getDuration(), existingTrainingProgram.getDuration());
//        assertEquals(updatedProgram.getModifyBy(), existingTrainingProgram.getModifyBy());
//        assertEquals(updatedProgram.getTopicId(), existingTrainingProgram.getTopicId());
//        // Verify that the startTime and modifyDate have been updated
//        assertEquals(updatedProgram.getStartTime(), existingTrainingProgram.getStartTime());
//        assertEquals(currentDate, existingTrainingProgram.getModifyDate());
//
//        // Verify that the deleteByTrainingProgram and save methods have been called
//        verify(trainingSyllabusRepository).deleteByTrainingProgram(existingTrainingProgram);
//        verify(trainingRepository).save(existingTrainingProgram);
//    }
//


    @Test
    public void testHasCSVFormatWithCSVFile() {
        MockMultipartFile csvFile = new MockMultipartFile("file", "training_data.csv", "text/csv", new byte[0]);

        boolean result = trainingService.hasCSVFormat(csvFile);

        assertTrue(result);
    }

    @Test
    public void testHasCSVFormatWithNonCSVFile() {
        MockMultipartFile nonCSVFile = new MockMultipartFile("file", "training_data.txt", "text/plain", new byte[0]);

        boolean result = trainingService.hasCSVFormat(nonCSVFile);

        assertFalse(result);
    }


//    @Test
//    public void testImportTrainingProgramFromFile_AllowDuplicateById() throws IOException {
//        File csvFile = new File("D:\\CV\\MOCK_DATA-BASE.csv");
//        FileInputStream fileInputStream = new FileInputStream(csvFile);
//        MultipartFile multipartFile = new MockMultipartFile("MOCK_DATA-BASE.csv", "MOCK_DATA-BASE.csv", "MOCK_DATA-BASE/csv", fileInputStream);
//
//        when(trainingRepository.findById(1)).thenReturn(Optional.of(new TrainingProgram()));
//        when(trainingRepository.findById(2)).thenReturn(Optional.empty());
//
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");
//
//        List<TrainingProgram> importedPrograms = trainingService.importTrainingProgramFromFile(
//                multipartFile,
//                multipartFile.getInputStream(),
//                "UTF-8",
//                ',',
//                "id",
//                "allow"
//        );
//
//        assertEquals(10, importedPrograms.size());
//        verify(trainingRepository, times(0)).save(any(TrainingProgram.class));
//        verify(trainingRepository, times(2)).save(any(TrainingProgram.class));
//    }

    @Test
    public void testImportTrainingProgramFromFile_ReplaceById() {
        // Test trường hợp import và thay thế dựa trên ID
        // Đảm bảo rằng chương trình đào tạo đã tồn tại với ID đã cho sẽ được thay thế bằng dữ liệu từ CSV.
    }

    @Test
    public void testImportTrainingProgramFromFile_AllowDuplicateByName() {
        // Test trường hợp import cho phép trùng tên chương trình đào tạo
        // Bạn cần tạo dữ liệu CSV với các tên chương trình trùng nhau và kiểm tra xem liệu chúng có được thêm vào không.
    }

    @Test
    public void testImportTrainingProgramFromFile_ReplaceByName() {
        // Test trường hợp import và thay thế dựa trên tên chương trình đào tạo
        // Đảm bảo rằng chương trình đào tạo đã tồn tại với tên đã cho sẽ được thay thế bằng dữ liệu từ CSV.
    }

    @Test
    public void testImportTrainingProgramFromFile_AllowDuplicateByIdAndName() {
        // Test trường hợp import cho phép trùng cả ID và tên chương trình đào tạo
        // Bạn cần tạo dữ liệu CSV sao cho ID và tên đều trùng và kiểm tra xem liệu chúng có được thêm vào không.
    }

    @Test
    public void testImportTrainingProgramFromFile_ReplaceByIdAndName() {
        // Test trường hợp import và thay thế dựa trên cả ID và tên chương trình đào tạo
        // Đảm bảo rằng chương trình đào tạo đã tồn tại với cả ID và tên đã cho sẽ được thay thế bằng dữ liệu từ CSV.
    }

    @Test
    public void testImportTrainingProgramFromFile_SkipDuplicateById() {
        // Test trường hợp import và bỏ qua dựa trên ID
        // Đảm bảo rằng chương trình đào tạo đã tồn tại với ID đã cho sẽ không bị thêm vào.
    }

    @Test
    public void testImportTrainingProgramFromFile_SkipDuplicateByName() {
        // Test trường hợp import và bỏ qua dựa trên tên chương trình đào tạo
        // Đảm bảo rằng chương trình đào tạo đã tồn tại với tên đã cho sẽ không bị thêm vào.
    }

    @Test
    public void testImportTrainingProgramFromFile_SkipDuplicateByIdAndName() {
        // Test trường hợp import và bỏ qua dựa trên cả ID và tên chương trình đào tạo
        // Đảm bảo rằng chương trình đào tạo đã tồn tại với cả ID và tên đã cho sẽ không bị thêm vào.
    }

    @Test
    public void testImportTrainingProgramFromFile_ExceptionHandling() {
        // Test trường hợp xử lý ngoại lệ khi có lỗi trong quá trình import từ CSV
        // Đảm bảo rằng ngoại lệ được xử lý đúng cách và thông báo lỗi được đưa ra.
    }


    @Test
    public void testGetSortedTrainingProgram() {
        // Mock the behavior of the trainingRepository
        Page<TrainingProgram> mockTrainingProgramPage = createMockTrainingProgramPage();
        when(trainingRepository.findAll(any(PageRequest.class))).thenReturn(mockTrainingProgramPage);

        // Call the method to be tested
        Pageable pageable = PageRequest.of(0, 10);
        Page<TrainingProgram> result = trainingService.getSortedTrainingProgram("fieldName", "ASC", pageable);

        // Verify the method's behavior
        assertThat(result).isNotNull();

        // Verify that the repository method was called with the correct arguments
        verify(trainingRepository).findAll(any(PageRequest.class));
    }

    private Page<TrainingProgram> createMockTrainingProgramPage() {
        // Create a mock Page of TrainingPrograms for testing
        List<TrainingProgram> trainingProgramList = new ArrayList<>();

        // Add TrainingProgram objects to the list for testing
        TrainingProgram trainingProgram1 = new TrainingProgram();
        trainingProgram1.setTrainingId(1);
        trainingProgram1.setName("Program 1");
        trainingProgram1.setDuration("Description for Program 1");

        trainingProgramList.add(trainingProgram1);


        return new PageImpl<>(trainingProgramList);
    }


}
