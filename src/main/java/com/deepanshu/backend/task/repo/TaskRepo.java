package com.deepanshu.backend.task.repo;

import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.task.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepo extends JpaRepository<Task, UUID> {

    Optional<Task> findByIdAndUserId(UUID taskId, UUID userId);

    @Query("""
        SELECT t
        FROM Task t
        WHERE t.user.id = :userId
        AND (:status IS NULL OR t.status = :status)
        AND (
            :search = ''
            OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<Task> getTasks(
            UUID userId,
            TaskStatus status,
            String search,
            Pageable pageable
    );

}
