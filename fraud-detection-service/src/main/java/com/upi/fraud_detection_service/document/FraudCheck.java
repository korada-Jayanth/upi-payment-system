package com.upi.fraud_detection_service.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "fraud_checks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudCheck {

    @Id
    private String id;

    private String transactionId;
    private String senderVpa;
    private String receiverVpa;
    private BigDecimal amount;

    private String riskLevel;       // LOW, MEDIUM, HIGH
    private boolean flagged;
    private List<String> triggeredRules;

    private LocalDateTime checkedAt;
}