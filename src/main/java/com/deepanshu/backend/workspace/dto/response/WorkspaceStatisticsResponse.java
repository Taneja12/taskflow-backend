package com.deepanshu.backend.workspace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceStatisticsResponse {

    private long totalMembers;
    private long totalProjects;
    private long totalBoards;
    private long totalTasks;

    private long todoTasks;
    private long inProgressTasks;
    private long completedTasks;

    private long highPriorityTasks;
    private long mediumPriorityTasks;
    private long lowPriorityTasks;
}
