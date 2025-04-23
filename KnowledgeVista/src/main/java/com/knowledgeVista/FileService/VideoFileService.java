package com.knowledgeVista.FileService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoFileService {
	@Value("${upload.video.directory}")
	private String videoUploadDirectory;

	private static final Logger logger = LoggerFactory.getLogger(VideoFileService.class);

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
		filePath.replace("video\\", "");
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
			logger.error("", e);
			;
			System.out.println("Error occurred while deleting the file");
			return false; // Return false in case of an exception
		}
	}

	public long getFileSize(String fileName) {
		Path filePath = Paths.get(videoUploadDirectory, fileName);

		try {
			System.out.println("Path: " + filePath);

			if (Files.exists(filePath)) {
				// Fetch file size safely
				try (SeekableByteChannel channel = Files.newByteChannel(filePath)) {
					long fileSize = channel.size();
					System.out.println("File size: " + fileSize + " bytes");
					return fileSize;
				}
			} else {
				System.out.println("File does not exist.");
				return 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error occurred while fetching file size.");
			return 0;
		}
	}

	public boolean deleteFile(String fileName) {
		Path filePath = Paths.get(videoUploadDirectory, fileName);

		try {
			System.out.println("Deleting file: " + filePath);

			if (Files.deleteIfExists(filePath)) {
				System.out.println("File deleted successfully.");
				return true;
			} else {
				System.out.println("File does not exist.");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error occurred while deleting the file.");
			return false;
		}
	}

	public String updateVideoFile(String existingFileName, MultipartFile newVideoFile, String videoUploadDirectory)
			throws IOException {
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

	public String saveAssignmentFile(MultipartFile videoFile, String institutionName, Long batchId, Long courseId,
			Long userId) throws IOException {

// Build the relative path structure
		String relativePath = Paths
				.get(sanitize(institutionName), "batch_" + batchId, "course_" + courseId, "student_" + userId)
				.toString();

// Ensure the upload directory exists
		Path uploadPath = Paths.get(relativePath);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

// Generate a unique file name
		String uniqueFileName = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();

// Full path to save the file
		Path fullPath = uploadPath.resolve(uniqueFileName);
		Files.copy(videoFile.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);

// Return the full relative path like: ABCCollege/batch_1/course_101/student_5/17136999_file.pdf
		return Paths.get(relativePath, uniqueFileName).toString().replace("\\", "/");
	}

	private String sanitize(String input) {
		return input.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
	}
}
