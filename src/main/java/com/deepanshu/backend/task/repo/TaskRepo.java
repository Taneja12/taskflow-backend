package com.deepanshu.backend.task.repo;

import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.task.entity.TaskPriority;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.projection.TaskStatusCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepo extends JpaRepository<Task, UUID> {

    @Query("""
        SELECT t
        FROM Task t
        WHERE t.board.id = :boardId
        AND (:status IS NULL OR t.status = :status)
        AND (:priority IS NULL OR t.priority = :priority)
        AND (
            :search = ''
            OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<Task> getTasks(
            TaskStatus status,
            TaskPriority priority,
            String search,
            UUID boardId,
            Pageable pageable
    );


    Pageable board(Board board);

    @Query("""
        select count(t) from Task t
        WHERE t.board.project.workspace.id = :workspaceId
    """)
    long countByWorkspaceId(UUID workspaceId);


    @Query("""
    SELECT
        t.status AS status,
        COUNT(t) AS total
    FROM Task t
    WHERE t.board.project.workspace.id = :workspaceId
    GROUP BY t.status
    """)
    List<TaskStatusCount> countTasksByStatus(UUID workspaceId);

    @Query("""
        select count(t) from Task t
        where t.board.project.workspace.id = :workspaceId
        and t.priority = :taskPriority
    """)
    long countByPriority(UUID workspaceId, TaskPriority taskPriority);

    @Query("""
        select t.status AS status, count(t) AS total from Task t
            where t.board.project.id = :projectId
                GROUP BY t.status
    """)
    List<TaskStatusCount> countTasksByStatusAndProjectId(UUID projectId);

    @Query("""
        select t.status AS status, count(t) AS total from Task t
            where t.board.id = :boardId
                GROUP BY t.status
    """)
    List<TaskStatusCount> countTasksByStatusAndBoardId(UUID boardId);

    @Query("""
        SELECT count(t) from Task t
            where t.board.id = :boardId
    """)
    long countByBoardId(UUID boardId);

    @Query("""
        SELECT count(t) from Task t
            where t.board.id = :boardId
                AND t.priority = :taskPriority
    """)
    long countByPriorityAndBoardId(TaskPriority taskPriority, UUID boardId);


    @Query("""
        select count(t) from Task t
            where t.board.id = :boardId
                AND t.dueDate = current_date
    """)
    long countDueToday(UUID boardId);

    @Query("""
        select count(t) from Task t
            where t.board.id = :boardId
                AND t.dueDate < current_date
    """)
    long countOverdue(UUID boardId);
}
