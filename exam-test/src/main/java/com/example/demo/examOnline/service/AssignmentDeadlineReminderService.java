package com.example.demo.examOnline.service;

import com.example.demo.examOnline.domain.Post;
import com.example.demo.examOnline.domain.enums.PostType;
import com.example.demo.examOnline.repository.PostRepository;
import com.example.demo.examOnline.repository.StudentClassRepository;
import com.example.demo.examOnline.repository.AssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentDeadlineReminderService {
   
    public void checkAssignmentDeadlines();
}

