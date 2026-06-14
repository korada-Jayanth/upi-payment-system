package com.upi.fraud_detection_service.service;

import com.upi.fraud_detection_service.document.FraudCheck;
import com.upi.fraud_detection_service.dto.FraudAlertEventDto;
import com.upi.fraud_detection_service.dto.TransactionCompletedEventDto;
import com.upi.fraud_detection_service.repository.FraudCheckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final FraudCheckRepository fraudCheckRepository;
    private final com.upi.fraud_detection_service.kafka.FraudAlertPublisher fraudAlertPublisher;

    @Value("${fraud.rules.high-amount-threshold}")
    private BigDecimal highAmountThreshold;

    @Value("${fraud.rules.velocity-window-minutes}")
    private int velocityWindowMinutes;

    @Value("${fraud.rules.velocity-max-transactions}")
    private int velocityMaxTransactions;

    public void evaluate(TransactionCompletedEventDto event) {

        // Only evaluate completed (successful) transactions
        if (!"COMPLETED".equalsIgnoreCase(event.getStatus())) {
            log.info("Skipping fraud check for non-completed txn {}", event.getTransactionId());
            return;
        }

        List<String> triggeredRules = new ArrayList<>();

        // Rule 1: High amount transaction
        if (event.getAmount().compareTo(highAmountThreshold) > 0) {
            triggeredRules.add("HIGH_AMOUNT");
            log.warn("Rule triggered: HIGH_AMOUNT for txn {}", event.getTransactionId());
        }

        // Rule 2: Velocity check — too many transactions in a short window
        List<FraudCheck> recentChecks = fraudCheckRepository.findBySenderVpa(event.getSenderVpa());
        long recentCount = recentChecks.stream()
                .filter(c -> c.getCheckedAt() != null &&
                        c.getCheckedAt().isAfter(LocalDateTime.now().minusMinutes(velocityWindowMinutes)))
                .count();

        if (recentCount >= velocityMaxTransactions) {
            triggeredRules.add("HIGH_VELOCITY");
            log.warn("Rule triggered: HIGH_VELOCITY for sender {}", event.getSenderVpa());
        }

        // Determine risk level
        String riskLevel;
        boolean flagged;

        if (triggeredRules.size() >= 2) {
            riskLevel = "HIGH";
            flagged = true;
        } else if (triggeredRules.size() == 1) {
            riskLevel = "MEDIUM";
            flagged = true;
        } else {
            riskLevel = "LOW";
            flagged = false;
        }

        // Save the check result to MongoDB
        FraudCheck fraudCheck = FraudCheck.builder()
                .transactionId(event.getTransactionId())
                .senderVpa(event.getSenderVpa())
                .receiverVpa(event.getReceiverVpa())
                .amount(event.getAmount())
                .riskLevel(riskLevel)
                .flagged(flagged)
                .triggeredRules(triggeredRules)
                .checkedAt(LocalDateTime.now())
                .build();

        fraudCheckRepository.save(fraudCheck);
        log.info("Fraud check saved: txn={}, risk={}, flagged={}",
                event.getTransactionId(), riskLevel, flagged);

        // Publish alert if flagged
        if (flagged) {
            fraudAlertPublisher.publish(FraudAlertEventDto.builder()
                    .transactionId(event.getTransactionId())
                    .userVpa(event.getSenderVpa())
                    .reason(String.join(", ", triggeredRules))
                    .riskLevel(riskLevel)
                    .build());
        }
    }
}