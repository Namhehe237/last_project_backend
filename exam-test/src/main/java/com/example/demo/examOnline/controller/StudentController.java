package com.example.demo.examOnline.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public String viewCourses() {
        return "View Courses - All authenticated users can access";
    }

    @GetMapping("/grades")
    @PreAuthorize("hasRole('STUDENT')")
    public String viewOwnGrades() {
        return "View Own Grades - Only STUDENT can access";
    }
}