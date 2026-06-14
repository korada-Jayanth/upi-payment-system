package com.upi.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;
}
