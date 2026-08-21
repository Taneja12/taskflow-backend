package com.deepanshu.backend.notification.dto;

import java.util.UUID;

public record NotificationContext(
        UUID commentId,
        UUID taskId,
        UUID boardId,
        UUID projectId,
        UUID workspaceId
) {}
