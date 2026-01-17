package pl.edu.pwr.s34641bank.repo;

import org.springframework.stereotype.Repository;
import pl.edu.pwr.s34641bank.domain.Client;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryClientRepository implements ClientRepository {

    private final ConcurrentHashMap<UUID, Client> store = new ConcurrentHashMap<>();

    @Override
    public Client save(Client client) {
        store.put(client.getId(), client);
        return client;
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }
}