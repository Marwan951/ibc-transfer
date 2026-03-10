package com.securetransfer.controller;

import com.securetransfer.dto.AuthDtos;
import com.securetransfer.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.LoginResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthDtos.SignupResponse> signup(@Valid @RequestBody AuthDtos.SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }
}
