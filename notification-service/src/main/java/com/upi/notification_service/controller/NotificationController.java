package com.upi.notification_service.controller;

import com.upi.notification_service.dto.NotificationResponseDto;
import com.upi.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/vpa/{vpa}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsForVpa(
            @PathVariable String vpa) {
        return ResponseEntity.ok(notificationService.getNotificationsForRecipient(vpa));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}