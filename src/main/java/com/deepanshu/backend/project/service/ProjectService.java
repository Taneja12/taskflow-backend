package com.deepanshu.backend.project.service;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.project.dto.request.AddProjectRequest;
import com.deepanshu.backend.project.dto.response.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectService {
    ProjectResponse createProject(UUID workspaceId, AddProjectRequest request);

    PageResponse<ProjectResponse> getProjects(Pageable pageable);

    PageResponse<ProjectResponse> getWorkspaceProjects(UUID workspaceId, Pageable pageable);

    ProjectResponse updateProject(@Valid AddProjectRequest request, UUID projectId);

    ProjectResponse getProjectById(UUID projectId);

    void deleteProject(UUID projectId);
}
