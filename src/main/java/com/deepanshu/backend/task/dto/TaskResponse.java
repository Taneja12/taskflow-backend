package com.deepanshu.backend.task.dto;

import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.task.entity.TaskPriority;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private UUID assignedMemberId;
    private String assignedMemberName;
    private String assignedMemberProfileImageUrl;
    private UUID boardId;
    private String boardName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
