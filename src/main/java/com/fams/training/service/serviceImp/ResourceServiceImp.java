package com.fams.training.service.serviceImp;

import com.fams.training.entity.Resource;
import com.fams.training.entity.TrainingProgram;
import com.fams.training.repository.ResourceRepository;
import com.fams.training.service.serviceInterface.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResourceServiceImp implements ResourceService {

    private final ResourceRepository resourceRepository;
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;

    private static final Set<String> ALLOWED_EXTENSIONS =
            new HashSet<>(Set.of("jpg", "jpeg", "png", "pdf", "ppt", "video",
                    "xls", "csv", "pptx", "txt", "docx"));

    @Override
    public Page<Resource> getAllResource(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return resourceRepository.findAll(pageable);
    }

    @Override
    public boolean uploadTrainingMaterial( String description, MultipartFile file) {
        try {
            if (file.getSize() > MAX_FILE_SIZE) {
                return false;
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = StringUtils.getFilenameExtension(originalFilename).toLowerCase();

            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                return false;
            }

            byte[] data = file.getBytes();


            Resource resource = Resource.builder()
                    .description(description)
                    .filename(originalFilename)
                    .data(data)
                    .uploadDateTime(LocalDateTime.now())
                    .status("Active")
                    .build();

            resourceRepository.save(resource);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public byte[] downloadTrainingMaterial(int resourceId) {
        Optional<Resource> resourceOptional = resourceRepository.findById(resourceId);
        if (resourceOptional.isPresent()) {
            Resource resource = resourceOptional.get();
            return resource.getData();
        }
        return null;
    }

    @Override
    public boolean deleteMaterials(int resourceId) {
        Optional<Resource> optionalResource = resourceRepository.findById(resourceId);
        if (optionalResource.isPresent()) {
            Resource resource = optionalResource.get();
            resource.setStatus("Deactivate");
            resourceRepository.save(resource);
            return true;
        }
        return false;

    }
}
