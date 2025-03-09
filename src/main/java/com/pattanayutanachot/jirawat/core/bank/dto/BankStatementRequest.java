package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.constraints.*;

public record BankStatementRequest(
        @NotNull(message = "Year is required")
        @Min(value = 1900, message = "Invalid year")
        Integer year,

        @NotNull(message = "Month is required")
        @Min(value = 1, message = "Invalid month")
        @Max(value = 12, message = "Invalid month")
        Integer month,

        @NotBlank(message = "Account number is required")
        String accountNumber,

        @NotBlank(message = "PIN is required")
        String pin
) {}