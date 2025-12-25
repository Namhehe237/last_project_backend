package com.example.demo.examOnline.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {

    public String uploadVideo(MultipartFile videoFile, String folder) throws IOException;

    public void deleteVideo(String publicId) throws IOException;

    public String uploadImage(MultipartFile imageFile, String folder) throws IOException;

    public void deleteImage(String publicId) throws IOException;

    public String uploadAssignmentFile(MultipartFile file, String folder) throws IOException;

    public void deleteAssignmentFile(String publicId) throws IOException;
}
