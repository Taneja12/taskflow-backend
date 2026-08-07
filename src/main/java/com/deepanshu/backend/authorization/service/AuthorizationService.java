package com.deepanshu.backend.authorization.service;

import com.deepanshu.backend.common.exception.ResourceNotFoundException;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceMember;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import com.deepanshu.backend.workspaceMember.repo.WorkspaceMemberRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class AuthorizationService {

    private final WorkspaceMemberRepo workspaceMemberRepo;
    private final HelperService helperService;

    public AuthorizationService(WorkspaceMemberRepo workspaceMemberRepo, HelperService helperService) {
        this.workspaceMemberRepo = workspaceMemberRepo;
        this.helperService = helperService;
    }

    // Access to the workspace
    public WorkspaceMember requireWorkspaceMember(UUID workspaceId)
    {
        return workspaceMemberRepo.findByWorkspaceIdAndUserId(workspaceId, helperService.getCurrentUser().getId())
                .orElseThrow();
    }

    // Permission for specific operation based on role
    public WorkspaceMember requireWorkspacePermission(
            UUID workspaceId,
            WorkspaceRole... roles
    )
    {
        WorkspaceMember member = requireWorkspaceMember(workspaceId);
        Set<WorkspaceRole> allowedRoles = Set.of(roles);
        if (!allowedRoles.contains(member.getRole())) {
            throw new AccessDeniedException("Permission denied");
        }

        return member;
    }

}
