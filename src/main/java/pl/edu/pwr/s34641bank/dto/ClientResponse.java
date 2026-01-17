package pl.edu.pwr.s34641bank.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String firstName,
        String lastName,
        BigDecimal balance
) {}
