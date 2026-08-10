package com.deepanshu.backend.activitylog.repo;

import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.activitylog.entity.ActivityLog;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ActivityLogRepo extends JpaRepository<ActivityLog, UUID> {
//    Page<ActivityLog> findByWorkspaceId(
//            UUID workspaceId,
//            Pageable pageable
//    );

    @Query("""
        SELECT a
        FROM ActivityLog a
        WHERE a.workspace.id = :workspaceId
          AND (:action IS NULL OR a.action = :action)
          AND (:entityType IS NULL OR a.entityType = :entityType)
          AND (:entityId IS NULL OR a.entityId = :entityId)
          AND (:userId IS NULL OR a.user.id = :userId)
        ORDER BY a.createdAt DESC
    """)
    Page<ActivityLog> findActivityLogs(UUID workspaceId, ActivityAction action, ActivityEntityType entityType, UUID entityId, UUID userId, Pageable pageable);
}
