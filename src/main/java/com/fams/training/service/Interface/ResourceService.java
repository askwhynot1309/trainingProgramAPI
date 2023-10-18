package com.fams.training.service.Interface;

import com.fams.training.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    Page<Resource> getAllResource(int page, int size);
    boolean uploadTrainingMaterial(String description, MultipartFile file);
    byte[] downloadTrainingMaterial(int resourceId);
    boolean deleteMaterials(int resourceId);
    public byte[] downloadTrainingProgramTemplate(String title);
}
