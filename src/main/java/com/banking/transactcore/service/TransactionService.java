package com.banking.transactcore.service;

import com.banking.transactcore.dto.DepositRequest;
import com.banking.transactcore.dto.TransactionResponse;
import com.banking.transactcore.dto.WithdrawalRequest;
import com.banking.transactcore.entity.Account;
import com.banking.transactcore.entity.Transaction;
import com.banking.transactcore.enums.TransactionStatus;
import com.banking.transactcore.enums.TransactionType;
import com.banking.transactcore.exception.AccountNotFoundException;
import com.banking.transactcore.exception.InsufficientBalanceException;
import com.banking.transactcore.repository.AccountRepository;
import com.banking.transactcore.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionReferenceGenerator referenceGenerator;

    public TransactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            TransactionReferenceGenerator referenceGenerator) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.referenceGenerator = referenceGenerator;
    }

    @Transactional
    public TransactionResponse deposit(DepositRequest request) {

        Account account = accountRepository.findByAccountNumberForUpdate(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + request.getAccountNumber()));

        BigDecimal updatedBalance = account.getBalance().add(request.getAmount());
        account.setBalance(updatedBalance);

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(referenceGenerator.generateReference());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setAmount(request.getAmount());
        transaction.setSourceAccount(account);
        transaction.setDescription(request.getDescription());

        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponse(
                savedTransaction.getId(),
                savedTransaction.getTransactionReference(),
                savedTransaction.getTransactionType(),
                savedTransaction.getStatus(),
                savedTransaction.getAmount(),
                account.getAccountNumber(),
                account.getBalance(),
                savedTransaction.getDescription(),
                savedTransaction.getCreatedAt()
        );
    }

    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest request) {

        Account account = accountRepository.findByAccountNumberForUpdate(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + request.getAccountNumber()));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance: " + account.getBalance());
        }

        BigDecimal updatedBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(updatedBalance);

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(referenceGenerator.generateReference());
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setAmount(request.getAmount());
        transaction.setSourceAccount(account);
        transaction.setDescription(request.getDescription());

        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponse(
                savedTransaction.getId(),
                savedTransaction.getTransactionReference(),
                savedTransaction.getTransactionType(),
                savedTransaction.getStatus(),
                savedTransaction.getAmount(),
                account.getAccountNumber(),
                account.getBalance(),
                savedTransaction.getDescription(),
                savedTransaction.getCreatedAt()
        );
    }
}