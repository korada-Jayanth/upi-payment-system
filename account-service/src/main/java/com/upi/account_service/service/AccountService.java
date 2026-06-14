package com.upi.account_service.service;

import com.upi.account_service.client.UserServiceClient;
import com.upi.account_service.dto.AccountRequestDto;
import com.upi.account_service.dto.AccountResponseDto;
import com.upi.account_service.dto.UserDto;
import com.upi.account_service.entity.AccountStatus;
import com.upi.account_service.entity.BankAccount;
import com.upi.account_service.exception.AccountNotFoundException;
import com.upi.account_service.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final BankAccountRepository accountRepository;
    private final UserServiceClient userServiceClient;

    public AccountResponseDto linkAccount(AccountRequestDto request) {
        // 1. Verify user exists via Feign
        log.info("Verifying user {} via User Service", request.getUserId());
        UserDto user = userServiceClient.getUserById(request.getUserId());
        log.info("User verified: {}", user.getEmail());

        // 2. Prevent duplicate account
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already linked");
        }

        // 3. Generate UPI ID from user's mobile
        String upiId = user.getMobile() + "@upi";

        // 4. Build and save
        BankAccount account = BankAccount.builder()
                .userId(request.getUserId())
                .accountNumber(request.getAccountNumber())
                .bankName(request.getBankName())
                .ifscCode(request.getIfscCode())
                .balance(request.getInitialBalance())
                .status(AccountStatus.ACTIVE)
                .upiId(upiId)
                .build();

        BankAccount saved = accountRepository.save(account);
        log.info("Account linked successfully with UPI ID: {}", upiId);
        return mapToResponse(saved);
    }

    public List<AccountResponseDto> getAccountsByUserId(Long userId) {
        // Verify user exists first
        userServiceClient.getUserById(userId);
        return accountRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).toList();
    }

    public AccountResponseDto getAccountById(Long accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
        return mapToResponse(account);
    }

    public BigDecimal getBalance(Long accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
        return account.getBalance();
    }

    public AccountResponseDto updateBalance(Long accountId, BigDecimal amount) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        BigDecimal newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(newBalance);
        return mapToResponse(accountRepository.save(account));
    }

    // --- Mapper ---
    private AccountResponseDto mapToResponse(BankAccount account) {
        return AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .bankName(account.getBankName())
                .ifscCode(account.getIfscCode())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .userId(account.getUserId())
                .upiId(account.getUpiId())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
