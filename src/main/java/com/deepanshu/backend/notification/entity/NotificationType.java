package com.deepanshu.backend.notification.entity;

public enum NotificationType {
    TASK_ASSIGNED,
    TASK_STATUS_CHANGED,
    TASK_COMMENTED,
    COMMENT_MENTION,
    TASK_MOVED,
    TASK_UPDATED,

    BOARD_CREATED,
    BOARD_UPDATED,

    PROJECT_CREATED,
    PROJECT_UPDATED,

    WORKSPACE_INVITATION,
    MEMBER_ADDED
}