package com.deepanshu.backend.task.dto;

import com.deepanshu.backend.task.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(
            max = 100,
            message = "Title cannot exceed 100 characters"
    )
    private String title;

    @NotBlank(message = "Description is required")
    @Size(
            max = 1000,
            message = "Description cannot exceed 1000 characters"
    )
    private String description;

    private TaskPriority priority;

    private LocalDate dueDate;

}
