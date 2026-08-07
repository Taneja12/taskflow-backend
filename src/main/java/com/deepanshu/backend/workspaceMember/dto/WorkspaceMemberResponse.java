package com.deepanshu.backend.workspaceMember.dto;

import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.workspace.entity.Workspace;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberResponse {

    private UUID id;

    private UUID userId;

    private String fullName;

    private String email;

    private String profileImageUrl;

    private WorkspaceRole role;

    private LocalDateTime joinedAt;

    private UUID invitedBy;

}