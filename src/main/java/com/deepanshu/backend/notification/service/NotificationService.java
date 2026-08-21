package com.deepanshu.backend.notification.service;

import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.comment.entity.Comment;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.notification.dto.NotificationDetailResponse;
import com.deepanshu.backend.notification.dto.NotificationResponse;
import com.deepanshu.backend.notification.entity.NotificationType;
import com.deepanshu.backend.project.entity.Project;
import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.workspace.entity.Workspace;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    PageResponse<NotificationResponse> getNotifications(Pageable pageable);
    void notification(User recipient, User actor, NotificationType notificationType, String message, Comment comment, Task task, Board board, Project project, Workspace workspace);

    NotificationDetailResponse getNotificationById(UUID notificationId);

    NotificationDetailResponse markAsReadNotification(UUID notificationId);
}
