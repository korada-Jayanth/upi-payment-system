package com.upi.transaction_service.controller;

import com.upi.transaction_service.dto.TransactionResponseDto;
import com.upi.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    // GET /api/transactions/{transactionId}
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getTransaction(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getByTransactionId(transactionId));
    }

    // GET /api/transactions/account/{accountId}
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsByAccount(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getByAccountId(accountId));
    }
}