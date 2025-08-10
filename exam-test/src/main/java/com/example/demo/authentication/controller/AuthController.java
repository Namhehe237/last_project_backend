package com.example.demo.authentication.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.authentication.dto.request.AuthRequest;
import com.example.demo.authentication.dto.response.AuthResponse;
import com.example.demo.authentication.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
   

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            System.out.println("Testing AuthService...");
            AuthResponse response = authService.authenticate(request);
            System.out.println("AuthService success!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("AuthService Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    AuthResponse.builder()
                            .accessToken("Error: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/test")
    public String test() {
        return "Auth controller is working!";
    }

   
}