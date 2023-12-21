//package com.fams.training.service.Imp;
//
//import com.fams.training.DTO.AssignmentDTO;
//import com.fams.training.entity.Assignment;
//import com.fams.training.repository.AssignmentRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.ArgumentMatchers.any;
//
//
//@SpringBootTest
//public class AssignmentServiceImplTest {
//
//    @InjectMocks
//    private AssignmentServiceImp assignmentService;
//
//    @Mock
//    private AssignmentRepository assignmentRepository;
//
//    private Assignment assignment;
//
//    @BeforeEach
//    public void setUp() {
//        assignment = Assignment.builder()
//                .assignmentId(1)
//                .title("Assignment 1")
//                .description("Description 1")
//                .dueDate(LocalDate.now())
//                .score(100)
//                .build();
//    }
//
//    @Test
//    public void testGetAllAssignments() {
//        List<Assignment> assignments = new ArrayList<>();
//        assignments.add(assignment);
//
//        Mockito.when(assignmentRepository.findAll()).thenReturn(assignments);
//
//        List<AssignmentDTO> assignmentDTOs = assignmentService.getAllAssignments();
//
//        assertEquals(1, assignmentDTOs.size());
//        assertEquals(assignment.getAssignmentId(), assignmentDTOs.get(0).getAssignmentId());
//        assertEquals(assignment.getTitle(), assignmentDTOs.get(0).getTitle());
//        assertEquals(assignment.getDescription(), assignmentDTOs.get(0).getDescription());
//        assertEquals(assignment.getDueDate(), assignmentDTOs.get(0).getDueDate());
//        assertEquals(assignment.getScore(), assignmentDTOs.get(0).getScore());
//    }
//
//    @Test
//    public void testGetAssignmentById() {
//        Mockito.when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
//
//        AssignmentDTO assignmentDTO = assignmentService.getAssignmentById(1);
//
//        assertEquals(assignment.getAssignmentId(), assignmentDTO.getAssignmentId());
//        assertEquals(assignment.getTitle(), assignmentDTO.getTitle());
//        assertEquals(assignment.getDescription(), assignmentDTO.getDescription());
//        assertEquals(assignment.getDueDate(), assignmentDTO.getDueDate());
//        assertEquals(assignment.getScore(), assignmentDTO.getScore());
//    }
//
//    @Test
//    public void testCreateAssignment() {
//        Mockito.when(assignmentRepository.save(any())).thenReturn(assignment);
//
//        AssignmentDTO assignmentDTO = AssignmentDTO.builder()
//                .assignmentId(1)
//                .title("Assignment 1")
//                .description("Description 1")
//                .dueDate(LocalDate.now())
//                .score(100)
//                .build();
//
//        AssignmentDTO createdAssignmentDTO = assignmentService.createAssignment(assignmentDTO);
//
//        assertEquals(assignment.getAssignmentId(), createdAssignmentDTO.getAssignmentId());
//        assertEquals(assignment.getTitle(), createdAssignmentDTO.getTitle());
//        assertEquals(assignment.getDescription(), createdAssignmentDTO.getDescription());
//        assertEquals(assignment.getDueDate(), createdAssignmentDTO.getDueDate());
//        assertEquals(assignment.getScore(), createdAssignmentDTO.getScore());
//    }
//
//    @Test
//    public void testUpdateAssignment() {
//        Assignment updatedAssignment = Assignment.builder()
//                .assignmentId(1)
//                .title("Updated Assignment")
//                .description("Updated Description")
//                .dueDate(LocalDate.now().plusDays(1))
//                .score(200)
//                .build();
//
//        Mockito.when(assignmentRepository.findById(1)).thenReturn(Optional.of(assignment));
//        Mockito.when(assignmentRepository.save(any())).thenReturn(updatedAssignment);
//
//        AssignmentDTO assignmentDTO = AssignmentDTO.builder()
//                .assignmentId(1)
//                .title("Updated Assignment")
//                .description("Updated Description")
//                .dueDate(LocalDate.now().plusDays(1))
//                .score(200)
//                .build();
//
//        AssignmentDTO updatedAssignmentDTO = assignmentService.updateAssignment(1, assignmentDTO);
//
//        assertEquals(updatedAssignment.getAssignmentId(), updatedAssignmentDTO.getAssignmentId());
//        assertEquals(updatedAssignment.getTitle(), updatedAssignmentDTO.getTitle());
//        assertEquals(updatedAssignment.getDescription(), updatedAssignmentDTO.getDescription());
//        assertEquals(updatedAssignment.getDueDate(), updatedAssignmentDTO.getDueDate());
//        assertEquals(updatedAssignment.getScore(), updatedAssignmentDTO.getScore());
//    }
//
//    @Test
//    public void testGetAssignmentByIdWithNull() {
//        Mockito.when(assignmentRepository.findById(1)).thenReturn(Optional.empty());
//
//        AssignmentDTO assignmentDTO = assignmentService.getAssignmentById(1);
//
//        assertNull(assignmentDTO);
//    }
//
//
//
//    @Test
//    public void testUpdateAssignmentWithNull() {
//        Mockito.when(assignmentRepository.findById(1)).thenReturn(Optional.empty());
//
//        AssignmentDTO assignmentDTO = AssignmentDTO.builder()
//                .assignmentId(1)
//                .title("Updated Assignment")
//                .description("Updated Description")
//                .dueDate(LocalDate.now().plusDays(1))
//                .score(200)
//                .build();
//
//        AssignmentDTO updatedAssignmentDTO = assignmentService.updateAssignment(1, assignmentDTO);
//
//        assertNull(updatedAssignmentDTO);
//    }
//}