package com.deepanshu.backend.project.entity;

import com.deepanshu.backend.common.entity.BaseEntity;
import com.deepanshu.backend.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

}
