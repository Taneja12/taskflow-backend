package com.deepanshu.backend.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStatisticsResponse {
    private long totalBoards;
    private long todoTasks;
    private long inProgressTasks;
    private long completedTasks;
}
