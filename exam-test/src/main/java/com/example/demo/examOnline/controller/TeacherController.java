package com.example.demo.examOnline.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.domain.Classes;
import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.JoinClassRequest;
import com.example.demo.examOnline.dto.request.HandleJoinRequestRequest;
import com.example.demo.examOnline.dto.request.UpdateClassInformationRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.repository.ClassRepository;
import com.example.demo.examOnline.service.ClassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final ClassService classService;

    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String manageCourses() {
        return "Manage Courses - ADMIN and TEACHER can access";
    }

    @GetMapping("/grades")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public String manageGrades() {
        return "Manage Grades - ADMIN and TEACHER can access";
    }


    @PostMapping("/list-class/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<ClassResponseDTO>> getListClassOfTeacher(@PathVariable Integer teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ClassResponseDTO> listClasses = classService.getClassOfTeacher(teacherId,pageable);

        return ResponseEntity.ok(listClasses);
    }

    @PostMapping("/list-student/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<UserResponseDTO>> getListStudentOfClass(
            @PathVariable Integer classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponseDTO> listStudent = classService.getStudentOfClass(classId, pageable);

        return ResponseEntity.ok(listStudent);
    }


}