package com.deepanshu.backend.comment.repo;
import com.deepanshu.backend.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepo extends JpaRepository<Comment, UUID> {
    Page<Comment> findByTaskIdOrderByCreatedAtDesc(UUID taskId, Pageable pageable);
}
