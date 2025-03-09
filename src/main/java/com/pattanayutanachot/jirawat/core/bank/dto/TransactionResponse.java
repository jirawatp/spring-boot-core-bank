package com.pattanayutanachot.jirawat.core.bank.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponse(
        Long transactionId,
        String accountNumber,
        String transactionType,
        BigDecimal amount,
        LocalDateTime createdAt
) {}