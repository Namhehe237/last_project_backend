package com.example.demo.examOnline.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {



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


}