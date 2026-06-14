package com.upi.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCompletedEventDto {
    private String transactionId;
    private String senderVpa;
    private String receiverVpa;
    private BigDecimal amount;
    private String status;          // COMPLETED or FAILED
    private String failureReason;    // null if COMPLETED
    private LocalDateTime completedAt;
}