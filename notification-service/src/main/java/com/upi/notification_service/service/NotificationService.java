package com.upi.notification_service.service;

import com.upi.notification_service.dto.NotificationResponseDto;
import com.upi.notification_service.entity.Notification;
import com.upi.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createAndSend(String recipientVpa, String title, String message,
                              Notification.NotificationType type,
                              Notification.NotificationChannel channel) {

        Notification notification = Notification.builder()
                .recipientVpa(recipientVpa)
                .title(title)
                .message(message)
                .type(type)
                .channel(channel)
                .sent(false)
                .build();

        notification = notificationRepository.save(notification);

        sendMock(notification);

        notification.setSent(true);
        notificationRepository.save(notification);
    }

    private void sendMock(Notification notification) {
        log.info("==========================================");
        log.info("📩 SENDING {} to {}", notification.getChannel(), notification.getRecipientVpa());
        log.info("Title: {}", notification.getTitle());
        log.info("Message: {}", notification.getMessage());
        log.info("==========================================");
    }

    public List<NotificationResponseDto> getNotificationsForRecipient(String recipientVpa) {
        return notificationRepository.findByRecipientVpaOrderByCreatedAtDesc(recipientVpa)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NotificationResponseDto mapToResponse(Notification n) {
        return NotificationResponseDto.builder()
                .id(n.getId())
                .recipientVpa(n.getRecipientVpa())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .channel(n.getChannel())
                .sent(n.isSent())
                .createdAt(n.getCreatedAt())
                .build();
    }
}