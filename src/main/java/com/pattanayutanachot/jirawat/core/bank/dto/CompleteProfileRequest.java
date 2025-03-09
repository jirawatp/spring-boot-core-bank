package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.constraints.*;

public record CompleteProfileRequest(
        @NotBlank(message = "Citizen ID is required")
        @Pattern(regexp = "\\d{13}", message = "Citizen ID must be 13 digits")
        String citizenId,

        @NotBlank(message = "Thai name is required")
        String thaiName,

        @NotBlank(message = "English name is required")
        String englishName,

        @Pattern(regexp = "\\d{6}", message = "PIN must be 6 digits")
        String pin
) {}