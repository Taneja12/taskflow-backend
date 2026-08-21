package com.deepanshu.backend.notification.dto;

import com.deepanshu.backend.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDetailResponse {

    private UUID id;

    private NotificationType type;

    private UUID actorId;
    private String actorName;
    private String actorEmail;

    private UUID commentId;

    private UUID taskId;

    private UUID boardId;

    private UUID projectId;

    private UUID workspaceId;

    private String message;

    private boolean read;

    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
