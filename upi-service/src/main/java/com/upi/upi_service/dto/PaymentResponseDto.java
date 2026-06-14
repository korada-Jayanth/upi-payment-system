package com.upi.upi_service.dto;

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
public class PaymentResponseDto {
    private String transactionId;
    private String senderVpa;
    private String receiverVpa;
    private BigDecimal amount;
    private String status;           // INITIATED
    private String remarks;
    private LocalDateTime timestamp;
}
