
package com.banking.transactcore.dto;

import com.banking.transactcore.enums.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private Long customerId;
    private String customerName;
    private AccountType accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public AccountResponse(Long id, String accountNumber,
                           Long customerId, String customerName,
                           AccountType accountType, BigDecimal balance,
                           LocalDateTime createdAt) {

        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
