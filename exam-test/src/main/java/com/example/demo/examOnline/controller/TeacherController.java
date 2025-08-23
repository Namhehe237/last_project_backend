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
import com.example.demo.examOnline.dto.request.DeleteStudentRequest;
import com.example.demo.examOnline.dto.request.JoinClassRequest;
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

    @PostMapping("/class-detail/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ClassResponseDTO> getClassInformationDetail(@PathVariable Integer classId) {
        return ResponseEntity.ok(classService.getClassInformationDetail(classId));
    }

    @PostMapping("class-detail/update/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> updateClassInformationDetail(@PathVariable Integer classId,
            @RequestBody UpdateClassInformationRequest request) {

        classService.updateClassInfomationDetail(classId, request);

        return ResponseEntity.ok("Update thông tin thành công");
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

    @PostMapping("/remove-student/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> deleteStudentFromClass(@PathVariable Integer classId,
            @RequestBody DeleteStudentRequest request) {

        classService.deleteStudentFromClass(classId, request);

        return ResponseEntity.ok("Xóa student khỏi class thành công");
    }

    @PostMapping("/request-join-class")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> requestJoinClass(@RequestBody JoinClassRequest request) {

        System.out.println("Received request: " + request);
        System.out.println("ClassRequestIds: " + request.getClassRequestId());
        System.out.println("Status: " + request.getStatus());

        classService.handleRequestJoinClass(request);

        return ResponseEntity.ok("Xử lý request thành công");
    }
}