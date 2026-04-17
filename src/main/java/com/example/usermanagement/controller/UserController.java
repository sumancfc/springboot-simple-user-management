package com.example.usermanagement.controller;

import com.example.usermanagement.config.JwtUtils;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.TokenBlacklistService;
import com.example.usermanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    public UserController(UserService userService, JwtUtils jwtUtils, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            userService.userRegister(user);
            // Returning a Map ensures the response is {"message": "..."}
            return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
        } catch (DataIntegrityViolationException e) {
            // Handle that "root" duplicate username error specifically
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username or Email already exists"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> authenticatedUser = userService.userLogin(user.getUsername(), user.getPassword());

        if(authenticatedUser.isPresent()) {
            // Create the token for the verified user
            String token = jwtUtils.generateToken(authenticatedUser.get().getUsername());

            Map<String, String> response = java.util.Map.of("token", token);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "Invalid credentials"));
        }
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id).map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.status(404).body(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);

        if(isDeleted) {
            return ResponseEntity.ok("User with ID " + id + " has been deleted.");
        } else {
            return ResponseEntity.status(404).body("User not found with ID " + id);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractToken(request);

        if (token != null) {
            // Extract username just for the response message
            String username = jwtUtils.getUsernameFromToken(token);
            long ttl = jwtUtils.getRemainingTime(token);

            tokenBlacklistService.blacklistToken(token, ttl);

            return ResponseEntity.ok(Map.of(
                    "message", "Logout successful",
                    "user", username,
                    "expiry_cleared_in_seconds", ttl
            ));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "No token provided"));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
