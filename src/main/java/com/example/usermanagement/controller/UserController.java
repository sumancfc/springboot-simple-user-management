package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ProfileUpdateDTO;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> getMyProfile(java.security.Principal principal) {
        return ResponseEntity.ok(userService.findByUsername(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> updateUserProfile(java.security.Principal principal, @RequestBody ProfileUpdateDTO updateData) {
        User updatedUser = userService.updateProfile(principal.getName(), updateData);

        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> assignRole(@RequestParam Long userId, @RequestParam String role) {
        return ResponseEntity.ok(userService.assignRole(userId, role));
    }

    @PostMapping("/revoke-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> revoke(@RequestParam Long userId, @RequestParam String role) {
        return ResponseEntity.ok(userService.revokeRole(userId, role));
    }

    @GetMapping("/roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<String>> getUserRoles(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        return ResponseEntity.ok(user.getRoles());
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> getAllAvailableRoles() {
        return ResponseEntity.ok(List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"));
    }
}
