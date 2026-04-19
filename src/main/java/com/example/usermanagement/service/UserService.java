package com.example.usermanagement.service;

import com.example.usermanagement.dto.ProfileUpdateDTO;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.ResourceNotFoundException;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public User updateProfile(String username, ProfileUpdateDTO dto) {
        User user = findByUsername(username);

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        return userRepository.save(user);
    }

    @Transactional
    public User assignRole(Long userId, String role) {
        User user = getUserById(userId);

        String roleName = formatRoleName(role);
        user.getRoles().add(roleName);

        return userRepository.save(user);
    }

    @Transactional
    public User revokeRole(Long userId, String role) {
        User user = getUserById(userId);

        String roleName = formatRoleName(role);
        user.getRoles().remove(roleName);

        return userRepository.save(user);
    }

    private String formatRoleName(String role) {
        return role.startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();
    }
}
