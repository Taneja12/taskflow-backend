package com.deepanshu.backend.task.service;

import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.board.repo.BoardRepo;
import com.deepanshu.backend.common.exception.BoardNotFoundException;
import com.deepanshu.backend.common.exception.TaskNotFoundException;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.task.dto.AddTaskRequest;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.task.dto.TaskResponse;
import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.task.entity.TaskPriority;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.repo.TaskRepo;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.user.repo.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepo taskRepo;
    private final BoardRepo boardRepo;
    private final HelperService helperService;

    public TaskServiceImpl(TaskRepo taskRepo, BoardRepo boardRepo, HelperService helperService) {
        this.taskRepo = taskRepo;
        this.boardRepo = boardRepo;
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

    private Task findOwnedTask(UUID taskId) {
        return taskRepo.findByIdAndBoardProjectWorkspaceOwnerId(
                taskId,
                helperService.getCurrentUser().getId()
        ).orElseThrow(() ->
                new TaskNotFoundException("Task not found"));
    }

    private Board findOwnedBoard(UUID boardId) {
        return boardRepo.findByIdAndProjectWorkspaceOwnerId(boardId, helperService.getCurrentUser().getId())
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));
    }

    @Override
    public PageResponse<TaskResponse> getTasks(TaskStatus status, TaskPriority priority, String search, UUID boardId, Pageable pageable) {
        search = (search == null) ? "" : search.trim();
        Page<TaskResponse> page = taskRepo.getTasks(helperService.getCurrentUser().getId(), status, priority, search, boardId, pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public TaskResponse addTask(AddTaskRequest request, UUID boardId) {
        Board board = findOwnedBoard(boardId);
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
        Task task = findOwnedTask(taskId);
        return mapToResponse(task);
    }

    @Override
    public void deleteTaskById(UUID taskId) {
        taskRepo.delete(findOwnedTask(taskId));
    }

    @Override
    public TaskResponse updateTask(UUID taskId, AddTaskRequest request) {
        Task task = findOwnedTask(taskId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public TaskResponse updateTaskStatus(UUID taskId, TaskStatus status) {
        Task task = findOwnedTask(taskId);
        task.setStatus(status);
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public TaskResponse updateBoard(UUID taskId, UUID boardId) {
        Task task = findOwnedTask(taskId);
        Board board = findOwnedBoard(boardId);
        task.setBoard(board);
        return mapToResponse(taskRepo.save(task));
    }
}
