
package com.banking.transactcore.service;

import com.banking.transactcore.dto.AccountRequest;
import com.banking.transactcore.dto.AccountResponse;
import com.banking.transactcore.entity.Account;
import com.banking.transactcore.entity.Customer;
import com.banking.transactcore.enums.AccountType;
import com.banking.transactcore.exception.AccountNotFoundException;
import com.banking.transactcore.exception.CustomerNotFoundException;
import com.banking.transactcore.repository.AccountRepository;
import com.banking.transactcore.repository.CustomerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    private final SecureRandom random = new SecureRandom();

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    // Create a new bank account
    @Transactional
    public AccountResponse createAccount(AccountRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with ID: " + request.getCustomerId()
                ));

        Account account = new Account();

        account.setAccountNumber(generateAccountNumber());
        account.setCustomer(customer);
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);

        Account savedAccount = accountRepository.save(account);

        return convertToResponse(savedAccount);
    }

    // Get account by ID
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with ID: " + id
                ));

        return convertToResponse(account);
    }

    // Get account by account number
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + accountNumber
                ));

        return convertToResponse(account);
    }

    // Get all accounts belonging to a customer
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(
                    "Customer not found with ID: " + customerId
            );
        }

        return accountRepository.findByCustomer_Id(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Generate a unique 12-digit account number
    private String generateAccountNumber() {

        String accountNumber;

        do {
            accountNumber = String.format("%012d",
                    random.nextLong(1_000_000_000_000L));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    // Convert Entity to Response DTO
    private AccountResponse convertToResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomer().getId(),
                account.getCustomer().getFullName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}
