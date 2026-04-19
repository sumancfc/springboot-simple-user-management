package com.example.usermanagement.service;

import com.example.usermanagement.config.JwtUtils;
import com.example.usermanagement.dto.LoginRequest;
import com.example.usermanagement.dto.LoginResponse;
import com.example.usermanagement.dto.RegisterRequest;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.exception.ResourceNotFoundException;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username '" + request.getUsername() + "' is already taken.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Hash the password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRoles(new HashSet<>(Set.of("ROLE_USER")));
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        // Authenticate user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid username or password.");
        }

        // ENFORCE SINGLE SESSION: Kill the previous token if it exists
        String oldToken = redisTemplate.opsForValue().get("active_session:" + user.getUsername());
        if (oldToken != null) {
            long remainingTime = jwtUtils.getRemainingTime(oldToken);
            if (remainingTime > 0) {
                tokenBlacklistService.blacklistToken(oldToken, remainingTime);
            }
        }

        // Generate New Tokens
        String accessToken = jwtUtils.generateToken(user.getUsername(), user.getRoles());
        String refreshToken = jwtUtils.generateRefreshToken();

        // Update Redis: Active Session (matches JWT lifespan)
        redisTemplate.opsForValue().set(
                "active_session:" + user.getUsername(),
                accessToken,
                Duration.ofSeconds(jwtUtils.getRemainingTime(accessToken))
        );

        // Update Redis: Refresh Token (7 days)
        redisTemplate.opsForValue().set(
                "refresh:" + refreshToken,
                user.getUsername(),
                Duration.ofDays(7)
        );

        // Store refresh token by user for validation during logout
        redisTemplate.opsForValue().set(
                "refresh_token_by_user:" + user.getUsername(),
                refreshToken,
                Duration.ofDays(7)
        );

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getRoles(),
                "Login Successful!"
        );
    }

    public void logout(String accessToken, String refreshToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new BusinessException("Access Token is required");
        }

        // Get user from Access Token
        String username = jwtUtils.getUsernameFromToken(accessToken);

        // STRICT Refresh Token Validation
        if (refreshToken != null) {
            String storedUsername = redisTemplate.opsForValue().get("refresh:" + refreshToken);

            // If the token is old/not in Redis, it won't have a username associated with it
            if (storedUsername == null || !storedUsername.equals(username)) {
                log.warn("Invalid or expired refresh token provided for logout: {}", refreshToken);
                throw new RuntimeException("Invalid or expired Refresh Token. Logout failed.");
            }

            // Only delete if validation passed
            redisTemplate.delete("refresh:" + refreshToken);
            redisTemplate.delete("refresh_token_by_user:" + username);
        }

        // Blacklist Access Token
        long ttl = jwtUtils.getRemainingTime(accessToken);
        if (ttl > 0) {
            tokenBlacklistService.blacklistToken(accessToken, ttl);
        }

        // Kill Active Session
        redisTemplate.delete("active_session:" + username);
    }

    public LoginResponse refreshSession(String refreshToken) {
        // Check Redis
        String username = redisTemplate.opsForValue().get("refresh:" + refreshToken);

        if (username == null) {
            throw new BusinessException("Session expired. Please log in again.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for the given session."));

        redisTemplate.delete("refresh:" + refreshToken);

        // Generate only a new Access Token
        String newAccessToken = jwtUtils.generateToken(username, user.getRoles());

        String newRefreshToken = jwtUtils.generateRefreshToken();

        redisTemplate.opsForValue().set(
                "active_session:" + username,
                newAccessToken,
                Duration.ofSeconds(jwtUtils.getRemainingTime(newAccessToken))
        );

        redisTemplate.opsForValue().set(
                "refresh:" + newRefreshToken,
                username,
                Duration.ofDays(7)
        );

        // Update refresh token by user
        redisTemplate.opsForValue().set(
                "refresh_token_by_user:" + username,
                newRefreshToken,
                Duration.ofDays(7)
        );

        // Return a response
        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                username,
                user.getRoles(),
                "Token refreshed and rotated successfully"
        );
    }
}
