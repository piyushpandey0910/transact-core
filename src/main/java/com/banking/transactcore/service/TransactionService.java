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
import com.banking.transactcore.dto.TransferRequest;
import com.banking.transactcore.dto.TransactionHistoryResponse;
import com.banking.transactcore.exception.TransactionNotFoundException;

import java.math.BigDecimal;
import java.util.List;

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

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        if (request.getSourceAccountNumber().equals(request.getDestinationAccountNumber())) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }

        List<String> accountNumbers = List.of(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber()
        );

        List<Account> accounts = accountRepository.findAccountsForTransfer(accountNumbers);

        if (accounts.size() != 2) {
            throw new AccountNotFoundException("One or both accounts were not found");
        }

        Account sourceAccount = accounts.stream()
                .filter(account -> account.getAccountNumber().equals(request.getSourceAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        Account destinationAccount = accounts.stream()
                .filter(account -> account.getAccountNumber().equals(request.getDestinationAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in source account. Available balance: "
                            + sourceAccount.getBalance());
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(referenceGenerator.generateReference());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setAmount(request.getAmount());
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setDescription(request.getDescription());

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponse(
                savedTransaction.getId(),
                savedTransaction.getTransactionReference(),
                savedTransaction.getTransactionType(),
                savedTransaction.getStatus(),
                savedTransaction.getAmount(),
                sourceAccount.getAccountNumber(),
                sourceAccount.getBalance(),
                savedTransaction.getDescription(),
                savedTransaction.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getAccountTransactionHistory(String accountNumber) {

        if (!accountRepository.existsByAccountNumber(accountNumber)) {
            throw new AccountNotFoundException("Account not found with number: " + accountNumber);
        }

        List<Transaction> transactions = transactionRepository.findAccountTransactions(accountNumber);

        return transactions.stream()
                .map(this::convertToHistoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionHistoryResponse getTransactionByReference(String transactionReference) {

        Transaction transaction = transactionRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with reference: " + transactionReference));

        return convertToHistoryResponse(transaction);
    }

    private TransactionHistoryResponse convertToHistoryResponse(Transaction transaction) {

        String sourceAccountNumber = transaction.getSourceAccount() != null
                ? transaction.getSourceAccount().getAccountNumber()
                : null;

        String destinationAccountNumber = transaction.getDestinationAccount() != null
                ? transaction.getDestinationAccount().getAccountNumber()
                : null;

        return new TransactionHistoryResponse(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getAmount(),
                sourceAccountNumber,
                destinationAccountNumber,
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}