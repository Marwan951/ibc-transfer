package com.securetransfer.repository;

import com.securetransfer.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByUserId(Long userId);
    Optional<Account> findByUserUsername(String username);
}
