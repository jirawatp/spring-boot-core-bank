package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DepositRequest(
        @NotBlank(message = "Account number is required")
        String accountNumber,

        @Min(value = 1, message = "Deposit amount must be at least 1 THB")
        @NotNull(message = "Amount is required")
        BigDecimal amount
) {}