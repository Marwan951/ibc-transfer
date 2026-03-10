package com.securetransfer.repository;

import com.securetransfer.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.id = :accountId OR t.recipientAccount.id = :accountId ORDER BY t.createdAt DESC")
    List<Transaction> findAllByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.id = :accountId OR t.recipientAccount.id = :accountId ORDER BY t.createdAt DESC")
    Page<Transaction> findAllByAccountIdPaged(@Param("accountId") Long accountId, Pageable pageable);
}
