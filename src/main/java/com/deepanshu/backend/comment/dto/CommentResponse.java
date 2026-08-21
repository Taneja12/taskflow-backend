package com.deepanshu.backend.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private UUID id;
    private UUID taskId;
    private UUID userId;
    private String userName;
    private String userEmail;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
