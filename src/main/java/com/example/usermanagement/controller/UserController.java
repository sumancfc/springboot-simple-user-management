package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ChangePasswordRequest;
import com.example.usermanagement.dto.ProfileUpdateDTO;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> getMyProfile(Principal principal) {
        return ResponseEntity.ok(userService.findByUsername(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateUserProfile(Principal principal, @RequestBody ProfileUpdateDTO updateData) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), updateData));
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(@RequestParam Long userId, @RequestParam String role) {
        return ResponseEntity.ok(userService.assignRole(userId, role));
    }

    @PostMapping("/revoke-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> revoke(@RequestParam Long userId, @RequestParam String role) {
        return ResponseEntity.ok(userService.revokeRole(userId, role));
    }

    @GetMapping("/roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<String>> getUserRoles(@PathVariable Long userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user.getRoles());
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> getAllAvailableRoles() {
        return ResponseEntity.ok(List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @GetMapping("/avatar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> getAvatar(Principal principal) {
        String avatarUrl = userService.getUserAvatar(principal.getName());
        return ResponseEntity.ok(Map.of("url", avatarUrl));
    }

    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            Principal principal,
            @RequestParam("file") MultipartFile file) {

        String imageUrl = userService.updateAvatar(principal.getName(), file);
        return ResponseEntity.ok(Map.of(
                "message", "Avatar updated successfully",
                "url", imageUrl
        ));
    }

    @PostMapping("/remove-avatar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> removeAvatar(Principal principal) {
        userService.removeAvatar(principal.getName());
        return ResponseEntity.ok(Map.of("message", "Avatar removed successfully"));
    }
}
