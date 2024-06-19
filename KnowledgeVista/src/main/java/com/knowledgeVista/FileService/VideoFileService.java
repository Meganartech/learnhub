package com.knowledgeVista.FileService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoFileService {
	 @Value("${upload.video.directory}")
	    private String videoUploadDirectory;
	    

	 public String saveVideoFile(MultipartFile videoFile) throws IOException {
	        // Ensure the upload directory exists
	        Path uploadPath = Paths.get(videoUploadDirectory);
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        // Generate a unique file name
	        String uniqueFileName = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();

	        // Define the file path where the video file will be stored
	        String filePath = uploadPath.resolve(uniqueFileName).toString();
	        String modifiedPath = filePath.replace("video\\", "");
	        // Save the file to the server
	        Files.copy(videoFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

	        // Return the relative path to the file
	        return uniqueFileName;
	    }
public boolean deleteVideoFile(String fileName) {
    Path filePath = Paths.get(videoUploadDirectory, fileName);

    try {
        boolean deleted = Files.deleteIfExists(filePath);

        if (deleted) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("File does not exist or deletion failed");
        }

        return deleted; // Return the result to the caller
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error occurred while deleting the file");
        return false; // Return false in case of an exception
    }
}

public String updateVideoFile(String existingFileName, MultipartFile newVideoFile, String videoUploadDirectory) throws IOException {
    // Generate a unique file name for the updated video
    String uniqueFileName = System.currentTimeMillis() + "_" + newVideoFile.getOriginalFilename();

    // Define the file paths for the existing and updated videos
    String existingFilePath = Paths.get(videoUploadDirectory, existingFileName).toString();
    String updatedFilePath = Paths.get(videoUploadDirectory, uniqueFileName).toString();
    String modifiedPath = updatedFilePath.replace("video\\", "");
    // Check if the existing file exists
    if (Files.exists(Path.of(existingFilePath))) {
        // Save the updated file to the specified location
        Files.copy(newVideoFile.getInputStream(), Path.of(updatedFilePath), StandardCopyOption.REPLACE_EXISTING);

        // Delete the existing file
        Files.deleteIfExists(Path.of(existingFilePath));

        // Return the path to the updated video
        return modifiedPath;
    } else {
        // Handle the case where the existing file is not found
        throw new FileNotFoundException("Existing file not found: " + existingFileName);
    }
}


}
