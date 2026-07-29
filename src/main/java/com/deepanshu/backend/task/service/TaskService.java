package com.deepanshu.backend.task.service;

import com.deepanshu.backend.task.dto.AddTaskRequest;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.task.dto.TaskResponse;
import com.deepanshu.backend.task.entity.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {
    PageResponse<TaskResponse> getTasks(TaskStatus status, String search, Pageable pageable);

    TaskResponse addTask(AddTaskRequest request);

    TaskResponse getTaskById(UUID taskId);

    void  deleteTaskById(UUID taskId);

    TaskResponse updateTask(UUID taskId, @Valid AddTaskRequest request);

    TaskResponse updateTaskStatus(UUID taskId, TaskStatus status);
}
