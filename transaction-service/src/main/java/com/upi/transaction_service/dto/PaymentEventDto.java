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
public class PaymentEventDto {
    private String transactionId;
    private String senderVpa;
    private String receiverVpa;
    private Long senderAccountId;
    private Long receiverAccountId;
    private BigDecimal amount;
    private String remarks;
    private LocalDateTime initiatedAt;
}
