package com.example.usermanagement.controller;

import com.example.usermanagement.config.JwtUtils;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.TokenBlacklistService;
import com.example.usermanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.boot.security.autoconfigure.web.reactive.ReactiveWebSecurityAutoConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;
    private final RedisTemplate<String, String> redisTemplate;

    public UserController(UserService userService, JwtUtils jwtUtils, TokenBlacklistService tokenBlacklistService, RedisTemplate redisTemplate) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.tokenBlacklistService = tokenBlacklistService;
        this.redisTemplate = redisTemplate;
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
            String username = authenticatedUser.get().getUsername();

            // Generate both tokens
            String accessToken = jwtUtils.generateToken(username);
            String refreshToken = jwtUtils.generateRefreshToken();

            // Save Refresh Token in Redis (Linked to username)
            // We set this to 7 days so the user stays logged in
            redisTemplate.opsForValue().set(
                    "refresh:" + refreshToken,
                    username,
                    Duration.ofDays(7)
            );

            // Return both to the frontend
            Map<String, String> response = java.util.Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );

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
    public ResponseEntity<?> logout(HttpServletRequest request, @RequestBody Map<String, String> body) {
        String accessToken = extractToken(request);
        String refreshToken = body.get("refreshToken");

        if (accessToken != null) {
            // Blacklist the current Access Token
            String username = jwtUtils.getUsernameFromToken(accessToken);
            long ttl = jwtUtils.getRemainingTime(accessToken);
            tokenBlacklistService.blacklistToken(accessToken, ttl);

            // Delete the Refresh Token from Redis
            if (refreshToken != null) {
                redisTemplate.delete("refresh:" + refreshToken);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Logout successful",
                    "user", username,
                    "status", "Tokens invalidated and session cleared"
            ));
        }

        return ResponseEntity.badRequest().body(Map.of("error", "No access token provided"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        // Look up the refresh token in Redis
        String username = redisTemplate.opsForValue().get("refresh_" + refreshToken);

        if(username == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired refresh token"));
        }

        // Generate a fresh access token
        String newAccessToken = jwtUtils.generateToken(username);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "refreshToken", refreshToken
        ));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
