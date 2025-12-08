package com.example.demo.examOnline.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.dto.request.CreateClassRequest;
import com.example.demo.examOnline.dto.request.DeleteClassRequest;
import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.HandleJoinRequestRequest;
import com.example.demo.examOnline.dto.request.UpdateClassInformationRequest;
import com.example.demo.examOnline.dto.response.ClassOptionResponse;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.CreateClassResponse;
import com.example.demo.examOnline.dto.response.RequestJoinClassResponse;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.service.ClassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;

    @PostMapping("/create-class")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<CreateClassResponse> createClass(@RequestBody CreateClassRequest request) {
        CreateClassResponse response = classService.createClass(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/class-detail/request/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER','STUDENT')")
    public ResponseEntity<Page<RequestJoinClassResponse>> getRequestOfClass(@PathVariable Integer classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(classService.getRequestOfClass(classId, pageable));
    }

    @PostMapping("/class-detail/list-student/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public ResponseEntity<Page<UserResponseDTO>> getStudentOfClass(@PathVariable Integer classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(classService.getStudentOfClass(classId, pageable));
    }

    @PostMapping("/class-detail/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
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

    @PostMapping("/delete-class")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> deleteClass(@RequestBody DeleteClassRequest request) {
        classService.deleteClass(request);
        return ResponseEntity.ok("Xóa lớp học thành công");
    }

    @PostMapping("/request-join-class")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> requestJoinClass(@RequestBody HandleJoinRequestRequest request) {

        classService.handleRequestJoinClass(request);

        return ResponseEntity.ok("Xử lý request thành công");
    }

    @PostMapping("/list-class/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<ClassResponseDTO>> getListClassOfTeacher(@PathVariable Integer teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ClassResponseDTO> listClasses = classService.getClassOfTeacher(teacherId, pageable);

        return ResponseEntity.ok(listClasses);
    }

    @PostMapping("/class-options/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ClassOptionResponse>> getClassOptions(@PathVariable Integer teacherId) {
        return ResponseEntity.ok(classService.getClassOptions(teacherId));
    }

    @PostMapping("/remove-student/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> deleteStudentFromClass(@PathVariable Integer classId,
            @RequestBody DeleteUserRequest request) {

        classService.deleteStudentFromClass(classId, request);

        return ResponseEntity.ok("Xóa student khỏi class thành công");
    }

}
