package com.deepanshu.backend.task.controller;

import com.deepanshu.backend.task.dto.AddTaskRequest;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.task.dto.TaskResponse;
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
@RequestMapping("/api/v1/tasks")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public ResponseEntity<PageResponse<TaskResponse>> getTasks(@RequestParam(required = false) TaskStatus status, @RequestParam(required = false) String search,@ParameterObject Pageable pageable)
    {
        return new ResponseEntity<>(service.getTasks(status, search, pageable), HttpStatus.OK);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by id")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId)
    {
        return ResponseEntity.ok(service.getTaskById(taskId));
    }

    @PostMapping
    @Operation(summary = "Create new task")
    public ResponseEntity<TaskResponse> addTask(@Valid @RequestBody AddTaskRequest request)
    {
        return new ResponseEntity<>(service.addTask(request), HttpStatus.CREATED);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update task")
    public ResponseEntity<TaskResponse> updateTask(@Valid @RequestBody AddTaskRequest request, @PathVariable UUID taskId)
    {
        return new ResponseEntity<>(service.updateTask(taskId, request), HttpStatus.OK);
    }

    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable UUID taskId,@Valid @RequestBody UpdateTaskStatus request)
    {
        return ResponseEntity.ok(service.updateTaskStatus(taskId, request.getStatus()));
    }

   @DeleteMapping("/{taskId}")
   @Operation(summary = "Delete task")
   public ResponseEntity<TaskResponse> deleteTaskById(@PathVariable UUID taskId)
   {
       service.deleteTaskById(taskId);
       return ResponseEntity.noContent().build();
   }

}
