package pl.edu.pwr.s34641bank.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull UUID clientId,
        @NotNull @Positive BigDecimal amount
) {}
