package com.example.demo.examOnline.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.dto.request.DeleteStudentRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.service.ClassService;
import com.example.demo.examOnline.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final UserService userService;
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

    @PostMapping("/list-student/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<UserResponseDTO>> getListStudentOfClass(@PathVariable Integer classId) {

        List<UserResponseDTO> listUser = userService.getListStudentOfClass(classId);

        return ResponseEntity.ok(listUser);

    }

    @PostMapping("/list-class/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ClassResponseDTO>> getListClassOfTeacher(@PathVariable Integer teacherId) {

        List<ClassResponseDTO> listClass = classService.getListClassOfTeacher(teacherId);

        return ResponseEntity.ok(listClass);

    }

    // @PostMapping("delete-student/{classId}")
    // @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    // public ResponseEntity<String> deleteStudent(@PathVariable Integer classId,
    // @RequestBody DeleteStudentRequest deleteStudentRequest) {

    // System.out.println("RequestBody studentIds = " +
    // deleteStudentRequest.getListStudentId());

    // classService.deleteStudent(classId, deleteStudentRequest.getListStudentId());

    // return ResponseEntity.ok("Xoá thành công");
    // }

    @PostMapping("/delete-student/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer classId,
            @RequestBody DeleteStudentRequest deleteStudentRequest) {

        System.out.println("Class ID: " + classId);
        System.out.println("DeleteStudentRequest object: " + deleteStudentRequest);

        if (deleteStudentRequest != null) {
            System.out.println("Student IDs: " + deleteStudentRequest.getListStudentId());
        }

        // Thêm lại validation
        if (deleteStudentRequest.getListStudentId() == null || deleteStudentRequest.getListStudentId().isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách studentId không được null hoặc rỗng");
        }

        // Thêm lại logic xử lý
        classService.deleteStudent(classId, deleteStudentRequest.getListStudentId());

        return ResponseEntity.ok("Xoá thành công");
    }
}