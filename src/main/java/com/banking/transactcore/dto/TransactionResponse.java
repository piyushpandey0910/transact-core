
package com.banking.transactcore.dto;

import com.banking.transactcore.enums.TransactionStatus;
import com.banking.transactcore.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private String transactionReference;
    private TransactionType transactionType;
    private TransactionStatus status;
    private BigDecimal amount;
    private String accountNumber;
    private BigDecimal updatedBalance;
    private String description;
    private LocalDateTime createdAt;

    public TransactionResponse(Long id, String transactionReference,
                               TransactionType transactionType,
                               TransactionStatus status,
                               BigDecimal amount,
                               String accountNumber,
                               BigDecimal updatedBalance,
                               String description,
                               LocalDateTime createdAt) {

        this.id = id;
        this.transactionReference = transactionReference;
        this.transactionType = transactionType;
        this.status = status;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.updatedBalance = updatedBalance;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getUpdatedBalance() {
        return updatedBalance;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
