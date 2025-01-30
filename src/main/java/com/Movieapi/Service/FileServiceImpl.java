package com.Movieapi.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
    
    @Value("${file.upload.dir}")
    private String BASE_PATH;

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        if (path.contains("..")) {
            throw new IllegalArgumentException("Invalid path, potentially malicious input detected.");
        }

        // Use Paths.get() to correctly resolve and normalize the directory path
        Path directoryPath = Paths.get(BASE_PATH, path).normalize();
        String fileName = file.getOriginalFilename(); // Get the name of the file

        System.out.println("Base path: " + BASE_PATH); // Debugging line
        System.out.println("Directory path: " + directoryPath); // Debugging line
        
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name is invalid!");
        }

        // Check file type (e.g., images and videos)
        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new IllegalArgumentException("Unsupported file type! Only images and videos are allowed.");
        }
        
        // Handle file name collisions by adding a UUID (optional)
        String uniqueFileName = generateUniqueFileName(fileName);

        // Construct the full file path
        Path filePath = directoryPath.resolve(uniqueFileName).normalize();

        // Create directory if it doesn't exist
        File directory = directoryPath.toFile();
        try {
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Failed to create directory at " + directoryPath);
                }
            }
        } catch (IOException e) {
            // Handle the IOException properly
            throw new IOException("Error creating directory at " + directoryPath, e);
        }

        // Copy the file to the specified path
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Error writing file to path " + filePath, e);
        }

        return uniqueFileName;  // Return the unique file name
    }


    private String generateUniqueFileName(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return UUID.randomUUID().toString();  // Generate UUID if no extension
        }
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;  // Return UUID with extension
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        // Resolve the file path
        Path filePath = Paths.get(BASE_PATH, path, fileName).normalize();
        File file = filePath.toFile();

        if (!file.exists()) {
            throw new FileNotFoundException("File not found at path: " + filePath);
        }

        return new FileInputStream(file);
    }
}
