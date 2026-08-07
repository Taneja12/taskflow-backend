package com.deepanshu.backend.workspaceMember.repo;

import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.workspace.entity.Workspace;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceMember;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface WorkspaceMemberRepo extends JpaRepository<WorkspaceMember,UUID> {

    boolean existsByWorkspaceIdAndUserIdAndRoleIn(UUID workspaceId, UUID userId, Set<WorkspaceRole> role);

    boolean existsByWorkspaceIdAndUserEmail(UUID workspace,String userEmail);

    Page<WorkspaceMember> findAllByWorkspace_Id(UUID workspaceId, Pageable pageable);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    List<WorkspaceMember> workspace(Workspace workspace);

    Optional<WorkspaceMember> findByIdAndWorkspaceId(UUID memberId, UUID workspaceId);

    long countWorkspaceMemberByWorkspaceId(UUID workspaceId);

    Page<WorkspaceMember> findByUserId(UUID id, Pageable pageable);
}
