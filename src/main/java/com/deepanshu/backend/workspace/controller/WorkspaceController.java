package com.deepanshu.backend.workspace.controller;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.workspace.dto.request.AddWorkSpaceRequest;
import com.deepanshu.backend.workspace.dto.response.WorkspaceResponse;
import com.deepanshu.backend.workspace.service.WorkspaceService;
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
@Tag(name = "Workspace")
@SecurityRequirement(name = "Bearer Authentication")
public class WorkspaceController {

    private final WorkspaceService service;

    public WorkspaceController(WorkspaceService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all workspaces")
    public ResponseEntity<PageResponse<WorkspaceResponse>> getWorkspaces(@ParameterObject Pageable pageable)
    {
        return ResponseEntity.ok(service.getWorkspaces(pageable));
    }

    @GetMapping("/{workspaceId}")
    @Operation(summary = "Get workspace by id")
    public ResponseEntity<WorkspaceResponse> getWorkSpaceById(@PathVariable UUID workspaceId)
    {
        return ResponseEntity.ok(service.getWorkSpaceById(workspaceId));
    }

    @PostMapping
    @Operation(summary = "Create new workspace")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody AddWorkSpaceRequest request)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createWorkspace(request));
    }

    @PutMapping("/{workSpaceId}")
    @Operation(summary = "Update workspace")
    public ResponseEntity<WorkspaceResponse> updateWorkSpace(@Valid @RequestBody AddWorkSpaceRequest request, @PathVariable UUID workSpaceId)
    {
        return ResponseEntity.ok().body(service.updateWorkSpace(workSpaceId,request));
    }

    @DeleteMapping("/{workSpaceId}")
    @Operation(summary = "Delete workspace")
    public ResponseEntity<?> deleteWorkspace(@PathVariable UUID workSpaceId)
    {
        service.deleteWorkspace(workSpaceId);
        return ResponseEntity.noContent().build();
    }
}
