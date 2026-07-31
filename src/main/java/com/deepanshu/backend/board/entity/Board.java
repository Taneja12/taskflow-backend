package com.deepanshu.backend.board.entity;

import com.deepanshu.backend.common.entity.BaseEntity;
import com.deepanshu.backend.project.entity.Project;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "boards")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

}
