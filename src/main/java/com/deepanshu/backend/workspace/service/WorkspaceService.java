package com.deepanshu.backend.workspace.service;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.workspace.dto.response.WorkspaceStatisticsResponse;
import com.deepanshu.backend.workspace.dto.request.AddWorkSpaceRequest;
import com.deepanshu.backend.workspace.dto.response.WorkspaceResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkspaceService {

    PageResponse<WorkspaceResponse> getWorkspaces(Pageable pageable);
    WorkspaceResponse createWorkspace(AddWorkSpaceRequest request) ;
    void deleteWorkspace(UUID workspaceId);

    WorkspaceResponse getWorkSpaceById(UUID workspaceId);

    WorkspaceResponse updateWorkSpace(UUID workspaceId, @Valid AddWorkSpaceRequest request);

    WorkspaceStatisticsResponse getWorkspaceStatistics(UUID workspaceId);
}


