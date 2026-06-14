package com.upi.fraud_detection_service.service;

import com.upi.fraud_detection_service.dto.TransactionCompletedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final FraudDetectionService fraudDetectionService;

    @KafkaListener(topics = "transaction.completed", groupId = "fraud-detection-group")
    public void handleTransactionCompleted(TransactionCompletedEventDto event) {
        log.info("Fraud service received transaction.completed: {}", event);
        fraudDetectionService.evaluate(event);
    }
}