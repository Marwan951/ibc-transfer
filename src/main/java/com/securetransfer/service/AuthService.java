package com.securetransfer.service;

import com.securetransfer.dto.AuthDtos;
import com.securetransfer.entity.Account;
import com.securetransfer.entity.User;
import com.securetransfer.exception.ResourceNotFoundException;
import com.securetransfer.repository.AccountRepository;
import com.securetransfer.repository.UserRepository;
import com.securetransfer.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = accountRepository.findByUserUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        return new AuthDtos.LoginResponse(
                token,
                user.getUsername(),
                user.getFullName(),
                account.getAccountNumber(),
                account.getBalance()
        );
    }
    public AuthDtos.SignupResponse signup(AuthDtos.SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        userRepository.save(user);

        String accountNumber = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        accountRepository.save(account);

        return new AuthDtos.SignupResponse(user.getUsername(), user.getFullName(), accountNumber);
    }
}
