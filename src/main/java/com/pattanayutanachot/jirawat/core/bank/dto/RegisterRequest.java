package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        @NotBlank(message = "Password is required")
        String password
) {}