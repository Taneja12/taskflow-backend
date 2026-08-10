package com.deepanshu.backend.workspace.service;

import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.activitylog.service.ActivityLogService;
import com.deepanshu.backend.authorization.service.AuthorizationService;
import com.deepanshu.backend.board.repo.BoardRepo;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.project.repo.ProjectRepo;
import com.deepanshu.backend.task.entity.TaskPriority;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.projection.TaskStatusCount;
import com.deepanshu.backend.task.repo.TaskRepo;
import com.deepanshu.backend.workspace.dto.response.WorkspaceStatisticsResponse;
import com.deepanshu.backend.workspace.dto.request.AddWorkSpaceRequest;
import com.deepanshu.backend.workspace.dto.response.WorkspaceResponse;
import com.deepanshu.backend.workspace.entity.Workspace;
import com.deepanshu.backend.workspace.repo.WorkspaceRepo;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceMember;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import com.deepanshu.backend.workspaceMember.repo.WorkspaceMemberRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepo repo;
    private final ProjectRepo projectRepo;
    private final BoardRepo boardRepo;
    private final TaskRepo taskRepo;
    private final WorkspaceMemberRepo workspaceMemberRepo;
    private final AuthorizationService authorizationService;
    private final ActivityLogService activityLogService;
    private final HelperService helperService;

    public WorkspaceServiceImpl(WorkspaceRepo repo, ProjectRepo projectRepo,
                                BoardRepo boardRepo, TaskRepo taskRepo, WorkspaceMemberRepo workspaceMemberRepo,
                                AuthorizationService authorizationService, ActivityLogService activityLogService,
                                HelperService helperService) {
        this.repo = repo;
        this.projectRepo = projectRepo;
        this.boardRepo = boardRepo;
        this.taskRepo = taskRepo;
        this.workspaceMemberRepo = workspaceMemberRepo;
        this.authorizationService = authorizationService;
        this.activityLogService = activityLogService;
        this.helperService = helperService;
    }

    public WorkspaceResponse mapToResponse(Workspace workspace)
    {
        return new WorkspaceResponse(
             workspace.getId(),
             workspace.getName(),
             workspace.getDescription(),
                workspace.getOwner().getFullName()
        );
    }


    @Override
    public PageResponse<WorkspaceResponse> getWorkspaces(Pageable pageable) {
        Page<WorkspaceResponse> page =
                workspaceMemberRepo.findByUserId(helperService.getCurrentUser().getId(), pageable)
                        .map(member -> mapToResponse(member.getWorkspace()));
        return helperService.setPageResponse(page);
    }

    @Override
    public WorkspaceResponse getWorkSpaceById(UUID workspaceId) {
        return mapToResponse(authorizationService.requireWorkspace(workspaceId));
    }

    @Transactional
    @Override
    public WorkspaceResponse updateWorkSpace(UUID workspaceId, AddWorkSpaceRequest request) {
        Workspace workspace = authorizationService.requireWorkspacePermission(workspaceId, WorkspaceRole.OWNER);
        workspace.setDescription(request.getDescription());
        workspace.setName(request.getName());
        workspace = repo.save(workspace);

        activityLogService.log(
                helperService.getCurrentUser(),
                workspace,
                ActivityAction.WORKSPACE_UPDATED,
                ActivityEntityType.WORKSPACE,
                workspace.getId(),
                helperService.getCurrentUser().getEmail()
                        + " updated workspace "
                        + workspace.getName()
        );
        return mapToResponse(workspace);
    }

    @Transactional
    @Override
    public WorkspaceResponse createWorkspace(AddWorkSpaceRequest request) {
        Workspace workspace = new Workspace();
        workspace.setOwner(helperService.getCurrentUser());
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace = repo.save(workspace);
        WorkspaceMember workspaceMember = new WorkspaceMember();
        workspaceMember.setWorkspace(workspace);
        workspaceMember.setUser(helperService.getCurrentUser());
        workspaceMember.setRole(WorkspaceRole.OWNER);
        workspaceMemberRepo.save(workspaceMember);

        activityLogService.log(
                helperService.getCurrentUser(),
                workspace,
                ActivityAction.WORKSPACE_CREATED,
                ActivityEntityType.WORKSPACE,
                workspace.getId(),
                helperService.getCurrentUser().getEmail()
                        + " created workspace "
                        + workspace.getName()
        );

        activityLogService.log(
                helperService.getCurrentUser(),
                workspace,
                ActivityAction.MEMBER_ADDED,
                ActivityEntityType.MEMBER,
                workspaceMember.getId(),
                "Added " + workspaceMember.getUser().getEmail() + " to the workspace"
        );
        return mapToResponse(workspace);
    }

    @Transactional
    @Override
    public void deleteWorkspace(UUID workspaceId) {
        Workspace workspace = authorizationService.requireWorkspacePermission(workspaceId, WorkspaceRole.OWNER);
        activityLogService.log(
                helperService.getCurrentUser(),
                workspace,
                ActivityAction.WORKSPACE_DELETED,
                ActivityEntityType.WORKSPACE,
                workspace.getId(),
                helperService.getCurrentUser().getEmail()
                        + " deleted workspace "
                        + workspace.getName()
        );

        repo.delete(workspace);
    }

    @Override
    public WorkspaceStatisticsResponse getWorkspaceStatistics(UUID workspaceId) {
        Workspace workspace = authorizationService.requireWorkspace(workspaceId);
        Map<TaskStatus, Long> statusCounts =  taskRepo.countTasksByStatus(workspace.getId())
                .stream().collect(Collectors.toMap(
                        TaskStatusCount::getStatus,
                        TaskStatusCount::getTotal
                ));

        return new WorkspaceStatisticsResponse(
                workspaceMemberRepo.countWorkspaceMemberByWorkspaceId(workspaceId),
                projectRepo.countByWorkspaceId(workspaceId),
                boardRepo.countByWorkspaceId(workspaceId),
                taskRepo.countByWorkspaceId(workspaceId),
                statusCounts.getOrDefault(TaskStatus.TODO, 0L),
                statusCounts.getOrDefault(TaskStatus.IN_PROGRESS, 0L),
                statusCounts.getOrDefault(TaskStatus.COMPLETED, 0L),
                taskRepo.countByPriority(workspaceId, TaskPriority.LOW),
                taskRepo.countByPriority(workspaceId, TaskPriority.MEDIUM),
                taskRepo.countByPriority(workspaceId, TaskPriority.HIGH)
        );
    }

}
