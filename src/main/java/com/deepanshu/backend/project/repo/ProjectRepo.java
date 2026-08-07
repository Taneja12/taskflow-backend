package com.deepanshu.backend.project.repo;

import com.deepanshu.backend.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepo extends JpaRepository<Project, UUID> {

    Optional<Project> findByIdAndWorkspaceOwnerId(
            UUID projectId,
            UUID ownerId
    );


    @Query("""
        select p from Project p
        where p.workspace.id = :workspaceId
        AND p.workspace.owner.id = :id
    """)
    Page<Project> findByWorkspaceIdAndOwnerId(UUID workspaceId, UUID id, Pageable pageable);

    Page<Project> findByWorkspaceOwnerId(UUID id, Pageable pageable);

    @Query("""
        select COUNT(p) from Project p
        where p.workspace.id = :workspaceId
    """)
    long countByWorkspaceId(UUID workspaceId);

    Page<Project> findByWorkspaceId(UUID workspaceId, Pageable pageable);

    @Query("""
        SELECT p
        FROM Project p
        JOIN WorkspaceMember wm
            ON wm.workspace.id = p.workspace.id
        WHERE wm.user.id = :userId
    """)
    Page<Project> findAccessibleProjects(UUID userId, Pageable pageable);
}
