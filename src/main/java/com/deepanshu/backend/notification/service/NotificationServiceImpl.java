package com.deepanshu.backend.notification.service;

import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.comment.entity.Comment;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.exception.ResourceNotFoundException;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.notification.dto.NotificationContext;
import com.deepanshu.backend.notification.dto.NotificationDetailResponse;
import com.deepanshu.backend.notification.dto.NotificationResponse;
import com.deepanshu.backend.notification.entity.Notification;
import com.deepanshu.backend.notification.entity.NotificationType;
import com.deepanshu.backend.notification.repo.NotificationRepo;
import com.deepanshu.backend.project.entity.Project;
import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.workspace.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepo notificationRepo;
    private final HelperService helperService;

    private NotificationContext resolveContext(Notification notification) {

        UUID commentId = null;
        UUID taskId = null;
        UUID boardId = null;
        UUID projectId = null;
        UUID workspaceId = null;

        if (notification.getComment() != null) {

            Comment comment = notification.getComment();
            commentId = comment.getId();

            Task task = comment.getTask();

            if (task != null) {
                taskId = task.getId();

                Board board = task.getBoard();

                if (board != null) {
                    boardId = board.getId();

                    Project project = board.getProject();

                    if (project != null) {
                        projectId = project.getId();

                        Workspace workspace = project.getWorkspace();

                        if (workspace != null) {
                            workspaceId = workspace.getId();
                        }
                    }
                }
            }

        } else if (notification.getTask() != null) {

            Task task = notification.getTask();

            taskId = task.getId();

            Board board = task.getBoard();

            if (board != null) {
                boardId = board.getId();

                Project project = board.getProject();

                if (project != null) {
                    projectId = project.getId();

                    Workspace workspace = project.getWorkspace();

                    if (workspace != null) {
                        workspaceId = workspace.getId();
                    }
                }
            }

        } else if (notification.getBoard() != null) {

            Board board = notification.getBoard();

            boardId = board.getId();

            Project project = board.getProject();

            if (project != null) {
                projectId = project.getId();

                Workspace workspace = project.getWorkspace();

                if (workspace != null) {
                    workspaceId = workspace.getId();
                }
            }

        } else if (notification.getProject() != null) {

            Project project = notification.getProject();

            projectId = project.getId();

            Workspace workspace = project.getWorkspace();

            if (workspace != null) {
                workspaceId = workspace.getId();
            }

        } else if (notification.getWorkspace() != null) {

            workspaceId = notification.getWorkspace().getId();
        }

        return new NotificationContext(
                commentId,
                taskId,
                boardId,
                projectId,
                workspaceId
        );
    }

    private NotificationResponse mapToResponse(Notification notification)
    {
        return new NotificationResponse(
          notification.getId(),
          notification.getType(),
                notification.getActor().getId(),
                notification.getActor().getFullName(),

                notification.getMessage(),
                notification.getReadAt() != null,
                notification.getCreatedAt(),
                notification.getReadAt()
                );
    }

    private NotificationDetailResponse mapToDetailResponse(Notification notification) {

        NotificationContext context = resolveContext(notification);

        return new NotificationDetailResponse(
                notification.getId(),
                notification.getType(),

                notification.getActor().getId(),
                notification.getActor().getFullName(),
                notification.getActor().getEmail(),

                context.commentId(),
                context.taskId(),
                context.boardId(),
                context.projectId(),
                context.workspaceId(),

                notification.getMessage(),
                notification.getReadAt() != null,
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

    @Override
    public PageResponse<NotificationResponse> getNotifications(Pageable pageable) {
        Page<NotificationResponse> page = notificationRepo.getByRecipientId(helperService.getCurrentUser().getId(), pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public void notification(User recipient, User actor, NotificationType notificationType, String message, Comment comment, Task task, Board board, Project project, Workspace workspace) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setActor(actor);
        notification.setType(notificationType);
        notification.setMessage(message);
        notification.setComment(comment);
        notification.setTask(task);
        notification.setBoard(board);
        notification.setProject(project);
        notification.setWorkspace(workspace);
        notificationRepo.save(notification);
    }

    @Transactional(readOnly = true)
    @Override
    public NotificationDetailResponse getNotificationById(UUID notificationId) {
        Notification notification = notificationRepo.getByIdAndRecipientId(notificationId, helperService.getCurrentUser().getId()).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        return mapToDetailResponse(notification);
    }

    @Transactional
    @Override
    public NotificationDetailResponse markAsReadNotification(UUID notificationId) {
        Notification notification = notificationRepo.getByIdAndRecipientId(notificationId, helperService.getCurrentUser().getId()).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setReadAt(LocalDateTime.now());
        return mapToDetailResponse(notification);
    }
}
