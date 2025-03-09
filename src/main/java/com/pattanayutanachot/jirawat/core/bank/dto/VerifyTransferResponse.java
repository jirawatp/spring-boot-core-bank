package com.pattanayutanachot.jirawat.core.bank.dto;

import lombok.Builder;

@Builder
public record VerifyTransferResponse(
        boolean canTransfer,
        String message,
        String recipientThaiName,
        String recipientEnglishName
) {}