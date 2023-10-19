package com.fams.training.service.Interface;


import com.fams.training.DTO.AssignmentDTO;


import java.util.List;

public interface AssignmentService {
    List<AssignmentDTO> getAllAssignments();
    AssignmentDTO  getAssignmentById(int assignmentId);
    AssignmentDTO createAssignment(AssignmentDTO assignmentDTO);
    AssignmentDTO updateAssignment(Integer assignmentId, AssignmentDTO assignmentDTO);

}
