
package com.banking.transactcore.repository;

import com.banking.transactcore.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(String transactionReference);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.sourceAccount.accountNumber = :accountNumber
           OR t.destinationAccount.accountNumber = :accountNumber
        ORDER BY t.createdAt DESC
        """)
    List<Transaction> findAccountTransactions(
            @Param("accountNumber") String accountNumber);
}
