package com.securetransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferDtos {

    @Data
    public static class TransferRequest {
        @NotBlank(message = "Recipient account number is required")
        private String recipientAccountNumber;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;

        private String description;
    }

    @Data
    public static class TransferResponse {
        private boolean success;
        private String message;
        private BigDecimal newBalance;
        private String transactionId;

        public static TransferResponse success(BigDecimal newBalance, String txId) {
            TransferResponse r = new TransferResponse();
            r.success = true;
            r.message = "Transfer successful!";
            r.newBalance = newBalance;
            r.transactionId = txId;
            return r;
        }

        public static TransferResponse failure(String message) {
            TransferResponse r = new TransferResponse();
            r.success = false;
            r.message = message;
            return r;
        }
    }

    @Data
    public static class TransactionHistoryItem {
        private Long id;
        private String type; // SENT or RECEIVED
        private BigDecimal amount;
        private String counterpartyAccount;
        private String counterpartyName;
        private LocalDateTime createdAt;
        private String description;
        private String status;
    }
}
