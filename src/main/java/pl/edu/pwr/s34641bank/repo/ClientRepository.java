package pl.edu.pwr.s34641bank.repo;

import pl.edu.pwr.s34641bank.domain.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(UUID id);
    boolean existsById(UUID id);
}
