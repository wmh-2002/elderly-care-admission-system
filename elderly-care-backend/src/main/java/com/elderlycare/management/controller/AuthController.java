package com.elderlycare.management.controller;

import com.elderlycare.management.dto.ApiResponse;
import com.elderlycare.management.dto.LoginRequest;
import com.elderlycare.management.dto.LoginResponse;
import com.elderlycare.management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse.LoginData>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(loginResponse.getData()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody LoginRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("注册成功"));
    }
}

