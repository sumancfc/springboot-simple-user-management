package com.example.usermanagement.controller;

import com.example.usermanagement.dto.*;
import com.example.usermanagement.service.AuthService;
import com.example.usermanagement.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, @RequestBody LogoutRequest logoutRequest) {
        String accessToken = SecurityUtils.extractToken(request);
        String refreshToken = logoutRequest.getRefreshToken();

        authService.logout(accessToken, refreshToken);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refreshSession(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
