package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record VerifyTransferRequest(
        @NotBlank(message = "Withdrawal account number is required")
        String withdrawalAccountNumber,

        @NotBlank(message = "Recipient account number is required")
        String recipientAccountNumber,

        @NotNull(message = "Transfer amount is required")
        @Min(value = 1, message = "Transfer amount must be at least 1 THB")
        BigDecimal amount
) {}