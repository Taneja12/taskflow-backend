package com.deepanshu.backend.project.service;

import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.activitylog.service.ActivityLogService;
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
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepo projectRepo;
    private final BoardRepo boardRepo;
    private final TaskRepo taskRepo;
    private final AuthorizationService authorizationService;
    private final ActivityLogService activityLogService;
    private final HelperService helperService;

    public ProjectServiceImpl(ProjectRepo projectRepo, BoardRepo boardRepo, TaskRepo taskRepo, AuthorizationService authorizationService,
                              ActivityLogService activityLogService, HelperService helperService) {
        this.projectRepo = projectRepo;
        this.boardRepo = boardRepo;
        this.taskRepo = taskRepo;
        this.authorizationService = authorizationService;
        this.activityLogService = activityLogService;
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

    @Transactional
    @Override
    public ProjectResponse createProject(UUID workspaceId, AddProjectRequest request)
    {
        Workspace workspace = authorizationService.requireWorkspacePermission(workspaceId, Permissions.WORKSPACE_WRITE);
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setWorkspace(workspace);
        project = projectRepo.save(project);

        activityLogService.log(
                helperService.getCurrentUser(),
                workspace,
                ActivityAction.PROJECT_CREATED,
                ActivityEntityType.PROJECT,
                project.getId(),
                helperService.getCurrentUser().getEmail() + " created project "+ project.getName()
        );

        return mapToResponse(project);
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

    @Transactional
    @Override
    public ProjectResponse updateProject(AddProjectRequest request, UUID projectId) {
        Project project = authorizationService.requireProjectPermission(projectId, Permissions.WORKSPACE_WRITE);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project  = projectRepo.save(project);

        activityLogService.log(
                helperService.getCurrentUser(),
                project.getWorkspace(),
                ActivityAction.PROJECT_UPDATED,
                ActivityEntityType.PROJECT,
                project.getId(),
                helperService.getCurrentUser().getEmail() + " updated project "+ project.getName()
        );

        return mapToResponse(project);
    }

    @Override
    public ProjectResponse getProjectById(UUID projectId) {
        return mapToResponse(authorizationService.requireProject(projectId));
    }

    @Transactional
    @Override
    public void deleteProject(UUID projectId) {
        Project project = authorizationService.requireProjectPermission(projectId, Permissions.WORKSPACE_WRITE);
        activityLogService.log(
                helperService.getCurrentUser(),
                project.getWorkspace(),
                ActivityAction.PROJECT_DELETED,
                ActivityEntityType.PROJECT,
                project.getId(),
                helperService.getCurrentUser().getEmail() + " deleted project "+ project.getName()
        );
        projectRepo.delete(project);

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
