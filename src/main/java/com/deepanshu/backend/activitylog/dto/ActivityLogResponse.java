package com.deepanshu.backend.activitylog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogResponse {
    private UUID id;
    private UUID userId;
    private String userFullName;
    private UUID workspaceId;
    private String workspaceName;
    private String action;
    private UUID entityId;
    private String entityType;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
