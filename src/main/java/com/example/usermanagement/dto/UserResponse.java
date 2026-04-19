package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private static final String DEFAULT_AVATAR = "https://res.cloudinary.com/sumancfc/image/upload/v1776631428/avatar_j91ep0.jpg";

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private String bio;
    private Set<String> roles;
    private String avatarUrl;

    public String getAvatarUrl() {
        return (avatarUrl == null || avatarUrl.isEmpty()) ? DEFAULT_AVATAR : avatarUrl;
    }
}