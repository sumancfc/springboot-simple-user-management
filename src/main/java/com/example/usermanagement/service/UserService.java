package com.example.usermanagement.service;

import com.example.usermanagement.dto.ProfileUpdateDTO;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
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

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean deleteUser(Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(String username, ProfileUpdateDTO dto) {
        User user = findByUsername(username);

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        return userRepository.save(user);
    }

    public User assignRole(Long userId, String role) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        String roleName = role.startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();

        user.getRoles().add(roleName);

        return userRepository.save(user);
    }

    public User revokeRole(Long userId, String role) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        String roleName = role.startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();

        user.getRoles().remove(roleName);

        return userRepository.save(user);
    }
}
