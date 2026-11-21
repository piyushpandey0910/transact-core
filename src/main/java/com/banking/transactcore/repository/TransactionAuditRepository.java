
package com.banking.transactcore.repository;

import com.banking.transactcore.entity.TransactionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionAuditRepository
        extends JpaRepository<TransactionAudit, Long> {
}
