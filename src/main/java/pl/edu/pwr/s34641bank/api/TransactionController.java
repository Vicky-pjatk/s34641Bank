package pl.edu.pwr.s34641bank.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.s34641bank.dto.DepositRequest;
import pl.edu.pwr.s34641bank.dto.TransactionResponse;
import pl.edu.pwr.s34641bank.dto.TransferRequest;
import pl.edu.pwr.s34641bank.service.BankService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final BankService bankService;

    public TransactionController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/deposit")
    public TransactionResponse deposit(@Valid @RequestBody DepositRequest req) {
        return bankService.deposit(req.clientId(), req.amount());
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest req) {
        return bankService.transfer(req.clientId(), req.amount());
    }
}