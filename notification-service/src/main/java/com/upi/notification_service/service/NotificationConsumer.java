package com.upi.notification_service.service;

import com.upi.notification_service.dto.FraudAlertEventDto;
import com.upi.notification_service.dto.TransactionCompletedEventDto;
import com.upi.notification_service.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "transaction.completed", groupId = "notification-group")
    public void handleTransactionCompleted(TransactionCompletedEventDto event) {
        log.info("Received transaction.completed event: {}", event);

        if ("COMPLETED".equalsIgnoreCase(event.getStatus())) {

            // Notify sender
            notificationService.createAndSend(
                    event.getSenderVpa(),
                    "Payment Sent",
                    String.format("₹%s sent to %s. Txn ID: %s",
                            event.getAmount(), event.getReceiverVpa(), event.getTransactionId()),
                    Notification.NotificationType.TRANSACTION,
                    Notification.NotificationChannel.SMS
            );

            // Notify receiver
            notificationService.createAndSend(
                    event.getReceiverVpa(),
                    "Payment Received",
                    String.format("₹%s received from %s. Txn ID: %s",
                            event.getAmount(), event.getSenderVpa(), event.getTransactionId()),
                    Notification.NotificationType.TRANSACTION,
                    Notification.NotificationChannel.SMS
            );

        } else {
            // Notify sender of failure
            notificationService.createAndSend(
                    event.getSenderVpa(),
                    "Payment Failed",
                    String.format("Your payment of ₹%s to %s failed (%s). Txn ID: %s",
                            event.getAmount(), event.getReceiverVpa(),
                            event.getFailureReason(), event.getTransactionId()),
                    Notification.NotificationType.TRANSACTION,
                    Notification.NotificationChannel.SMS
            );
        }
    }

    @KafkaListener(topics = "fraud.alert", groupId = "notification-group")
    public void handleFraudAlert(FraudAlertEventDto event) {
        log.info("Received fraud.alert event: {}", event);

        notificationService.createAndSend(
                event.getUserVpa(),
                "⚠️ Suspicious Activity Detected",
                String.format("We flagged a transaction (ID: %s) as %s risk: %s",
                        event.getTransactionId(), event.getRiskLevel(), event.getReason()),
                Notification.NotificationType.FRAUD_ALERT,
                Notification.NotificationChannel.PUSH
        );
    }
}