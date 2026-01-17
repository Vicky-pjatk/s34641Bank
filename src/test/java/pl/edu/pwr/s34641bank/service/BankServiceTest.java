package pl.edu.pwr.s34641bank.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.edu.pwr.s34641bank.domain.Client;
import pl.edu.pwr.s34641bank.domain.TransactionStatus;
import pl.edu.pwr.s34641bank.dto.CreateClientRequest;
import pl.edu.pwr.s34641bank.repo.ClientRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankServiceTest {

    private ClientRepository repo;
    private BankService service;

    @BeforeEach
    void setup() {
        repo = mock(ClientRepository.class);
        service = new BankService(repo);
    }

    @Test
    void registerClient_savesAndReturnsClient() {
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var resp = service.registerClient(new CreateClientRequest("Jan", "Kowalski", new BigDecimal("100.00")));

        assertThat(resp.id()).isNotNull();
        assertThat(resp.balance()).isEqualByComparingTo("100.00");

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getFirstName()).isEqualTo("Jan");
    }

    @Test
    void deposit_whenClientNotRegistered_returnsRejected() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        var tx = service.deposit(id, new BigDecimal("50.00"));

        assertThat(tx.status()).isEqualTo(TransactionStatus.REJECTED);
        assertThat(tx.balanceAfter()).isNull();
    }

    @Test
    void transfer_whenInsufficientFunds_returnsDeclinedAndDoesNotChangeBalance() {
        UUID id = UUID.randomUUID();
        Client client = new Client(id, "A", "B", new BigDecimal("10.00"));
        when(repo.findById(id)).thenReturn(Optional.of(client));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var tx = service.transfer(id, new BigDecimal("25.00"));

        assertThat(tx.status()).isEqualTo(TransactionStatus.DECLINED);
        assertThat(tx.balanceAfter()).isEqualByComparingTo("10.00");
        assertThat(client.getBalance()).isEqualByComparingTo("10.00");
        verify(repo, never()).save(argThat(c -> ((Client)c).getBalance().compareTo(new BigDecimal("10.00")) != 0));
    }

    @Test
    void deposit_happyPath_increasesBalance() {
        UUID id = UUID.randomUUID();
        Client client = new Client(id, "A", "B", new BigDecimal("10.00"));
        when(repo.findById(id)).thenReturn(Optional.of(client));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var tx = service.deposit(id, new BigDecimal("7.50"));

        assertThat(tx.status()).isEqualTo(TransactionStatus.ACCEPTED);
        assertThat(tx.balanceAfter()).isEqualByComparingTo("17.50");
        assertThat(client.getBalance()).isEqualByComparingTo("17.50");
        verify(repo).save(any());
    }
}
