package com.example.usermanagement.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateDTO {
    @Size(max = 50)
    private String fullName;
    @Size(max = 10)
    private String gender;
    @Size(max = 15)
    private String phoneNumber;
    @Size(max = 255)
    private String bio;
}
