package com.example.usermanagement.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}