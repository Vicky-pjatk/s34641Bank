package pl.edu.pwr.s34641bank.dto;


import pl.edu.pwr.s34641bank.domain.TransactionStatus;
import pl.edu.pwr.s34641bank.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID clientId,
        TransactionType type,
        BigDecimal amount,
        TransactionStatus status,
        BigDecimal balanceAfter,
        String message,
        Instant createdAt
) {}
