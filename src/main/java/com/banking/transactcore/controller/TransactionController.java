package com.banking.transactcore.controller;

import com.banking.transactcore.dto.DepositRequest;
import com.banking.transactcore.dto.TransactionResponse;
import com.banking.transactcore.dto.WithdrawalRequest;
import com.banking.transactcore.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banking.transactcore.dto.TransferRequest;
import com.banking.transactcore.dto.TransactionHistoryResponse;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request) {

        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody WithdrawalRequest request) {

        TransactionResponse response = transactionService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request) {

        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/account/{accountNumber}/history")
    public ResponseEntity<List<TransactionHistoryResponse>> getAccountTransactionHistory(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                transactionService.getAccountTransactionHistory(accountNumber)
        );
    }

    @GetMapping("/reference/{transactionReference}")
    public ResponseEntity<TransactionHistoryResponse> getTransactionByReference(
            @PathVariable String transactionReference) {

        return ResponseEntity.ok(
                transactionService.getTransactionByReference(transactionReference)
        );
    }

    @GetMapping("/reference/{transactionReference}/status")
    public ResponseEntity<TransactionHistoryResponse> getTransactionStatus(
            @PathVariable String transactionReference) {

        return ResponseEntity.ok(
                transactionService.getTransactionByReference(transactionReference)
        );
    }
}