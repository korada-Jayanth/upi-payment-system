package com.upi.account_service.controller;

import com.upi.account_service.dto.AccountRequestDto;
import com.upi.account_service.dto.AccountResponseDto;
import com.upi.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    // POST /api/accounts/link
    @PostMapping("/link")
    public ResponseEntity<AccountResponseDto> linkAccount(
            @Valid @RequestBody AccountRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.linkAccount(request));
    }

    // GET /api/accounts/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    // GET /api/accounts/{accountId}
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDto> getAccountById(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }

    // GET /api/accounts/{accountId}/balance
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(Map.of("balance", accountService.getBalance(accountId)));
    }

    // PATCH /api/accounts/{accountId}/balance
    // Used internally by Payment Service later
    @PatchMapping("/{accountId}/balance")
    public ResponseEntity<AccountResponseDto> updateBalance(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.updateBalance(accountId, amount));
    }
}
