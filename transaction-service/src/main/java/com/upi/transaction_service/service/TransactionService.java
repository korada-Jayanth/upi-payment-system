package com.upi.transaction_service.service;

import com.upi.transaction_service.client.AccountServiceClient;
import com.upi.transaction_service.dto.AccountDto;
import com.upi.transaction_service.dto.PaymentEventDto;
import com.upi.transaction_service.dto.TransactionCompletedEventDto;
import com.upi.transaction_service.dto.TransactionResponseDto;
import com.upi.transaction_service.entity.Transaction;
import org.springframework.transaction.annotation.Transactional;
import com.upi.transaction_service.entity.TransactionStatus;
import com.upi.transaction_service.exception.TransactionNotFoundException;
import com.upi.transaction_service.kafka.TransactionEventPublisher;
import com.upi.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final TransactionEventPublisher eventPublisher;

    // ─────────────────────────────────────────────────────────────
    // @Transactional ensures:
    // 1. Save PROCESSING record
    // 2. Verify accounts
    // 3. Update to COMPLETED
    // All happen atomically — if step 3 fails, step 1 rolls back
    // ─────────────────────────────────────────────────────────────
    @Transactional
    public void processPaymentEvent(PaymentEventDto event) {

        log.info("Processing payment event: {}", event.getTransactionId());

        // Guard: skip if already processed (idempotency)
        if (transactionRepository.findByTransactionId(event.getTransactionId()).isPresent()) {
            log.warn("Duplicate event received for TXN: {} — skipping", event.getTransactionId());
            return;
        }

        // Step 1 — Save with PROCESSING status
        Transaction transaction = Transaction.builder()
                .transactionId(event.getTransactionId())
                .senderVpa(event.getSenderVpa())
                .receiverVpa(event.getReceiverVpa())
                .senderAccountId(event.getSenderAccountId())
                .receiverAccountId(event.getReceiverAccountId())
                .amount(event.getAmount())
                .remarks(event.getRemarks())
                .status(TransactionStatus.PROCESSING)
                .initiatedAt(event.getInitiatedAt())
                .build();

        transactionRepository.save(transaction);
        log.info("Saved TXN {} with status PROCESSING", event.getTransactionId());

        try {
            // Step 2 — Verify both accounts are still ACTIVE (audit check)
            AccountDto senderAccount = accountServiceClient
                    .getAccountById(event.getSenderAccountId());
            AccountDto receiverAccount = accountServiceClient
                    .getAccountById(event.getReceiverAccountId());

            if (!"ACTIVE".equals(senderAccount.getStatus())) {
                throw new IllegalStateException("Sender account is not active");
            }
            if (!"ACTIVE".equals(receiverAccount.getStatus())) {
                throw new IllegalStateException("Receiver account is not active");
            }

            // Step 3 — Mark COMPLETED
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transactionRepository.save(transaction);
            log.info("TXN {} marked COMPLETED", event.getTransactionId());

            // Step 4 — Publish transaction.completed (outside @Transactional scope
            //           so DB is committed before Kafka fires)
            publishCompletedEvent(transaction, "COMPLETED", null);

        } catch (Exception ex) {
            // Step 3 (failure path) — Mark FAILED and save reason
            log.error("TXN {} failed: {}", event.getTransactionId(), ex.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(ex.getMessage());
            transaction.setCompletedAt(LocalDateTime.now());
            transactionRepository.save(transaction);

            // Publish transaction.failed
            publishCompletedEvent(transaction, "FAILED", ex.getMessage());
        }
    }

    // Called AFTER @Transactional commits to avoid Kafka/DB inconsistency
    private void publishCompletedEvent(Transaction txn, String status, String failureReason) {
        TransactionCompletedEventDto event = TransactionCompletedEventDto.builder()
                .transactionId(txn.getTransactionId())
                .senderVpa(txn.getSenderVpa())
                .receiverVpa(txn.getReceiverVpa())
                .amount(txn.getAmount())
                .status(status)
                .failureReason(failureReason)
                .completedAt(txn.getCompletedAt())
                .build();

        eventPublisher.publishCompleted(event);
    }

    // ── REST query methods ──────────────────────────────────────

    @Transactional(readOnly = true)
    public TransactionResponseDto getByTransactionId(String transactionId) {
        Transaction txn = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found: " + transactionId));
        return mapToResponse(txn);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getByAccountId(Long accountId) {
        return transactionRepository
                .findBySenderAccountIdOrReceiverAccountId(accountId, accountId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TransactionResponseDto mapToResponse(Transaction txn) {
        return TransactionResponseDto.builder()
                .id(txn.getId())
                .transactionId(txn.getTransactionId())
                .senderVpa(txn.getSenderVpa())
                .receiverVpa(txn.getReceiverVpa())
                .amount(txn.getAmount())
                .status(txn.getStatus().name())
                .remarks(txn.getRemarks())
                .failureReason(txn.getFailureReason())
                .initiatedAt(txn.getInitiatedAt())
                .completedAt(txn.getCompletedAt())
                .build();
    }
}
