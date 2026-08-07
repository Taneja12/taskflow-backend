package com.deepanshu.backend.authorization.service;

import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.board.repo.BoardRepo;
import com.deepanshu.backend.common.exception.*;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.project.entity.Project;
import com.deepanshu.backend.project.repo.ProjectRepo;
import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.task.repo.TaskRepo;
import com.deepanshu.backend.workspace.entity.Workspace;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceMember;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import com.deepanshu.backend.workspaceMember.repo.WorkspaceMemberRepo;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class AuthorizationService {

    private final WorkspaceMemberRepo workspaceMemberRepo;
    private final ProjectRepo projectRepo;
    private final BoardRepo boardRepo;
    private final TaskRepo taskRepo;
    private final HelperService helperService;

    public AuthorizationService( WorkspaceMemberRepo workspaceMemberRepo, ProjectRepo projectRepo,
                                BoardRepo boardRepo, TaskRepo taskRepo, HelperService helperService) {
        this.workspaceMemberRepo = workspaceMemberRepo;
        this.projectRepo = projectRepo;
        this.boardRepo = boardRepo;
        this.taskRepo = taskRepo;
        this.helperService = helperService;
    }

    // Access to the workspace
    public WorkspaceMember requireWorkspaceMember(UUID workspaceId)
    {
        return workspaceMemberRepo.findByWorkspaceIdAndUserId(workspaceId, helperService.getCurrentUser().getId())
                .orElseThrow(() -> new WorkSpaceNotFoundException("Workspace not found"));
    }

    // Permission for specific operation based on role
    public WorkspaceMember requireWorkspaceMemberPermission(
            UUID workspaceId,
            WorkspaceRole... roles
    ) {
        WorkspaceMember member = requireWorkspaceMember(workspaceId);
        Set<WorkspaceRole> allowedRoles = Set.of(roles);
        if (!allowedRoles.contains(member.getRole())) {
            throw new PermissionDeniedException("You don't have permission to perform this action.");
        }
        return member;
    }

    public Workspace requireWorkspacePermission(
            UUID workspaceId,
            WorkspaceRole... roles
    )
    {
        return requireWorkspaceMemberPermission(workspaceId, roles).getWorkspace();
    }

    public Workspace requireWorkspace(UUID workspaceId)
    {
        return requireWorkspaceMember(workspaceId).getWorkspace();
    }


    public Project requireProject(UUID projectId)
    {
        Project project = projectRepo.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        requireWorkspace(project.getWorkspace().getId());
        return project;
    }

    public Project requireProjectPermission(UUID projectId, WorkspaceRole ...roles)
    {
        Project project = requireProject(projectId);
        requireWorkspacePermission(project.getWorkspace().getId(), roles);
        return project;
    }

    public Board requireBoard(UUID boardId)
    {
        Board board = boardRepo.findById(boardId).orElseThrow(() -> new BoardNotFoundException("Board not found"));
        requireProject(board.getProject().getId());
        return board;
    }

    public Board requireBoardPermission(UUID boardId, WorkspaceRole ...roles)
    {
        Board board = requireBoard(boardId);
        requireProjectPermission(board.getProject().getId(), roles);
        return board;
    }

    public Task requireTask(UUID taskId)
    {
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        requireBoard(task.getBoard().getId());
        return task;
    }

    public Task requireTaskPermission(UUID taskId, WorkspaceRole ...roles)
    {
        Task task = requireTask(taskId);
        requireBoardPermission(task.getBoard().getId(), roles);
        return task;
    }

    public WorkspaceMember requireAssignableMember(UUID memberId, UUID workspaceId){
        return workspaceMemberRepo.findByIdAndWorkspaceId(memberId, workspaceId).orElseThrow(()-> new ResourceNotFoundException("Member not found"));
    }

}
