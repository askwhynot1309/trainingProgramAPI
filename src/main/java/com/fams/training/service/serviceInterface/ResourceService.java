package com.fams.training.service.serviceInterface;

import com.fams.training.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    Page<Resource> getAllResource(int page, int size);
    boolean uploadTrainingMaterial(String description, MultipartFile file);
    byte[] downloadTrainingMaterial(int resourceId);
    boolean deleteMaterials(int resourceId);

}
