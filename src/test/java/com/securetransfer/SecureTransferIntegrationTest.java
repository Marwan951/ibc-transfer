package com.securetransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securetransfer.dto.AuthDtos;
import com.securetransfer.dto.TransferDtos;
import com.securetransfer.entity.Account;
import com.securetransfer.entity.User;
import com.securetransfer.repository.AccountRepository;
import com.securetransfer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecureTransferIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = userRepository.save(User.builder()
                .username("testuser1")
                .password(passwordEncoder.encode("password123"))
                .email("test1@test.com")
                .fullName("Test User One")
                .build());

        accountRepository.save(Account.builder()
                .accountNumber("TEST-001")
                .balance(new BigDecimal("1000.00"))
                .user(testUser1)
                .build());

        testUser2 = userRepository.save(User.builder()
                .username("testuser2")
                .password(passwordEncoder.encode("password456"))
                .email("test2@test.com")
                .fullName("Test User Two")
                .build());

        accountRepository.save(Account.builder()
                .accountNumber("TEST-002")
                .balance(new BigDecimal("500.00"))
                .user(testUser2)
                .build());
    }

    @Test
    void testLoginSuccess() throws Exception {
        AuthDtos.LoginRequest request = new AuthDtos.LoginRequest();
        request.setUsername("testuser1");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser1"));
    }

    @Test
    void testLoginFailure() throws Exception {
        AuthDtos.LoginRequest request = new AuthDtos.LoginRequest();
        request.setUsername("testuser1");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/account/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTransferSuccess() throws Exception {
        String token = loginAndGetToken("testuser1", "password123");

        TransferDtos.TransferRequest transferRequest = new TransferDtos.TransferRequest();
        transferRequest.setRecipientAccountNumber("TEST-002");
        transferRequest.setAmount(new BigDecimal("200.00"));
        transferRequest.setDescription("Test transfer");

        mockMvc.perform(post("/api/transfers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.newBalance").value(800.00));
    }

    @Test
    void testTransferInsufficientFunds() throws Exception {
        String token = loginAndGetToken("testuser1", "password123");

        TransferDtos.TransferRequest transferRequest = new TransferDtos.TransferRequest();
        transferRequest.setRecipientAccountNumber("TEST-002");
        transferRequest.setAmount(new BigDecimal("5000.00"));

        mockMvc.perform(post("/api/transfers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testTransactionHistory() throws Exception {
        String token = loginAndGetToken("testuser1", "password123");

        mockMvc.perform(get("/api/transfers/history")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        AuthDtos.LoginRequest request = new AuthDtos.LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AuthDtos.LoginResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthDtos.LoginResponse.class);
        return response.getToken();
    }
}
