package com.deepanshu.backend.task.controller;

import com.deepanshu.backend.task.dto.AddTaskRequest;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.task.dto.TaskResponse;
import com.deepanshu.backend.task.dto.UpdateTaskBoard;
import com.deepanshu.backend.task.dto.UpdateTaskStatus;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.service.TaskService;
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
@RequestMapping("/api/v1/boards")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/{boardId}/tasks")
    @Operation(summary = "Get all tasks")
    public ResponseEntity<PageResponse<TaskResponse>> getTasks(@RequestParam(required = false) TaskStatus status, @RequestParam(required = false) String search, @PathVariable UUID boardId, @ParameterObject Pageable pageable)
    {
        return new ResponseEntity<>(service.getTasks(status, search, boardId, pageable), HttpStatus.OK);
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Get task by id")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId)
    {
        return ResponseEntity.ok(service.getTaskById(taskId));
    }

    @PostMapping("/{boardId}/tasks")
    @Operation(summary = "Create new task")
    public ResponseEntity<TaskResponse> addTask(@Valid @RequestBody AddTaskRequest request, @PathVariable UUID boardId)
    {
        return new ResponseEntity<>(service.addTask(request, boardId), HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{taskId}")
    @Operation(summary = "Update task")
    public ResponseEntity<TaskResponse> updateTask(@Valid @RequestBody AddTaskRequest request, @PathVariable UUID taskId)
    {
        return new ResponseEntity<>(service.updateTask(taskId, request), HttpStatus.OK);
    }

    @PatchMapping("/tasks/{taskId}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable UUID taskId,@Valid @RequestBody UpdateTaskStatus request)
    {
        return ResponseEntity.ok(service.updateTaskStatus(taskId, request.getStatus()));
    }

    @PatchMapping("/tasks/{taskId}/board")
    @Operation(summary = "Update task board")
    public ResponseEntity<TaskResponse> updateTaskBoard(@PathVariable UUID taskId,@Valid @RequestBody UpdateTaskBoard request)
    {
        return ResponseEntity.ok(service.updateBoard(taskId, request.getBoardId()));
    }

   @DeleteMapping("/tasks/{taskId}")
   @Operation(summary = "Delete task")
   public ResponseEntity<TaskResponse> deleteTaskById(@PathVariable UUID taskId)
   {
       service.deleteTaskById(taskId);
       return ResponseEntity.noContent().build();
   }

}
