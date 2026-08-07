package com.deepanshu.backend.project.service;

import com.deepanshu.backend.authorization.service.AuthorizationService;
import com.deepanshu.backend.board.repo.BoardRepo;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.permission.Permissions;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.project.dto.request.AddProjectRequest;
import com.deepanshu.backend.project.dto.response.ProjectResponse;
import com.deepanshu.backend.project.dto.response.ProjectStatisticsResponse;
import com.deepanshu.backend.project.entity.Project;
import com.deepanshu.backend.project.repo.ProjectRepo;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.projection.TaskStatusCount;
import com.deepanshu.backend.task.repo.TaskRepo;
import com.deepanshu.backend.workspace.entity.Workspace;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceMember;
import com.deepanshu.backend.workspaceMember.repo.WorkspaceMemberRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepo projectRepo;
    private final BoardRepo boardRepo;
    private final TaskRepo taskRepo;
    private final WorkspaceMemberRepo workspaceMemberRepo;
    private final AuthorizationService authorizationService;
    private final HelperService helperService;

    public ProjectServiceImpl(ProjectRepo projectRepo, BoardRepo boardRepo, TaskRepo taskRepo, WorkspaceMemberRepo workspaceMemberRepo, AuthorizationService authorizationService, HelperService helperService) {
        this.projectRepo = projectRepo;
        this.boardRepo = boardRepo;
        this.taskRepo = taskRepo;
        this.workspaceMemberRepo = workspaceMemberRepo;
        this.authorizationService = authorizationService;
        this.helperService = helperService;
    }

    public ProjectResponse mapToResponse(Project project){
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getWorkspace().getName()
        );
    }

    @Override
    public ProjectResponse createProject(UUID workspaceId, AddProjectRequest request)
    {
        Workspace workspace = authorizationService.requireWorkspacePermission(workspaceId, Permissions.WORKSPACE_WRITE);
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setWorkspace(workspace);
        return mapToResponse(projectRepo.save(project));
    }

    @Override
    public PageResponse<ProjectResponse> getProjects(Pageable pageable) {
        Page<ProjectResponse> page = projectRepo
                .findAccessibleProjects(
                        helperService.getCurrentUser().getId(),
                        pageable
                )
                .map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public PageResponse<ProjectResponse> getWorkspaceProjects(UUID workspaceId, Pageable pageable) {
        Workspace workspace = authorizationService.requireWorkspace(workspaceId);
        Page<ProjectResponse> page = projectRepo.findByWorkspaceId(workspace.getId(), pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public ProjectResponse updateProject(AddProjectRequest request, UUID projectId) {
        Project project = authorizationService.requireProjectPermission(projectId, Permissions.WORKSPACE_WRITE);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        projectRepo.save(project);
        return mapToResponse(project);
    }

    @Override
    public ProjectResponse getProjectById(UUID projectId) {
        return mapToResponse(authorizationService.requireProject(projectId));
    }

    @Override
    public void deleteProject(UUID projectId) {
        projectRepo.delete(authorizationService.requireProjectPermission(projectId, Permissions.WORKSPACE_WRITE));
    }

    @Override
    public ProjectStatisticsResponse getProjectStatistics(UUID projectId) {
        Project project = authorizationService.requireProject(projectId);
        Map<TaskStatus, Long> statusCounts = taskRepo.countTasksByStatusAndProjectId(project.getId())
                .stream().collect(Collectors.toMap(
                TaskStatusCount::getStatus,
                TaskStatusCount::getTotal
        ));

        return new ProjectStatisticsResponse(
                boardRepo.countByProjectId(project.getId()),
                statusCounts.getOrDefault(TaskStatus.TODO, 0L),
                statusCounts.getOrDefault(TaskStatus.IN_PROGRESS, 0L),
                statusCounts.getOrDefault(TaskStatus.COMPLETED, 0L)
        );
    }


}
