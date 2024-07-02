package com.knowledgeVista.FileService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class LicenceService {
    @Value("${upload.licence.directory}")
    private String licenceUploadDirectory;

    
//    public String saveLicence(MultipartFile File) throws IOException {
//        String uniqueFileName =File.getOriginalFilename();
//       
//        Path uploadPath = Paths.get(licenceUploadDirectory);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//        String filePath = Paths.get(licenceUploadDirectory).resolve(uniqueFileName).toString();
//
//        Files.copy(File.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
//
//        return "modifiedPath";   
//        
//    }
}
