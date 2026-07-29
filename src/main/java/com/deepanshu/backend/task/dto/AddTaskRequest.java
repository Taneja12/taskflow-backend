package com.deepanshu.backend.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    @Size(max = 1000)
    private String description;
}
