package com.fams.training.service.Imp;

import com.fams.training.DTO.AssignmentDTO;
import com.fams.training.entity.Assignment;
import com.fams.training.repository.AssignmentRepository;
import com.fams.training.service.Interface.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    @Override
    public List<AssignmentDTO> getAllAssignments() {
        List<Assignment> assignments = assignmentRepository.findAll();
        List<AssignmentDTO> assignmentDTOs = new ArrayList<>();

        for (Assignment assignment : assignments) {
            assignmentDTOs.add(convertToDTO(assignment));
        }

        return assignmentDTOs;
    }

    private AssignmentDTO convertToDTO(Assignment assignment) {
        return AssignmentDTO.builder()
                .assignmentId(assignment.getAssignmentId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDate(assignment.getDueDate())
                .score(assignment.getScore())
                .build();
    }

    @Override
    public AssignmentDTO getAssignmentById(int assignmentId) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);

        if (assignmentOptional.isPresent()) {
            Assignment assignment = assignmentOptional.get();
            return convertToDTO(assignment);
        }

        return null;
    }


    @Override
    public AssignmentDTO createAssignment(AssignmentDTO assignmentDTO) {
        Assignment assignment = convertToEntity(assignmentDTO);
        Assignment createdAssignment = assignmentRepository.save(assignment);
        return convertToDTO(createdAssignment);
    }

    @Override
    public AssignmentDTO updateAssignment(Integer assignmentId, AssignmentDTO assignmentDTO) {
        Assignment existingAssignment = assignmentRepository.findById(assignmentId).orElse(null);
        if (existingAssignment != null) {
            Assignment updatedAssignment = convertToEntity(assignmentDTO);
            updatedAssignment.setAssignmentId(existingAssignment.getAssignmentId());
            Assignment savedAssignment = assignmentRepository.save(updatedAssignment);
            return convertToDTO(savedAssignment);
        }
        return null;
    }

    private Assignment convertToEntity(AssignmentDTO assignmentDTO) {
        return Assignment.builder()
                .assignmentId(assignmentDTO.getAssignmentId())
                .title(assignmentDTO.getTitle())
                .description(assignmentDTO.getDescription())
                .dueDate(assignmentDTO.getDueDate())
                .score(assignmentDTO.getScore())
                .build();
    }
}
