package com.deepanshu.backend.activitylog.service;

import com.deepanshu.backend.activitylog.dto.ActivityLogResponse;
import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.workspace.entity.Workspace;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface ActivityLogService {

    void log(
            User user,
            Workspace workspace,
            ActivityAction action,
            ActivityEntityType entityType,
            UUID entityId,
            String description
    );

    PageResponse<ActivityLogResponse> getWorkspaceActivityLogs(
            UUID workspaceId,
            ActivityAction action,
            ActivityEntityType entityType,
            UUID entityId,
            UUID userId,
            Pageable pageable
    );
}
