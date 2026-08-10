package com.deepanshu.backend.task.service;

import com.deepanshu.backend.activitylog.entity.ActivityAction;
import com.deepanshu.backend.activitylog.entity.ActivityEntityType;
import com.deepanshu.backend.activitylog.service.ActivityLogService;
import com.deepanshu.backend.authorization.service.AuthorizationService;
import com.deepanshu.backend.board.entity.Board;
import com.deepanshu.backend.common.exception.InvalidOperationException;
import com.deepanshu.backend.common.permission.Permissions;
import com.deepanshu.backend.common.service.HelperService;
import com.deepanshu.backend.task.dto.AddTaskRequest;
import com.deepanshu.backend.common.dto.PageResponse;
import com.deepanshu.backend.task.dto.TaskResponse;
import com.deepanshu.backend.task.entity.Task;
import com.deepanshu.backend.task.entity.TaskPriority;
import com.deepanshu.backend.task.entity.TaskStatus;
import com.deepanshu.backend.task.repo.TaskRepo;
import com.deepanshu.backend.workspaceMember.entity.WorkspaceMember;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepo taskRepo;
    private final AuthorizationService authorizationService;
    private final ActivityLogService activityLogService;
    private final HelperService helperService;

    public TaskServiceImpl(TaskRepo taskRepo, AuthorizationService authorizationService, ActivityLogService activityLogService, HelperService helperService) {
        this.taskRepo = taskRepo;
        this.authorizationService = authorizationService;
        this.activityLogService = activityLogService;
        this.helperService = helperService;
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getAssignedMember() != null? task.getAssignedMember().getId(): null,
                task.getAssignedMember() != null? task.getAssignedMember().getUser().getFullName(): null,
                task.getAssignedMember() != null? task.getAssignedMember().getUser().getProfileImageUrl() : null,
                task.getBoard().getId(),
                task.getBoard().getName(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }


    @Override
    public PageResponse<TaskResponse> getTasks(TaskStatus status, TaskPriority priority, String search, UUID boardId, Pageable pageable) {
        Board board = authorizationService.requireBoard(boardId);
        search = (search == null) ? "" : search.trim();
        Page<TaskResponse> page = taskRepo.getTasks( status, priority, search, board.getId(), pageable).map(this::mapToResponse);
        return helperService.setPageResponse(page);
    }

    @Transactional
    @Override
    public TaskResponse addTask(AddTaskRequest request, UUID boardId) {
        Board board = authorizationService.requireBoardPermission(boardId, Permissions.TASK_EDIT);
        WorkspaceMember assignedMember  = null;
        if(request.getAssignedMemberId()!=null)
        {
            assignedMember  = authorizationService.requireAssignableMember(request.getAssignedMemberId(),board.getProject().getWorkspace().getId());
        }
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.TODO);
        task.setPriority(request.getPriority());
        task.setAssignedMember(assignedMember );
        task.setBoard(board);
        task.setDueDate(request.getDueDate());
        task = taskRepo.save(task);

        activityLogService.log(
                helperService.getCurrentUser(),
                board.getProject().getWorkspace(),
                ActivityAction.TASK_CREATED,
                ActivityEntityType.TASK,
                task.getId(),
                helperService.getCurrentUser().getEmail() + " created task "+ task.getTitle()
        );

        if(request.getAssignedMemberId()!=null)
        {
            activityLogService.log(
                    helperService.getCurrentUser(),
                    board.getProject().getWorkspace(),
                    ActivityAction.TASK_ASSIGNED,
                    ActivityEntityType.TASK,
                    task.getId(),
                    helperService.getCurrentUser().getEmail()
                            + " assigned task "
                            + task.getTitle()
                            + " to "
                            + task.getAssignedMember().getUser().getEmail()
            );
        }

        return mapToResponse(task);
    }

    @Override
    public TaskResponse getTaskById(UUID taskId) {
        Task task = authorizationService.requireTask(taskId);
        return mapToResponse(task);
    }

    @Transactional
    @Override
    public void deleteTaskById(UUID taskId) {
        Task task = authorizationService.requireTaskPermission(taskId, Permissions.WORKSPACE_WRITE);
        activityLogService.log(
                helperService.getCurrentUser(),
                task.getBoard().getProject().getWorkspace(),
                ActivityAction.TASK_DELETED,
                ActivityEntityType.TASK,
                task.getId(),
                helperService.getCurrentUser().getEmail() + " deleted task "+ task.getTitle()
        );
        taskRepo.delete(task);

    }

    @Transactional
    @Override
    public TaskResponse updateTask(UUID taskId, AddTaskRequest request) {
        Task task = authorizationService.requireTaskPermission(taskId, Permissions.TASK_EDIT);
        WorkspaceMember oldMember = task.getAssignedMember();
        WorkspaceMember assignedMember=null;
        if(request.getAssignedMemberId()!=null)
        {
            assignedMember  = authorizationService.requireAssignableMember(request.getAssignedMemberId(), task.getBoard().getProject().getWorkspace().getId());
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setAssignedMember(assignedMember);
        task = taskRepo.save(task);

        activityLogService.log(
                helperService.getCurrentUser(),
                task.getBoard().getProject().getWorkspace(),
                ActivityAction.TASK_UPDATED,
                ActivityEntityType.TASK,
                task.getId(),
                helperService.getCurrentUser().getEmail() + " updated task "+ task.getTitle()
        );

        if(oldMember==null && task.getAssignedMember()!=null)
        {
            activityLogService.log(
                    helperService.getCurrentUser(),
                    task.getBoard().getProject().getWorkspace(),
                    ActivityAction.TASK_ASSIGNED,
                    ActivityEntityType.TASK,
                    task.getId(),
                    helperService.getCurrentUser().getEmail()
                            + " assigned task "
                            + task.getTitle()
                            + " to "
                            + task.getAssignedMember().getUser().getEmail()
            );
        }

        if(oldMember!=null && task.getAssignedMember()==null)
        {
            activityLogService.log(
                    helperService.getCurrentUser(),
                    task.getBoard().getProject().getWorkspace(),
                    ActivityAction.TASK_UNASSIGNED,
                    ActivityEntityType.TASK,
                    task.getId(),
                    helperService.getCurrentUser().getEmail()
                            + " unassigned task "
                            + task.getTitle()
                            + " from "
                            + oldMember.getUser().getEmail()
            );
        }

        if (oldMember != null
                && task.getAssignedMember() != null
                && !oldMember.getId().equals(task.getAssignedMember().getId())) {

            activityLogService.log(
                    helperService.getCurrentUser(),
                    task.getBoard().getProject().getWorkspace(),
                    ActivityAction.TASK_ASSIGNED,
                    ActivityEntityType.TASK,
                    task.getId(),
                    helperService.getCurrentUser().getEmail()
                            + " reassigned task "
                            + task.getTitle()
                            + " from "
                            + oldMember.getUser().getEmail()
                            + " to "
                            + task.getAssignedMember().getUser().getEmail()
            );
        }

        return mapToResponse(task);
    }

    @Transactional
    @Override
    public TaskResponse updateTaskStatus(UUID taskId, TaskStatus status) {
        Task task = authorizationService.requireTaskPermission(taskId, Permissions.TASK_EDIT);
        TaskStatus oldStatus = task.getStatus();
        task.setStatus(status);
        task = taskRepo.save(task);

        activityLogService.log(
                helperService.getCurrentUser(),
                task.getBoard().getProject().getWorkspace(),
                ActivityAction.TASK_STATUS_CHANGED,
                ActivityEntityType.TASK,
                task.getId(),
                helperService.getCurrentUser().getEmail()
                        + " changed task "
                        + task.getTitle()
                        + " status from "
                        + oldStatus
                        + " to "
                        + task.getStatus()
        );

        return mapToResponse(task);
    }

    @Transactional
    @Override
    public TaskResponse updateBoard(UUID taskId, UUID boardId) {
        Task task = authorizationService.requireTaskPermission(taskId, Permissions.WORKSPACE_WRITE);
        String oldBoardName = task.getBoard().getName();
        Board board = authorizationService.requireBoardPermission(boardId, Permissions.WORKSPACE_WRITE);
        if(!task.getBoard().getProject().getWorkspace().getId().equals( board.getProject().getWorkspace().getId()))
        {
            throw new InvalidOperationException("Task cannot be moved to another workspace.");
        }
        task.setBoard(board);
        task = taskRepo.save(task);

        activityLogService.log(
                helperService.getCurrentUser(),
                task.getBoard().getProject().getWorkspace(),
                ActivityAction.TASK_MOVED,
                ActivityEntityType.TASK,
                task.getId(),
                helperService.getCurrentUser().getEmail()
                        + " moved task "
                        + task.getTitle()
                        + " from board "
                        + oldBoardName
                        + " to "
                        + board.getName()
        );

        return mapToResponse(task);
    }
}
