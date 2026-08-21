package com.deepanshu.backend.notification.dto;

import com.deepanshu.backend.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;

    private NotificationType type;

    private UUID actorId;
    private String actorName;

    private String message;

    private boolean read;

    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}