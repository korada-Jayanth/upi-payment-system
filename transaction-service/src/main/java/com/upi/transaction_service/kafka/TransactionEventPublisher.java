package com.upi.transaction_service.kafka;

import com.upi.transaction_service.dto.TransactionCompletedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {

    private final KafkaTemplate<String, TransactionCompletedEventDto> kafkaTemplate;

    @Value("${app.kafka.topic.transaction-completed}")
    private String completedTopic;

    public void publishCompleted(TransactionCompletedEventDto event) {
        // Both COMPLETED and FAILED go to the same topic —
        // the 'status' field inside the event distinguishes them.
        // Notification & Fraud services both consume this single topic.
        kafkaTemplate.send(completedTopic, event.getTransactionId(), event);
        log.info("Published {} event for TXN: {}", event.getStatus(), event.getTransactionId());
    }
}