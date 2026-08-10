package com.deepanshu.backend.activitylog.controller;

import com.deepanshu.backend.activitylog.dto.ActivityLogResponse;
import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.activitylog.service.ActivityLogService;
import com.deepanshu.backend.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/logs")
@Tag(name = "ActivityLog")
@SecurityRequirement(name = "Bearer Authentication")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<PageResponse<ActivityLogResponse>> getActivityLogs(@PathVariable UUID workspaceId,
                                                                             @RequestParam(required = false) ActivityAction action,
                                                                             @RequestParam(required = false) ActivityEntityType entityType,
                                                                             @RequestParam(required = false) UUID entityId,
                                                                             @RequestParam(required = false) UUID userId,
                                                                             @ParameterObject Pageable pageable)
    {
        return ResponseEntity.ok().body(activityLogService.getWorkspaceActivityLogs(workspaceId,
                action, entityType, entityId, userId, pageable));
    }
}
