package com.upi.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlertEventDto {
    private String transactionId;
    private String userVpa;
    private String reason;
    private String riskLevel;   // LOW, MEDIUM, HIGH
}