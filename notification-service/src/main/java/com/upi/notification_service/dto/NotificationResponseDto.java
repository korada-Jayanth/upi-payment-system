package com.upi.notification_service.dto;

import com.upi.notification_service.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long id;
    private String recipientVpa;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Notification.NotificationChannel channel;
    private boolean sent;
    private LocalDateTime createdAt;
}