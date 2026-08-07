package com.deepanshu.backend.workspaceMember.service;

import com.deepanshu.backend.authorization.service.AuthorizationService;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.exception.InvalidOperationException;
import com.deepanshu.backend.common.exception.MemberAlreadyExistsException;
import com.deepanshu.backend.common.exception.ResourceNotFoundException;
import com.deepanshu.backend.common.permission.Permissions;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.task.repo.TaskRepo;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.user.repo.UserRepo;
import com.deepanshu.backend.workspaceMember.dto.AddWorkspaceMemberRequest;
import com.deepanshu.backend.workspaceMember.dto.UpdateMemberRoleRequest;
import com.deepanshu.backend.workspaceMember.dto.WorkspaceMemberResponse;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceMember;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import com.deepanshu.backend.workspaceMember.repo.WorkspaceMemberRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService{

    private final WorkspaceMemberRepo repo;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;
    private final AuthorizationService authorizationService;
    private final HelperService helperService;

    public WorkspaceMemberServiceImpl(WorkspaceMemberRepo repo, UserRepo userRepo, TaskRepo taskRepo, AuthorizationService authorizationService, HelperService helperService) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.taskRepo = taskRepo;
        this.authorizationService = authorizationService;
        this.helperService = helperService;
    }

    private WorkspaceMemberResponse mapToResponse(WorkspaceMember workspaceMember)
    {
        return new WorkspaceMemberResponse(
                workspaceMember.getId(),
                workspaceMember.getUser().getId(),
                workspaceMember.getUser().getFullName(),
                workspaceMember.getUser().getEmail(),
                workspaceMember.getUser().getProfileImageUrl(),
                workspaceMember.getRole(),
                workspaceMember.getJoinedAt(),
                workspaceMember.getInvitedBy() == null ? null: workspaceMember.getInvitedBy().getId()
        );
    }

    private void validateMemberRemoval(WorkspaceMember currentMember, WorkspaceMember target) {
        if (target.getRole() == WorkspaceRole.OWNER) {
            throw new InvalidOperationException(
                    "Workspace owner cannot be removed."
            );
        }
        if(currentMember.getRole() == WorkspaceRole.ADMIN &&
                target .getRole() != WorkspaceRole.MEMBER &&
                target .getRole() != WorkspaceRole.VIEWER){
            throw new AccessDeniedException("You cannot remove this member.");
        }
        if(currentMember.getId().equals(target.getId())){
            throw new InvalidOperationException("You cannot remove yourself. Leave the workspace instead.");
        }
    }

    private void validateRoleModification( WorkspaceRole newRole ){
        if (newRole== WorkspaceRole.OWNER) {
            throw new InvalidOperationException(
                    "Owner role cannot be assigned."
            );
        }
    }

    @Override
    public PageResponse<WorkspaceMemberResponse> getWorkspaceMembers(UUID workspaceId, Pageable pageable) {
        authorizationService.requireWorkspaceMember(workspaceId);
        Page<WorkspaceMemberResponse> page = repo.findAllByWorkspace_Id(workspaceId, pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Transactional
    @Override
    public WorkspaceMemberResponse addWorkspaceMembers(UUID workspaceId, AddWorkspaceMemberRequest request) {
        if (request.getRole() == WorkspaceRole.OWNER) {
            throw new InvalidOperationException(
                    "Owner role cannot be assigned."
            );
        }
        WorkspaceMember currentMember = authorizationService.requireWorkspaceMemberPermission(workspaceId, Permissions.WORKSPACE_WRITE);
        if(repo.existsByWorkspaceIdAndUserEmail(workspaceId, request.getEmail())) throw new MemberAlreadyExistsException("Workspace member with the same email already exists");
        User user = userRepo.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        WorkspaceMember workspaceMember = new WorkspaceMember();
        workspaceMember.setUser(user);
        workspaceMember.setWorkspace(currentMember.getWorkspace());
        workspaceMember.setRole(request.getRole());
        workspaceMember.setInvitedBy(currentMember.getUser());
        return mapToResponse(repo.save(workspaceMember));
    }

    @Transactional
    @Override
    public void removeWorkspaceMember(UUID workspaceId, UUID memberId) {
        WorkspaceMember currentMember = authorizationService.requireWorkspaceMemberPermission(workspaceId, Permissions.WORKSPACE_WRITE);
        WorkspaceMember target  = repo.findByIdAndWorkspaceId(memberId, workspaceId).orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        validateMemberRemoval(currentMember, target);
        repo.delete(target);
    }

    @Override
    public WorkspaceMemberResponse updateMemberRole(UUID workspaceId, UUID memberId, UpdateMemberRoleRequest request) {
        WorkspaceMember workspaceMember = authorizationService.requireWorkspaceMemberPermission(workspaceId, Permissions.OWNER_ONLY);
        WorkspaceMember target = repo.findByIdAndWorkspaceId(memberId, workspaceMember.getWorkspace().getId()).orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        if(request.getRole()==target.getRole())
        {
            throw new InvalidOperationException("Member already has this role.");
        }
        validateRoleModification(request.getRole());
        target.setRole(request.getRole());
        return mapToResponse(repo.save(target));
    }
}
