package com.deepanshu.backend.workspaceMember.controller;

import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.workspaceMember.dto.AddWorkspaceMemberRequest;
import com.deepanshu.backend.workspaceMember.dto.UpdateMemberRoleRequest;
import com.deepanshu.backend.workspaceMember.dto.WorkspaceMemberResponse;
import com.deepanshu.backend.workspaceMember.service.WorkspaceMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
@Tag(name = "Workspace")
@SecurityRequirement(name = "Bearer Authentication")
public class WorkspaceMemberController {

    private final WorkspaceMemberService service;

    public WorkspaceMemberController(WorkspaceMemberService service) {
        this.service = service;
    }

    @GetMapping("/{workspaceId}/members")
    @Operation(summary = "Get workspace members")
    public ResponseEntity<PageResponse<WorkspaceMemberResponse>> getWorkspaceMembers(@PathVariable UUID workspaceId, @ParameterObject Pageable pageable)
    {
        return ResponseEntity.ok(service.getWorkspaceMembers(workspaceId, pageable));
    }

    @PostMapping("/{workspaceId}/members")
    @Operation(summary = "Add workspace members")
    public ResponseEntity<WorkspaceMemberResponse> createWorkspaceMembers(@PathVariable UUID workspaceId,@Valid @RequestBody AddWorkspaceMemberRequest request)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addWorkspaceMembers(workspaceId, request ));
    }

    @DeleteMapping("/{workspaceId}/members/{memberId}")
    public ResponseEntity<?> removeWorkspaceMember(@PathVariable UUID workspaceId, @PathVariable UUID memberId)
    {
        service.removeWorkspaceMember(workspaceId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{workspaceId}/members/{memberId}/role")
    @Operation(summary = "Update role of workspace member")
    public ResponseEntity<WorkspaceMemberResponse> updateMemberRole(@PathVariable UUID workspaceId, @PathVariable UUID memberId, @Valid @RequestBody UpdateMemberRoleRequest request)
    {
        return ResponseEntity.ok(service.updateMemberRole(workspaceId, memberId, request));
    }
}
