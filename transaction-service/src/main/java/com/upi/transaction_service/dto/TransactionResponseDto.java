package com.upi.transaction_service.dto;

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
public class TransactionResponseDto {
    private Long id;
    private String transactionId;
    private String senderVpa;
    private String receiverVpa;
    private BigDecimal amount;
    private String status;
    private String remarks;
    private String failureReason;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
}
