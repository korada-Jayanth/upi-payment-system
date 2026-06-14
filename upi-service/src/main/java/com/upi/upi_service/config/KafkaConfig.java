package com.upi.upi_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.payment-initiated}")
    private String paymentInitiatedTopic;

    @Bean
    public NewTopic paymentInitiatedTopic() {
        return TopicBuilder.name(paymentInitiatedTopic)
                .partitions(3)       // 3 partitions for parallel consumption
                .replicas(1)         // 1 replica (local dev)
                .build();
    }
}
