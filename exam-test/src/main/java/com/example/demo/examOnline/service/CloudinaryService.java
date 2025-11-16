package com.example.demo.examOnline.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final String cloudName;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudName = cloudName;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String uploadVideo(MultipartFile videoFile, String folder) throws IOException {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    videoFile.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", folder,
                            "format", "mp4"
                    )
            );

            String videoUrl = (String) uploadResult.get("secure_url");
            log.info("Video uploaded successfully to Cloudinary: {}", videoUrl);
            return videoUrl;
        } catch (IOException e) {
            log.error("Error uploading video to Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to upload video to Cloudinary", e);
        }
    }

    public void deleteVideo(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
            log.info("Video deleted successfully from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Error deleting video from Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to delete video from Cloudinary", e);
        }
    }

    public String uploadImage(MultipartFile imageFile, String folder) throws IOException {
        try {
            log.info("Uploading image to Cloudinary - folder: {}, size: {} bytes, contentType: {}", 
                    folder, imageFile.getSize(), imageFile.getContentType());
            
            // Simple upload without transformation for now
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    imageFile.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", folder
                    )
            );

            log.info("Cloudinary upload response keys: {}", uploadResult.keySet());
            
            String imageUrl = (String) uploadResult.get("secure_url");
            if (imageUrl == null || imageUrl.isEmpty()) {
                log.error("Received null or empty secure_url from Cloudinary. Response: {}", uploadResult);
                // Try to construct URL from public_id
                String publicId = (String) uploadResult.get("public_id");
                if (publicId != null && !publicId.isEmpty()) {
                    imageUrl = String.format("https://res.cloudinary.com/%s/image/upload/%s", this.cloudName, publicId);
                    log.info("Constructed URL from public_id: {}", imageUrl);
                } else {
                    throw new IOException("Failed to get image URL from Cloudinary response");
                }
            }
            
            log.info("Image uploaded successfully to Cloudinary: {}", imageUrl);
            return imageUrl;
        } catch (Exception e) {
            log.error("Error uploading image to Cloudinary: {}", e.getMessage(), e);
            e.printStackTrace();
            if (e instanceof IOException) {
                throw e;
            }
            throw new IOException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }

    public void deleteImage(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
            log.info("Image deleted successfully from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Error deleting image from Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to delete image from Cloudinary", e);
        }
    }

    public String uploadAssignmentFile(MultipartFile file, String folder) throws IOException {
        try {
            // Validate file size (max 100MB)
            long maxSize = 100 * 1024 * 1024; // 100MB in bytes
            if (file.getSize() > maxSize) {
                throw new IOException("File size exceeds maximum allowed size of 100MB");
            }

            // Validate file type (docx, excel, pdf, ppt)
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            String fileExtension = fileName != null && fileName.contains(".") 
                    ? fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase() 
                    : "";

            boolean isValidType = false;
            if (contentType != null) {
                isValidType = contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || // .docx
                        contentType.equals("application/msword") || // .doc
                        contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") || // .xlsx
                        contentType.equals("application/vnd.ms-excel") || // .xls
                        contentType.equals("application/pdf") || // .pdf
                        contentType.equals("application/vnd.ms-powerpoint") || // .ppt
                        contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation"); // .pptx
            }

            // Also check by extension as fallback
            if (!isValidType) {
                isValidType = fileExtension.equals("docx") || fileExtension.equals("doc") ||
                        fileExtension.equals("xlsx") || fileExtension.equals("xls") ||
                        fileExtension.equals("pdf") ||
                        fileExtension.equals("pptx") || fileExtension.equals("ppt");
            }

            if (!isValidType) {
                throw new IOException("Invalid file type. Only DOCX, XLSX, PDF, and PPT files are allowed.");
            }

            log.info("Uploading assignment file to Cloudinary - folder: {}, fileName: {}, size: {} bytes, contentType: {}", 
                    folder, fileName, file.getSize(), contentType);

            // Prepare upload options
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadOptions = (Map<String, Object>) ObjectUtils.asMap(
                    "resource_type", "raw",
                    "folder", folder,
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true
            );
            
            // Add filename if available to preserve original name and extension
            if (fileName != null && !fileName.isEmpty()) {
                // Clean filename for Cloudinary (remove special characters but keep extension)
                String cleanFileName = fileName.replaceAll("[^a-zA-Z0-9_.-]", "_");
                // Set public_id with full filename including extension
                uploadOptions.put("public_id", folder + "/" + cleanFileName);
            }

            // Upload as raw file (not image or video)
            Map<?, ?> uploadResult;
            try {
                uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        uploadOptions
                );
            } catch (Exception e) {
                log.error("Cloudinary upload exception: {}", e.getMessage(), e);
                throw new IOException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
            }

            log.info("Cloudinary upload response keys: {}", uploadResult.keySet());
            log.debug("Cloudinary upload response: {}", uploadResult);

            String fileUrl = (String) uploadResult.get("secure_url");
            if (fileUrl == null || fileUrl.isEmpty()) {
                log.error("Received null or empty secure_url from Cloudinary. Response: {}", uploadResult);
                String publicId = (String) uploadResult.get("public_id");
                if (publicId != null && !publicId.isEmpty()) {
                    // Construct URL with public_id (which includes extension)
                    fileUrl = String.format("https://res.cloudinary.com/%s/raw/upload/%s", this.cloudName, publicId);
                    log.info("Constructed URL from public_id: {}", fileUrl);
                } else {
                    throw new IOException("Failed to get file URL from Cloudinary response. Response: " + uploadResult);
                }
            }

            // Ensure URL has proper extension for download
            // If the original filename had an extension but URL doesn't, append it
            if (fileName != null && fileName.contains(".")) {
                String extension = fileName.substring(fileName.lastIndexOf("."));
                // Check if URL already has this extension
                if (!fileUrl.toLowerCase().endsWith(extension.toLowerCase())) {
                    // Append extension to ensure proper download
                    fileUrl = fileUrl + extension;
                    log.info("Appended extension to URL: {}", fileUrl);
                }
            }

            log.info("Assignment file uploaded successfully to Cloudinary: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("Error uploading assignment file to Cloudinary: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error uploading assignment file to Cloudinary: {}", e.getMessage(), e);
            throw new IOException("Failed to upload assignment file to Cloudinary: " + e.getMessage(), e);
        }
    }

    public void deleteAssignmentFile(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
            log.info("Assignment file deleted successfully from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Error deleting assignment file from Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to delete assignment file from Cloudinary", e);
        }
    }
}

