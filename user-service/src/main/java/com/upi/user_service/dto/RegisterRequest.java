package com.upi.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Valid email is required")
    @NotBlank
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Valid 10-digit Indian mobile number required")
    private String phoneNumber;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Pattern(regexp = "^\\d{6}$", message = "UPI PIN must be exactly 6 digits")
    private String upiPin;
}
