package com.deepanshu.backend.task.service;

import com.deepanshu.backend.authorization.service.AuthorizationService;
import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.common.exception.InvalidOperationException;
import com.deepanshu.backend.common.permission.Permissions;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.task.dto.AddTaskRequest;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.task.dto.TaskResponse;
import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.task.entity.TaskPriority;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.repo.TaskRepo;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Permission;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepo taskRepo;
    private final AuthorizationService authorizationService;
    private final HelperService helperService;

    public TaskServiceImpl(TaskRepo taskRepo, AuthorizationService authorizationService, HelperService helperService) {
        this.taskRepo = taskRepo;
        this.authorizationService = authorizationService;
        this.helperService = helperService;
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getBoard().getId(),
                task.getBoard().getName(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    @Override
    public PageResponse<TaskResponse> getTasks(TaskStatus status, TaskPriority priority, String search, UUID boardId, Pageable pageable) {
        Board board = authorizationService.requireBoard(boardId);
        search = (search == null) ? "" : search.trim();
        Page<TaskResponse> page = taskRepo.getTasks( status, priority, search, board.getId(), pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public TaskResponse addTask(AddTaskRequest request, UUID boardId) {
        Board board = authorizationService.requireBoardPermission(boardId, Permissions.TASK_EDIT);
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.TODO);
        task.setPriority(request.getPriority()==null ? TaskPriority.LOW : request.getPriority());
        task.setBoard(board);
        task.setDueDate(request.getDueDate());
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public TaskResponse getTaskById(UUID taskId) {
        Task task = authorizationService.requireTask(taskId);
        return mapToResponse(task);
    }

    @Override
    public void deleteTaskById(UUID taskId) {
        taskRepo.delete(authorizationService.requireTaskPermission(taskId, Permissions.WORKSPACE_WRITE));
    }

    @Override
    public TaskResponse updateTask(UUID taskId, AddTaskRequest request) {
        Task task = authorizationService.requireTaskPermission(taskId, Permissions.TASK_EDIT);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public TaskResponse updateTaskStatus(UUID taskId, TaskStatus status) {
        Task task = authorizationService.requireTaskPermission(taskId, Permissions.TASK_EDIT);
        task.setStatus(status);
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public TaskResponse updateBoard(UUID taskId, UUID boardId) {
        Task task = authorizationService.requireTaskPermission(taskId, Permissions.WORKSPACE_WRITE);
        Board board = authorizationService.requireBoard(boardId);
        if(!task.getBoard().getProject().getWorkspace().getId().equals( board.getProject().getWorkspace().getId()))
        {
            throw new InvalidOperationException("Task cannot be moved to another workspace.");
        }
        task.setBoard(board);
        return mapToResponse(taskRepo.save(task));
    }
}
