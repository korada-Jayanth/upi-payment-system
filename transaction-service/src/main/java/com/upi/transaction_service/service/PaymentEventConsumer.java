package com.upi.transaction_service.service;

import com.upi.transaction_service.dto.PaymentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final TransactionService transactionService;

    @KafkaListener(
            topics = "${app.kafka.topic.payment-initiated}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(PaymentEventDto event, Acknowledgment acknowledgment) {
        log.info("Received payment.initiated event: {}", event.getTransactionId());
        try {
            transactionService.processPaymentEvent(event);
            // Manual ack — only commit offset after successful processing
            acknowledgment.acknowledge();
            log.info("Offset committed for TXN: {}", event.getTransactionId());
        } catch (Exception ex) {
            // Do NOT acknowledge — Kafka will redeliver for retry
            log.error("Failed to process TXN {}: {}", event.getTransactionId(), ex.getMessage());
        }
    }
}
