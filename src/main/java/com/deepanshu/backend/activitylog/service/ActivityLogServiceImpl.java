package com.deepanshu.backend.activitylog.service;

import com.deepanshu.backend.activitylog.dto.ActivityLogResponse;
import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.activitylog.entity.ActivityLog;
import com.deepanshu.backend.activitylog.repo.ActivityLogRepo;
import com.deepanshu.backend.authorization.service.AuthorizationService;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.workspace.entity.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActivityLogServiceImpl implements ActivityLogService{

    private final ActivityLogRepo activityLogRepo;
    private final AuthorizationService authorizationService;
    private final HelperService helperService;


    public ActivityLogServiceImpl(ActivityLogRepo activityLogRepo, AuthorizationService authorizationService, HelperService helperService) {
        this.activityLogRepo = activityLogRepo;
        this.authorizationService = authorizationService;
        this.helperService = helperService;
    }

    private ActivityLogResponse mapToResponse(ActivityLog activityLog)
    {
        UUID workspaceId = activityLog.getWorkspace() != null
                ? activityLog.getWorkspace().getId()
                : null;

        String workspaceName = activityLog.getWorkspace() != null
                ? activityLog.getWorkspace().getName()
                : null;

        return new ActivityLogResponse(
                activityLog.getId(),
                activityLog.getUser().getId(),
                activityLog.getUser().getFullName(),
                workspaceId,
                workspaceName,
                activityLog.getAction().toString(),
                activityLog.getEntityId(),
                activityLog.getEntityType().toString(),
                activityLog.getDescription(),
                activityLog.getCreatedAt(),
                activityLog.getUpdatedAt()
        );
    }

    @Override
    public void log(User user, Workspace workspace, ActivityAction action, ActivityEntityType entityType, UUID entityId, String description) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUser(user);
        activityLog.setWorkspace(workspace);
        activityLog.setAction(action);
        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setDescription(description);

        activityLogRepo.save(activityLog);
    }

    @Override
    public PageResponse<ActivityLogResponse> getWorkspaceActivityLogs(UUID workspaceId,
                                                                      ActivityAction action,
                                                                      ActivityEntityType entityType,
                                                                      UUID entityId,
                                                                      UUID userId,
                                                                      Pageable pageable) {
        authorizationService.requireWorkspaceMember(workspaceId);
        Page<ActivityLogResponse> page = activityLogRepo.findActivityLogs(workspaceId, action, entityType, entityId, userId, pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }
}
