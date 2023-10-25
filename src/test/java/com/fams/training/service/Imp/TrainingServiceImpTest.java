package com.fams.training.service.Imp;

import com.fams.training.entity.TrainingProgram;
import com.fams.training.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
public class TrainingServiceImpTest {

    @InjectMocks
    private TrainingServiceImp trainingService;

    @Mock
    private TrainingRepository trainingRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllPagingTrainingProgram() {

        int page = 0;
        int size = 10;

        List<TrainingProgram> mockTrainingPrograms = createMockTrainingPrograms();
        org.springframework.data.domain.Page<TrainingProgram> expectedPage = new PageImpl<>(mockTrainingPrograms, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")), mockTrainingPrograms.size());

        when(trainingRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<TrainingProgram> result = trainingService.getAllPagingTrainingProgram(page, size);

        verify(trainingRepository).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate")));
        assertEquals(expectedPage, result);
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

    @Test
    public void testCreateNewTrainingProgram() {

        TrainingProgram newProgram = new TrainingProgram();
        when(trainingRepository.save(newProgram)).thenReturn(newProgram);

        int result = trainingService.createNewTrainingProgram(newProgram);

        verify(trainingRepository).save(newProgram);
        assertEquals(1, result);
    }

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

    @Test
    public void testUpdateTrainingProgram() {
        Integer trainingId = 1;
        TrainingProgram existingProgram = new TrainingProgram();
        existingProgram.setTrainingId(trainingId);
        existingProgram.setStatus("Active");
        existingProgram.setModifyDate(LocalDate.now().minusDays(1));
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(existingProgram));
        when(trainingRepository.save(any(TrainingProgram.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        TrainingProgram updatedProgram = new TrainingProgram();
        updatedProgram.setName("Updated Program");
        updatedProgram.setCreateDate(LocalDate.now());
        updatedProgram.setCreateBy("John Doe");
        updatedProgram.setDuration("2 days");
        updatedProgram.setModifyBy("Jane Smith");
        updatedProgram.setTopicId("123");
        updatedProgram.setStartTime(LocalDate.now());

        TrainingProgram result = trainingService.updateTrainingProgram(trainingId, updatedProgram);

        verify(trainingRepository).findById(trainingId);
        verify(trainingRepository).save(any(TrainingProgram.class));
        assertEquals(updatedProgram.getName(), result.getName());
    }


    @Test
    public void testUpdateTrainingProgram_InvalidStatus() {
        Integer id = 1;
        TrainingProgram program = new TrainingProgram();
        program.setStatus("Inactive");
        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));

        TrainingProgram updatedProgram = new TrainingProgram();

        assertThrows(IllegalStateException.class, () -> trainingService.updateTrainingProgram(id, updatedProgram));
    }

    @Test
    public void testSearchTrainingProgram() {
        Integer id = 1;
        TrainingProgram program = new TrainingProgram();
        when(trainingRepository.findById(id)).thenReturn(Optional.of(program));

        TrainingProgram result = trainingService.searchTrainingProgram(id);

        verify(trainingRepository).findById(id);
        assertEquals(program, result);
    }

    @Test
    public void testSearchTrainingProgram_NotFound() {
        Integer id = 1;
        when(trainingRepository.findById(id)).thenReturn(Optional.empty());

        TrainingProgram result = trainingService.searchTrainingProgram(id);

        verify(trainingRepository).findById(id);
        assertNull(result);
    }

    @Test
    public void testExistsTrainingProgramById() {
        Integer id = 1;
        when(trainingRepository.existsById(id)).thenReturn(true);

        boolean exists = trainingService.existsTrainingProgramById(id);

        verify(trainingRepository).existsById(id);
        assertTrue(exists);
    }


//    @Test
//    public void testImportTrainingProgramFromFile_DuplicateHandling() {
//        String csvDataWithDuplicates = "1,2023-10-01,2023-10-05,2023-10-01,Duplicate Program,User 1,2023-10-01,User 1,2023-10-05,2023-10-01,60,Topic 1,Active";
//        InputStream inputStream = new ByteArrayInputStream(csvDataWithDuplicates.getBytes());
//
//        when(trainingRepository.findById(1)).thenReturn(Optional.of(new TrainingProgram()));
//
//        List<TrainingProgram> programsAllow = trainingService.importTrainingProgramFromFile(null, inputStream, "UTF-8", ',', "id", "allow");
//        assertEquals(1, programsAllow.size());
//
//        when(trainingRepository.findById(1)).thenReturn(Optional.of(new TrainingProgram()));
//        List<TrainingProgram> programsReplace = trainingService.importTrainingProgramFromFile(null, inputStream, "UTF-8", ',', "id", "replace");
//        assertEquals(1, programsReplace.size());
//
//        when(trainingRepository.findById(1)).thenReturn(Optional.of(new TrainingProgram()));
//        List<TrainingProgram> programsSkip = trainingService.importTrainingProgramFromFile(null, inputStream, "UTF-8", ',', "id", "skip");
//        assertEquals(0, programsSkip.size());
//    }



}