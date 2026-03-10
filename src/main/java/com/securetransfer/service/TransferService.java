package com.securetransfer.service;

import com.securetransfer.dto.TransferDtos;
import com.securetransfer.entity.Account;
import com.securetransfer.entity.Transaction;
import com.securetransfer.exception.InsufficientFundsException;
import com.securetransfer.exception.ResourceNotFoundException;
import com.securetransfer.repository.AccountRepository;
import com.securetransfer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransferDtos.TransferResponse transfer(String senderUsername, TransferDtos.TransferRequest request) {
        // Get sender account
        Account senderAccount = accountRepository.findByUserUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));

        // Validate recipient exists
        Account recipientAccount = accountRepository.findByAccountNumber(request.getRecipientAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account not found: " + request.getRecipientAccountNumber()));

        // Prevent self-transfer
        if (senderAccount.getAccountNumber().equals(recipientAccount.getAccountNumber())) {
            return TransferDtos.TransferResponse.failure("Cannot transfer to your own account");
        }

        // Check sufficient funds
        if (senderAccount.getBalance().compareTo(request.getAmount()) < 0) {
            return TransferDtos.TransferResponse.failure("Insufficient funds. Available balance: $" + senderAccount.getBalance());
        }

        // Perform transfer
        senderAccount.setBalance(senderAccount.getBalance().subtract(request.getAmount()));
        recipientAccount.setBalance(recipientAccount.getBalance().add(request.getAmount()));

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        // Record transaction
        Transaction transaction = Transaction.builder()
                .senderAccount(senderAccount)
                .recipientAccount(recipientAccount)
                .amount(request.getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Fund Transfer")
                .status(Transaction.TransactionStatus.SUCCESS)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return TransferDtos.TransferResponse.success(senderAccount.getBalance(), saved.getId().toString());
    }

    public List<TransferDtos.TransactionHistoryItem> getTransactionHistory(String username) {
        Account account = accountRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        List<Transaction> transactions = transactionRepository.findAllByAccountId(account.getId());

        return transactions.stream().map(tx -> {
            TransferDtos.TransactionHistoryItem item = new TransferDtos.TransactionHistoryItem();
            item.setId(tx.getId());
            item.setAmount(tx.getAmount());
            item.setCreatedAt(tx.getCreatedAt());
            item.setDescription(tx.getDescription());
            item.setStatus(tx.getStatus().name());

            boolean isSender = tx.getSenderAccount().getId().equals(account.getId());
            item.setType(isSender ? "SENT" : "RECEIVED");

            Account counterparty = isSender ? tx.getRecipientAccount() : tx.getSenderAccount();
            item.setCounterpartyAccount(counterparty.getAccountNumber());
            item.setCounterpartyName(counterparty.getUser() != null ? counterparty.getUser().getFullName() : "Unknown");

            return item;
        }).collect(Collectors.toList());
    }

    public Account getAccount(String username) {
        return accountRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }
}
