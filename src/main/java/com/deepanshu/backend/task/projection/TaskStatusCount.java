package com.deepanshu.backend.task.projection;

import com.deepanshu.backend.task.entity.TaskStatus;

public interface TaskStatusCount {
    TaskStatus getStatus();
    Long getTotal();
}
