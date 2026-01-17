package pl.edu.pwr.s34641bank.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateClientRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull @PositiveOrZero BigDecimal initialBalance
) {}
