package pl.edu.pwr.s34641bank.service;


import org.springframework.stereotype.Service;
import pl.edu.pwr.s34641bank.domain.Client;
import pl.edu.pwr.s34641bank.domain.Transaction;
import pl.edu.pwr.s34641bank.domain.TransactionStatus;
import pl.edu.pwr.s34641bank.domain.TransactionType;
import pl.edu.pwr.s34641bank.dto.ClientResponse;
import pl.edu.pwr.s34641bank.dto.CreateClientRequest;
import pl.edu.pwr.s34641bank.dto.TransactionResponse;
import pl.edu.pwr.s34641bank.repo.ClientRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class BankService {

    private final ClientRepository clientRepository;

    public BankService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientResponse registerClient(CreateClientRequest req) {
        UUID id = UUID.randomUUID();
        Client client = new Client(id, req.firstName(), req.lastName(), req.initialBalance());
        clientRepository.save(client);
        return toClientResponse(client);
    }

    public ClientResponse getClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        return toClientResponse(client);
    }

    // Operacje na saldzie robimy w synchronized na obiekcie Client (bez bazy danych)
    public TransactionResponse deposit(UUID clientId, BigDecimal amount) {
        Transaction tx = depositInternal(clientId, amount);
        return toTransactionResponse(tx);
    }

    public TransactionResponse transfer(UUID clientId, BigDecimal amount) {
        Transaction tx = transferInternal(clientId, amount);
        return toTransactionResponse(tx);
    }

    private Transaction depositInternal(UUID clientId, BigDecimal amount) {
        Instant now = Instant.now();
        UUID txId = UUID.randomUUID();

        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null) {
            return new Transaction(txId, clientId, TransactionType.DEPOSIT, amount,
                    TransactionStatus.REJECTED, null, "Client not registered", now);
        }

        synchronized (client) {
            BigDecimal newBalance = client.getBalance().add(amount);
            client.setBalance(newBalance);
            clientRepository.save(client);
            return new Transaction(txId, clientId, TransactionType.DEPOSIT, amount,
                    TransactionStatus.ACCEPTED, newBalance, "Deposit accepted", now);
        }
    }

    private Transaction transferInternal(UUID clientId, BigDecimal amount) {
        Instant now = Instant.now();
        UUID txId = UUID.randomUUID();

        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null) {
            return new Transaction(txId, clientId, TransactionType.TRANSFER, amount,
                    TransactionStatus.REJECTED, null, "Client not registered", now);
        }

        synchronized (client) {
            BigDecimal current = client.getBalance();
            if (current.compareTo(amount) < 0) {
                return new Transaction(txId, clientId, TransactionType.TRANSFER, amount,
                        TransactionStatus.DECLINED, current, "Insufficient funds", now);
            }

            BigDecimal newBalance = current.subtract(amount);
            client.setBalance(newBalance);
            clientRepository.save(client);
            return new Transaction(txId, clientId, TransactionType.TRANSFER, amount,
                    TransactionStatus.ACCEPTED, newBalance, "Transfer accepted", now);
        }
    }

    private static ClientResponse toClientResponse(Client c) {
        return new ClientResponse(c.getId(), c.getFirstName(), c.getLastName(), c.getBalance());
    }

    private static TransactionResponse toTransactionResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getClientId(),
                t.getType(),
                t.getAmount(),
                t.getStatus(),
                t.getBalanceAfter(),
                t.getMessage(),
                t.getCreatedAt()
        );
    }
}
