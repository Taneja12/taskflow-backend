package com.deepanshu.backend.task.service;

import com.deepanshu.backend.common.exception.TaskNotFoundException;
import com.deepanshu.backend.task.dto.AddTaskRequest;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.task.dto.TaskResponse;
import com.deepanshu.backend.task.entity.Task;
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
    private final UserRepo userRepo;

    public TaskServiceImpl(TaskRepo taskRepo, UserRepo userRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepo.findByEmail(email)
                .orElseThrow();
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }

    private Task getUserTask(UUID taskId) {
        return taskRepo.findByIdAndUserId(
                taskId,
                getCurrentUser().getId()
        ).orElseThrow(() ->
                new TaskNotFoundException("Task not found"));
    }

    @Override
    public PageResponse<TaskResponse> getTasks(TaskStatus status, String search, Pageable pageable) {
        search = (search == null) ? "" : search.trim();
        Page<TaskResponse> page = taskRepo.getTasks(getCurrentUser().getId(),status, search, pageable).map(this::mapToResponse);
        return setPageResponse(page);
    }

    private PageResponse<TaskResponse> setPageResponse(Page<TaskResponse> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @Override
    public TaskResponse addTask(AddTaskRequest request) {
        Task task = new Task();
        task.setUser(getCurrentUser());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.TODO);
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public TaskResponse getTaskById(UUID taskId) {
        Task task = getUserTask(taskId);
        return mapToResponse(task);
    }

    @Override
    public void deleteTaskById(UUID taskId) {
        taskRepo.delete(getUserTask(taskId));
    }

    @Override
    public TaskResponse updateTask(UUID taskId, AddTaskRequest request) {
        Task task = getUserTask(taskId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        return mapToResponse(taskRepo.save(task));
    }

    @Override
    public TaskResponse updateTaskStatus(UUID taskId, TaskStatus status) {
        Task task = getUserTask(taskId);
        task.setStatus(status);
        return mapToResponse(taskRepo.save(task));
    }
}
