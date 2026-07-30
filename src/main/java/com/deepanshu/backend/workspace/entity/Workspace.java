package com.deepanshu.backend.workspace.entity;

import com.deepanshu.backend.common.entity.BaseEntity;
import com.deepanshu.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "workspaces")
public class Workspace extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}
