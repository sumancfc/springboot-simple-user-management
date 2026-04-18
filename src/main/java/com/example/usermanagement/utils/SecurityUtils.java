package com.example.usermanagement.utils;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtils {
    public static String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}