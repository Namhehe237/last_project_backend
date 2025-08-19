package com.example.demo.examOnline.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.examOnline.dto.request.UpdateClassInformationRequest;
import com.example.demo.examOnline.dto.request.UpdateUserInformationRequest;
import com.example.demo.examOnline.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/general")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/update-info/{userId}")
    public ResponseEntity<String> updateUserInformation(@PathVariable Integer userId,@RequestBody UpdateUserInformationRequest request){
        userService.updateUserInformation(userId, request);

        return ResponseEntity.ok("Update thông tin thành công");
    }
}
