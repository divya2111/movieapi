package com.Movieapi.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Movieapi.Service.FileService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/file/")
public class FileController {
	
	@Autowired
	private  FileService fileService;
	
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}
	@Value("${file.upload.dir}")
	private String path;
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFileHandler(
	    @RequestPart("poster") MultipartFile posterFile, 
	    @RequestPart("video") MultipartFile videoFile) throws IOException {

	    // Extract file extensions
	    String posterExtension = getFileExtension(posterFile.getOriginalFilename());
	    String videoExtension = getFileExtension(videoFile.getOriginalFilename());

	    // Determine upload paths for poster and video
	    String posterUploadPath = determineUploadPath(posterExtension); // Should resolve to "posters"
	    String videoUploadPath = determineUploadPath(videoExtension);   // Should resolve to "videos"

	    // Ensure directories exist
	    ensureDirectoryExists(posterUploadPath);
	    ensureDirectoryExists(videoUploadPath);

	    // Upload the poster and video files
	    String uploadedPosterFileName = fileService.uploadFile(posterUploadPath, posterFile);
	    String uploadedVideoFileName = fileService.uploadFile(videoUploadPath, videoFile);

	    return ResponseEntity.ok("Files uploaded: Poster - " + uploadedPosterFileName + ", Video - " + uploadedVideoFileName);
	}	

	    
	@GetMapping("/{fileName}")
	public void serviceFileHandler(@PathVariable("fileName") String fileName,HttpServletResponse response ) throws IOException {
		InputStream resourceFile = fileService.getResourceFile(path,fileName);
		// Extract file extension
	    String fileExtension = getFileExtension(fileName);
	    
	    // Set the content type based on the file type
	    if (fileExtension.equalsIgnoreCase("png") || fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
	        response.setContentType(MediaType.IMAGE_PNG_VALUE);  // Or MediaType.IMAGE_JPEG_VALUE
	    } 
	    else if (fileExtension.equalsIgnoreCase("mp4")) {
	        response.setContentType("video/mp4");  // Manually set content type for mp4 videos
	    } 
	    else if (fileExtension.equalsIgnoreCase("webm")) {
	        response.setContentType("video/webm");  // Manually set content type for webm videos
	    }
	    else if (fileExtension.equalsIgnoreCase("avi")) {
	        response.setContentType("video/avi");  // Manually set content type for avi videos
	    } 
	    else {
	        response.setContentType("application/octet-stream"); 
	    }//
	        //response.setContentType(MediaType.IMAGE_PNG_VALUE);
		StreamUtils.copy(resourceFile, response.getOutputStream());
    }
	private String getFileExtension(String fileName) {
	    if (fileName == null || !fileName.contains(".")) {
	        return "";  // Return empty string if no extension is found
	    }
	    
	    return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();  // Extract and return the file extension in lowercase
	}
	 // Method to determine the appropriate upload path based on the file extension
    private String determineUploadPath(String fileExtension) {
        if (fileExtension.equalsIgnoreCase("png") || fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
            return "posters";  // Folder for image files (posters)
        } else if (fileExtension.equalsIgnoreCase("mp4")) {
            return "videos";  // Folder for video files
        } else if (fileExtension.equalsIgnoreCase("webm")) {
            return "videos";  // Folder for webm videos
        } else if (fileExtension.equalsIgnoreCase("avi")) {
            return "videos";  // Folder for avi videos
        } 
        else if (fileExtension.equalsIgnoreCase("mkv")) {
            return "videos";  // Folder for avi videos
        }else {
            throw new IllegalArgumentException("Unsupported file type: " + fileExtension);  // Throw exception for unsupported file types
        }
    }

    // Method to ensure the upload directory exists
    private void ensureDirectoryExists(String uploadPath) {
        Path directoryPath = Paths.get(path, uploadPath);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);  // Create the directory if it doesn't exist
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + directoryPath, e);
            }
        }
    }

}
