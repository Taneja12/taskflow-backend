package com.deepanshu.backend.board.service;

import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.activitylog.service.ActivityLogService;
import com.deepanshu.backend.authorization.service.AuthorizationService;
import com.deepanshu.backend.board.dto.request.AddBoardRequest;
import com.deepanshu.backend.board.dto.response.BoardResponse;
import com.deepanshu.backend.board.dto.response.BoardStatisticsResponse;
import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.board.repo.BoardRepo;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.permission.Permissions;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.project.entity.Project;
import com.deepanshu.backend.task.entity.TaskPriority;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.projection.TaskStatusCount;
import com.deepanshu.backend.task.repo.TaskRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService{

    private final BoardRepo repo;
    private final HelperService helperService;
    private final AuthorizationService authorizationService;
    private final TaskRepo taskRepo;
    private final ActivityLogService activityLogService;

    public BoardServiceImpl(BoardRepo repo, HelperService helperService, AuthorizationService authorizationService, TaskRepo taskRepo, ActivityLogService activityLogService) {
        this.repo = repo;
        this.helperService = helperService;
        this.authorizationService = authorizationService;
        this.taskRepo = taskRepo;
        this.activityLogService = activityLogService;
    }

    private BoardResponse mapToResponse(Board board)
    {
        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getProject().getName()
        );
    }

    @Transactional
    @Override
    public BoardResponse addBoard(AddBoardRequest request, UUID projectId) {
        Project project = authorizationService.requireProjectPermission( projectId, Permissions.WORKSPACE_WRITE);
        Board board = new Board();
        board.setName(request.getName());
        board.setProject(project);
        board = repo.save(board);

        activityLogService.log(
                helperService.getCurrentUser(),
                project.getWorkspace(),
                ActivityAction.BOARD_CREATED,
                ActivityEntityType.BOARD,
                board.getId(),
                helperService.getCurrentUser().getEmail() + " created board "+ board.getName()
        );

        return mapToResponse(board);
    }

    @Override
    public BoardResponse getBoardById(UUID boardId) {
        return mapToResponse(authorizationService.requireBoard(boardId));
    }

    @Override
    public PageResponse<BoardResponse> getBoardsByProjectId(UUID projectId, Pageable pageable) {
        Project project = authorizationService.requireProject(projectId);
        Page<BoardResponse> page = repo.findByProjectId(project.getId(),pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Transactional
    @Override
    public BoardResponse updateBoard(AddBoardRequest request, UUID boardId) {
        Board board = authorizationService.requireBoardPermission(boardId, Permissions.WORKSPACE_WRITE);
        board.setName(request.getName());
        board = repo.save(board);
        activityLogService.log(
                helperService.getCurrentUser(),
                board.getProject().getWorkspace(),
                ActivityAction.BOARD_UPDATED,
                ActivityEntityType.BOARD,
                board.getId(),
                helperService.getCurrentUser().getEmail() + " updated board "+ board.getName()
        );
        return mapToResponse(board);
    }

    @Transactional
    @Override
    public void deleteBoard( UUID boardId) {
        Board board = authorizationService.requireBoardPermission(boardId, Permissions.WORKSPACE_WRITE);
        activityLogService.log(
                helperService.getCurrentUser(),
                board.getProject().getWorkspace(),
                ActivityAction.BOARD_DELETED,
                ActivityEntityType.BOARD,
                board.getId(),
                helperService.getCurrentUser().getEmail() + " deleted board "+ board.getName()
        );
        repo.delete(board);

    }

    @Override
    public BoardStatisticsResponse getBoardStatistics(UUID boardId) {
        Board board = authorizationService.requireBoard(boardId);
        Map<TaskStatus, Long> statusCounts = taskRepo.countTasksByStatusAndBoardId(board.getId())
                .stream().collect(Collectors.toMap(
                        TaskStatusCount::getStatus,
                        TaskStatusCount::getTotal
                ));


        return new BoardStatisticsResponse(
                taskRepo.countByBoardId(board.getId()),
                statusCounts.getOrDefault(TaskStatus.TODO, 0L),
                statusCounts.getOrDefault(TaskStatus.IN_PROGRESS, 0L),
                statusCounts.getOrDefault(TaskStatus.COMPLETED, 0L),
                taskRepo.countByPriorityAndBoardId(TaskPriority.HIGH, board.getId()),
                taskRepo.countDueToday(board.getId()),
                taskRepo.countOverdue(board.getId())
        );
    }

}
