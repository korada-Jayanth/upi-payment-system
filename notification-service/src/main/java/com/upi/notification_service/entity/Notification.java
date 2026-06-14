package com.upi.notification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipientVpa;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;       // TRANSACTION, FRAUD_ALERT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel; // SMS, EMAIL, PUSH

    @Column(nullable = false)
    private boolean sent;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum NotificationType {
        TRANSACTION, FRAUD_ALERT
    }

    public enum NotificationChannel {
        SMS, EMAIL, PUSH
    }
}