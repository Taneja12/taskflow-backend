package com.deepanshu.backend.comment.service;

import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.activitylog.service.ActivityLogService;
import com.deepanshu.backend.authorization.service.AuthorizationService;
import com.deepanshu.backend.comment.dto.AddCommentRequest;
import com.deepanshu.backend.comment.dto.CommentResponse;
import com.deepanshu.backend.comment.entity.Comment;
import com.deepanshu.backend.comment.repo.CommentRepo;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.user.entity.User;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepo commentRepo;
    private final HelperService helperService;
    private final ActivityLogService activityLogService;
    private final AuthorizationService authorizationService;

    private CommentResponse mapToResponse(Comment comment){
        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                comment.getUser().getId(),
                comment.getUser().getFullName(),
                comment.getUser().getEmail(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    @Override
    public PageResponse<CommentResponse> getTaskComments(UUID taskId, Pageable pageable) {
        authorizationService.requireTask(taskId);
        Page<CommentResponse> page = commentRepo.findByTaskIdOrderByCreatedAtDesc(taskId,pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Transactional
    @Override
    public CommentResponse addTaskComment(UUID taskId, AddCommentRequest request) {
        Task task = authorizationService.requireTaskPermission(taskId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN, WorkspaceRole.MEMBER);
        User currentUser = helperService.getCurrentUser();
        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(currentUser);
        comment.setContent(request.getContent());
        comment = commentRepo.save(comment);
        activityLogService.log(
                currentUser,
                task.getBoard().getProject().getWorkspace(),
                ActivityAction.COMMENT_CREATED,
                ActivityEntityType.COMMENT,
                comment.getId(),
                currentUser.getEmail()
                        + " commented on task "
                        + task.getTitle()
        );
        return mapToResponse(comment);
    }

    @Transactional
    @Override
    public void deleteComment(UUID commentId) {
        Comment comment = authorizationService.requireCommentPermission(commentId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);
        User currentUser = helperService.getCurrentUser();
        activityLogService.log(
                currentUser,
                comment.getTask().getBoard().getProject().getWorkspace(),
                ActivityAction.COMMENT_DELETED,
                ActivityEntityType.COMMENT,
                comment.getId(),
                currentUser.getEmail()
                        + " deleted a comment from task "
                        + comment.getTask().getTitle()
        );
        commentRepo.delete(comment);
    }

    @Transactional
    @Override
    public CommentResponse updateTaskComment(UUID commentId, AddCommentRequest request) {
        Comment comment = authorizationService.requireCommentPermission(commentId);
        comment.setContent(request.getContent());
        User currentUser = helperService.getCurrentUser();
        activityLogService.log(
                currentUser,
                comment.getTask().getBoard().getProject().getWorkspace(),
                ActivityAction.COMMENT_EDITED,
                ActivityEntityType.COMMENT,
                comment.getId(),
                currentUser.getEmail()
                        + " edited a comment on task "
                        + comment.getTask().getTitle()
        );
        return mapToResponse(comment);
    }
}
