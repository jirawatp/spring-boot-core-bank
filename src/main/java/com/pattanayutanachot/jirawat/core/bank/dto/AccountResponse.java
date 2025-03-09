package com.pattanayutanachot.jirawat.core.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        String accountNumber,
        BigDecimal balance,
        LocalDateTime createdAt
) {}