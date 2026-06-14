package com.upi.transaction_service.config;

import com.upi.transaction_service.dto.PaymentEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.transaction-completed}")
    private String completedTopic;

    @Bean
    public NewTopic transactionCompletedTopic() {
        return TopicBuilder.name(completedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEventDto>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentEventDto> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, PaymentEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}