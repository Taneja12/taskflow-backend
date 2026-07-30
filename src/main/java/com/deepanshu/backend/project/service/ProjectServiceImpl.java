package com.deepanshu.backend.project.service;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.exception.ProjectNotFoundException;
import com.deepanshu.backend.common.exception.WorkSpaceNotFoundException;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.project.dto.request.AddProjectRequest;
import com.deepanshu.backend.project.dto.response.ProjectResponse;
import com.deepanshu.backend.project.entity.Project;
import com.deepanshu.backend.project.repo.ProjectRepo;
import com.deepanshu.backend.workspace.entity.Workspace;
import com.deepanshu.backend.workspace.repo.WorkspaceRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepo projectRepo;
    private final WorkspaceRepo workspaceRepo;
    private final HelperService helperService;

    public ProjectServiceImpl(ProjectRepo projectRepo, WorkspaceRepo workspaceRepo, HelperService helperService) {
        this.projectRepo = projectRepo;
        this.workspaceRepo = workspaceRepo;
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

    public Project findProjectById(UUID projectId)
    {
        return projectRepo.findByIdAndWorkspaceOwnerId(projectId, helperService.getCurrentUser().getId()).orElseThrow(()-> new ProjectNotFoundException("Project not found"));
//        Project project = projectRepo.findById(projectId).
//                orElseThrow(() -> new ProjectNotFoundException("Project not found"));
//        workspaceRepo.findByIdAndOwnerId(project.getWorkspace().getId(),
//                        helperService.getCurrentUser().getId())
//                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
//        return project;
    }

    @Override
    public ProjectResponse createProject(UUID workspaceId, AddProjectRequest request)
    {
        Workspace workspace = workspaceRepo.findByIdAndOwnerId(workspaceId,helperService.getCurrentUser().getId()).orElseThrow(() -> new WorkSpaceNotFoundException("Workspace not found"));
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setWorkspace(workspace);
        return mapToResponse(projectRepo.save(project));
    }

    @Override
    public PageResponse<ProjectResponse> getProjects(Pageable pageable) {
        Page<ProjectResponse> page = projectRepo.findByWorkspaceOwnerId(helperService.getCurrentUser().getId(), pageable).map(this:: mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public PageResponse<ProjectResponse> getWorkspaceProjects(UUID workspaceId, Pageable pageable) {
        Page<ProjectResponse> page = projectRepo.findByWorkspaceIdAndOwnerId(workspaceId, helperService.getCurrentUser().getId(), pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public ProjectResponse updateProject(AddProjectRequest request, UUID projectId) {
        Project project = findProjectById(projectId);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        projectRepo.save(project);
        return mapToResponse(project);
    }

    @Override
    public ProjectResponse getProjectById(UUID projectId) {
        return mapToResponse(findProjectById(projectId));
    }

    @Override
    public void deleteProject(UUID projectId) {
        projectRepo.delete(findProjectById(projectId));
    }


}
