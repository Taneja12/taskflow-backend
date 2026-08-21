package com.deepanshu.backend.comment.controller;

import com.deepanshu.backend.comment.dto.AddCommentRequest;
import com.deepanshu.backend.comment.dto.CommentResponse;
import com.deepanshu.backend.comment.service.CommentService;
import com.deepanshu.backend.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
@Tag(name = "Task Comments")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/tasks/{taskId}/comments")
    @Operation(summary = "Get task comments")
    public ResponseEntity<PageResponse<CommentResponse>> getTaskComments(@PathVariable UUID taskId, @ParameterObject Pageable pageable)
    {
        return ResponseEntity.ok(commentService.getTaskComments(taskId, pageable));
    }

    @PostMapping("/tasks/{taskId}/comments")
    @Operation(summary = "Add comment")
    public ResponseEntity<CommentResponse> createTaskComment(@PathVariable UUID taskId, @Valid @RequestBody AddCommentRequest request)
    {
        return ResponseEntity.ok(commentService.addTaskComment(taskId, request));
    }

    @PutMapping("/tasks/comments/{commentId}")
    @Operation(summary = "Update comment")
    public ResponseEntity<CommentResponse> updateTaskComment(@PathVariable UUID commentId, @Valid @RequestBody AddCommentRequest request)
    {
        return ResponseEntity.ok(commentService.updateTaskComment(commentId, request));
    }

    @DeleteMapping("/tasks/comments/{commentId}")
    @Operation(summary = "Delete comment")
    public ResponseEntity<Void> deleteTaskComment(@PathVariable UUID commentId)
    {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
