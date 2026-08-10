package com.deepanshu.backend.activitylog.entity;

import com.deepanshu.backend.common.entity.BaseEntity;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Data
@Entity
@Table(name = "activity_logs")
public class ActivityLog extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityEntityType entityType;

    @Column(nullable = false)
    private UUID entityId;

    @Column(length = 500)
    private String description;

}
