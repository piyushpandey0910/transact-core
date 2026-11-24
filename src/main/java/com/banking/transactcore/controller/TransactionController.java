
package com.banking.transactcore.controller;

import com.banking.transactcore.dto.DepositRequest;
import com.banking.transactcore.dto.TransactionResponse;
import com.banking.transactcore.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Deposit money into an account
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request) {

        TransactionResponse response =
                transactionService.deposit(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
