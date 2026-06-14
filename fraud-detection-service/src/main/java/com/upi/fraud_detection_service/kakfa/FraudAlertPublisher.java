package com.upi.fraud_detection_service.kafka;

import com.upi.fraud_detection_service.dto.FraudAlertEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudAlertPublisher {

    private static final String TOPIC = "fraud.alert";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(FraudAlertEventDto event) {
        kafkaTemplate.send(TOPIC, event.getTransactionId().toString(), event);
        log.info("Published fraud.alert event for transaction {}: {}",
                event.getTransactionId(), event.getRiskLevel());
    }
}