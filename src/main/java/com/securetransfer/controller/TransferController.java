package com.securetransfer.controller;

import com.securetransfer.dto.TransferDtos;
import com.securetransfer.entity.Account;
import com.securetransfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/transfers")
    public ResponseEntity<TransferDtos.TransferResponse> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransferDtos.TransferRequest request) {
        TransferDtos.TransferResponse response = transferService.transfer(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transfers/history")
    public ResponseEntity<List<TransferDtos.TransactionHistoryItem>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transferService.getTransactionHistory(userDetails.getUsername()));
    }

    @GetMapping("/account/me")
    public ResponseEntity<Map<String, Object>> getMyAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Account account = transferService.getAccount(userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "accountNumber", account.getAccountNumber(),
                "balance", account.getBalance(),
                "username", userDetails.getUsername(),
                "fullName", account.getUser().getFullName()
        ));
    }
}
