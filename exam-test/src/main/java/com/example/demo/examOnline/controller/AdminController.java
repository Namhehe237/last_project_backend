package com.example.demo.examOnline.controller;

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

import com.example.demo.examOnline.dto.request.CreateUserRequest;
import com.example.demo.examOnline.dto.request.DeleteUserRequest;
import com.example.demo.examOnline.dto.request.GetUserListRequest;
import com.example.demo.examOnline.dto.response.ClassResponseDTO;
import com.example.demo.examOnline.dto.response.UserResponseDTO;
import com.example.demo.examOnline.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/list-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getListUser(@RequestBody GetUserListRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getUserList(request, pageable));

    }

    @PostMapping("/list-class")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClassResponseDTO>> getListClass(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "false") Boolean includeArchived) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getClassList(includeArchived, pageable));

    }

    @PostMapping("/delete-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getListUser(@RequestBody DeleteUserRequest request) {

        adminService.deleteUser(request);

        return ResponseEntity.ok("Xoá User thành công");
    }

    @PostMapping("/add-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addUser(@RequestBody CreateUserRequest request) {
        adminService.addUser(request);
        return ResponseEntity.ok("Thêm User thành công");
    }

}