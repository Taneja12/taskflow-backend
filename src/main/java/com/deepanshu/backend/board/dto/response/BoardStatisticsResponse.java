package com.deepanshu.backend.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardStatisticsResponse {

    private long totalTasks;
    private long toDoTasks;
    private long inProgressTasks;
    private long completedTasks;
    private long highPriorityTasks;
    private long dueToday;
    private long overDue;
}
