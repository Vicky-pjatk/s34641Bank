package pl.edu.pwr.s34641bank.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final UUID clientId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final TransactionStatus status;
    private final BigDecimal balanceAfter;
    private final String message;
    private final Instant createdAt;

    public Transaction(UUID id,
                       UUID clientId,
                       TransactionType type,
                       BigDecimal amount,
                       TransactionStatus status,
                       BigDecimal balanceAfter,
                       String message,
                       Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.clientId = Objects.requireNonNull(clientId);
        this.type = Objects.requireNonNull(type);
        this.amount = Objects.requireNonNull(amount);
        this.status = Objects.requireNonNull(status);
        this.balanceAfter = Objects.requireNonNull(balanceAfter);
        this.message = Objects.requireNonNull(message);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public TransactionStatus getStatus() { return status; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
