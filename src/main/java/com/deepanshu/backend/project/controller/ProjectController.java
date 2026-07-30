package com.deepanshu.backend.project.controller;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.project.dto.request.AddProjectRequest;
import com.deepanshu.backend.project.dto.response.ProjectResponse;
import com.deepanshu.backend.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
@Tag(name = "Projects")
@SecurityRequirement(name = "Bearer Authentication")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    @Operation(summary = "Get all projects")
    public ResponseEntity<PageResponse<ProjectResponse>> getProjects(@ParameterObject Pageable pageable)
    {
        return ResponseEntity.ok(projectService.getProjects(pageable));
    }

    @GetMapping("/{workspaceId}/projects")
    @Operation(summary = "Get all projects under a workspace")
    public ResponseEntity<PageResponse<ProjectResponse>> getWorkspaceProjects(@PathVariable UUID workspaceId,
                                                                              @ParameterObject Pageable pageable)
    {
        return ResponseEntity.ok(projectService.getWorkspaceProjects(workspaceId, pageable));
    }

    @PostMapping("/{workspaceId}/projects")
    @Operation(summary = "Create a project under a workspace")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody AddProjectRequest request,
                                                         @PathVariable UUID workspaceId)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(workspaceId, request));
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "Get Project By Id")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId)
    {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @PutMapping("/projects/{projectId}")
    @Operation(summary = "Update Project Details")
    public ResponseEntity<ProjectResponse> updateProject(@Valid @RequestBody AddProjectRequest request,
                                                         @PathVariable UUID projectId)
    {
        return ResponseEntity.ok(projectService.updateProject(request, projectId));
    }

    @DeleteMapping("/projects/{projectId}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<?> deleteProject(@PathVariable UUID projectId)
    {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

}
