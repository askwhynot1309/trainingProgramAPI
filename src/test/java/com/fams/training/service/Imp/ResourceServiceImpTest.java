package com.fams.training.service.Imp;

import com.fams.training.entity.Resource;
import com.fams.training.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ResourceServiceImpTest {

    @InjectMocks
    private ResourceServiceImp resourceService;

    @Mock
    private ResourceRepository resourceRepository;

    private Resource resource;
    private MultipartFile validFile;
    private MultipartFile invalidFile;

    @BeforeEach
    public void setUp() {
        resource = Resource.builder()
                .description("Test Resource")
                .filename("testfile.pdf")
                .data(new byte[1024])
                .uploadDateTime(LocalDateTime.now())
                .status("Active")
                .build();

        validFile = new MockMultipartFile("file", "testfile.pdf", "application/pdf", "Test PDF Content".getBytes());

        invalidFile = new MockMultipartFile("file", "largefile.pdf", "application/pdf", new byte[26 * 1024 * 1024]);
    }

    @Test
    public void testUploadTrainingMaterial_ValidFile() {
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        boolean result = resourceService.uploadTrainingMaterial("Test Resource", validFile);

        assertTrue(result);

    }

    @Test
    public void testUploadTrainingMaterial_InvalidFile_Size() {
        boolean result = resourceService.uploadTrainingMaterial("Test Resource", invalidFile);

        assertFalse(result);
    }

    @Test
    public void testUploadTrainingMaterial_InvalidFile_Extension() {
        MultipartFile invalidFile = new MockMultipartFile("file", "testfile.invalid", "application/invalid", new byte[1024]);
        boolean result = resourceService.uploadTrainingMaterial("Test Resource", invalidFile);

        assertFalse(result);
    }



    @Test
    public void testDownloadTrainingMaterial() {
        int resourceId = 1;

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        byte[] downloadedData = resourceService.downloadTrainingMaterial(resourceId);

        assertNotNull(downloadedData);
        assertArrayEquals(resource.getData(), downloadedData);
    }

    @Test
    public void testDownloadTrainingProgramTemplate() {
        Mockito.when(resourceRepository.findByTitle("Training program template")).thenReturn(resource);

        byte[] downloadedData = resourceService.downloadTrainingProgramTemplate("Training program template");

        assertNotNull(downloadedData);
        assertArrayEquals(resource.getData(), downloadedData);
    }

    @Test
    public void testDownloadTrainingMaterial_NotFound() {
        Mockito.when(resourceRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        byte[] downloadedData = resourceService.downloadTrainingMaterial(1);

        assertNull(downloadedData);
    }

    @Test
    public void testDownloadTrainingProgramTemplate_NotFound() {
        Mockito.when(resourceRepository.findByTitle(Mockito.anyString())).thenReturn(null);

        byte[] downloadedData = resourceService.downloadTrainingProgramTemplate("Non-Existent Title");


        assertNull(downloadedData);
    }
    @Test
    public void testDeleteMaterials() {
        when(resourceRepository.findById(1)).thenReturn(Optional.of(resource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        boolean result = resourceService.deleteMaterials(1);

        assertTrue(result);
        assertEquals("Deactivate", resource.getStatus());
    }

    @Test
    public void testDeleteMaterials_InvalidResource() {
        Mockito.when(resourceRepository.findById(2)).thenReturn(Optional.empty());

        boolean result = resourceService.deleteMaterials(2);

        assertFalse(result);
    }

    @Test
    public void testGetAllResource() {

        int page = 0;
        int size = 10;

        List<Resource> mockResources = createMockResources();
        org.springframework.data.domain.Page<Resource> expectedPage = new PageImpl<>(mockResources, PageRequest.of(page, size), mockResources.size());

        when(resourceRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<Resource> result = resourceService.getAllResource(page, size);

        assertNotNull(result);
        assertEquals(expectedPage, result);
    }

    private List<Resource> createMockResources() {
        List<Resource> resources = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Resource resource = Resource.builder()
                    .description("Resource " + i)
                    .filename("file" + i + ".pdf")
                    .data(new byte[1024])
                    .uploadDateTime(LocalDateTime.now())
                    .status("Active")
                    .build();
            resources.add(resource);
        }
        return resources;
    }


    @Test
    public void testUploadTrainingMaterial_ExceptionCaught() {
        String description = "Test Resource";
        MockMultipartFile fileWithException = new MockMultipartFile("file", "testfile.pdf", "application/pdf", "Test PDF Content".getBytes());

        fileWithException = null;

        boolean result = resourceService.uploadTrainingMaterial(description, fileWithException);

        assertFalse(result);
    }

    @Test
    public void testUploadTrainingMaterialCatchIOException() throws IOException {
        ResourceRepository resourceRepository = mock(ResourceRepository.class);
        ResourceServiceImp resourceService = new ResourceServiceImp(resourceRepository);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(100L); // Set a file size that will cause an IOException
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getBytes()).thenThrow(new IOException()); // Simulate IOException

        boolean result = resourceService.uploadTrainingMaterial("Description", file);

        assertFalse(result);
    }


    @Test
    public void testDownloadTrainingMaterialCatch() {
        ResourceRepository resourceRepository = mock(ResourceRepository.class);
        ResourceServiceImp resourceService = new ResourceServiceImp(resourceRepository);

        int resourceId = 1;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        byte[] data = resourceService.downloadTrainingMaterial(resourceId);

        assertNull(data);
    }

    @Test
    public void testDownloadTrainingProgramTemplateCatch() {
        ResourceRepository resourceRepository = mock(ResourceRepository.class);
        ResourceServiceImp resourceService = new ResourceServiceImp(resourceRepository);

        String title = "Training program template";
        when(resourceRepository.findByTitle(title)).thenReturn(null);

        byte[] data = resourceService.downloadTrainingProgramTemplate(title);

        assertNull(data);
    }

    @Test
    public void testDeleteMaterialsCatch() {
        ResourceRepository resourceRepository = mock(ResourceRepository.class);
        ResourceServiceImp resourceService = new ResourceServiceImp(resourceRepository);

        int resourceId = 1;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        boolean result = resourceService.deleteMaterials(resourceId);

        assertFalse(result);
    }

}
