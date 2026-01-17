package pl.edu.pwr.s34641bank.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Client {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private BigDecimal balance;

    public Client(UUID id, String firstName, String lastName, BigDecimal balance) {
        this.id = Objects.requireNonNull(id);
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.balance = Objects.requireNonNull(balance);
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    public synchronized BigDecimal getBalance() { return balance; }

    public synchronized void setBalance(BigDecimal balance) {
        this.balance = Objects.requireNonNull(balance);
    }
}