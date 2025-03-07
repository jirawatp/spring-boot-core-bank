package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.model.Transaction;
import com.pattanayutanachot.jirawat.core.bank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.deposit(transaction));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.withdraw(transaction));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestParam Long fromAccountId,
                                                @RequestParam Long toAccountId,
                                                @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(transactionService.transfer(fromAccountId, toAccountId, amount));
    }
}