package com.example.demo.examOnline.service;

import com.example.demo.examOnline.dto.request.CreatePostRequest;
import com.example.demo.examOnline.dto.response.PostResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    public PostResponseDTO createPost(CreatePostRequest request, MultipartFile attachmentFile);

    public List<PostResponseDTO> getClassFeed(Integer classId, Integer currentUserId);

    public PostResponseDTO updatePost(Integer postId, Integer userId, CreatePostRequest request);

    public void deletePost(Integer postId, Integer userId);

}
