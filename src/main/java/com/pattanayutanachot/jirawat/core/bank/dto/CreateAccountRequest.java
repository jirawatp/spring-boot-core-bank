package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank(message = "Citizen ID is required")
        String citizenId,  // Target user

        @NotBlank(message = "Thai name is required")
        String thaiName,

        @NotBlank(message = "English name is required")
        String englishName,

        @Min(value = 0, message = "Initial deposit must be zero or positive")
        @NotNull(message = "Initial deposit is required")
        BigDecimal initialDeposit
) {}