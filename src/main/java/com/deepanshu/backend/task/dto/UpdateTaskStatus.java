package com.deepanshu.backend.task.dto;

import com.deepanshu.backend.task.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskStatus {
    @NotNull
    private TaskStatus status;
}
