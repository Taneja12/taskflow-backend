package com.deepanshu.backend.comment.service;

import com.deepanshu.backend.comment.dto.AddCommentRequest;
import com.deepanshu.backend.comment.dto.CommentResponse;
import com.deepanshu.backend.common.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {
    PageResponse<CommentResponse> getTaskComments(UUID taskId, Pageable pageable);

    CommentResponse addTaskComment(UUID taskId, AddCommentRequest request);

    void deleteComment(UUID commentId);

    CommentResponse updateTaskComment(UUID commentId, @Valid AddCommentRequest request);
}
