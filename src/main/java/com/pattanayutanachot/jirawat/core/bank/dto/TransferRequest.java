package com.pattanayutanachot.jirawat.core.bank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferRequest(
        @NotBlank(message = "Sender account number is required")
        String fromAccountNumber,

        @NotBlank(message = "Recipient account number is required")
        String toAccountNumber,

        @NotNull(message = "Amount is required")
        @Min(value = 1, message = "Transfer amount must be at least 1 THB")
        BigDecimal amount,

        @NotBlank(message = "PIN is required")
        String pin
) {}