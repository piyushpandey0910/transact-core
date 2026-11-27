package com.banking.transactcore.dto;

import com.banking.transactcore.enums.TransactionStatus;
import com.banking.transactcore.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionHistoryResponse {

    private Long id;
    private String transactionReference;
    private TransactionType transactionType;
    private TransactionStatus status;
    private BigDecimal amount;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private String description;
    private LocalDateTime createdAt;

    public TransactionHistoryResponse(Long id, String transactionReference,
                                      TransactionType transactionType,
                                      TransactionStatus status,
                                      BigDecimal amount,
                                      String sourceAccountNumber,
                                      String destinationAccountNumber,
                                      String description,
                                      LocalDateTime createdAt) {
        this.id = id;
        this.transactionReference = transactionReference;
        this.transactionType = transactionType;
        this.status = status;
        this.amount = amount;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}