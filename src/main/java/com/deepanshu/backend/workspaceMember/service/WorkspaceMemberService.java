package com.deepanshu.backend.workspaceMember.service;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.workspaceMember.dto.AddWorkspaceMemberRequest;
import com.deepanshu.backend.workspaceMember.dto.UpdateMemberRoleRequest;
import com.deepanshu.backend.workspaceMember.dto.WorkspaceMemberResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkspaceMemberService {
    PageResponse<WorkspaceMemberResponse> getWorkspaceMembers(UUID workspaceId, Pageable pageable);

    WorkspaceMemberResponse addWorkspaceMembers(UUID workspaceId, AddWorkspaceMemberRequest request);

    void removeWorkspaceMember(UUID workspaceId, UUID memberId);

    WorkspaceMemberResponse updateMemberRole(UUID workspaceId, UUID memberId, @Valid UpdateMemberRoleRequest request);
}
