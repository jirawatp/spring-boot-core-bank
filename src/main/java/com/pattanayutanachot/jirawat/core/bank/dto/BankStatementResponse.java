package com.pattanayutanachot.jirawat.core.bank.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BankStatementResponse(
        String accountNumber,
        String date, // Formatted Date
        String time, // Formatted Time
        String code, // Transaction Code
        String channel, // Channel Type
        BigDecimal debit, // Money Out (null if credit)
        BigDecimal credit, // Money In (null if debit)
        BigDecimal balance, // Balance after transaction
        String remark // Description
) {}