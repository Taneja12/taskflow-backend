package com.deepanshu.backend.workspaceMember.dto;

import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRoleRequest {

    @NotNull(message = "Role is required")
    private WorkspaceRole role;
}
