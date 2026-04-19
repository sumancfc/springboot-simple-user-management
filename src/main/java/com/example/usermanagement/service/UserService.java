package com.example.usermanagement.service;

import com.example.usermanagement.dto.ChangePasswordRequest;
import com.example.usermanagement.dto.ProfileUpdateDTO;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.BusinessException;
import com.example.usermanagement.exception.ResourceNotFoundException;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // TODO:: Remove UserService Constructor with @RequiredArgsConstructor annotations in future
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper: Internal method to get the Entity (stays private/protected)
    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    public UserResponse getUserById(Long id) {
        return convertToResponse(getUserEntityById(id));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return convertToResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String username, ProfileUpdateDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse assignRole(Long userId, String role) {
        User user = getUserEntityById(userId);
        user.getRoles().add(formatRoleName(role));
        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse revokeRole(Long userId, String role) {
        User user = getUserEntityById(userId);
        user.getRoles().remove(formatRoleName(role));
        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Current password does not match our records.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException("New password cannot be the same as the old password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private String formatRoleName(String role) {
        return role.startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();
    }

    /**
     * The Mapping Engine
     * This private method handles the conversion from Entity to DTO in one place.
     */
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .roles(user.getRoles())
                .build();
    }
}
