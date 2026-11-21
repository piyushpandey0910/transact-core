
package com.banking.transactcore.repository;

import com.banking.transactcore.entity.FailedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedTransactionRepository
        extends JpaRepository<FailedTransaction, Long> {
}
