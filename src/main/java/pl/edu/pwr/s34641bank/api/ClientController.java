package pl.edu.pwr.s34641bank.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.s34641bank.dto.ClientResponse;
import pl.edu.pwr.s34641bank.dto.CreateClientRequest;
import pl.edu.pwr.s34641bank.service.BankService;

import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final BankService bankService;

    public ClientController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse register(@Valid @RequestBody CreateClientRequest req) {
        return bankService.registerClient(req);
    }

    @GetMapping("/{id}")
    public ClientResponse get(@PathVariable UUID id) {
        return bankService.getClient(id);
    }
}