package com.securetransfer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

public class AuthDtos {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String username;
        private String fullName;
        private String accountNumber;
        private java.math.BigDecimal balance;

        public LoginResponse(String token, String username, String fullName,
                             String accountNumber, java.math.BigDecimal balance) {
            this.token = token;
            this.username = username;
            this.fullName = fullName;
            this.accountNumber = accountNumber;
            this.balance = balance;
        }
    }

    @Data
    public static class SignupRequest {
        @NotBlank private String username;
        @NotBlank private String password;
        @Email
        private String email;
        @NotBlank private String fullName;
    }

    @Data
    @AllArgsConstructor
    public static class SignupResponse {
        private String username;
        private String fullName;
        private String accountNumber;
    }
}
