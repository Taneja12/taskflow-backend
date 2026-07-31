package com.deepanshu.backend.board.service;

import com.deepanshu.backend.board.dto.request.AddBoardRequest;
import com.deepanshu.backend.board.dto.response.BoardResponse;
import com.deepanshu.backend.board.dto.response.BoardStatisticsResponse;
import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.board.repo.BoardRepo;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.common.exception.BoardNotFoundException;
import com.deepanshu.backend.common.exception.ProjectNotFoundException;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.project.entity.Project;
import com.deepanshu.backend.project.repo.ProjectRepo;
import com.deepanshu.backend.task.entity.TaskPriority;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.projection.TaskStatusCount;
import com.deepanshu.backend.task.repo.TaskRepo;
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
    private final ProjectRepo projectRepo;
    private final TaskRepo taskRepo;

    public BoardServiceImpl(BoardRepo repo, HelperService helperService, ProjectRepo projectRepo, TaskRepo taskRepo) {
        this.repo = repo;
        this.helperService = helperService;
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
    }

    private BoardResponse mapToResponse(Board board)
    {
        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getProject().getName()
        );
    }

    public Project findOwnedProject(UUID projectId) {
        return projectRepo.findByIdAndWorkspaceOwnerId(projectId, helperService.getCurrentUser().getId()).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
    }

    private Board findBoardById(UUID boardId) {
        return repo.findByIdAndProjectWorkspaceOwnerId(boardId, helperService.getCurrentUser().getId()).orElseThrow(() -> new BoardNotFoundException("Board not found"));
    }

    @Override
    public BoardResponse addBoard(AddBoardRequest request, UUID projectId) {
        Project project = findOwnedProject( projectId);
        Board board = new Board();
        board.setName(request.getName());
        board.setProject(project);
        return mapToResponse(repo.save(board));
    }

    @Override
    public BoardResponse getBoardById(UUID boardId) {
        return mapToResponse(findBoardById(boardId));
    }

    @Override
    public PageResponse<BoardResponse> getBoardsByProjectId(UUID projectId, Pageable pageable) {
        Project project = findOwnedProject(projectId);
        Page<BoardResponse> page = repo.findByProjectId(project.getId(),pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Override
    public BoardResponse updateBoard(AddBoardRequest request, UUID boardId) {
        Board board = findBoardById(boardId);
        board.setName(request.getName());
        return mapToResponse(repo.save(board));
    }

    @Override
    public void deleteBoard( UUID boardId) {
        Board board = findBoardById(boardId);
        repo.delete(board);
    }

    @Override
    public BoardStatisticsResponse getBoardStatistics(UUID boardId) {
        findBoardById(boardId);
        Map<TaskStatus, Long> statusCounts = taskRepo.countTasksByStatusAndBoardId(boardId)
                .stream().collect(Collectors.toMap(
                        TaskStatusCount::getStatus,
                        TaskStatusCount::getTotal
                ));


        return new BoardStatisticsResponse(
                taskRepo.countByBoardId(boardId),
                statusCounts.getOrDefault(TaskStatus.TODO, 0L),
                statusCounts.getOrDefault(TaskStatus.IN_PROGRESS, 0L),
                statusCounts.getOrDefault(TaskStatus.COMPLETED, 0L),
                taskRepo.countByPriorityAndBoardId(TaskPriority.HIGH, boardId),
                taskRepo.countDueToday(boardId),
                taskRepo.countOverdue(boardId)
        );
    }

}
