package com.deepanshu.backend.board.repo;

import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardRepo extends JpaRepository<Board, UUID> {

    Page<Board> findByProjectId(UUID projectId, Pageable pageable);

    Optional<Board> findByIdAndProjectWorkspaceOwnerId(UUID boardId, UUID id);

    @Query("""
        SELECT count(b) from Board b
        where b.project.workspace.id = :workspaceId
    """)
    long countByWorkspaceId(UUID workspaceId);


    @Query("""
        SELECT count(b) from Board b
        where b.project.id = :projectId
    """)
    long countByProjectId(UUID projectId);
}
