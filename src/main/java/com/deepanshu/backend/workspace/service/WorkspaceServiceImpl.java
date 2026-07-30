package com.deepanshu.backend.workspace.service;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.exception.WorkSpaceNotFoundException;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.workspace.dto.request.AddWorkSpaceRequest;
import com.deepanshu.backend.workspace.dto.response.WorkspaceResponse;
import com.deepanshu.backend.workspace.entity.Workspace;
import com.deepanshu.backend.workspace.repo.WorkspaceRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepo repo;
    private final HelperService helperService;

    public WorkspaceServiceImpl(WorkspaceRepo repo, HelperService helperService) {
        this.repo = repo;
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

    private Workspace getUserWorkspace(UUID workspaceId) {
        return repo.findByIdAndOwnerId(workspaceId, helperService.getCurrentUser().getId())
                .orElseThrow(() -> new WorkSpaceNotFoundException("Workspace not found"));
    }

    @Override
    public PageResponse<WorkspaceResponse> getWorkspaces(Pageable pageable) {
        Page<WorkspaceResponse> page = repo.findByOwnerId(helperService.getCurrentUser().getId(),pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public WorkspaceResponse getWorkSpaceById(UUID workspaceId) {
        return mapToResponse(getUserWorkspace(workspaceId));
    }

    @Override
    public WorkspaceResponse updateWorkSpace(UUID workspaceId, AddWorkSpaceRequest request) {
        Workspace workspace = getUserWorkspace(workspaceId);
        workspace.setDescription(request.getDescription());
        workspace.setName(request.getName());
        return mapToResponse(repo.save(workspace));
    }

    @Override
    public WorkspaceResponse createWorkspace(AddWorkSpaceRequest request) {
        Workspace workspace = new Workspace();
        workspace.setOwner(helperService.getCurrentUser());
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        return mapToResponse(repo.save(workspace));
    }

    @Override
    public void deleteWorkspace(UUID workspaceId) {
        repo.delete(getUserWorkspace(workspaceId));
    }


}
